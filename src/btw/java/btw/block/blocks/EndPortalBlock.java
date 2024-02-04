// FCMOD

package btw.block.blocks;

import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class EndPortalBlock extends BlockEndPortal
{
    public EndPortalBlock(int iBlockID, Material material)
    {
        super( iBlockID, material );
        
        initBlockBounds(0F, 0F, 0F, 1F, 0.0625F, 1F);
        
        setTickRandomly( true );
    }
    
    @Override
    public void onBlockAdded(World world, int i, int j, int k )
    {
    	super.onBlockAdded( world, i, j, k );
    	
		WorldUtils.gameProgressSetEndDimensionHasBeenAccessedServerOnly();
    }
    
    @Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
    	super.updateTick( world, i, j, k, rand );
    	
		WorldUtils.gameProgressSetEndDimensionHasBeenAccessedServerOnly();
    }
    	
	@Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int i, int j, int k )
    {
    	// override to deprecate parent
    }
	
    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k)
    {	 
    	return null; // can't be picked up
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	return false;
    }    
}
