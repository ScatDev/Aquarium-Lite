package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.PacketUtil;
import lombok.Getter;

@Getter
public class CombatProcessor extends Processor {

    private int id = -1111, ticksSinceAttacking;

    private final static boolean[] BOOLEANS = {true, false};

    public CombatProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity useEntity = new WrapperPlayClientInteractEntity(event);

            if (useEntity.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                id = useEntity.getEntityId();
                ticksSinceAttacking = 0;
            }
        } else if (PacketUtil.isFlying(event.getPacketType())) {
            ++ticksSinceAttacking;
        }
    }
}
