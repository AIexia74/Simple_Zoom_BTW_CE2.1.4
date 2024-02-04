// FCMOD

package btw.item.blockitems;

import net.minecraft.src.Block;
import net.minecraft.src.ItemBlockWithMetadata;

public class SnowBlockItem extends ItemBlockWithMetadata
{
	// FCMOD: This class exists to remove the onItemUse override of ItemSnow so that snow blocks can't be combined/stacked.
	
    public SnowBlockItem(int iItemID, Block block )
    {
        super( iItemID, block );
    }    
}
