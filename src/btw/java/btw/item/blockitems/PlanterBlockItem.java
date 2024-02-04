//FCMOD 

package btw.item.blockitems;

import btw.block.blocks.PlanterBlock;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class PlanterBlockItem extends ItemBlock
{
    public PlanterBlockItem(int i )
    {
        super( i );
        
        setMaxDamage( 0 );
        setHasSubtypes( true );
        setUnlocalizedName( "fcBlockPlanter" );
    }

    @Override
    public int getMetadata( int i )
    {
    	return i;
    }
    
    @Override
    public String getItemDisplayName( ItemStack stack )
    {
    	String name = super.getItemDisplayName( stack );
    	
    	int iDamage = stack.getItemDamage();
    	
    	if ( iDamage == PlanterBlock.TYPE_SOIL ||
    		iDamage == PlanterBlock.TYPE_SOIL_FERTILIZED)
    	{
    		// deprecated subtypes
    		
    		return "Old " + name;
    	}
    	
    	return name;
    }    
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
