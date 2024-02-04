// FCMOD

package btw.entity.mob;

import btw.item.BTWItems;
import net.minecraft.src.*;

public class MagmaCubeEntity extends EntityMagmaCube
{
    public MagmaCubeEntity(World world )
    {
        super( world );
        
        landMovementFactor = 0.5F; // unifying client and server values in parent
    }
    
    @Override
    protected boolean canDamagePlayer()
    {
    	return isEntityAlive() && attackTime <= 0;
    }

    @Override
    public void checkForScrollDrop()
    {    	
        if ( getSlimeSize() == 1 && rand.nextInt( 250 ) == 0 )
        {
            ItemStack itemstack = new ItemStack( BTWItems.arcaneScroll, 1,
            	Enchantment.fireAspect.effectId );
            
            entityDropItem( itemstack, 0F );
        }
    }
    
    @Override
    protected EntitySlime createInstance()
    {
        return (EntitySlime) EntityList.createEntityOfType(MagmaCubeEntity.class, worldObj);
    }

    //------------- Class Specific Methods ------------//
}
