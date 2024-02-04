// FCMOD

package btw.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EnumArmorMaterial;
import btw.crafting.util.FurnaceBurnTime;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;

public class ArmorItemWood extends ArmorItemMod
{
	static final int RENDER_INDEX = 1;
	private final int enchantability = 0; // can not be enchanted normally
	
    public ArmorItemWood(int iItemID, int iArmorType )
    {
        super(iItemID, EnumArmorMaterial.CLOTH, RENDER_INDEX, iArmorType, 0); // we're overriding the material
     
        setMaxDamage( getMaxDamage() >> 2  ); // 1/4 that of leather
        
        damageReduceAmount = 1;
        
        setBuoyant();
        
    	setfurnaceburntime(getNumWoolKnitMadeOf() *
                           FurnaceBurnTime.WOOL_KNIT.burnTime / 2);
    }
    
    @Override
    public boolean hasCustomColors()
    {
    	return true;
    }
    
    @Override
    public boolean hasSecondRenderLayerWhenWorn()
    {
    	return true;
    }
    
    @Override
    public int getDefaultColor()
    {
    	return 0x808080;
    }
    
    @Override
	public String getWornTexturePrefix()
    {
    	return "fcWool";
    }
    
    @Override
    public int getItemEnchantability()
    {
        return enchantability;
    }
    
    //------------- Class Specific Methods ------------//
    
    private int getNumWoolKnitMadeOf()
    {
    	switch ( armorType )
    	{
    		case 0: // helmet
    			
    			return 2;
    			
    		case 1: // chest
    			
    			return 4;
    			
    		case 2: // leggings
    			
    			return 3;
    			
			default: // 3 == boots
				
    			return 2;
    	}
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconOverlay = null;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
    	super.registerIcons( register );

    	if ( armorType == 0 )
    	{
            iconOverlay = register.registerIcon("fcItemWoolHelm_overlay");
    	}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return iconOverlay != null;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamageForRenderPass( int iDamage, int iRenderPass )
    {
    	if ( iRenderPass == 1 && iconOverlay != null )
    	{
    		return iconOverlay;
    	}
    	
        return getIconFromDamage( iDamage );
    }
}
