package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class SpiderEyeSlab extends SlabBlock {
	public SpiderEyeSlab(int blockID) {
		super(blockID, Material.ground);
		this.setHardness(0.6F);
		this.setShovelsEffectiveOn(true);
		this.setBuoyancy(1.0F);
		this.setStepSound(BTWBlocks.stepSoundSquish);
		this.setUnlocalizedName("fcBlockSpiderEyeSlab");
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
	
	@Override
	public int getCombinedBlockID(int iMetadata) {
		return BTWBlocks.spiderEyeBlock.blockID;
	}
	
	@Override
	public boolean canBePistonShoveled(World world, int x, int y, int z) {
		return true;
	}
	
	//------------ Client Side Functionality ----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public Icon getIcon(int side, int metadata) {
		return BTWBlocks.spiderEyeBlock.getIcon(side, metadata);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void registerIcons(IconRegister register) {}
}
