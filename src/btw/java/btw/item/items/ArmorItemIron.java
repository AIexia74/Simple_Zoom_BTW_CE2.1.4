// FCMOD

package btw.item.items;

import net.minecraft.src.EnumArmorMaterial;

public class ArmorItemIron extends ArmorItem
{
	private static final int RENDER_INDEX = 2;
	
    public ArmorItemIron(int iItemID, int iArmorType, int iWeight )
    {
        super(iItemID, EnumArmorMaterial.IRON, RENDER_INDEX, iArmorType, iWeight);
     
		setInfernalMaxEnchantmentCost(25);
		setInfernalMaxNumEnchants(2);
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
