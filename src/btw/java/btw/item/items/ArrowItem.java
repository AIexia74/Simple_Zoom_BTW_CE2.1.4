// FCMOD

package btw.item.items;

import btw.crafting.util.FurnaceBurnTime;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

public class ArrowItem extends Item
{
    public ArrowItem(int iItemID )
    {
    	super( iItemID );
    	
    	setBuoyant();
    	setBellowsBlowDistance(1);
    	setIncineratedInCrucible();
    	setfurnaceburntime(FurnaceBurnTime.SHAFT);
    	setFilterableProperties(FILTERABLE_NARROW);
    	
    	setUnlocalizedName( "arrow" );
    	
    	setCreativeTab( CreativeTabs.tabCombat );
    }
    
    @Override
	public boolean onItemUsedByBlockDispenser(ItemStack stack, World world,
											  int i, int j, int k, int iFacing)
	{
        BlockPos offsetPos = new BlockPos( 0, 0, 0, iFacing );
        
        double dXPos = i + (offsetPos.x * 0.6D ) + 0.5D;
        double dYPos = j + (offsetPos.y * 0.6D ) + 0.5D;
        double dZPos = k + (offsetPos.z * 0.6D ) + 0.5D;
    	
    	double dYHeading;
    	
    	if ( iFacing > 2 )
    	{
    		// slight upwards trajectory when fired sideways
    		
    		dYHeading = 0.10000000149011611F;
    	}
    	else
    	{
    		dYHeading = offsetPos.y;
    	}
    	
        EntityArrow entityarrow = getFiredArrowEntity(world, dXPos, dYPos, dZPos);
        entityarrow.setThrowableHeading(offsetPos.x, dYHeading, offsetPos.z, 1.1F, 6F);
        world.spawnEntityInWorld( entityarrow );
        
        world.playAuxSFX( 1002, i, j, k, 0 ); // bow sound
        
		return true;
	}
	
    //------------- Class Specific Methods ------------//
    
    EntityArrow getFiredArrowEntity(World world, double dXPos, double dYPos, double dZPos)
    {
        EntityArrow entity = (EntityArrow) EntityList.createEntityOfType(EntityArrow.class, world, dXPos, dYPos, dZPos );
        
        entity.canBePickedUp = 1;
        
        return entity;
    }
    
	//------------ Client Side Functionality ----------//
}
