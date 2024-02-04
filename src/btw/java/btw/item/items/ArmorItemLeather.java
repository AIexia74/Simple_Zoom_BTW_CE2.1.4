// FCMOD

package btw.item.items;

import net.minecraft.src.EnumArmorMaterial;

public class ArmorItemLeather extends ArmorItem
{
	private static final int RENDER_INDEX = 0;
	private static final int WORN_WEIGHT = 0;
	
    public ArmorItemLeather(int iItemID, int iArmorType )
    {
        super(iItemID, EnumArmorMaterial.CLOTH, RENDER_INDEX, iArmorType, WORN_WEIGHT);
     
		setInfernalMaxEnchantmentCost(10);
		setInfernalMaxNumEnchants(2);
        
        setBuoyant();
        setIncineratedInCrucible();
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
