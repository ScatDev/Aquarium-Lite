package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.MathUtil;
import dev.scat.aquarium.util.PacketUtil;
import dev.scat.aquarium.util.RotationUtil;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
public class RotationProcessor extends Processor {

    private float yaw, pitch, lastYaw, lastPitch, deltaYaw,
            deltaPitch, lastDeltaYaw, lastDeltaPitch, calcSensitivity,
            accelPitch, accelYaw;
    private float gcdYaw, gcdPitch, absGcdYaw, absGcdPitch;
    private final Set<Integer> candidates = new HashSet<>();
    private int sensitivity, lastSensitivity, cinematicTicks;

    public RotationProcessor(PlayerData data) {
        super(data);
    }

    public void handlePre(PacketReceiveEvent event) {
        if (!PacketUtil.isFlying(event.getPacketType())) return;
        WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);

        lastYaw = yaw;
        lastPitch = pitch;
        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;

        if (flying.hasRotationChanged()) {
            yaw = flying.getLocation().getYaw();
            pitch = flying.getLocation().getPitch();

            if (Math.abs(yaw) >= 3.0E7
                    || Math.abs(pitch) >= 3.0E7
                    || !Float.isFinite(yaw)
                    || !Float.isFinite(pitch)) {
                event.setCancelled(true);

                data.notify("Large rotation, yaw=" + yaw + " pitch=" + pitch);
                data.getUser().closeConnection();
            }

            deltaYaw = MathUtil.angleDistance(yaw, lastYaw);
            deltaPitch = Math.abs(pitch - lastPitch);

            accelPitch = Math.abs(deltaPitch - lastPitch);
            accelYaw = Math.abs(deltaYaw - lastDeltaYaw);

            processDivisors();
            processSensitivity();
            handleCinematic();
        }

    }

    private void processDivisors() {
        long expandedYaw = (long) (deltaYaw * MathUtil.EXPANDER);
        long previousExpandedYaw = (long) (lastDeltaYaw * MathUtil.EXPANDER);
        gcdYaw = (float) (MathUtil.getGcd(expandedYaw, previousExpandedYaw) / MathUtil.EXPANDER);

        long expandedPitch = (long) (deltaPitch * MathUtil.EXPANDER);
        long previousExpandedPitch = (long) (lastDeltaPitch * MathUtil.EXPANDER);
        gcdPitch = (float) (MathUtil.getGcd(expandedPitch, previousExpandedPitch) / MathUtil.EXPANDER);

        absGcdYaw = MathUtil.getAbsoluteGcd(Math.abs(deltaYaw), Math.abs(lastDeltaYaw));
        absGcdPitch = MathUtil.getAbsoluteGcd(Math.abs(deltaPitch), Math.abs(lastDeltaPitch));
    }

    private void processSensitivity() {
        if (Math.abs(pitch) == 90.0f || (yaw == lastYaw && pitch == lastPitch))
            return;

        float distanceY = pitch - lastPitch;
        double errorY = Math.max(Math.abs(pitch), Math.abs(lastPitch)) * 3.814697265625E-6;
        computeSensitivity(distanceY, errorY);

        float distanceX = circularDistance(yaw, lastYaw);
        double errorX = Math.max(Math.abs(yaw), Math.abs(lastYaw)) * 3.814697265625E-6;
        computeSensitivity(distanceX, errorX);


        if (candidates.size() == 1) {
            calcSensitivity = candidates.iterator().next();
            lastSensitivity = sensitivity;
            sensitivity = (int) (200 * calcSensitivity / 143);
        } else {
            lastSensitivity = sensitivity;
            sensitivity = -1;
            forEach(candidates::add);
        }
    }

    public void computeSensitivity(double delta, double error) {
        if (delta < error) return;

        double start = delta - error;
        double end = delta + error;

        forEach(s -> {
            double f0 = ((double) s / 142.0) * 0.6 + 0.2;
            double f = (f0 * f0 * f0 * 8.0) * 0.15;
            int pStart = (int) Math.ceil(start / f);
            int pEnd = (int) Math.floor(end / f);

            if (pStart <= pEnd) {
                for (int p = pStart; p <= pEnd; p++) {
                    double d = p * f;
                    if (d < start || d > end) {
                        candidates.remove(s);
                    }
                }
            } else {
                candidates.remove(s);
            }
        });
    }

    public float circularDistance(float a, float b) {
        float d = Math.abs(a % 360.0f - b % 360.0f);
        return d < 180.0f ? d : 360.0f - d;
    }

    public void forEach(Consumer<Integer> consumer) {
        for (int s = 0; s < 143; s++) {
            consumer.accept(s);
        }
    }

    private void handleCinematic() {
        if (sensitivity > 0) {
            cinematicTicks = cinematicTicks <= 0 ? 0 : cinematicTicks / 2;
            return;
        }
        if (deltaPitch < 0.33 && deltaPitch > 0)
            cinematicTicks++;
        else if (pitch < 0.92 && pitch > 0)
            cinematicTicks++;
        else {
            cinematicTicks = cinematicTicks <= 0 ? 0 : cinematicTicks / 2;
        }
    }

    public float getSensitivityFromGcd(float gcd) {
        float closest = 1, lowestDiff = Float.MAX_VALUE;

        for (float sensitivity : RotationUtil.SENSITIVITIES) {
            float diff = Math.abs((sensitivity * 0.15F) - gcd);

            if (diff < lowestDiff) {
                closest = sensitivity;
                lowestDiff = diff;
            } else if (diff > lowestDiff) {
                // We are going past the closest sensitivity
                break;
            }
        }

        return closest;
    }
}