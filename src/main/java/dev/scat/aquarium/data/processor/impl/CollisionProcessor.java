package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.FrenchBlock;
import dev.scat.aquarium.util.mc.AxisAlignedBB;
import lombok.Getter;

import java.util.List;

@Getter
public class CollisionProcessor extends Processor {

      /*
     basically times with tick when x thing happened x ticks ago
     */


    private boolean slime, lastSlime,
            ice, lastIce,
            web, lastWeb,
            water, lastWater,
            lava, lastLava,
            liquid, lastLiquid,
            climbable, lastClimbable,
            stairs, lastStairs,
            slabs, lastSlabs,
            piston, lastPiston;

    private boolean clientGround, lastClientGround;
    private boolean mathGround, lastMathGround;

    public CollisionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            final WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);
            final Location location = wrapper.getLocation();
            final AxisAlignedBB boundingBox = new AxisAlignedBB(location.getX(), location.getY(), location.getZ());

            final List<FrenchBlock> blocks = boundingBox.getBlocks(data);

            this.actualize();

            slime = blocks.stream().anyMatch(block -> block.getType().toString().contains("SLIME"));
            ice = blocks.stream().anyMatch(block -> block.getType().toString().contains("ICE"));
            web = blocks.stream().anyMatch(block -> block.getType().toString().contains("WEB"));
            clientGround = wrapper.isOnGround();
            water = blocks.stream().anyMatch(block -> block.getType().name().contains("WATER"));
            lava = blocks.stream().anyMatch(block -> block.getType().name().contains("LAVA"));
            liquid = water || lava;
            climbable = blocks.stream().anyMatch(block -> block.getType().toString().contains("LADDER")
                    || block.getType().toString().contains("VINE"));
            stairs = blocks.stream().anyMatch(block -> block.getType().toString().contains("STAIR"));
            slabs = blocks.stream().anyMatch(block -> block.getType().toString().contains("SLAB"));
            piston = blocks.stream().anyMatch(block -> block.getType().toString().contains("PISTON"));
            mathGround = (location.getY() % 0.015625) <= 0.0001;

        }
    }

    private void actualize() {
        this.lastClientGround = this.clientGround;
        this.lastMathGround = this.mathGround;
        this.lastSlime = slime;
        this.lastIce = ice;
        this.lastWeb = web;
        this.lastWater = water;
        this.lastLava = lava;
        this.lastLiquid = liquid;
        this.lastClimbable = climbable;
        this.lastStairs = stairs;
        this.lastSlabs = slabs;
        this.lastPiston = piston;
    }
}
