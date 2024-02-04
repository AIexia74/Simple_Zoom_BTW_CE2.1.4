// FCMOD

package btw.block.tileentity.dispenser;

import btw.entity.RottenArrowEntity;
import net.minecraft.src.*;

public class RottedArrowDispenserBehavior extends BehaviorProjectileDispense
{
    public RottedArrowDispenserBehavior()
    {
    }

    /**
     * Return the projectile entity spawned by this dispense behavior.
     */
    protected IProjectile getProjectileEntity(World world, IPosition position)
    {
    	RottenArrowEntity arrow = (RottenArrowEntity) EntityList.createEntityOfType(RottenArrowEntity.class, world, position.getX(), position.getY(), position.getZ() );
        
        arrow.canBePickedUp = 2;
        
        return arrow;        
    }
}
