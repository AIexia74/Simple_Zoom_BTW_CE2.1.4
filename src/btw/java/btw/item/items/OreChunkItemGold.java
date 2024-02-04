// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class OreChunkItemGold extends PlaceAsBlockItem
{
    public OreChunkItemGold(int iItemID )
    {
        super( iItemID, BTWBlocks.goldOreChunk.blockID );
        
        setFilterableProperties(Item.FILTERABLE_SMALL);
        
        setUnlocalizedName( "fcItemChunkGoldOre" );
        
        setCreativeTab( CreativeTabs.tabMaterials );
    }
}
