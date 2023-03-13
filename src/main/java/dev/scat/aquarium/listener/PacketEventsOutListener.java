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
        Player player = (Player) event.getPlayer();

        if (player == null) return;

        PlayerData data = Aquarium.getInstance().getPlayerDataManager().get(player);

        if (data == null) return;

        data.handle(event);
    }
}
