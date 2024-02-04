// FCMOD

package btw.item.items;

import net.minecraft.src.EnumArmorMaterial;

public class ArmorItemGimpSuit extends ArmorItemMod
{
	static final int RENDER_INDEX = 1;
	static final int WORN_WEIGHT = 0;
	
    public ArmorItemGimpSuit(int iItemID, int iArmorType )
    {
        super(iItemID, EnumArmorMaterial.CLOTH, RENDER_INDEX, iArmorType, WORN_WEIGHT); // we're overriding the material
     
        setMaxDamage( getMaxDamage() << 1 ); // 2X durability as normal leather
        
		setInfernalMaxEnchantmentCost(10);
		setInfernalMaxNumEnchants(2);
		
		setBuoyant();
    }
    
    @Override
	public String getWornTexturePrefix()
    {
    	return "fcGimp";
    }    
}
