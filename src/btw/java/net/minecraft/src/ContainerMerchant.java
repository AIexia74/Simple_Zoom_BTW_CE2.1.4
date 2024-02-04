package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Iterator;

public class ContainerMerchant extends Container
{
    /** Instance of Merchant. */
    private IMerchant theMerchant;
    private InventoryMerchant merchantInventory;

    /** Instance of World. */
    private final World theWorld;

    public ContainerMerchant(InventoryPlayer par1InventoryPlayer, IMerchant par2IMerchant, World par3World)
    {
        this.theMerchant = par2IMerchant;
        this.theWorld = par3World;
        this.merchantInventory = new InventoryMerchant(par1InventoryPlayer.player, par2IMerchant);
        this.addSlotToContainer(new Slot(this.merchantInventory, 0, 36, 119));
        this.addSlotToContainer(new Slot(this.merchantInventory, 1, 62, 119));
        this.addSlotToContainer(new SlotMerchantResult(par1InventoryPlayer.player, par2IMerchant, this.merchantInventory, 2, 120, 119));
        int var4;

        for (var4 = 0; var4 < 3; ++var4)
        {
            for (int var5 = 0; var5 < 9; ++var5)
            {
                this.addSlotToContainer(new Slot(par1InventoryPlayer,
                	var5 + var4 * 9 + 9, 
                	8 + var5 * 18, 
                	157 + var4 * 18));
            	// END FCMOD
            }
        }

        for (var4 = 0; var4 < 9; ++var4)
        {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, var4, 8 + var4 * 18, 215));
        }
    }

    public InventoryMerchant getMerchantInventory()
    {
        return this.merchantInventory;
    }

    public void addCraftingToCrafters(ICrafting par1ICrafting)
    {
        super.addCraftingToCrafters(par1ICrafting);
        
        // FCMOD: Added (Note that uses different function name on client and server)
        onCrafterAdded(par1ICrafting);
        // END FCMOD
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        
        // FCMOD: Added        
        detectAndSendChangesToBTSMTradeVariables();
        // END FCMOD
    }
    
    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        this.merchantInventory.resetRecipeAndSlots();
        super.onCraftMatrixChanged(par1IInventory);
    }

    public void setCurrentRecipeIndex(int par1)
    {
        this.merchantInventory.setCurrentRecipeIndex(par1);
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.theMerchant.getCustomer() == par1EntityPlayer;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNum)
    {
        ItemStack stackCopy = null;
        Slot slot = (Slot)this.inventorySlots.get(slotNum);

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            stackCopy = stack.copy();

            if (slotNum == 2)
            {
            	// FCMOD: Code added to abort trade if it's used up
            	if ( merchantInventory.getCurrentRecipe().func_82784_g() )
            	{
                    slot.putStack((ItemStack)null);

            		return null;
            	}
            	// END FCMOD
            	
                if (!this.mergeItemStack(stack, 3, 39, true))
                {
                    return null;
                }

                slot.onSlotChange(stack, stackCopy);
            }
            else if (slotNum != 0 && slotNum != 1)
            {
                if (slotNum >= 3 && slotNum < 30)
                {
                    if (!this.mergeItemStack(stack, 0, 2, false))
                    {
                        return null;
                    }
                }
                else if (slotNum >= 30 && slotNum < 39 && !this.mergeItemStack(stack, 0, 2, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(stack, 3, 39, true))
            {
                return null;
            }

            if (stack.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (stack.stackSize == stackCopy.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, stack);
        }

        return stackCopy;
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    public void onCraftGuiClosed(EntityPlayer par1EntityPlayer)
    {
        super.onCraftGuiClosed(par1EntityPlayer);
        this.theMerchant.setCustomer((EntityPlayer)null);
        super.onCraftGuiClosed(par1EntityPlayer);

        if (!this.theWorld.isRemote)
        {
            ItemStack var2 = this.merchantInventory.getStackInSlotOnClosing(0);

            if (var2 != null)
            {
                par1EntityPlayer.dropPlayerItem(var2);
            }

            var2 = this.merchantInventory.getStackInSlotOnClosing(1);

            if (var2 != null)
            {
                par1EntityPlayer.dropPlayerItem(var2);
            }
        }
    }
    
    // FCMOD: Added
    public int associatedVillagerTradeLevel = 0;
    public int associatedVillagerTradeXP = 0;
    public int associatedVillagerTradeMaxXP = 0;
    
    private void detectAndSendChangesToBTSMTradeVariables()
    {
        int iCurrentTradeLevel = theMerchant.getCurrentTradeLevel();
        
        if (iCurrentTradeLevel != associatedVillagerTradeLevel)
        {
        	sendProgressBarUpdateToAllCrafters(0, iCurrentTradeLevel);

            associatedVillagerTradeLevel = iCurrentTradeLevel;
        }
        
        int iCurrentTradeXP = theMerchant.getCurrentTradeXP();
        
        if (iCurrentTradeXP != associatedVillagerTradeXP)
        {
        	sendProgressBarUpdateToAllCrafters(1, iCurrentTradeXP);

            associatedVillagerTradeXP = iCurrentTradeXP;
        }
        
        int iCurrentTradeMaxXP = theMerchant.getCurrentTradeMaxXP();
        
        if (iCurrentTradeMaxXP != associatedVillagerTradeMaxXP)
        {
        	sendProgressBarUpdateToAllCrafters(2, iCurrentTradeMaxXP);

            associatedVillagerTradeMaxXP = iCurrentTradeMaxXP;
        }
    }
    
    public void sendProgressBarUpdateToAllCrafters(int iVariableIndex, int iValue)
    {
        Iterator iterator = crafters.iterator();

        while( iterator.hasNext() )
        {
            ICrafting icrafting = (ICrafting)iterator.next();

            icrafting.sendProgressBarUpdate( this, iVariableIndex, iValue );
        }
    }
    
    public void onCrafterAdded(ICrafting crafter)
    {
        crafter.sendProgressBarUpdate(this, 0, associatedVillagerTradeLevel);
        crafter.sendProgressBarUpdate(this, 1, associatedVillagerTradeXP);
        crafter.sendProgressBarUpdate(this, 2, associatedVillagerTradeMaxXP);
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void updateProgressBar( int iVariableIndex, int iValue )
    {
        if ( iVariableIndex == 0 )
        {
            associatedVillagerTradeLevel = iValue;
        }
        else if ( iVariableIndex == 1 )
        {
            associatedVillagerTradeXP = iValue;
        }
        else if ( iVariableIndex == 2 )
        {
            associatedVillagerTradeMaxXP = iValue;
        }
    }
    // END FCMOD
}
