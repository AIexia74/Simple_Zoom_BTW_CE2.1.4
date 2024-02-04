// FCMOD

package btw.item.items;

import net.minecraft.src.EnumArmorMaterial;

public class ArmorItemSteel extends ArmorItemMod
{
	static final int RENDER_INDEX = 1;

	private final int enchantability = 0; // can not be enchanted normally
	static final int MAX_DAMAGE = 576; // diamond breastplate is 528

    public ArmorItemSteel(int iItemID, int iArmorType, int iWeight )
    {
        super(iItemID, EnumArmorMaterial.DIAMOND, RENDER_INDEX, iArmorType, iWeight);
     
        // max damage is uniform for refined armour over all types
        
        setMaxDamage(MAX_DAMAGE);
        
        setInfernalMaxEnchantmentCost(30);
        setInfernalMaxNumEnchants(4);
    }
    
    @Override
    public int getItemEnchantability()
    {
        return enchantability;
    }
    
    @Override
	public String getWornTexturePrefix()
    {
    	return "plate";
    }    
}