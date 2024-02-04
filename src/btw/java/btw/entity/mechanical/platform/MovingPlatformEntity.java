// FCMOD

package btw.entity.mechanical.platform;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.entity.IgnoreServerValidationEntity;
import btw.entity.EntityWithCustomPacket;
import btw.entity.mechanical.source.MechanicalPowerSourceEntity;
import btw.item.util.ItemUtils;
import btw.util.MiscUtils;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class MovingPlatformEntity extends Entity
	implements EntityWithCustomPacket, IgnoreServerValidationEntity
{
	// constants
	
	private static final int Y_MOTION_DATA_WATCHER_ID = 22;
	
    static final private int VEHICLE_SPAWN_PACKET_TYPE = 103;
    
    // local vars

	// the following tracking variables are necessary
	private double associatedAnchorLastKnownXPos;
	private double associatedAnchorLastKnownYPos;
	private double associatedAnchorLastKnownZPos;
	
    public MovingPlatformEntity(World world )
    {
        super( world );
        
        preventEntitySpawning = true;
        
        setSize( 0.98F, 0.98F );        
        yOffset = height / 2.0F;
        
        motionX = 0.0D;        
    	motionY = 0.0D;
        motionZ = 0.0D;

		associatedAnchorLastKnownXPos = 0.0D;
		associatedAnchorLastKnownYPos = 0.0D;
		associatedAnchorLastKnownZPos = 0.0D;
    }
    
    public MovingPlatformEntity(World world, double x, double y, double z )
    {
    	this(world, x, y, z, null);
    }
    
    public MovingPlatformEntity(World world, double x, double y, double z,
								MovingAnchorEntity entityMovingAnchor )
    {
        this( world );
        
        if ( entityMovingAnchor != null )
        {
			associatedAnchorLastKnownXPos = entityMovingAnchor.posX;
			associatedAnchorLastKnownYPos = entityMovingAnchor.posY;
			associatedAnchorLastKnownZPos = entityMovingAnchor.posZ;
	        
	        motionY = entityMovingAnchor.motionY;
        }
        
        setPosition( x, y, z );
        
        lastTickPosX = prevPosX = x;
        lastTickPosY = prevPosY = y;
        lastTickPosZ = prevPosZ = z;        
    }   
    
	@Override
    protected void entityInit()
    {
        dataWatcher.addObject(Y_MOTION_DATA_WATCHER_ID, new Integer(0 ));
    }
    
	@Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
    	nbttagcompound.setDouble("m_AssociatedAnchorLastKnownXPos", associatedAnchorLastKnownXPos);
    	nbttagcompound.setDouble("m_AssociatedAnchorLastKnownYPos", associatedAnchorLastKnownYPos);
    	nbttagcompound.setDouble("m_AssociatedAnchorLastKnownZPos", associatedAnchorLastKnownZPos);
    }

	@Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
		associatedAnchorLastKnownXPos = nbttagcompound.getDouble("m_AssociatedAnchorLastKnownXPos");
		associatedAnchorLastKnownYPos = nbttagcompound.getDouble("m_AssociatedAnchorLastKnownYPos");
		associatedAnchorLastKnownZPos = nbttagcompound.getDouble("m_AssociatedAnchorLastKnownZPos");
    }
    
	@Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

	@Override
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        return entity.boundingBox;
    }

	@Override
    public AxisAlignedBB getBoundingBox()
    {
        return boundingBox;
    }

	@Override
    public boolean canBePushed()
    {
        return false;
    }

	@Override
    public boolean canBeCollidedWith()
    {
        return !isDead;
    }
    
	@Override
    public void onUpdate()
    {
    	if ( isDead )
    	{
    		return;
    	}
    
    	if ( worldObj.isRemote )
    	{
        	motionY = getCorseYMotion();
    	}
    	
		MovingAnchorEntity associatedMovingAnchor = null;
		
		boolean bPauseMotion = false;
		
		int i = MathHelper.floor_double( posX );
    	int oldCenterJ = MathHelper.floor_double( posY );
		int k = MathHelper.floor_double( posZ );
		
    	if ( !worldObj.isRemote )
    	{
			// find our anchor
			
	        List list = worldObj.getEntitiesWithinAABB( MovingAnchorEntity.class,
	    		AxisAlignedBB.getAABBPool().getAABB(
						associatedAnchorLastKnownXPos - 0.25F, associatedAnchorLastKnownYPos - 0.25F, associatedAnchorLastKnownZPos - 0.25F,
						associatedAnchorLastKnownXPos + 0.25F, associatedAnchorLastKnownYPos + 0.25F, associatedAnchorLastKnownZPos + 0.25F));
				
	        if ( list != null && list.size() > 0 )
	        {        	
	        	associatedMovingAnchor = (MovingAnchorEntity)list.get( 0 );
	        	
	        	if ( !associatedMovingAnchor.isDead )
	        	{
			    	motionY = associatedMovingAnchor.posY - associatedAnchorLastKnownYPos;//associatedMovingAnchor.motionY;
			    	
			    	if ( motionY < 0.01D && motionY > -0.01D )
			    	{
			    		// the anchor has stopped moving, likely due to chunk load, so pause our motion here.
			    		
			    		motionY = 0.0D;
			    		bPauseMotion = true;		    		
			    	}

					associatedAnchorLastKnownXPos = associatedMovingAnchor.posX;
					associatedAnchorLastKnownYPos = associatedMovingAnchor.posY;
					associatedAnchorLastKnownZPos = associatedMovingAnchor.posZ;
	        	}
	        	else
	        	{
	        		associatedMovingAnchor = null;
	        	}
	        }
	        
	        setCorseYMotion(motionY);
    	}
    	
    	// move the platform    	
    	
    	double oldPosY = posY;
    	
    	moveEntityInternal(motionX, motionY, motionZ);
    	
    	double newPosY = posY;
    	
        // handle collisions
        
    	List collisionList = worldObj.getEntitiesWithinAABBExcludingEntity( this, 
    		boundingBox.expand( 0.0D, 0.15D, 0.0D ) );
        
        if ( collisionList != null && collisionList.size() > 0 )
        {
            for(int j1 = 0; j1 < collisionList.size(); j1++)
            {
                Entity entity = (Entity)collisionList.get(j1);
   
                if( entity.canBePushed() || ( entity instanceof EntityItem ) || ( entity instanceof EntityXPOrb ) )
                {
                    pushEntity(entity);
                }
                else if ( !entity.isDead )
                {
	                if ( entity instanceof MechanicalPowerSourceEntity)
	                {
	                	MechanicalPowerSourceEntity entityDevice = (MechanicalPowerSourceEntity)entity;
	                	
	                	entityDevice.destroyWithDrop();
	                }
                }
            }
        }
        
    	if ( !worldObj.isRemote )
    	{
	    	if ( associatedMovingAnchor == null )
	    	{
	    		// the anchor has come to a stop or otherwise dissapeared and we should do the same
	    		
				convertToBlock(i, oldCenterJ, k, null, motionY > 0.0F);
				
				return;
	    	}    	
	    	
	    	if ( !bPauseMotion )
	    	{
				if ( motionY > 0.0F )
				{
					// moving upwards
					
			    	int newTopJ = MathHelper.floor_double( newPosY + 0.49F );
			    	
		    		// we're entering a new block
		    		
					int iTargetBlockID = worldObj.getBlockId( i, newTopJ, k );
					
					if ( !WorldUtils.isReplaceableBlock(worldObj, i, newTopJ, k) )
					{
				    	if ( !Block.blocksList[iTargetBlockID].blockMaterial.isSolid() || iTargetBlockID == Block.web.blockID ||
			        		iTargetBlockID == BTWBlocks.web.blockID )
				    	{
				    		int iTargetMetadata = worldObj.getBlockMetadata( i, newTopJ, k );
				    		
				    		// we've collided with a non-solid block.  Destroy it.
				    		
				    		Block.blocksList[iTargetBlockID].dropBlockAsItem( 
								worldObj, i, newTopJ, k, iTargetMetadata, 0 );
				    		
				    		worldObj.setBlockWithNotify( i, newTopJ, k, 0 );
				    		
		    		        worldObj.playAuxSFX( BTWEffectManager.DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID,
		    		        	i, newTopJ, k, iTargetBlockID + ( iTargetMetadata << 12 ) );
						}
				    	else
				    	{
							// we've collided with something.  Stop movement of the entire platform
							
							convertToBlock(i, oldCenterJ, k, associatedMovingAnchor, true);
							
							associatedMovingAnchor.forceStopByPlatform();
							
							return;
				    	}
					}
				}
				else
				{
					// moving downwards
					
			    	int newBottomJ = MathHelper.floor_double( newPosY - 0.49F );
			    	
					int iTargetBlockID = worldObj.getBlockId( i, newBottomJ, k );
						
					if ( !WorldUtils.isReplaceableBlock(worldObj, i, newBottomJ, k) )
					{
				    	if ( !Block.blocksList[iTargetBlockID].blockMaterial.isSolid() || iTargetBlockID == Block.web.blockID ||
				    		iTargetBlockID == BTWBlocks.web.blockID )
				    	{
				    		int iTargetMetadata = worldObj.getBlockMetadata( i, newBottomJ, k );
				    		
				    		// we've collided with a non-solid block.  Destroy it.
				    		
				    		Block.blocksList[iTargetBlockID].dropBlockAsItem( 
								worldObj, i, newBottomJ, k, iTargetMetadata, 0 );
				    		
				    		worldObj.setBlockWithNotify( i, newBottomJ, k, 0 );
				    		
		    		        worldObj.playAuxSFX( BTWEffectManager.DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID,
		    		        	i, newBottomJ, k, iTargetBlockID + ( iTargetMetadata << 12 ) );
						}
				    	else
				    	{
		    				// we've collided with something.  Stop movement of the entire platform    				
		    				
		    				convertToBlock(i, oldCenterJ, k, associatedMovingAnchor, false);
		    				
		    				associatedMovingAnchor.forceStopByPlatform();
		    				
		    				return;
				    	}
			    	}
				}
	    	}
    	}
	}
    
	@Override
    public void moveEntity( double deltaX, double deltaY, double deltaZ )
    {
    	// this might be called by external sources (like the pistons), so we have to override it
    	
    	// since we are already dealing with a moving platform here, and since handling it any other way 
    	// would result in a ton of exception cases forming, just destroy the platform outright.
    	
    	destroyPlatformWithDrop();
    }
    
    @Override
    protected boolean shouldSetPositionOnLoad()
    {
    	return false;
    }
    
    //************* FCIEntityPacketHandler ************//

    @Override
    public Packet getSpawnPacketForThisEntity()
    {
		return new Packet23VehicleSpawn(this, getVehicleSpawnPacketType(), 0 );
    }
    
    @Override
    public int getTrackerViewDistance()
    {
    	return 160;
    }
    
    @Override
    public int getTrackerUpdateFrequency()
    {
    	return 3;
    }
    
    @Override
    public boolean getTrackMotion()
    {
    	return false;
    }
    
    @Override
    public boolean shouldServerTreatAsOversized()
    {
    	return false;
    }
    
    //************* Class Specific Methods ************//

    private double getCorseYMotion()
    {
        return (double)( dataWatcher.getWatchableObjectInt(Y_MOTION_DATA_WATCHER_ID) ) / 100F;
    }
    
    private void setCorseYMotion(double yMotion)
    {
        dataWatcher.updateObject(Y_MOTION_DATA_WATCHER_ID, Integer.valueOf((int)(yMotion * 100F )));
    }
    
    static public int getVehicleSpawnPacketType()
    {
    	return VEHICLE_SPAWN_PACKET_TYPE;
    }
    
    public void destroyPlatformWithDrop()
    {
		int i = MathHelper.floor_double( posX );
    	int j = MathHelper.floor_double( posY );
		int k = MathHelper.floor_double( posZ );
		
		ItemStack platformStack = new ItemStack( BTWBlocks.platform);
		
		ItemUtils.ejectStackWithRandomOffset(worldObj, i, j, k, platformStack);
		
    	setDead();
    }
    
    private void moveEntityInternal(double deltaX, double deltaY, double deltaZ)
    {
    	double newPosX = posX + deltaX;
    	double newPosY = posY + deltaY;
    	double newPosZ = posZ + deltaZ;
    	
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        
        setPosition( newPosX, newPosY, newPosZ );     
        
        testForBlockCollisions();
    }
    
    private void testForBlockCollisions()
    {
    	// FCTODO: Code ripped out of Entity.java.  Clean up and rename variables appropriately
    	
        int i1 = MathHelper.floor_double(boundingBox.minX + 0.001D);
        int k1 = MathHelper.floor_double(boundingBox.minY + 0.001D);
        int i2 = MathHelper.floor_double(boundingBox.minZ + 0.001D);
        int k3 = MathHelper.floor_double(boundingBox.maxX - 0.001D);
        int l3 = MathHelper.floor_double(boundingBox.maxY - 0.001D);
        int i4 = MathHelper.floor_double(boundingBox.maxZ - 0.001D);
        
        if(worldObj.checkChunksExist(i1, k1, i2, k3, l3, i4))
        {
            for(int j4 = i1; j4 <= k3; j4++)
            {
                for(int k4 = k1; k4 <= l3; k4++)
                {
                    for(int l4 = i2; l4 <= i4; l4++)
                    {
                        int i5 = worldObj.getBlockId(j4, k4, l4);
                        if(i5 > 0)
                        {
                            Block.blocksList[i5].onEntityCollidedWithBlock(worldObj, j4, k4, l4, this);
                        }
                    }
                }
            }
        }
    }
    
    private void pushEntity(Entity entity)
    {
    	double testZoneMaxY = boundingBox.maxY + 0.075;
    	
    	double entityMinY = entity.boundingBox.minY;
    	
    	if ( entityMinY < testZoneMaxY )
    	{
    		if ( entityMinY > boundingBox.maxY - 0.5D )
    		{
    			// only update player pos on the client
    			
    			if ( entity instanceof EntityPlayer )
    			{
    				if ( worldObj.isRemote )
    				{
    					// client only
    					clientPushPlayer(entity);
    				}
    			}    			
    			else
    			{
		    		double entityYOffset = ( boundingBox.maxY + 0.01D ) - entityMinY;
		    		
	    			entity.setPosition( entity.posX, entity.posY + entityYOffset, entity.posZ );
	    			
		    		if ( entity.riddenByEntity != null )
		    		{
			    		entity.riddenByEntity.setPosition( entity.riddenByEntity.posX, entity.riddenByEntity.posY + entityYOffset, entity.riddenByEntity.posZ );		    		
		    		}
    			}
    		}
    		else if ( entity instanceof EntityLiving )
    		{
    			if ( motionY < 0F )
    			{
        			double entityMaxY = entity.boundingBox.maxY;
        			
        			if ( boundingBox.minY < entityMaxY - 0.25D && testZoneMaxY > entityMaxY )
        			{
        				entity.attackEntityFrom( DamageSource.inWall, 1 );
        			}
    			}
    		}
    	}
    }

    // associatedAnchor can be null, should only be called on the server
    private void convertToBlock(int i, int j, int k, MovingAnchorEntity associatedAnchor, boolean bMovingUpwards)
    {
    	boolean moveEntities = true;
    	
    	int iTargetBlockID = worldObj.getBlockId( i, j, k );
    	
    	if ( WorldUtils.isReplaceableBlock(worldObj, i, j, k) )
    	{
    		worldObj.setBlockWithNotify( i, j, k, BTWBlocks.platform.blockID );
    	}
    	else if ( !Block.blocksList[iTargetBlockID].blockMaterial.isSolid() || iTargetBlockID == Block.web.blockID ||
    		iTargetBlockID == BTWBlocks.web.blockID )
    	{
    		int iTargetMetadata = worldObj.getBlockMetadata( i, j, k );
    		
    		Block.blocksList[iTargetBlockID].dropBlockAsItem( 
				worldObj, i, j, k, iTargetMetadata, 0 );
    		
	        worldObj.playAuxSFX( BTWEffectManager.DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID,
	        	i, j, k, iTargetBlockID + ( iTargetMetadata << 12 ) );
	        
    		worldObj.setBlockWithNotify( i, j, k, BTWBlocks.platform.blockID );
		}
    	else
    	{
    		// this shouldn't usually happen, but if the block is already occupied, eject the platform
    		// as an item
    		
			ItemUtils.ejectSingleItemWithRandomOffset(worldObj, i, j, k,
                                                      BTWBlocks.platform.blockID, 0);
			
			moveEntities = false;
    	}
    	
    	MiscUtils.positionAllNonPlayerMoveableEntitiesOutsideOfLocation(worldObj, i, j, k);
    	
		// FCTODO: hacky way of making sure players don't fall through platforms when they stop
		
    	if ( !bMovingUpwards )
    	{
    		MiscUtils.serverPositionAllPlayerEntitiesOutsideOfLocation(worldObj, i, j + 1, k);
    		MiscUtils.serverPositionAllPlayerEntitiesOutsideOfLocation(worldObj, i, j, k);
    	}
    	else
    	{
    		MiscUtils.serverPositionAllPlayerEntitiesOutsideOfLocation(worldObj, i, j - 1, k);
    		MiscUtils.serverPositionAllPlayerEntitiesOutsideOfLocation(worldObj, i, j, k);
    	}
    	
    	setDead();
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
    	// empty override to prevent network entity teleport packets from fucking up position and bounding box
    	//super.setPositionAndRotation2( par1, par3, par5, par7, par8, par9 );
    	setPosition( par1, par3, par5 );
    }

    @Environment(EnvType.CLIENT)
    private void clientPushPlayer(Entity entity)
	{
    	double entityMinY = entity.boundingBox.minY;
    	
		double entityYOffset = ( boundingBox.maxY + 0.01D ) - entityMinY;
		
		entity.setPosition( entity.posX, entity.posY + entityYOffset, entity.posZ );
		
		entity.serverPosX = (int)( entity.posX * 32D );
		entity.serverPosY = (int)( entity.posY * 32D );
		entity.serverPosZ = (int)( entity.posZ * 32D );
		
		if ( entity.riddenByEntity != null )
		{
    		entity.riddenByEntity.setPosition( entity.riddenByEntity.posX, entity.riddenByEntity.posY + entityYOffset, entity.riddenByEntity.posZ );		    		
		}
		
		entity.onGround = true;
	}
}