package net.minecraft.src;

import btw.block.blocks.FurnaceBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class TileEntityFurnace extends TileEntity implements ISidedInventory
{
    private static final int[] field_102010_d = new int[] {0};
    private static final int[] field_102011_e = new int[] {2, 1};
    private static final int[] field_102009_f = new int[] {1};

    /**
     * The ItemStacks that hold the items currently being used in the furnace
     */
    protected ItemStack[] furnaceItemStacks = new ItemStack[3];

    /** The number of ticks that the furnace will keep burning */
    public int furnaceBurnTime = 0;

    /**
     * The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for
     */
    public int currentItemBurnTime = 0;

    /** The number of ticks that the current item has been cooking for */
    public int furnaceCookTime = 0;
    private String field_94130_e;

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.furnaceItemStacks.length;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return this.furnaceItemStacks[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.furnaceItemStacks[par1] != null)
        {
            ItemStack var3;

            if (this.furnaceItemStacks[par1].stackSize <= par2)
            {
                var3 = this.furnaceItemStacks[par1];
                this.furnaceItemStacks[par1] = null;
                return var3;
            }
            else
            {
                var3 = this.furnaceItemStacks[par1].splitStack(par2);

                if (this.furnaceItemStacks[par1].stackSize == 0)
                {
                    this.furnaceItemStacks[par1] = null;
                }

                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.furnaceItemStacks[par1] != null)
        {
            ItemStack var2 = this.furnaceItemStacks[par1];
            this.furnaceItemStacks[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.furnaceItemStacks[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return this.isInvNameLocalized() ? this.field_94130_e : "container.furnace";
    }

    /**
     * If this returns false, the inventory name will be used as an unlocalized name, and translated into the player's
     * language. Otherwise it will be used directly.
     */
    public boolean isInvNameLocalized()
    {
        return this.field_94130_e != null && this.field_94130_e.length() > 0;
    }

    public void func_94129_a(String par1Str)
    {
        this.field_94130_e = par1Str;
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
        this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.furnaceItemStacks.length)
            {
                this.furnaceItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

        this.furnaceBurnTime = par1NBTTagCompound.getShort("BurnTime");
        this.furnaceCookTime = par1NBTTagCompound.getShort("CookTime");
        this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);

        if (par1NBTTagCompound.hasKey("CustomName"))
        {
            this.field_94130_e = par1NBTTagCompound.getString("CustomName");
        }
        
        // FCMOD: Code added to track extended burn times
        if ( par1NBTTagCompound.hasKey( "fcBurnTimeEx" ) )
        {
	        furnaceBurnTime = par1NBTTagCompound.getInteger( "fcBurnTimeEx" );
	        furnaceCookTime = par1NBTTagCompound.getInteger( "fcCookTimeEx" );
	        
	        if ( par1NBTTagCompound.hasKey( "fcItemBurnTimeEx" ) )
	        {
	            currentItemBurnTime = par1NBTTagCompound.getInteger( "fcItemBurnTimeEx" );
	        }
        }
        // END FCMOD
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("BurnTime", (short)this.furnaceBurnTime);
        par1NBTTagCompound.setShort("CookTime", (short)this.furnaceCookTime);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.furnaceItemStacks.length; ++var3)
        {
            if (this.furnaceItemStacks[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.furnaceItemStacks[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        par1NBTTagCompound.setTag("Items", var2);

        if (this.isInvNameLocalized())
        {
            par1NBTTagCompound.setString("CustomName", this.field_94130_e);
        }
        
        // FCMOD: Code added to track extended burn times
        par1NBTTagCompound.setInteger( "fcBurnTimeEx", furnaceBurnTime );
        par1NBTTagCompound.setInteger( "fcCookTimeEx", furnaceCookTime );
        par1NBTTagCompound.setInteger( "fcItemBurnTimeEx", currentItemBurnTime );
        // END FCMOD
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Returns an integer between 0 and the passed value representing how close the current item is to being completely
     * cooked
     */
    @Environment(EnvType.CLIENT)
    public int getCookProgressScaled(int par1)
    {
    	// FCMOD: Client only. Change to adjust burn times relative to item cooked and have minimum value on indicator
    	/*
        return this.furnaceCookTime * par1 / 200;
        */
    	int iCookProgress = this.furnaceCookTime * par1 / getCookTimeForCurrentItem();
    	
    	if ( iCookProgress == 0 && isBurning() && canSmelt() )
    	{
    		iCookProgress = 1;
    	}
    	
        return iCookProgress;
        // END FCMOD
    }

    /**
     * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
     * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
     */
    @Environment(EnvType.CLIENT)
    public int getBurnTimeRemainingScaled(int par1)
    {
        if (this.currentItemBurnTime == 0)
        {
            this.currentItemBurnTime = 200;
        }

        return this.furnaceBurnTime * par1 / this.currentItemBurnTime;
    }

    /**
     * Returns true if the furnace is currently burning
     */
    public boolean isBurning()
    {
        return this.furnaceBurnTime > 0;
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity()
    {
        boolean var1 = this.furnaceBurnTime > 0;
        boolean var2 = false;

        if (this.furnaceBurnTime > 0)
        {
            --this.furnaceBurnTime;
        }

        if (!this.worldObj.isRemote)
        {
            if (this.furnaceBurnTime == 0)
            {
                this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);

                if (this.furnaceBurnTime > 0)
                {
                    var2 = true;

                    if (this.furnaceItemStacks[1] != null)
                    {
                        --this.furnaceItemStacks[1].stackSize;

                        if (this.furnaceItemStacks[1].stackSize == 0)
                        {
                            Item var3 = this.furnaceItemStacks[1].getItem().getContainerItem();
                            this.furnaceItemStacks[1] = var3 != null ? new ItemStack(var3) : null;
                        }
                    }
                }
            }

            if (this.isBurning() && this.canSmelt())
            {
                ++this.furnaceCookTime;

                if (this.furnaceCookTime >= getCookTimeForCurrentItem())
                {
                    this.furnaceCookTime = 0;
                    this.smeltItem();
                    var2 = true;
                }
            }
            else
            {
                this.furnaceCookTime = 0;
            }

            // FCMOD: Added
            boolean bHasVisibleContents = furnaceItemStacks[0] != null || furnaceItemStacks[2] != null;
            
            FurnaceBlock furnaceBlock = (FurnaceBlock)Block.blocksList[worldObj.getBlockId( xCoord, yCoord, zCoord )];
            // END FCMOD

            if (var1 != this.furnaceBurnTime > 0)
            {
                var2 = true;
                
                furnaceBlock.updateFurnaceBlockState( furnaceBurnTime > 0, worldObj, xCoord, yCoord, zCoord, bHasVisibleContents );
            }
            // FCMOD: Added
            else
            {
            	boolean bPreviousContentsState = ( worldObj.getBlockMetadata( xCoord, yCoord, zCoord ) & 8 ) != 0;
            	
            	if ( bPreviousContentsState != bHasVisibleContents )
            	{
            		furnaceBlock.updateFurnaceBlockState( furnaceBurnTime > 0, worldObj, xCoord, yCoord, zCoord, bHasVisibleContents );
            	}
            }
            // END FCMOD
        }

        if (var2)
        {
            this.onInventoryChanged();
        }
    }

    /**
     * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, etc.
     */
    protected boolean canSmelt()
    {
        if (this.furnaceItemStacks[0] == null)
        {
            return false;
        }
        else
        {
            ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0].getItem().itemID);
            if ( var1 == null )
            {
            	return false;
            }
            else if ( this.furnaceItemStacks[2] == null )
            {
            	return true;
            }
            else if ( !this.furnaceItemStacks[2].isItemEqual(var1) )
            {
            	return false;
            }
            else 
            {
            	int iOutputStackSizeIfCooked = furnaceItemStacks[2].stackSize + var1.stackSize;
            	
            	if ( iOutputStackSizeIfCooked <= getInventoryStackLimit() && iOutputStackSizeIfCooked <= furnaceItemStacks[2].getMaxStackSize()  )
            	{
            		return true;
            	}
                else
                {            	
                	return iOutputStackSizeIfCooked <= var1.getMaxStackSize();
                }
            }            
        }
    }

    /**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
     */
    public void smeltItem()
    {
        if (this.canSmelt())
        {
            ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0].getItem().itemID);

            if (this.furnaceItemStacks[2] == null)
            {
                this.furnaceItemStacks[2] = var1.copy();
            }
            else if (this.furnaceItemStacks[2].itemID == var1.itemID)
            {
                furnaceItemStacks[2].stackSize += var1.stackSize;
            }

            --this.furnaceItemStacks[0].stackSize;

            if (this.furnaceItemStacks[0].stackSize <= 0)
            {
                this.furnaceItemStacks[0] = null;
            }
        }
    }

    /**
     * Return true if item is a fuel source (getItemBurnTime() > 0).
     */
    public static boolean isItemFuel(ItemStack par0ItemStack)
    {
    	return par0ItemStack.getItem().getFurnaceBurnTime(par0ItemStack.getItemDamage()) > 0;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    public void openChest() {}

    public void closeChest() {}

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isStackValidForSlot(int par1, ItemStack par2ItemStack)
    {
        return par1 == 2 ? false : (par1 == 1 ? isItemFuel(par2ItemStack) : true);
    }

    /**
     * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
     * block.
     */
    public int[] getAccessibleSlotsFromSide(int par1)
    {
        return par1 == 0 ? field_102011_e : (par1 == 1 ? field_102010_d : field_102009_f);
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    public boolean canInsertItem(int par1, ItemStack par2ItemStack, int par3)
    {
        return this.isStackValidForSlot(par1, par2ItemStack);
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    public boolean canExtractItem(int par1, ItemStack par2ItemStack, int par3)
    {
        return par3 != 0 || par1 != 1 || par2ItemStack.itemID == Item.bucketEmpty.itemID;
    }

    // FCMOD: Added
    public static final int DEFAULT_COOK_TIME = 400;
    public static final int BASE_BURN_TIME_MULTIPLIER = 2;
    
    protected int getCookTimeForCurrentItem()
    {
    	int iCookTimeShift = 0;
    	
    	if ( furnaceItemStacks[0] != null )
    	{
    		iCookTimeShift = FurnaceRecipes.smelting().getCookTimeBinaryShift(
    			furnaceItemStacks[0].getItem().itemID);
    	}
    	
    	return DEFAULT_COOK_TIME << iCookTimeShift;
    }
    
    public int getItemBurnTime( ItemStack stack )
    {
        if ( stack != null )
        {
        	return stack.getItem().getFurnaceBurnTime(stack.getItemDamage()) * BASE_BURN_TIME_MULTIPLIER;
        }
        
        return 0;
    }
    // END FCMOD
}
