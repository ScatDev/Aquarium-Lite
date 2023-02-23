package dev.scat.aquarium.data;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.PacketUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PlayerData {

    private final Player player;

    private final List<Processor> processors = new ArrayList<>();

    private int tick;

    @Setter
    private boolean alerting, punishing;

    public PlayerData(Player player) {
        this.player = player;

        if (player.hasPermission("aquarium.alerts")) {
            alerting = true;
        }
    }

    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) ++tick;

        processors.forEach(processor -> processor.handle(event));
    }

    public void handle(PacketSendEvent event) {
        processors.forEach(processor -> processor.handle(event));
    }
}
