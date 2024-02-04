// FCMOD

package btw.block.tileentity;

import btw.block.blocks.ArcaneVesselBlock;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

public class ArcaneVesselTileEntity extends TileEntity
	implements TileEntityDataPacketHandler
{
	static final public int MAX_CONTAINED_EXPERIENCE = 1000;
	
	static final private int MIN_TEMPLE_EXPERIENCE = 200;
	static final private int MAX_TEMPLE_EXPERIENCE = 256;
	
	static final public int MAX_VISUAL_EXPERIENCE_LEVEL = 10;
	
	private final int xpEjectUnitSize = 20;
	
    private int visualExperienceLevel;
    
    private int containedRegularExperience;
    private int containedDragonExperience;
    
    
	public ArcaneVesselTileEntity()
	{
		visualExperienceLevel = 0;

		containedRegularExperience = 0;
		containedDragonExperience = 0;
	}
	
    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        
        nbttagcompound.setInteger("regXP", containedRegularExperience);
        nbttagcompound.setInteger("dragXP", containedDragonExperience);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);

		containedRegularExperience = nbttagcompound.getInteger("regXP");
		containedDragonExperience = nbttagcompound.getInteger("dragXP");
    	
    	int iTotalExperience = containedRegularExperience + containedDragonExperience;

		visualExperienceLevel = (int)((float) MAX_VISUAL_EXPERIENCE_LEVEL * ((float)iTotalExperience / (float) MAX_CONTAINED_EXPERIENCE) );
    	
    	if ( iTotalExperience > 0 && visualExperienceLevel == 0 )
    	{
			visualExperienceLevel = 1;
    	}
    }
    
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        
        nbttagcompound.setByte( "x", (byte) visualExperienceLevel);
        
        return new Packet132TileEntityData( xCoord, yCoord, zCoord, 1, nbttagcompound );
    }
    
    @Override
    public void updateEntity()
    {
    	if ( worldObj.isRemote )
    	{
    		return;
    	}
    	
		int iBlockID = worldObj.getBlockId( xCoord, yCoord, zCoord );
		
		Block block = Block.blocksList[iBlockID];
		
		if ( block == null || !( block instanceof ArcaneVesselBlock) )
		{
			// shouldn't happen
			
			return;
		}
		
		ArcaneVesselBlock vesselBlock = (ArcaneVesselBlock)block;
		
		if ( vesselBlock.getMechanicallyPoweredFlag(worldObj, xCoord, yCoord, zCoord) )
		{
    		int iTiltFacing = vesselBlock.getTiltFacing(worldObj, xCoord, yCoord, zCoord);
    		
			attemptToSpillXPFromInv(iTiltFacing);
		}
    }
    	
    //************* FCITileEntityDataPacketHandler ************//
    
    @Override
    public void readNBTFromPacket( NBTTagCompound nbttagcompound )
    {
		visualExperienceLevel = nbttagcompound.getByte("x");
        
        // force a visual update for the block since the above variables affect it
        
        worldObj.markBlockRangeForRenderUpdate( xCoord, yCoord, zCoord, xCoord, yCoord, zCoord );        
    }    
    
    //************* Class Specific Methods ************//
    
    public int getVisualExperienceLevel()
    {
    	return visualExperienceLevel;
    }
    
    public int getContainedRegularExperience()
    {
    	return containedRegularExperience;
    }
    
    public void setContainedRegularExperience(int iExperience)
    {
		containedRegularExperience = iExperience;
    	
    	validateVisualExperience();
    }
    
    public int getContainedDragonExperience()
    {
    	return containedDragonExperience;
    }
    
    public void setContainedDragonExperience(int iExperience)
    {
		containedDragonExperience = iExperience;
    	
    	validateVisualExperience();
    }
    
    public int getContainedTotalExperience()
    {
    	return containedDragonExperience + containedRegularExperience;
    }
    
    public void validateVisualExperience()
    {
    	int iTotalExperience = containedRegularExperience + containedDragonExperience;
    	
    	int iNewVisualExperience = (int)((float) MAX_VISUAL_EXPERIENCE_LEVEL * ((float)iTotalExperience / (float) MAX_CONTAINED_EXPERIENCE) );
    	
    	if ( iTotalExperience > 0 && iNewVisualExperience == 0 )
    	{
    		iNewVisualExperience = 1;
    	}
    	
    	// mark block to be sent to client
    	
    	if (iNewVisualExperience != visualExperienceLevel)
    	{
			visualExperienceLevel = iNewVisualExperience;
    		
    		worldObj.markBlockForUpdate( xCoord, yCoord, zCoord );
    	}
    }
    
    public void initTempleExperience()
    {
    	setContainedRegularExperience(worldObj.rand.nextInt(MAX_TEMPLE_EXPERIENCE - MIN_TEMPLE_EXPERIENCE) + MIN_TEMPLE_EXPERIENCE);
    }
    
    public boolean attemptToSwallowXPOrb(World world, int i, int j, int k, EntityXPOrb entityXPOrb)
    {
    	int iTotalContainedXP = containedRegularExperience + containedDragonExperience;
    	int iRemainingSpace = MAX_CONTAINED_EXPERIENCE - iTotalContainedXP;
    	
    	if ( iRemainingSpace > 0 )
    	{
        	int iXPToAddToInventory = 0;
        	boolean bIsDragonOrb = entityXPOrb.notPlayerOwned;
        	
    		if ( entityXPOrb.xpValue <= iRemainingSpace )
    		{
    			iXPToAddToInventory = entityXPOrb.xpValue;
    			
    			entityXPOrb.setDead();
    		}
    		else
    		{
    			iXPToAddToInventory = iRemainingSpace;    			
    		}
    		
    		if ( bIsDragonOrb )
    		{
    			setContainedDragonExperience(containedDragonExperience + iXPToAddToInventory);
    		}
    		else
    		{
    			setContainedRegularExperience(containedRegularExperience + iXPToAddToInventory);
    		}
			
			return true;    			
    	}    
        
    	return false;
    }
    
    private void attemptToSpillXPFromInv(int iTiltFacing)
    {
    	int iXPToSpill = 0;
    	boolean bSpillDragonOrb = false;
    	
    	if (containedDragonExperience > 0 || containedRegularExperience > 0 && !isTiltedOutputBlocked(iTiltFacing) )
    	{
    		if (containedDragonExperience > 0 )
    		{
    			bSpillDragonOrb = true;
    			
    			if (containedDragonExperience < xpEjectUnitSize)
    			{
    				iXPToSpill = containedDragonExperience;
    			}
    			else
    			{
    				iXPToSpill = xpEjectUnitSize;
    			}
    			
    			setContainedDragonExperience(containedDragonExperience - iXPToSpill);
    		}
    		else
    		{
    			if (containedRegularExperience < xpEjectUnitSize)
    			{
    				iXPToSpill = containedRegularExperience;
    			}
    			else
    			{
    				iXPToSpill = xpEjectUnitSize;
    			}
    			
    			setContainedRegularExperience(containedRegularExperience - iXPToSpill);
    		}
    	}
    	
    	if ( iXPToSpill > 0 )
    	{
    		spillXPOrb(iXPToSpill, bSpillDragonOrb, iTiltFacing);
    	}
    }
    
    private boolean isTiltedOutputBlocked(int iTiltFacing)
    {
		BlockPos targetPos = new BlockPos( xCoord, yCoord, zCoord );
		
		targetPos.addFacingAsOffset(iTiltFacing);
		
		if ( !worldObj.isAirBlock(targetPos.x, targetPos.y, targetPos.z) )
		{
			if ( !WorldUtils.isReplaceableBlock(worldObj, targetPos.x, targetPos.y, targetPos.z) )
			{				
				int iTargetBlockID = worldObj.getBlockId(targetPos.x, targetPos.y, targetPos.z);
				
				Block targetBlock = Block.blocksList[iTargetBlockID];
				
				if ( targetBlock.blockMaterial.isSolid() )
				{
					return true;
				}
			}				
		}
		
		return false;
    }
    
    public void ejectContentsOnBlockBreak()
    {
    	while (containedRegularExperience > 0 )
    	{
    		int iEjectSize = xpEjectUnitSize;
    		
    		if (containedRegularExperience < xpEjectUnitSize)
    		{
    			iEjectSize = containedRegularExperience;
    		}
    		
    		ejectXPOrbOnBlockBreak(iEjectSize, false);

			containedRegularExperience -= iEjectSize;
    	}
    	
    	while (containedDragonExperience > 0 )
    	{
    		int iEjectSize = xpEjectUnitSize;
    		
    		if (containedDragonExperience < xpEjectUnitSize)
    		{
    			iEjectSize = containedDragonExperience;
    		}
    		
    		ejectXPOrbOnBlockBreak(iEjectSize, true);

			containedDragonExperience -= iEjectSize;
    	}
    }
    
    private void spillXPOrb(int iXPValue, boolean bDragonOrb, int iFacing)
    {
		Vec3 itemPos = MiscUtils.convertBlockFacingToVector(iFacing);

		itemPos.xCoord *= 0.5F;
		itemPos.yCoord *= 0.5F;
		itemPos.zCoord *= 0.5F;
		
		itemPos.xCoord += ((float)xCoord ) + 0.5F;
		itemPos.yCoord += ((float)yCoord ) + 0.25F;
		itemPos.zCoord += ((float)zCoord ) + 0.5F + worldObj.rand.nextFloat() * 0.3F;
		
		if ( itemPos.xCoord > 0.1F || itemPos.xCoord < -0.1F )
		{
			itemPos.xCoord +=  ( worldObj.rand.nextFloat() * 0.5F ) - 0.25F;
		}
		else
		{
			itemPos.zCoord +=  ( worldObj.rand.nextFloat() * 0.5F ) - 0.25F;
		}
    	
        EntityXPOrb xpOrb = (EntityXPOrb) EntityList.createEntityOfType(EntityXPOrb.class, worldObj, 
			itemPos.xCoord, itemPos.yCoord, itemPos.zCoord, 
    		iXPValue, bDragonOrb );

		Vec3 itemVel = MiscUtils.convertBlockFacingToVector(iFacing);
		
		itemVel.xCoord *= 0.1F;
		itemVel.yCoord *= 0.1F;
		itemVel.zCoord *= 0.1F;
		
		xpOrb.motionX = itemVel.xCoord;
		xpOrb.motionY = itemVel.yCoord;
		xpOrb.motionZ = itemVel.zCoord;
        
        worldObj.spawnEntityInWorld( xpOrb );
    }
    
    private void ejectXPOrbOnBlockBreak(int iXPValue, boolean bDragonOrb)
    {
        double xOffset = worldObj.rand.nextDouble() * 0.7D + 0.15D;
        double yOffset = worldObj.rand.nextDouble() * 0.7D + 0.15D;
        double zOffset = worldObj.rand.nextDouble() * 0.7D + 0.15D;
    	
        EntityXPOrb xpOrb = (EntityXPOrb) EntityList.createEntityOfType(EntityXPOrb.class, worldObj, 
    			xCoord + xOffset, yCoord + yOffset, zCoord + zOffset, 
        		iXPValue, bDragonOrb );

        xpOrb.motionX = (float)worldObj.rand.nextGaussian() * 0.05F;
        xpOrb.motionY = (float)worldObj.rand.nextGaussian() * 0.05F + 0.2F;
        xpOrb.motionZ = (float)worldObj.rand.nextGaussian() * 0.05F;
        
        worldObj.spawnEntityInWorld( xpOrb );        
    }    
}