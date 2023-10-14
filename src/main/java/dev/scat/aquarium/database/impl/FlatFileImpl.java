package dev.scat.aquarium.database.impl;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.database.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FlatFileImpl {

    public void run(Log log) {
        PlayerData data = Aquarium.getInstance().getPlayerDataManager().get(log.getUuid());

        try {
            if (data == null)
                return;

            FileWriter fileWriter = new FileWriter(data.getLogsFile(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(log.toString());
            bufferedWriter.newLine();

            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public List<Log> getLogs(UUID uuid) {
        List<Log> logs = new ArrayList<>();

        File logsFile = new File(Aquarium.getInstance().getDataFolder() + File.separator + "logs"
                + File.separator + uuid.toString() + ".txt");
        try {
            FileReader fileReader = new FileReader(logsFile);
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

    public void deleteLogs(UUID uuid) {
        File logsFile = new File(Aquarium.getInstance().getDataFolder() + File.separator + "logs"
                + File.separator + uuid.toString() + ".txt");

        if (logsFile.exists())
            logsFile.delete();
    }
}
