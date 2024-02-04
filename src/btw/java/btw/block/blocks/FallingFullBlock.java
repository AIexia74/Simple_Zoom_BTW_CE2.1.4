// FCMOD
// special cased full block with standard attributes (stuff like sand and gravel) to speed rendering

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Material;
import net.minecraft.src.RenderBlocks;

public class FallingFullBlock extends FallingBlock
{
    public FallingFullBlock(int iBlockID, Material material )
    {
    	super( iBlockID, material );
    }
    
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
