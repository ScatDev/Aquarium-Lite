package dev.scat.aquarium.check.impl.reach;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.PacketUtil;
import dev.scat.aquarium.util.PlayerUtil;
import dev.scat.aquarium.util.TrackedEntity;
import dev.scat.aquarium.util.mc.AxisAlignedBB;
import dev.scat.aquarium.util.mc.MovingObjectPosition;
import dev.scat.aquarium.util.mc.Vec3;

public class ReachA extends Check {

    private int id;
    private boolean attacked;

    private final static boolean[] BOOLEANS = {true, false};

    public ReachA(PlayerData data) {
        super(data, "Reach", "A", 0);
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

            AxisAlignedBB aabb = entity.getBoundingBox();

            if (data.getVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) {
                aabb = aabb.expand(0.1F, 0.1F, 0.1F);
            }

            if (data.getPositionProcessor().getTicksSincePosition() > 0) {
                aabb.expand(0.03, 0.03, 0.03);
            }

            Vec3[] rotations = {
                    PlayerUtil.getVectorForRotation(data.getRotationProcessor().getPitch(),
                            data.getRotationProcessor().getYaw()),
                    PlayerUtil.getVectorForRotation(data.getRotationProcessor().getLastPitch(),
                            data.getRotationProcessor().getLastYaw()),
            };
            double x = data.getPositionProcessor().getLastX();
            double z = data.getPositionProcessor().getLastZ();

            double distance = 10;

            for (boolean sneaking : BOOLEANS) {
                for (Vec3 rotation : rotations) {

                    double y = data.getPositionProcessor().getLastY()
                            + (double) PlayerUtil.getEyeHeight(sneaking);

                    Vec3 eyePos = new Vec3(x, y, z);

                    Vec3 ray = eyePos.addVector(rotation.xCoord * 6D,
                            rotation.yCoord * 6D, rotation.zCoord * 6D);

                    MovingObjectPosition collision = aabb.calculateIntercept(eyePos, ray);

                    if (collision != null) {
                        distance = Math.min(eyePos.distanceTo(collision.hitVec), distance);
                    }
                }
            }

            final double maxDistance = data.getAbilitiesProcessor().getAbilities().isCreativeMode() ? 4.501D : 3.01D;

            // This will false on a lot of stuff
            if (distance > maxDistance) {
                flag((distance == 10 ? "tried to attack oustide of the hitbox" : "reach=" + distance));
            }
        }
    }
}
