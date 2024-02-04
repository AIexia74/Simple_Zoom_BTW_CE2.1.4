// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class OreChunkItemIron extends PlaceAsBlockItem
{
    public OreChunkItemIron(int iItemID )
    {
        super( iItemID, BTWBlocks.ironOreChunk.blockID );
        
        setFilterableProperties(Item.FILTERABLE_SMALL);
        
        setUnlocalizedName( "fcItemChunkIronOre" );
        
        setCreativeTab( CreativeTabs.tabMaterials );
    }
}
