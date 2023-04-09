package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.PacketUtil;
import dev.scat.aquarium.util.TrackedEntity;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class EntityProcessor extends Processor {

    private final Map<Integer, TrackedEntity> trackedEntities = new HashMap<>();

    public EntityProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
            WrapperPlayServerSpawnPlayer spawnPlayer = new WrapperPlayServerSpawnPlayer(event);

            Bukkit.broadcastMessage("spawned player");

            double x = spawnPlayer.getPosition().getX();
            double y = spawnPlayer.getPosition().getY();
            double z = spawnPlayer.getPosition().getZ();

            TrackedEntity entity = new TrackedEntity(x, y, z);

            data.getPledgeProcessor().confirmPre(()
                    -> trackedEntities.put(spawnPlayer.getEntityId(), entity));
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            WrapperPlayServerEntityRelativeMove relMove = new WrapperPlayServerEntityRelativeMove(event);

            double x = relMove.getDeltaX();
            double y = relMove.getDeltaZ();
            double z = relMove.getDeltaZ();

            int id = relMove.getEntityId();

            data.getPledgeProcessor().confirmPre(() -> {
                TrackedEntity entity = trackedEntities.get(id);

                if (entity != null) {
                    entity.handleMovement(x, y, z);
                }
            });
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            WrapperPlayServerEntityRelativeMoveAndRotation relMove =
                    new WrapperPlayServerEntityRelativeMoveAndRotation(event);

            double x = relMove.getDeltaX();
            double y = relMove.getDeltaZ();
            double z = relMove.getDeltaZ();

            int id = relMove.getEntityId();

            data.getPledgeProcessor().confirmPre(() -> {
                TrackedEntity entity = trackedEntities.get(id);

                if (entity != null) {
                    entity.handleMovement(x, y, z);
                }
            });
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            WrapperPlayServerEntityTeleport entityTeleport = new WrapperPlayServerEntityTeleport(event);

            double x = entityTeleport.getPosition().getX();
            double y = entityTeleport.getPosition().getY();
            double z = entityTeleport.getPosition().getZ();

            int id = entityTeleport.getEntityId();

            data.getPledgeProcessor().confirmPre(() -> {
                TrackedEntity entity = trackedEntities.get(id);

                if (entity != null) {
                    if (Math.abs(entity.getPosX() - x) < 0.03125D
                            && Math.abs(entity.getPosY() - y) < 0.015625D
                            && Math.abs(entity.getPosZ() - z) < 0.03125D) {
                        entity.handleMovement(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                    } else {
                        entity.handleMovement(x, y, z);
                    }
                }
            });
        } else if (event.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES) {
            WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(event);

            data.getPledgeProcessor().confirmPost(()
                    -> Arrays.stream(destroyEntities.getEntityIds()).forEach(trackedEntities::remove));
        }
    }

    @Override
    public void handlePost(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            trackedEntities.values().forEach(TrackedEntity::interpolate);
        }
    }
}
