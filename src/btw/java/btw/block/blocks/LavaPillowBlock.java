// FCMOD

package btw.block.blocks;

import btw.client.fx.BTWEffectManager;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

import java.util.Random;

public class LavaPillowBlock extends FullBlock {
    public LavaPillowBlock(int blockID) {
        super(blockID, Material.rock);

        setHardness(0.8F);

        setPicksEffectiveOn();
        setChiselsEffectiveOn();

        setStepSound(soundGlassFootstep);

        setUnlocalizedName("fcBlockLavaPillow");
    }

    @Override
    public int idDropped(int metadata, Random random, int fortuneModifier) {
        return 0;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        super.harvestBlock(world, player, x, y, z, metadata);
        setBlockToLava(world, x, y, z);
    }

    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        super.onBlockDestroyedWithImproperTool(world, player, x, y, z, metadata);
        setBlockToLava(world, x, y, z);
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public void postBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        super.postBlockDestroyedByExplosion(world, x, y, z, explosion);
        setBlockToLava(world, x, y, z);
    }

    @Override
    public boolean isBlockDestroyedByBlockDispenser(int iMetadata) {
        return true;
    }
    
    public void onRemovedByBlockDispenser(World world, int x, int y, int z) {
        super.onRemovedByBlockDispenser(world, x, y, z);
        setBlockToLava(world, x, y, z);
    }

    //------------- Class Specific Methods ------------//

    public void setBlockToLava(World world, int x, int y, int z) {
        if (world.isAirBlock(x, y, z)) {
            world.playAuxSFX(BTWEffectManager.FIRE_FIZZ_EFFECT_ID, x, y, z, 0);

            if (!hasWaterToSidesOrTop(world, x, y, z)) {
                int decayLevel = 1;

                world.setBlockAndMetadataWithNotify(x, y, z, Block.lavaMoving.blockID, decayLevel);

                decayLevel++;

                for (int facing = 2; facing <= 5; facing++) {
                    BlockPos pos = new BlockPos(x, y, z, facing);

                    if (world.isAirBlock(pos.x, pos.y, pos.z)) {
                        world.setBlockAndMetadataWithNotify(pos.x, pos.y, pos.z, Block.lavaMoving.blockID, decayLevel);
                    }
                }
            }
        }
    }

    //----------- Client Side Functionality -----------//
}