package dev.scat.aquarium.check.impl.timer;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.PacketUtil;

public class TimerA extends Check {

    private long balance = -150L, lastFlying;

    public TimerA(PlayerData data) {
        super(data, "Timer", "A", true);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            if (data.getPositionProcessor().getTicksSinceTeleport() == 0)
                return;

            long now = System.currentTimeMillis();
            long delay = now - lastFlying;

            balance += (50 - delay);

            if (balance > 50) {
                flag("b=" + balance);

                balance -= 50L;
            }

            lastFlying = now;
        }
    }
}
