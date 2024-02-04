// FCMOD
// seperate AI task from regular howl due to different mutex bits being required to handle howls while a wolf is sitting

package btw.entity.mob.behavior;

import btw.entity.mob.WolfEntity;

public class SittingWolfHowlBehavior extends WolfHowlBehavior
{
    public SittingWolfHowlBehavior(WolfEntity wolf )
    {
    	super( wolf );
        
        setMutexBits( 2 );
    }

    @Override
    public boolean shouldExecute()
    {
    	if (associatedWolf.isSitting() && !associatedWolf.isChild()  )
    	{
	        int iTimeOfDay = (int)(world.worldInfo.getWorldTime() % 24000L );
	        
	        if ( iTimeOfDay > 13500 && iTimeOfDay < 22500 )
	        {
	        	if (associatedWolf.heardHowlCountdown > 0 && associatedWolf.heardHowlCountdown <= HEARD_HOWL_DURATION - 15 )
	        	{
					howlingGroupInitiator = false;
	        		
			    	return associatedWolf.getRNG().nextInt(CHANCE_OF_HOWLING_WHEN_OTHERS_HOWL) == 0;
	        	}
	        }
    	}
    	
    	return false;
    }
}