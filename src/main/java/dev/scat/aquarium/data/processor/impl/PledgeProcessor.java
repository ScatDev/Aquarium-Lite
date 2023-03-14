package dev.scat.aquarium.data.processor.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.thomazz.pledge.PledgeImpl;
import dev.thomazz.pledge.api.PacketFrame;
import dev.thomazz.pledge.api.Pledge;
import dev.thomazz.pledge.api.event.PacketFrameReceiveEvent;
import dev.thomazz.pledge.api.event.ReceiveType;
import lombok.Getter;

@Getter
public class PledgeProcessor extends Processor {

    private final Multimap<Integer, Runnable> transactionTasks = ArrayListMultimap.create();

    public PledgeProcessor(PlayerData data) {
        super(data);
    }

    public void handle(PacketFrameReceiveEvent event) {
        if (event.getType() == ReceiveType.RECEIVE_START) {
            int id = event.getFrame().getId1();

            if (transactionTasks.containsKey(id)) {
                for (Runnable runnable : transactionTasks.removeAll(id)) {
                    runnable.run();
                }
            }
        } else {
            int id = event.getFrame().getId2();

            if (transactionTasks.containsKey(id)) {
                for (Runnable runnable : transactionTasks.removeAll(id)) {
                    runnable.run();
                }
            }
        }
    }

    public void confirmPre(Runnable runnable) {
        PacketFrame frame = Aquarium.getInstance().getPledge().getOrCreateFrame(data.getPlayer());

        transactionTasks.put(frame.getId1(), runnable);
    }

    public void confirmPost(Runnable runnable) {
        PacketFrame frame = Aquarium.getInstance().getPledge().getOrCreateFrame(data.getPlayer());

        transactionTasks.put(frame.getId2(), runnable);
    }
}
