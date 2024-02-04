// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.EntityFallingSand;
import net.minecraft.src.World;

import java.util.Random;

public class InfiniteBurningTorchBlock extends TorchBlockBurningBase
{
	private boolean startsFires;
	
    public InfiniteBurningTorchBlock(int iBlockID, boolean startsFires)
    {
    	super( iBlockID );
    	
    	setLightValue( 0.9375F );
    	
    	setUnlocalizedName( "fcBlockTorchNetherBurning" );
    	
    	setTickRandomly( true );
		
		this.startsFires = startsFires;
    }
	
	@Override
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return BTWBlocks.infiniteBurningTorch.blockID;
	}
	
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand )
    {
        super.updateTick( world, x, y, z, rand );
        
		// last param provides increased chance of fire spread, over default of 100
		if (this.startsFires) {
			if (world.getDifficulty().shouldNetherCoalTorchesStartFires()) {
				FireBlock.checkForFireSpreadAndDestructionToOneBlockLocation(world, x, y + 1, z, rand, 0, 25);
			}
			else {
				world.setBlockAndMetadata(x, y, z, Block.torchWood.blockID, world.getBlockMetadata(x, y, z));
			}
		}
    }
    
	@Override
    public boolean canBeCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
    	return true;
    }
    
    //------------- Class Specific Methods ------------//

	//----------- Client Side Functionality -----------//
}
