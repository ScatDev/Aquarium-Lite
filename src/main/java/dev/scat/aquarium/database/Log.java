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
    private final String type, name, info;
    private final double vl;

    public Log(UUID uuid, String string) {
        String[] split = string.split("//");

        if (split.length != 10) {
            Bukkit.getLogger().info("Invalid log from string, length: " + split.length +".");
        }

        this.uuid = uuid;
        time = Long.parseLong(split[1]);
        type = split[3];
        name = split[5];
        info = split[7];
        vl = Double.parseDouble(split[9]);
    }

    public Log(Document doc) {
        uuid = doc.get("uuid", UUID.class);
        time = doc.getLong("time");
        type = doc.getString("type");
        name = doc.getString("name");
        info = doc.getString("info");
        vl = doc.getDouble("vl");
    }
    
    public Document toDocument() {
        Document doc = new Document();

        doc.put("uuid", uuid);
        doc.put("time", time);
        doc.put("type", type);
        doc.put("name", name);
        doc.put("info", info);
        doc.put("vl", vl);

        return doc;
    }

    public String toString() {
        return "Time//" + time + "//Type//" + type + "//Name//" + name
                + "//Info//" + info + "//VL//" + vl;
    }
}
