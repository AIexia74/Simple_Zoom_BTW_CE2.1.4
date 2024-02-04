// FCMOD

package btw.block.tileentity.dispenser;

import btw.entity.UrnEntity;
import btw.item.BTWItems;
import net.minecraft.src.*;

public class SoulUrnDispenserBehavior extends BehaviorProjectileDispense
{
    public SoulUrnDispenserBehavior()
    {
    }

    protected IProjectile getProjectileEntity(World world, IPosition pos )
    {
        return (IProjectile) EntityList.createEntityOfType(UrnEntity.class, world, pos.getX(), pos.getY(), pos.getZ(), BTWItems.soulUrn.itemID );
    }
}
