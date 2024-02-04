// FCMOD

package btw.block.tileentity;

import btw.block.BTWBlocks;
import btw.block.blocks.CookingVesselBlock;
import btw.inventory.util.InventoryUtils;
import btw.crafting.manager.BulkCraftingManager;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

import java.util.List;

public abstract class CookingVesselTileEntity extends TileEntity
    implements IInventory, TileEntityDataPacketHandler
{
	// constants
	
	protected static final int INVENTORY_SIZE = 27;
	private static final int STACK_SIZE_LIMIT = 64;
	
	private static final int PRIMARY_FIRE_FACTOR = 5;
	private static final int SECONDARY_FIRE_FACTOR = 3;
	
	protected static final int STACK_SIZE_TO_DROP_WHEN_TIPPED = 8;

	private static final double MAX_PLAYER_INTERACTION_DIST = 64D;

	private static final int STOKED_TICKS_TO_COOLDOWN = 20;
	
	// the first number in this equation represents the minimum number of ticks to cook something (with max fire)
    private static final int TIME_TO_COOK = 150 * (PRIMARY_FIRE_FACTOR + (SECONDARY_FIRE_FACTOR * 8 ) );
    
    // local vars

    protected ItemStack contents[];
    protected int cookCounter;
    
    protected int stokedCooldownCounter;
    
    protected boolean containsValidIngredientsForState;
    private boolean forceValidateOnUpdate;
    
    protected int fireUnderType; // 0 = none, 1 = normal, 2 = stoked, -1 = requires validation
    
    public int scaledCookCounter; // used to communicate cook progress from server to client
    
    // variable to support legacy vessels that had their facing set in a different manner
    public int forceFacing;
    
    // state variables used to communicate basic inventory info to the client
    
    public short storageSlotsOccupied;
    
    public CookingVesselTileEntity()
    {
		contents = new ItemStack[INVENTORY_SIZE];

		cookCounter = 0;

		stokedCooldownCounter = 0;
		containsValidIngredientsForState = false;
		forceValidateOnUpdate = true;
		scaledCookCounter = 0;
		fireUnderType = 0;
		forceFacing = -1;

		storageSlotsOccupied = 0;
    }
    
    @Override
    public void readFromNBT( NBTTagCompound nbttagcompound )
    {
        super.readFromNBT(nbttagcompound);
        
        NBTTagList nbttaglist = nbttagcompound.getTagList("Items");

		contents = new ItemStack[getSizeInventory()];
        
        for ( int i = 0; i < nbttaglist.tagCount(); i++ )
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt( i );
            
            int j = nbttagcompound1.getByte( "Slot" ) & 0xff;
            
            if ( j >= 0 && j < contents.length )
            {
				contents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);;
            }
        }
        
        if ( nbttagcompound.hasKey( "m_iFireUnderType" ) )
        {
			fireUnderType = nbttagcompound.getInteger("m_iFireUnderType");
        }
        else
        {
        	// legacy support to force compute of this state is it's not in the NBT

			fireUnderType = -1;

        }        
        
        if ( nbttagcompound.hasKey( "m_iFacing" ) )
        {
        	// legacy: this state is no longer written as it is set in the metadata for the block

			forceFacing = nbttagcompound.getInteger("m_iFacing");
        }
        
        validateInventoryStateVariables();
    }
        
    @Override
    public void writeToNBT( NBTTagCompound nbttagcompound )
    {
        super.writeToNBT(nbttagcompound);
        
        NBTTagList nbttaglist = new NBTTagList();
        
        for (int i = 0; i < contents.length; i++ )
        {
            if (contents[i] != null )
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte( "Slot", (byte)i );
                
                contents[i].writeToNBT(nbttagcompound1);
                
                nbttaglist.appendTag( nbttagcompound1 );
            }
        }

        nbttagcompound.setTag( "Items", nbttaglist );
        
    	nbttagcompound.setBoolean("m_bContainsValidIngrediantsForState", containsValidIngredientsForState);
    	
    	nbttagcompound.setInteger("m_iFireUnderType", fireUnderType);
    }
        
    @Override
    public void updateEntity()
    {
    	if ( worldObj.isRemote )
    	{
    		return;
    	}
    	
		int iBlockID = worldObj.getBlockId( xCoord, yCoord, zCoord );
		
		Block block = Block.blocksList[iBlockID];
		
		if ( block == null || !( block instanceof CookingVesselBlock) )
		{
			// shouldn't happen
			
			return;
		}
		
		CookingVesselBlock cookingBlock = (CookingVesselBlock)block;
		
    	if (forceFacing >= 0 )
    	{
    		// legacy support 
    		
    		cookingBlock.setTiltFacing(worldObj, xCoord, yCoord, zCoord, forceFacing);

			forceFacing = -1;
    	}
    	
    	if (fireUnderType == -1 )
    	{
    		// forced update of fire state for legacy blocks that didn't have this tracking variable
    		
    		validateFireUnderType();
    	}
    	
		if ( !cookingBlock.getMechanicallyPoweredFlag(worldObj, xCoord, yCoord, zCoord) )
		{
			// only cook if upright (not mechanically powered) and if there's a fire under
			
	    	if (fireUnderType > 0 )
	    	{
	        	if (forceValidateOnUpdate)
	        	{
	        		validateContentsForState();

					forceValidateOnUpdate = false;
	        		
	        		// FCTODO: Variable content display currently disabled
	        		// force a rerender of block since the quantity of stuff in its inventory	        		
	            	// changes its appearance,
	            	
	                // worldObj.markBlockRangeForRenderUpdate( xCoord, yCoord, zCoord, xCoord, yCoord, zCoord );
	        	}
	        	
	        	if (fireUnderType == 2 )
	        	{
	    			if (stokedCooldownCounter <= 0 )
	    			{
						cookCounter = 0;
	    			}

					stokedCooldownCounter = STOKED_TICKS_TO_COOLDOWN;
	
					performStokedFireUpdate(getCurrentFireFactor());
	        	}
	        	else if (stokedCooldownCounter > 0 )
	        	{
	    			// this prevents the vessel from going back into regular cook mode if the fire beneath it is 
	    			// momentarily not stoked
	    			
	        		stokedCooldownCounter--;
	    			
	    			if (stokedCooldownCounter <= 0 )
	    			{
	    				// reset the cook counter so that time spent rendering does not translate into cook time

						cookCounter = 0;
	    			}			
	        	}
	        	else
	        	{
					performNormalFireUpdate(getCurrentFireFactor());
	        	}
	    	}
	    	else
	    	{
				cookCounter = 0;
	    	}
    	}
    	else
    	{
			cookCounter = 0;
			
    		int iTiltFacing = cookingBlock.getTiltFacing(worldObj, xCoord, yCoord, zCoord);
    		
			attemptToEjectStackFromInv(iTiltFacing);
		}

		scaledCookCounter = (cookCounter * 1000 ) / TIME_TO_COOK;
	}
    
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        
        nbttagcompound.setShort("s", storageSlotsOccupied);
        
        return new Packet132TileEntityData( xCoord, yCoord, zCoord, 1, nbttagcompound );
    }
    
    //************* FCITileEntityDataPacketHandler ************//
    
    @Override
    public void readNBTFromPacket( NBTTagCompound nbttagcompound )
    {
		storageSlotsOccupied = nbttagcompound.getShort("s");
        
        // force a visual update for the block since the above variable affects it
        
        worldObj.markBlockRangeForRenderUpdate( xCoord, yCoord, zCoord, xCoord, yCoord, zCoord );        
    }
    
    //************* IInventory ************//
    
    @Override
    public int getSizeInventory()
    {
        return INVENTORY_SIZE;
    }

    @Override
    public ItemStack getStackInSlot( int iSlot )
    {
        return contents[iSlot];
    }

    @Override
    public ItemStack decrStackSize( int iSlot, int iAmount )
    {
    	return InventoryUtils.decreaseStackSize(this, iSlot, iAmount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (contents[par1] != null )
        {
            ItemStack itemstack = contents[par1];
			contents[par1] = null;
            
            return itemstack;
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setInventorySlotContents( int iSlot, ItemStack itemstack )
    {
		contents[iSlot] = itemstack;
    	
        if( itemstack != null && itemstack.stackSize > getInventoryStackLimit() )
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
        
        onInventoryChanged();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return STACK_SIZE_LIMIT;
    }

    @Override
    public void onInventoryChanged()
    {
    	super.onInventoryChanged();

		forceValidateOnUpdate = true;
		
    	if ( worldObj != null )
    	{
	        if ( validateInventoryStateVariables() )
	        {
		        worldObj.markBlockForUpdate( xCoord, yCoord, zCoord );
	        }
    	}
    }

    @Override
    public boolean isUseableByPlayer( EntityPlayer entityPlayer )
    {
        if( worldObj.getBlockTileEntity( xCoord, yCoord, zCoord ) != this )
        {
            return false;
        }
        
        int iBlockID = worldObj.getBlockId( xCoord, yCoord, zCoord );
        Block block = Block.blocksList[iBlockID];
        
        if ( !( block instanceof CookingVesselBlock) )
        {
        	return false;
        }
        
        if ( ((CookingVesselBlock)block).isOpenSideBlocked(worldObj, xCoord, yCoord, zCoord) )
        {
        	return false;
        }

        return (entityPlayer.getDistanceSq(
    		(double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D )
				<= MAX_PLAYER_INTERACTION_DIST);
    }
    
    @Override
    public void openChest()
    {
    }

    @Override
    public void closeChest()
    {
    }

    //************* Class Specific Methods ************//
    
    public abstract void validateContentsForState();

    protected abstract BulkCraftingManager getCraftingManager(int iFireType);
    
    /*
     * FireUnderType is 0 for none, 1 for normal, 2 for stoked
     */
    public int getCurrentFireFactor()
    {
    	int iFireFactor = 0;
    	
    	if (fireUnderType > 0 )
    	{
			iFireFactor = PRIMARY_FIRE_FACTOR;
    		
    		if (fireUnderType == 1 )
    		{
        		int tempY = yCoord - 1;
        		
        		for ( int tempX = xCoord - 1; tempX <= xCoord + 1; tempX++ )
        		{
        			for ( int tempZ = zCoord - 1; tempZ <= zCoord + 1; tempZ++ )
        			{
        				if ( tempX != xCoord || tempZ != zCoord )
        				{
        					int iTempBlockID = worldObj.getBlockId( tempX, tempY, tempZ );
        					
        		        	if ( iTempBlockID == Block.fire.blockID ||
        		        		iTempBlockID == BTWBlocks.mediumCampfire.blockID ||
        		        		iTempBlockID == BTWBlocks.largeCampfire.blockID )
        		        	{
        		        		iFireFactor += SECONDARY_FIRE_FACTOR;
        		        	}
        				}
        			}
        		}  		
    		}
    		else
    		{
        		int tempY = yCoord - 1;
        		
        		for ( int tempX = xCoord - 1; tempX <= xCoord + 1; tempX++ )
        		{
        			for ( int tempZ = zCoord - 1; tempZ <= zCoord + 1; tempZ++ )
        			{
        				if ( tempX != xCoord || tempZ != zCoord )
        				{
        		        	if ( worldObj.getBlockId( tempX, tempY, tempZ ) == BTWBlocks.stokedFire.blockID )
        		        	{
        		        		iFireFactor += SECONDARY_FIRE_FACTOR;
        		        	}
        				}
        			}
        		}  		
    		}    		
    	}
    	
    	return iFireFactor;
    }
    
	public void validateFireUnderType()
	{
		if (worldObj == null) {
			return;
		}
		int iNewType = 0;
		
		int iBlockUnderID = worldObj.getBlockId( xCoord, yCoord - 1, zCoord );
		
		if ( iBlockUnderID == Block.fire.blockID ||
			iBlockUnderID == BTWBlocks.mediumCampfire.blockID ||
			iBlockUnderID == BTWBlocks.largeCampfire.blockID )
		{
			iNewType = 1;
		}
		else if ( iBlockUnderID == BTWBlocks.stokedFire.blockID )
		{
			iNewType = 2;
		}
		
		if (iNewType != fireUnderType)
		{
			fireUnderType = iNewType;
			
	    	validateContentsForState();
		}
	}
	
    private void performNormalFireUpdate(int iFireFactor)
    {
		if (containsValidIngredientsForState)
		{
			cookCounter += iFireFactor;
    		
    		if (cookCounter >= TIME_TO_COOK)
    		{
    			attemptToCookNormal();
    			
    	        // reset the cook counter to start the next item

				cookCounter = 0;
    		}
		}
		else
		{
			cookCounter = 0;
		}
    }
    
    private void performStokedFireUpdate(int iFireFactor)
    {
		if (containsValidIngredientsForState)
		{
			cookCounter += iFireFactor;
			
			if (cookCounter >= TIME_TO_COOK)
			{
	    		if ( doesContainExplosives() )
				{
	    			blowUp();
				}
				else 
				{
					attemptToCookStoked();
	    		}

				cookCounter = 0;
			}
		}
		else
		{
			cookCounter = 0;
		}
    }

    protected boolean attemptToCookNormal()
    {
    	return attemptToCookWithManager(getCraftingManager(1));
    }
    
    protected boolean attemptToCookStoked()
    {
    	return attemptToCookWithManager(getCraftingManager(2));
    }
    
    private boolean attemptToCookWithManager(BulkCraftingManager manager)
    {
    	if ( manager != null )
    	{
        	if (manager.getCraftingResult(this) != null )
        	{        		
        		List<ItemStack> outputList = manager.consumeIngredientsAndReturnResult(this);
        		
    			assert( outputList != null && outputList.size() > 0 );
    			
                for ( int listIndex = 0; listIndex < outputList.size(); listIndex++ )
                {
    	    		ItemStack cookedStack = ((ItemStack)outputList.get( listIndex )).copy();
    	    		
    	    		if ( cookedStack != null )
    	    		{
    	    	        if ( !InventoryUtils.addItemStackToInventory(this, cookedStack) )
    	    	        {    	        	
    	    	        	ItemUtils.ejectStackWithRandomOffset(worldObj, xCoord, yCoord + 1, zCoord, cookedStack);
    	    	        }
    	    		}
                }
                
                return true;
    		}        	
    	}
    	
    	return false;
    }
    
    public int getCookProgressScaled( int iScale )
    {
        return (scaledCookCounter * iScale ) / 1000;
    }    
    
    public boolean isCooking()
    {
    	return (scaledCookCounter > 0 );
    }
    
    protected boolean doesContainExplosives()
    {
		if (InventoryUtils.getFirstOccupiedStackOfItem(this, BTWItems.hellfireDust.itemID) >= 0 ||
			InventoryUtils.getFirstOccupiedStackOfItem(this, Block.tnt.blockID) >= 0 ||
			InventoryUtils.getFirstOccupiedStackOfItem(this, Item.gunpowder.itemID) >= 0 ||
			InventoryUtils.getFirstOccupiedStackOfItem(this, BTWItems.blastingOil.itemID) >= 0
		)
		{
			return true;
		}
		
		return false;
    }
    
    private void blowUp()
    {
    	int iHellfireCount = InventoryUtils.countItemsInInventory(this, BTWItems.hellfireDust.itemID, -1);
    	
    	float fExplosionSize = ( iHellfireCount * 10.0F ) / 64.0F;
    	
    	fExplosionSize += (InventoryUtils.countItemsInInventory(this, Item.gunpowder.itemID, -1) * 10.0F ) / 64.0F;
    	
    	fExplosionSize += (InventoryUtils.countItemsInInventory(this, BTWItems.blastingOil.itemID, -1) * 10.0F ) / 64.0F;
    	
    	int iTNTCount = InventoryUtils.countItemsInInventory(this, Block.tnt.blockID, -1);
    	
    	if ( iTNTCount > 0 )
    	{
    		if ( fExplosionSize < 4.0F )
    		{
    			fExplosionSize = 4.0F;
    		}
    		
        	fExplosionSize += InventoryUtils.countItemsInInventory(this, Block.tnt.blockID, -1);
    	}
    	
    	if ( fExplosionSize < 2.0F )
    	{
    		fExplosionSize = 2.0F;
    	}
    	else if ( fExplosionSize > 10.0F )
    	{
    		fExplosionSize = 10.0F;
    	}
    	
    	InventoryUtils.clearInventoryContents(this);
    	
    	worldObj.setBlockWithNotify( xCoord, yCoord, zCoord, 0 );
    	
        worldObj.createExplosion( null, xCoord, yCoord, zCoord, fExplosionSize, true );
    }
    
    private void attemptToEjectStackFromInv(int iTiltFacing)
    {
 		int iStackIndex = InventoryUtils.getFirstOccupiedStackNotOfItem(this, Item.brick.itemID);
		
		if ( iStackIndex >= 0 && iStackIndex < getSizeInventory() )
		{
			ItemStack invStack = getStackInSlot( iStackIndex );
			
			int iEjectStackSize;
			
			if (STACK_SIZE_TO_DROP_WHEN_TIPPED > invStack.stackSize )
			{
				iEjectStackSize = invStack.stackSize;
			}
			else
			{
				iEjectStackSize = STACK_SIZE_TO_DROP_WHEN_TIPPED;
			}
			
			ItemStack ejectStack = new ItemStack( invStack.itemID, iEjectStackSize, invStack.getItemDamage() );
			
			InventoryUtils.copyEnchantments(ejectStack, invStack);

			BlockPos targetPos = new BlockPos( xCoord, yCoord, zCoord );
			
			targetPos.addFacingAsOffset(iTiltFacing);
			
			boolean bEjectIntoWorld = false;
			
			if ( worldObj.isAirBlock(targetPos.x, targetPos.y, targetPos.z) )
			{
				bEjectIntoWorld = true;
			}
			else
			{
				if ( WorldUtils.isReplaceableBlock(worldObj, targetPos.x, targetPos.y, targetPos.z) )
				{
					bEjectIntoWorld = true;
				}
				else
				{				
					int iTargetBlockID = worldObj.getBlockId(targetPos.x, targetPos.y, targetPos.z);
					
					Block targetBlock = Block.blocksList[iTargetBlockID];
					
					if ( !targetBlock.blockMaterial.isSolid() )
					{
						bEjectIntoWorld = true;
					}
				}				
			}
			
			if ( bEjectIntoWorld )
			{
				ejectStack(ejectStack, iTiltFacing);
				
				decrStackSize( iStackIndex, iEjectStackSize );
			}
		}		
    }
    
    private void ejectStack(ItemStack stack, int iFacing)
    {
		Vec3 itemPos = MiscUtils.convertBlockFacingToVector(iFacing);

		itemPos.xCoord *= 0.5F;
		itemPos.yCoord *= 0.5F;
		itemPos.zCoord *= 0.5F;
		
		itemPos.xCoord += ((float)xCoord ) + 0.5F;
		itemPos.yCoord += ((float)yCoord ) + 0.25F;
		itemPos.zCoord += ((float)zCoord ) + 0.5F;		
    	
        EntityItem entityItem = (EntityItem) EntityList.createEntityOfType(EntityItem.class, worldObj, itemPos.xCoord, itemPos.yCoord, itemPos.zCoord, stack );

		Vec3 itemVel = MiscUtils.convertBlockFacingToVector(iFacing);
		
		itemVel.xCoord *= 0.1F;
		itemVel.yCoord *= 0.1F;
		itemVel.zCoord *= 0.1F;
		
        entityItem.motionX = itemVel.xCoord;
        entityItem.motionY = itemVel.yCoord;
        entityItem.motionZ = itemVel.zCoord;
        
        entityItem.delayBeforeCanPickup = 10;
        
        worldObj.spawnEntityInWorld( entityItem );
    }
    
    private boolean validateInventoryStateVariables()
    {
    	boolean bStateChanged = false;
    	
    	short currentSlotsOccupied = (short)( InventoryUtils.getNumOccupiedStacks(this) );
    	
    	if (currentSlotsOccupied != storageSlotsOccupied)
    	{
			storageSlotsOccupied = currentSlotsOccupied;
    		
    		bStateChanged = true;
    	}
    	
    	return bStateChanged;
    }    
}