/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.persistencia;

/**
 *
 * @author ordon
 */
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

public class ConexionMongoDB {
    private static final String URL = "mongodb://localhost:27017";
    private static final String DB_NAME = "hotel_db";
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static MongoDatabase getDatabase() {
        if (database == null) {
            mongoClient = MongoClients.create(URL);
            database = mongoClient.getDatabase(DB_NAME);
            inicializarIndices();
        }
        return database;
    }

    private static void inicializarIndices() {
        IndexOptions unique = new IndexOptions().unique(true);
        database.getCollection("usuarios").createIndex(Indexes.ascending("nombreUsuario"), unique);
        database.getCollection("clientes").createIndex(Indexes.ascending("codigoCliente"), unique);
        database.getCollection("habitaciones").createIndex(Indexes.ascending("codigoHabitacion"), unique);
        database.getCollection("reservas").createIndex(Indexes.ascending("codigoReserva"), unique);
    }

    public static void cerrar() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
        }
    }
}

