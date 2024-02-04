// FCMOD

package btw.item.items;

import btw.crafting.util.FurnaceBurnTime;
import net.minecraft.src.Item;

public class BucketItemLava extends Item
{
	// deprecated item.  Only legacy support for any lingering items.
	
    public BucketItemLava(int iItemID )
    {
    	super( iItemID );
    	
    	setfurnaceburntime(FurnaceBurnTime.LAVA_BUCKET);
    	
    	setUnlocalizedName( "bucketLava" );
	}
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
