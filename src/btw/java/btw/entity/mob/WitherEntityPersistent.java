// FCMOD

package btw.entity.mob;

import btw.client.fx.BTWEffectManager;
import net.minecraft.src.EntityList;
import btw.world.util.WorldUtils;
import net.minecraft.src.World;

public class WitherEntityPersistent extends WitherEntity
{
    public WitherEntityPersistent(World world )
    {
        super( world );
    }
    
    //------------- Class Specific Methods ------------//
    
    static public void summonWitherAtLocation(World world, int i, int j, int k)
    {
    	// FCTEST: Change this to create new FCEntityWithPersistent. Release as is
        WitherEntity wither = (WitherEntity) EntityList.createEntityOfType(WitherEntity.class, world);
        
        wither.setLocationAndAngles( (double)i + 0.5D, (double)j - 1.45D, (double)k + 0.5D, 
        	0F, 0F );
        	
        wither.func_82206_m();
        
        world.spawnEntityInWorld( wither );

        world.playAuxSFX( BTWEffectManager.CREATE_WITHER_EFFECT_ID, i, j, k, 0 );
        
        WorldUtils.gameProgressSetWitherHasBeenSummonedServerOnly();
    }    
}