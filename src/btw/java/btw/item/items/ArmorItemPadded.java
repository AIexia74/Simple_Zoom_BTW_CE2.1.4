// FCMOD

package btw.item.items;

import net.minecraft.src.EnumArmorMaterial;

public class ArmorItemPadded extends ArmorItemMod
{
	static final int RENDER_INDEX = 1;
	private final int enchantability = 0; // can not be enchanted normally
	
    public ArmorItemPadded(int iItemID, int iArmorType )
    {
        super(iItemID, EnumArmorMaterial.CLOTH, RENDER_INDEX, iArmorType, 0); // we're overriding the material
     
        setMaxDamage( getMaxDamage() >> 1  ); // 1/2 that of leather        
        
        setBuoyant();
        setIncineratedInCrucible();
    }
    
    @Override
    public boolean hasCustomColors()
    {
    	return true;
    }
    
    @Override
    public int getDefaultColor()
    {
    	return 0x998F7F;
    }
    
    @Override
	public String getWornTexturePrefix()
    {
    	return "fcPadded";
    }    
    
    @Override
    public int getItemEnchantability()
    {
        return enchantability;
    }
}
