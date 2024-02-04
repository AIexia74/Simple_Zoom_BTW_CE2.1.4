// FCMOD

package btw.entity.util;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.IEntitySelector;

public class ZombieSecondaryTargetFilter implements IEntitySelector
{
	EntityZombie zombie;
	
	public ZombieSecondaryTargetFilter(EntityZombie zombie )
	{
		this.zombie = zombie;
	}
	
    public boolean isEntityApplicable( Entity entity )
    {
        return entity.isValidZombieSecondaryTarget(zombie);
    }
}
