package dev.scat.aquarium.data.processor;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import dev.scat.aquarium.data.PlayerData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Processor {

    protected final PlayerData data;

    public void handlePre(PacketReceiveEvent event) {}
    public void handlePre(PacketSendEvent event) {}
    public void handlePost(PacketReceiveEvent event) {}
    public void handlePost(PacketSendEvent event) {}
}
