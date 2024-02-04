// FCMOD

package btw.item.items;

import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class ArcaneScrollItem extends Item
{
    public ArcaneScrollItem(int iItemID )
    {
    	super( iItemID );
    	
        setMaxDamage( 0 );
        setHasSubtypes( true );
        
        setBuoyant();
		setBellowsBlowDistance(3);
		setFilterableProperties(FILTERABLE_SMALL | FILTERABLE_THIN);

    	setUnlocalizedName( "fcItemScrollArcane" );
    	
        setCreativeTab( CreativeTabs.tabBrewing );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean hasEffect( ItemStack itemStack )
    {
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List infoList, boolean bAdvamcedToolTips )
    {
    	int iIndex = MathHelper.clamp_int( itemStack.getItemDamage(), 0, Enchantment.enchantmentsList.length - 1 );
    	
    	Enchantment enchantment = Enchantment.enchantmentsList[iIndex];
    	
    	if ( enchantment != null )
    	{
    		infoList.add( (new StringBuilder()).append( StatCollector.translateToLocal( enchantment.getName() ) ).toString() );
    	}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubItems( int iItemID, CreativeTabs creativeTabs, List list )
    {
		for ( int iTempIndex = 0; iTempIndex < Enchantment.enchantmentsList.length; iTempIndex++ )
		{
			if ( Enchantment.enchantmentsList[iTempIndex] != null )
			{
				list.add( new ItemStack( BTWItems.arcaneScroll, 1,
					Enchantment.enchantmentsList[iTempIndex].effectId ) );
			}
		}
    }
}