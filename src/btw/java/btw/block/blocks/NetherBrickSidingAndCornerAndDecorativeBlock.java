//FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;
import btw.BTWMod;

public class NetherBrickSidingAndCornerAndDecorativeBlock extends SidingAndCornerAndDecorativeWallBlock
{
	public NetherBrickSidingAndCornerAndDecorativeBlock(int iBlockID)
	{
		super( iBlockID, BTWBlocks.netherRockMaterial,
			"fcBlockDecorativeNetherBrick", 2.0F, 10F, Block.soundStoneFootstep,
			"fcNetherBrickSiding" );
	}	
}