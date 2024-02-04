package btw.block.blocks;

import btw.item.BTWItems;

public class CarrotBlock extends CarrotBlockBase {
	public CarrotBlock(int iBlockID) {
		super(iBlockID);
		this.setUnlocalizedName("fcBlockCarrots");
	}

	@Override
	protected int getCropItemID() {
		return BTWItems.carrot.itemID;
	}

	@Override
	protected int getSeedItemID() {
		return 0;
	}
}