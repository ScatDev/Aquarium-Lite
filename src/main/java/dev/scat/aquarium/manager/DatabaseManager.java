package dev.scat.aquarium.manager;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.database.Log;
import dev.scat.aquarium.database.impl.FlatFileImpl;
import dev.scat.aquarium.database.impl.MongoImpl;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DatabaseManager {

    private final List<Log> pendingLogs = new ArrayList<>();

    private FlatFileImpl flatFile;
    private MongoImpl mongo;

    public void setup() {
        if (Config.DATABASE_TYPE.getValue().toString().equalsIgnoreCase("flat-file")) {
            flatFile = new FlatFileImpl();
        } else if (Config.DATABASE_TYPE.getValue().toString().equalsIgnoreCase("mongo")) {
            mongo = new MongoImpl();
        }
    }

    public void addLog(Log log) {
        if (Config.DATABASE_TYPE.getValue().toString().equalsIgnoreCase("flat-file")) {
            Aquarium.getInstance().getExecutorService().execute(()
                    -> flatFile.run(log));
        } else {
            pendingLogs.add(log);
        }
    }

    public void run() {
        if (pendingLogs.isEmpty())
            return;

        if (Config.DATABASE_TYPE.getValue().toString().equalsIgnoreCase("mongo")) {
            mongo.run();
        }

        pendingLogs.clear();
    }
}
