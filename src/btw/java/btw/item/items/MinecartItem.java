// FCMOD

package btw.item.items;

import btw.world.util.BlockPos;
import net.minecraft.src.*;

import java.util.List;

public class MinecartItem extends ItemMinecart
{
    public MinecartItem(int iItemID, int iMinecartType )
    {
    	super( iItemID, iMinecartType );    	
    }
    
    @Override
	public boolean onItemUsedByBlockDispenser(ItemStack stack, World world,
                                              int i, int j, int k, int iFacing)
	{
        BlockPos offsetPos = new BlockPos( 0, 0, 0, iFacing );
        
        double dXPos = i + (offsetPos.x * 1D ) + 0.5D;
        double dYPos = j + offsetPos.y;
        double dZPos = k + (offsetPos.z * 1D ) + 0.5D;
    	
        // check for already present minecarts
		List list = world.getEntitiesWithinAABB(EntityMinecart.class,
				AxisAlignedBB.getAABBPool().getAABB(dXPos, dYPos, dZPos, dXPos + 1, dYPos + 1, dZPos + 1));

		if (list != null && list.size() > 0) {
			// minecart was found, don't eject new minecart
			return false;
		}
        
        Entity entity = EntityMinecart.createMinecart( world, 
        	dXPos, dYPos, dZPos, ( (ItemMinecart)stack.getItem() ).minecartType );
        
        // speed of minecart getting shot out: just 1 or -1 in the offset direction atm

        world.spawnEntityInWorld( entity );
        
        entity.setVelocity((double) (offsetPos.x), (double) (offsetPos.y), (double) (offsetPos.z));
        

        
        world.playAuxSFX( 1000, i, j, k, 0 ); // normal pitch click							        
        
		return true;
	}
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
