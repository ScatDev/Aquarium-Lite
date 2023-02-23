package dev.scat.aquarium.data;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.PacketUtil;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PlayerData {

    private final Player player;

    private final List<Processor> processors = new ArrayList<>();

    private int tick;

    public PlayerData(Player player) {
        this.player = player;
    }

    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) ++tick;

        processors.forEach(processor -> processor.handle(event));
    }

    public void handle(PacketSendEvent event) {
        processors.forEach(processor -> processor.handle(event));
    }
}
