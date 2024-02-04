// FCMOD

package btw.item.items;

import btw.world.util.BlockPos;
import net.minecraft.src.*;

public abstract class ThrowableItem extends Item
{
    public ThrowableItem(int iItemID )
    {
    	super( iItemID );
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player )
    {        
        if ( !player.capabilities.isCreativeMode )
        {
        	stack.stackSize--;
        }
        
        world.playSoundAtEntity( player, "random.bow", 0.5F, 0.4F / 
        	( itemRand.nextFloat() * 0.4F + 0.8F ) );
        
        if( !world.isRemote )
        {
        	spawnThrownEntity(stack, world, player);
        }
    	
        return stack;
    }
    
    @Override
	public boolean onItemUsedByBlockDispenser(ItemStack stack, World world,
                                              int i, int j, int k, int iFacing)
	{
        BlockPos offsetPos = new BlockPos( 0, 0, 0, iFacing );
        
        double dXPos = i + (offsetPos.x * 0.6D ) + 0.5D;
        double dYPos = j + (offsetPos.y * 0.6D ) + 0.5D;
        double dZPos = k + (offsetPos.z * 0.6D ) + 0.5D;
    	
    	double dYHeading;
    	
    	if ( iFacing > 2 )
    	{
    		// slight upwards trajectory when fired sideways
    		
    		dYHeading = 0.1D;
    	}
    	else
    	{
    		dYHeading = offsetPos.y;
    	}
    	
    	EntityThrowable entity = getEntityFiredByByBlockDispenser(world, dXPos, dYPos, dZPos);
    	
        entity.setThrowableHeading(offsetPos.x, dYHeading, offsetPos.z, 1.1F, 6F);
        
        world.spawnEntityInWorld( entity );
        
        world.playAuxSFX( 1002, i, j, k, 0 ); // bow sound
        
		return true;
	}
    
    //------------- Class Specific Methods ------------//
    
    protected abstract void spawnThrownEntity(ItemStack stack, World world,
                                              EntityPlayer player);
    
    protected abstract EntityThrowable getEntityFiredByByBlockDispenser(World world,
                                                                        double dXPos, double dYPos, double dZPos);
    
	//------------ Client Side Functionality ----------//
}
