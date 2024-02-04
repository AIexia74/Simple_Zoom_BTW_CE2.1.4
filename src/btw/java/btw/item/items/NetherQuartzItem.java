// FCMOD

package btw.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.IconRegister;
import net.minecraft.src.Item;

public class NetherQuartzItem extends Item
{
    public NetherQuartzItem(int iItemID)
    {
    	super( iItemID );
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon( "fcItemNetherQuartz" );
    }
}
