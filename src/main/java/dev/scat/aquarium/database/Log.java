package dev.scat.aquarium.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.Calendar;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Log {
    private final UUID uuid;
    private final long time;
    private final String action;
    private final String info;
    private final int vl;

    public Log(UUID uuid, String string) {
        String[] split = string.split(": ");

        if (split.length != 8) {
            Bukkit.getLogger().info("Invalid log from string, length: " + split.length +".");
        }

        this.uuid = uuid;
        time = Long.parseLong(split[1]);
        action = split[3];
        info = split[5];
        vl = Integer.parseInt(split[7]);
    }

    public Log(Document doc) {
        uuid = doc.get("uuid", UUID.class);
        time = doc.getLong("time");
        action = doc.getString("action");
        info = doc.getString("info");
        vl = doc.getInteger("vl");
    }
    
    public Document toDocument() {
        Document doc = new Document();

        doc.put("uuid", uuid.toString());
        doc.put("time", time);
        doc.put("action", action);
        doc.put("info", info);
        doc.put("vl", vl);

        return doc;
    }

    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return "Time: " + calendar.getTime() + " Action: " + action + " Info: " + info + " VL: " + vl;
    }
}
