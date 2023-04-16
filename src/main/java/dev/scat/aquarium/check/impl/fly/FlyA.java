package dev.scat.aquarium.check.impl.fly;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.potion.PotionTypes;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.impl.AbilitiesProcessor;
import dev.scat.aquarium.data.processor.impl.CollisionProcessor;
import dev.scat.aquarium.data.processor.impl.PositionProcessor;
import dev.scat.aquarium.util.PacketUtil;
import org.bukkit.GameMode;

public class FlyA extends Check {

    public FlyA(PlayerData data) {
        super(data, "Fly", "A", 3);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (!PacketUtil.isFlying(event.getPacketType())) return;

        final CollisionProcessor collisionProcessor = data.getCollisionProcessor();
        final AbilitiesProcessor abilitiesProcessor = data.getAbilitiesProcessor();
        final PositionProcessor positionProcessor = data.getPositionProcessor();

        final int airTicksLimit = 7 + (data.getPotionProcessor().getAmplifier(PotionTypes.JUMP_BOOST) * 2);

        // exempt when the vertical movement could be altered
        if (collisionProcessor.isInLava() ||
                collisionProcessor.isInWater() ||
                collisionProcessor.isInWeb() ||
                collisionProcessor.isOnClimbable() ||
                collisionProcessor.getClientAirTicks() < airTicksLimit ||
                abilitiesProcessor.getAbilities().isCreativeMode() ||
                data.getPlayer().getGameMode() == GameMode.SPECTATOR ||
                abilitiesProcessor.getAbilities().isFlightAllowed() ||
                abilitiesProcessor.getAbilities().isFlying()) {

            buffer.reduce(0.25);
            return;
        }

        // get the vertical ascension
        final double ascension = positionProcessor.getDeltaY() - positionProcessor.getLastDeltaY();
        // get the vertical acceleration
        final double acceleration = Math.abs(ascension);

        if (acceleration < 1E-3 || ascension > 0.03) {
            if(buffer.fail()) {
                buffer.reset();
                flag("ASC=" + ascension + " ACL=" + acceleration);
            }
        } else buffer.reduce(0.1);


    }
}
