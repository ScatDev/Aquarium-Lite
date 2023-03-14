package dev.scat.aquarium.listener;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.data.PlayerData;
import dev.thomazz.pledge.api.event.PacketFrameReceiveEvent;
import dev.thomazz.pledge.api.event.PacketFrameSendEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PledgeListener implements Listener {

    @EventHandler
    public void onReceive(PacketFrameReceiveEvent event) {
        PlayerData data = Aquarium.getInstance().getPlayerDataManager().get(event.getPlayer());

        data.getPledgeProcessor().handle(event);
    }

    @EventHandler
    public void onSend(PacketFrameSendEvent event) {
        PlayerData data = Aquarium.getInstance().getPlayerDataManager().get(event.getPlayer());

    }

}
