//FCMOD 

package btw.item.blockitems;

import net.minecraft.src.ItemBlock;

public class UnfiredPotteryBlockItem extends ItemBlock
{
    public UnfiredPotteryBlockItem(int i )
    {
        super( i );
        
        setMaxDamage( 0 );
        setHasSubtypes( true );
        
        setUnlocalizedName( "fcBlockUnfiredPottery" );
    }

    @Override
    public int getMetadata( int i )
    {
        return i;
    }
}
