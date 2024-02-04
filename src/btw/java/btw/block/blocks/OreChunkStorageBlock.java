// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Material;
import net.minecraft.src.RenderBlocks;

public abstract class OreChunkStorageBlock extends FallingFullBlock
{
    protected OreChunkStorageBlock(int iBlockID )
    {
        super( iBlockID, Material.rock );
        
        setHardness( 1F );
        setResistance( 5F );
        setPicksEffectiveOn();
        
        setStepSound( Block.soundStoneFootstep );
        
		setCreativeTab( CreativeTabs.tabBlock );
    }
    
	//------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult)
    {
        renderCookingByKiLnOverlay(renderBlocks, i, j, k, bFirstPassResult);
    }
}
