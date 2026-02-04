package com.biblioteca.controllers;

import com.biblioteca.models.Libro;
import com.biblioteca.services.FirebaseService;
import com.google.firebase.database.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LibroController {

	@FXML
	private Label numLibrosPosesion;
	@FXML
	private Label numLibrosPendiente;
    @FXML
    private ListView<Libro> listaLibros;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TabPane tabGeneros;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtAutor;
    @FXML
    private ComboBox<String> comboGenero;
    @FXML
    private ComboBox<String> comboEstado;
    private ObservableList<Libro> libros = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
    	System.out.println("LibroController inicializado");
        listaLibros.setItems(libros);
        
        listaLibros.setCellFactory(lv -> new ListCell<Libro>() {
            @Override
            protected void updateItem(Libro libro, boolean empty) {
                super.updateItem(libro, empty);

                if (empty || libro == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(
                        libro.getTitulo() + " - " +
                        libro.getAutor() + " [" +
                        libro.getEstado() + "]"
                    );

                    if ("En posesión".equals(libro.getEstado())) {
                        setStyle("-fx-text-fill: green;");
                    } else {
                        setStyle("-fx-text-fill: orange;");
                    }
                }
            }
        });
        
        numLibrosPosesion.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        numLibrosPendiente.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");

        comboGenero.setItems(FXCollections.observableArrayList("USA", "Francés", "Grecolatino", "Esp. clásicos", "Asia", "Nordico", "Centroeuropeo", "Britanico", 
        		"Ruso", "Esp. actual", "Sudamericano", "Italiano", "África"));
        comboGenero.getSelectionModel().selectFirst();
        
        // ComboBox para estado
        comboEstado.setItems(FXCollections.observableArrayList("En posesión", "Pendiente"));
        comboEstado.getSelectionModel().selectFirst();

        cargarLibros();

        Platform.runLater(() -> {
            actualizarPestañasGeneros();
            filtrarLista();
        });
        
        tabGeneros.getSelectionModel().selectedItemProperty()
        .addListener((obs, oldTab, newTab) -> filtrarLista());

        // Buscador dinámico
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> filtrarLista());
    }

    private void cargarLibros() {
        DatabaseReference ref = FirebaseService.getDatabase().child("libros");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

            	Platform.runLater(() -> {
            	    libros.clear();

            	    for (DataSnapshot ds : snapshot.getChildren()) {
            	        @SuppressWarnings("unchecked")
            	        java.util.Map<String, Object> map =
            	                (java.util.Map<String, Object>) ds.getValue();

            	        String id = ds.getKey();
            	        String titulo = (String) map.get("titulo");
            	        String autor = (String) map.get("autor");
            	        String estado = (String) map.get("estado");
            	        String genero = map.get("genero") != null ? (String) map.get("genero") : "Sin género";

            	        libros.add(new Libro(id, titulo, autor, estado, genero));
            	    }

            	    int enPosesion = 0;
            	    int pendientes = 0;
            	    
            	    for(DataSnapshot ds : snapshot.getChildren()) {
            	    	Map<String, Object> map = (Map<String, Object>) ds.getValue();
            	    	
            	    	String estado = (String) map.get("estado");
            	    	
            	    	if ("En posesión".equals(estado)) {
            	    		enPosesion++;
            	    	}else if ("Pendiente".equals(estado)) {
            	    		pendientes++;
            	    	}
            	    }
            	    
            	    // 🔢 Actualizar contador
            	    numLibrosPosesion.setText("📗 En posesión: " + enPosesion);
            	    numLibrosPendiente.setText("📙 Pendientes: " + pendientes);

            	    // 🟢 Actualizar pestañas dinámicamente después de cargar los libros
            	    actualizarPestañasGeneros();

            	    // Filtrar lista
            	    filtrarLista();
            	});
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("ERROR FIREBASE: " + error.getMessage());
            }
        });
    }

    private void actualizarPestañasGeneros() {
        tabGeneros.getTabs().clear();

        Tab todosTab = new Tab("Todos");
        todosTab.setClosable(false);
        tabGeneros.getTabs().add(todosTab);

        Set<String> generos = new HashSet<>();
        for (Libro libro : libros) {
            String genero = libro.getGenero() != null ? libro.getGenero() : "Sin género";
            generos.add(genero);
        }

        for (String g : generos) {
            Tab tab = new Tab(g);
            tab.setClosable(false);
            tabGeneros.getTabs().add(tab);
        }

        // Selecciona la primera pestaña de forma segura
        if (!tabGeneros.getTabs().isEmpty()) {
            tabGeneros.getSelectionModel().selectFirst();
        }
    }

    private void filtrarLista() {
        String filtroTexto = txtBuscar.getText().toLowerCase();
        Tab tabSeleccionada = tabGeneros.getSelectionModel().getSelectedItem();

        // 🔹 Si no hay ninguna pestaña seleccionada, seleccionamos la primera
        if (tabSeleccionada == null && !tabGeneros.getTabs().isEmpty()) {
            tabGeneros.getSelectionModel().selectFirst();
            tabSeleccionada = tabGeneros.getSelectionModel().getSelectedItem();
        }

        if (tabSeleccionada == null) {
            // Si aún es null, salimos para evitar crash
            return;
        }

        String filtroGenero = tabSeleccionada.getText();

        ObservableList<Libro> filtrados = FXCollections.observableArrayList();

        for (Libro libro : libros) {
            boolean coincideTexto = libro.getTitulo().toLowerCase().contains(filtroTexto)
                    || libro.getAutor().toLowerCase().contains(filtroTexto);

            boolean coincideGenero = filtroGenero.equals("Todos")
                    || libro.getGenero().equalsIgnoreCase(filtroGenero); // o estado si quieres

            if (coincideTexto && coincideGenero) {
                filtrados.add(libro);
            }
        }

        listaLibros.setItems(filtrados);
    }

    @FXML
    private void handleAgregar() {
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String genero = comboGenero.getSelectionModel().getSelectedItem();
        String estado = comboEstado.getSelectionModel().getSelectedItem();

        if (titulo.isEmpty() || autor.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Debe completar título y autor");
            alert.show();
            return;
        }

        if (genero.isEmpty()){
        	Libro libro = new Libro(null, titulo, autor, estado, "Sin género");
        	agregarLibro(libro);
        }else {
        	Libro libro = new Libro(null, titulo, autor, estado, genero);
            agregarLibro(libro);
        }

        // Limpiar campos
        txtTitulo.clear();
        txtAutor.clear();
        comboGenero.getSelectionModel().selectFirst();
        comboEstado.getSelectionModel().selectFirst();
    }

    private void agregarLibro(Libro libro) {
        DatabaseReference ref = FirebaseService.getDatabase().child("libros");

        // Obtenemos todos los hijos actuales para determinar el siguiente número
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int max = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.getKey(); // ejemplo: libro1, libro2
                    if (key.startsWith("libro")) {
                        try {
                            int num = Integer.parseInt(key.substring(5));
                            if (num > max) max = num;
                        } catch (NumberFormatException ignored) {}
                    }
                }

                int siguiente = max + 1;
                String id = "libro" + siguiente;
                libro.setId(id);
                ref.child(id).setValueAsync(libro);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Error al agregar libro: " + error.getMessage());
            }
        });
    }
}
