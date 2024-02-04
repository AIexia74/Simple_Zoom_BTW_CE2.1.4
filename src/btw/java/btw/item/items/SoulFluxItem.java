// FCMOD

package btw.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.PotionHelper;

public class SoulFluxItem extends Item
{
    public SoulFluxItem(int iItemID )
    {
    	super( iItemID );
    	
    	setBuoyant();
        setBellowsBlowDistance(3);
		setFilterableProperties(FILTERABLE_FINE);
    	
    	setPotionEffect( PotionHelper.glowstoneEffect );
        
    	setUnlocalizedName( "fcItemSoulFlux" );
    	
        setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean hasEffect( ItemStack itemStack )
    {
		return true;
    }
}