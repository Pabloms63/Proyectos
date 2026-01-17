package com.biblioteca.controllers;

import com.biblioteca.models.Libro;
import com.biblioteca.services.FirebaseService;
import com.google.firebase.database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LibroController {

    @FXML
    private ListView<String> listaLibros;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtAutor;
    @FXML
    private ComboBox<String> comboEstado;

    private ObservableList<Libro> libros = FXCollections.observableArrayList();
    private ObservableList<String> librosObservable = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        listaLibros.setItems(librosObservable);

        // ComboBox para estado
        comboEstado.setItems(FXCollections.observableArrayList("en Posesion", "Pendiente"));
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
                libros.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // ds es libro1, libro2, ...
                    Libro libro = ds.getValue(Libro.class);
                    if (libro != null) libros.add(libro);
                    else System.out.println("Libro nulo: " + ds.getValue());
                }
                filtrarLista(txtBuscar.getText());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Error al leer libros: " + error.getMessage());
            }
        });
    }

    private void filtrarLista(String filtro) {
        librosObservable.clear();
        for (Libro libro : libros) {
            if (libro.getTitulo().toLowerCase().contains(filtro.toLowerCase()) ||
                libro.getAutor().toLowerCase().contains(filtro.toLowerCase())) {
                librosObservable.add(libro.getTitulo() + " - " + libro.getAutor() +
                        " [" + libro.getEstado() + "]");
            }
        }
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
