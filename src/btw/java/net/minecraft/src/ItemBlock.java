package net.minecraft.src;

import btw.item.items.PlaceAsBlockItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

public class ItemBlock extends PlaceAsBlockItem
{
    /** The block ID of the Block associated with this ItemBlock */
    @Environment(EnvType.CLIENT)
    private Icon field_94588_b;

    public ItemBlock(int par1)
    {
        super(par1);
        this.blockID = par1 + 256;        
    }

    /**
     * Returns the blockID for this Item
     */
    public int getBlockID()
    {
        return this.blockID;
    }

    /**
     * Returns 0 for /terrain.png, 1 for /gui/items.png
     */
    @Environment(EnvType.CLIENT)
    public int getSpriteNumber()
    {
        return Block.blocksList[this.blockID].getItemIconName() != null ? 1 : 0;
    }

    /**
     * Gets an icon index based on an item's damage value
     */
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage(int par1)
    {
        return this.field_94588_b != null ? this.field_94588_b : Block.blocksList[this.blockID].getBlockTextureFromSide(1);
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return Block.blocksList[this.blockID].getUnlocalizedName();
    }

    /**
     * Returns the unlocalized name of this item.
     */
    public String getUnlocalizedName()
    {
        return Block.blocksList[this.blockID].getUnlocalizedName();
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    @Environment(EnvType.CLIENT)
    public CreativeTabs getCreativeTab()
    {
        return Block.blocksList[this.blockID].getCreativeTabToDisplayOn();
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Environment(EnvType.CLIENT)
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        Block.blocksList[this.blockID].getSubBlocks(par1, par2CreativeTabs, par3List);
    }

    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        String var2 = Block.blocksList[this.blockID].getItemIconName();

        if (var2 != null)
        {
            this.field_94588_b = par1IconRegister.registerIcon(var2);
        }
    }

    @Environment(EnvType.CLIENT)
    public int getColorFromItemStack(ItemStack var1, int var2)
    {
        Block var3 = Block.blocksList[this.blockID];
        return var3 != null ? var3.getRenderColor(var2) : super.getColorFromItemStack(var1, var2);
    }
    
    // FCMOD: Added New
    private boolean hasOldNamePrefix = false;
    
    @Override
    public float getBuoyancy(int iItemDamage)
    {
    	return Block.blocksList[blockID].getBuoyancy(iItemDamage);
    }
    
    @Override
    public Item setBuoyancy(float fBuoyancy)
    {
    	Block.blocksList[blockID].setBuoyancy(fBuoyancy);
    	
    	return super.setBuoyancy(fBuoyancy);
    }

    @Override
    public int getFurnaceBurnTime(int iItemDamage)
    {
    	return Block.blocksList[blockID].getFurnaceBurnTime(iItemDamage);
    }
    
    @Override
    public Item setfurnaceburntime(int iBurnTime)
    {
    	Block.blocksList[blockID].setFurnaceBurnTime(iBurnTime);
    	
    	return super.setfurnaceburntime(iBurnTime);
    }
    
    @Override
    public int getHerbivoreFoodValue(int iItemDamage)
    {
    	return Block.blocksList[blockID].getHerbivoreItemFoodValue(iItemDamage);
    }
    
    @Override
    public Item setHerbivoreFoodValue(int iFoodValue)
    {
    	Block.blocksList[blockID].setHerbivoreItemFoodValue(iFoodValue);
    	
    	return super.setHerbivoreFoodValue(iFoodValue);
    }
    
    @Override
    public int getChickenFoodValue(int iItemDamage)
    {
    	return Block.blocksList[blockID].getChickenItemFoodValue(iItemDamage);
    }
    
    @Override
    public Item setChickenFoodValue(int iFoodValue)
    {
    	Block.blocksList[blockID].setChickenItemFoodValue(iFoodValue);
    	
    	return super.setChickenFoodValue(iFoodValue);
    }
    
    @Override
    public int getPigFoodValue(int iItemDamage)
    {
    	return Block.blocksList[blockID].getPigItemFoodValue(iItemDamage);
    }
    
    @Override
    public Item setPigFoodValue(int iFoodValue)
    {
    	Block.blocksList[blockID].setPigItemFoodValue(iFoodValue);
    	
    	return super.setPigFoodValue(iFoodValue);
    }
    
    @Override
    public boolean isIncineratedInCrucible()
    {
    	return Block.blocksList[blockID].isIncineratedInCrucible();
    }
    
    @Override
    public PlaceAsBlockItem setAssociatedBlockID(int iBlockID)
    {
    	blockID = iBlockID;
    	
    	return super.setAssociatedBlockID(iBlockID);
    }
    
    @Override
    public boolean canItemPassIfFilter(ItemStack filteredItem)
    {
    	return Block.blocksList[blockID].canItemPassIfFilter(filteredItem);
    }
    
    @Override
    public int getFilterableProperties(ItemStack stack)
    {
    	return Block.blocksList[blockID].getFilterableProperties(stack);
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getHopperFilterIcon()
    {
    	return Block.blocksList[blockID].getHopperFilterIcon();
    }
    // END FCMOD
}
