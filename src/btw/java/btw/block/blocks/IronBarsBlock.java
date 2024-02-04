// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class IronBarsBlock extends PaneBlock
{
    public IronBarsBlock(int iBlockID)
    {
        super( iBlockID, "fenceIron", "fenceIron", Material.iron, true );
        
        setHardness( 5F );
        setResistance( 10F );
        
        setStepSound( Block.soundMetalFootstep );
        
        setUnlocalizedName( "fenceIron" );
    }

    @Override
    public boolean canItemPassIfFilter(ItemStack filteredItem)
    {
    	int iFilterableProperties = filteredItem.getItem().getFilterableProperties(filteredItem);

    	return filteredItem.getMaxStackSize() > 1 &&
               ( iFilterableProperties & Item.FILTERABLE_SOLID_BLOCK) == 0;
    }
    
    //------------- Class Specific Methods ------------//	
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon filterIcon;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

        filterIcon = register.registerIcon("fcBlockHopper_ironbars");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getHopperFilterIcon()
    {
    	return filterIcon;
    }
}