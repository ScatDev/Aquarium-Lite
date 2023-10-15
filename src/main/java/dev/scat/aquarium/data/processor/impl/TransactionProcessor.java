package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPong;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.check.impl.badpackets.BadPacketsA;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.PacketUtil;
import dev.thomazz.pledge.api.PacketFrame;
import dev.thomazz.pledge.api.event.PacketFrameErrorEvent;
import dev.thomazz.pledge.api.event.PacketFrameReceiveEvent;
import dev.thomazz.pledge.api.event.PacketFrameTimeoutEvent;
import dev.thomazz.pledge.api.event.ReceiveType;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TransactionProcessor extends Processor {

    private final Multimap<Integer, Runnable> pledgeTasks = ArrayListMultimap.create();
    private final Map<Short, Runnable> transactionTasks = new HashMap<>();
    private short transactionId = -17000;
    private boolean responded;

    private Check badPacketsA;

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

            if (Math.abs(pong.getId()) > Short.MIN_VALUE && pong.getId() < 0) {
                Runnable runnable = transactionTasks.remove((short) pong.getId());

                if (runnable != null) runnable.run();
            }
        } else if (PacketUtil.isFlying(event.getPacketType())) {
            responded = false;
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

        responded = true;
    }

    public void handle(PacketFrameErrorEvent event) {
        if (badPacketsA == null) {
            badPacketsA = data.getChecks().stream().filter(check -> check.getClass() == BadPacketsA.class).findFirst().get();
        }

        badPacketsA.flag(event.getType().name());
    }

    public void handle(PacketFrameTimeoutEvent event) {
        data.notify("Timed out");

        data.getUser().closeConnection();
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

        // add before send cause on localhost it will respond before the task is added lmao
        transactionTasks.put(transactionId, runnable);

        // TODO: fix possible tranny rape
        PacketEvents.getAPI().getPlayerManager().sendPacket(data.getPlayer(), packet);

        transactionId--;

        if (transactionId < -18000)
            transactionId = -17000;
    }
}
