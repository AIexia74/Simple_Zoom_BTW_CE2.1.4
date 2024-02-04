// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.BlockOre;
import net.minecraft.src.IconRegister;
import net.minecraft.src.RenderBlocks;

public class NetherQuartzOreBlock extends BlockOre
{
    public NetherQuartzOreBlock(int iBlockID )
    {
        super( iBlockID );
        
    	setBlockMaterial(BTWBlocks.netherRockMaterial);
    	
    	setHardness( 1F );
    	setResistance( 5F );
    	
    	setStepSound( soundStoneFootstep );
    	
    	setUnlocalizedName("netherquartz");
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		blockIcon = register.registerIcon( "fcBlockNetherQuartz" );
    }

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