package dev.scat.aquarium.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.collision.CollisionBox;
import dev.scat.aquarium.util.collision.SimpleCollisionBox;
import dev.scat.aquarium.util.mc.AxisAlignedBB;
import dev.scat.aquarium.util.mc.MathHelper;
import dev.scat.aquarium.util.mc.Vec3;
import io.github.retrooper.packetevents.util.protocolsupport.ProtocolSupportUtil;
import io.github.retrooper.packetevents.util.viaversion.ViaVersionUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class PlayerUtil {

    public float getEyeHeight(boolean sneaking) {
        float eyeHeight = 1.62F;

        if (sneaking) {
            eyeHeight -= 0.08F;
        }

        return eyeHeight;
    }

    public AxisAlignedBB getBoundingBox(double x, double y, double z) {
        float width = 0.6F / 2.0F;

        return new AxisAlignedBB(x - width, y, z - width, x + width, y + 1.8F, z + width);
    }

    public Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3((double) (f1 * f2), (double) f3, (double) (f * f2));
    }

    public boolean isInWater(PlayerData data) {
        AxisAlignedBB bb = getBoundingBox(data.getPositionProcessor().getX(),
                data.getPositionProcessor().getY(), data.getPositionProcessor().getZ())
                .expand(0, -0.4000000059604645D, 0)
                .contract(0.001, 0.001, 0.001);

        for (int x = MathHelper.floor_double(bb.minX); x <= MathHelper.floor_double(bb.maxX); x++) {
            for (int y = MathHelper.floor_double(bb.minY); y <= MathHelper.floor_double(bb.maxY); y++) {
                for (int z = MathHelper.floor_double(bb.minZ); z <= MathHelper.floor_double(bb.maxZ); z++) {
                    WrappedBlockState block = data.getWorldProcessor().getBlock(x, y, z);

                    if (block.getType() == StateTypes.WATER)
                        return true;
                }
            }
        }

        return false;
    }

    public boolean isInLava(PlayerData data) {
        AxisAlignedBB bb = getBoundingBox(data.getPositionProcessor().getX(),
                data.getPositionProcessor().getY(), data.getPositionProcessor().getZ())
                .expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D);

        for (int x = MathHelper.floor_double(bb.minX); x <= MathHelper.floor_double(bb.maxX); x++) {
            for (int y = MathHelper.floor_double(bb.minY); y <= MathHelper.floor_double(bb.maxY); y++) {
                for (int z = MathHelper.floor_double(bb.minZ); z <= MathHelper.floor_double(bb.maxZ); z++) {
                    WrappedBlockState block = data.getWorldProcessor().getBlock(x, y, z);

                    if (block.getType() == StateTypes.LAVA)
                        return true;
                }
            }
        }

        return false;
    }

    public float[] getRotations(Vec3 origin, Vec3 position) {
        Vec3 org = new Vec3(origin.xCoord, origin.yCoord, origin.zCoord);
        Vec3 difference = position.subtract(org);
        double distance = difference.flat().lengthVector();
        float yaw = ((float) Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0F);
        float pitch = (float) (-Math.toDegrees(Math.atan2(difference.yCoord, distance)));

        return new float[]{yaw, pitch};
    }

    public ClientVersion getClientVersion(User user) {
        if (user == null) return ClientVersion.UNKNOWN;

        if (user.getClientVersion() == null) {
            int protocolVersion;
            if (ProtocolSupportUtil.isAvailable()) {
                protocolVersion = ProtocolSupportUtil.getProtocolVersion(user.getAddress());
                PacketEvents.getAPI().getLogManager().debug("Requested ProtocolSupport for user " + user.getName() + "'s protocol version. Protocol version: " + protocolVersion);
            } else if (ViaVersionUtil.isAvailable()) {
                protocolVersion = ViaVersionUtil.getProtocolVersion(user);
                PacketEvents.getAPI().getLogManager().debug("Requested ViaVersion for " + user.getName() + "'s protocol version. Protocol version: " + protocolVersion);
            } else {
                //No protocol translation plugins available, the client must be the same version as the server.
                protocolVersion = PacketEvents.getAPI().getServerManager().getVersion().getProtocolVersion();
                PacketEvents.getAPI().getLogManager().debug("No protocol translation plugins are available. We will assume " + user.getName() + "'s protocol version is the same as the server's protocol version. Protocol version: " + protocolVersion);
            }
            ClientVersion version = ClientVersion.getById(protocolVersion);
            user.setClientVersion(version);
        }

        return user.getClientVersion();
    }

    public double calculateYOffset(SimpleCollisionBox box, AxisAlignedBB other, double offsetY) {
        if (other.maxX > box.getMinX() && other.minX < box.getMaxX() && other.maxZ > box.getMinZ() && other.minZ < box.getMaxZ()) {
            if (offsetY > 0.0D && other.maxY <= box.getMinY()) {
                double d1 = box.getMinY() - other.maxY;

                if (d1 < offsetY) {
                    offsetY = d1;
                }
            } else if (offsetY < 0.0D && other.minY >= box.getMaxY()) {
                double d0 = box.getMaxY() - other.minY;

                if (d0 > offsetY) {
                    offsetY = d0;
                }
            }

            return offsetY;
        } else {
            return offsetY;
        }
    }
}
