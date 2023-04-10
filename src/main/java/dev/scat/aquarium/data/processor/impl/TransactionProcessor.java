package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPong;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.thomazz.pledge.api.PacketFrame;
import dev.thomazz.pledge.api.event.PacketFrameReceiveEvent;
import dev.thomazz.pledge.api.event.ReceiveType;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TransactionProcessor extends Processor {

    private final Multimap<Integer, Runnable> pledgeTasks = ArrayListMultimap.create();
    private final Map<Short, Runnable> transactionTasks = new HashMap<>();
    private short transactionId = -1000;

    public TransactionProcessor(PlayerData data) {
        super(data);
    }

    public void handlePre(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            WrapperPlayClientWindowConfirmation transaction =
                    new WrapperPlayClientWindowConfirmation(event);
            
            Runnable runnable = transactionTasks.remove(transaction.getActionId());

            if (runnable != null) runnable.run();
        } else if (event.getPacketType() == PacketType.Play.Client.PONG) {
            WrapperPlayClientPong pong = new WrapperPlayClientPong(event);

            if (Math.abs(pong.getId()) > Short.MAX_VALUE && pong.getId() < 0) {
                Runnable runnable = transactionTasks.remove((short) pong.getId());

                if (runnable != null) runnable.run();
            }
        }
    }

    public void handle(PacketFrameReceiveEvent event) {
        if (event.getType() == ReceiveType.RECEIVE_START) {
            int id = event.getFrame().getId1();

            if (pledgeTasks.containsKey(id)) {
                for (Runnable runnable : pledgeTasks.removeAll(id)) {
                    runnable.run();
                }
            }
        } else {
            int id = event.getFrame().getId2();

            if (pledgeTasks.containsKey(id)) {
                for (Runnable runnable : pledgeTasks.removeAll(id)) {
                    runnable.run();
                }
            }
        }
    }

    public void confirmPre(Runnable runnable) {
        PacketFrame frame = Aquarium.getInstance().getPledge().getOrCreateFrame(data.getPlayer());

        pledgeTasks.put(frame.getId1(), runnable);
    }

    public void confirmPost(Runnable runnable) {
        PacketFrame frame = Aquarium.getInstance().getPledge().getOrCreateFrame(data.getPlayer());

        pledgeTasks.put(frame.getId2(), runnable);
    }

    public void sendTransaction(Runnable runnable) {
        PacketWrapper<?> packet;

        if (data.getVersion().isNewerThanOrEquals(ClientVersion.V_1_17)) {
            packet = new WrapperPlayServerPing(transactionId);
        } else {
            packet = new WrapperPlayServerWindowConfirmation(0, transactionId, false);
        }
        
        PacketEvents.getAPI().getPlayerManager().sendPacket(data.getPlayer(), packet);

        transactionTasks.put(transactionId, runnable);

        transactionId--;
        
        if (transactionId < -2000) transactionId = -1000;
    }
}
