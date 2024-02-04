// FCMOD

package btw.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.IconRegister;
import net.minecraft.src.ItemFood;

public class CookedMysteryMeatItem extends ItemFood
{
	public CookedMysteryMeatItem(int iItemID )
	{
		super( iItemID, 5, 0.25F, true, false );
		
		setUnlocalizedName( "fcItemMysteryMeatCooked" );		
	}

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        itemIcon = register.registerIcon("beefCooked");
    }
}
