// FCMOD

package btw.item.items;

import net.minecraft.src.EnumArmorMaterial;

public class ArmorItemGold extends ArmorItem
{
	private static final int RENDER_INDEX = 4;
	
    public ArmorItemGold(int iItemID, int iArmorType, int iWeight )
    {
        super(iItemID, EnumArmorMaterial.GOLD, RENDER_INDEX, iArmorType, iWeight);
     
		setInfernalMaxEnchantmentCost(30);
		setInfernalMaxNumEnchants(3);
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
