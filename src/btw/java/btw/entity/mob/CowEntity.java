// FCMOD

package btw.entity.mob;

import btw.client.fx.BTWEffectManager;
import btw.entity.mob.behavior.*;
import btw.item.BTWItems;
import btw.network.packet.BTWPacketManager;
import btw.client.network.packet.handler.EntityEventPacketHandler;
import btw.util.MiscUtils;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Iterator;
import java.util.List;

public class CowEntity extends EntityCow
{
    protected static final int GOT_MILK_DATA_WATCHER_ID = 26;
    
    private static final int FULL_MILK_ACCUMULATION_COUNT = MiscUtils.TICKS_PER_GAME_DAY;

	private static final int KICK_ATTACK_TICKS_TO_COOLDOWN = 40;
	private static final double KICK_ATTACK_RANGE = 1.75D;
    public static final int KICK_ATTACK_DURATION = 20;
	public static final double KICK_ATTACK_TIP_COLLISION_WIDTH = 2.75D;
	public static final double KICK_ATTACK_TIP_COLLISION_HALF_WIDTH = (KICK_ATTACK_TIP_COLLISION_WIDTH / 2D );
	public static final double KICK_ATTACK_TIP_COLLISION_HEIGHT = 2D;
	public static final double KICK_ATTACK_TIP_COLLISION_HALF_HEIGHT = (KICK_ATTACK_TIP_COLLISION_HEIGHT / 2D );
    
    private int milkAccumulationCount = 0;
    
    private int kickAttackCooldownTimer = KICK_ATTACK_TICKS_TO_COOLDOWN;
    
    public int kickAttackInProgressCounter = -1;
    
    public int kickAttackLegUsed = 0;
    
    public CowEntity(World world )
    {
        super( world );
        
        tasks.removeAllTasks();
        
        tasks.addTask( 0, new EntityAISwimming( this ) );
        tasks.addTask( 1, new AnimalFleeBehavior( this, 0.38F ) );
        tasks.addTask( 2, new EntityAIMate( this, 0.2F ) );
        tasks.addTask( 3, new MultiTemptBehavior( this, 0.25F ) );
        tasks.addTask( 4, new GrazeBehavior( this ) );
        tasks.addTask( 5, new MoveToLooseFoodBehavior( this, 0.2F ) );
        tasks.addTask( 6, new MoveToGrazeBehavior( this, 0.2F ) );
        tasks.addTask( 7, new EntityAIFollowParent( this, 0.25F ) );
        tasks.addTask( 8, new SimpleWanderBehavior( this, 0.25F ) );
        tasks.addTask( 9, new EntityAIWatchClosest( this, EntityPlayer.class, 6F ) );
        tasks.addTask( 10, new EntityAILookIdle( this ) );
    }
    
    @Override
    protected void entityInit()
    {
        super.entityInit();
        
        dataWatcher.addObject(GOT_MILK_DATA_WATCHER_ID, new Byte((byte)0 ));
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setBoolean("fcGotMilk", gotMilk());
        par1NBTTagCompound.setInteger("fcMilkCount", milkAccumulationCount);
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        
        if ( par1NBTTagCompound.hasKey( "fcGotMilk" ) )
    	{
        	setGotMilk(par1NBTTagCompound.getBoolean("fcGotMilk"));
    	}
        
        if ( par1NBTTagCompound.hasKey( "fcMilkCount" ) )
    	{
			milkAccumulationCount = par1NBTTagCompound.getInteger("fcMilkCount");
    	}
    }
    
    @Override
    public boolean isAIEnabled()
    {
    	return !getWearingBreedingHarness();
    }

    @Override
    public int getMaxHealth()
    {
    	return 15;
    }
    
    @Override
    public void onLivingUpdate()
    {
    	updateKickAttack();
    	
        super.onLivingUpdate();
        
    }
    
    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iLootingModifier )
    {
    	if ( !isStarving() )
    	{
	        int iNumDrops = rand.nextInt( 3 ) + rand.nextInt( 1 + iLootingModifier ) + 1;
	        
	        if ( isFamished() )
	        {
	        	iNumDrops = iNumDrops / 2;
	        }
	
	        for ( int iTempCount = 0; iTempCount < iNumDrops; ++iTempCount )
	        {
	            dropItem( Item.leather.itemID, 1 );
	        }
	
	        if ( !hasHeadCrabbedSquid() )
	        {
		        iNumDrops = rand.nextInt( 3 ) + 1 + rand.nextInt( 1 + iLootingModifier );
		        
		        if ( isFamished() )
		        {
		        	iNumDrops = iNumDrops / 2;
		        }
		
		        for ( int iTempCount = 0; iTempCount < iNumDrops; ++iTempCount )
		        {
		            if ( isBurning() )
		            {
						if (worldObj.getDifficulty().shouldBurningMobsDropCookedMeat()) {
							dropItem(Item.beefCooked.itemID, 1);
						}
						else {
							dropItem(BTWItems.burnedMeat.itemID, 1);
						}
		            }
		            else
		            {
		                dropItem( Item.beefRaw.itemID, 1 );
		            }
		        }
	        }
    	}
    }
    
    @Override
    public boolean isBreedingItem( ItemStack stack )
    {
        return stack.itemID == Item.cake.itemID;
    }

    @Override
    public boolean interact( EntityPlayer player )
    {
        ItemStack stack = player.inventory.getCurrentItem();

        if ( stack != null && stack.itemID == Item.bucketEmpty.itemID )
        {
        	if ( gotMilk() )
        	{
	        	stack.stackSize--;
	        	
	            if ( stack.stackSize <= 0 )
	            {
	                player.inventory.setInventorySlotContents( player.inventory.currentItem, new ItemStack( Item.bucketMilk ) );
	            }
	            else if ( !player.inventory.addItemStackToInventory( new ItemStack( Item.bucketMilk ) ) )
	            {
	                player.dropPlayerItem( new ItemStack( Item.bucketMilk.itemID, 1, 0 ) );
	            }
	
	            attackEntityFrom( DamageSource.generic, 0 );
	            
	            if ( !worldObj.isRemote )
	            {
	            	setGotMilk(false);
	            	
			        worldObj.playAuxSFX( BTWEffectManager.COW_MILKING_EFFECT_ID,
		                MathHelper.floor_double( posX ), (int)posY,
		                MathHelper.floor_double( posZ ), 0 );
	            }
        	}
        	else if (this.worldObj.getDifficulty().canMilkingStartleCows())
        	{
	            attackEntityFrom( DamageSource.causePlayerDamage( player ), 0 );
        	}

            return true;
        }
        
        // skip over EntityCow() super to avoid vanilla milking
        
        return entityAnimalInteract(player);
    }

    @Override
    public void onGrazeBlock(int i, int j, int k)
    {
    	super.onGrazeBlock(i, j, k);
    	
    	if ( !getWearingBreedingHarness() )
    	{    	
	        checkForGrazeSideEffects(i, j, k);
    	}
    }
    
    @Override
    public boolean isSubjectToHunger()
    {
    	return true;
    }
    
    @Override
    public void onBecomeFamished()
    {
    	super.onBecomeFamished();
    	
    	if ( gotMilk() )
    	{
    		setGotMilk(false);
    	}

		milkAccumulationCount = 0;
    }
    
    @Override
    public boolean canGrazeMycelium()
    {
    	return true;
    }
    
    @Override
    public double getMountedYOffset()
    {
		return (double)height * 1.2D;
    }
    
    @Override
	public boolean getCanCreatureTypeBePossessed()
    {
    	return true;
    }
    
	@Override
	protected void giveBirthAtTargetLocation(EntityAnimal targetMate, double dChildX, double dChildY, double dChildZ)
    {
		// small chance of normal birth when possessed
    	if ((isFullyPossessed() || targetMate.isFullyPossessed() ) && rand.nextInt(8) != 0 )
    	{
    		if ( worldObj.provider.dimensionId != 1 && worldObj.rand.nextInt( 2 ) == 0 )
    		{
    			birthMutant(targetMate, dChildX, dChildY, dChildZ);
    		}
    		else
    		{
    			stillBirth(targetMate, dChildX, dChildY, dChildZ);
    		}
    	}
    	else
    	{    	
    		super.giveBirthAtTargetLocation(targetMate, dChildX, dChildY, dChildZ);
    	}
    }
    
    @Override
    public void initCreature()
    {
    	initHungerWithVariance();
    	
        if ( !isChild() )
        {
			milkAccumulationCount = worldObj.rand.nextInt(FULL_MILK_ACCUMULATION_COUNT +
														  (FULL_MILK_ACCUMULATION_COUNT / 4 ) + 1);
        	
        	if (milkAccumulationCount >= FULL_MILK_ACCUMULATION_COUNT)
        	{
				milkAccumulationCount = 0;
        		
            	setGotMilk(true);
        	}
        }        
    }
    
    @Override
    public boolean isValidZombieSecondaryTarget(EntityZombie zombie)
    {
    	return true;
    }
    
    @Override
    public CowEntity spawnBabyAnimal(EntityAgeable parent )
    {
        return (CowEntity) EntityList.createEntityOfType(CowEntity.class, worldObj );
    }

    @Override
    protected String getLivingSound()
    {
    	if ( !isStarving() )
    	{
    		return "mob.cow.say";
    	}
    	else
    	{
    		return "mob.cow.hurt";
    	}
    }

    @Override
	public void updateHungerState()
    {
    	if (!gotMilk() && isFullyFed() && !isChild() && !getWearingBreedingHarness() )
    	{
			// producing milk consumes extra food. Hunger will be validated in super method
			
			hungerCountdown--;
			
        	milkAccumulationCount++;
        	
        	if (milkAccumulationCount >= FULL_MILK_ACCUMULATION_COUNT)
        	{
        		setGotMilk(true);
				milkAccumulationCount = 0;
        		
		        worldObj.playAuxSFX( BTWEffectManager.COW_REGEN_MILK_EFFECT_ID,
		        	MathHelper.floor_double( posX ), (int)posY + 1, 
		        	MathHelper.floor_double( posZ ), 0 );
        	}
    	}

    	// must call super method after extra hunger consumed above to validate
    	
    	super.updateHungerState();
    }
    
    @Override
    public float knockbackMagnitude()
    {
    	return 0.3F;
    }
    
	//------------- Class Specific Methods ------------//
    
    public void checkForGrazeSideEffects(int i, int j, int k)
    {
    	int iTargetBlockID = worldObj.getBlockId( i, j, k );
    	
    	if ( iTargetBlockID == Block.mycelium.blockID )
    	{
    		convertToMooshroom();
    	}
    }
    
    public void convertToMooshroom()
    {
        int iFXI = MathHelper.floor_double( posX );
        int iFXJ = MathHelper.floor_double( posY ) + 1;
        int iFXK = MathHelper.floor_double( posZ );
        
        int iExtendedFXData = 0;
        
        if ( isChild() )
        {
        	iExtendedFXData = 1;
        }
        
        worldObj.playAuxSFX( BTWEffectManager.COW_CONVERSION_TO_MOOSHROOM_EFFECT_ID, iFXI, iFXJ, iFXK, iExtendedFXData );
        
        setDead();
        
        CowEntity entityMooshroom = (CowEntity) EntityList.createEntityOfType(EntityMooshroom.class, worldObj );
        entityMooshroom.setLocationAndAngles( posX, posY, posZ, rotationYaw, rotationPitch );
        entityMooshroom.setEntityHealth( getHealth() );
        entityMooshroom.renderYawOffset = renderYawOffset;
        entityMooshroom.setGrowingAge( getGrowingAge() );
        
        worldObj.spawnEntityInWorld( entityMooshroom );
    }
    
    public boolean gotMilk()
    {
    	byte bGotMilk = dataWatcher.getWatchableObjectByte(GOT_MILK_DATA_WATCHER_ID);
    	
    	if ( bGotMilk != 0 )
    	{
    		return true;
    	}
    	
    	return false;
    }
    
    protected void setGotMilk(boolean bGotMilk)
    {    	
    	byte byteValue = 0;
    	
    	if ( bGotMilk )
    	{
    		byteValue = 1;
    	}
    	
        dataWatcher.updateObject(GOT_MILK_DATA_WATCHER_ID, Byte.valueOf(byteValue));
    }
    
	private void updateKickAttack()
	{
    	if (kickAttackInProgressCounter >= 0 )
    	{
    		kickAttackInProgressCounter++;
    		
    		if (kickAttackInProgressCounter >= KICK_ATTACK_DURATION)
    		{
				kickAttackInProgressCounter = -1;
    		}
    	}
    	else if ( !worldObj.isRemote ) // attacks are only launched on the server
    	{
            kickAttackCooldownTimer--;
            
    		// check if we should initiate an attack, which only applies if the cow is burning or has a target, which are the same conditions
            // that are used to determine if the cow is panicked and fleeing
            
            if (isEntityAlive() && !isChild() && !getWearingBreedingHarness() && kickAttackCooldownTimer <= 0 && (isBurning() || getAITarget() != null ) )
            {
				Vec3 kickCenter = computeKickAttackCenter();
				
				AxisAlignedBB tipBox = AxisAlignedBB.getAABBPool().getAABB(
						kickCenter.xCoord - KICK_ATTACK_TIP_COLLISION_HALF_WIDTH,
					kickCenter.yCoord - KICK_ATTACK_TIP_COLLISION_HALF_HEIGHT,
						kickCenter.zCoord - KICK_ATTACK_TIP_COLLISION_HALF_WIDTH,
						kickCenter.xCoord + KICK_ATTACK_TIP_COLLISION_HALF_WIDTH,
					kickCenter.yCoord + KICK_ATTACK_TIP_COLLISION_HALF_HEIGHT,
						kickCenter.zCoord + KICK_ATTACK_TIP_COLLISION_HALF_WIDTH);
				
		        List potentialCollisionList = worldObj.getEntitiesWithinAABB( EntityLiving.class, tipBox );
		        
		        if ( !potentialCollisionList.isEmpty() )
		        {
            		boolean bAttackLaunched = false;
            		
    				Vec3 lineOfSightOrigin = Vec3.createVectorHelper( posX, posY + ( height / 2F ), posZ );
    				
    		        Iterator collisionIterator = potentialCollisionList.iterator();
    		        
    		        while ( collisionIterator.hasNext() )
    		        {
    		        	EntityLiving tempEntity = (EntityLiving)collisionIterator.next();
    		        	
    		        	if (!( tempEntity instanceof CowEntity) && tempEntity.isEntityAlive() && tempEntity.ridingEntity != this &&
							canEntityBeSeenForAttackToCenterOfMass(tempEntity, lineOfSightOrigin) )
    		        	{
    		        		bAttackLaunched = true;
    		        		
		        			kickAttackHitTarget(tempEntity);
    		        	}
    		        }
    		        
    		        if ( bAttackLaunched )
    		        {
		        		launchKickAttack();
    		        }
		        }
            }
    	}
	}
		
    public boolean canEntityBeSeenForAttackToCenterOfMass(Entity entity, Vec3 attackOrigin)
    {
        return worldObj.rayTraceBlocks_do_do( attackOrigin, 
        	worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + ( entity.height / 2F ), entity.posZ ), false, true ) == null;
    }
    
	public Vec3 computeKickAttackCenter()
	{		
		float fAttackAngle = MathHelper.wrapAngleTo180_float( rotationYaw + 180F );

        double dPosX = (double)( -MathHelper.sin( fAttackAngle / 180.0F * (float)Math.PI ) ) * KICK_ATTACK_RANGE;
		double dPosY = height / 2F;
        double dPosZ = (double)( MathHelper.cos( fAttackAngle / 180.0F * (float)Math.PI ) ) * KICK_ATTACK_RANGE;
        
		dPosX += posX;		
		dPosY += posY;
		dPosZ += posZ;
		
		return Vec3.createVectorHelper( dPosX, dPosY, dPosZ );
	}	
	
	private void launchKickAttack()
	{
		kickAttackInProgressCounter = 0;
		kickAttackCooldownTimer = KICK_ATTACK_TICKS_TO_COOLDOWN;
		
		transmitKickAttackToClients();
	}
	
    private void transmitKickAttackToClients()
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream( byteStream );
        
        try
        {
	        dataStream.writeInt( entityId );
	        dataStream.writeByte( (byte) EntityEventPacketHandler.COW_KICK_ATTACK_EVENT_ID);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }        
	        
        Packet250CustomPayload packet = new Packet250CustomPayload(BTWPacketManager.CUSTOM_ENTITY_EVENT_PACKET_CHANNEL, byteStream.toByteArray() );
        
        WorldUtils.sendPacketToAllPlayersTrackingEntity((WorldServer)worldObj, this, packet);
    }
    
    public void onClientNotifiedOfKickAttack()
    {
		kickAttackInProgressCounter = 0;

		kickAttackLegUsed = rand.nextInt(2);
    	
        worldObj.playSound( posX, posY, posZ, "random.bow", 1F, 
        	( rand.nextFloat() - rand.nextFloat() ) * 0.2F + 0.5F );    
    }
    
	private void kickAttackHitTarget(Entity hitEntity)
	{
		DamageSource cowSource = DamageSource.causeMobDamage( this );
		
		int kickDamage = 7;
		
		if (hitEntity instanceof EntityPlayer) {
			kickDamage *= this.worldObj.getDifficulty().getCowKickStrengthMultiplier();
		}
		
		if ( hitEntity.attackEntityFrom( cowSource, kickDamage ) )
		{
            if ( isBurning() && rand.nextFloat() < 0.6F )
            {
                hitEntity.setFire( 4 );
            }
            
			hitEntity.onKickedByCow(this);
		}
	}
	
    private boolean birthMutant(EntityAnimal targetMate,
								double dChildX, double dChildY, double dChildZ)
    {
    	int iRandomFactor = rand.nextInt( 20 );
    	
    	if ( iRandomFactor == 0 )
    	{
            CaveSpiderEntity childEntity = (CaveSpiderEntity) EntityList.createEntityOfType(CaveSpiderEntity.class, worldObj);
    		
            if ( childEntity != null )
            {
            	childEntity.setLocationAndAngles( dChildX, dChildY, dChildZ, rotationYaw, rotationPitch );
                
                worldObj.spawnEntityInWorld( childEntity );        
            }
    	}
    	else if ( iRandomFactor < 4 )
    	{
    		for ( int iTempCount = 0; iTempCount < 10; iTempCount++ )
    		{
	            BatEntity childEntity = (BatEntity) EntityList.createEntityOfType(BatEntity.class, worldObj);
	    		
	            if ( childEntity != null )
	            {
	            	childEntity.setLocationAndAngles( dChildX, dChildY, dChildZ, rotationYaw, rotationPitch );
	                
	                worldObj.spawnEntityInWorld( childEntity );        
	            }
    		}
    	}
    	else if ( iRandomFactor < 7 )
    	{
    		for ( int iTempCount = 0; iTempCount < 5; iTempCount++ )
    		{
	            EntitySilverfish childEntity = (EntitySilverfish) EntityList.createEntityOfType(EntitySilverfish.class, worldObj);
	    		
	            if ( childEntity != null )
	            {
	            	childEntity.setLocationAndAngles( dChildX, dChildY, dChildZ, rotationYaw, rotationPitch );
	                
	                worldObj.spawnEntityInWorld( childEntity );        
	            }
    		}
    	}
    	else
    	{
            SquidEntity childEntity = (SquidEntity) EntityList.createEntityOfType(SquidEntity.class, worldObj);
    		
            if ( childEntity != null )
            {
            	childEntity.setLocationAndAngles( dChildX, dChildY, dChildZ, rotationYaw, rotationPitch );
                
                worldObj.spawnEntityInWorld( childEntity );        
            }
    	}

    	return true;
    }
    
    protected void stillBirth(EntityAnimal targetMate, double dChildX, double dChildY, double dChildZ)
    {
    	// just a copy of GiveBirthAtTargetLocation() from parent class that kills the baby after birth
    	
        EntityAgeable childEntity = createChild( targetMate );

        if ( childEntity != null )
        {
            childEntity.setGrowingAge( -childEntity.getTicksForChildToGrow());
            
        	childEntity.setLocationAndAngles( dChildX, dChildY, dChildZ, rotationYaw, rotationPitch );
            
            worldObj.spawnEntityInWorld( childEntity );
            
            childEntity.attackEntityFrom( DamageSource.generic, 20 );
        }        
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public String getTexture()
    {
    	if ( getWearingBreedingHarness() )
    	{
			return "/btwmodtex/fc_mr_cow.png";
    	}
    	
    	int iHungerLevel = getHungerLevel();
    	
    	if ( iHungerLevel == 1 )
    	{
			return "/btwmodtex/fcCowFamished.png";
    	}
    	else if ( iHungerLevel == 2 )
    	{
			return "/btwmodtex/fcCowStarving.png";
    	}
    	
        return super.getTexture();
    }
}
