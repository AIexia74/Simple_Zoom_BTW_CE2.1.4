// FCMOD

package btw.item.items;

import btw.crafting.util.FurnaceBurnTime;
import btw.util.ColorUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class WoolKnitItem extends Item
{
    public WoolKnitItem(int iItemID )
    {
    	super( iItemID );
    	
        setMaxDamage( 0 );
        setHasSubtypes( true );
        
        setBuoyant();
        setBellowsBlowDistance(1);
    	setfurnaceburntime(FurnaceBurnTime.WOOL_KNIT);
    	setFilterableProperties(Item.FILTERABLE_THIN);
        
    	setUnlocalizedName( "fcItemWoolKnit" );
    	
        setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    @Override
    public String getItemDisplayName( ItemStack stack )
    {
		int itemDamage = stack.getItemDamage();
		
		return ("" + StringTranslate.getInstance().translateNamedKey("woolKnit." + ColorUtils.colorOrder[itemDamage])).trim();
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubItems( int iItemID, CreativeTabs creativeTabs, List list )
    {
    	for ( int iColor = 0; iColor < 16; iColor++ )
    	{
    		list.add( new ItemStack( iItemID, 1, iColor ) );
    	}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColorFromItemStack( ItemStack stack, int iRenderPass )
    {
    	return WoolItem.woolColors[stack.getItemDamage()];
    }
}
