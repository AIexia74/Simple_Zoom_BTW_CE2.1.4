// FCMOD

package btw.entity.mob.behavior;

import btw.client.fx.BTWEffectManager;
import btw.entity.mob.DireWolfEntity;
import net.minecraft.src.*;

public class DireWolfHowlBehavior extends EntityAIBase
{
    protected DireWolfEntity associatedWolf;
    protected World world;
    
    protected int howlCounter;
    
    protected static final int CHANCE_OF_HOWLING = 2400;  // chance is 1 over value per tick
    
    protected static final int HOWL_DURATION = 80;
    
    public DireWolfHowlBehavior(DireWolfEntity wolf )
    {
        this.associatedWolf = wolf;
        this.world = wolf.worldObj;
        
        this.setMutexBits( 7 );
    }

    @Override
    public boolean shouldExecute()
    {
        int iTimeOfDay = (int)(world.worldInfo.getWorldTime() % 24000L );
        
        if ( iTimeOfDay > 13500 && iTimeOfDay < 22500 )
        {
	    	return associatedWolf.getRNG().nextInt(CHANCE_OF_HOWLING) == 0;
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
        
    	notifyOtherWolvesInAreaOfHowl();

        associatedWolf.heardHowlCountdown = 0;

        int iTargetI = MathHelper.floor_double(associatedWolf.posX);
        int iTargetJ = MathHelper.floor_double(associatedWolf.posY) + 1;
        int iTargetK = MathHelper.floor_double(associatedWolf.posZ);
        
        world.func_82739_e(BTWEffectManager.WOLF_HOWL_EFFECT_ID, iTargetI, iTargetJ, iTargetK, 1); // this function broadcasts FX at a much larger range than playAuxFX
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
