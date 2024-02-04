// FCMOD

package btw.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumArmorMaterial;

public class ArmorItemChain extends ArmorItemMod
{
	static final int RENDER_INDEX = 1;
	
    public ArmorItemChain(int iItemID, int iArmorType, int iWeight )
    {
        super(iItemID, EnumArmorMaterial.CHAIN, RENDER_INDEX, iArmorType, iWeight);
        
        setInfernalMaxEnchantmentCost(30);
        setInfernalMaxNumEnchants(2);
        
        this.setCreativeTab(CreativeTabs.tabCombat);
    }
    
    @Override
	public String getWornTexturePrefix()
    {
    	return "chain";
    }    
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}