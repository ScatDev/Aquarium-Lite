package dev.scat.aquarium.listener;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.data.PlayerData;
import dev.thomazz.pledge.api.event.PacketFrameErrorEvent;
import dev.thomazz.pledge.api.event.PacketFrameReceiveEvent;
import dev.thomazz.pledge.api.event.PacketFrameSendEvent;
import dev.thomazz.pledge.api.event.PacketFrameTimeoutEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PledgeListener implements Listener {

    @EventHandler
    public void onReceive(PacketFrameReceiveEvent event) {
        PlayerData data = Aquarium.getInstance().getPlayerDataManager().get(event.getPlayer().getUniqueId());

        data.handle(event);
    }

    @EventHandler
    public void onFrameError(PacketFrameErrorEvent event) {
        PlayerData data = Aquarium.getInstance().getPlayerDataManager().get(event.getPlayer().getUniqueId());

        data.handle(event);
    }

    @EventHandler
    public void onFrameTimeout(PacketFrameTimeoutEvent event) {
        PlayerData data = Aquarium.getInstance().getPlayerDataManager().get(event.getPlayer().getUniqueId());

        data.handle(event);
    }
}
