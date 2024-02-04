// FCMOD

package btw.item.blockitems;

import btw.block.blocks.SandAndGravelSlabBlock;
import net.minecraft.src.ItemStack;

public class SandAndGravelSlabBlockItem extends SlabBlockItem
{
    public SandAndGravelSlabBlockItem(int iItemID )
    {
        super( iItemID );        
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
    		case SandAndGravelSlabBlock.SUBTYPE_SAND:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("sand").toString();
                
			default:
				
				return super.getUnlocalizedName();
    	}
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
}
