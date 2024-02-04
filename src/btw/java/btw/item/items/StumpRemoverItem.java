// FCMOD

package btw.item.items;

import btw.client.fx.BTWEffectManager;
import net.minecraft.src.*;

public class StumpRemoverItem extends Item
{
    public StumpRemoverItem(int iItemID )
    {
    	super( iItemID );
    	
        setMaxDamage( 0 );
        setHasSubtypes( false );
        
        maxStackSize = 16;
        
        setBuoyant();
	    setBellowsBlowDistance(1);
		setFilterableProperties(FILTERABLE_SMALL);
        
    	setUnlocalizedName( "fcItemStumpRemover" );
    	
    	setCreativeTab( CreativeTabs.tabTools );
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
		// tests copied over from itemReed to be on the safe side
        if ( player != null && !player.canPlayerEdit( i, j, k, iFacing, itemStack ) )
        {
            return false;
        }
        
        if ( itemStack.stackSize == 0 )
        {
            return false;
        }
        
		int iTargetBlockID = world.getBlockId( i, j, k );
		Block blockTarget = Block.blocksList[iTargetBlockID];
		
        if ( blockTarget != null && blockTarget.getDoesStumpRemoverWorkOnBlock(world, i, j, k) )
        {
        	if ( !world.isRemote )
        	{
        		world.setBlockWithNotify( i, j, k, 0 );
        		
    	        world.playAuxSFX( BTWEffectManager.REMOVE_STUMP_EFFECT_ID, i, j, k, 0 );
        	}
        	
            itemStack.stackSize--;	                
            
        	return true;        	
        }
            
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}