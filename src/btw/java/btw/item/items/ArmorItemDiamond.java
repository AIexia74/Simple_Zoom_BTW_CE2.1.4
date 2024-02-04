// FCMOD

package btw.item.items;

import net.minecraft.src.EnumArmorMaterial;

public class ArmorItemDiamond extends ArmorItem
{
	private static final int RENDER_INDEX = 3;
	
    public ArmorItemDiamond(int iItemID, int iArmorType, int iWeight )
    {
        super(iItemID, EnumArmorMaterial.DIAMOND, RENDER_INDEX, iArmorType, iWeight);
     
		setInfernalMaxEnchantmentCost(30);
		setInfernalMaxNumEnchants(2);
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
