package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.RenderBlocks;

public class CarrotBlockFlowers extends CarrotBlockBase {
	public CarrotBlockFlowers(int iBlockID) {
		super(iBlockID);
		this.setUnlocalizedName("fcBlockCarrotFlowers");
	}

	@Override
	protected int getCropItemID() {
		return BTWItems.carrotSeeds.itemID;
	}

	@Override
	protected int getSeedItemID() {
		return BTWItems.carrotSeeds.itemID;
	}
    
	//------------ Client Side Functionality ----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, x, y, z));
        
        BTWBlocks.weeds.renderWeeds(this, renderer, x, y, z);

    	return renderer.renderCrossedSquares(this, x, y, z);
    }
}