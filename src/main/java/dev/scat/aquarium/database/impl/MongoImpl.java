package dev.scat.aquarium.database.impl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.database.Log;
import lombok.Getter;
import org.bson.Document;
import org.bson.UuidRepresentation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MongoImpl {
    
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    @Getter
    private MongoCollection<Document> logs;

    public MongoImpl() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString((String) Config.MONGO_URI.getValue()))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();

        mongoClient = MongoClients.create(settings);

        mongoDatabase = mongoClient.getDatabase(Config.MONGO_DATABASE.getValue().toString());

        logs = mongoDatabase.getCollection(Config.MONGO_COLLECTION.getValue().toString());
    }

    public void run() {
        Aquarium.getInstance().getDatabaseManager().setClearingLogs(true);

        if (!Aquarium.getInstance().getDatabaseManager().getPendingLogs().isEmpty()) {
            if (Aquarium.getInstance().getDatabaseManager().getPendingLogs().size() > 1) {
                logs.insertMany(Aquarium.getInstance().getDatabaseManager().getPendingLogs()
                        .stream().map(Log::toDocument)
                        .collect(Collectors.toList()));
            } else {
                logs.insertOne(Aquarium.getInstance().getDatabaseManager().getPendingLogs()
                        .stream().findFirst().get().toDocument());
            }

            Aquarium.getInstance().getDatabaseManager().getPendingLogs().clear();
        }

        Aquarium.getInstance().getDatabaseManager().setClearingLogs(false);

        Iterator<Log> iterator = Aquarium.getInstance().getDatabaseManager().getTemporaryLogs().iterator();

        while (iterator.hasNext()) {
            Log log = iterator.next();

            iterator.remove();

            Aquarium.getInstance().getDatabaseManager().getPendingLogs().add(log);
        }
    }

    public List<Log> getLogs(UUID uuid) {
        List<Log> logsList = new ArrayList<>();

        logs.find(Filters.eq("uuid", uuid))
                .forEach(doc -> logsList.add(new Log(doc)));

        return logsList;
    }

    public void deleteLogs(UUID uuid) {
        logs.deleteMany(Filters.eq("uuid", uuid));
    }
}
