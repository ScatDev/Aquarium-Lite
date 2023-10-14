package dev.scat.aquarium.data;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.data.processor.impl.*;
import dev.scat.aquarium.database.Log;
import dev.scat.aquarium.util.ColorUtil;
import dev.scat.aquarium.util.PacketUtil;
import dev.thomazz.pledge.api.event.PacketFrameReceiveEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class PlayerData {

    private final Player player;
    private final User user;
    private ClientVersion version;

    private final List<Processor> processors = new ArrayList<>();
    private PositionProcessor positionProcessor;
    private ActionProcessor actionProcessor;
    private RotationProcessor rotationProcessor;
    private WorldProcessor worldProcessor;
    private TransactionProcessor transactionProcessor;
    private CollisionProcessor collisionProcessor;
    private AbilitiesProcessor abilitiesProcessor;
    private PotionProcessor potionProcessor;
    private VelocityProcessor velocityProcessor;
    private CombatProcessor combatProcessor;
    private VehicleProcessor vehicleProcessor;
    private SetbackProcessor setbackProcessor;
    
    private final List<Check> checks;

    private int tick;

    private File logsFile;

    @Setter
    private boolean alerting, punishing;

    @Setter
    private String debug = "none";

    public PlayerData(Player player) {
        this.player = player;

        user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        version = PacketEvents.getAPI().getPlayerManager().getClientVersion(player);

        // Do this so processors can use user and version on init
        positionProcessor = new PositionProcessor(this);
        rotationProcessor = new RotationProcessor(this);
        worldProcessor = new WorldProcessor(this);
        transactionProcessor = new TransactionProcessor(this);
        collisionProcessor = new CollisionProcessor(this);
        potionProcessor = new PotionProcessor(this);
        abilitiesProcessor = new AbilitiesProcessor(this);
        velocityProcessor = new VelocityProcessor(this);
        combatProcessor = new CombatProcessor(this);
        vehicleProcessor = new VehicleProcessor(this);
        actionProcessor = new ActionProcessor(this);
        setbackProcessor = new SetbackProcessor(this);

        processors.add(positionProcessor);
        processors.add(rotationProcessor);
        processors.add(worldProcessor);
        processors.add(transactionProcessor);
        processors.add(collisionProcessor);
        processors.add(potionProcessor);
        processors.add(abilitiesProcessor);
        processors.add(velocityProcessor);
        processors.add(combatProcessor);
        processors.add(actionProcessor);
        processors.add(vehicleProcessor);
        processors.add(setbackProcessor);

        checks = Aquarium.getInstance().getCheckManager().loadChecks(this);

        alerting = player.hasPermission("aquarium.alerts");

        if (alerting) {
            Aquarium.getInstance().getPlayerDataManager().getAlertingPlayers().add(player);
        }

        if (Config.DATABASE_TYPE.getValue().toString().equalsIgnoreCase("flat-file")) {
            logsFile = new File(Aquarium.getInstance().getDataFolder() + File.separator + "logs"
                    + File.separator + player.getUniqueId() + ".txt");

            try {
                logsFile.getParentFile().mkdir();
                logsFile.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

    }

    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) ++tick;

        processors.forEach(processor -> processor.handlePre(event));

        checks.stream().filter(Check::isEnabled).forEach(check -> check.handle(event));

        processors.forEach(processor -> processor.handlePost(event));
    }

    public void handle(PacketSendEvent event) {
        processors.forEach(processor -> processor.handlePre(event));

        checks.stream().filter(Check::isEnabled).forEach(check -> check.handle(event));

        processors.forEach(processor -> processor.handlePost(event));
    }

    public void handle(PacketFrameReceiveEvent event) {
        transactionProcessor.handle(event);

        processors.forEach(processor -> processor.handlePre(event));

        checks.stream().filter(Check::isEnabled).forEach(check -> check.handle(event));

        processors.forEach(processor -> processor.handlePost(event));
    }

    public void notify(String input) {
        String message = ((String) Config.NOTIFY_MESSAGE.getValue())
                .replaceAll("%player%", player.getName())
                .replaceAll("%message%", ColorUtil.translate(input));

        Aquarium.getInstance().getExecutorService().execute(()
                -> Aquarium.getInstance().getPlayerDataManager().getAlertingPlayers()
                .forEach(data -> Objects.requireNonNull(data.getPlayer())
                        .sendMessage(message)));

        Bukkit.getConsoleSender().sendMessage(message);

        Aquarium.getInstance().getDatabaseManager().addLog(
                new Log(player.getUniqueId(),
                        System.currentTimeMillis(),
                        "NOTIFY", "NOTIFY", message, 0));
    }
}
