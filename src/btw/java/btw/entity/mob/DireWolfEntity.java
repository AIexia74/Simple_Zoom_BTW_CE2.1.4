// FCMOD

package btw.entity.mob;

import btw.client.fx.BTWEffectManager;
import btw.entity.mob.behavior.SimpleWanderBehavior;
import btw.entity.mob.behavior.DireWolfHowlBehavior;
import btw.entity.mob.behavior.WolfHowlBehavior;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehavior;
import btw.entity.mob.villager.VillagerEntity;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Iterator;
import java.util.List;

public class DireWolfEntity extends EntityCreature implements IAnimals
{    
    private static final float MOVE_SPEED_AGGRESSIVE = 0.45F;
    private static final float MOVE_SPEED_PASSIVE = 0.3F;
    
    public int howlingCountdown = 0;
    public int heardHowlCountdown = 0;
    
    public DireWolfEntity(World world )
    {
        super( world );
        
        texture = "/btwmodtex/fcWolfDire.png";
        
        setSize( 0.9F, 1.2F );
        
        moveSpeed = MOVE_SPEED_AGGRESSIVE;
        
        getNavigator().setBreakDoors( true );
        
        tasks.addTask( 0, new EntityAISwimming( this ) );
        tasks.addTask( 1, new ZombieBreakBarricadeBehavior( this ) );
        tasks.addTask( 1, new EntityAILeapAtTarget( this, 0.4F ) );
        tasks.addTask( 2, new EntityAIAttackOnCollide( this, moveSpeed, true ) );
        tasks.addTask( 3, new EntityAIRestrictSun( this ) );
        tasks.addTask( 4, new EntityAIFleeSun( this, this.moveSpeed ) );
        tasks.addTask( 5, new DireWolfHowlBehavior( this ) );
        tasks.addTask( 7, new SimpleWanderBehavior(this, MOVE_SPEED_PASSIVE));
        tasks.addTask( 9, new EntityAIWatchClosest( this, EntityPlayer.class, 8.0F ) );
        tasks.addTask( 9, new EntityAILookIdle( this ) );
        
        targetTasks.addTask( 2, new EntityAIHurtByTarget( this, true ) );

        // FCTODO: Figure out why the following range of 32 doesn't seem to actually work
        targetTasks.addTask( 3, new EntityAINearestAttackableTarget( this, EntityPlayer.class, 32F, 0, false ) );
        
        targetTasks.addTask( 4, new EntityAINearestAttackableTarget( this, VillagerEntity.class, 16F, 0, false ) );
        targetTasks.addTask( 4, new EntityAINearestAttackableTarget( this, ChickenEntity.class, 16F, 0, false ) );
        targetTasks.addTask( 4, new EntityAINearestAttackableTarget( this, CowEntity.class, 16F, 0, false ) );
        targetTasks.addTask( 4, new EntityAINearestAttackableTarget( this, PigEntity.class, 16F, 0, false ) );
        targetTasks.addTask( 4, new EntityAINearestAttackableTarget( this, SheepEntity.class, 16F, 0, false ) );
    }

    @Override
    public boolean isAIEnabled()
    {
        return true;
    }

    @Override
    public int getMaxHealth()
    {
        return 40;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
    }

    @Override
    protected void playStepSound(int par1, int par2, int par3, int par4)
    {
        this.playSound( "mob.wolf.step", 0.15F, 1.0F );
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
    }

    @Override
    protected boolean canDespawn()
    {
        return false;
    }

    @Override
    protected String getLivingSound()
    {
        return "mob.wolf.growl";
    }

    @Override
    protected String getHurtSound()
    {
        return "mob.wolf.growl";
    }

    @Override
    protected String getDeathSound()
    {
        return "mob.wolf.death";
    }

    @Override
    protected float getSoundVolume()
    {
        return 3.0F;
    }

    @Override
    protected float getSoundPitch()
    {
        return ( rand.nextFloat() - rand.nextFloat() ) * 0.05F + 0.55F;
    }

    @Override
    protected int getDropItemId()
    {
        if ( !worldObj.isRemote )
        {
        	return Item.rottenFlesh.itemID;
        }
        
        return -1;
    }
    
    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iLootingLevel )
    {
    	super.dropFewItems( bKilledByPlayer, iLootingLevel );
    	
        dropItem( BTWItems.rawLiver.itemID, 1 );
    }    

    @Override
    public void onLivingUpdate()
    {
    	checkForLooseFood();
    	
        if (this.worldObj.isRemote)
        {
            howlingCountdown = Math.max(0, howlingCountdown - 1);
        }
        else
        {
            heardHowlCountdown = Math.max(0, heardHowlCountdown - 1);
        	
            if ( worldObj.isDaytime() )
            {
                float fBrightness = getBrightness(1.0F);

                if ( fBrightness > 0.5F && rand.nextFloat() * 30.0F < (fBrightness - 0.4F) * 2.0F && 
                	worldObj.canBlockSeeTheSky( MathHelper.floor_double( posX ), MathHelper.floor_double( posY ), MathHelper.floor_double( posZ ) ) )
                {
                    setFire( 8 );
                }
            }
        }
        
        super.onLivingUpdate();
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();        
    }

    @Override
    public void knockBack( Entity entity, int iDamageDone, double dMotionX, double dMotionY )
    {
    	// override to remove knockback on dire
    }
    
    @Override
    public float getEyeHeight()
    {
        return height * 0.8F;
    }

    @Override
    public int getMeleeAttackStrength(Entity target)
    {
    	return 6;
    }
    
    @Override
    public float getBlockPathWeight(int par1, int par2, int par3)
    {
        return 0.5F - this.worldObj.getLightBrightness(par1, par2, par3);
    }
    
    @Override
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEAD;
    }
    
    @Override
    public String getEntityName()
    {
    	return "The Beast";
    }
    
    //------------- Class Specific Methods ------------//    
    
    public float getTailRotation()
    {
        return 1.5393804F;
    }

	private void checkForLooseFood()
	{
	    if ( !worldObj.isRemote && !isLivingDead )
	    {
	    	boolean bAte = false;
	    	
	        List itemList = worldObj.getEntitiesWithinAABB( EntityItem.class, boundingBox.expand( 2.5D, 1.0D, 2.5D ) );
	        
	        Iterator itemIterator = itemList.iterator();
	
	        while ( itemIterator.hasNext())
	        {
	    		EntityItem itemEntity = (EntityItem)itemIterator.next();
	    		
		        if ( itemEntity.delayBeforeCanPickup == 0 && !(itemEntity.isDead) )
		        {
		        	ItemStack itemStack = itemEntity.getEntityItem();
		        	
		        	Item item = itemStack.getItem();
		        	
		        	if (item.doZombiesConsume() || item.itemID == Item.chickenRaw.itemID )
		        	{
			            itemEntity.setDead();
			            
			            bAte = true;				            
		        	}
		        }		        
	        }
	        
	        if ( bAte )
	        {
	        	worldObj.playAuxSFX( BTWEffectManager.BURP_SOUND_EFFECT_ID,
	        		MathHelper.floor_double( posX ), MathHelper.floor_double( posY ), MathHelper.floor_double( posZ ), 0 );
	        }
	    }
	}

    public float getHeadRotationPointOffset(float par1)
    {
    	if (howlingCountdown > 0 )
    	{
			float fTiltFraction = 1F;
			
    		if (howlingCountdown < 5 )
			{
    			fTiltFraction = (float) howlingCountdown / 5F;
			}
    		else if (howlingCountdown > WolfHowlBehavior.HOWL_DURATION - 10 )
    		{
    			fTiltFraction = (float)(WolfHowlBehavior.HOWL_DURATION + 1 - howlingCountdown) / 10F;
    		}
    		
			return fTiltFraction * -0.5F;
    	}
    	
    	return 0F;    	
    }

    public float getHeadRotation(float par1)
    {
    	if (howlingCountdown > 0 )
    	{
			float fTiltFraction = 1F;
			
    		if (howlingCountdown < 5 )
			{
    			fTiltFraction = (float) howlingCountdown / 5F;
			}
    		else if (howlingCountdown > WolfHowlBehavior.HOWL_DURATION - 10 )
    		{
    			fTiltFraction = (float)(WolfHowlBehavior.HOWL_DURATION + 1 - howlingCountdown) / 10F;
    		}    			
    		
			return fTiltFraction * -((float)Math.PI / 5F);
    	}
    	else
    	{
            return rotationPitch / (180F / (float)Math.PI);
    	}    	
    }

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void handleHealthUpdate( byte bUpdateType )
    {
    	if ( bUpdateType == 10 )
    	{
            howlingCountdown = WolfHowlBehavior.HOWL_DURATION;
    		
    		return;
    	}
    	else
    	{
    		super.handleHealthUpdate( bUpdateType );
    	}
    }
}
