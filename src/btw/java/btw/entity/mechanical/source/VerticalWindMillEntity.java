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

public class VerticalWindMillEntity extends MechanicalPowerSourceEntity
{
	// constants
	
    static final public float HEIGHT = 6.8F;
    static final public float WIDTH = 8.8F;
    
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
	private static final int BLADE_COLOR_4_DATA_WATCHER_ID = 27;
	private static final int BLADE_COLOR_5_DATA_WATCHER_ID = 28;
	private static final int BLADE_COLOR_6_DATA_WATCHER_ID = 29;
	private static final int BLADE_COLOR_7_DATA_WATCHER_ID = 30;
    
    private int currentBladeColoringIndex;
    
    protected int overpowerTimer;
    
    public VerticalWindMillEntity(World world )
    {
        super( world );

		currentBladeColoringIndex = 0;
    }
    
    public VerticalWindMillEntity(World world, double x, double y, double z  )
    {
    	super( world, x, y, z );
    }

    @Override
    protected void entityInit()
    {
    	super.entityInit();
    	
        dataWatcher.addObject(BLADE_COLOR_0_DATA_WATCHER_ID, new Byte((byte) 0 ));
        dataWatcher.addObject(BLADE_COLOR_1_DATA_WATCHER_ID, new Byte((byte) 0 ));
        dataWatcher.addObject(BLADE_COLOR_2_DATA_WATCHER_ID, new Byte((byte) 0 ));
        dataWatcher.addObject(BLADE_COLOR_3_DATA_WATCHER_ID, new Byte((byte) 0 ));
        dataWatcher.addObject(BLADE_COLOR_4_DATA_WATCHER_ID, new Byte((byte) 0 ));
        dataWatcher.addObject(BLADE_COLOR_5_DATA_WATCHER_ID, new Byte((byte) 0 ));
        dataWatcher.addObject(BLADE_COLOR_6_DATA_WATCHER_ID, new Byte((byte) 0 ));
        dataWatcher.addObject(BLADE_COLOR_7_DATA_WATCHER_ID, new Byte((byte) 0 ));
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
    	nbttagcompound.setFloat("fRotation", rotation);
    	
    	nbttagcompound.setBoolean("bProvidingPower", providingPower);
    	
    	nbttagcompound.setInteger("iOverpowerTimer", overpowerTimer);
    	
    	nbttagcompound.setInteger( "iBladeColors0", getBladeColor( 0 ) ); 
    	nbttagcompound.setInteger( "iBladeColors1", getBladeColor( 1 ) ); 
    	nbttagcompound.setInteger( "iBladeColors2", getBladeColor( 2 ) ); 
    	nbttagcompound.setInteger( "iBladeColors3", getBladeColor( 3 ) ); 
    	nbttagcompound.setInteger( "iBladeColors4", getBladeColor( 4 ) ); 
    	nbttagcompound.setInteger( "iBladeColors5", getBladeColor( 5 ) ); 
    	nbttagcompound.setInteger( "iBladeColors6", getBladeColor( 6 ) ); 
    	nbttagcompound.setInteger( "iBladeColors7", getBladeColor( 7 ) ); 
    }    	

	@Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
		rotation = nbttagcompound.getFloat("fRotation");

		providingPower = nbttagcompound.getBoolean("bProvidingPower");

		overpowerTimer = nbttagcompound.getInteger("iOverpowerTimer");
    	
    	setBladeColor( 0, nbttagcompound.getInteger( "iBladeColors0" ) );
    	setBladeColor( 1, nbttagcompound.getInteger( "iBladeColors1" ) );
    	setBladeColor( 2, nbttagcompound.getInteger( "iBladeColors2" ) );
    	setBladeColor( 3, nbttagcompound.getInteger( "iBladeColors3" ) );
    	setBladeColor( 4, nbttagcompound.getInteger( "iBladeColors4" ) );
    	setBladeColor( 5, nbttagcompound.getInteger( "iBladeColors5" ) );
    	setBladeColor( 6, nbttagcompound.getInteger( "iBladeColors6" ) );
    	setBladeColor( 7, nbttagcompound.getInteger( "iBladeColors7" ) );
    	
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
	        	
	        	if (currentBladeColoringIndex >= 8 )
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
    public void setDead()
    {
    	if (providingPower)
    	{   
    		boolean m_bAxlesPresent[] = new boolean[7];
    		
    		for ( int iTempIndex = 0; iTempIndex < 7; iTempIndex++ )
    		{
    			m_bAxlesPresent[iTempIndex] = false;
    		}
    		
        	int iCenterI = MathHelper.floor_double( posX );
        	int iCenterJ = MathHelper.floor_double( posY );
        	int iCenterK = MathHelper.floor_double( posZ );
        	
    		AxleBlock blockAxle = (AxleBlock) BTWBlocks.axlePowerSource;
    		
    		// depower the center axles without neighbor notifications
        	
        	for ( int iTempJ = iCenterJ - 2; iTempJ <= iCenterJ + 2; iTempJ++ )
        	{
            	int iTempBlockID = worldObj.getBlockId( iCenterI, iTempJ, iCenterK );

            	if ( iTempBlockID == BTWBlocks.axlePowerSource.blockID )
            	{
            		int iAxisAlignment = blockAxle.getAxisAlignment(worldObj, iCenterI, iTempJ, iCenterK);
            		
            		if ( iAxisAlignment == 0 )
            		{
            			int iAxleIndex = iTempJ - iCenterJ + 3;
            			
            			m_bAxlesPresent[iAxleIndex] = true;
            			
                        worldObj.setBlock( iCenterI, iTempJ, iCenterK, BTWBlocks.axle.blockID, 0, 2 );
            		}            		
            	}
        	}
        	
        	// depower outlaying axles with notification so that power state change is propegated
        	
        	for ( int iTempJ = iCenterJ - 3; iTempJ <= iCenterJ + 3; iTempJ += 6 )
        	{
            	int iTempBlockID = worldObj.getBlockId( iCenterI, iTempJ, iCenterK );

            	if ( iTempBlockID == BTWBlocks.axlePowerSource.blockID )
            	{
            		int iAxisAlignment = blockAxle.getAxisAlignment(worldObj, iCenterI, iTempJ, iCenterK);
            		
            		if ( iAxisAlignment == 0 )
            		{
                        worldObj.setBlock( iCenterI, iTempJ, iCenterK, BTWBlocks.axle.blockID, 0, 3 );
            		}            		
            	}
        	}        	
    	}
    	
        super.setDead();
    }
	
	@Override
	public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt) {
		if (this.worldObj.getGameRules().getGameRuleBooleanValue("doFireTick")) {
			this.setDead();
		}
	}
    
    //------------- FCEntityMechPower ------------//
	
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
		return WIDTH;
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
	    	dropItemWithOffset( BTWItems.verticalWindMill.itemID, 1, 0.0F );
	    	
			setDead();
    	}
    }
    
	@Override
    public boolean validateAreaAroundDevice()
    {
    	int iCenterI = MathHelper.floor_double( posX );
    	int iCenterJ = MathHelper.floor_double( posY );
    	int iCenterK = MathHelper.floor_double( posZ );
    	
    	return windMillValidateAreaAroundBlock(worldObj, iCenterI, iCenterJ, iCenterK);
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
    		// Wind Mill always rotates in hell
    		
    		fRotationAmount = ROTATION_PER_TICK_IN_HELL;

			overpowerTimer = -1;
    	}
    	else if( worldObj.provider.dimensionId != 1 && canSeeSky() )
        {
	    	if (worldObj.isThundering() && isBeingPrecipitatedOn() )
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
    protected boolean validateConnectedAxles()
    {
    	int iCenterI = MathHelper.floor_double( posX );
    	int iCenterJ = MathHelper.floor_double( posY );
    	int iCenterK = MathHelper.floor_double( posZ );

    	// check if we still have appropriately aligned axles in the center column
    	
    	for ( int iTempJ = iCenterJ - 3; iTempJ <= iCenterJ + 3; iTempJ++ )
    	{
	    	int iTempBlockID = worldObj.getBlockId( iCenterI, iTempJ, iCenterK );
	
	    	if ( MechPowerUtils.isBlockIDAxle(iTempBlockID) )
	    	{
	        	int iAxisAlignment = ((AxleBlock)(Block.blocksList[iTempBlockID])).getAxisAlignment(worldObj, iCenterI, iTempJ, iCenterK);
	        	
	        	if ( iAxisAlignment != 0  )
				{
		    		return false;
				}            		
	    	}
	    	else
	    	{
	    		return false;
	    	}
    	}
    	
    	if ( !providingPower)
    	{
	    	for ( int iTempJ = iCenterJ - 3; iTempJ <= iCenterJ + 3; iTempJ++ )
	    	{
		    	int iTempBlockID = worldObj.getBlockId( iCenterI, iTempJ, iCenterK );
		    	
	    		if (( (AxleBlock)Block.blocksList[iTempBlockID]).getPowerLevel(worldObj, iCenterI, iTempJ, iCenterK) > 0 )
	    		{
	    			// we have an unpowered device on a powered axle
	    			
	        		return false;
	    		}
	    	}
    	}
    	else
    	{    	
	    	for ( int iTempJ = iCenterJ - 3; iTempJ <= iCenterJ + 3; iTempJ++ )
	    	{
		    	int iTempBlockID = worldObj.getBlockId( iCenterI, iTempJ, iCenterK );
		    	
	    		if ( iTempBlockID != BTWBlocks.axlePowerSource.blockID )
	    		{
	    			// we have powered device on a unpowered axle.  Restore power (this is likely the result of a player-rotated axle or Gear Box).
	    			
	    			powerAxleColumn();
	    			
	    			break;	    			
	    		}
	    	}
    	}
    	
    	return true;    	
    }
    
    @Override
	public void transferPowerStateToConnectedAxles()
    {
    	if (providingPower)
    	{
    		powerAxleColumn();
    	}
    	else
    	{
    		depowerAxleColumn();
    	}    	
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
    
    //------------- FCIEntityPacketHandler ------------//

    @Override
    public Packet getSpawnPacketForThisEntity()
    {    	
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream( byteStream );
        
        try
        {
	        dataStream.writeInt( BTWPacketManager.VERTICAL_WIND_MILL_SPAWN_PACKET_ID);
	        dataStream.writeInt( entityId );
	        
	        dataStream.writeInt( MathHelper.floor_double( posX * 32D ) );
	        dataStream.writeInt( MathHelper.floor_double( posY * 32D ) );
	        dataStream.writeInt( MathHelper.floor_double( posZ * 32D ) );
	        
	        dataStream.writeInt( getRotationSpeedScaled() );
	        
	        dataStream.writeByte( (byte)getBladeColor( 0 ) );
	        dataStream.writeByte( (byte)getBladeColor( 1 ) );
	        dataStream.writeByte( (byte)getBladeColor( 2 ) );
	        dataStream.writeByte( (byte)getBladeColor( 3 ) );
	        dataStream.writeByte( (byte)getBladeColor( 4 ) );
	        dataStream.writeByte( (byte)getBladeColor( 5 ) );
	        dataStream.writeByte( (byte)getBladeColor( 6 ) );
	        dataStream.writeByte( (byte)getBladeColor( 7 ) );
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }        
	        
    	return new Packet250CustomPayload(BTWPacketManager.SPAWN_CUSTOM_ENTITY_PACKET_CHANNEL, byteStream.toByteArray() );
    }
    
    //------------- Class Specific Methods ------------//
    
    private void powerAxleColumn()
    {
    	int iCenterI = MathHelper.floor_double( posX );
    	int iCenterJ = MathHelper.floor_double( posY );
    	int iCenterK = MathHelper.floor_double( posZ );
    	
    	// set the power level without notifying other axles on central blocks
    	
    	for ( int iTempJ = iCenterJ - 2; iTempJ <= iCenterJ + 2; iTempJ++ )
    	{
            worldObj.setBlock( iCenterI, iTempJ, iCenterK, BTWBlocks.axlePowerSource.blockID, 0, 2 );
    	}
    	
    	// set power level on top and bottom blocks with notify to transfer power to other connected axles
    	
        worldObj.setBlock( iCenterI, iCenterJ + 3, iCenterK, BTWBlocks.axlePowerSource.blockID, 0, 3 );
        worldObj.setBlock( iCenterI, iCenterJ + -3, iCenterK, BTWBlocks.axlePowerSource.blockID, 0, 3 );
    }
    
    private void depowerAxleColumn()
    {
    	int iCenterI = MathHelper.floor_double( posX );
    	int iCenterJ = MathHelper.floor_double( posY );
    	int iCenterK = MathHelper.floor_double( posZ );
    	
    	// set the power level without notifying other axles on central blocks
    	
    	for ( int iTempJ = iCenterJ - 2; iTempJ <= iCenterJ + 2; iTempJ++ )
    	{
            worldObj.setBlock( iCenterI, iTempJ, iCenterK, BTWBlocks.axle.blockID, 0, 2 );
    	}
    	
    	// set power level on top and bottom blocks with notify to transfer power to other connected axles
    	
        worldObj.setBlock( iCenterI, iCenterJ + 3, iCenterK, BTWBlocks.axle.blockID, 0, 3 );
        worldObj.setBlock( iCenterI, iCenterJ + -3, iCenterK, BTWBlocks.axle.blockID, 0, 3 );
    }
    
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

	public static boolean windMillValidateAreaAroundBlock(World world, int i, int j, int k)
    {
    	for ( int iTempI = i - 4; iTempI <= i + 4; iTempI++ )
    	{
        	for ( int iTempJ = j - 3; iTempJ <= j + 3; iTempJ++ )
        	{
            	for ( int iTempK = k - 4; iTempK <= k + 4; iTempK++ )
            	{
            		if ( iTempI != i || iTempK != k )
            		{
	    				if ( !isValidBlockForWindMillToOccupy(world, iTempI, iTempJ, iTempK) )
	    				{
	    					return false;
	    				}
            		}
            	}
        	}
    	}
    	
    	return true;
    }

	public static boolean isValidBlockForWindMillToOccupy(World world, int i, int j, int k)
    {
    	if ( !world.isAirBlock( i, j, k ) )
    	{
			return false;
    	}
    	
    	return true;
    }

	public void initBoundingBox()
    {
    	boundingBox.setBounds( posX - (getWidth() * 0.5F ), posY - (getHeight() * 0.5F ), posZ - (getWidth() * 0.5F ),
			posX + (getWidth() * 0.5F ), posY + (getHeight() * 0.5F ), posZ + (getWidth() * 0.5F ));
    }
    
    private boolean canSeeSky()
    {
    	int iCenterI = MathHelper.floor_double( posX );
    	int iCenterJ = MathHelper.floor_double( posY );
    	int iCenterK = MathHelper.floor_double( posZ );

    	for ( int iTempI = iCenterI - 4; iTempI <= iCenterI + 4; iTempI++ )
    	{
        	for ( int iTempK = iCenterK - 4; iTempK <= iCenterK + 4; iTempK++ )
        	{
        		if ( worldObj.canBlockSeeTheSky( iTempI, iCenterJ, iTempK ) )
        		{
        			return true;
        		}
        	}
    	}
		
		return false;
    }
    
    private boolean isBeingPrecipitatedOn()
    {
    	if ( worldObj.isRaining() )
    	{
        	int iCenterI = MathHelper.floor_double( posX );
        	int iCenterJ = MathHelper.floor_double( posY );
        	int iCenterK = MathHelper.floor_double( posZ );

        	for ( int iTempI = iCenterI - 4; iTempI <= iCenterI + 4; iTempI++ )
        	{
            	for ( int iTempK = iCenterK - 4; iTempK <= iCenterK + 4; iTempK++ )
            	{
            		if ( worldObj.isPrecipitatingAtPos(iTempI, iTempK) )
            		{
            			return true;
            		}
            	}
        	}
    	}
    	
    	return false;
    }

	public AxisAlignedBB getDeviceBounds()
    {
    	return AxisAlignedBB.getAABBPool().getAABB( 
    		posX - (getWidth() * 0.5F ), posY - (getHeight() * 0.5F ), posZ - (getWidth() * 0.5F ),
			posX + (getWidth() * 0.5F ), posY + (getHeight() * 0.5F ), posZ + (getWidth() * 0.5F ));
    }
}