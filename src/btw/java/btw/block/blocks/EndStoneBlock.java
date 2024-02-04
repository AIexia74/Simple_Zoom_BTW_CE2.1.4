// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Material;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.World;

public class EndStoneBlock extends FullBlock
{
    public EndStoneBlock(int iBlockID, Material material )
    {
    	super( iBlockID, material );
    }
    
    @Override
    public float getMovementModifier(World world, int i, int j, int k)
    {
    	return 1.0F;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult)
    {
        renderCookingByKiLnOverlay(renderBlocks, i, j, k, bFirstPassResult);
    }
}
