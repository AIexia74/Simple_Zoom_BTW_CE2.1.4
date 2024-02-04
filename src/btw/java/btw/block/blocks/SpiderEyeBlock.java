package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Material;
import net.minecraft.src.World;

public class SpiderEyeBlock extends Block {
	public SpiderEyeBlock(int blockID) {
		super(blockID, Material.ground);
		this.setHardness(0.6F);
		this.setShovelsEffectiveOn(true);
		this.setBuoyancy(1.0F);
		this.setStepSound(BTWBlocks.stepSoundSquish);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setUnlocalizedName("fcBlockSpiderEye");
	}
	
	@Override
	public boolean canBePistonShoveled(World world, int x, int y, int z) {
		return true;
	}
}
