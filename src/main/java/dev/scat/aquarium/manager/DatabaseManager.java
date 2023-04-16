package dev.scat.aquarium.manager;

import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.database.Log;
import dev.scat.aquarium.database.impl.FlatFileImpl;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DatabaseManager {

    private final List<Log> pendingLogs = new ArrayList<>();

    private final FlatFileImpl flatFile = new FlatFileImpl();

    public void setup() {
        if (Config.DATABASE_TYPE.getValue().toString().equalsIgnoreCase("flat-file")) {

        }
    }

    public void addLog(Log log) {
        if (Config.DATABASE_TYPE.getValue().toString().equalsIgnoreCase("flat-file")) {
            flatFile.run(log);
        }
    }
}
