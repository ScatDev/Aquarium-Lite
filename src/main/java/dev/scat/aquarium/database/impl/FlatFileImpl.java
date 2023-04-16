package dev.scat.aquarium.database.impl;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.database.Log;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FlatFileImpl {

    public void run(Log log) {
        PlayerData data = Aquarium.getInstance().getPlayerDataManager().get(log.getUuid());

        try {
            FileWriter fileWriter = new FileWriter(data.getLogsFile(), false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(log.toString());
            bufferedWriter.newLine();

            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        long end = System.currentTimeMillis();
    }

    public List<Log> getLogs(UUID uuid) {
        List<Log> logs = new ArrayList<>();

        PlayerData data = Aquarium.getInstance().getPlayerDataManager().get(uuid);

        try {
            FileReader fileReader = new FileReader(data.getLogsFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            bufferedReader.lines().collect(Collectors.toList()).stream()
                    .map(string -> new Log(uuid, string))
                    .forEach(logs::add);

            fileReader.close();
            bufferedReader.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return logs;
    }
}
