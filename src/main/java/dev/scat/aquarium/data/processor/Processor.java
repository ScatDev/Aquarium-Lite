package dev.scat.aquarium.data.processor;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import dev.scat.aquarium.data.PlayerData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Processor {

    private final PlayerData data;

    public void handle(PacketReceiveEvent event) {}
    public void handle(PacketSendEvent event) {}
}
