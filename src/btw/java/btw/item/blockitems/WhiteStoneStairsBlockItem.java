// FCMOD

package btw.item.blockitems;

import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class WhiteStoneStairsBlockItem extends ItemBlock
{
    public WhiteStoneStairsBlockItem(int iItemID )
    {
        super( iItemID );
        
        setMaxDamage( 0 );
        setHasSubtypes( true );
        
        setUnlocalizedName( "fcBlockWhiteStoneStairs" );
    }

    @Override
    public int getMetadata( int iItemDamage )
    {
		return iItemDamage;    	
    }
    
    @Override
    public String getUnlocalizedName( ItemStack itemstack )
    {
    	int iDamage = itemstack.getItemDamage();
    	
    	if ( ( iDamage & 8 ) > 0 )
    	{
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("cobble").toString();
    	}
    	else
    	{
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("smooth").toString();
    	}
    }
}