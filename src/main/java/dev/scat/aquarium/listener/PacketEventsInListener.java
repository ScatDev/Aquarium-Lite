package dev.scat.aquarium.listener;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.handshaking.client.WrapperHandshakingClientHandshake;
import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketEventsInListener extends PacketListenerAbstract {

    public PacketEventsInListener() {
        super(PacketListenerPriority.HIGHEST);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPlayer() == null)
            return;

        PlayerData data = Aquarium.getInstance().getPlayerDataManager().get(event.getUser().getUUID());

        if (data == null)
            return;

        data.handle(event);
    }
}
