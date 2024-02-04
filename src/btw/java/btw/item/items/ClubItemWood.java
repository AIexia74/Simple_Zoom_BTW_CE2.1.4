// FCMOD

package btw.item.items;

import btw.crafting.util.FurnaceBurnTime;

public class ClubItemWood extends ClubItem
{
    public static final int WEAPON_DAMAGE_WOOD = 2;
    public static final int DURABILITY_WOOD = 10;
    
    public ClubItemWood(int iItemID )
    {
        super(iItemID, DURABILITY_WOOD, WEAPON_DAMAGE_WOOD, "fcItemClub");
        
    	setfurnaceburntime(FurnaceBurnTime.SHAFT);
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
