package dev.scat.aquarium.manager;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.database.Log;
import dev.scat.aquarium.database.impl.FlatFileImpl;
import dev.scat.aquarium.database.impl.MongoImpl;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public class DatabaseManager {

    private final List<Log> pendingLogs = new ArrayList<>();
    private final List<Log> temporaryLogs = new ArrayList<>();

    @Setter
    private boolean clearingLogs;
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
            Aquarium.getInstance().getExecutorService().execute(() -> flatFile.run(log));
        } else if (Config.DATABASE_TYPE.getValue().toString().equalsIgnoreCase("mongo")) {
            if (clearingLogs) {
                temporaryLogs.add(log);
            } else {
                pendingLogs.add(log);
            }
        }
    }

    public void run() {
        if (Config.DATABASE_TYPE.getValue().toString().equalsIgnoreCase("mongo")) {
            mongo.run();
        }
    }

    public List<Log> getLogs(UUID uuid, int page) {
        List<Log> logsByPage = new ArrayList<>();

        if (Config.DATABASE_TYPE.getValue().toString().equalsIgnoreCase("mongo")) {
            logsByPage = mongo.getLogs(uuid);
        } else if (Config.DATABASE_TYPE.getValue().toString().equalsIgnoreCase("flat-file")) {
            logsByPage = flatFile.getLogs(uuid);
        }

        logsByPage.sort(Comparator.comparingLong(Log::getTime));
        Collections.reverse(logsByPage);

        List<Log> logs = new ArrayList<>();

        for (int i = (page * 8) - 8; i < page * 8; i++) {
            if (i >= logsByPage.size())
                break;

            Log log = logsByPage.get(i);

            if (log != null) {
                logs.add(log);
            }
        }

        return logs;
    }

    public void removeLogs(UUID uuid) {
        if (Config.DATABASE_TYPE.getValue().toString().equalsIgnoreCase("mongo")) {
            mongo.deleteLogs(uuid);
        } else if (Config.DATABASE_TYPE.getValue().toString().equalsIgnoreCase("flat-file")) {
            flatFile.deleteLogs(uuid);
        }
    }
}
