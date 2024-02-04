// FCMOD 

package btw.item.blockitems;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class AestheticOpaqueBlockItem extends ItemBlock
{
    public AestheticOpaqueBlockItem(int iItemID )
    {
        super( iItemID );
        
        setMaxDamage( 0 );
        setHasSubtypes(true);
        
        setUnlocalizedName( "fcAestheticOpaque" );
    }

    @Override
    public int getMetadata( int iItemDamage )
    {
    	if ( iItemDamage == AestheticOpaqueBlock.SUBTYPE_STEEL)
    	{
    		// substitute the new block type for the old
    		
    		return 0;
    	}
    	
		return iItemDamage;    	
    }
    
    @Override
    public String getUnlocalizedName( ItemStack itemstack )
    {
    	switch( itemstack.getItemDamage() )
    	{
    		case AestheticOpaqueBlock.SUBTYPE_WICKER:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("wicker").toString();
    			
    		case AestheticOpaqueBlock.SUBTYPE_DUNG:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("dung").toString();
    			
    		case AestheticOpaqueBlock.SUBTYPE_STEEL:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("steel").toString();
    			
    		case AestheticOpaqueBlock.SUBTYPE_HELLFIRE:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("hellfire").toString();
    			
    		case AestheticOpaqueBlock.SUBTYPE_PADDING:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("padding").toString();
    			
    		case AestheticOpaqueBlock.SUBTYPE_SOAP:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("soap").toString();
    			
    		case AestheticOpaqueBlock.SUBTYPE_ROPE:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("rope").toString();
    			
    		case AestheticOpaqueBlock.SUBTYPE_FLINT:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("flint").toString();
    			
    		case AestheticOpaqueBlock.SUBTYPE_WHITE_STONE:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("whitestone").toString();
    			
    		case AestheticOpaqueBlock.SUBTYPE_WHITE_COBBLE:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("whitecobble").toString();
    			
    		case AestheticOpaqueBlock.SUBTYPE_BARREL:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("barrel").toString();
    			
    		case AestheticOpaqueBlock.SUBTYPE_CHOPPING_BLOCK_CLEAN:
    		case AestheticOpaqueBlock.SUBTYPE_CHOPPING_BLOCK_DIRTY:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("choppingblock").toString();
    			
    		case AestheticOpaqueBlock.SUBTYPE_ENDER_BLOCK:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("enderblock").toString();
    			
    		case AestheticOpaqueBlock.SUBTYPE_BONE:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("bone").toString();
    			
			default:
				
				return super.getUnlocalizedName();
    	}
    }    

    @Override
    public int getBlockIDToPlace(int iItemDamage, int iFacing, float fClickX, float fClickY, float fClickZ)
    {
    	if ( iItemDamage == AestheticOpaqueBlock.SUBTYPE_STEEL)
    	{
    		// substitute the new block type for the old
    		
    		return BTWBlocks.soulforgedSteelBlock.blockID;
    	}
    	
    	return super.getBlockIDToPlace(iItemDamage, iFacing, fClickX, fClickY, fClickZ);
    }
    
    @Override
    public float getBuoyancy(int iItemDamage)
    {
    	switch ( iItemDamage )
    	{
    		case AestheticOpaqueBlock.SUBTYPE_WICKER:
    		case AestheticOpaqueBlock.SUBTYPE_DUNG:
    		case AestheticOpaqueBlock.SUBTYPE_PADDING:
    		case AestheticOpaqueBlock.SUBTYPE_SOAP:
    		case AestheticOpaqueBlock.SUBTYPE_ROPE:
    		case AestheticOpaqueBlock.SUBTYPE_BONE:
    			
    			return 1.0F;
    			
    	}
    	
    	return super.getBuoyancy(iItemDamage);
    }    
}