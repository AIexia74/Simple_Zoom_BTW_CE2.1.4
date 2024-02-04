// FCMOD

package btw.block.tileentity.dispenser;

import btw.entity.DynamiteEntity;
import btw.item.BTWItems;
import net.minecraft.src.*;

public class DynamiteDispenserBehavior extends BehaviorProjectileDispense
{
    public DynamiteDispenserBehavior()
    {
    }

    protected IProjectile getProjectileEntity(World world, IPosition pos )
    {
        return (IProjectile) EntityList.createEntityOfType(DynamiteEntity.class, world, pos.getX(), pos.getY(), pos.getZ(), BTWItems.dynamite.itemID );
    }
}
