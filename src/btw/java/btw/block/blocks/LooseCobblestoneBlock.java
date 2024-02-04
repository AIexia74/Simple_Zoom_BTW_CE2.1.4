// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class LooseCobblestoneBlock extends LavaReceiverBlock {
	public LooseCobblestoneBlock(int iBlockID) {
		super(iBlockID, Material.rock);

		setHardness(1F); // setHardness( 2F ); regular cobble
		setResistance(5F); // setResistance( 10F ); regular cobble

		setPicksEffectiveOn();
		setChiselsEffectiveOn();

		setStepSound(Block.soundStoneFootstep);

		setUnlocalizedName("fcBlockCobblestoneLoose");

		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public boolean onMortarApplied(World world, int i, int j, int k) {
		world.setBlockAndMetadataWithNotify(i, j, k, Block.cobblestone.blockID, getStrata(world, i, j, k));

		return true;
	}

	@Override
	public int damageDropped(int metadata) {
		return getStrata(metadata) << 2; // this block stores strata in last 2 bits
	}

	@Override
	public void dropItemsOnDestroyedByMiningCharge(World world, int x, int y, int z, int metadata) {
		if (!world.isRemote) {
			dropBlockAsItem_do(world, x, y, z, new ItemStack(Block.gravel));
		}
	}

	@Override
	public boolean canBeConvertedByMobSpawner(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public void convertBlockFromMobSpawner(World world, int x, int y, int z) {
		world.setBlockAndMetadataWithNotify(x, y, z, Block.cobblestoneMossy.blockID, getStrata(world, x, y, z));
	}

	// ------------- Class Specific Methods ------------//


	// ------------ Client Side Functionality ----------//
	
	@Environment(EnvType.CLIENT)	
	private Icon iconLavaCracks;
	@Environment(EnvType.CLIENT)	
	private Icon iconByMetadataArray[] = new Icon[3];

	@Override
	@Environment(EnvType.CLIENT)
	public void registerIcons(IconRegister register) {
		super.registerIcons(register);

		iconLavaCracks = register.registerIcon("fcOverlayCobblestoneLava");

		iconByMetadataArray[0] = blockIcon;
		iconByMetadataArray[1] = register.registerIcon("fcBlockCobblestoneLoose_1");
		iconByMetadataArray[2] = register.registerIcon("fcBlockCobblestoneLoose_2");
	}

	@Override
	public void getSubBlocks(int iBlockID, CreativeTabs creativeTabs, List list) {
		list.add(new ItemStack(iBlockID, 1, 0));
		list.add(new ItemStack(iBlockID, 1, 4));
		list.add(new ItemStack(iBlockID, 1, 8));
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		// used only by pick block
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Icon getIcon(int iSide, int iMetadata) {
		return iconByMetadataArray[getStrata(iMetadata)];
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected Icon getLavaCracksOverlay() {
		return iconLavaCracks;
	}

}