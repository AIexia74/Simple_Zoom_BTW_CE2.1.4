// FCMOD

package btw.entity.mechanical.source;

import btw.entity.EntityWithCustomPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public abstract class MechanicalPowerSourceEntity extends Entity
	implements EntityWithCustomPacket
{
	// constants

	private static final int ROTATION_SPEED_DATA_WATCHER_ID = 22;

	// local vars

    public float rotation;
    public int currentDamage;
    public int timeSinceHit;
    public int rockDirection;

    protected boolean providingPower;
    protected int fullUpdateTickCount;

    public MechanicalPowerSourceEntity(World world )
    {
        super( world );

        providingPower = false;

        currentDamage = 0;
        timeSinceHit = 0;
        rockDirection = 1;

        rotation = 0F;

        fullUpdateTickCount = 0;

        preventEntitySpawning = true;

        setSize(getWidth(), getHeight());

        yOffset = height / 2.0F;
    }

    public MechanicalPowerSourceEntity(World world, double x, double y, double z  )
    {
        this( world );

        setPosition( x, y, z );
    }

    @Override
    protected void entityInit()
    {
        dataWatcher.addObject(ROTATION_SPEED_DATA_WATCHER_ID, new Integer(0 ));
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
    public void moveEntity( double deltaX, double deltaY, double deltaZ )
    {
    	// this might be called by external sources (like the pistons), so we have to override it
    	// destroy the device if it is moved by an external source

    	if ( !isDead )
    	{
    		destroyWithDrop();
    	}
    }

	@Override
    public void setFire( int i )
    {
		// stub to prevent this entity from catching fire as it is so large that the fire effect would just look fucked
    }

	@Override
    public boolean attackEntityFrom(DamageSource damageSource, int i )
    {
        if ( isDead )
        {
            return true;
        }

        // Note: the server and client can have differing damage values as the server will destroy the ent when it takes too much, and the rest is just visual

        currentDamage += i * 5;
        rockDirection = -rockDirection;
        timeSinceHit = 10;

        if ( !worldObj.isRemote )
        {
	        Entity attackingEntity = damageSource.getEntity();

	        if ( (attackingEntity instanceof EntityPlayer) && ((EntityPlayer)attackingEntity).capabilities.isCreativeMode )
	        {
	        	// destroy on first hit in creative

	        	destroyWithDrop();
	        }
	        else
	        {
		        setBeenAttacked();

		        if (currentDamage > getMaxDamage() )
		        {
		        	destroyWithDrop();
		        }
	        }
        }

    	return true;
    }

	@Override
    public void onUpdate()
    {
		// intentionally doesn't call super method

    	if ( isDead )
    	{
    		return;
    	}

    	if ( !worldObj.isRemote )
    	{
    		fullUpdateTickCount--;

	        if (fullUpdateTickCount <= 0 )
	        {
                fullUpdateTickCount = getTicksPerFullUpdate();

	        	onFullUpdateServer();
	        }

	        updateRotationAndDamageState();
    	}
    	else
    	{
        	float m_fPrevRotation = rotation;

	        updateRotationAndDamageState();

	    	int iNewOctant = (int)(rotation / 45F );
	    	int iOldOctant = (int)( m_fPrevRotation / 45F );

	    	if ( iOldOctant != iNewOctant )
	    	{
	    		onClientRotationOctantChange();
	    	}
    	}
    }

    @Override
    protected boolean shouldSetPositionOnLoad()
    {
    	return false;
    }

    @Override
    public boolean attractsLightning()
    {
    	return true;
    }

    //************* FCIEntityPacketHandler ************//

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
    	return true;
    }

	//------------- Class Specific Methods ------------//

    public abstract float getWidth();

    public abstract float getHeight();

    public abstract float getDepth();

    public abstract void initBoundingBox();

    public abstract AxisAlignedBB getDeviceBounds();

    public abstract int getMaxDamage();

    public abstract int getTicksPerFullUpdate();

    public abstract void destroyWithDrop();

    public abstract boolean validateAreaAroundDevice();

    protected abstract boolean validateConnectedAxles();

    public abstract float computeRotation();

    public abstract void transferPowerStateToConnectedAxles();

	private void updateRotationAndDamageState()
	{
        // update rotation

        rotation += getRotationSpeed();

    	if (rotation > 360F )
    	{
            rotation -= 360;
    	}
    	else if (rotation < -360F )
    	{
            rotation += 360F;
    	}

    	// update damage state

        if (timeSinceHit > 0 )
        {
        	timeSinceHit--;
        }

        if (currentDamage > 0 )
        {
        	currentDamage--;
        }
	}

	protected void onClientRotationOctantChange()
	{
	}

    public boolean isClearOfBlockingEntities()
    {
    	AxisAlignedBB deviceBounds = getDeviceBounds();

    	return worldObj.checkNoEntityCollision( deviceBounds, this );
    }

    public float getRotationSpeed()
    {
        return (float)( dataWatcher.getWatchableObjectInt(ROTATION_SPEED_DATA_WATCHER_ID) ) / 100F;
    }

    public void setRotationSpeed( float fRotation )
    {
        dataWatcher.updateObject(ROTATION_SPEED_DATA_WATCHER_ID, Integer.valueOf((int)(fRotation * 100F )));
    }

    public int getRotationSpeedScaled()
    {
        return dataWatcher.getWatchableObjectInt(ROTATION_SPEED_DATA_WATCHER_ID);
    }

    public void setRotationSpeedScaled( int iRotationSpeedScaled )
    {
        dataWatcher.updateObject(ROTATION_SPEED_DATA_WATCHER_ID, Integer.valueOf(iRotationSpeedScaled));
    }

    protected void onFullUpdateServer()
    {
    	// validate the entity is still occupying a valid location

    	if (!validateAreaAroundDevice() || !validateConnectedAxles()  )
    	{
    		destroyWithDrop();

    		return;
    	}

        // update the rotation;

        setRotationSpeed(computeRotation());

        float fCurrentSpeed = getRotationSpeed();

        boolean bNewPoweredState = false;

        if ( fCurrentSpeed > 0.01F || fCurrentSpeed < -0.01F )
        {
        	bNewPoweredState = true;
        }

    	if (providingPower != bNewPoweredState )
    	{
            providingPower = bNewPoweredState;

    		transferPowerStateToConnectedAxles();
    	}
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

    @Override
    @Environment(EnvType.CLIENT)
    public void performHurtAnimation()
    {
        rockDirection = -rockDirection;
        timeSinceHit = 10;
        currentDamage += currentDamage * 5;
    }
}