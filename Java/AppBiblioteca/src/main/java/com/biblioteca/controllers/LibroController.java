package com.biblioteca.controllers;

import com.biblioteca.models.Libro;
import com.biblioteca.services.FirebaseService;
import com.google.firebase.database.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LibroController {

	@FXML
	private Label numLibros;
    @FXML
    private ListView<Libro> listaLibros;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtAutor;
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

        // ComboBox para estado
        comboEstado.setItems(FXCollections.observableArrayList("En posesión", "Pendiente"));
        comboEstado.getSelectionModel().selectFirst();

        cargarLibros();

        // Buscador dinámico
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> filtrarLista(newVal));
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

                        libros.add(new Libro(id, titulo, autor, estado));
                    }

                    // 🔢 ACTUALIZAR CONTADOR
                    numLibros.setText("Libros: " + snapshot.getChildrenCount());
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("ERROR FIREBASE: " + error.getMessage());
            }
        });
    }


    private void filtrarLista(String filtro) {
        ObservableList<Libro> filtrados = FXCollections.observableArrayList();

        for (Libro libro : libros) {
            if (libro.getTitulo().toLowerCase().contains(filtro.toLowerCase()) ||
                libro.getAutor().toLowerCase().contains(filtro.toLowerCase())) {
                filtrados.add(libro);
            }
        }

        listaLibros.setItems(filtrados);
    }

    @FXML
    private void handleAgregar() {
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String estado = comboEstado.getSelectionModel().getSelectedItem();

        if (titulo.isEmpty() || autor.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Debe completar título y autor");
            alert.show();
            return;
        }

        Libro libro = new Libro(null, titulo, autor, estado);
        agregarLibro(libro);

        // Limpiar campos
        txtTitulo.clear();
        txtAutor.clear();
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
