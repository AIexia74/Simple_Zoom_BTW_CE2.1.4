// FCMOD

package btw.item.blockitems;

import net.minecraft.src.ItemBlock;

public class DamageToMetadataBlockItem extends ItemBlock
{
	public DamageToMetadataBlockItem(int iItemID)
	{
        super( iItemID );
        
        setMaxDamage( 0 );
        setHasSubtypes(true);
	}
	
    @Override
    public int getMetadata( int iItemDamage )
    {
		return iItemDamage;    	
    }
}
