// FCMOD

package btw.entity.mechanical.source;

import btw.network.packet.BTWPacketManager;
import btw.item.BTWItems;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class WaterWheelEntity extends MechanicalPowerSourceEntityHorizontal
{
	// constants
	
    static final public float HEIGHT = 4.8F;
    static final public float WIDTH = 4.8F;
    static final public float DEPTH = 0.8F;
    
    static final public int MAX_DAMAGE = 160;

    static final public float ROTATION_PER_TICK = 0.25F;
    
    static final public int TICKS_PER_FULL_UPDATE = 20;
    
    public WaterWheelEntity(World world )
    {
        super(world);        
    }
    
    public WaterWheelEntity(World world, double x, double y, double z, boolean bIAligned  )
    {
    	super( world, x, y, z, bIAligned );
        
    }
    
	@Override
    protected void entityInit()
    {
    	super.entityInit();
    	
    }
    
	@Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
    	nbttagcompound.setBoolean("bWaterWheelIAligned", alignedToX);
    	nbttagcompound.setFloat("fRotation", rotation);
    	nbttagcompound.setBoolean("bProvidingPower", providingPower);
    }
    	

	@Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        alignedToX = nbttagcompound.getBoolean("bWaterWheelIAligned");
        rotation = nbttagcompound.getFloat("fRotation");
        providingPower = nbttagcompound.getBoolean("bProvidingPower");
    	
        initBoundingBox();
    }
    
    //------------- FCIEntityPacketHandler ------------//

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
	        
	        dataStream.writeInt( BTWPacketManager.WATER_WHEEL_SPAWN_PACKET_ID);
	        dataStream.writeInt( entityId );
	        
	        dataStream.writeInt( MathHelper.floor_double( posX * 32D ) );
	        dataStream.writeInt( MathHelper.floor_double( posY * 32D ) );
	        dataStream.writeInt( MathHelper.floor_double( posZ * 32D ) );
	        
	        dataStream.writeByte( bIAligned );	        
	        dataStream.writeInt( getRotationSpeedScaled() );
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }        
	        
    	return new Packet250CustomPayload(BTWPacketManager.SPAWN_CUSTOM_ENTITY_PACKET_CHANNEL, byteStream.toByteArray() );
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
    public void destroyWithDrop()
    {
    	if (!worldObj.isRemote && !isDead )
    	{
	    	dropItemWithOffset( BTWItems.waterWheel.itemID, 1, 0.0F );
	    	
			setDead();
    	}
    }
        
	@Override
    public boolean validateAreaAroundDevice()
    {
    	int iCenterI = MathHelper.floor_double( posX );
    	int iCenterJ = MathHelper.floor_double( posY );
    	int iCenterK = MathHelper.floor_double( posZ );
    	
    	return waterWheelValidateAreaAroundBlock(worldObj, iCenterI, iCenterJ, iCenterK, alignedToX);
    }
    
	@Override
    public float computeRotation()
    {
    	int iCenterI = MathHelper.floor_double( posX );
    	int iCenterJ = MathHelper.floor_double( posY );
    	int iCenterK = MathHelper.floor_double( posZ );
    	
    	float fRotationAmount = 0.0F;
    	
    	int iFlowJ = iCenterJ - 2;
    	
    	// check for rotation below
    	
    	int iFlowBlockID = worldObj.getBlockId( iCenterI, iFlowJ, iCenterK );
    	
    	if ( iFlowBlockID == Block.waterMoving.blockID || iFlowBlockID == Block.waterStill.blockID )
    	{
    		Vec3 flowVector = getFlowVector( worldObj, iCenterI, iFlowJ, iCenterK );
    		
    		if (alignedToX)
    		{
    			if ( flowVector.zCoord > 0.33F )
    			{
    				fRotationAmount = -ROTATION_PER_TICK;
    			}
    			else if ( flowVector.zCoord < -0.33F )
    			{
    				fRotationAmount = ROTATION_PER_TICK;
    			}
    		}
    		else
    		{
    			if ( flowVector.xCoord > 0.33F )
    			{
    				fRotationAmount = ROTATION_PER_TICK;
    			}
    			else if ( flowVector.xCoord < -0.33F )
    			{
    				fRotationAmount = -ROTATION_PER_TICK;
    			}
    		}
    	}
    	
    	// check for rotation on the sides
    	
    	int iOffset;
    	int kOffset;
    	
    	if (alignedToX)
    	{
    		iOffset = 0;    		
    		kOffset = 2;
    	}
    	else
    	{    		
       		iOffset = 2;    		
    		kOffset = 0;
    	}
    	
    	iFlowBlockID = worldObj.getBlockId( iCenterI + iOffset, iCenterJ, iCenterK - kOffset );
    	
    	if ( iFlowBlockID == Block.waterMoving.blockID || iFlowBlockID == Block.waterStill.blockID )
    	{
    		BlockFluid fluidBlock = (BlockFluid)(Block.blocksList[iFlowBlockID]);
    		
			fRotationAmount -= ROTATION_PER_TICK;
    	}
    	
    	iFlowBlockID = worldObj.getBlockId( iCenterI - iOffset, iCenterJ, iCenterK + kOffset );
    	
    	if ( iFlowBlockID == Block.waterMoving.blockID || iFlowBlockID == Block.waterStill.blockID )
    	{
    		BlockFluid fluidBlock = (BlockFluid)(Block.blocksList[iFlowBlockID]);
    		
			fRotationAmount += ROTATION_PER_TICK;
    	}
    	
    	if (fRotationAmount > ROTATION_PER_TICK)
    	{
    		fRotationAmount = ROTATION_PER_TICK;
    	}
    	else if ( fRotationAmount <= -ROTATION_PER_TICK)
    	{
    		fRotationAmount = -ROTATION_PER_TICK;
    	}
    		
    	return fRotationAmount;
    }
    
	//------------- Class Specific Methods ------------//

    static public boolean waterWheelValidateAreaAroundBlock(World world, int i, int j, int k, boolean bIAligned)
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
    	
    	for ( int iHeightOffset = -2; iHeightOffset <= 2; iHeightOffset++ )
    	{
    		for ( int iWidthOffset = -2; iWidthOffset <= 2; iWidthOffset++ )
    		{
    			if ( iHeightOffset != 0 || iWidthOffset != 0 )
    			{
    				int tempI = i + ( iOffset * iWidthOffset );
    				int tempJ = j + iHeightOffset;
    				int tempK = k + ( kOffset * iWidthOffset );
    				
    				if ( !isValidBlockForWaterWheelToOccupy(world, tempI, tempJ, tempK) )
    				{
    					return false;
    				}
    			}
    		}
    	}
    	
    	return true;
    }
    
    static public boolean isValidBlockForWaterWheelToOccupy(World world, int i, int j, int k)
    {
    	if ( !world.isAirBlock( i, j, k ) )
    	{
    		int iBlockID = world.getBlockId( i, j, k );
    		
    		if ( iBlockID != Block.waterMoving.blockID && iBlockID != Block.waterStill.blockID )
    		{
    			return false;
    		}
    	}
    	
    	return true;
    }    
    
    // ripped from BlockFluid so I don't have to modify base-class
    private Vec3 getFlowVector( IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        Vec3 vec3 = par1IBlockAccess.getWorldVec3Pool().getVecFromPool(0.0D, 0.0D, 0.0D);
        
        int i = getEffectiveFlowDecay(par1IBlockAccess, par2, par3, par4);

        for (int j = 0; j < 4; j++)
        {
            int k = par2;
            int l = par3;
            int i1 = par4;

            if (j == 0)
            {
                k--;
            }

            if (j == 1)
            {
                i1--;
            }

            if (j == 2)
            {
                k++;
            }

            if (j == 3)
            {
                i1++;
            }

            int j1 = getEffectiveFlowDecay(par1IBlockAccess, k, l, i1);

            if (j1 < 0)
            {
                if (par1IBlockAccess.getBlockMaterial(k, l, i1).blocksMovement())
                {
                    continue;
                }

                j1 = getEffectiveFlowDecay(par1IBlockAccess, k, l - 1, i1);

                if (j1 >= 0)
                {
                    int k1 = j1 - (i - 8);
                    vec3 = vec3.addVector((k - par2) * k1, (l - par3) * k1, (i1 - par4) * k1);
                }

                continue;
            }

            if (j1 >= 0)
            {
                int l1 = j1 - i;
                vec3 = vec3.addVector((k - par2) * l1, (l - par3) * l1, (i1 - par4) * l1);
            }
        }

        if (par1IBlockAccess.getBlockMetadata(par2, par3, par4) >= 8)
        {
            boolean flag = false;

            if (flag || isBlockSolid(par1IBlockAccess, par2, par3, par4 - 1, 2))
            {
                flag = true;
            }

            if (flag || isBlockSolid(par1IBlockAccess, par2, par3, par4 + 1, 3))
            {
                flag = true;
            }

            if (flag || isBlockSolid(par1IBlockAccess, par2 - 1, par3, par4, 4))
            {
                flag = true;
            }

            if (flag || isBlockSolid(par1IBlockAccess, par2 + 1, par3, par4, 5))
            {
                flag = true;
            }

            if (flag || isBlockSolid(par1IBlockAccess, par2, par3 + 1, par4 - 1, 2))
            {
                flag = true;
            }

            if (flag || isBlockSolid(par1IBlockAccess, par2, par3 + 1, par4 + 1, 3))
            {
                flag = true;
            }

            if (flag || isBlockSolid(par1IBlockAccess, par2 - 1, par3 + 1, par4, 4))
            {
                flag = true;
            }

            if (flag || isBlockSolid(par1IBlockAccess, par2 + 1, par3 + 1, par4, 5))
            {
                flag = true;
            }

            if (flag)
            {
                vec3 = vec3.normalize().addVector(0.0D, -6D, 0.0D);
            }
        }

        vec3 = vec3.normalize();
        return vec3;
    }
    
    // ripped from BlockFluid so I don't have to modify base-class
    private boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        Material material = par1IBlockAccess.getBlockMaterial(par2, par3, par4);

        if (material == Block.waterMoving.blockMaterial)
        {
            return false;
        }

        if (par5 == 1)
        {
            return true;
        }

        if (material == Material.ice)
        {
            return false;
        }
        else
        {
        	// changed this line to replace the super call in the original
            return par1IBlockAccess.getBlockMaterial(par2, par3, par4).isSolid();
        }
    }
    
    // ripped from BlockFluid so I don't have to modify base-class
    private int getEffectiveFlowDecay( IBlockAccess iblockaccess, int i, int j, int k)
    {
        if(iblockaccess.getBlockMaterial(i, j, k) != Block.waterMoving.blockMaterial)
        {
            return -1;
        }
        int l = iblockaccess.getBlockMetadata(i, j, k);
        if(l >= 8)
        {
            l = 0;
        }
        return l;
    }    
}