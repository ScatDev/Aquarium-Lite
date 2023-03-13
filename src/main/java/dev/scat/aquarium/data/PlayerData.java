package dev.scat.aquarium.data;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.processor.PositionProcessor;
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
    private final PositionProcessor positionProcessor = new PositionProcessor(this);
    
    private final List<Check> checks = Aquarium.getInstance().getCheckManager().loadChecks(this);

    private int tick;

    @Setter
    private boolean alerting, punishing;

    public PlayerData(Player player) {
        this.player = player;

        processors.add(positionProcessor);

        if (player.hasPermission("aquarium.alerts")) {
            alerting = true;
        }
    }

    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) ++tick;

        processors.forEach(processor -> processor.handle(event));

        checks.forEach(check -> check.handle(event));
    }

    public void handle(PacketSendEvent event) {
        processors.forEach(processor -> processor.handle(event));

        checks.stream().filter(Check::isEnabled).forEach(check -> check.handle(event));
    }
}
