// FCMOD

package btw.block.util;

import btw.block.BTWBlocks;
import btw.block.MechanicalBlock;
import btw.block.blocks.AxleBlock;
import btw.block.blocks.GearBoxBlock;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

public class MechPowerUtils
{
	public static boolean isBlockPoweredByAxleToSide(World world, int i, int j, int k, int iSide)
	{		
		BlockPos targetPos = new BlockPos( i, j, k );
		targetPos.addFacingAsOffset(iSide);
		
		int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
		
		if ( isBlockIDAxle(iTargetBlockID) )
		{
			AxleBlock axleBlock = (AxleBlock) Block.blocksList[iTargetBlockID];
			
    		if ( axleBlock.isAxleOrientedTowardsFacing(
					world, targetPos.x, targetPos.y, targetPos.z, iSide) )
    		{    				
    			if (axleBlock.getPowerLevel(world, targetPos.x, targetPos.y, targetPos.z) > 0 )
    			{
    				return true;
    			}
    		}
		}
		
		return false;
	}
	
	static public boolean isBlockIDAxle(int iBlockID)
	{
		return iBlockID == BTWBlocks.axle.blockID || iBlockID == BTWBlocks.axlePowerSource.blockID;
	}
	
	static public boolean doesBlockHaveFacingAxleToSide(IBlockAccess blockAccess, int i, int j, int k, int iSide)
	{		
		BlockPos targetPos = new BlockPos( i, j, k );
		targetPos.addFacingAsOffset(iSide);
		
		int iTargetBlockID = blockAccess.getBlockId(targetPos.x, targetPos.y, targetPos.z);
		
		if ( isBlockIDAxle(iTargetBlockID) )
		{
			AxleBlock axleBlock = (AxleBlock)Block.blocksList[iTargetBlockID];
			
    		if ( axleBlock.isAxleOrientedTowardsFacing(
					blockAccess, targetPos.x, targetPos.y, targetPos.z, iSide) )
    		{    				
				return true;
    		}
		}
		
		return false;
	}
	
	static public boolean doesBlockHaveAnyFacingAxles(IBlockAccess blockAccess, int i, int j, int k)
	{
		for ( int iFacing = 0; iFacing <= 5; iFacing++ )
		{
			if ( doesBlockHaveFacingAxleToSide(blockAccess, i, j, k, iFacing) )
			{
				return true;
			}			
		}
		
		return false;
	}
	
	static public boolean isBlockPoweredByHandCrank(World world, int i, int j, int k)
	{		
    	for ( int iFacing = 1; iFacing <= 5; iFacing++ )
    	{
    		if ( isBlockPoweredByHandCrankToSide(world, i, j, k, iFacing) )
    		{
    			return true;
    		}
    	}
    	
    	return false;    	
	}
	
	static public boolean isBlockPoweredByHandCrankToSide(World world, int i, int j, int k, int iSide)
	{
		BlockPos targetPos = new BlockPos( i, j, k );
		targetPos.addFacingAsOffset(iSide);
		
		int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
		
		if ( iTargetBlockID == BTWBlocks.handCrank.blockID )
		{
    		Block targetBlock = Block.blocksList[iTargetBlockID];
    		
			MechanicalBlock device = (MechanicalBlock)targetBlock;
			
			if ( device.isOutputtingMechanicalPower(world, targetPos.x, targetPos.y, targetPos.z) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	static public boolean isBlockPoweredByAxle(World world, int i, int j, int k, MechanicalBlock block)
	{		
    	for ( int iFacing = 0; iFacing <= 5; iFacing++ )
    	{
    		if ( block.canInputAxlePowerToFacing(world, i, j, k, iFacing) )
    		{
        		if ( MechPowerUtils.isBlockPoweredByAxleToSide(world, i, j, k, iFacing) )
        		{
        			return true;
        		}
    		}
    	}
    	
		return false;
	}
	
	static public void destroyHorizontallyAttachedAxles(World world, int i, int j, int k)
	{
		for ( int iFacing = 2; iFacing <= 5; iFacing ++ )
		{
			BlockPos tempPos = new BlockPos( i, j, k );
			
			tempPos.addFacingAsOffset(iFacing);
			
			int iTempBlockID = world.getBlockId(tempPos.x, tempPos.y, tempPos.z);
			
			if ( isBlockIDAxle(iTempBlockID) )
			{
				AxleBlock axleBlock = (AxleBlock)Block.blocksList[iTempBlockID];
				
	    		if ( axleBlock.isAxleOrientedTowardsFacing(world, tempPos.x, tempPos.y, tempPos.z, iFacing) )
	    		{
	    			axleBlock.breakAxle(world, tempPos.x, tempPos.y, tempPos.z);
	    		}
			}
		}
	}
	
	static public boolean isPoweredGearBox(IBlockAccess blockAccess, int i, int j, int k)
	{
		int iTempBlockID = blockAccess.getBlockId( i, j, k );
		
		if ( iTempBlockID == BTWBlocks.redstoneClutch.blockID ||
			iTempBlockID == BTWBlocks.gearBox.blockID )
		{
			GearBoxBlock gearBlock = (GearBoxBlock)Block.blocksList[iTempBlockID];
			
			return gearBlock.isGearBoxOn(blockAccess, i, j, k);
		}
		
		return false;
	}
}
