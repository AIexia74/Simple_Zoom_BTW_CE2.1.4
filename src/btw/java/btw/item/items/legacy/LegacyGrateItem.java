// FCMOD

package btw.item.items.legacy;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticNonOpaqueBlock;
import btw.item.items.PlaceAsBlockItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Icon;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class LegacyGrateItem extends PlaceAsBlockItem
{
    public LegacyGrateItem(int iItemID )
    {
    	super(iItemID, BTWBlocks.aestheticNonOpaque.blockID,
              AestheticNonOpaqueBlock.SUBTYPE_GRATE, "fcItemGrate");
    	
    	setBuoyant();
    	setIncineratedInCrucible();
    }
	
    @Override
    public boolean canItemPassIfFilter(ItemStack filteredItem)
    {
    	int iFilterableProperties = filteredItem.getItem().getFilterableProperties(filteredItem);
    		
    	return ( iFilterableProperties & (Item.FILTERABLE_SMALL |
                                          Item.FILTERABLE_FINE) ) != 0;
    }
    
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getHopperFilterIcon()
    {
		return BTWBlocks.gratePane.getHopperFilterIcon();
    }
}
