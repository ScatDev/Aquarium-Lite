package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.PacketUtil;
import dev.scat.aquarium.util.PlayerUtil;
import dev.scat.aquarium.util.collision.WrappedBlock;
import dev.scat.aquarium.util.mc.AxisAlignedBB;
import lombok.Getter;
import org.bukkit.util.NumberConversions;

import java.util.List;

@Getter
public class CollisionProcessor extends Processor {

    private boolean onSlime, lastOnSlime,
            onIce, lastOnIce,
            inWeb, lastInWeb,
            inWater, lastInWater,
            inLava, lastInLava,
            onClimbable, lastOnClimbable,
            onStairs, lastOnStairs,
            onSlabs, lastOnSlabs,
            onSoulSand, lastOnSoulSand,
            bonking, lastBonking,
            sideHoney, lastSideHoney,
            onHoney, lastOnHoney,
            nearWall, lastNearWall,
            clientGround, lastClientGround;

    private AxisAlignedBB sideBB, verticalBB;
    private int clientAirTicks, clientGroundTicks;

    public CollisionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            actualize();

            if (PacketUtil.isPosition(event.getPacketType())) {
                sideBB = PlayerUtil.getBoundingBox(data.getPositionProcessor().getX(),
                        data.getPositionProcessor().getY(), data.getPositionProcessor().getZ()).expand(0.001, 0, 0.001);

                verticalBB = PlayerUtil.getBoundingBox(data.getPositionProcessor().getX(),
                        data.getPositionProcessor().getY(), data.getPositionProcessor().getZ()).expand(0, 0.001, 0);

                int floorX = NumberConversions.floor(data.getPositionProcessor().getX());
                int floorY = NumberConversions.floor(data.getPositionProcessor().getY());
                int floorZ = NumberConversions.floor(data.getPositionProcessor().getZ());
                int floorYHead = NumberConversions.floor(data.getPositionProcessor().getY() + 1.8F);

                List<WrappedBlock> sideBlocks = sideBB.getBlocks(data);
                List<WrappedBlock> verticalBlocks = verticalBB.getBlocks(data);

                bonking = verticalBlocks.stream().anyMatch(block -> block.getY() > floorY);
                onSlime = verticalBlocks.stream().anyMatch(block -> block.getType() == StateTypes.SLIME_BLOCK
                        && block.getX() == floorX && block.getY() == floorY - 1 && block.getZ() == floorZ);
                onSoulSand = verticalBlocks.stream().anyMatch(block -> block.getType() == StateTypes.SOUL_SAND
                        && block.getX() == floorX && block.getY() == floorY && block.getZ() == floorZ);
                onSlabs = verticalBlocks.stream().anyMatch(block -> BlockTags.SLABS.contains(block.getType())
                        && block.getY() == floorY);
                onIce = verticalBlocks.stream().anyMatch(block -> BlockTags.ICE.contains(block.getType())
                        && block.getX() == floorX && block.getY() == floorY - 1 && block.getZ() == floorZ);
                onStairs = verticalBlocks.stream().anyMatch(block -> BlockTags.SLABS.contains(block.getType())
                        && block.getY() == floorY);
                inWeb = verticalBlocks.stream().anyMatch(block -> block.getType() == StateTypes.COBWEB
                        && block.getY() >= floorY && block.getY() <= floorYHead);

                // TODO: make sure honey is 1 block tall and is not for all bb
                onHoney = verticalBlocks.stream().anyMatch(block -> block.getType() == StateTypes.HONEY_BLOCK
                        && block.getX() == floorX && block.getY() == floorY - 1 && block.getZ() == floorZ);

                nearWall = !sideBlocks.isEmpty();
                sideHoney = sideBlocks.stream().anyMatch(block -> block.getType() == StateTypes.HONEY_BLOCK);

                onClimbable = BlockTags.CLIMBABLE.contains(data.getWorldProcessor()
                        .getBlock(floorX, floorY, floorZ).getType());

                inWater = PlayerUtil.isInWater(data);
                inLava = PlayerUtil.isInLava(data);


            }

            final WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);

            clientGround = wrapper.isOnGround();

            clientGroundTicks = clientGround ? clientGroundTicks + 1 : 0;
            clientAirTicks = clientGround ? 0 : clientAirTicks + 1;
        }
    }

    private void actualize() {
        lastClientGround = clientGround;
        lastOnSlime = onSlime;
        lastOnIce = onIce;
        lastInWeb = inWeb;
        lastInWater = inWater;
        lastInLava = inLava;
        lastOnClimbable = onClimbable;
        lastOnStairs = onStairs;
        lastOnSlabs = onSlabs;
        lastOnSoulSand = onSoulSand;
        lastBonking = bonking;
        lastSideHoney = sideHoney;
        lastOnHoney = onHoney;
        lastNearWall = nearWall;
    }
}
