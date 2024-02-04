// FCMOD

package btw.item.items;

import btw.client.fx.BTWEffectManager;
import net.minecraft.src.*;

public class MortarItem extends Item
{
    public MortarItem(int iItemID )
    {
    	super( iItemID );
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
                             int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
        if ( player != null && player.canPlayerEdit( i, j, k, iFacing, stack ) )
        {
        	Block targetBlock = Block.blocksList[world.getBlockId( i, j, k )];
        	
        	if ( targetBlock != null && targetBlock.onMortarApplied(world, i, j, k) )
        	{            	
    			if ( !world.isRemote )
    			{
    		        world.playAuxSFX( BTWEffectManager.APPLY_MORTAR_EFFECT_ID, i, j, k, 0 );
    			}
    	        
    			stack.stackSize--;
    			
            	return true;
        	}
        }
        
        return false;
    }
    
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
