// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class RopeBlock extends Block
{
	public static final double ROPE_WIDTH = (2D / 16D );
	public static final double ROPE_HALF_WIDTH = (ROPE_WIDTH / 2D );
	
	public RopeBlock(int iBlockID)
	{
        super( iBlockID, Material.circuits );

        setHardness( 0.5F );        
        setAxesEffectiveOn(true);
        
        setBuoyancy(1F);
        
    	initBlockBounds(0.5D - ROPE_HALF_WIDTH, 0D, 0.5D - ROPE_HALF_WIDTH,
						0.5D + ROPE_HALF_WIDTH, 1D, 0.5D + ROPE_HALF_WIDTH);
    	
        setStepSound( soundGrassFootstep );
        
        setUnlocalizedName( "fcBlockRope" );
    }
	
	@Override
    public int idDropped( int iMetadata, Random random, int iFortuneModifier )
    {
        return BTWItems.rope.itemID;
    }
    
	@Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int iBlockID )
    {
    	int iBlockAboveID = world.getBlockId( i, j + 1, k );
    	
    	boolean bSupported = true;
    	
    	if ( iBlockAboveID == BTWBlocks.anchor.blockID )
    	{
    		int iFacing = ((AnchorBlock) BTWBlocks.anchor).getFacing(world, i, j + 1, k);
    		
    		if ( iFacing == 1 )
    		{
    			// upwards facing anchors can't support rope below
    			
    			bSupported = false;
    		}
    	}
    	else if ( iBlockAboveID != blockID &&
			iBlockAboveID != BTWBlocks.pulley.blockID )
    	{
    		bSupported = false;
    	}
    	
    	if ( !bSupported )
    	{
    		// the block above is no longer a rope, valid anchor, or pulley.  Self Destruct.
    		
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            
            world.setBlockWithNotify( i, j, k, 0 );
    	}
    }
    
	@Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
    	int iBlockAboveID = world.getBlockId( i, j + 1, k );
    	
    	if ( iBlockAboveID == blockID ||
    			iBlockAboveID == BTWBlocks.anchor.blockID )
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }    
    
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

	@Override
	public boolean isBlockClimbable(World world, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public boolean hasSmallCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k,
												   int iFacing, boolean bIgnoreTransparency )
	{
		// so lightning rods can attach to the bottom of rope for chandeliers and such
		
		return iFacing == 0;
	}
	
    //------------- Class Specific Methods ------------//
    
    public void breakRope(World world, int i, int j, int k)
    {
		ItemUtils.ejectSingleItemWithRandomOffset(world, i, j, k,
                                                  BTWItems.rope.itemID, 0);
		
        world.playAuxSFX( 2001, i, j, k, blockID );
        
		world.setBlockWithNotify( i, j, k, 0 );		
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return idDropped( world.getBlockMetadata( i, j, k ), world.rand, 0 );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
    	if ( iSide < 2 )
    	{
    		int iNeighborBlockID = blockAccess.getBlockId( iNeighborI, iNeighborJ, iNeighborK );
    		
    		return iNeighborBlockID != blockID && super.shouldSideBeRendered( blockAccess, 
    			iNeighborI, iNeighborJ, iNeighborK, iSide ); 
		}
    	
        return true;
    }
}