// FCMOD

package btw.inventory.container;

import btw.block.BTWBlocks;
import btw.inventory.inventories.InfernalEnchanterInventory;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Iterator;
import java.util.Random;

public class InfernalEnchanterContainer extends Container
{
    public IInventory tableInventory;
    
    private World localWorld;
    
    private int blockX;
    private int blockY;
    private int blockZ;
    
    private static final double MAX_INTERACTION_DISTANCE = 8.0D;
    private static final double MAX_INTERACTION_DISTANCE_SQ = (MAX_INTERACTION_DISTANCE * MAX_INTERACTION_DISTANCE);
    
    private static final int SLOT_SCREEN_WIDTH = 18;
    private static final int SLOT_SCREEN_HEIGHT = 18;
    
    private static final int SCROLL_SLOT_SCREEN_POS_X = 17;
    private static final int SCROLL_SLOT_SCREEN_POS_Y = 37;
    
    private static final int ITEM_SLOT_SCREEN_POS_X = 17;
    private static final int ITEM_SLOT_SCREEN_POS_Y = 75;
    
    private static final int PLAYER_INVENTORY_SCREEN_POS_X = 8;
    private static final int PLAYER_INVENTORY_SCREEN_POS_Y = 129;
    
    private static final int PLAYER_HOTBAR_SCREEN_POS_Y = (PLAYER_INVENTORY_SCREEN_POS_Y + 58 );
    
	private static final int HORIZONTAL_BOOK_SHELF_CHECK_DISTANCE = 8;
	private static final int VERTICAL_POSITIVE_BOOK_SHELF_CHECK_DISTANCE = 8;
	private static final int VERTICAL_NEGATIVE_BOOK_SHELF_CHECK_DISTANCE = 8;
	
    public static final int MAX_ENCHANTMENT_POWER_LEVEL = 5;
    
    public int currentEnchantmentLevels[];
    
    public int maxSurroundingBookshelfLevel;
    public int lastMaxSurroundingBookshelfLevel;
    
    public long nameSeed;
    
    Random rand;

    /*
     * world, i, j, & k are only relevant on the server
     */
    public InfernalEnchanterContainer(InventoryPlayer playerInventory, World world, int i, int j, int k )
    {
		localWorld = world;

		blockX = i;
		blockY = j;
		blockZ = k;
        
        rand = new Random();

		nameSeed = rand.nextLong();

		tableInventory = new InfernalEnchanterInventory(this, "fcInfernalEnchanterInv", 2 );

		currentEnchantmentLevels = new int[MAX_ENCHANTMENT_POWER_LEVEL];
        
        resetEnchantingLevels();

		maxSurroundingBookshelfLevel = 0;
		lastMaxSurroundingBookshelfLevel = 0;
        
        // add scroll slot
        
        addSlotToContainer( new Slot(tableInventory, 0, SCROLL_SLOT_SCREEN_POS_X, SCROLL_SLOT_SCREEN_POS_Y));

        addSlotToContainer( new Slot(tableInventory, 1, ITEM_SLOT_SCREEN_POS_X, ITEM_SLOT_SCREEN_POS_Y));
        
        // add item slot
        
        // add slots for the player inventory
        
        for ( int tempSlotY = 0; tempSlotY < 3; tempSlotY++ )
        {
            for ( int tempSlotX = 0; tempSlotX < 9; tempSlotX++ )
            {
            	addSlotToContainer( new Slot(playerInventory,
            		tempSlotX + tempSlotY * 9 + 9,
											 PLAYER_INVENTORY_SCREEN_POS_X + tempSlotX * SLOT_SCREEN_WIDTH,
											 PLAYER_INVENTORY_SCREEN_POS_Y + tempSlotY * SLOT_SCREEN_HEIGHT));
            }
        }

        // add slots for the player hot-bar
        
        for ( int tempSlotX = 0; tempSlotX < 9; tempSlotX++ )
        {
        	addSlotToContainer( new Slot(playerInventory, tempSlotX,
										 PLAYER_INVENTORY_SCREEN_POS_X + tempSlotX * SLOT_SCREEN_WIDTH, PLAYER_HOTBAR_SCREEN_POS_Y));
        }
        
        if ( world != null && !world.isRemote )
        {
        	checkForSurroundingBookshelves();
        }
    }

	@Override
    public void onCraftMatrixChanged( IInventory inventory )
    {
        if (inventory == tableInventory)
        {
			nameSeed = rand.nextLong();
	
    		resetEnchantingLevels();
    		
        	computeCurrentEnchantmentLevels();
        	
        	if (localWorld != null && !localWorld.isRemote )
        	{	        	
	            detectAndSendChanges();
        	}
        }
    }

	@Override
    public void onCraftGuiClosed( EntityPlayer player )
    {
        super.onCraftGuiClosed( player );
        
        if (localWorld == null || localWorld.isRemote )
        {
            return;
        }
        
        for (int i = 0; i < tableInventory.getSizeInventory(); i++ )
        {
            ItemStack itemstack = tableInventory.getStackInSlot(i);
            
            if ( itemstack != null )
            {
                player.dropPlayerItem( itemstack );
            }
        }
    }
	
	@Override
    public boolean canInteractWith( EntityPlayer entityplayer )
    {
		if (localWorld == null || localWorld.isRemote )
		{
			return true;
		}
		
        if (localWorld.getBlockId(blockX, blockY, blockZ) !=
			BTWBlocks.infernalEnchanter.blockID )
        {
            return false;
        }
        
        return entityplayer.getDistanceSq((double) blockX + 0.5D, (double) blockY + 0.5D,
										  (double) blockZ + 0.5D) <= MAX_INTERACTION_DISTANCE_SQ;
    }

	@Override
    public ItemStack transferStackInSlot( EntityPlayer player, int iSlotIndex )
    {
        ItemStack clickedStack = null;
        
        Slot slot = (Slot)inventorySlots.get( iSlotIndex );

        if ( slot != null && slot.getHasStack() )
        {
            ItemStack processedStack = slot.getStack();
            clickedStack = processedStack.copy();

            if ( iSlotIndex <= 1 )
            {
            	// scroll or item stack: transfer to player inv
            	
                if ( !mergeItemStack( processedStack, 2, 38, true ) )
                {
                    return null;
                }
            }
            else if ( processedStack.getItem().itemID == BTWItems.arcaneScroll.itemID )
            {
            	// scroll item in player inv
            	
                if ( !mergeItemStack( processedStack, 0, 1, false ) )
                {
                    return null;
                }
            }
            else if (getMaximumEnchantmentCost(processedStack) > 0 )
            {
            	// enchantable item 
            	
                if ( !mergeItemStack( processedStack, 1, 2, false ) )
                {
                    return null;
                }
            }
            else if ( iSlotIndex >= 2 && iSlotIndex < 29 )
            {
                if ( !mergeItemStack( processedStack, 29, 38, false ) )
                {
                    return null;
                }
            }
            else if ( iSlotIndex >= 29 && iSlotIndex < 38 && !mergeItemStack( processedStack, 2, 29, false ) )
            {
                return null;
            }

            if ( processedStack.stackSize == 0 )
            {
                slot.putStack( null );
            }
            else
            {
                slot.onSlotChanged();
            }

            if ( processedStack.stackSize != clickedStack.stackSize )
            {
                slot.onPickupFromSlot( player, processedStack );

            	if (localWorld != null && !localWorld.isRemote )
                {
                	detectAndSendChanges();
                }

            }
            else
            {
                return null;
            }
        }

        return clickedStack;
    }
	
	private void checkForSurroundingBookshelves()
	{
		int iBookshelfCount = 0;
		
		for (int iTempI = blockX - HORIZONTAL_BOOK_SHELF_CHECK_DISTANCE; iTempI <= blockX + HORIZONTAL_BOOK_SHELF_CHECK_DISTANCE; iTempI++ )
		{
			for (int iTempJ = blockY - VERTICAL_NEGATIVE_BOOK_SHELF_CHECK_DISTANCE; iTempJ <= blockY + VERTICAL_POSITIVE_BOOK_SHELF_CHECK_DISTANCE; iTempJ++ )
			{
				for (int iTempK = blockZ - HORIZONTAL_BOOK_SHELF_CHECK_DISTANCE; iTempK <= blockZ + HORIZONTAL_BOOK_SHELF_CHECK_DISTANCE; iTempK++ )
				{
					if ( isValidBookshelf(iTempI, iTempJ, iTempK) )
					{
						iBookshelfCount++;
					}
				}
			}
		}

		maxSurroundingBookshelfLevel = iBookshelfCount;
	}
	
	private boolean isValidBookshelf(int i, int j, int k)
	{
		int iBlockID = localWorld.getBlockId(i, j, k);
		
		if ( iBlockID == Block.bookShelf.blockID )
		{
			// check around the bookshelf for an empty block to provide access
			
			if (localWorld.isAirBlock(i + 1, j, k) ||
				localWorld.isAirBlock(i - 1, j, k) ||
				localWorld.isAirBlock(i, j, k + 1) ||
				localWorld.isAirBlock(i, j, k - 1) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	private void setCurrentEnchantingLevels(int iMaxPowerLevel, int iCostMultiplier, int iMaxBaseCostForItem)
	{
		resetEnchantingLevels();
		
		if ( iMaxPowerLevel == 1 )
		{
			currentEnchantmentLevels[0] = 30;
		}
		else if ( iMaxPowerLevel == 2 )
		{
			currentEnchantmentLevels[0] = 15;
			currentEnchantmentLevels[1] = 30;
		}
		else if ( iMaxPowerLevel == 3 )
		{
			currentEnchantmentLevels[0] = 10;
			currentEnchantmentLevels[1] = 20;
			currentEnchantmentLevels[2] = 30;
		}
		else if ( iMaxPowerLevel == 4 )
		{
			currentEnchantmentLevels[0] = 8;
			currentEnchantmentLevels[1] = 15;
			currentEnchantmentLevels[2] = 23;
			currentEnchantmentLevels[3] = 30;
		}
		else if ( iMaxPowerLevel == 5 )
		{
			currentEnchantmentLevels[0] = 6;
			currentEnchantmentLevels[1] = 12;
			currentEnchantmentLevels[2] = 18;
			currentEnchantmentLevels[3] = 24;
			currentEnchantmentLevels[4] = 30;
		}
		
		int iCostIncrement = ( ( iCostMultiplier - 1 ) * 30 );
		
        for (int iTemp = 0; iTemp < MAX_ENCHANTMENT_POWER_LEVEL; iTemp++ )
        {
        	if (currentEnchantmentLevels[iTemp] > 0 )
        	{
        		if (iMaxBaseCostForItem < currentEnchantmentLevels[iTemp] )
        		{
					currentEnchantmentLevels[iTemp] = 0;
        		}
        		else
        		{
					currentEnchantmentLevels[iTemp] += iCostIncrement;
        		}
        	}
        }        
	}
	
	private void resetEnchantingLevels()
	{
		// clear the current levels
		
        for (int iTemp = 0; iTemp < MAX_ENCHANTMENT_POWER_LEVEL; iTemp++ )
        {
			currentEnchantmentLevels[iTemp] = 0;
        }        
	}
	
	private void computeCurrentEnchantmentLevels()
	{
		ItemStack scrollStack = tableInventory.getStackInSlot(0);
		
		if ( scrollStack != null && scrollStack.getItem().itemID == BTWItems.arcaneScroll.itemID )
		{
			ItemStack itemToEnchantStack = tableInventory.getStackInSlot(1);
			
			if ( itemToEnchantStack != null )
			{
				int iMaxEnchantmentCost = getMaximumEnchantmentCost(itemToEnchantStack);
				
				if ( iMaxEnchantmentCost > 0 )
				{
					int iEnchantmentIndex = scrollStack.getItemDamage();
					
					if ( iEnchantmentIndex >= Enchantment.enchantmentsList.length || Enchantment.enchantmentsList[iEnchantmentIndex] == null )
					{
						return;
					}
					
					if ( isEnchantmentAppropriateForItem(iEnchantmentIndex, itemToEnchantStack) )
					{
						if ( !doesEnchantmentConflictWithExistingOnes(iEnchantmentIndex, itemToEnchantStack) )
						{
							int iMaxNumberOfItemEnchants = getMaximumNumberOfEnchantments(itemToEnchantStack);
							int iCurrentNumberOfItemEnchants = 0;
							
					        NBTTagList enchantmentTagList = itemToEnchantStack.getEnchantmentTagList();
					        
					        if ( enchantmentTagList != null )
					        {
					        	iCurrentNumberOfItemEnchants = itemToEnchantStack.getEnchantmentTagList().tagCount();			        	
					        }
					        
				        	if ( iCurrentNumberOfItemEnchants < iMaxNumberOfItemEnchants )
				        	{
				        		// the item may be enchanted
				        		
				        		setCurrentEnchantingLevels(getMaxEnchantmentPowerLevel(iEnchantmentIndex, itemToEnchantStack),
			        				iCurrentNumberOfItemEnchants + 1, getMaximumEnchantmentCost(itemToEnchantStack));
				        	}
						}
					}
				}
			}			
		}
	}
	
	private int getMaximumEnchantmentCost(ItemStack itemStack)
	{
		return itemStack.getItem().getInfernalMaxEnchantmentCost();
	}
	
	private int getMaximumNumberOfEnchantments(ItemStack itemStack)
	{
		return itemStack.getItem().getInfernalMaxNumEnchants();
	}
	
	private boolean isEnchantmentAppropriateForItem(int iEnchantmentIndex, ItemStack itemStack)
	{
		// client
		return Enchantment.enchantmentsList[iEnchantmentIndex].canApply( itemStack );
		// server
		//return Enchantment.enchantmentsList[iEnchantmentIndex].func_92089_a( itemStack );		
	}
	
	private boolean doesEnchantmentConflictWithExistingOnes(int iEnchantmentIndex, ItemStack itemStack)
	{
        NBTTagList enchantmentTagList = itemStack.getEnchantmentTagList();
        
        if ( enchantmentTagList != null )
        {
        	int iCurrentNumberOfItemEnchants = itemStack.getEnchantmentTagList().tagCount();
        	
            for (int iTemp = 0; iTemp < iCurrentNumberOfItemEnchants; iTemp++)
            {
                int iTempEnchantmentIndex = ((NBTTagCompound)enchantmentTagList.tagAt(iTemp)).getShort("id");

                if ( iTempEnchantmentIndex == iEnchantmentIndex )
                {
                	// enchantments always conflict with themselves
                	
                    return true;
                }
                else if ( ( iEnchantmentIndex == Enchantment.silkTouch.effectId && iTempEnchantmentIndex == Enchantment.fortune.effectId ) || 
            		( iEnchantmentIndex == Enchantment.fortune.effectId && iTempEnchantmentIndex == Enchantment.silkTouch.effectId ) )
                {
                	return true;
                }
            }
        }
        
		return false;
	}
	
	private int getMaxEnchantmentPowerLevel(int iEnchantmentIndex, ItemStack itemStack)
	{
        if ( iEnchantmentIndex == Enchantment.respiration.effectId && itemStack.getItem().itemID == BTWItems.plateHelmet.itemID )
        {
        	return 5;
        }
        
        return Enchantment.enchantmentsList[iEnchantmentIndex].getMaxLevel();
	}
	
	/*
	 * Return true if the item was successfully enchanted
	 */
	@Override
	public boolean enchantItem( EntityPlayer player, int iButtonIndex )
	{
		if (localWorld == null || localWorld.isRemote )
		{
			return true;
		}
		
    	int iButtonEnchantmentLevel = currentEnchantmentLevels[iButtonIndex];
    	
    	if ( iButtonEnchantmentLevel > 0 )
    	{    	
    		boolean bPlayerCapable = iButtonEnchantmentLevel <= player.experienceLevel && iButtonEnchantmentLevel <= maxSurroundingBookshelfLevel;
    		
    		if ( bPlayerCapable )
    		{
    			ItemStack scrollStack = tableInventory.getStackInSlot(0);
    			
    			if ( scrollStack != null && scrollStack.getItem().itemID == BTWItems.arcaneScroll.itemID )
    			{
    				ItemStack itemToEnchantStack = tableInventory.getStackInSlot(1);
    				
    				if ( itemToEnchantStack != null )
    				{
    					int iEnchantmentIndex = scrollStack.getItemDamage();
    					
    					if ( iEnchantmentIndex >= Enchantment.enchantmentsList.length || Enchantment.enchantmentsList[iEnchantmentIndex] == null )
    					{
    						return false;
    					}
    					
    					itemToEnchantStack.addEnchantment( Enchantment.enchantmentsList[iEnchantmentIndex], iButtonIndex + 1 );
    					
                        player.addExperienceLevel( -iButtonEnchantmentLevel );
                        
                		tableInventory.decrStackSize(0, 1);
    					
                        onCraftMatrixChanged(tableInventory);
                        
                        localWorld.playSoundAtEntity(player, "ambient.weather.thunder", 1.0F, localWorld.rand.nextFloat() * 0.4F + 0.8F);

                        return true;
					}			
    			}
    		}
    	}
    	
		return false;
	}
	
	@Override
    public void addCraftingToCrafters( ICrafting craftingInterface )
    //public void onCraftGuiOpened( ICrafting craftingInterface )
    {
        super.addCraftingToCrafters( craftingInterface );
        //super.onCraftGuiOpened( craftingInterface );
        
        craftingInterface.sendProgressBarUpdate(this, 0, maxSurroundingBookshelfLevel);
    }
	
	@Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        
        Iterator iterator = crafters.iterator();

        do
        {
            if (!iterator.hasNext())
            {
                break;
            }

            ICrafting icrafting = (ICrafting)iterator.next();

            if (lastMaxSurroundingBookshelfLevel != maxSurroundingBookshelfLevel)
            {
                icrafting.sendProgressBarUpdate(this, 0, maxSurroundingBookshelfLevel);
            }
        }
        while (true);

		lastMaxSurroundingBookshelfLevel = maxSurroundingBookshelfLevel;
    }

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void updateProgressBar( int iVariableIndex, int iValue )
    {
        if ( iVariableIndex == 0 )
        {
			maxSurroundingBookshelfLevel = iValue;
        }
    }
}

