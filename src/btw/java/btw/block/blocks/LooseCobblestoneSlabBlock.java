// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class LooseCobblestoneSlabBlock extends MortarReceiverSlabBlock {

	public LooseCobblestoneSlabBlock(int iBlockID) {
		super(iBlockID, Material.rock);

		setHardness(1F); // setHardness( 2F ); regular cobble
		setResistance(5F); // setResistance( 10F ); regular cobble

		setPicksEffectiveOn();
		setChiselsEffectiveOn();

		setStepSound(Block.soundStoneFootstep);

		setUnlocalizedName("fcBlockCobblestoneLooseSlab");

		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public int getCombinedBlockID(int iMetadata) {
		return BTWBlocks.looseCobblestone.blockID;
	}

	@Override
	public int getCombinedMetadata(int iMetadata) {
		return getStrata(iMetadata) << 2; // loose cobblestone stores strata in last 2 bits
	}

	@Override
	public boolean attemptToCombineWithFallingEntity(World world, int i, int j, int k, EntityFallingSand entity) {
		if (entity.blockID == blockID && !getIsUpsideDown(world, i, j, k)
				&& entity.metadata == world.getBlockMetadata(i, j, k)) {

			convertToFullBlock(world, i, j, k);

			return true;
		}

		return false;
	}

	@Override
	public int damageDropped(int metadata) {
		return metadata &= (~1);
	}

	@Override
	public boolean onMortarApplied(World world, int i, int j, int k) {
		int iNewMetadata = getStrata(world, i, j, k);

		if (getIsUpsideDown(world, i, j, k)) {
			iNewMetadata |= 8;
		}

		world.setBlockAndMetadataWithNotify(i, j, k, BTWBlocks.cobblestoneSlab.blockID, iNewMetadata);

		return true;
	}

	// ------------- Class Specific Methods ------------//

	/**
	 * returns 0 - 2 regardless of what metadata is used to store strata. 
	 * BEWARE: Different blocks store strata differently
	 */
	public int getStrata(IBlockAccess blockAccess, int i, int j, int k) {
		return getStrata(blockAccess.getBlockMetadata(i, j, k));
	}

	/**
	 * returns 0 - 2 regardless of what metadata is used to store strata. 
	 * BEWARE: Different blocks store strata differently
	 */
	public int getStrata(int iMetadata) {
		return (iMetadata & 12) >>> 2;
	}

	// ----------- Client Side Functionality -----------//

	private Icon iconByMetadataArray[] = new Icon[3];

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
	public void registerIcons(IconRegister register) {
		blockIcon = register.registerIcon("fcBlockCobblestoneLoose");

		iconByMetadataArray[0] = blockIcon;
		iconByMetadataArray[1] = register.registerIcon("fcBlockCobblestoneLoose_1");
		iconByMetadataArray[2] = register.registerIcon("fcBlockCobblestoneLoose_2");

	}

	@Override
	@Environment(EnvType.CLIENT)
	public Icon getIcon(int iSide, int iMetadata) {
		return iconByMetadataArray[getStrata(iMetadata)];
	}
}