// FCMOD

package btw.entity.mechanical.platform;

import btw.block.BTWBlocks;
import btw.block.blocks.AnchorBlock;
import btw.block.blocks.RopeBlock;
import btw.block.tileentity.PulleyTileEntity;
import btw.client.fx.BTWEffectManager;
import btw.entity.IgnoreServerValidationEntity;
import btw.entity.EntityWithCustomPacket;
import btw.entity.mechanical.source.MechanicalPowerSourceEntity;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class MovingAnchorEntity extends Entity
	implements EntityWithCustomPacket, IgnoreServerValidationEntity
{
	// constants
	
    static final public float MOVEMENT_SPEED = 1.0F / 20.0F;
    
	private static final int Y_MOTION_DATA_WATCHER_ID = 22;
    
    static final private int VEHICLE_SPAWN_PACKET_TYPE = 102;
    
    // local vars
    
    private BlockPos associatedPulleyPos = new BlockPos();
    private int associatedPulleyRopeStateCounter;
    private int oldBottomY;
    
    public MovingAnchorEntity(World world )
    {
        super( world );
        
        preventEntitySpawning = true;
        
        setSize( 0.98F, 1.98F );        
        yOffset = 0.5F;
        
        motionX = 0.0D;        
    	motionY = 0.0D;
        motionZ = 0.0D;

		associatedPulleyRopeStateCounter = -1;

		oldBottomY = 0;
    }
    
    /*
     * Used client-side for basic init
     */ 
    public MovingAnchorEntity(World world, double x, double y, double z )
    {
        this( world );
        
        setPosition( x, y, z );
        
        lastTickPosX = prevPosX = x;
        lastTickPosY = prevPosY = y;
        lastTickPosZ = prevPosZ = z;

		oldBottomY = MathHelper.floor_double(posY - yOffset);
    }
    
    public MovingAnchorEntity(World world, double x, double y, double z,
							  BlockPos pulleyPos, int iMovementDirection )
    {
        this( world );
        
        associatedPulleyPos.x = pulleyPos.x;
        associatedPulleyPos.y = pulleyPos.y;
        associatedPulleyPos.z = pulleyPos.z;
        
        if ( iMovementDirection > 0 )
        {
        	motionY = MOVEMENT_SPEED;
        }
        else
        {
        	motionY = -MOVEMENT_SPEED;
        }
        
        setPosition( x, y, z );
        
        lastTickPosX = prevPosX = x;
        lastTickPosY = prevPosY = y;
        lastTickPosZ = prevPosZ = z;
        
    	int associatedPulleyBlockID = worldObj.getBlockId(associatedPulleyPos.x,
														  associatedPulleyPos.y, associatedPulleyPos.z);
    	
    	if ( associatedPulleyBlockID == BTWBlocks.pulley.blockID )
    	{   
        	PulleyTileEntity tileEntityPulley = (PulleyTileEntity)worldObj.getBlockTileEntity(associatedPulleyPos.x,
																							  associatedPulleyPos.y, associatedPulleyPos.z);
        	
        	if ( tileEntityPulley != null )
        	{
				associatedPulleyRopeStateCounter = tileEntityPulley.updateRopeStateCounter;
        	}
    	}

		oldBottomY = MathHelper.floor_double(posY - yOffset);
    }
    
	@Override
    protected void entityInit()
    {
        dataWatcher.addObject(Y_MOTION_DATA_WATCHER_ID, new Integer(0 ));
    }
    
	@Override
    protected void writeEntityToNBT( NBTTagCompound nbttagcompound )
    {
    	nbttagcompound.setInteger( "associatedPulleyPosI", associatedPulleyPos.x);
    	nbttagcompound.setInteger( "associatedPulleyPosJ", associatedPulleyPos.y);
    	nbttagcompound.setInteger( "associatedPulleyPosK", associatedPulleyPos.z);
    	
    	nbttagcompound.setInteger("m_iAssociatedPulleyRopeStateCounter", associatedPulleyRopeStateCounter);
    	
    	nbttagcompound.setInteger("m_iOldBottomJ", oldBottomY);
    	
    }    	

	@Override
    protected void readEntityFromNBT( NBTTagCompound nbttagcompound )
    {
    	associatedPulleyPos.x = nbttagcompound.getInteger("associatedPulleyPosI");
    	associatedPulleyPos.y = nbttagcompound.getInteger("associatedPulleyPosJ");
    	associatedPulleyPos.z = nbttagcompound.getInteger("associatedPulleyPosK");
    	
        if ( nbttagcompound.hasKey( "m_iAssociatedPulleyRopeStateCounter" ) )
        {
			associatedPulleyRopeStateCounter = nbttagcompound.getInteger("m_iAssociatedPulleyRopeStateCounter");
        }
        
        if ( nbttagcompound.hasKey( "m_iOldBottomJ" ) )
        {
			oldBottomY = nbttagcompound.getInteger("m_iOldBottomJ");
        }
        else
        {
			oldBottomY = MathHelper.floor_double(posY - yOffset);
        }
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
    	// this determines the actual collision box around the object
    	
    	return AxisAlignedBB.getBoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.minZ,
                                            boundingBox.maxX, boundingBox.minY + AnchorBlock.anchorBaseHeight, boundingBox.maxZ);
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
    	
		int i = MathHelper.floor_double( posX );
		int k = MathHelper.floor_double( posZ );

		// The following is an increased radius version of the test to see whether an entity should be updated in world.java.
		// By extending the range, it ensures that an anchor won't update unless all the platforms that may be attached to it
		// are capable of updating as well.  This prevents Anchor/Platform assemblies from being broken apart during chunk save/load
		
        byte checkChunksRange = 35;

        if ( !worldObj.checkChunksExist( i - checkChunksRange, 0, k - checkChunksRange, i + checkChunksRange, 0, k + checkChunksRange ) )
        {
            return;
        }
	        
    	PulleyTileEntity tileEntityPulley = null;
		int iBlockAboveID = worldObj.getBlockId(i, oldBottomY + 1, k);
		boolean bForceValidation = false;
    	
		if ( !worldObj.isRemote )
		{
	    	int associatedPulleyBlockID = worldObj.getBlockId(associatedPulleyPos.x,
															  associatedPulleyPos.y, associatedPulleyPos.z);
	        	
			int i2BlockAboveID = worldObj.getBlockId(i, oldBottomY + 2, k);
			
			boolean bPauseMotion = false;
			
	    	if ( associatedPulleyBlockID == BTWBlocks.pulley.blockID )
	    	{   
	    		// check for broken rope
	    		
	    		if ( iBlockAboveID == BTWBlocks.pulley.blockID ||
					iBlockAboveID == BTWBlocks.ropeBlock.blockID ||
					i2BlockAboveID == BTWBlocks.pulley.blockID ||
					i2BlockAboveID == BTWBlocks.ropeBlock.blockID )
	    		{
		    		tileEntityPulley = (PulleyTileEntity)worldObj.getBlockTileEntity(associatedPulleyPos.x,
																					 associatedPulleyPos.y, associatedPulleyPos.z);
		    		
		    		if (associatedPulleyRopeStateCounter != tileEntityPulley.updateRopeStateCounter)
		    		{
			    		if ( motionY > 0.0F )
			    		{
			    			// moving upwards
			    			
			        		if ( tileEntityPulley.isLowering() )
			        		{
			        			// if the pulley has switched direction, change motion to match immediately
			        			
			        			motionY = -motionY;
			        			
			        			bForceValidation = true;
			        		}
			    		}
			    		else
			    		{
			    			// moving downwards
			    			
			        		if ( tileEntityPulley.isRaising() )
			        		{
			        			// if the pulley has switched direction, change motion to match immediately
			        			
			        			motionY = -motionY;
			        			
			        			bForceValidation = true;
			        		}
			    		}

						associatedPulleyRopeStateCounter = tileEntityPulley.updateRopeStateCounter;
		    		}
		    		else
		    		{
		    			// the Pulley hasn't updated, perhaps due to chunk load.  Pause the anchor's motion until it updates again.
		    			
		    			return;	    			
		    		}		    		
	    		}
	    		
		        setCorseYMotion(motionY);
	    	}
	    	
	        if ( motionY <= 0.01 && motionY >= -0.01 )
	        {
	        	// we've stopped for some reason.  Convert
	        	
				convertToBlock(i, oldBottomY, k);
				
		        return;	        
	        }
		}
        
    	moveEntityInternal(motionX, motionY, motionZ);
    	
    	double newPosY = posY;
    	
    	int newBottomJ = MathHelper.floor_double( newPosY - yOffset );
    	
        // handle collisions with other entities
        
        List list = 
        	worldObj.getEntitiesWithinAABBExcludingEntity( this, getBoundingBox().expand( 0.0D, 0.15D, 0.0D ) );
        
        if ( list != null && list.size() > 0 )
        {
            for(int j1 = 0; j1 < list.size(); j1++)
            {
                Entity entity = (Entity)list.get(j1);
                
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
	        if (oldBottomY != newBottomJ || bForceValidation )
	        {
				if ( motionY > 0.0F )
				{
					// moving upwards
					
					if ( worldObj.getBlockId( i, newBottomJ, k ) == BTWBlocks.ropeBlock.blockID )
					{
						tileEntityPulley.attemptToRetractRope();
					}
					
					int iTargetBlockID = worldObj.getBlockId( i, newBottomJ + 1, k );
	
					if ( iTargetBlockID != BTWBlocks.ropeBlock.blockID ||
						tileEntityPulley == null ||
                         !tileEntityPulley.isRaising() ||
						newBottomJ + 1 >= associatedPulleyPos.y)
					{
						// we've reached the top of our rope or the pulley is no longer raising.  
						// Stop our movement.
						
						convertToBlock(i, newBottomJ, k);
						
						return;
					}
				}
				else
				{
					// moving downwards
					
					boolean bEnoughRope = false;
					
					if ( tileEntityPulley != null )
					{
						int iRopeRequiredToDescend = 2;
						
			    		if ( iBlockAboveID == BTWBlocks.pulley.blockID ||
		    				iBlockAboveID == BTWBlocks.ropeBlock.blockID )
			    		{
		    				iRopeRequiredToDescend = 1;
		    				
		    				int iOldBlockID = worldObj.getBlockId(i, oldBottomY, k);
		    				
		    	    		if ( iOldBlockID == BTWBlocks.pulley.blockID ||
			    				iOldBlockID == BTWBlocks.ropeBlock.blockID )
		    	    		{
		        				iRopeRequiredToDescend = 0;
		    	    		}
			    		}
			    		
			    		if (tileEntityPulley.getContainedRopeCount() >= iRopeRequiredToDescend )
			    		{
			    			bEnoughRope = true;
			    		}
			    		else
			    		{
			    			bEnoughRope = false;
			    		}
					}
					
					int iTargetBlockID = worldObj.getBlockId( i, newBottomJ, k );
					
					boolean bStop = false;
					
					if ( tileEntityPulley == null ||
						 !tileEntityPulley.isLowering() ||
						 !bEnoughRope )
					{
						bStop = true;
					}
					else if ( !WorldUtils.isReplaceableBlock(worldObj, i, newBottomJ, k) )
					{
				    	if ( !Block.blocksList[iTargetBlockID].blockMaterial.isSolid() || iTargetBlockID == Block.web.blockID ||
			        		iTargetBlockID == BTWBlocks.web.blockID )
				    	{
				    		// we've collided with a non-solid block.  Destroy it.
				    		
				    		int iTargetMetadata = worldObj.getBlockMetadata( i, newBottomJ, k );
				    		
				    		if ( iTargetBlockID == BTWBlocks.ropeBlock.blockID )
				    		{
				    			if ( !returnRopeToPulley() )
				    			{
			    		    		Block.blocksList[iTargetBlockID].dropBlockAsItem( 
			    						worldObj, i, newBottomJ, k, iTargetMetadata, 0 );
				    			}
				    		}
				    		else
				    		{
			    		        worldObj.playAuxSFX( BTWEffectManager.DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID,
			    		        	i, newBottomJ, k, iTargetBlockID + ( iTargetMetadata << 12 ) );
					    		
					    		Block.blocksList[iTargetBlockID].dropBlockAsItem( 
									worldObj, i, newBottomJ, k, iTargetMetadata, 0 );			    		        
				    		}				    		
				    		
				    		worldObj.setBlockWithNotify( i, newBottomJ, k, 0 );
						}
				    	else
				    	{
				    		bStop = true;
				    	}
					}
		
					if ( bStop )
					{
						convertToBlock(i, oldBottomY, k);
						
						return;
					}
					
					if ( tileEntityPulley != null && worldObj.getBlockId( i, newBottomJ + 1, k ) != BTWBlocks.ropeBlock.blockID
							&& worldObj.getBlockId( i, newBottomJ + 1, k ) != BTWBlocks.pulley.blockID )
					{
						// make sure the pulley fills in the last block above us with rope
						
						tileEntityPulley.attemptToDispenseRope();
					}					
				}

				oldBottomY = newBottomJ;
	        }
		}
    }
    
	@Override
    public void moveEntity( double deltaX, double deltaY, double deltaZ )
    {
    	// this might be called by external sources (like the pistons), so we have to override it
    	// FCTODO: We may want to react to this kind of event
    	
    	notifyAssociatedPulleyOfLossOfAnchorEntity();
    	
    	destroyAnchorWithDrop();
    }
    
    @Override
    protected boolean shouldSetPositionOnLoad()
    {
    	return false;
    }
    
    //------------- FCIEntityPacketHandler ------------//

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
    
    //------------- Class Specific Methods ------------//
	
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
    
    public void destroyAnchorWithDrop()
    {
		int i = MathHelper.floor_double( posX );
    	int j = MathHelper.floor_double( posY );
		int k = MathHelper.floor_double( posZ );
		
		ItemStack anchorStack = new ItemStack( BTWBlocks.anchor);
		
		ItemUtils.ejectStackWithRandomOffset(worldObj, i, j, k, anchorStack);
		
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
    	
        int i1 = MathHelper.floor_double(getBoundingBox().minX + 0.001D);
        int k1 = MathHelper.floor_double(getBoundingBox().minY + 0.001D);
        int i2 = MathHelper.floor_double(getBoundingBox().minZ + 0.001D);
        int k3 = MathHelper.floor_double(getBoundingBox().maxX - 0.001D);
        int l3 = MathHelper.floor_double(getBoundingBox().maxY - 0.001D);
        int i4 = MathHelper.floor_double(getBoundingBox().maxZ - 0.001D);
        
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
    	AxisAlignedBB collisionBox = getBoundingBox();
    	
    	double testZoneMaxY = collisionBox.maxY + 0.075;
    	
    	double entityMinY = entity.boundingBox.minY;
    	
    	if ( entityMinY < testZoneMaxY )
    	{
    		if ( entityMinY > collisionBox.maxY - 0.25D )
    		{
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
		    		double entityYOffset = ( collisionBox.maxY + 0.01D ) - entityMinY;
		    		
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
        			
        			if ( collisionBox.minY < entityMaxY - 0.25D && testZoneMaxY > entityMaxY )
        			{
        				entity.attackEntityFrom( DamageSource.inWall, 1 );
        			}
    			}
    		}
    	}
    }
    
    public void forceStopByPlatform()
    {
    	if ( isDead )
    	{
    		return;
    	}
    	
    	if ( motionY > 0.0F )
    	{
    		// the pulley is ascending.  Break any rope attached above
    		
    		int i = MathHelper.floor_double( posX );
    		int jAbove = MathHelper.floor_double( posY ) + 1;
    		int k = MathHelper.floor_double( posZ );
    		
    		int iBlockAboveID  = worldObj.getBlockId( i, jAbove, k ); 
    		
			if ( iBlockAboveID == BTWBlocks.ropeBlock.blockID )
			{
				( (RopeBlock)(BTWBlocks.ropeBlock) ).breakRope(worldObj, i, jAbove, k);
			}   		
    	}
    	
		int i = MathHelper.floor_double( posX );
		int j = MathHelper.floor_double( posY );
		int k = MathHelper.floor_double( posZ );
		
		convertToBlock(i, j, k);
    }
    
    private void convertToBlock(int i, int j, int k)
    {
    	boolean bCanPlace = true;
    	
    	int iTargetBlockID = worldObj.getBlockId( i, j, k );
    	
    	if ( !WorldUtils.isReplaceableBlock(worldObj, i, j, k) )
    	{
    		if ( iTargetBlockID == BTWBlocks.ropeBlock.blockID )
    		{
    			// this shouldn't happen, but if there is a rope at the destination, 
    			// send it back to the pulley above
    			
    			if ( !returnRopeToPulley() )
    			{
	    			ItemUtils.ejectSingleItemWithRandomOffset(worldObj, i, j, k,
                                                              BTWItems.rope.itemID, 0);
    			}
    		}
        	else if ( !Block.blocksList[iTargetBlockID].blockMaterial.isSolid() || iTargetBlockID == Block.web.blockID ||
        		iTargetBlockID == BTWBlocks.web.blockID )
	    	{
	    		int iTargetMetadata = worldObj.getBlockMetadata( i, j, k );
	    		
	    		Block.blocksList[iTargetBlockID].dropBlockAsItem( 
					worldObj, i, j, k, iTargetMetadata, 0 );
	    		
		        worldObj.playAuxSFX( BTWEffectManager.DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID,
		        	i, j, k, iTargetBlockID + ( iTargetMetadata << 12 ) );
			}
	    	else
	    	{
	    		bCanPlace = false;
	    	}
    	}
    	
    	if ( bCanPlace )
    	{
    		worldObj.setBlockWithNotify( i, j, k, BTWBlocks.anchor.blockID );
    		
    		( (AnchorBlock)( BTWBlocks.anchor) ).setFacing(worldObj, i, j, k, 1);
		}
    	else
    	{
    		// this shouldn't usually happen, but if the block is already occupied, eject the anchor
    		// as an item
    		
			ItemUtils.ejectSingleItemWithRandomOffset(worldObj, i, j, k,
                                                      BTWBlocks.anchor.blockID, 0);
    	}
    	
    	notifyAssociatedPulleyOfLossOfAnchorEntity();
    	
    	setDead();
    }
    
    public boolean returnRopeToPulley()
    {
    	int associatedPulleyBlockID = worldObj.getBlockId(associatedPulleyPos.x,
														  associatedPulleyPos.y, associatedPulleyPos.z);
    	
    	if ( associatedPulleyBlockID == BTWBlocks.pulley.blockID )
    	{    		
    		// FCTODO: Check for the continuity of the rope here
    		
    		PulleyTileEntity tileEntityPulley =
    			(PulleyTileEntity)worldObj.getBlockTileEntity(associatedPulleyPos.x,
															  associatedPulleyPos.y, associatedPulleyPos.z);
    		
    		if ( tileEntityPulley != null )
    		{
    			tileEntityPulley.addRopeToInventory();
    			
    			return true;
    		}    		
    	}
    	
    	return false;
    }
    
    private void notifyAssociatedPulleyOfLossOfAnchorEntity()
    {
    	int associatedPulleyBlockID = worldObj.getBlockId(associatedPulleyPos.x,
														  associatedPulleyPos.y, associatedPulleyPos.z);
        	
    	if ( associatedPulleyBlockID == BTWBlocks.pulley.blockID )
    	{    		
    		PulleyTileEntity tileEntityPulley =
    			(PulleyTileEntity)worldObj.getBlockTileEntity(associatedPulleyPos.x,
															  associatedPulleyPos.y, associatedPulleyPos.z);
    		
    		tileEntityPulley.notifyOfLossOfAnchorEntity();
    	}    	
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
    	setPosition( par1, par3, par5 );
    }

    @Environment(EnvType.CLIENT)
    private void clientPushPlayer(Entity entity)
	{
    	double entityMinY = entity.boundingBox.minY;
    	AxisAlignedBB collisionBox = getBoundingBox();
    	
		double entityYOffset = ( collisionBox.maxY + 0.01D ) - entityMinY;
		
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