// FCMOD

package btw.item.items;

import btw.crafting.util.FurnaceBurnTime;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

public class BoatItem extends ItemBoat
{
    public BoatItem(int iItemID )
    {
    	super( iItemID );

    	setBuoyant();
    	setIncineratedInCrucible();
    	
    	// same as 5 jungle sidings to be equivalent to crappiest recipe
    	setfurnaceburntime(5 * FurnaceBurnTime.PLANKS_JUNGLE.burnTime / 2);
    	
    	setUnlocalizedName( "boat" );
    }
    
    @Override
	public boolean onItemUsedByBlockDispenser(ItemStack stack, World world,
                                              int i, int j, int k, int iFacing)
	{
        BlockPos offsetPos = new BlockPos( 0, 0, 0, iFacing );
        
        double dXPos = i + (offsetPos.x * 1.6D ) + 0.5D;
        double dYPos = j + offsetPos.y;
        double dZPos = k + (offsetPos.z * 1.6D ) + 0.5D;
    	
    	double dBoatYPos = j + offsetPos.y;
    	
        Entity entity = EntityList.createEntityOfType(EntityBoat.class, world, dXPos, dYPos, dZPos );
        
        world.spawnEntityInWorld( entity );
        
        world.playAuxSFX( 1000, i, j, k, 0 ); // normal pitch click							        
        
		return true;
	}
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
