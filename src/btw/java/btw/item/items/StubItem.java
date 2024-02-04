//FCMOD

package btw.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.IconRegister;
import net.minecraft.src.Item;

public class StubItem extends Item
{
    public StubItem(int iItemID )
    {
        super( iItemID );
        
        setCreativeTab( null );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//

    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        itemIcon = register.registerIcon( "fcItemDung" );
    }
}
    
