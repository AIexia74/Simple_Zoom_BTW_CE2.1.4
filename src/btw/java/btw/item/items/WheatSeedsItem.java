// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;

public class WheatSeedsItem extends SeedItem
{
    public WheatSeedsItem(int iItemID )
    {
    	super( iItemID, BTWBlocks.wheatCrop.blockID );
    	
    	setAsBasicChickenFood();
    	
    	setUnlocalizedName( "fcItemWheatSeeds" );
	}
    
    //------------- Class Specific Methods ------------//	
    
	//----------- Client Side Functionality -----------//
}
