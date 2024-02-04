// FCMOD

package btw.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.IconRegister;
import net.minecraft.src.ItemFood;

public class CookedWolfChopItem extends ItemFood
{
	public CookedWolfChopItem(int iItemID )
	{
		super( iItemID, 5, 0.25F, false, false );
		
		setUnlocalizedName( "fcItemWolfChopCooked" );		
	}

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        itemIcon = register.registerIcon("porkchopCooked");
    }
}
