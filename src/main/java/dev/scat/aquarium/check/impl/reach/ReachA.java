package dev.scat.aquarium.check.impl.reach;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.PacketUtil;
import dev.scat.aquarium.util.PlayerUtil;
import dev.scat.aquarium.util.TrackedEntity;
import dev.scat.aquarium.util.mc.AxisAlignedBB;
import dev.scat.aquarium.util.mc.MovingObjectPosition;
import dev.scat.aquarium.util.mc.Vec3;
import org.bukkit.Bukkit;

public class ReachA extends Check {

    private int id;
    private boolean attacked;

    public ReachA(PlayerData data) {
        super(data, "Reach", "A");
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity useEntity = new WrapperPlayClientInteractEntity(event);

            if (useEntity.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                attacked = true;
                id = useEntity.getEntityId();
            }
        } else if (PacketUtil.isFlying(event.getPacketType())) {
             if (!attacked) return;
             attacked = false;

            TrackedEntity entity = data.getEntityProcessor().getTrackedEntities().get(id);

            if (entity == null) return;

            double x = data.getPositionProcessor().getLastX();

            // Switch from player.isSneaking
            double y = data.getPositionProcessor().getLastY()
                    + (double) PlayerUtil.getEyeHeight(data.getPlayer().isSneaking());
            double z = data.getPositionProcessor().getLastZ();

            AxisAlignedBB aabb = entity.getBoundingBox();

            if (data.getVersion().isOlderThanOrEquals(ClientVersion.V_1_8))
                aabb.expand(0.1, 0.1, 0.1);

            Vec3 eyePos = new Vec3(x, y, z);

            Vec3[] rotations = {
                    PlayerUtil.getVectorForRotation(data.getRotationProcessor().getPitch(),
                            data.getRotationProcessor().getYaw()),
                    PlayerUtil.getVectorForRotation(data.getRotationProcessor().getLastPitch(),
                            data.getRotationProcessor().getLastYaw())
            };

            double distance = 10;

            for (Vec3 rotation : rotations) {
                Vec3 ray = eyePos.addVector(rotation.xCoord * 6,
                        rotation.yCoord * 6, rotation.zCoord * 6);

                MovingObjectPosition collision = aabb.calculateIntercept(eyePos, ray);

                if (collision != null) {
                    distance = Math.min(eyePos.distanceTo(collision.hitVec), distance);
                }
            }

            Bukkit.broadcastMessage("distance=" + distance);
        }
    }
}
