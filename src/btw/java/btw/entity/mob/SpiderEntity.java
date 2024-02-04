// FCMOD

package btw.entity.mob;

import btw.client.fx.BTWEffectManager;
import btw.entity.SpiderWebEntity;
import btw.item.BTWItems;
import net.minecraft.src.*;

import java.util.Iterator;
import java.util.List;

public class SpiderEntity extends EntitySpider
{
    private static final double SPIDER_ATTACK_RANGE = 16.0D;
    private static final int TIME_BETWEEN_WEBS = (20 * 60 * 20 ); // a full day
    
    protected int timeToNextWeb = 0;

    public SpiderEntity(World world )
    {
        super( world );
    }
    
    @Override
    public void writeEntityToNBT( NBTTagCompound tag )
    {
        super.writeEntityToNBT( tag );
        
        tag.setInteger("timeToWeb", timeToNextWeb);
    }
    
    @Override
    public void readEntityFromNBT( NBTTagCompound tag )
    {
        super.readEntityFromNBT( tag );
        
        if ( tag.hasKey( "timeToWeb" ) )
        {
            timeToNextWeb = tag.getInteger("timeToWeb");
        }
    }
    
    @Override
    public void initCreature()
    {
        if ( worldObj.rand.nextInt( 100 ) == 0 )
        {
            SkeletonEntity jockey = (SkeletonEntity) EntityList.createEntityOfType(SkeletonEntity.class, worldObj);
            jockey.setLocationAndAngles( posX, posY, posZ, rotationYaw, 0F );
            jockey.initCreature();
            
            worldObj.spawnEntityInWorld( jockey );
            
            jockey.mountEntity( this );
        }
    }
    
    @Override
    public void spawnerInitCreature()
    {
    	// does not call super or initCreature() to override spider jokey generation

        timeToNextWeb = TIME_BETWEEN_WEBS;
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        Entity targetEntity = null;
        
        if ((!this.doesLightAffectAggessiveness() || this.getBrightness(1F) < 0.5F)
				&& !this.isAlwaysNeutral())
        {            
        	targetEntity = worldObj.getClosestVulnerablePlayerToEntity(this, SPIDER_ATTACK_RANGE);
        }
        
        if ( targetEntity == null )
        {
        	List chickenList = worldObj.getEntitiesWithinAABB( ChickenEntity.class,
        		boundingBox.expand(SPIDER_ATTACK_RANGE, 4D, SPIDER_ATTACK_RANGE));
        	
            Iterator chickenIterator = chickenList.iterator();
            
            double dClosestChickenDistSq = SPIDER_ATTACK_RANGE * SPIDER_ATTACK_RANGE + 1D;

            while ( chickenIterator.hasNext() )
            {
                ChickenEntity chicken = (ChickenEntity)chickenIterator.next();
                
                if ( !chicken.isLivingDead )
                {
                	double dDeltaX = posX - chicken.posX;
                	double dDeltaY = posY - chicken.posY;
                	double dDeltaZ = posZ - chicken.posZ;
                	
                	double dDistSq = ( dDeltaX * dDeltaX ) + ( dDeltaY * dDeltaY ) + ( dDeltaZ * dDeltaZ );
                	
                	if ( dDistSq < dClosestChickenDistSq )
                	{
                		targetEntity = chicken;
                		dClosestChickenDistSq = dDistSq;
                	}
                }
            }
        }
        
        return targetEntity;
    }

	@Override
    public void setRevengeTarget( EntityLiving target )
    {
		// override to lengthen revenge time
		
        entityLivingToAttack = target;
        
        if ( entityLivingToAttack != null )
        {
        	revengeTimer = 200; // 10 seconds
        }
        else
        {
        	revengeTimer = 0;
        }
    }
    
    @Override
    protected boolean shouldContinueAttacking(float fDistanceToTarget)
    {
        if ( revengeTimer <= 0 && doesLightAffectAggessiveness() && rand.nextInt(600) == 0 &&
        	getBrightness( 1.0F ) > 0.5F && entityToAttack instanceof EntityPlayer && !canEntityBeSeen( entityToAttack )  )
        {
            return false;
        }
        
    	return true;
    }

    @Override
    protected void attackEntity( Entity targetEntity, float fDistanceToTarget )
    {
    	if ( fDistanceToTarget < 2F )
    	{
        	if ( targetEntity instanceof EntityAnimal )
        	{
            	// extend reach on zombies slightly to avoid problems attacking animals due to their elongated bodies
            	
                if ( attackTime <= 0 )
                {
                    attackTime = 20;
                    attackEntityAsMob(targetEntity);
                }
        	}
        	else
        	{
        		entityMobAttackEntity(targetEntity, fDistanceToTarget);
        	}    	
    	}
    	else if ( fDistanceToTarget < 6F )
        {
            if ( onGround && rand.nextInt( 10 ) == 0 )
            {
            	// jump at the target
            	
                double var4 = targetEntity.posX - this.posX;
                double var6 = targetEntity.posZ - this.posZ;
                float var8 = MathHelper.sqrt_double(var4 * var4 + var6 * var6);
                this.motionX = var4 / (double)var8 * 0.5D * 0.800000011920929D + this.motionX * 0.20000000298023224D;
                this.motionZ = var6 / (double)var8 * 0.5D * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
                this.motionY = 0.4000000059604645D;
            }
        }
        else if ( fDistanceToTarget < 10F )
    	{
        	if (hasWeb() && !isEntityInWeb(targetEntity) && rand.nextInt(10) == 0 && !( targetEntity instanceof SpiderEntity) )
        	{
        		spitWeb(targetEntity);
        	}
    	}            	
    }
    
    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iLootingModifier )
    {
        if ( hasWeb() )
        {
            entityLivingDropFewItems(bKilledByPlayer, iLootingModifier);
        }

        if (dropsSpiderEyes() && rand.nextInt(8) - (iLootingModifier << 1 ) <= 0 )
        {
            dropItem( Item.spiderEye.itemID, 1 );
        }
    }
    
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect)
    {
    	return true;
    }
    
    @Override
    public void onLivingUpdate()
    {
    	checkForLooseFood();
    	checkForSpiderSkeletonMounting();
    	
    	if (timeToNextWeb > 0 )
    	{
    		timeToNextWeb--;
    	}
    	
    	super.onLivingUpdate();
    }
    
    @Override
    public void checkForScrollDrop()
    {    	
    	if ( rand.nextInt( 1000 ) == 0 )
    	{
            ItemStack itemstack = new ItemStack( BTWItems.arcaneScroll, 1, Enchantment.baneOfArthropods.effectId );
            
            entityDropItem(itemstack, 0.0F);
    	}
    }
    
	@Override
    public boolean isAffectedByMovementModifiers()
    {
    	return false;
    }
	
    //------------- Class Specific Methods ------------//
	
    protected boolean dropsSpiderEyes()
    {
    	return true;
    }
    
    public boolean hasWeb()
    {    	
    	return timeToNextWeb <= 0;
    }
    
    public boolean isEntityInWeb(Entity targetEntity)
    {
        return worldObj.isMaterialInBB( targetEntity.boundingBox, Material.web );
    }
    
    private void spitWeb(Entity targetEntity)
    {
    	if ( !worldObj.isRemote )
    	{
            worldObj.spawnEntityInWorld( EntityList.createEntityOfType(SpiderWebEntity.class, worldObj, this, targetEntity ) );

            timeToNextWeb = TIME_BETWEEN_WEBS;
    	}
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
	    		
		        if ( itemEntity.delayBeforeCanPickup == 0 && itemEntity.isEntityAlive() )
		        {
		        	// client
		        	ItemStack itemStack = itemEntity.getEntityItem();
		        	// server
		        	//ItemStack itemStack = itemEntity.func_92059_d();
		        	
		        	Item item = itemStack.getItem();
		        	
		        	if ( item.itemID == Item.chickenRaw.itemID || item.itemID == BTWItems.rawMysteryMeat.itemID )
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
	
	public boolean doesLightAffectAggessiveness()
	{
		return true;
	}
	
	public boolean isAlwaysNeutral() {
		return false;
	}
	
	public boolean doEyesGlow()
	{
		return true;
	}	
    
	protected void checkForSpiderSkeletonMounting()
	{
	    if ( !worldObj.isRemote && isEntityAlive() && riddenByEntity == null )
	    {
	        List itemList = worldObj.getEntitiesWithinAABB(SkeletonEntity.class, getSpiderJockeyCollisionBoxFromPool());
	        
	        Iterator itemIterator = itemList.iterator();
	
	        while ( itemIterator.hasNext())
	        {
	        	SkeletonEntity tempSkeleton = (SkeletonEntity)itemIterator.next();
	        	
	        	if ( tempSkeleton != entityToAttack && tempSkeleton.entityToAttack != this &&  
	        		tempSkeleton.ridingEntity == null )
	        	{
	        		tempSkeleton.mountEntity( this );
	        	}
	        }		        
		}
	}

	private AxisAlignedBB getSpiderJockeyCollisionBoxFromPool()
	{
		double dWidthOffset = width / 16F; 
			
        return AxisAlignedBB.getAABBPool().getAABB( 
        	boundingBox.minX + dWidthOffset, boundingBox.maxY, boundingBox.minZ + dWidthOffset, 
        	boundingBox.maxX - dWidthOffset, boundingBox.maxY + 0.1F, boundingBox.maxZ - dWidthOffset );
	}
	
	//----------- Client Side Functionality -----------//
}
