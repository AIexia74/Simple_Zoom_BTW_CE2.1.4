// FCMOD 

package btw.item.blockitems;

import btw.block.blocks.AestheticOpaqueEarthBlock;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class AestheticEarthBlockItem extends ItemBlock
{
    public AestheticEarthBlockItem(int iItemID )
    {
        super( iItemID );
        
        setMaxDamage( 0 );
        setHasSubtypes(true);
        
        setUnlocalizedName( "fcAestheticOpaqueEarth" );
    }

    @Override
    public int getMetadata( int iItemDamage )
    {
		return iItemDamage;    	
    }
    
    @Override
    public String getUnlocalizedName( ItemStack itemstack )
    {
    	switch( itemstack.getItemDamage() )
    	{
    		case AestheticOpaqueEarthBlock.SUBTYPE_BLIGHT_LEVEL_0:
    		case AestheticOpaqueEarthBlock.SUBTYPE_BLIGHT_LEVEL_1:
    		case AestheticOpaqueEarthBlock.SUBTYPE_BLIGHT_LEVEL_2:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("blight").toString();
    			
    		case AestheticOpaqueEarthBlock.SUBTYPE_BLIGHT_LEVEL_3:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("blight3").toString();
    			
    		case AestheticOpaqueEarthBlock.SUBTYPE_PACKED_EARTH:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("packed").toString();
    			
    		case AestheticOpaqueEarthBlock.SUBTYPE_DUNG:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("dung").toString();
    			
			default:
				
				return super.getUnlocalizedName();
    	}
    }
}