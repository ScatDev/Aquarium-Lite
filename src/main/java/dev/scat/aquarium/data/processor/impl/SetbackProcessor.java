package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.PacketUtil;
import lombok.Getter;
import org.bukkit.Location;

@Getter
public class SetbackProcessor extends Processor {

    // TODO: fix setbacks when changing worlds

    private Location lastLegitLocation = data.getPlayer().getLocation();
    private boolean setbacked = false;

    public SetbackProcessor(PlayerData data) {
        super(data);
    }

    public void setback() {
        setbacked = true;

        data.getPlayer().teleport(lastLegitLocation);
    }

    @Override
    public void handlePost(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);

            if (!setbacked && flying.isOnGround()) {
                lastLegitLocation = data.getPlayer().getLocation();
            }

            setbacked = false;
        }
    }
}
