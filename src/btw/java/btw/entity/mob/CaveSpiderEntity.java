// FCMOD

package btw.entity.mob;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class CaveSpiderEntity extends SpiderEntity
{
    public CaveSpiderEntity(World world )
    {
        super( world );
        
        texture = "/mob/cavespider.png";
        
        setSize( 0.7F, 0.5F );
    }
    
    @Override
    public int getMaxHealth()
    {
        return 12;
    }

    @Override
    public boolean attackEntityAsMob( Entity target )
    {
        if ( super.attackEntityAsMob( target ) )
        {
            if ( target instanceof EntityLiving)
            {
                ((EntityLiving)target).addPotionEffect( new PotionEffect(
                	Potion.poison.id, 7 * 20, 0 ) );
            }

            return true;
        }
        
        return false;
    }

    @Override
    public void initCreature() 
    {
    	// skip spider jockey chance in parent    	
    }
    
    @Override
	public boolean doesLightAffectAggessiveness()
	{
		return false;
	}
    
    @Override
    protected boolean dropsSpiderEyes()
    {
    	return false;
    }
    
    @Override
	protected void checkForSpiderSkeletonMounting()
	{
	}
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public float spiderScaleAmount()
    {
        return 0.7F;
    }
}