// FCMOD

package btw.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.IconRegister;

public class RawWolfChopItem extends FoodItem
{
	public RawWolfChopItem(int iItemID )
	{
		super( iItemID, 4, 0.25F, false, "fcItemWolfChopRaw", true );
		
		setStandardFoodPoisoningEffect();
	}

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        itemIcon = register.registerIcon("porkchopRaw");
    }
}
