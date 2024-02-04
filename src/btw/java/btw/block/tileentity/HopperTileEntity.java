// FCMOD

package btw.block.tileentity;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticNonOpaqueBlock;
import btw.block.blocks.ArcaneVesselBlock;
import btw.block.blocks.HopperBlock;
import btw.block.blocks.PortalBlock;
import btw.client.fx.BTWEffectManager;
import btw.inventory.util.InventoryUtils;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

import java.util.List;

public class HopperTileEntity extends TileEntity implements IInventory, TileEntityDataPacketHandler {
	private static final int INVENTORY_SIZE = 19;
	private static final int STACK_SIZE_LIMIT = 64;
	private static final double MAX_PLAYER_INTERACTION_DIST = 64D;
	
	private static final int STACK_SIZE_TO_EJECT = 8;
	private static final int TIME_TO_EJECT = 3;
	
	private static final int OVERLOAD_SOUL_COUNT = 8;
	private static final int CHANCE_OF_CHECKING_FOR_POSSESSION = 8;
	private static final int POSSESSION_COUNT_ON_OVERLOAD = 4;
	
	private static final int XP_INVENTORY_SPACE = 100;
	private static final int XP_EJECT_UNIT_SIZE = 20;
	private static final int XP_DELAY_BETWEEN_DROPS = 10;
	
	private ItemStack contents[];
	
	private int ejectCounter;
	private int containedSoulCount;
	private int containedXPCount;
	private int hopperXPDropDelayCount;
	
	public boolean outputBlocked;
	
	public int mechanicalPowerIndicator; // used to communicate power status from server to client.  0 for off, 1 for on
	
	// state variables used to communicate basic inventory info to the client
	
	protected int filterItemID;
	public short storageSlotsOccupied;
	
	public HopperTileEntity() {
		contents = new ItemStack[INVENTORY_SIZE];
		
		ejectCounter = 0;
		containedSoulCount = 0;
		
		containedXPCount = 0;
		
		hopperXPDropDelayCount = XP_DELAY_BETWEEN_DROPS;
		
		outputBlocked = false;
		
		mechanicalPowerIndicator = 0;
		filterItemID = 0;
		storageSlotsOccupied = 0;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
		
		contents = new ItemStack[getSizeInventory()];
		
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
			
			int j = nbttagcompound1.getByte("Slot") & 0xff;
			
			if (j >= 0 && j < contents.length) {
				contents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
		
		// note, this is misnamed, but we've already release with it as is, so it will have to remain this way
		if (nbttagcompound.hasKey("grindCounter")) {
			ejectCounter = nbttagcompound.getInteger("grindCounter");
		}
		
		if (nbttagcompound.hasKey("iHoppeContainedSoulCount")) {
			containedSoulCount = nbttagcompound.getInteger("iHoppeContainedSoulCount");
		}
		
		if (nbttagcompound.hasKey("iHopperContainedXPCount")) {
			containedXPCount = nbttagcompound.getInteger("iHopperContainedXPCount");
		}
		
		validateInventoryStateVariables();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		NBTTagList nbttaglist = new NBTTagList();
		
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				
				contents[i].writeToNBT(nbttagcompound1);
				
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		
		nbttagcompound.setTag("Items", nbttaglist);
		
		// note, this is misnamed, but we've already release with it as is, so it will have to remain this way
		nbttagcompound.setInteger("grindCounter", ejectCounter);
		
		nbttagcompound.setInteger("iHoppeContainedSoulCount", containedSoulCount);
		
		nbttagcompound.setInteger("iHopperContainedXPCount", containedXPCount);
	}
	
	@Override
	public void updateEntity() {
		if (worldObj.isRemote) {
			return;
		}
		
		boolean bHopperOn = ((HopperBlock) BTWBlocks.hopper).isBlockOn(worldObj, xCoord, yCoord, zCoord);
		
		if (bHopperOn) {
			mechanicalPowerIndicator = 1;
			
			attemptToEjectXPFromInv();
			
			if (!outputBlocked) {
				// the hopper is powered, eject items
				
				ejectCounter += 1;
				
				if (ejectCounter >= TIME_TO_EJECT) {
					attemptToEjectStackFromInv();
					
					ejectCounter = 0;
				}
			}
			else {
				ejectCounter = 0;
			}
		}
		else {
			mechanicalPowerIndicator = 0;
			
			ejectCounter = 0;
			hopperXPDropDelayCount = 0;
		}
		
		if (containedSoulCount > 0) {
			// souls can only be trapped if there's a soul sand filter on the hopper
			
			if (filterItemID == Block.slowSand.blockID) {
				int iBlockBelowID = worldObj.getBlockId(xCoord, yCoord - 1, zCoord);
				int iBlockBelowMetaData = worldObj.getBlockMetadata(xCoord, yCoord - 1, zCoord);
				
				if (bHopperOn) {
					if (iBlockBelowID != BTWBlocks.aestheticNonOpaque.blockID || iBlockBelowMetaData != AestheticNonOpaqueBlock.SUBTYPE_URN) {
						for (int i = 0; i < this.containedSoulCount; i++) {
							if (this.worldObj.rand.nextInt(CHANCE_OF_CHECKING_FOR_POSSESSION) == 0) {
								EntityCreature.attemptToPossessCreaturesAroundBlock(this.worldObj, this.xCoord, this.yCoord, this.zCoord, 1, PortalBlock.CREATURE_POSSESSION_RANGE);
							}
						}
						
						containedSoulCount = 0;
					}
				}
				
				if (containedSoulCount >= OVERLOAD_SOUL_COUNT) {
					if (bHopperOn && iBlockBelowID == BTWBlocks.aestheticNonOpaque.blockID && iBlockBelowMetaData == AestheticNonOpaqueBlock.SUBTYPE_URN) {
						// convert the urn below to a soul urn
						
						worldObj.setBlockWithNotify(xCoord, yCoord - 1, zCoord, 0);
						
						ItemStack newItemStack = new ItemStack(BTWItems.soulUrn.itemID, 1, 0);
						
						ItemUtils.ejectStackWithRandomOffset(worldObj, xCoord, yCoord - 1, zCoord, newItemStack);
						
						// the rest of the souls escape (if any remain)
						
						containedSoulCount = 0;
					}
					else {
						// the hopper has become overloaded with souls.  Destroy it and attempt to possess nearby creatures
						hopperSoulOverload();
					}
				}
			}
		}
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		
		nbttagcompound.setInteger("f", filterItemID);
		nbttagcompound.setShort("s", storageSlotsOccupied);
		
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, nbttagcompound);
	}
	
	//------------- FCITileEntityDataPacketHandler ------------//
	
	@Override
	public void readNBTFromPacket(NBTTagCompound nbttagcompound) {
		filterItemID = nbttagcompound.getInteger("f");
		storageSlotsOccupied = nbttagcompound.getShort("s");
		
		// force a visual update for the block since the above variables affect it
		
		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}
	
	//------------- IInventory implementation -------------//
	
	@Override
	public int getSizeInventory() {
		return INVENTORY_SIZE;
	}
	
	@Override
	public ItemStack getStackInSlot(int iSlot) {
		return contents[iSlot];
	}
	
	@Override
	public ItemStack decrStackSize(int iSlot, int iAmount) {
		return InventoryUtils.decreaseStackSize(this, iSlot, iAmount);
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (contents[par1] != null) {
			ItemStack itemstack = contents[par1];
			contents[par1] = null;
			return itemstack;
		}
		else {
			return null;
		}
	}
	
	@Override
	public void setInventorySlotContents(int iSlot, ItemStack itemstack) {
		contents[iSlot] = itemstack;
		
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		
		onInventoryChanged();
	}
	
	@Override
	public String getInvName() {
		return "Hopper";
	}
	
	@Override
	public int getInventoryStackLimit() {
		return STACK_SIZE_LIMIT;
	}
	
	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
		
		if (worldObj != null) {
			outputBlocked = false;    // the change in inventory may allow the dispenser to eject again.
			
			if (validateInventoryStateVariables()) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			
			int iOccupiedStacks = InventoryUtils.getNumOccupiedStacksInRange(this, 0, 17);
			
			((HopperBlock) (BTWBlocks.hopper)).setHasFilter(worldObj, xCoord, yCoord, zCoord, filterItemID > 0);
			
		}
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		}
		
		return (entityplayer.getDistanceSq((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= MAX_PLAYER_INTERACTION_DIST);
	}
	
	@Override
	public void openChest() {
	}
	
	@Override
	public void closeChest() {
	}
	
	@Override
	public boolean isStackValidForSlot(int iSlot, ItemStack stack) {
		return true;
	}
	
	@Override
	public boolean isInvNameLocalized() {
		return true;
	}
	
	//------------- Class Specific Methods ------------//
	
	public Item getCurrentFilterItem() {
		return Item.itemsList[filterItemID];
	}
	
	/*
	 * Returns true if any changes are made, false otherwise
	 */
	private boolean validateInventoryStateVariables() {
		boolean bStateChanged = false;
		
		int currentFilterID = getFilterIDBasedOnInventory();
		
		if (currentFilterID != filterItemID) {
			filterItemID = currentFilterID;
			
			bStateChanged = true;
		}
		
		short numSlotsOccupied = (short) (InventoryUtils.getNumOccupiedStacksInRange(this, 0, 17));
		
		if (numSlotsOccupied != storageSlotsOccupied) {
			storageSlotsOccupied = numSlotsOccupied;
			
			bStateChanged = true;
		}
		
		return bStateChanged;
	}
	
	public int getFilterIDBasedOnInventory() {
		ItemStack filterStack = getStackInSlot(18);
		
		if (filterStack != null && filterStack.stackSize > 0) {
			return filterStack.itemID;
		}
		
		return 0;
	}
	
	public Item getFilterItem() {
		ItemStack filterStack = getStackInSlot(18);
		
		if (filterStack != null) {
			return filterStack.getItem();
		}
		
		return null;
	}
	
	public boolean canCurrentFilterProcessItem(ItemStack itemStack) {
		Item filterItem = getFilterItem();
		
		if (filterItem != null) {
			return filterItem.canItemPassIfFilter(itemStack);
		}
		
		return true;
	}
	
	public boolean isEjecting() {
		return ((HopperBlock) BTWBlocks.hopper).isBlockOn(worldObj, xCoord, yCoord, zCoord);
	}
	
	private void attemptToEjectStackFromInv() {
		int iStackIndex = InventoryUtils.getRandomOccupiedStackInRange(this, worldObj.rand, 0, 17);
		
		if (iStackIndex >= 0 && iStackIndex <= 17) {
			ItemStack invStack = getStackInSlot(iStackIndex);
			
			int iEjectStackSize;
			
			if (STACK_SIZE_TO_EJECT > invStack.stackSize) {
				iEjectStackSize = invStack.stackSize;
			}
			else {
				iEjectStackSize = STACK_SIZE_TO_EJECT;
			}
			
			ItemStack ejectStack = new ItemStack(invStack.itemID, iEjectStackSize, invStack.getItemDamage());
			
			InventoryUtils.copyEnchantments(ejectStack, invStack);
			
			int iTargetI = xCoord;
			int iTargetJ = yCoord - 1;
			int iTargetK = zCoord;
			
			boolean bEjectIntoWorld = false;
			
			if (worldObj.isAirBlock(iTargetI, iTargetJ, iTargetK)) {
				bEjectIntoWorld = true;
			}
			else {
				if (WorldUtils.isReplaceableBlock(worldObj, iTargetI, iTargetJ, iTargetK)) {
					bEjectIntoWorld = true;
				}
				else {
					int iTargetBlockID = worldObj.getBlockId(iTargetI, iTargetJ, iTargetK);
					
					Block targetBlock = Block.blocksList[iTargetBlockID];
					
					if (targetBlock == null || !targetBlock.doesBlockHopperEject(worldObj, iTargetI, iTargetJ, iTargetK)) {
						bEjectIntoWorld = true;
					}
					else if (targetBlock.doesBlockHopperInsert(worldObj, iTargetI, iTargetJ, iTargetK)) {
						outputBlocked = true;
					}
					else {
						TileEntity targetTileEntity = worldObj.getBlockTileEntity(iTargetI, iTargetJ, iTargetK);
						
						int iNumItemsStored = 0;
						
						if (targetTileEntity != null && targetTileEntity instanceof IInventory) {
							int iMinSlotToAddTo = 0;
							int iMaxSlotToAddTo = ((IInventory) targetTileEntity).getSizeInventory() - 1;
							boolean canProcessStack = true;
							
							if (iTargetBlockID == Block.furnaceIdle.blockID || iTargetBlockID == Block.furnaceBurning.blockID) {
								iMaxSlotToAddTo = 0;
							}
							else if (iTargetBlockID == BTWBlocks.hopper.blockID) {
								iMaxSlotToAddTo = 17;
								
								int iTargetFilterID = ((HopperTileEntity) targetTileEntity).filterItemID;
								
								if (iTargetFilterID > 0) {
									// filters in the hopper below block ejection
									
									canProcessStack = false;
								}
							}
							
							if (canProcessStack) {
								boolean bFullStackDeposited;
								
								if (iTargetBlockID != Block.chest.blockID && iTargetBlockID != BTWBlocks.chest.blockID) {
									bFullStackDeposited = InventoryUtils
											.addItemStackToInventoryInSlotRange((IInventory) targetTileEntity, ejectStack, iMinSlotToAddTo, iMaxSlotToAddTo);
								}
								else {
									bFullStackDeposited = InventoryUtils.addItemStackToChest((TileEntityChest) targetTileEntity, ejectStack);
								}
								
								if (!bFullStackDeposited) {
									iNumItemsStored = iEjectStackSize - ejectStack.stackSize;
								}
								else {
									iNumItemsStored = iEjectStackSize;
								}
								
								if (iNumItemsStored > 0) {
									decrStackSize(iStackIndex, iNumItemsStored);
									
									worldObj.playAuxSFX(BTWEffectManager.ITEM_COLLECTION_POP_EFFECT_ID, xCoord, yCoord, zCoord, 0);
								}
							}
							else {
								outputBlocked = true;
							}
						}
						else {
							outputBlocked = true;
						}
					}
				}
			}
			
			if (bEjectIntoWorld) {
				// test for a storage cart below the hopper
				
				List list = worldObj.getEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getAABBPool()
						.getAABB((float) xCoord + 0.4f, (float) yCoord - 0.5f, (float) zCoord + 0.4f, (float) xCoord + 0.6f, yCoord, (float) zCoord + 0.6f));
				
				if (list != null && list.size() > 0) {
					for (int listIndex = 0; listIndex < list.size(); listIndex++) {
						EntityMinecart minecartEntity = (EntityMinecart) list.get(listIndex);
						
						if (minecartEntity.getMinecartType() == 1)    // storage cart
						{
							// check if the cart is properly aligned with the nozzle
							
							if (minecartEntity.boundingBox.intersectsWith(AxisAlignedBB.getAABBPool()
									.getAABB((float) xCoord, (float) yCoord - 0.5f, (float) zCoord, (float) xCoord + 0.25f, yCoord, (float) zCoord + 1.0f)) &&
									minecartEntity.boundingBox.intersectsWith(AxisAlignedBB.getAABBPool()
											.getAABB((float) xCoord + 0.75f, (float) yCoord - 0.5f, (float) zCoord, (float) xCoord + 1.0f, yCoord,
													(float) zCoord + 1.0f)) && minecartEntity.boundingBox.intersectsWith(AxisAlignedBB.getAABBPool()
									.getAABB((float) xCoord, (float) yCoord - 0.5f, (float) zCoord, (float) xCoord + 1.0f, yCoord, (float) zCoord + 0.25f)) &&
									minecartEntity.boundingBox.intersectsWith(AxisAlignedBB.getAABBPool()
											.getAABB((float) xCoord, (float) yCoord - 0.5f, (float) zCoord + 0.75f, (float) xCoord + 1.0f, yCoord,
													(float) zCoord + 1.0f))) {
								int iNumItemsStored = 0;
								
								if (InventoryUtils.addItemStackToInventory((IInventory) minecartEntity, ejectStack)) {
									iNumItemsStored = iEjectStackSize;
								}
								else {
									iNumItemsStored = iEjectStackSize - ejectStack.stackSize;
								}
								
								if (iNumItemsStored > 0) {
									decrStackSize(iStackIndex, iNumItemsStored);
									
									worldObj.playAuxSFX(BTWEffectManager.ITEM_COLLECTION_POP_EFFECT_ID, xCoord, yCoord, zCoord, 0);
								}
								
								bEjectIntoWorld = false;
								
								break;
							}
						}
					}
				}
			}
			
			if (bEjectIntoWorld) {
				ejectStack(ejectStack);
				
				decrStackSize(iStackIndex, iEjectStackSize);
			}
		}
	}
	
	private void ejectStack(ItemStack stack) {
		float xOffset = worldObj.rand.nextFloat() * 0.1F + 0.45F;
		float yOffset = -0.35F;
		float zOffset = worldObj.rand.nextFloat() * 0.1F + 0.45F;
		
		EntityItem entityitem = (EntityItem) EntityList
				.createEntityOfType(EntityItem.class, worldObj, (float) xCoord + xOffset, (float) yCoord + yOffset, (float) zCoord + zOffset, stack);
		
		entityitem.motionX = 0.0F;
		entityitem.motionY = -0.01F;
		entityitem.motionZ = 0.0F;
		
		entityitem.delayBeforeCanPickup = 10;
		
		worldObj.spawnEntityInWorld(entityitem);
	}
	
	public void attemptToEjectXPFromInv() {
		boolean bShouldResetEjectCount = true;
		
		if (containedXPCount >= XP_EJECT_UNIT_SIZE) {
			int iTargetI = xCoord;
			int iTargetJ = yCoord - 1;
			int iTargetK = zCoord;
			
			boolean bCanEjectIntoWorld = false;
			
			if (worldObj.isAirBlock(iTargetI, iTargetJ, iTargetK)) {
				bCanEjectIntoWorld = true;
			}
			else {
				int iTargetBlockID = worldObj.getBlockId(iTargetI, iTargetJ, iTargetK);
				
				if (iTargetBlockID == BTWBlocks.hopper.blockID) {
					bShouldResetEjectCount = attemptToEjectXPIntoHopper(iTargetI, iTargetJ, iTargetK);
				}
				else if (iTargetBlockID == BTWBlocks.arcaneVessel.blockID) {
					bShouldResetEjectCount = attemptToEjectXPIntoArcaneVessel(iTargetI, iTargetJ, iTargetK);
				}
				else if (WorldUtils.isReplaceableBlock(worldObj, iTargetI, iTargetJ, iTargetK)) {
					bCanEjectIntoWorld = true;
				}
				else {
					Block targetBlock = Block.blocksList[iTargetBlockID];
					
					if (!targetBlock.blockMaterial.isSolid()) {
						bCanEjectIntoWorld = true;
					}
				}
			}
			
			if (bCanEjectIntoWorld) {
				if (hopperXPDropDelayCount <= 0) {
					ejectXPOrb(XP_EJECT_UNIT_SIZE);
					
					containedXPCount -= XP_EJECT_UNIT_SIZE;
				}
				else {
					bShouldResetEjectCount = false;
				}
			}
		}
		
		if (bShouldResetEjectCount) {
			resetXPEjectCount();
		}
		else {
			hopperXPDropDelayCount--;
		}
	}
	
	
	private boolean attemptToEjectXPIntoHopper(int iTargetI, int iTargetJ, int iTargetK) {
		// returns whether the hopper eject counter should be reset 
		
		HopperBlock hopperBlock = (HopperBlock) BTWBlocks.hopper;
		HopperTileEntity targetTileEntity = (HopperTileEntity) worldObj.getBlockTileEntity(iTargetI, iTargetJ, iTargetK);
		
		if (targetTileEntity != null) {
			if (filterItemID == Block.slowSand.blockID) // soul sand filter required
			{
				int iTargetSpaceRemaining = XP_INVENTORY_SPACE - targetTileEntity.containedXPCount;
				
				if (iTargetSpaceRemaining > 0) {
					if (hopperXPDropDelayCount <= 0) {
						int iXPEjected = XP_EJECT_UNIT_SIZE;
						
						if (iTargetSpaceRemaining < iXPEjected) {
							iXPEjected = iTargetSpaceRemaining;
						}
						
						targetTileEntity.containedXPCount += iXPEjected;
						
						containedXPCount -= iXPEjected;
						
						worldObj.playAuxSFX(BTWEffectManager.XP_EJECT_POP_EFFECT_ID, xCoord, yCoord, zCoord, 0);
					}
					else {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private boolean attemptToEjectXPIntoArcaneVessel(int iTargetI, int iTargetJ, int iTargetK) {
		// returns whether the hopper eject counter should be reset 
		
		ArcaneVesselBlock vesselBlock = (ArcaneVesselBlock) BTWBlocks.arcaneVessel;
		ArcaneVesselTileEntity targetTileEntity = (ArcaneVesselTileEntity) worldObj.getBlockTileEntity(iTargetI, iTargetJ, iTargetK);
		
		if (targetTileEntity != null) {
			if (!vesselBlock.getMechanicallyPoweredFlag(worldObj, iTargetI, iTargetJ, iTargetK)) {
				int iTargetSpaceRemaining = targetTileEntity.MAX_CONTAINED_EXPERIENCE - targetTileEntity.getContainedTotalExperience();
				
				if (iTargetSpaceRemaining > 0) {
					if (hopperXPDropDelayCount <= 0) {
						int iXPEjected = XP_EJECT_UNIT_SIZE;
						
						if (iTargetSpaceRemaining < iXPEjected) {
							iXPEjected = iTargetSpaceRemaining;
						}
						
						targetTileEntity.setContainedRegularExperience(targetTileEntity.getContainedRegularExperience() + iXPEjected);
						
						containedXPCount -= iXPEjected;
						
						worldObj.playAuxSFX(BTWEffectManager.XP_EJECT_POP_EFFECT_ID, xCoord, yCoord, zCoord, 0);
					}
					else {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private void resetXPEjectCount() {
		hopperXPDropDelayCount = XP_DELAY_BETWEEN_DROPS + worldObj.rand.nextInt(3);
	}
	
	private void ejectXPOrb(int iXPValue) {
		double xOffset = worldObj.rand.nextDouble() * 0.1D + 0.45D;
		double yOffset = -0.20D;
		double zOffset = worldObj.rand.nextDouble() * 0.1D + 0.45D;
		
		EntityXPOrb xpOrb =
				(EntityXPOrb) EntityList.createEntityOfType(EntityXPOrb.class, worldObj, xCoord + xOffset, yCoord + yOffset, zCoord + zOffset, iXPValue);
		
		xpOrb.motionX = 0D;
		xpOrb.motionY = 0D;
		xpOrb.motionZ = 0D;
		
		worldObj.spawnEntityInWorld(xpOrb);
		
		worldObj.playAuxSFX(BTWEffectManager.HOPPER_EJECT_XP_EFFECT_ID, xCoord, yCoord, zCoord, 0);
	}
	
	public void resetContainedSoulCount() {
		containedSoulCount = 0;
	}
	
	public void incrementContainedSoulCount(int iNumSouls) {
		containedSoulCount += iNumSouls;
	}
	
	private void hopperSoulOverload() {
		worldObj.playAuxSFX(BTWEffectManager.GHAST_SCREAM_EFFECT_ID, xCoord, yCoord, zCoord, 0);
		((HopperBlock) BTWBlocks.hopper).breakHopper(worldObj, xCoord, yCoord, zCoord);
		
		EntityCreature.attemptToPossessCreaturesAroundBlock(this.worldObj, this.xCoord, this.yCoord, this.zCoord, POSSESSION_COUNT_ON_OVERLOAD, PortalBlock.CREATURE_POSSESSION_RANGE);
	}
	
	/*
	 * returns true if the *entire* XP orb is swallowed, false otherwise
	 */
	public boolean attemptToSwallowXPOrb(World world, int i, int j, int k, EntityXPOrb entityXPOrb) {
		int iRemainingSpace = XP_INVENTORY_SPACE - containedXPCount;
		
		if (iRemainingSpace > 0) {
			if (entityXPOrb.xpValue <= iRemainingSpace) {
				containedXPCount += entityXPOrb.xpValue;
				
				entityXPOrb.setDead();
				
				return true;
			}
			else {
				entityXPOrb.xpValue -= iRemainingSpace;
				
				containedXPCount = XP_INVENTORY_SPACE;
			}
		}
		
		return false;
	}
}