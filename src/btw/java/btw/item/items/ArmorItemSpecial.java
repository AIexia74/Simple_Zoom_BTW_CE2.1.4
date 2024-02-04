// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumArmorMaterial;

public class ArmorItemSpecial extends ArmorItemMod
{
	static final int RENDER_INDEX = 1;
	static final int ARMOR_LEVEL = 1; // we're overriding this variable's behavior
	
	static final int MAX_DAMAGE = 12; // leather helm is 55

    public ArmorItemSpecial(int iItemID, int iArmorType )
    {
        super(iItemID, EnumArmorMaterial.IRON, RENDER_INDEX, iArmorType, 0); // we're overriding the material
     
        // max damage is uniform for refined armour over all types
        
        setMaxDamage(MAX_DAMAGE);
        
        // we can't use cloth material above because it trigger the colored display of leather armor, so manually assign damage reduction amount
        
        damageReduceAmount = EnumArmorMaterial.CLOTH.getDamageReductionAmount( iArmorType );
    	
    	setCreativeTab( CreativeTabs.tabCombat );
    }
    
    @Override
	public String getWornTexturePrefix()
    {
    	return "special";
    }    
}
