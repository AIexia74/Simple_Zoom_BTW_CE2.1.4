// FCMOD 

package btw.item.blockitems;

import net.minecraft.src.ItemBlock;

public class BloodWoodLogBlockItem extends ItemBlock
{
    public BloodWoodLogBlockItem(int iItemID )
    {
        super( iItemID );
    }
    
    @Override
    public int getCampfireBurnTime(int iItemDamage)
    {
    	// logs can't be burned directly in a campfire without being split first
    	
    	return 0;
    }    
}
