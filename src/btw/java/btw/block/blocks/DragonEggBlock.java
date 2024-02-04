// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.BlockDragonEgg;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.World;

import java.util.Random;

public class DragonEggBlock extends BlockDragonEgg
{
    public DragonEggBlock(int iBlockID )
    {
        super( iBlockID );
        
        initBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
        
        setCreativeTab( CreativeTabs.tabDecorations );
    }
    
	@Override
    public void updateTick(World world, int i, int j, int k, Random rand )
    {
        checkForFall(world, i, j, k);
    }

	@Override
    public void onBlockDestroyedLandingFromFall(World world, int i, int j, int k, int iMetadata)
    {
		dropBlockAsItem( world, i, j, k, iMetadata, 0 );
    }
    
	//------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderBlockDragonEgg( this, i, j, k );
    }    
}
