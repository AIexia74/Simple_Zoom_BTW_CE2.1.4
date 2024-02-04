// FCMOD

package btw.entity.mob.behavior;

import btw.client.fx.BTWEffectManager;
import btw.entity.mob.WolfEntity;
import net.minecraft.src.*;

public class WolfHowlBehavior extends EntityAIBase
{
    public WolfEntity associatedWolf;
    public World world;

    public int howlCounter;
    public boolean howlingGroupInitiator;

    public static final int CHANCE_OF_HOWLING = 4800;  // chance is 1 over value per tick
    public static final int CHANCE_OF_HOWLING_DURING_FULL_MOON = 240;
    public static final int CHANCE_OF_HOWLING_WHEN_OTHERS_HOWL = 240;

    public static final int HOWL_DURATION = 80;
    public static final int HEARD_HOWL_DURATION = 95;
    public static final double HEAR_HOWL_DISTANCE = 320D;
    public static final double HEAR_HOWL_DISTANCE_SQ = (HEAR_HOWL_DISTANCE * HEAR_HOWL_DISTANCE);
    
    public WolfHowlBehavior(WolfEntity wolf )
    {
        this.associatedWolf = wolf;
        this.world = wolf.worldObj;
        
        this.setMutexBits( 7 );
    }

    @Override
    public boolean shouldExecute()
    {
    	if ( !associatedWolf.isChild() )
    	{
	        int iTimeOfDay = (int)(world.worldInfo.getWorldTime() % 24000L );
	        
	        if ( iTimeOfDay > 13500 && iTimeOfDay < 22500 )
	        {
	        	int iMoonPhase = world.getMoonPhase();
	        	
	        	if ( iMoonPhase == 0 && world.worldInfo.getWorldTime() > 24000L ) // full moon, and not the first one of the game
	        	{
	            	if ( !associatedWolf.isTamed() )
	            	{
                        howlingGroupInitiator = false;
	            		
	    		    	return associatedWolf.getRNG().nextInt(CHANCE_OF_HOWLING_DURING_FULL_MOON) == 0;
	            	}
	        	}
	        	else if (associatedWolf.heardHowlCountdown > 0 && associatedWolf.heardHowlCountdown <= HEARD_HOWL_DURATION - 15 )
	        	{
                    howlingGroupInitiator = false;
	        		
			    	return associatedWolf.getRNG().nextInt(CHANCE_OF_HOWLING_WHEN_OTHERS_HOWL) == 0;
	        	}
	        	else
	        	{
	            	if ( !associatedWolf.isTamed() )
	            	{
                        howlingGroupInitiator = true;
	            		
	    		    	return associatedWolf.getRNG().nextInt(CHANCE_OF_HOWLING) == 0;
	            	}
	        	}
	        }
    	}
    	
    	return false;
    }

    @Override
    public boolean continueExecuting()
    {
    	return howlCounter < HOWL_DURATION;
    }

    @Override
    public void startExecuting()
    {
        howlCounter = 0;
    	
        world.setEntityState(associatedWolf, (byte)10);
        
        associatedWolf.getNavigator().clearPathEntity();
        
        if (howlingGroupInitiator)
        {
        	notifyOtherWolvesInAreaOfHowl();

            howlingGroupInitiator = false;
        }

        associatedWolf.heardHowlCountdown = 0;

        int iTargetI = MathHelper.floor_double(associatedWolf.posX);
        int iTargetJ = MathHelper.floor_double(associatedWolf.posY) + 1;
        int iTargetK = MathHelper.floor_double(associatedWolf.posZ);
        
        world.func_82739_e(BTWEffectManager.WOLF_HOWL_EFFECT_ID, iTargetI, iTargetJ, iTargetK, 0); // this function broadcasts FX at a much larger range than playAuxFX
    }

    @Override
    public void resetTask()
    {
        howlCounter = 0;
    }

    @Override
    public void updateTask()
    {
    	howlCounter++;
    }
    
    //------------- Class Specific Methods ------------//
    
    private void notifyOtherWolvesInAreaOfHowl()
    {
        for (int l = 0; l < world.loadedEntityList.size(); l++)
        {
            Entity tempEntity = (Entity) world.loadedEntityList.get(l);
            
            tempEntity.notifyOfWolfHowl(associatedWolf);
        }        
    }
}
