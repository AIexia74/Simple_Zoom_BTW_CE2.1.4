// FCMOD 

package btw.item.blockitems;

import net.minecraft.src.ItemBlock;

public class VaseBlockItem extends ItemBlock
{
    public VaseBlockItem(int i )
    {
        super( i );
        
        setMaxDamage( 0 );
        setHasSubtypes( true );
        
        setUnlocalizedName( "fcBlockVase" );
    }

    @Override
    public int getMetadata( int i )
    {
    	return i;
    }    
}
