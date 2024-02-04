package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.MechanicalBlock;
import btw.block.tileentity.LoomTileEntity;
import btw.block.util.Flammability;
import btw.block.util.MechPowerUtils;
import btw.inventory.util.InventoryUtils;
import btw.util.MiscUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class LoomBlock extends BlockContainer implements MechanicalBlock {
	private static final int TICK_RATE = 10;
	
	public LoomBlock(int blockID) {
		super(blockID, BTWBlocks.plankMaterial);
		
		this.setHardness(1F);
		this.setResistance(5F);
		this.setAxesEffectiveOn(true);
		
		this.setBuoyant();
		this.setFireProperties(Flammability.PLANKS);
		
		this.setStepSound(soundWoodFootstep);
		
		this.setTickRandomly(true);
		
		this.setCreativeTab(CreativeTabs.tabRedstone);
		
		this.setUnlocalizedName("fcBlockLoom");
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new LoomTileEntity();
	}
	
	@Override
	public int tickRate(World world) {
		return TICK_RATE;
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		world.scheduleBlockUpdate(x, y, z, this.blockID, tickRate(world));
	}
	
	@Override
	public void breakBlock( World world, int x, int y, int z, int blockID, int metadata ) {
		InventoryUtils.ejectInventoryContents(world, x, y, z, (IInventory) world.getBlockTileEntity(x, y, z));
		super.breakBlock(world, x, y, z, blockID, metadata);
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		return this.setFacing(metadata, side);
	}
	
	@Override
	public int preBlockPlacedBy(World world, int x, int y, int z, int metadata, EntityLiving entityLiving) {
		int facing = MiscUtils.convertOrientationToFlatBlockFacing(entityLiving);
		return this.setFacing(metadata, facing);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			LoomTileEntity var13 = (LoomTileEntity) world.getBlockTileEntity(x, y, z);
			
			if (player instanceof EntityPlayerMP) {
			
			}
		}
		
		return true;
	}
	
	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		boolean isReceivingPower = isInputtingMechanicalPower(world, i, j, k);
		boolean isPowered = isMechPowered(world, i, j, k);
		
		if (isPowered != isReceivingPower) {
			setMechPowered(world, i, j, k, isReceivingPower);
		}
	}
	
	@Override
	public void randomUpdateTick(World world, int x, int y, int z, Random rand) {
		if (!isCurrentStateValid(world, x, y, z)) {
			// verify we have a tick already scheduled to prevent jams on chunk load
			if (!world.isUpdateScheduledForBlock(x, y, z, blockID)) {
				world.scheduleBlockUpdate(x, y, z, blockID, tickRate(world));
			}
		}
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
		if (!isCurrentStateValid(world, x, y, z) && !world.isUpdatePendingThisTickForBlock(x, y, z, this.blockID)) {
			world.scheduleBlockUpdate(x, y, z, this.blockID, tickRate(world));
		}
	}
	
	//------ Metadata Functionality ------//
	
	// Bits 0 and 1 store facing
	// Bit 2 stores powered state
	// Bit 3 stores processing state
	
	@Override
	public int getFacing(int metadata) {
		return (metadata & 3) + 2;
	}
	
	@Override
	public int setFacing(int metadata, int facing) {
		int poweredMetadata = metadata & 12;
		int clampedFacing = MathHelper.clamp_int(facing, 2, 5);
		
		return ((clampedFacing - 2) & 3) | poweredMetadata;
	}
	
	public boolean isMechPowered(int metadata) {
		return (metadata & 4) >> 2 == 1;
	}
	
	public boolean isMechPowered(World world, int x, int y, int z) {
		return this.isMechPowered(world.getBlockMetadata(x, y, z));
	}
	
	public int setMechPowered(int metadata, boolean isPowered) {
		int poweredState = isPowered ? 1 : 0;
		int poweredMetadata = poweredState << 2;
		
		return (metadata & 11) | poweredMetadata;
	}
	
	public void setMechPowered(World world, int x, int y, int z, boolean isPowered) {
		int metadata = world.getBlockMetadata(x, y, z);
		int newMetadata = this.setMechPowered(metadata, isPowered);
		
		if (metadata != newMetadata) {
			world.setBlockMetadataWithNotify(x, y, z, newMetadata);
		}
	}
	
	public boolean isProcessing(int metadata) {
		return (metadata & 8) >> 3 == 1;
	}
	
	public boolean isProcessing(World world, int x, int y, int z) {
		return this.isProcessing(world.getBlockMetadata(x, y, z));
	}
	
	public int setProcessing(int metadata, boolean isPowered) {
		int processingState = isPowered ? 1 : 0;
		int processingMetadata = processingState << 3;
		
		return (metadata & 7) | processingMetadata;
	}
	
	public void setProcessing(World world, int x, int y, int z, boolean isProcessing) {
		int metadata = world.getBlockMetadata(x, y, z);
		int newMetadata = this.setProcessing(metadata, isProcessing);
		
		if (metadata != newMetadata) {
			world.setBlockMetadataWithNotify(x, y, z, newMetadata);
		}
	}
	
	//------ Mechanical Power Functionality ------//
	
	@Override
	public boolean canOutputMechanicalPower() {
		return false;
	}
	
	@Override
	public boolean canInputMechanicalPower() {
		return true;
	}
	
	@Override
	public boolean isInputtingMechanicalPower(World world, int x, int y, int z) {
		return MechPowerUtils.isBlockPoweredByAxle(world, x, y, z, this);
	}
	
	@Override
	public boolean isOutputtingMechanicalPower(World world, int x, int y, int z) {
		return false;
	}
	
	@Override
	public boolean canInputAxlePowerToFacing(World world, int x, int y, int z, int facing) {
		// Can't be powered from the top or front, but can be powered from any other side
		return facing != 1 && facing != Facing.oppositeSide[this.getFacing(world, x, y, z)];
	}
	
	@Override
	public void overpower(World world, int x, int y, int z) {
	
	}
	
	//------------- Class Specific Methods ------------//
	
	public boolean isCurrentStateValid(World world, int x, int y, int z) {
		boolean isReceivingPower = isInputtingMechanicalPower(world, x, y, z);
		boolean isMechPowered = isMechPowered(world, x, y, z);
		
		return isMechPowered == isReceivingPower;
	}
	
	//------------ Client Side Functionality ----------//
	
	@Environment(EnvType.CLIENT)
	private Icon iconFront;
	@Environment(EnvType.CLIENT)
	private Icon iconTop;
	@Environment(EnvType.CLIENT)
	private Icon iconBottom;
	
	@Override
	@Environment(EnvType.CLIENT)
	public void registerIcons(IconRegister register) {
		super.registerIcons(register);
		this.iconFront = register.registerIcon("fcBlockLoom_front");
		this.iconTop = register.registerIcon("fcBlockLoom_top");
		this.iconBottom = register.registerIcon("fcBlockLoom_bottom");
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public Icon getIcon(int side, int meta) {
		if (side == 0) {
			return this.iconBottom;
		}
		else if (side == 1) {
			return this.iconTop;
		}
		else if (side == Facing.oppositeSide[this.getFacing(meta)]) {
			return this.iconFront;
		}
		else {
			return this.blockIcon;
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks render, int x, int y, int z) {
		int facing = this.getFacing(render.blockAccess.getBlockMetadata(x, y, z));
		
		switch (facing) {
			case 3:
				render.setUVRotateTop(0);
				break;
			case 2:
				render.setUVRotateTop(3);
				break;
			case 5:
				render.setUVRotateTop(2);
				break;
			case 4:
				render.setUVRotateTop(1);
				break;
		}
		
		boolean success = super.renderBlock(render, x, y, z);
		render.clearUVRotation();
		return success;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void renderBlockAsItem(RenderBlocks render, int metadata, float brightness) {
		render.setUVRotateTop(3);
		render.renderBlockAsItemVanilla(this, metadata, brightness);
		render.clearUVRotation();
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void clientNotificationOfMetadataChange(World world, int x, int y, int z, int oldMetadata, int newMetadata) {
		if (!this.isMechPowered(oldMetadata) && this.isMechPowered(newMetadata)) {
			boolean isProcessing = this.isProcessing(newMetadata);
			
			this.playSound(world, x, y, z, world.rand, isProcessing);
			this.emitParticles(world, x, y, z, world.rand, isProcessing);
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick( World world, int x, int y, int z, Random random ) {
		if (this.isMechPowered(world, x, y, z)) {
			boolean isProcessing = this.isProcessing(world, x, y, z);
			
			this.playSound(world, x, y, z, world.rand, isProcessing);
			this.emitParticles(world, x, y, z, world.rand, isProcessing);
		}
	}
	
	@Environment(EnvType.CLIENT)
	private void emitParticles(World world, int x, int y, int z, Random rand, boolean isProcessing) {
		String particle;
		
		if (isProcessing) {
			particle = "fcwhitesmoke";
		}
		else {
			particle = "smoke";
		}
		
		for (int i = 0; i < 5; i++) {
			float smokeX = x + rand.nextFloat();
			float smokeY = y + rand.nextFloat() * 0.5F + 1F;
			float smokeZ = z + rand.nextFloat();
			
			world.spawnParticle(particle, smokeX, smokeY, smokeZ, 0D, 0D, 0D);
		}
	}
	
	@Environment(EnvType.CLIENT)
	private void playSound(World world, int x, int y, int z, Random rand, boolean isProcessing) {
	
	}
}
