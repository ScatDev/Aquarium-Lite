package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.PacketUtil;
import lombok.Getter;

import java.util.ArrayDeque;

@Getter
public class PositionProcessor extends Processor {

    private double x, y, z, lastX, lastY, lastZ,
            lastLastX, lastLastY, lastLastZ,
            deltaX, deltaY, deltaZ, deltaXZ,
            lastDeltaX, lastDeltaY, lastDeltaZ, lastDeltaXZ;

    private int ticksSincePosition, ticksSinceTeleport;

    private boolean sentMotion, inLoadedChunk, lastInLoadedCHunk;

    private ArrayDeque<Vector3d> teleports = new ArrayDeque<>();

    public PositionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (!PacketUtil.isFlying(event.getPacketType()))
            return;

        WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);

        ++ticksSinceTeleport;

        lastInLoadedCHunk = inLoadedChunk;
        lastLastX = lastX;
        lastLastY = lastY;
        lastLastZ = lastZ;
        lastX = x;
        lastY = y;
        lastZ = z;

        if (PacketUtil.isPosition(event.getPacketType())) {
            // Do these on pos to help with 0.03 shit
            lastDeltaX = deltaX;
            lastDeltaY = deltaY;
            lastDeltaZ = deltaZ;
            lastDeltaXZ = deltaXZ;

            if (Math.abs(x) >= 3.0E7
                    || Math.abs(y) >= 3.0E7
                    || Math.abs(z) >= 3.0E7
                    || !Double.isFinite(x)
                    || !Double.isFinite(y)
                    || !Double.isFinite(z)) {
                data.notify("Invalid position, x=" + x + " y=" + y + " z=" + z);
                data.getUser().closeConnection();
                return;
            }


            x = flying.getLocation().getX();
            y = flying.getLocation().getY();
            z = flying.getLocation().getZ();

            deltaX = x - lastX;
            deltaY = y - lastY;
            deltaZ = z - lastZ;

            deltaXZ = Math.hypot(deltaX, deltaZ);

            if (!teleports.isEmpty()) {
                Vector3d oldest = teleports.getFirst();

                if (Math.abs(x - oldest.x) < 1E-8
                        && Math.abs(y - oldest.y) < 1E-8
                        && Math.abs(z - oldest.z) < 1E-8) {
                    teleports.removeFirst();

                    ticksSinceTeleport = 0;
                }
            }

            // do this on split to be more accurate bitch
            inLoadedChunk = data.getWorldProcessor().isChunkLoaded(x, z);
            // Run in pos because 0.03 is impossible in unloaded chunk
            if (!inLoadedChunk) sentMotion = false;

            // If they didn't send chunk motion we know for sure they arent in an unloaded chunk
            if (ticksSinceTeleport != 0 && Math.abs(deltaY - (-0.1 * 0.9800000190734863D)) > 1E-4)
                sentMotion = true;
        }

        if (!PacketUtil.isPosition(event.getPacketType()) && ticksSinceTeleport != 0) {
            // Only pos packets are possible in unloaded chunk
            inLoadedChunk = true;
            sentMotion = true;
        }
    }

    @Override
    public void handlePost(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            ++ticksSincePosition;

            if (PacketUtil.isPosition(event.getPacketType())) {
                ticksSincePosition = 0;
            }
        }
    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_POSITION_AND_LOOK) {
            WrapperPlayServerPlayerPositionAndLook teleport = new WrapperPlayServerPlayerPositionAndLook(event);

            data.getTransactionProcessor().confirmPre(()
                    -> teleports.add(new Vector3d(teleport.getX(), teleport.getY(), teleport.getZ())));
        }
    }
}
