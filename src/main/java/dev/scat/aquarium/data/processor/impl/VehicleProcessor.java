package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAttachEntity;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class VehicleProcessor extends Processor {

    private boolean inVehicle;
    private int vehicleId;

    public VehicleProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ATTACH_ENTITY) {
            WrapperPlayServerAttachEntity attachEntity = new WrapperPlayServerAttachEntity(event);

            if (attachEntity.getAttachedId() != data.getPlayer().getEntityId())
                return;

            data.getTransactionProcessor().confirmPre(() -> {
                vehicleId = attachEntity.getHoldingId();
                inVehicle = vehicleId != -1;
            });
        }
    }
}
