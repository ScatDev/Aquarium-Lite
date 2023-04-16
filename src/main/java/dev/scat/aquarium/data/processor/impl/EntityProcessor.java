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

            double x = spawnPlayer.getPosition().getX();
            double y = spawnPlayer.getPosition().getY();
            double z = spawnPlayer.getPosition().getZ();

            data.getPledgeProcessor().sendTransaction(()
                    -> trackedEntities.put(spawnPlayer.getEntityId(), new TrackedEntity(x, y, z)));
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            WrapperPlayServerEntityRelativeMove relMove = new WrapperPlayServerEntityRelativeMove(event);

            int x = (int) Math.round(relMove.getDeltaX() * 32D);
            int y = (int) Math.round(relMove.getDeltaY() * 32D);
            int z = (int) Math.round(relMove.getDeltaZ() * 32D);

            int id = relMove.getEntityId();

            data.getPledgeProcessor().sendTransaction(() -> {
                TrackedEntity entity = trackedEntities.get(id);

                if (entity != null) {
                    entity.setServerPos(entity.getServerX() + x, entity.getServerY() + y, entity.getServerZ() + z);

                    entity.handleMovement(entity.getServerX() / 32D, entity.getServerY() / 32D, entity.getServerZ() / 32D);
                }
            });
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            WrapperPlayServerEntityRelativeMoveAndRotation relMove =
                    new WrapperPlayServerEntityRelativeMoveAndRotation(event);

            int x = (int) Math.round(relMove.getDeltaX() * 32D);
            int y = (int) Math.round(relMove.getDeltaY() * 32D);
            int z = (int) Math.round(relMove.getDeltaZ() * 32D);

            int id = relMove.getEntityId();

            data.getPledgeProcessor().sendTransaction(() -> {
                TrackedEntity entity = trackedEntities.get(id);

                if (entity != null) {
                    entity.setServerPos(entity.getServerX() + x, entity.getServerY() + y, entity.getServerZ() + z);

                    entity.handleMovement(entity.getServerX() / 32D, entity.getServerY() / 32D, entity.getServerZ() / 32D);
                }
            });
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            WrapperPlayServerEntityTeleport entityTeleport = new WrapperPlayServerEntityTeleport(event);

            int x = (int) Math.round(entityTeleport.getPosition().getX() * 32D);
            int y = (int) Math.round(entityTeleport.getPosition().getY() * 32D);
            int z = (int) Math.round(entityTeleport.getPosition().getZ() * 32D);

            int id = entityTeleport.getEntityId();

            data.getPledgeProcessor().sendTransaction(() -> {
                TrackedEntity entity = trackedEntities.get(id);

                if (entity != null) {
                    entity.setServerPos(x, y, z);

                    if (Math.abs(entity.getPosX() - (x / 32D)) < 0.03125D
                            && Math.abs(entity.getPosY() - (y / 32D)) < 0.015625D
                            && Math.abs(entity.getPosZ() - (z / 32D)) < 0.03125D) {
                        entity.handleMovement(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                    } else {
                        entity.handleMovement(entity.getServerX() / 32D, entity.getServerY() / 32D, entity.getServerZ() / 32D);
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

//        if (trackedEntities.values().stream().findFirst().isPresent()) {
//            TrackedEntity entity = trackedEntities.values().stream().findFirst().get();
//
//            data.getPlayer().sendMessage(entity.getPosX() + " " + entity.getPosY() + " " + entity.getPosZ());
//        }
    }
}
