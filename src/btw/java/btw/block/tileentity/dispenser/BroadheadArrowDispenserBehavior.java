// FCMOD

package btw.block.tileentity.dispenser;

import btw.entity.BroadheadArrowEntity;
import net.minecraft.src.*;

public class BroadheadArrowDispenserBehavior extends BehaviorProjectileDispense
{
    public BroadheadArrowDispenserBehavior()
    {
    }

    /**
     * Return the projectile entity spawned by this dispense behavior.
     */
    protected IProjectile getProjectileEntity(World world, IPosition position)
    {
    	BroadheadArrowEntity arrow = (BroadheadArrowEntity) EntityList.createEntityOfType(BroadheadArrowEntity.class, world, position.getX(), position.getY(), position.getZ() );
        
        arrow.canBePickedUp = 1;
        
        return arrow;        
    }
}
