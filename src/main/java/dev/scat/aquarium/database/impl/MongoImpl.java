package dev.scat.aquarium.database.impl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.database.Log;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MongoImpl {
    
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> logs;

    public MongoImpl() {
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString((String) Config.MONGO_URI.getValue()))
                .serverApi(serverApi)
                .build();

        mongoClient = MongoClients.create(settings);

        mongoDatabase = mongoClient.getDatabase(Config.MONGO_DATABASE.getValue().toString());

        logs = mongoDatabase.getCollection(Config.MONGO_COLLECTION.getValue().toString());
    }

    public void run() {
        List<Document> documents = Aquarium.getInstance().getDatabaseManager().getPendingLogs()
                .stream().map(Log::toDocument).collect(Collectors.toList());

        logs.insertMany(documents);
    }
}
