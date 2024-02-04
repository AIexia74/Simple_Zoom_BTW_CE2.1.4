// FCMOD

package btw.entity.mob;

import btw.entity.mob.behavior.SimpleWanderBehavior;
import net.minecraft.src.*;

public class SnowmanEntity extends EntitySnowman
{
    public SnowmanEntity(World world )
    {
        super( world );
        
        tasks.removeAllTasksOfClass(EntityAIWander.class);
        
        tasks.addTask( 2, new SimpleWanderBehavior( this, 0.2F ) );
    }

    @Override
    public void onLivingUpdate()
    {
    	entityLivingOnLivingUpdate(); // skip over immediate parent

        if ( isWet() )
        {
            attackEntityFrom( DamageSource.drown, 1 );
        }

        int iEntityI = MathHelper.floor_double( posX );
        int iEntityK = MathHelper.floor_double( posZ );

        if ( worldObj.getBiomeGenForCoords( iEntityI, iEntityK ).getFloatTemperature() > 1F )
        {
            attackEntityFrom( DamageSource.onFire, 1 );
        }
        else
        {
	        for ( int iTempCount = 0; iTempCount < 4; iTempCount++ )
	        {
	            int iTempI = MathHelper.floor_double( posX + 
	            	(float)( iTempCount % 2 * 2 - 1 ) * 0.25F );
	            
	            int iTempJ = (int)posY;
	            
	            int iTempK = MathHelper.floor_double( posZ + 
	            	(float)( iTempCount / 2 % 2 * 2 - 1 ) * 0.25F );
	
	            if ( worldObj.getBiomeGenForCoords( iTempI, iTempK).getFloatTemperature() < 0.8F )
	            {
	            	if ( worldObj.isAirBlock( iTempI, iTempJ, iTempK ) )
	            	{
	            		if ( Block.snow.canPlaceBlockAt( worldObj, iTempI, iTempJ, iTempK ) )
	            		{
	                        worldObj.setBlock(iTempI, iTempJ, iTempK, Block.snow.blockID);
	            		}
	            	}
	            	else if ( worldObj.isAirBlock( iTempI, iTempJ + 1, iTempK ) && 
	            		Block.snow.canPlaceBlockAt( worldObj, iTempI, iTempJ + 1, iTempK ) )
	            	{
	                    worldObj.setBlock(iTempI, iTempJ + 1, iTempK, Block.snow.blockID);
	            	}
	            }
	        }
        }
    }

	//------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
}
