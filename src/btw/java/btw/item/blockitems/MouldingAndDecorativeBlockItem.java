// FCMOD

package btw.item.blockitems;

import btw.block.blocks.MouldingAndDecorativeBlock;
import net.minecraft.src.ItemStack;

public class MouldingAndDecorativeBlockItem extends MouldingBlockItem
{	
    public MouldingAndDecorativeBlockItem(int iItemID )
    {
        super( iItemID );        
    }
    
    @Override
    public int getMetadata( int iItemDamage )
    {
		return iItemDamage;    	
    }
    
    @Override
    public String getUnlocalizedName( ItemStack itemStack )
    {
    	switch( itemStack.getItemDamage() )
    	{
    		case MouldingAndDecorativeBlock.SUBTYPE_COLUMN:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("column").toString();
    			
    		case MouldingAndDecorativeBlock.SUBTYPE_PEDESTAL_UP:
    		case MouldingAndDecorativeBlock.SUBTYPE_PEDESTAL_DOWN:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("pedestal").toString();
    			
    		case MouldingAndDecorativeBlock.SUBTYPE_TABLE:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("table").toString();
    			
			default:
				
				return super.getUnlocalizedName( itemStack );
    	}
    }    
}
