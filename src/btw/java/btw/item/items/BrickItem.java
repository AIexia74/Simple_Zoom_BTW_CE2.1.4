// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.CreativeTabs;

public class BrickItem extends PlaceAsBlockItem
{
    public BrickItem(int iItemID )
    {
    	super( iItemID );
    	
    	setUnlocalizedName( "brick" );

        setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    @Override
    public int getBlockID()
    {
        return BTWBlocks.placedBrick.blockID;
    }
}
