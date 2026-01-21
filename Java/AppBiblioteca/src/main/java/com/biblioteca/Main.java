package com.biblioteca;

import com.biblioteca.services.FirebaseService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage stage) {
	    try {
	        FirebaseService.init();

	        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/libros.fxml"));
	        Parent root = fxmlLoader.load();
	        Scene scene = new Scene(root, 875, 475);
	        stage.setTitle("Biblioteca");
	        stage.setScene(scene);
	        stage.show();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

    public static void main(String[] args) {
        launch();
    }
}