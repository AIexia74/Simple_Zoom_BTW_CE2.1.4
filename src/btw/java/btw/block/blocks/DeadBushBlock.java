// FCMOD

package btw.block.blocks;

import net.minecraft.src.*;

public class DeadBushBlock extends BlockDeadBush
{
    protected static final double WIDTH = 0.8D;
    protected static final double HALF_WIDTH = (WIDTH / 2D );
    
    public DeadBushBlock(int iBlockID)
    {
    	super( iBlockID );
    	
    	setHardness( 0F );
    	
    	setBuoyant();
    	
        initBlockBounds(
                0.5D - HALF_WIDTH, 0D, 0.5D - HALF_WIDTH,
                0.5D + HALF_WIDTH, 0.8D, 0.5D + HALF_WIDTH);
        
    	setStepSound( Block.soundGrassFootstep );
    	
    	setUnlocalizedName("deadbush");    	
    }
    
    @Override
    public boolean canSpitWebReplaceBlock(World world, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public boolean isReplaceableVegetation(World world, int i, int j, int k)
    {
    	return true;
    }
	
    @Override
    public boolean canBeGrazedOn(IBlockAccess blockAccess, int i, int j, int k,
                                 EntityAnimal animal)
    {
		return animal.canGrazeOnRoughVegetation();
    }
    
    @Override
    protected boolean canGrowOnBlock(World world, int i, int j, int k)
    {
    	return world.getBlockId( i, j, k ) == Block.sand.blockID;
    }
    
    //------------- Class Specific Methods ------------//

	//----------- Client Side Functionality -----------//    
}
    
