// FCMOD

package btw.item.items;

import btw.crafting.util.FurnaceBurnTime;
import btw.entity.BroadheadArrowEntity;
import net.minecraft.src.*;

public class BroadheadArrowItem extends ArrowItem
{
    public BroadheadArrowItem(int iItemID )
    {
    	super( iItemID );
		
    	setNeutralBuoyant();
    	
    	// neutralize parent properties
		setBellowsBlowDistance(0);
    	setNotIncineratedInCrucible();
    	setfurnaceburntime(FurnaceBurnTime.NONE);
    	
    	setUnlocalizedName( "fcItemArrowBroadhead" );
    }

    @Override
    EntityArrow getFiredArrowEntity(World world, double dXPos, double dYPos, double dZPos)
    {
        EntityArrow entity = (EntityArrow) EntityList.createEntityOfType(BroadheadArrowEntity.class, world, dXPos, dYPos, dZPos );
        
        entity.canBePickedUp = 1;
        
        return entity;
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
