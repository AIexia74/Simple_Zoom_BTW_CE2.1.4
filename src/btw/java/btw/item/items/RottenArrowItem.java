// FCMOD

package btw.item.items;

import net.minecraft.src.EntityArrow;
import net.minecraft.src.EntityList;
import btw.entity.RottenArrowEntity;
import net.minecraft.src.World;

public class RottenArrowItem extends ArrowItem
{
    public RottenArrowItem(int iItemID )
    {
    	super( iItemID );
		
		setUnlocalizedName( "fcItemArrowRotten" );
    }

    @Override
    EntityArrow getFiredArrowEntity(World world, double dXPos, double dYPos, double dZPos)
    {
        EntityArrow entity = (EntityArrow) EntityList.createEntityOfType(RottenArrowEntity.class, world, dXPos, dYPos, dZPos );
        
        entity.canBePickedUp = 2;
        
        return entity;
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
