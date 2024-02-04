// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class WeedsBlock extends PlantsBlock
{
	static public final double WEEDS_BOUNDS_WIDTH = (1D - (4D / 16D ) );
	static public final double WEEDS_BOUNDS_HALF_WIDTH = (WEEDS_BOUNDS_WIDTH / 2D );
	
    public WeedsBlock(int iBlockID)
    {
        super( iBlockID, Material.plants );
        
        setHardness( 0F );
        setBuoyant();
		setFireProperties(Flammability.CROPS);
        
        initBlockBounds(0.5D - WEEDS_BOUNDS_WIDTH, 0D, 0.5D - WEEDS_BOUNDS_WIDTH,
                        0.5D + WEEDS_BOUNDS_WIDTH, 0.5D, 0.5D + WEEDS_BOUNDS_WIDTH);
        
        setStepSound( soundGrassFootstep );
        
        setTickRandomly( true );
        
        disableStats();
    }
    
    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return -1;
    }

	@Override
    public void breakBlock(World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
		// override to prevent parent from disrupting soil as it felt weird
		// relative to being able to clear weeds around plants without disruption
    }
	
    @Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
        super.onNeighborBlockChange( world, i, j, k, iNeighborBlockID );
        
        if ( !canBlockStay( world, i, j, k ) )
        {
            world.setBlockToAir( i, j, k );
        }
    }
    
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
            IBlockAccess blockAccess, int i, int j, int k)
    {
        float dVerticalOffset = 0;
        Block blockBelow = Block.blocksList[blockAccess.getBlockId( i, j - 1, k )];
        
        if ( blockBelow != null )
        {
        	dVerticalOffset = blockBelow.groundCoverRestingOnVisualOffset(
        		blockAccess, i, j - 1, k);
        }
        
    	int iGrowthLevel = getWeedsGrowthLevel(blockAccess, i, j, k);
    	
		double dBoundsHeight = getWeedsBoundsHeight(iGrowthLevel);
    	
    	return AxisAlignedBB.getAABBPool().getAABB(
                0.5D - WEEDS_BOUNDS_HALF_WIDTH, 0F + dVerticalOffset,
                0.5D - WEEDS_BOUNDS_HALF_WIDTH,
                0.5D + WEEDS_BOUNDS_HALF_WIDTH, dBoundsHeight + dVerticalOffset,
                0.5D + WEEDS_BOUNDS_HALF_WIDTH);
    }
    
	@Override
	public void removeWeeds(World world, int i, int j, int k)
	{
		Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
		
		if ( blockBelow != null )
		{
			blockBelow.removeWeeds(world, i, j - 1, k);
		}
		
		world.setBlockToAir( i, j, k );
	}
    
	@Override
	public boolean canWeedsGrowInBlock(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
    protected boolean canGrowOnBlock(World world, int i, int j, int k)
    {
		int iBlockOnID = world.getBlockId( i, j, k );
		
		return world.getBlockId( i, j, k ) == BTWBlocks.farmland.blockID ||
			world.getBlockId( i, j, k ) == BTWBlocks.fertilizedFarmland.blockID;
    }
    
    //------------- Class Specific Methods ------------//
    
    public static double getWeedsBoundsHeight(int iGrowthLevel)
    {
    	return ( ( iGrowthLevel >> 1 ) + 1 ) / 8D; 
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] weedIconArray = null;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        weedIconArray = new Icon[8];

        weedIconArray[0] = weedIconArray[1] = register.registerIcon("fcBlockWeeds_0");
        weedIconArray[2] = weedIconArray[3] = register.registerIcon("fcBlockWeeds_1");
        weedIconArray[4] = weedIconArray[5] = register.registerIcon("fcBlockWeeds_2");
        weedIconArray[6] = weedIconArray[7] = register.registerIcon("fcBlockWeeds_3");
    	
        blockIcon = weedIconArray[7]; // for block hit effects and item render
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
		renderWeeds(this, renderer, i, j, k);
		
		return true;
    }
    
    /**
     * Note that this will be called by different block types that can support weeds,
     * like the various forms of crops, and thus the block parameter is not necessarily of type
     * FCBlockWeeds
     */
    @Environment(EnvType.CLIENT)
    public void renderWeeds(Block block, RenderBlocks renderer, int i, int j, int k)
    {
		int iGrowthLevel = block.getWeedsGrowthLevel(renderer.blockAccess, i, j, k);
		
		if ( iGrowthLevel > 0 )
		{
	        double dVerticalOffset = 0;
	        Block blockBelow = Block.blocksList[renderer.blockAccess.getBlockId( i, j - 1, k )];
	        
	        if ( blockBelow != null )
	        {
	        	dVerticalOffset = blockBelow.groundCoverRestingOnVisualOffset(
	        		renderer.blockAccess, i, j - 1, k);
	        }
	        
	        Tessellator tessellator = Tessellator.instance;
	        
	        tessellator.setBrightness( block.getMixedBrightnessForBlock( renderer.blockAccess, 
	        	i, j, k ) );
	        
	        tessellator.setColorOpaque_F( 1F, 1F, 1F );
	        
			iGrowthLevel = MathHelper.clamp_int( iGrowthLevel, 0, 7 );
			
	        block.renderCrossHatch(renderer, i, j, k, weedIconArray[iGrowthLevel],
	        	2D / 16D, dVerticalOffset);
		}
    }
}
