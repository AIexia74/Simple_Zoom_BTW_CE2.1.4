// FCMOD

package btw.entity.mob;

import btw.entity.mob.behavior.SimpleWanderBehavior;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

public class WitherEntity extends EntityWither
{
    public WitherEntity(World world )
    {
        super( world );
        
        tasks.removeAllTasksOfClass(EntityAIWander.class);
        
        tasks.addTask( 5, new SimpleWanderBehavior( this, moveSpeed ) );
    }
    
    @Override
    public void checkForScrollDrop()
    {    	
        ItemStack stack = new ItemStack( BTWItems.arcaneScroll, 1,
        	Enchantment.knockback.effectId );
        
        entityDropItem( stack, 0F );
    }
    
    @Override
    protected void modSpecificOnLivingUpdate()
    {
    	super.modSpecificOnLivingUpdate();
    	
    	if ( !worldObj.isRemote )
    	{            
            WorldUtils.gameProgressSetWitherHasBeenSummonedServerOnly();
    	}
    }
    
    //------------- Class Specific Methods ------------//
}
