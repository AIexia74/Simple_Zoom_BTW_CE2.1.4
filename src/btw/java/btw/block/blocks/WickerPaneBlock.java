// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class WickerPaneBlock extends PaneBlock
{
    public WickerPaneBlock(int iBlockID)
    {
        super( iBlockID, "fcBlockWicker", "fcBlockWicker",
                BTWBlocks.wickerMaterial, false );
        
        setHardness( 0.5F );        
        setAxesEffectiveOn();
		
        setBuoyant();
        
		setFireProperties(Flammability.WICKER);
		
        setLightOpacity( 4 );
        Block.useNeighborBrightness[iBlockID] = true;
        
        setStepSound( soundGrassFootstep );        
        
        setUnlocalizedName( "fcBlockWickerPane" );
    }
    
	@Override
    public boolean doesBlockBreakSaw(World world, int i, int j, int k )
    {
		return false;
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k,
                                                int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.wickerPane.itemID,
                              1, 0, fChanceOfDrop);
		
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID,
                              2, 0, fChanceOfDrop);
		
		return true;
	}
	
    @Override
    public boolean canItemPassIfFilter(ItemStack filteredItem)
    {
    	int iFilterableProperties = filteredItem.getItem().getFilterableProperties(filteredItem);
    		
    	return ( iFilterableProperties & Item.FILTERABLE_FINE) != 0;
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

        filterIcon = register.registerIcon("fcBlockHopper_wicker");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getHopperFilterIcon()
    {
    	return filterIcon;
    }
}
