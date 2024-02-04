// FCMOD

package btw.item.blockitems.legacy;

import btw.block.BTWBlocks;
import btw.item.blockitems.InfiniteBurningTorchBlockItem;

public class LegacyTorchBlockItem extends InfiniteBurningTorchBlockItem
{	
    public LegacyTorchBlockItem(int iItemID )
    {
        super( iItemID );
    }
    
    @Override
    public int getBlockIDToPlace(int iItemDamage, int iFacing, float fClickX, float fClickY, float fClickZ)
    {
    	return BTWBlocks.infiniteBurningTorch.blockID;
    }
}
