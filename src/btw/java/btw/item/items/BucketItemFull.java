// FCMOD

package btw.item.items;

import btw.world.util.BlockPos;
import net.minecraft.src.*;

public abstract class BucketItemFull extends BucketItem
{
    public BucketItemFull(int iItemID )
    {
    	super( iItemID );
	}
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player )
    {
    	MovingObjectPosition posClicked = getMovingObjectPositionFromPlayer( world, player, false );
        
        if ( posClicked != null && posClicked.typeOfHit == EnumMovingObjectType.TILE &&
        	world.canMineBlock( player, posClicked.blockX, posClicked.blockY, posClicked.blockZ ) )
        {
        	BlockPos targetPos = new BlockPos( posClicked.blockX,
	            posClicked.blockY, posClicked.blockZ, posClicked.sideHit );

        	if (player.canPlayerEdit(targetPos.x, targetPos.y, targetPos.z,
									 posClicked.sideHit, itemStack ) &&
                attemptPlaceContentsAtLocation(world, targetPos.x, targetPos.y, targetPos.z) )
        	{
                if ( !player.capabilities.isCreativeMode )
                {
                    return new ItemStack( Item.bucketEmpty );
                }
        	}
        } 
        
        return itemStack;
    }
    
	//------------- Class Specific Methods ------------//

	protected abstract boolean attemptPlaceContentsAtLocation(World world, int i, int j, int k);
	
	//----------- Client Side Functionality -----------//
}
