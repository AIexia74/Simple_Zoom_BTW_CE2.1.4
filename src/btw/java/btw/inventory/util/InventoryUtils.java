// FCMOD

package btw.inventory.util;

import btw.block.BTWBlocks;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Random;

public class InventoryUtils
{
	public static final int IGNORE_METADATA = 32767;
	
    static public void clearInventoryContents(IInventory inventory)
    {
        for ( int iSlot = 0; iSlot < inventory.getSizeInventory(); iSlot++ )
        {
            ItemStack itemstack = inventory.getStackInSlot( iSlot );
            
            if ( itemstack != null )
            {
                inventory.setInventorySlotContents( iSlot, null );
            }
        }
    }
    
    static public void ejectInventoryContents(World world, int i, int j, int k, IInventory inventory)
    {
    	// ripped from BlockChest (with small mods).  This function puts everything in the
    	// inventory into the world
		if (inventory != null) {
    	
        for(int l = 0; l < inventory.getSizeInventory(); l++)
        {
            ItemStack itemstack = inventory.getStackInSlot(l);
            
            if(itemstack == null)
            {
                continue;
            }
            float f = world.rand.nextFloat() * 0.7F + 0.15F;
            float f1 = world.rand.nextFloat() * 0.7F + 0.15F;
            float f2 = world.rand.nextFloat() * 0.7F + 0.15F;
            
            while(itemstack.stackSize > 0) 
            {
                int i1 = world.rand.nextInt(21) + 10;
                
                if(i1 > itemstack.stackSize)
                {
                    i1 = itemstack.stackSize;
                }
                
                itemstack.stackSize -= i1;
                EntityItem entityitem = (EntityItem) EntityList.createEntityOfType(EntityItem.class, world,
                        (double) ((float) i + f), (double) ((float) j + f1), (double) ((float) k + f2),
                        new ItemStack(itemstack.itemID, i1, itemstack.getItemDamage()));

                float f3 = 0.05F;
                
                entityitem.motionX = (float)world.rand.nextGaussian() * f3;
                entityitem.motionY = (float)world.rand.nextGaussian() * f3 + 0.2F;
                entityitem.motionZ = (float)world.rand.nextGaussian() * f3;

                copyEnchantments(entityitem.getEntityItem(), itemstack);
                
                world.spawnEntityInWorld(entityitem);
            }
        }
		}
    }
    
    static public void copyEnchantments(ItemStack destStack, ItemStack sourceStack)
    {
        if ( sourceStack.hasTagCompound())
        {
            destStack.setTagCompound( (NBTTagCompound)sourceStack.getTagCompound().copy() );
        }
    }
    
    static public ItemStack decreaseStackSize(IInventory inventory, int iSlot, int iAmount)
    {
        if ( inventory.getStackInSlot( iSlot ) != null)
        {
            if ( inventory.getStackInSlot( iSlot ).stackSize <= iAmount )
            {
                ItemStack itemstack = inventory.getStackInSlot( iSlot );
                inventory.setInventorySlotContents( iSlot, null );
                
                return itemstack;
            }
            else
            {            
	            ItemStack splitStack = inventory.getStackInSlot( iSlot ).splitStack( iAmount );
	            
	            if ( inventory.getStackInSlot( iSlot ).stackSize == 0 ) 
	            {
	                inventory.setInventorySlotContents( iSlot, null );
	            }
	            else
	            {
	            	inventory.onInventoryChanged();
	            }
	            
	            return splitStack;
            }
        } 
        else
        {
            return null;
        }
    }

    static public int getNumOccupiedStacks(IInventory inventory)
    {
    	return getNumOccupiedStacksInRange(inventory, 0, inventory.getSizeInventory() - 1);
    }
    
    static public int getNumOccupiedStacksInRange(IInventory inventory, int iMinSlot, int iMaxSlot)
    {
    	int iCount = 0;
    	
        for ( int iTempSlot = iMinSlot; iTempSlot <= iMaxSlot; iTempSlot++ )
        {
            if( inventory.getStackInSlot( iTempSlot ) != null )
            {
                iCount++;
            }
        }

        return iCount;
    }
    
    static public int getRandomOccupiedStackInRange(IInventory inventory, Random rand, int iMinSlot, int iMaxSlot)
    {
    	int iNumStacks = getNumOccupiedStacksInRange(inventory, iMinSlot, iMaxSlot);
    	
    	if ( iNumStacks > 0 )
    	{
    		int iRandomStackNum = rand.nextInt( iNumStacks ) + 1;
    		
    		int iStackCount = 0;
    		
            for(int iTempSlot = iMinSlot; iTempSlot <= iMaxSlot; iTempSlot++)
            {
                if( inventory.getStackInSlot( iTempSlot ) != null )
                {
                	iStackCount++;
                	
                	if ( iStackCount >= iRandomStackNum )
                	{
                        return iTempSlot;
                	}                	
                }
            }

    	}
    	
		return -1;
    }
    
    static public int getFirstOccupiedStack(IInventory inventory)
    {
        for ( int iTempSlot = 0; iTempSlot < inventory.getSizeInventory(); iTempSlot++ )
        {
            if ( inventory.getStackInSlot( iTempSlot ) != null )
            {
                return iTempSlot;
            }
        }

        return -1;
    }    
    
    static public int getFirstOccupiedStackOfItem(IInventory inventory, int iItemID)
    {
        for(int i = 0; i < inventory.getSizeInventory(); i++)
        {
            if( inventory.getStackInSlot( i ) != null )
            {
            	if ( inventory.getStackInSlot(i ).getItem().itemID == iItemID )
            	{
            		return i;
            	}
            }
        }

        return -1;
    }
    
    public static ArrayList<Integer> getAllOccupiedStacksOfItem(IInventory inventory, int itemID) {
    	ArrayList<Integer> slots = new ArrayList();
    	
    	for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i) != null) {
            	if (inventory.getStackInSlot(i).getItem().itemID == itemID) {
            		slots.add(i);
            	}
            }
        }
    	
    	return slots;
    }
    
    static public int getFirstOccupiedStackNotOfItem(IInventory inventory, int iNotItemID)
    {
        for ( int iTempSlot = 0; iTempSlot < inventory.getSizeInventory(); iTempSlot++ )
        {
            if ( inventory.getStackInSlot( iTempSlot ) != null )
            {
            	if ( inventory.getStackInSlot(iTempSlot ).getItem().itemID != iNotItemID )
            	{
            		return iTempSlot;
            	}
            }
        }

        return -1;
    }
    
    static public int getFirstEmptyStack(IInventory inventory)
    {
    	return getFirstEmptyStackInSlotRange(inventory, 0, inventory.getSizeInventory() - 1);
    }
    
    static public int getFirstEmptyStackInSlotRange(IInventory inventory, int iMinSlotIndex, int iMaxSlotIndex)
    {
        for( int iTempSlot = iMinSlotIndex; iTempSlot <= iMaxSlotIndex; iTempSlot++ )
        {
            if( inventory.getStackInSlot( iTempSlot ) == null )
            {
                return iTempSlot;
            }
        }

        return -1;
    }
    
    static public int countItemsInInventory
    ( 
		IInventory inventory, 
		int iItemID, 
		int iItemDamage // m_iIgnoreMetadata to disregard
	)
    {
    	return countItemsInInventory(inventory, iItemID, iItemDamage, false);
    }
    
    static public int countItemsInInventory
    ( 
		IInventory inventory, 
		int iItemID, 
		int iItemDamage, // m_iIgnoreMetadata to disregard
		boolean bMetaDataExclusive 
	)
    {
    	int itemCount = 0;
    	
        for(int i = 0; i < inventory.getSizeInventory(); i++)
        {
        	ItemStack tempStack = inventory.getStackInSlot( i );
        	
            if( tempStack != null )
            {
            	if ( tempStack.getItem().itemID == iItemID )
            	{
            		if (iItemDamage == IGNORE_METADATA ||
                        ( (!bMetaDataExclusive) && tempStack.getItemDamage() == iItemDamage ) ||
                        ( bMetaDataExclusive && tempStack.getItemDamage() != iItemDamage ) )
            		{
            			itemCount += inventory.getStackInSlot( i ).stackSize;
            		}
            	}
            }
        }

        return itemCount;
    }
    
    static public boolean consumeItemsInInventory
    ( 
		IInventory inventory, 
		int iShiftedItemIndex,
		int iItemDamage, // m_iIgnoreMetadata to disregard
		int iItemCount 
	)
    {
    	return consumeItemsInInventory(inventory, iShiftedItemIndex, iItemDamage, iItemCount, false);
    }
    
    static public boolean consumeItemsInInventory
    ( 
		IInventory inventory, 
		int iShiftedItemIndex,
		int iItemDamage, // m_iIgnoreMetadata to disregard
		int iItemCount,
		boolean bMetaDataExclusive
	)
    {
        for ( int iSlot = 0; iSlot < inventory.getSizeInventory(); iSlot++ )
        {
            ItemStack tempItemStack = inventory.getStackInSlot( iSlot );
            
            if ( tempItemStack != null )
            {
            	Item tempItem =  tempItemStack.getItem();
            	
            	if ( tempItem.itemID == iShiftedItemIndex )
            	{
            		if (iItemDamage == IGNORE_METADATA ||
                        ( (!bMetaDataExclusive) && tempItemStack.getItemDamage() == iItemDamage ) ||
                        ( bMetaDataExclusive && tempItemStack.getItemDamage() != iItemDamage ) )
            		{
	            		if ( tempItemStack.stackSize >= iItemCount )
	            		{
	            			decreaseStackSize(inventory, iSlot, iItemCount);
	            			
	            			return true;            			
	            		}
	            		else
	            		{
	            			iItemCount -= tempItemStack.stackSize;
	            			
	                        inventory.setInventorySlotContents( iSlot, null );
	            		}
            		}
            	}
            }
        }
        
        return false;
    }
    
    static public boolean addSingleItemToInventory(IInventory inventory, int iItemShiftedIndex, int itemDamage)
    {
    	ItemStack itemStack = new ItemStack( iItemShiftedIndex, 1, itemDamage );

    	return addItemStackToInventory(inventory, itemStack);
    }
    
    static public boolean addItemStackToInventory(IInventory inventory, ItemStack stack)
    {
    	return addItemStackToInventoryInSlotRange(inventory, stack, 0, inventory.getSizeInventory() - 1);
    }

    /*
     * returns true if the full stack has been stored
     */
    static public boolean addItemStackToInventoryInSlotRange(IInventory inventory, ItemStack itemstack,
                                                             int iMinSlotIndex, int iMaxSlotIndex)
	{
	    if ( !itemstack.isItemDamaged() )
	    {
	        if ( attemptToMergeWithExistingStacksInSlotRange(inventory, itemstack, iMinSlotIndex, iMaxSlotIndex) )
	        {
	            return true;
	        }
	    }
	    
        return attemptToPlaceInEmptySlotInSlotRange(inventory, itemstack, iMinSlotIndex, iMaxSlotIndex);
	}
    
    public static int getMaxNumberOfItemsForTransferInRange(IInventory inventory, ItemStack itemStack, int countLimit, int minSlotIndex, int maxSlotIndex) {
    	int totalSlotCount = maxSlotIndex - minSlotIndex + 1;
    	int maximumEmptySlotCount = totalSlotCount - getNumOccupiedStacksInRange(inventory, minSlotIndex, maxSlotIndex);
    	
    	int maxItemsForEmptySlots = maximumEmptySlotCount * itemStack.getMaxStackSize();
    	
    	if (maxItemsForEmptySlots >= countLimit) {
    		return countLimit;
    	}
    	else {
    		int itemCountToMerge = countLimit - maxItemsForEmptySlots;
    		
    		ArrayList<Integer> slotsAvailableToMerge = getAllOccupiedStacksOfItem(inventory, itemStack.itemID);
    		
    		int availableItems = 0;
    		
    		for (int slotIndex : slotsAvailableToMerge) {
    			ItemStack slotStack = inventory.getStackInSlot(slotIndex);
    			availableItems += slotStack.getMaxStackSize() - slotStack.stackSize;
    		}
    		
    		if (availableItems >= itemCountToMerge) {
    			return countLimit;
    		}
    		else {
    			return countLimit - (itemCountToMerge - availableItems);
    		}
    	}
    }

    /*
     * This function differs from a normal add in that it checks for double chests and deposits
     * first to the one that normally occupied the top GUI position
     */
    static public boolean addItemStackToChest(TileEntityChest chest, ItemStack itemstack)
    {
    	World world = chest.worldObj;
    	
    	// check for a chest neigboring on the first
    	
    	BlockPos secondChestPos;
    	
		for ( int iTempFacing = 2; iTempFacing <= 5; iTempFacing++ )
		{
			secondChestPos = new BlockPos( chest.xCoord, chest.yCoord, chest.zCoord );
			
			secondChestPos.addFacingAsOffset(iTempFacing);
			
			int iSecondBlockID = world.getBlockId(secondChestPos.x, secondChestPos.y, secondChestPos.z);
			
			if ( iSecondBlockID == Block.chest.blockID || iSecondBlockID == BTWBlocks.chest.blockID )
			{
				TileEntityChest secondChest = (TileEntityChest)world.getBlockTileEntity(secondChestPos.x, secondChestPos.y, secondChestPos.z);
				
				if ( secondChest != null )
				{
					// determine which chest is the "top" one when displayed in the GUI, and use it as the primary chest
					
					if ( chest.xCoord < secondChest.xCoord || chest.zCoord < secondChest.zCoord )
			    	{
						return addItemStackToDoubleInventory((IInventory)chest, (IInventory)secondChest, itemstack);
			    	}
			    	else
			    	{
						return addItemStackToDoubleInventory((IInventory)secondChest, (IInventory)chest, itemstack);
			    	}    	
				}
			}
		}

		// a second chest wasn't found, so just add the stack to the first
		
		return addItemStackToInventory((IInventory)chest, itemstack);
    }
    
	//------------- Private Methods ------------//
	
    static private boolean canStacksMerge(ItemStack sourceStack, ItemStack destStack, int iInventoryStackSizeLimit)
    {
        return ( destStack != null && 
        	destStack.itemID == sourceStack.itemID && 
        	destStack.isStackable() && 
        	destStack.stackSize < destStack.getMaxStackSize() && 
        	destStack.stackSize < iInventoryStackSizeLimit && 
    		( !destStack.getHasSubtypes() || destStack.getItemDamage() == sourceStack.getItemDamage() ) &&
			ItemStack.areItemStackTagsEqual( destStack, sourceStack ) );
    }
    
    static private int findSlotToMergeItemStackInSlotRange(IInventory inventory, ItemStack itemStack, int iMinSlotIndex, int iMaxSlotIndex)
    {
    	int iInventoryStackLimit = inventory.getInventoryStackLimit();
    	
        for ( int i = iMinSlotIndex; i <= iMaxSlotIndex; i++ )
        {
            ItemStack tempStack = inventory.getStackInSlot( i );
            
            if ( canStacksMerge(itemStack, tempStack, iInventoryStackLimit) )
            {
                return i;
            }
        }

        return -1;
    }
    
    /*
     * returns true if the full stack has been stored
     */
    static private boolean attemptToMergeWithExistingStacks(IInventory inventory, ItemStack stack)
    {
    	return attemptToMergeWithExistingStacksInSlotRange(inventory, stack, 0, inventory.getSizeInventory() - 1);
    }
    
    /*
     * returns true if the full stack has been stored
     */
    static private boolean attemptToMergeWithExistingStacksInSlotRange(IInventory inventory, ItemStack stack, int iMinSlotIndex, int iMaxSlotIndex)
    {
        int iMergeSlot = findSlotToMergeItemStackInSlotRange(inventory, stack, iMinSlotIndex, iMaxSlotIndex);
        
    	while ( iMergeSlot >= 0 )
        {
	        int iNumItemsToStore = stack.stackSize;
	        
	        ItemStack tempStack = inventory.getStackInSlot( iMergeSlot );        
	        
	        if ( iNumItemsToStore > tempStack.getMaxStackSize() - tempStack.stackSize )
	        {
	            iNumItemsToStore = tempStack.getMaxStackSize() - tempStack.stackSize;
	        }
	        
	        if ( iNumItemsToStore > inventory.getInventoryStackLimit() - tempStack.stackSize )
	        {
	            iNumItemsToStore = inventory.getInventoryStackLimit() - tempStack.stackSize;
	        }
	        
	        if ( iNumItemsToStore == 0 )
	        {
	            return false;
	        } 
	        else
	        {
	            stack.stackSize -= iNumItemsToStore;
	            
	            tempStack.stackSize += iNumItemsToStore;
	            inventory.setInventorySlotContents( iMergeSlot, tempStack );
	            
	            if ( stack.stackSize <= 0 )
	            {
	            	return true;
	            }	        	
	        }
	        
	        iMergeSlot = findSlotToMergeItemStackInSlotRange(inventory, stack, iMinSlotIndex, iMaxSlotIndex);
        }
        	
    	return false;
    }
    
    /*
     * returns true if the full stack has been stored
     */
    static private boolean attemptToPlaceInEmptySlot(IInventory inventory, ItemStack stack)
    {
    	return attemptToPlaceInEmptySlotInSlotRange(inventory, stack, 0, inventory.getSizeInventory() - 1);
    }
    
    /*
     * returns true if the full stack has been stored
     */
    static private boolean attemptToPlaceInEmptySlotInSlotRange(IInventory inventory, ItemStack stack, int iMinSlotIndex, int iMaxSlotIndex)
    {
        int iItemID = stack.itemID;
        int iItemDamage = stack.getItemDamage();
        
	    int iEmptySlot = getFirstEmptyStackInSlotRange(inventory, iMinSlotIndex, iMaxSlotIndex);
	  
	    while ( iEmptySlot >= 0 )
	    {
	        int iNumItemsToStore = stack.stackSize;
	        
	        if ( iNumItemsToStore > inventory.getInventoryStackLimit() )
	        {
	            iNumItemsToStore = inventory.getInventoryStackLimit();	            
	        }
	        
        	ItemStack newStack = new ItemStack( iItemID, iNumItemsToStore, iItemDamage );
        	
        	copyEnchantments(newStack, stack);
        	
        	inventory.setInventorySlotContents( iEmptySlot, newStack );
        	
        	stack.stackSize -= iNumItemsToStore;
        	
            if ( stack.stackSize <= 0 )
            {
            	return true;
            }
        	
        	iEmptySlot = getFirstEmptyStackInSlotRange(inventory, iMinSlotIndex, iMaxSlotIndex);
	    }
	    
        return false;
    }
    
    /*
     * returns true if the full stack has been stored
     */
    static private boolean addItemStackToDoubleInventory(IInventory primaryInventory, IInventory secondaryInventory, ItemStack stack)
    {
        if ( !stack.isItemDamaged() )
        {
            if ( attemptToMergeWithExistingStacks(primaryInventory, stack) )
            {
            	return true;
            }
            
            if ( attemptToMergeWithExistingStacks(secondaryInventory, stack) )
            {
            	return true;
            }            
        }

        if ( attemptToPlaceInEmptySlot(primaryInventory, stack) )
        {
        	return true;
        }
        
        return attemptToPlaceInEmptySlot(secondaryInventory, stack);
    }
}
