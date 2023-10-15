package dev.scat.aquarium.check.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.PacketUtil;

public class BadPacketsH extends Check {

    private boolean swung;

    public BadPacketsH(PlayerData data) {
        super(data, "BadPackets", "H");
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            swung = false;
        } else if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            swung = true;
        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            if (new WrapperPlayClientInteractEntity(event).getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK
                    && !swung) {
                flag("Didn't swing on attack.");
            }
        }
    }
}
