// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class SandstoneBlock extends BlockSandStone
{
    public SandstoneBlock(int iBlockID )
    {
        super( iBlockID );
        
        setPicksEffectiveOn();
        
        setHardness( 1.5F );
        
        setStepSound( soundStoneFootstep );
        
        setUnlocalizedName( "sandStone" );        
    }
    
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 3; // diamond or better
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.sandPile.itemID, 16, 0, fChanceOfDrop);
		
		return true;
	}
	
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	return renderer.renderStandardFullBlock(this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean doesItemRenderAsBlock(int iItemDamage)
    {
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockMovedByPiston(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	renderBlocks.renderStandardFullBlockMovedByPiston(this, i, j, k);
    }    
}
