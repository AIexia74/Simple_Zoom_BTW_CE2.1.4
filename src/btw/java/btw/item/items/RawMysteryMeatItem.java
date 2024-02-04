// FCMOD

package btw.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.IconRegister;

public class RawMysteryMeatItem extends FoodItem
{
	public RawMysteryMeatItem(int iItemID )
	{
		super( iItemID, 4, 0.25F, true, "fcItemMysteryMeatRaw", true );
		
		setStandardFoodPoisoningEffect();
	}

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        itemIcon = register.registerIcon("beefRaw");
    }
}
