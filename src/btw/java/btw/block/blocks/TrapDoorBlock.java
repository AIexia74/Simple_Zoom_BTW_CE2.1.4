// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class TrapDoorBlock extends BlockTrapDoor {
	protected static final double THICKNESS = (3D / 16D);
	protected static final double HALF_THICKNESS = (THICKNESS / 2D);
	
	// Used to track values for placement on upper or lower faces
	private static boolean flagBetterPlaceRule;
	private static boolean betterPlaceRuleIsLower;
	
	public TrapDoorBlock(int blockID) {
		super(blockID, BTWBlocks.plankMaterial);
		
		setHardness(1.5F);
		
		setAxesEffectiveOn();
		
		setBuoyant();
		
		initBlockBounds(0D, 0.5D - HALF_THICKNESS, 0D, 1D, 0.5D + HALF_THICKNESS, 1D);
		
		setStepSound(soundWoodFootstep);
		
		setUnlocalizedName("trapdoor");
		
		disableStats();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		int metadata = world.getBlockMetadata(x, y, z);
		world.setBlockMetadataWithNotify(x, y, z, metadata ^ 4);
		world.playAuxSFXAtEntity(player, 1003, x, y, z, 0);
		return true;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID) {
		// override to prevent trap doors from responding to redstone signal and
		// dropping without support
	}
	
	@Override
	public void onPoweredBlockChange(World world, int x, int y, int z, boolean isPowered) {
		// override to prevent trap doors from responding to redstone signal
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		return true;
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		int newMetadata = 0;
		
		if (side == 2) {
			newMetadata = 0;
		}
		
		if (side == 3) {
			newMetadata = 1;
		}
		
		if (side == 4) {
			newMetadata = 2;
		}
		
		if (side == 5) {
			newMetadata = 3;
		}
		
		if (side != 1 && side != 0 && hitY > 0.5F) {
			newMetadata += 8;
		}
		
		if (side == 0 || side == 1) {
			flagBetterPlaceRule = true;
			betterPlaceRuleIsLower = (side == 1 ? true : false);
		}
		
		return newMetadata;
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity, ItemStack itemStack) {
		if (flagBetterPlaceRule) {
			int facing = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			int metadataMod = (betterPlaceRuleIsLower ? 0 : 8);
			
			if (facing == 0) {
				world.setBlockMetadataWithNotify(x, y, z, metadataMod + 0, 2);
			}
			
			if (facing == 1) {
				world.setBlockMetadataWithNotify(x, y, z, metadataMod + 3, 2);
			}
			
			if (facing == 2) {
				world.setBlockMetadataWithNotify(x, y, z, metadataMod + 1, 2);
			}
			
			if (facing == 3) {
				world.setBlockMetadataWithNotify(x, y, z, metadataMod + 2, 2);
			}
			
			flagBetterPlaceRule = false;
		}
	}
	
	@Override
	public boolean isBlockClimbable(World world, int x, int y, int z) {
		Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
		return blockBelow instanceof LadderBlockBase && world.getBlockMetadata(x, y - 1, z) == (world.getBlockMetadata(x, y, z) & 3);
	}
	
	@Override
	public int getWeightOnPathBlocked(IBlockAccess blockAccess, int x, int y, int z) {
		return -4;
	}
	
	// Removed this so that mobs can reliably path through trap doors
    /*
    @Override
    public int AdjustPathWeightOnNotBlocked( int iPreviousWeight )
    {
    	return 2;
    }
    */
	
	@Override
	public boolean canPathThroughBlock(IBlockAccess blockAccess, int x, int y, int z, Entity entity, PathFinder pathFinder) {
		if (!pathFinder.CanPathThroughClosedWoodDoor()) {
			// note: getBlocksMovement() is misnamed and returns if the block *doesn't* block movement
			if (!pathFinder.canPathThroughOpenWoodDoor() || !getBlocksMovement(blockAccess, x, y, z)) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean isBreakableBarricade(IBlockAccess blockAccess, int x, int y, int z) {
		return true;
	}
	
	@Override
	public boolean isBreakableBarricadeOpen(IBlockAccess blockAccess, int x, int y, int z) {
		return isTrapdoorOpen(blockAccess.getBlockMetadata(x, y, z));
	}
	
	@Override
	public boolean getBlocksMovement(IBlockAccess blockAccess, int x, int y, int z) {
		// override because vanilla function does the opposite of what's intended
		return isTrapdoorOpen(blockAccess.getBlockMetadata(x, y, z));
	}
	
	@Override
	public int getHarvestToolLevel(IBlockAccess blockAccess, int x, int y, int z) {
		return 2; // iron or better
	}
	
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 2, 0, fChanceOfDrop);
		
		return true;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		return getBlockBoundsFromPoolBasedOnState(world, i, j, k).offset(i, j, k);
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
		// override to deprecate parent
	}
	
	@Override
	public void setBlockBoundsForItemRender() {
		// override to deprecate parent
	}
	
	@Override
	public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		
		if (isTrapdoorOpen(metadata)) {
			int direction = metadata & 3;
			
			switch (direction) {
				case 0:
					return AxisAlignedBB.getAABBPool().getAABB(0.0F, 0.0F, 1.0F - THICKNESS, 1.0F, 1.0F, 1.0F);
				case 1:
					return AxisAlignedBB.getAABBPool().getAABB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, THICKNESS);
				case 2:
					return AxisAlignedBB.getAABBPool().getAABB(1.0F - THICKNESS, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				default: // 3
					return AxisAlignedBB.getAABBPool().getAABB(0.0F, 0.0F, 0.0F, THICKNESS, 1.0F, 1.0F);
			}
		}
		
		if ((metadata & 8) != 0) {
			return AxisAlignedBB.getAABBPool().getAABB(0.0F, 1.0F - THICKNESS, 0.0F, 1.0F, 1.0F, 1.0F);
		}
		
		return AxisAlignedBB.getAABBPool().getAABB(0.0F, 0.0F, 0.0F, 1.0F, THICKNESS, 1.0F);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 startRay, Vec3 endRay) {
		// copy of method in block base class to override that in parent
		AxisAlignedBB collisionBox = getBlockBoundsFromPoolBasedOnState(world, x, y, z).offset(x, y, z);
		
		MovingObjectPosition collisionPoint = collisionBox.calculateIntercept(startRay, endRay);
		
		if (collisionPoint != null) {
			collisionPoint.blockX = x;
			collisionPoint.blockY = y;
			collisionPoint.blockZ = z;
		}
		
		return collisionPoint;
	}
	
	@Override
	public boolean canItemPassIfFilter(ItemStack filteredItem) {
		int filterableProperties = filteredItem.getItem().getFilterableProperties(filteredItem);
		return (filterableProperties & (Item.FILTERABLE_SMALL | Item.FILTERABLE_NARROW | Item.FILTERABLE_FINE)) != 0;
	}
	
	@Override
	public boolean getCanGrassGrowUnderBlock(World world, int x, int y, int z, boolean var5) {
		return true;
	}
	
	@Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing, boolean var6) {
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		int facingOpposite = Facing.oppositeSide[facing];
		
		switch (facingOpposite) {
			case 0:
				return metadata >= 8 && metadata < 12;
			case 1:
				return metadata < 4;
			case 2:
				return metadata == 4 || metadata == 12;
			case 3:
				return metadata == 5 || metadata == 13;
			case 4:
				return metadata == 6 || metadata == 14;
			case 5:
				return metadata == 7 || metadata == 15;
			default:
				return false;
		}
	}
	
	//------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public Icon getHopperFilterIcon() {
		return this.blockIcon;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return getBlockBoundsFromPoolBasedOnState(world, x, y, z).offset(x, y, z);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int neighborX, int neighborY, int neighborZ, int side) {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(neighborX, neighborY, neighborZ, side);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks renderBlocks, int x, int y, int z) {
		super.setBlockBoundsForItemRender();
		super.setBlockBoundsBasedOnState(renderBlocks.blockAccess, x, y, z);
		renderBlocks.setRenderBounds(this.getBlockBoundsFromPoolBasedOnState(renderBlocks.blockAccess, x, y, z));
		
		//0 east
		//1 west
		//2 north
		//3 south
		
		switch (renderBlocks.blockAccess.getBlockMetadata(x, y, z)) {
			case 0:
			case 8:
				renderBlocks.setUVRotateTop(3);
				renderBlocks.setUVRotateBottom(3);
				break;
			case 1:
			case 9:
				renderBlocks.setUVRotateTop(0);
				renderBlocks.setUVRotateBottom(0);
				break;
			case 2:
			case 10:
			case 14:
			case 15:
				renderBlocks.setUVRotateTop(1);
				renderBlocks.setUVRotateBottom(1);
				break;
			case 3:
			case 11:
				renderBlocks.setUVRotateTop(2);
				renderBlocks.setUVRotateBottom(2);
				break;
			case 4:
			case 5:
				renderBlocks.setUVRotateEast(3);
				renderBlocks.setUVRotateWest(3);
				renderBlocks.setUVRotateNorth(1);
				renderBlocks.setUVRotateSouth(1);
				renderBlocks.setUVRotateTop(1);
				renderBlocks.setUVRotateBottom(1);
				break;
			case 6:
			case 7:
				renderBlocks.setUVRotateEast(1);
				renderBlocks.setUVRotateWest(1);
				renderBlocks.setUVRotateNorth(3);
				renderBlocks.setUVRotateSouth(3);
				renderBlocks.setUVRotateTop(1);
				renderBlocks.setUVRotateBottom(1);
				break;
			default:
				break;
		}
		
		renderBlocks.renderStandardBlock(this, x, y, z);
		renderBlocks.clearUVRotation();
		return true;
	}
}
