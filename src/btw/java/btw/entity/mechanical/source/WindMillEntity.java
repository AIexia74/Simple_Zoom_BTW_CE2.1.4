// FCMOD

package btw.entity.mechanical.source;

import btw.block.BTWBlocks;
import btw.block.blocks.AxleBlock;
import btw.block.util.MechPowerUtils;
import btw.network.packet.BTWPacketManager;
import btw.item.BTWItems;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class WindMillEntity extends MechanicalPowerSourceEntityHorizontal
{
	// constants
	
    static final public float HEIGHT = 12.8F;
    static final public float WIDTH = 12.8F;
    static final public float DEPTH = 0.8F;
    
    static final private int MAX_DAMAGE = 160;

    static final private float ROTATION_PER_TICK = -0.12F;
    static final private float ROTATION_PER_TICK_IN_STORM = -2.0F;
    static final private float ROTATION_PER_TICK_IN_HELL = -0.07F;
    
    static final private int TICKS_PER_FULL_UPDATE = 20;
    static final private int UPDATES_TO_OVERPOWER = 30;	// 30 seconds in a storm will overpower the mill
    
    // local vars 
    
	private static final int BLADE_COLOR_0_DATA_WATCHER_ID = 23;
	private static final int BLADE_COLOR_1_DATA_WATCHER_ID = 24;
	private static final int BLADE_COLOR_2_DATA_WATCHER_ID = 25;
	private static final int BLADE_COLOR_3_DATA_WATCHER_ID = 26;
    
    private int currentBladeColoringIndex;
    
    protected int overpowerTimer;
    
    private boolean legacyWindMill;
    
    public WindMillEntity(World world )
    {
        super( world );

		currentBladeColoringIndex = 0;

		legacyWindMill = false;
    }
    
    public WindMillEntity(World world, double x, double y, double z, boolean bIAligned  )
    {
    	super( world, x, y, z, bIAligned );
    }

    @Override
    protected void entityInit()
    {
    	super.entityInit();
    	
        dataWatcher.addObject(BLADE_COLOR_0_DATA_WATCHER_ID, new Byte((byte) 0 ));
        dataWatcher.addObject(BLADE_COLOR_1_DATA_WATCHER_ID, new Byte((byte) 0 ));
        dataWatcher.addObject(BLADE_COLOR_2_DATA_WATCHER_ID, new Byte((byte) 0 ));
        dataWatcher.addObject(BLADE_COLOR_3_DATA_WATCHER_ID, new Byte((byte) 0 ));
    }
    
    public int getBladeColor( int iBladeIndex )
    {
        return (int)( dataWatcher.getWatchableObjectByte(BLADE_COLOR_0_DATA_WATCHER_ID + iBladeIndex) );
    }
    
    public void setBladeColor( int iBladeIndex, int iColor )
    {
        dataWatcher.updateObject(BLADE_COLOR_0_DATA_WATCHER_ID + iBladeIndex, Byte.valueOf((byte)iColor));
    }
    
	@Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
    	nbttagcompound.setBoolean("bWindMillIAligned", alignedToX);
    	
    	nbttagcompound.setFloat("fRotation", rotation);
    	
    	nbttagcompound.setBoolean("bProvidingPower", providingPower);
    	
    	nbttagcompound.setInteger("iOverpowerTimer", overpowerTimer);
    	
    	nbttagcompound.setInteger( "iBladeColors0", getBladeColor( 0 ) ); 
    	nbttagcompound.setInteger( "iBladeColors1", getBladeColor( 1 ) ); 
    	nbttagcompound.setInteger( "iBladeColors2", getBladeColor( 2 ) ); 
    	nbttagcompound.setInteger( "iBladeColors3", getBladeColor( 3 ) );
    	
    	nbttagcompound.setBoolean("fcLegacy", legacyWindMill);
    }    	

	@Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        alignedToX = nbttagcompound.getBoolean("bWindMillIAligned");

        rotation = nbttagcompound.getFloat("fRotation");

		providingPower = nbttagcompound.getBoolean("bProvidingPower");

		overpowerTimer = nbttagcompound.getInteger("iOverpowerTimer");
    	
    	setBladeColor( 0, nbttagcompound.getInteger( "iBladeColors0" ) );
    	setBladeColor( 1, nbttagcompound.getInteger( "iBladeColors1" ) );
    	setBladeColor( 2, nbttagcompound.getInteger( "iBladeColors2" ) );
    	setBladeColor( 3, nbttagcompound.getInteger( "iBladeColors3" ) );
    	
        if ( nbttagcompound.hasKey( "fcLegacy" ) )
        {
			legacyWindMill = nbttagcompound.getBoolean("fcLegacy");
        }
        else
        {
			legacyWindMill = true;
        }
        
    	initBoundingBox();
    }
    
	@Override
    public boolean interact( EntityPlayer player )
    {
        ItemStack itemstack = player.inventory.getCurrentItem();
        
        if ( itemstack != null && ( itemstack.itemID == Item.dyePowder.itemID ||
    		itemstack.itemID == BTWItems.dung.itemID ) )
        {
        	if ( !worldObj.isRemote )
        	{
	        	int iColor = 0;
	        	
	        	if ( itemstack.itemID == Item.dyePowder.itemID )
	        	{
	        		iColor = BlockCloth.getBlockFromDye( itemstack.getItemDamage() );
	        	}
	        	else
	        	{
	        		iColor = 12; // brown smear for dung
	        	}
	            
	        	setBladeColor(currentBladeColoringIndex, iColor);
	        	
	        	currentBladeColoringIndex++;
	        	
	        	if (currentBladeColoringIndex >= 4 )
	        	{
					currentBladeColoringIndex = 0;
	        	}
        	}
        	
        	itemstack.stackSize--;
        	
            if ( itemstack.stackSize == 0 )
            {
                player.inventory.setInventorySlotContents( player.inventory.currentItem, null);
            }
            
            return true;
        }
    	
        return super.interact( player );
    }
    
	@Override
	public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt) {
		if (this.worldObj.getGameRules().getGameRuleBooleanValue("doFireTick") && this.worldObj.getDifficulty().shouldLightningStartFires()) {
			this.setDead();
		}
	}
	
    //************* FCEntityMechPower ************//
	
	@Override
    public float getWidth()
	{
		return WIDTH;
	}
    
	@Override
    public float getHeight()
	{
		return HEIGHT;
	}
	
	@Override
    public float getDepth()
	{
		return DEPTH;
	}
	
	@Override
    public int getMaxDamage()
	{
		return MAX_DAMAGE;
	}
	
	@Override
    public int getTicksPerFullUpdate()
	{
		return TICKS_PER_FULL_UPDATE;
	}
	
	@Override
    protected void onFullUpdateServer()
    {
		super.onFullUpdateServer();
		
        // update overpower
        
        if (overpowerTimer >= 0 )
        {
        	if (overpowerTimer > 0 )
        	{
        		overpowerTimer--;
        	}
        	
        	if (overpowerTimer <= 0 )
        	{
            	int iCenterI = MathHelper.floor_double( posX );
            	int iCenterJ = MathHelper.floor_double( posY );
            	int iCenterK = MathHelper.floor_double( posZ );
            	
        		// destroy any gearboxes attached to the windmill by overpowering the axle
	    		
	    		((AxleBlock)(BTWBlocks.axle)).overpower(
    				worldObj, iCenterI, iCenterJ, iCenterK);
        	}
        }
    }
    
	@Override
    public void destroyWithDrop()
    {
		if (!worldObj.isRemote && !isDead )
    	{
	    	dropItemWithOffset( BTWItems.windMill.itemID, 1, 0.0F );
	    	
			setDead();
    	}
    }
    
	@Override
    public boolean validateAreaAroundDevice()
    {
    	int iCenterI = MathHelper.floor_double( posX );
    	int iCenterJ = MathHelper.floor_double( posY );
    	int iCenterK = MathHelper.floor_double( posZ );
    	
    	return windMillValidateAreaAroundBlock(worldObj, iCenterI, iCenterJ, iCenterK, alignedToX);
    }
    
    @Override
	public float computeRotation()
    {
    	int iCenterI = MathHelper.floor_double( posX );
    	int iCenterJ = MathHelper.floor_double( posY );
    	int iCenterK = MathHelper.floor_double( posZ );
    	
    	float fRotationAmount = 0.0F;
 
    	if ( worldObj.provider.dimensionId == -1 )
    	{
    		if (legacyWindMill)
    		{
	    		// Old Wind Mills always rotates in hell, but new ones do not
	    		
	    		fRotationAmount = ROTATION_PER_TICK_IN_HELL;
    		}

			overpowerTimer = -1;
    	}
    	else if( worldObj.provider.dimensionId != 1 && worldObj.canBlockSeeTheSky( iCenterI, iCenterJ, iCenterK ) )
        {
	    	if ( worldObj.isThundering() && worldObj.isPrecipitatingAtPos(iCenterI, iCenterK) )
	    	{
	    		fRotationAmount = ROTATION_PER_TICK_IN_STORM;
	    		
	    		if (overpowerTimer < 0 )
	    		{
					overpowerTimer = UPDATES_TO_OVERPOWER;
	    		}
	    	}
	    	else
	    	{
	    		fRotationAmount = ROTATION_PER_TICK;

				overpowerTimer = -1;
	    	}
        }
    	else
    	{
			overpowerTimer = -1;
    	}
    	
		return fRotationAmount;
    }    
    
    @Override
	protected void onClientRotationOctantChange()
	{
		float fSpeed = getRotationSpeed();
		
		if (fSpeed < ROTATION_PER_TICK)
		{
	    	int iCenterI = MathHelper.floor_double( posX );
	    	int iCenterJ = MathHelper.floor_double( posY );
	    	int iCenterK = MathHelper.floor_double( posZ );
	    	
	    	int iCenterBlockID = worldObj.getBlockId( iCenterI, iCenterJ, iCenterK );
	    	
	    	if ( iCenterBlockID == BTWBlocks.axlePowerSource.blockID )
	    	{
	    		int iAxleAlignment = ((AxleBlock) BTWBlocks.axlePowerSource).getAxisAlignment(worldObj, iCenterI, iCenterJ, iCenterK);
	    		
	    		clientNotifyGearboxOfOverpoweredOctantChangeInDirection(iCenterI, iCenterJ, iCenterK, iAxleAlignment << 1);
	    		clientNotifyGearboxOfOverpoweredOctantChangeInDirection(iCenterI, iCenterJ, iCenterK, (iAxleAlignment << 1 ) + 1);
	    	}
			
		}
	}
    
    /*
    @Override
    public float getRotationSpeed()
    {	
    	float fSpeed = super.getRotationSpeed();
    	
    	if ( worldObj.isRemote )
    	{
	    	if ( worldObj.provider.dimensionId != -1 )
	    	{
	            if ( fSpeed < -0.01F )
	            {
		    		// gradually throttle up and down in storm
		    	
		    		fSpeed =  ( m_fRotationPerTick + (  ( m_fRotationPerTickInStorm - m_fRotationPerTick ) * worldObj.thunderingStrength ) );  
	            }
	    	}
    	}
    	
    	return fSpeed;
    }
    */
    
    //************* FCIEntityPacketHandler ************//

    @Override
    public Packet getSpawnPacketForThisEntity()
    {    	
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream( byteStream );
        
        try
        {
	        byte bIAligned = 0;
	        
	        if (alignedToX)
	        {
	        	bIAligned = 1;
	        }
	        
	        dataStream.writeInt( BTWPacketManager.WIND_MILL_SPAWN_PACKET_ID);
	        dataStream.writeInt( entityId );
	        
	        dataStream.writeInt( MathHelper.floor_double( posX * 32D ) );
	        dataStream.writeInt( MathHelper.floor_double( posY * 32D ) );
	        dataStream.writeInt( MathHelper.floor_double( posZ * 32D ) );
	        
	        dataStream.writeByte( bIAligned );
	        dataStream.writeInt( getRotationSpeedScaled() );
	        
	        dataStream.writeByte( (byte)getBladeColor( 0 ) );
	        dataStream.writeByte( (byte)getBladeColor( 1 ) );
	        dataStream.writeByte( (byte)getBladeColor( 2 ) );
	        dataStream.writeByte( (byte)getBladeColor( 3 ) );
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }        
	        
    	return new Packet250CustomPayload(BTWPacketManager.SPAWN_CUSTOM_ENTITY_PACKET_CHANNEL, byteStream.toByteArray() );
    }
    
    //************* Class Specific Methods ************//

    protected void clientNotifyGearboxOfOverpoweredOctantChangeInDirection(int iCenterI, int iCenterJ, int iCenterK, int iFacing)
    {
    	BlockPos tempPos = new BlockPos( iCenterI, iCenterJ, iCenterK );
    	
    	for ( int iTempCount = 0; iTempCount < 10; iTempCount++ ) // just to prevent running on indefinitely
    	{
    		tempPos.addFacingAsOffset(iFacing);
    		
    		int iTempBlockID = worldObj.getBlockId(tempPos.x, tempPos.y, tempPos.z);
    		
    		if ( iTempBlockID == BTWBlocks.axle.blockID ||
    			iTempBlockID == BTWBlocks.axlePowerSource.blockID )
    		{
    			if ( ((AxleBlock)Block.blocksList[iTempBlockID]).isAxleOrientedTowardsFacing(worldObj, tempPos.x, tempPos.y, tempPos.z, iFacing) )
    			{
        			continue;
    			}
    		}
    		else if ( MechPowerUtils.isPoweredGearBox(worldObj,
													  tempPos.x, tempPos.y, tempPos.z) )
    		{
				worldObj.playSound(tempPos.x + 0.5D, tempPos.y + 0.5D, tempPos.z + 0.5D,
								   "random.chestclosed", 1.5F, worldObj.rand.nextFloat() * 0.1F + 0.5F);
    		}
    		
    		break;
    	}
    }
	
    static public boolean windMillValidateAreaAroundBlock(World world, int i, int j, int k, boolean bIAligned)
    {
    	int iOffset;
    	int kOffset;
    	
    	if ( bIAligned )
    	{
    		iOffset = 0;    		
    		kOffset = 1;
    	}
    	else
    	{
       		iOffset = 1;    		
    		kOffset = 0;
    	}
    	
    	for ( int iHeightOffset = -6; iHeightOffset <= 6; iHeightOffset++ )
    	{
    		for ( int iWidthOffset = -6; iWidthOffset <= 6; iWidthOffset++ )
    		{
    			if ( iHeightOffset != 0 || iWidthOffset != 0 )
    			{
    				int tempI = i + ( iOffset * iWidthOffset );
    				int tempJ = j + iHeightOffset;
    				int tempK = k + ( kOffset * iWidthOffset );
    				
    				if ( !isValidBlockForWindMillToOccupy(world, tempI, tempJ, tempK) )
    				{
    					return false;
    				}
    			}
    		}
    	}
    	
    	return true;
    }
    
    static public boolean isValidBlockForWindMillToOccupy(World world, int i, int j, int k)
    {
    	if ( !world.isAirBlock( i, j, k ) )
    	{
			return false;
    	}
    	
    	return true;
    }    
}