package com.biblioteca.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;

public class FirebaseService {
	private static DatabaseReference database;
	
	public static void init() {
		try {
			InputStream serviceAccount = FirebaseService.class.getResourceAsStream("/serviceAccountKey.json");
			
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setDatabaseUrl("https://biblioteca-personal-23e06-default-rtdb.firebaseio.com/")
					.build();
			
			FirebaseApp.initializeApp(options);
			
			//Referencia a la bbdd
			database = FirebaseDatabase.getInstance().getReference();
			
			System.out.println("Firebase inicializado correctamente.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DatabaseReference getDatabase() {
		return database;
	}
}
