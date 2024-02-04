// FCMOD

package btw.entity;

import btw.network.packet.BTWPacketManager;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;


public class MiningChargeEntity extends Entity
	implements EntityWithCustomPacket
{
    public int fuse;
    public int facing;
    public boolean attachedToBlock;
    
    public MiningChargeEntity(World world )
    {
        super( world );

        fuse = 0;
        facing = 0;
        attachedToBlock = true;
        
        preventEntitySpawning = true;
        setSize(0.98F, 0.98F);
        yOffset = height / 2.0F;
    }

    public MiningChargeEntity(World world, int i, int j, int k, int iFacing )
    {
        this( world );

        fuse = 80;
        facing = iFacing;
        
        setPosition( (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F );        
        
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
    }

    public MiningChargeEntity(World world, double x, double y, double z, int iFacing, int iFuse, boolean bAttachedToBlock )
    {
        this( world );
        
        setPosition( x, y, z );

        facing = iFacing;
        fuse = iFuse;
        attachedToBlock = bAttachedToBlock;
        
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
    }
    
	@Override
    protected void entityInit()
    {
    }

	@Override
    public boolean canBePushed()
    {
        return false;
    }

	@Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

	@Override
    public boolean canBeCollidedWith()
    {
        return !isDead;
    }

	@Override
    public boolean attackEntityFrom(DamageSource damagesource, int i)
    {
    	if ( damagesource.isExplosion() )
    	{
    		if (fuse > 1 )
    		{
                fuse = 1;
    		}
    	}
    	
        setBeenAttacked();
        
        return false;
    }

	@Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        
        if (attachedToBlock)
        {
        	// make sure we're still attached
        	
        	boolean bStillAttached = false;
        	
        	BlockPos attachedBlockPos = new BlockPos( MathHelper.floor_double( posX ),
	            MathHelper.floor_double( posY ), MathHelper.floor_double( posZ ) );
        	
        	attachedBlockPos.addFacingAsOffset(facing);
        	
			if (worldObj.isBlockNormalCube(attachedBlockPos.x, attachedBlockPos.y, attachedBlockPos.z) ||
				(facing == 0 && worldObj.doesBlockHaveSolidTopSurface(attachedBlockPos.x, attachedBlockPos.y, attachedBlockPos.z) ) )
			{
				bStillAttached = true;
			}

            attachedToBlock = bStillAttached;
        }

        if ( !attachedToBlock)
        {
        	if (facing == 1 )
        	{
        		// flip upwards facing blocks downwards since that's the direction they'll be falling.

                facing = 0;
        	}
        	
	        motionY -= 0.039999999105930328D;
	        
	        moveEntity( motionX, motionY, motionZ );
	        
	        motionX *= 0.98000001907348633D;
	        motionY *= 0.98000001907348633D;
	        motionZ *= 0.98000001907348633D;
	        
	        if ( onGround )
	        {
	            motionX *= 0.69999998807907104D;
	            motionZ *= 0.69999998807907104D;
	            motionY *= -0.5D;
	        }
        }
        
        if (fuse-- <= 0 )
        {
            if ( !worldObj.isRemote )
            {
                setDead();
                explode();
            } 
            else
            {
                fuse = 0;
            }
        } 
        else
        {
            worldObj.spawnParticle( "smoke", posX, posY + 0.5D, posZ, 0.0D, 0.0D, 0.0D );
        }
    }

	@Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setByte( "m_iFuse", (byte) fuse);
        nbttagcompound.setByte( "m_iFacing", (byte) facing);
        nbttagcompound.setBoolean("m_bAttachedToBlock", attachedToBlock);
    }

	@Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        fuse = nbttagcompound.getByte("m_iFuse");
        facing = nbttagcompound.getByte("m_iFacing");
        attachedToBlock = nbttagcompound.getBoolean("m_bAttachedToBlock");
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
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream( byteStream );
        
        try
        {
	        dataStream.writeInt( BTWPacketManager.MINING_CHARGE_SPAWN_PACKET_ID);
	        dataStream.writeInt( entityId );
	        
	        dataStream.writeInt( (int)( posX * 32D ) );
	        dataStream.writeInt( (int)( posY * 32D ) );
	        dataStream.writeInt( (int)( posZ * 32D ) );
	        
	        dataStream.writeByte( (byte) facing);
	        dataStream.writeByte( (byte) fuse);
	        
	        dataStream.writeByte( (attachedToBlock ? 1 : 0 ));
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }        
	        
    	return new Packet250CustomPayload(BTWPacketManager.SPAWN_CUSTOM_ENTITY_PACKET_CHANNEL, byteStream.toByteArray() );
    }
    
    @Override
    public int getTrackerViewDistance()
    {
    	return 160;
    }
    
    @Override
    public int getTrackerUpdateFrequency()
    {
    	return 10;
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

    private void explode()
    {
        MiningChargeExplosion explosion = new MiningChargeExplosion(worldObj, posX, posY, posZ, facing);
        
        explosion.doExplosion();
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
    	// empty override to prevent network entity teleport packets from fucking up position and bounding box
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }
}