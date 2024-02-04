// FCMOD

package btw.entity.mob;

import btw.client.fx.BTWEffectManager;
import btw.entity.LightningBoltEntity;
import btw.entity.mob.behavior.*;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Iterator;
import java.util.List;

public class PigEntity extends EntityPig
{
    public PigEntity(World world )
    {
    	super( world );
    	
        tasks.removeAllTasks();
        
        tasks.addTask( 0, new EntityAISwimming( this ) );
        tasks.addTask( 1, new AnimalFleeBehavior( this, 0.38F ) );
        tasks.addTask( 2, getAIControlledByPlayer() );
        tasks.addTask( 3, new EntityAIMate( this, 0.25F ) );
        tasks.addTask( 4, new MultiTemptBehavior( this, 0.3F ) );
        tasks.addTask( 5, new GrazeBehavior( this ) );
        tasks.addTask( 6, new MoveToLooseFoodBehavior( this, 0.25F ) );
        tasks.addTask( 7, new MoveToGrazeBehavior( this, 0.25F ) );
        tasks.addTask( 8, new EntityAIFollowParent( this, 0.28F ) );
        tasks.addTask( 9, new SimpleWanderBehavior( this, 0.25F ) );
        tasks.addTask( 10, new EntityAIWatchClosest( this, EntityPlayer.class, 6F ) );
        tasks.addTask( 11, new EntityAILookIdle( this ) );
    }

    @Override
    public boolean isAIEnabled()
    {
    	return !getWearingBreedingHarness();
    }

    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iLootingModifier )
    {
        if (!isStarving() && !hasHeadCrabbedSquid() )
        {
	    	int iNumDrops = rand.nextInt( 2 ) + 1 + rand.nextInt( 1 + iLootingModifier );
	    	
	        if ( isFamished() )
	        {
	        	iNumDrops = iNumDrops / 2;
	        }
	
	        for ( int iTempCount = 0; iTempCount < iNumDrops; ++iTempCount )
	        {
	            if ( isBurning() )
	            {
					if (worldObj.getDifficulty().shouldBurningMobsDropCookedMeat()) {
						dropItem(Item.porkCooked.itemID, 1);
					}
					else {
						dropItem(BTWItems.burnedMeat.itemID, 1);
					}
	            }
	            else
	            {
	                dropItem( Item.porkRaw.itemID, 1 );
	            }
	        }
        }

        if ( getSaddled() )
        {
            dropItem( Item.saddle.itemID, 1 );
        }
    }
	
	@Override
    public boolean attackEntityFrom( DamageSource damageSource, int par2)
    {
		// override to anger nearby pigmen
		
        if ( isEntityInvulnerable() )
        {
            return false;
        }
        
		Entity attackingEntity = damageSource.getEntity();

		if ( attackingEntity != null && ( attackingEntity instanceof EntityPlayer ) )
		{
			EntityPlayer attackingPlayer = (EntityPlayer)attackingEntity;
			
	        List pigmanList = worldObj.getEntitiesWithinAABB( ZombiePigmanEntity.class, boundingBox.expand( 16D, 8D, 16D ) );
	        
	        Iterator itemIterator = pigmanList.iterator();
	
	        while ( itemIterator.hasNext())
	        {
	    		ZombiePigmanEntity tempPigman = (ZombiePigmanEntity)itemIterator.next();
	    		
		        if ( !tempPigman.isLivingDead )
		        {
		        	tempPigman.becomeAngryWhenPigAttacked(attackingPlayer);
		        }		        
	        }
		}
		
        return super.attackEntityFrom( damageSource, par2 );
    }
	
    @Override
    public double getMountedYOffset()
    {
        if ( hasHeadCrabbedSquid() )
    	{
    		return (double)height * 1.2D;
    	}
    	
    	return super.getMountedYOffset();
    }
    
    @Override
    public boolean isBreedingItem( ItemStack itemStack )
    {
    	return itemStack.itemID == BTWItems.chocolate.itemID;
    }
    
	@Override
    public boolean isAffectedByMovementModifiers()
    {
    	return false;
    }
	
    @Override
    public boolean getCanCreatureTypeBePossessed()
    {
    	return true;
    }
    
	@Override
    public void onFullPossession()
    {
        worldObj.playAuxSFX( BTWEffectManager.POSSESSED_PIG_TRANSFORMATION_EFFECT_ID,
    		MathHelper.floor_double( posX ), MathHelper.floor_double( posY ), MathHelper.floor_double( posZ ), 
    		0 );
        
        setDead();
        
        ZombiePigmanEntity entityPigman = (ZombiePigmanEntity) EntityList.createEntityOfType(ZombiePigmanEntity.class, worldObj);
        
        entityPigman.setLocationAndAngles( posX, posY, posZ, rotationYaw, rotationPitch );
        entityPigman.renderYawOffset = renderYawOffset;
        
        entityPigman.setPersistent(true);
        
        entityPigman.setCanPickUpLoot( true );
        
        worldObj.spawnEntityInWorld( entityPigman );
    }
    
    @Override
    public boolean isValidZombieSecondaryTarget(EntityZombie zombie)
    {
    	return true;
    }
    
    @Override
    public PigEntity spawnBabyAnimal(EntityAgeable parent )
    {
        return (PigEntity) EntityList.createEntityOfType(PigEntity.class, worldObj);
    }
    
    @Override
    public void onStruckByLightning(LightningBoltEntity bolt)
    {
        if ( !worldObj.isRemote )
        {
            ZombiePigmanEntity pigman = (ZombiePigmanEntity) EntityList.createEntityOfType(ZombiePigmanEntity.class, worldObj);
            
            pigman.setLocationAndAngles( posX, posY, posZ, rotationYaw, rotationPitch );
            
            worldObj.spawnEntityInWorld( pigman );
            
            setDead();
        }
    }
    
    @Override
    protected String getLivingSound()
    {
    	if ( !isStarving() )
    	{
    		return "mob.pig.say";
    	}
    	else
    	{
    		return "mob.pig.death";
    	}
    }

    @Override
    public boolean isSubjectToHunger()
    {
    	return true;
    }
    
    @Override
    public int getFoodValueMultiplier()
    {
    	return 4;
    }

    @Override
    public boolean getDisruptsEarthOnGraze()
    {
    	return true;
    }
    
    @Override
    public boolean canGrazeOnRoughVegetation()
    {
    	return true;
    }
    
    @Override
    public int getGrazeDuration()
    {
    	return 80;
    }
    
    @Override
    public int getItemFoodValue(ItemStack stack)
    {
    	return stack.getItem().getPigFoodValue(stack.getItemDamage()) *
               getFoodValueMultiplier();
    }
    
    @Override
    public float getGrazeHeadRotationMagnitudeDivisor()
    {
    	return 3F;
    }
    
    @Override
    public float getGrazeHeadRotationRateMultiplier()
    {
    	return 28.7F * 1.75F;
    }
    
	//------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public String getTexture()
    {
    	if ( getWearingBreedingHarness() )
    	{
			return "/btwmodtex/fc_mr_pig.png";
    	}
    	
    	int iHungerLevel = getHungerLevel();
    	
    	if ( iHungerLevel == 1 )
    	{
			return "/btwmodtex/fcPigFamished.png";
    	}
    	else if ( iHungerLevel == 2 )
    	{
			return "/btwmodtex/fcPigStarving.png";
    	}
    	
        return super.getTexture();
    }
}
