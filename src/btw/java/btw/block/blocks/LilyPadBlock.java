// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class LilyPadBlock extends BlockLilyPad
{
    public LilyPadBlock(int iBlockID)
    {
        super(iBlockID);
        
        setBuoyant();
        
        initBlockBounds(0D, 0D, 0D, 1D, 0.015625D, 1D);
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k )
    {
    	return getBlockBoundsFromPoolBasedOnState(world, i, j, k).offset(i, j, k);
    }
    
    @Override
    protected boolean canGrowOnBlock(World world, int i, int j, int k)
    {
    	return world.getBlockId( i, j, k ) == Block.waterStill.blockID;
    }
    
    @Override
    public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k)
    {
    	return false;
    }

    @Override
    public boolean canBlocksBePlacedAgainstThisBlock(World world, int x, int y, int z) {
        return false;
    }
    
	//------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderBlockLilyPad( this, i, j, k );
    }    
}
