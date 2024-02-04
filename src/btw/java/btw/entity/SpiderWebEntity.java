// FCMOD

package btw.entity;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.network.packet.BTWPacketManager;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class SpiderWebEntity extends EntityThrowable
	implements EntityWithCustomPacket
{
    public SpiderWebEntity(World world )
    {
        super(world);
    }
    
    public SpiderWebEntity(World world, int iItemShiftedIndex )
    {
    	this( world );
    }

    public SpiderWebEntity(World world, EntityLiving throwingEntity )
    {
    	super( world, throwingEntity );
    }

    public SpiderWebEntity(World world, double d, double d1, double d2 )
    {
    	super( world, d, d1, d2 );
    }

    public SpiderWebEntity(World world, EntityLiving throwingEntity, Entity targetEntity )
    {
    	super( world );
    	
    	setThrower(throwingEntity);
    	
        setSize(0.25F, 0.25F);
        
        setLocationAndAngles(throwingEntity.posX, throwingEntity.posY + (double)throwingEntity.getEyeHeight(), throwingEntity.posZ, 
        	throwingEntity.rotationYaw, throwingEntity.rotationPitch);
        
        posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        posY -= 0.2D;
        posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        
        setPosition( posX, posY, posZ );

        yOffset = 0.0F;
        
        // try to aim for the target's feet first
        
        double targetY = targetEntity.posY;
        
        if ( worldObj.rayTraceBlocks_do_do( worldObj.getWorldVec3Pool().getVecFromPool( throwingEntity.posX, throwingEntity.posY + throwingEntity.getEyeHeight(), throwingEntity.posZ), 
        	worldObj.getWorldVec3Pool().getVecFromPool( targetEntity.posX, targetY, throwingEntity.posZ ), false, true ) != null )
        {
        	// try for the center of mass
        	
            targetY = targetEntity.posY + ( targetEntity.getEyeHeight() / 2F );
            
            if ( worldObj.rayTraceBlocks_do_do( worldObj.getWorldVec3Pool().getVecFromPool( throwingEntity.posX, throwingEntity.posY + throwingEntity.getEyeHeight(), throwingEntity.posZ), 
            	worldObj.getWorldVec3Pool().getVecFromPool( targetEntity.posX, targetY, throwingEntity.posZ ), false, true ) != null )
            {
            	// eye to eye contact has already been established by the attack code, so just use that
            	
            	targetY = targetEntity.posY + targetEntity.getEyeHeight();
            }
        }
        
        double deltaX = targetEntity.posX - posX;
        double deltaY = targetY - posY;
        double deltaZ = targetEntity.posZ - posZ;
        
        setThrowableHeading( deltaX, deltaY, deltaZ, 1.5F, 1.0F );
        
        motionY += 0.1F; // slight vertical offset to compensate for drop
    }

	@Override
	protected void onImpact(MovingObjectPosition impactPos) {
		Entity entityHit = impactPos.entityHit;
		
		if (entityHit != null) {
			entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0);
			
			if (!worldObj.isRemote) {
				int x = MathHelper.floor_double(entityHit.posX);
				int y = MathHelper.floor_double(entityHit.posY);
				int z = MathHelper.floor_double(entityHit.posZ);
				
				// attempt to place at feet of entity first
				if (!attemptToPlaceWebInBlock(x, y - 1, z) && !attemptToPlaceWebInBlock(x, y, z)) {
					spawnTangledWebItem(x, y, z);
				}
			}
		}
		else {
			if (!worldObj.isRemote) {
				BlockPos targetPos = new BlockPos(impactPos.blockX, impactPos.blockY, impactPos.blockZ, impactPos.sideHit);
				
				if (!attemptToPlaceWebInBlock(targetPos.x, targetPos.y, targetPos.z)) {
					spawnTangledWebItem(targetPos.x, targetPos.y, targetPos.z);
				}
			}
		}
		
		setDead();
	}
    
    //------------- FCIEntityPacketHandler ------------//

    @Override
    public int getTrackerViewDistance()
    {
    	return 64;
    }
    
    @Override
    public int getTrackerUpdateFrequency()
    {
    	return 10;
    }
    
    @Override
    public boolean getTrackMotion()
    {
    	return true;
    }
    
    @Override
    public boolean shouldServerTreatAsOversized()
    {
    	return false;
    }
    
    @Override
    public Packet getSpawnPacketForThisEntity()
    {    	
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream( byteStream );
        
        try
        {
	        dataStream.writeInt( BTWPacketManager.SPIDER_WEB_SPAWN_PACKET_ID);
	        dataStream.writeInt( entityId );
	        
	        dataStream.writeInt( MathHelper.floor_double( posX * 32D ) );
	        dataStream.writeInt( MathHelper.floor_double( posY * 32D ) );
	        dataStream.writeInt( MathHelper.floor_double( posZ * 32D ) );
	        
	        dataStream.writeByte( (byte)(int)( motionX * 128D ) );
	        dataStream.writeByte( (byte)(int)( motionY * 128D ) );
	        dataStream.writeByte( (byte)(int)( motionZ * 128D ) );	        		
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }        
	        
    	return new Packet250CustomPayload(BTWPacketManager.SPAWN_CUSTOM_ENTITY_PACKET_CHANNEL, byteStream.toByteArray() );
    }
    
    //------------- Class Specific Methods ------------//
	
	private boolean attemptToPlaceWebInBlock(int x, int y, int z) {
		if (canWebReplaceBlock(x, y, z)) {
			worldObj.setBlockWithNotify(x, y, z, BTWBlocks.web.blockID);
			return true;
		}
		
		return false;
	}
	
	private void spawnTangledWebItem(int x, int y, int z) {
		float f1 = 0.7F;
		
		double d = (this.worldObj.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		double d1 = (this.worldObj.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		double d2 = (this.worldObj.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		
		EntityItem entityitem = (EntityItem) EntityList.createEntityOfType(EntityItem.class, this.worldObj, x + d, y + d1, z + d2, new ItemStack(BTWItems.tangledWeb));
		
		entityitem.delayBeforeCanPickup = 10;
		
		this.worldObj.spawnEntityInWorld( entityitem );
	}
    
    private boolean canWebReplaceBlock(int i, int j, int k)
    {
    	int iBlockID = worldObj.getBlockId( i, j, k );
    	Block block = Block.blocksList[iBlockID];
    	
    	return block == null || block.canSpitWebReplaceBlock(worldObj, i, j, k);
    }

	//----------- Client Side Functionality -----------//
}