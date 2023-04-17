package dev.scat.aquarium.listener;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.data.PlayerData;
import org.bukkit.entity.Player;

public class PacketEventsOutListener extends PacketListenerAbstract {

    public PacketEventsOutListener() {
        super(PacketListenerPriority.LOWEST);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        PlayerData data = Aquarium.getInstance().getPlayerDataManager().get(event.getUser().getUUID());

        if (data == null) return;

        data.handle(event);
    }
}
