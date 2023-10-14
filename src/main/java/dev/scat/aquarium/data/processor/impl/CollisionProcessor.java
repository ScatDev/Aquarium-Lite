package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.BlockUtil;
import dev.scat.aquarium.util.PacketUtil;
import dev.scat.aquarium.util.PlayerUtil;
import dev.scat.aquarium.util.collision.WrappedBlock;
import dev.scat.aquarium.util.mc.AxisAlignedBB;
import dev.scat.aquarium.util.mc.MathHelper;
import lombok.Getter;
import org.bukkit.util.NumberConversions;

import java.util.List;

@Getter
public class CollisionProcessor extends Processor {

    // Only calculates half blocks like slabs and stairs when standing on the half,
    // For example if you are standing on the top of the stairs where youre one whole block
    // Above the bottom of the stair it wont count you as on stairs

    private boolean onSlime, lastOnSlime,
            onIce, lastOnIce,
            inWeb, lastInWeb,
            inWater, lastInWater, lastLastInWater,
            inLava, lastInLava, lastLastInLava,
            onClimbable, lastOnClimbable, lastLastOnClimbable,
            onStairs, lastOnStairs,
            onSlabs, lastOnSlabs,
            onSoulSand, lastOnSoulSand,
            bonking, lastBonking, lastLastBonking,
            onHoney, lastOnHoney,
            nearWall, lastNearWall,
            nearPiston, lastNearPiston,
            clientGround, lastClientGround, lastLastClientGround,
            serverGround, lastServerGround;

    private List<WrappedBlock> blocks;

    private float friction, lastFriction, lastLastFriction;

    private AxisAlignedBB bb, lastBB, lastLastBB;

    private int clientGroundTicks, clientAirTicks;

    public CollisionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);

            actualize();

            clientGround = flying.isOnGround();

            clientGroundTicks = clientGround ? clientGroundTicks + 1 : 0;
            clientAirTicks = clientGround ? 0 : clientAirTicks + 1;

            if (PacketUtil.isPosition(event.getPacketType())) {
                friction = BlockUtil.getFriction(data.getWorldProcessor().getBlock(
                        MathHelper.floor_double(data.getPositionProcessor().getX()),
                        MathHelper.floor_double(data.getPositionProcessor().getY()) - 1,
                        MathHelper.floor_double(data.getPositionProcessor().getZ())));

                bb = PlayerUtil.getBoundingBox(data.getPositionProcessor().getX(),
                        data.getPositionProcessor().getY(), data.getPositionProcessor().getZ()).expand(0.001, 0.001, 0.001);

                int floorX = NumberConversions.floor(data.getPositionProcessor().getX());
                int floorY = NumberConversions.floor(data.getPositionProcessor().getY());
                int floorZ = NumberConversions.floor(data.getPositionProcessor().getZ());
                int floorYHead = MathHelper.floor_double(data.getPositionProcessor().getY() + 1.8F);

                // fuck it we using bad values cause thats all we can
                blocks = bb.getBlocks(data);

                bonking = blocks.stream().anyMatch(block -> block.getY() >= floorYHead
                        && block.getCollisionBox().isCollided(bb.expand(-0.001, 0, -0.001)));
                onSlime = blocks.stream().anyMatch(block -> block.getBlock().getType() == StateTypes.SLIME_BLOCK
                        && block.getX() == floorX && block.getY() == floorY - 1 && block.getZ() == floorZ);
                onSoulSand = blocks.stream().anyMatch(block -> block.getBlock().getType() == StateTypes.SOUL_SAND
                        && block.getX() == floorX && block.getY() == floorY && block.getZ() == floorZ);
                onSlabs = blocks.stream().anyMatch(block -> BlockTags.SLABS.contains(block.getBlock().getType())
                        && block.getY() == floorY);
                onIce = blocks.stream().anyMatch(block -> BlockTags.ICE.contains(block.getBlock().getType())
                        && block.getX() == floorX && block.getY() == floorY - 1 && block.getZ() == floorZ);
                onStairs = blocks.stream().anyMatch(block -> BlockTags.STAIRS.contains(block.getBlock().getType())
                        && block.getY() == floorY);
                inWeb = blocks.stream().anyMatch(block -> block.getBlock().getType() == StateTypes.COBWEB);
                onHoney = blocks.stream().anyMatch(block -> block.getBlock().getType() == StateTypes.HONEY_BLOCK
                        && block.getX() == floorX && block.getY() == floorY - 1 && block.getZ() == floorZ);

                nearWall = blocks.stream().anyMatch(block
                        -> block.getCollisionBox().isCollided(bb.expand(0, -0.001, 0)));

                nearPiston = blocks.stream().anyMatch(block ->
                        block.getBlock().getType() == StateTypes.PISTON ||
                                block.getBlock().getType() == StateTypes.PISTON_HEAD ||
                                block.getBlock().getType() == StateTypes.MOVING_PISTON ||
                                block.getBlock().getType() == StateTypes.STICKY_PISTON) ||
                        blocks.stream().anyMatch(block
                                -> block.getBlock().getType() == StateTypes.PISTON ||
                                block.getBlock().getType() == StateTypes.PISTON_HEAD ||
                                block.getBlock().getType() == StateTypes.MOVING_PISTON ||
                                block.getBlock().getType() == StateTypes.STICKY_PISTON);

                onClimbable = BlockTags.CLIMBABLE.contains(data.getWorldProcessor()
                        .getBlock(floorX, floorY, floorZ).getType());

                inWater = PlayerUtil.isInWater(data);

                inLava = PlayerUtil.isInLava(data);

                if (lastBB != null) {
                    // modulo isn't always accurate due to 0.03 but we use it to fix stepping having false
                    List<WrappedBlock> expandedBlocks = lastBB.clone().addCoord(
                                    data.getPositionProcessor().getDeltaX(),
                                    data.getPositionProcessor().getDeltaY()
                                    , data.getPositionProcessor().getDeltaZ()
                            ).expand(0.001, 0.001, 0.001)
                            .getBlocks(data);

                    serverGround = data.getPositionProcessor().getY() % 0.015625 == 0
                            && expandedBlocks.stream().anyMatch(block -> block.getY() <= floorY
                            && block.getBlock().getType().isSolid());
                } else {
                    serverGround = true;
                }
            }
        }
    }


    private void actualize() {
        lastOnSlime = onSlime;
        lastOnIce = onIce;
        lastInWeb = inWeb;
        lastLastInWater = lastInWater;
        lastInWater = inWater;
        lastLastInLava = lastInLava;
        lastInLava = inLava;
        lastLastOnClimbable = lastOnClimbable;
        lastOnClimbable = onClimbable;
        lastOnStairs = onStairs;
        lastOnSlabs = onSlabs;
        lastOnSoulSand = onSoulSand;
        lastLastBonking = lastBonking;
        lastBonking = bonking;
        lastOnHoney = onHoney;
        lastNearWall = nearWall;
        lastNearPiston = nearPiston;
        lastLastFriction = lastFriction;
        lastFriction = friction;
        lastLastBB = lastBB;
        lastBB = bb;
        lastLastClientGround = lastClientGround;
        lastClientGround = clientGround;
        lastServerGround = serverGround;
    }
}
