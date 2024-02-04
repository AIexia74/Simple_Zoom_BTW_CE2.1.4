// FCMOD

package btw.block.blocks;

import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class DetectorLogicBlock extends Block
{
    private final static int DETECTOR_LOGIC_TICK_RATE = 5;
    
    public final static boolean LOGIC_DEBUG_DISPLAY = false;
    
    public DetectorLogicBlock(int iBlockID )
    {
        super( iBlockID, Material.air );
        
        setTickRandomly( true );        
    }
    
	@Override
    public int tickRate( World world )
    {
        return DETECTOR_LOGIC_TICK_RATE;
    }    

	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
	@Override
    public int getMobilityFlag()
    {
    	// disable the ability for piston to push this block
    	
        return 1;
    }
    
	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        super.onBlockAdded( world, i, j, k );
    }

	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
		if ( !BTWMod.isLensBeamBeingRemoved)
		{
			// this basically occurs if a logic block is removed by an external source, in which case it needs to
			// cut off any orphaned beams surrounding it
			
			if (!isDetectorLogicFlagOn(world, i, j, k) || isIntersectionPointFlagOn(world, i, j, k) )
			{
				for ( int iTempFacing = 0; iTempFacing <= 5; iTempFacing++ )
				{
					int iRangeToSource = getRangeToValidLensSourceToFacing(world, i, j, k, iTempFacing);
					
					if ( iRangeToSource > 0 )
					{
						// we have a valid source, kill the beam in the opposite direction, since it is now blocked
						
						int iBeamRangeRemaining = LensBlock.LENS_MAX_RANGE - iRangeToSource;
						
						if ( iBeamRangeRemaining > 0 )
						{
							removeLensBeamFromBlock(world, i, j, k, Block.getOppositeFacing(iTempFacing), iBeamRangeRemaining);
						}
					}
				}
			}
		}
    }
	
	@Override
    public int idDropped( int i, Random random, int iFortuneModifier )
    {
        return 0;
    }
    
	@Override
    public boolean canCollideCheck(int i, boolean flag)
    {
        return false;
    }    
    
	@Override
    public int quantityDropped( Random random )
    {
        return 0;
    }

	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
    	// this turns off entities not being able to pass through the block
    	
        return null;
    }
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
		if ( isDetectorLogicFlagOn(world, i, j, k) )
		{
	    	if ( !checkForNeighboringDetector(world, i, j, k) )
			{
	    		// there is no longer an associated detector
	    		
	    		if ( isIntersectionPointFlagOn(world, i, j, k) )
	    		{
	    			setIsDetectorLogicFlag(world, i, j, k, false);
	    			
	    			if ( !hasMultipleValidLensSources(world, i, j, k) )
	    			{
	    				setIsIntersectionPointFlag(world, i, j, k, false);
	    			}
	    		}
	    		else
	    		{
	    			// no lens sources either...self destruct
	    			
	    			removeSelf(world, i, j, k);
	    		}	
			}
	    	else
	    	{
	    		// notify any neigboring detector blocks so that they can check for wheat growth
	    		
	    		notifyNeighboringDetectorBlocksOfChange(world, i, j, k);
    		}
		}
		
		// check if a beam is passing through this block
		
		if (!isDetectorLogicFlagOn(world, i, j, k) || isIntersectionPointFlagOn(world, i, j, k) )
		{
			// a neigboring air block just appeared next to a beam.  
			// Schedule an immediate update to check for beam propagation
			
			if ( !world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
			{
				world.scheduleBlockUpdate( i, j, k, world.getBlockId( i, j, k ), 
					tickRate( world ) );
			}
		}
		
		if (isLitFlagOn(world, i, j, k) && isBlockGlowing(world, i, j, k) )
		{
			// force re-render of glowing blocks in case lit facings have changed
			
        	world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
		}
    }
	
	@Override
    public void onEntityCollidedWithBlock( World world, int i, int j, int k, Entity entity)
    {
		if ( !world.isRemote )
		{
	    	if ( isEntityWithinBounds(world, i, j, k) )
	    	{
		    	boolean bIsOn = isEntityCollidingFlagOn(world, i, j, k);
		    	
		    	if ( !bIsOn )
		    	{
		    		changeStateToRegisterEntityCollision(world, i, j, k);
		    		
		    		// schedule an update to check if the entity leaves.  Have to manually get the block ID as it may have changed above.
		    		
		        	world.scheduleBlockUpdate( i, j, k, world.getBlockId( i, j, k ), tickRate( world ) );
		    	}
	    	}
		}
    }
	
	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
    	boolean bIsOn = isEntityCollidingFlagOn(world, i, j, k);
    	
        boolean bShouldBeOn = isEntityWithinBounds(world, i, j, k);
        
        if ( bShouldBeOn )
        {
        	if ( !bIsOn )
        	{
	    		changeStateToRegisterEntityCollision(world, i, j, k);
        	}

    		// schedule another update to check if the entity leaves.  Have to manually get the block ID as it may have changed above.
    		
        	world.scheduleBlockUpdate( i, j, k, world.getBlockId( i, j, k ), tickRate( world ) );
        }
        else 
        {
            if ( bIsOn )
            {        
	    		changeStateToClearEntityCollision(world, i, j, k);
            }
        }
        
        fullyValidateBlock(world, i, j, k);
    }
	
	@Override
    public boolean isAirBlock()
    {
    	return true;
    }
    
	@Override
	public boolean triggersBuddy()
	{
		return false;
	}
	
    //------------- Class Specific Methods ------------//
    
	protected void removeSelf(World world, int i, int j, int k)
	{
		// this function exists as the regular block shouldn't notify the client when it is removed, but the glowing variety should 
		
    	world.setBlock( i, j, k, 0, 0, 0 );        	
	}
    
	public boolean isEntityCollidingFlagOn(IBlockAccess iBlockAccess, int i, int j, int k)
	{
        int iMetaData = iBlockAccess.getBlockMetadata( i, j, k );
        
		return ( iMetaData & 1 ) > 0;
	}
	
	public void setEntityCollidingFlag(World world, int i, int j, int k, boolean bEntityColliding)
	{
        int iMetaData = ( world.getBlockMetadata( i, j, k ) ) & (~1); // filter out old state
        
        if ( bEntityColliding )
        {
        	iMetaData |= 1;
        }

        // need notify as this can affect the state of neighboring lenses and Detector Blocks
        
        world.setBlockMetadataWithNotifyNoClient( i, j, k, iMetaData );

        if (LOGIC_DEBUG_DISPLAY)
        {
        	world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        }
	}
	
	public boolean isDetectorLogicFlagOn(IBlockAccess iBlockAccess, int i, int j, int k)
	{
        int iMetaData = iBlockAccess.getBlockMetadata( i, j, k );
        
		return ( iMetaData & 2 ) > 0;
	}
	
	public void setIsDetectorLogicFlag(World world, int i, int j, int k, boolean bIsDetectorLogic)
	{
        int iMetaData = ( world.getBlockMetadata( i, j, k ) ) & (~2); // filter out old state
        
        if ( bIsDetectorLogic )
        {
        	iMetaData |= 2;
        }
        
        world.setBlockMetadata( i, j, k, iMetaData );
        
        if (LOGIC_DEBUG_DISPLAY)
        {
        	world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        }
	}
	
	/*
	 * blocks are considered intersection points if multiple lens beams pass through them or if both beams and detector logic are present.  
	 * They are NOT considered intersection points if only multiple detector blocks are facing them without a lens beam.
	 */
	public boolean isIntersectionPointFlagOn(IBlockAccess iBlockAccess, int i, int j, int k)
	{
        int iMetaData = iBlockAccess.getBlockMetadata( i, j, k );
        
		return ( iMetaData & 4 ) > 0;
	}
	
	public void setIsIntersectionPointFlag(World world, int i, int j, int k, boolean bIsIntersectionPoint)
	{
        int iMetaData = ( world.getBlockMetadata( i, j, k ) ) & (~4); // filter out old state
        
        if ( bIsIntersectionPoint )
        {
        	iMetaData |= 4;
        }
        
        world.setBlockMetadata( i, j, k, iMetaData );
        
        if (LOGIC_DEBUG_DISPLAY)
        {
        	world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        }
	}
	
	public boolean isLitFlagOn(IBlockAccess iBlockAccess, int i, int j, int k)
	{
        int iMetaData = iBlockAccess.getBlockMetadata( i, j, k );
        
		return ( iMetaData & 8 ) > 0;
	}
	
	public void setIsLitFlag(World world, int i, int j, int k, boolean bIsLitByLens)
	{
        int iMetaData = ( world.getBlockMetadata( i, j, k ) ) & (~8); // filter out old state
        
        if ( bIsLitByLens )
        {
        	iMetaData |= 8;
        }
        
        world.setBlockMetadata( i, j, k, iMetaData );
        
        if (LOGIC_DEBUG_DISPLAY)
        {
        	world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        }
	}
	
    public boolean isBlockGlowing( World world, int i, int j, int k )
    {
    	int iBlockID = world.getBlockId( i, j, k );
    	
    	return iBlockID == BTWBlocks.glowingDetectorLogic.blockID;
    }
    
    public void setBlockAsGlowing(World world, int i, int j, int k)
    {
    	int iMetaData = world.getBlockMetadata( i, j, k );
    	
    	BTWMod.isLensBeamBeingRemoved = true;
    	
    	world.setBlockAndMetadataWithNotify( i, j, k, BTWBlocks.glowingDetectorLogic.blockID, iMetaData );
    	
    	BTWMod.isLensBeamBeingRemoved = false;
    	
    	if ( isEntityCollidingFlagOn(world, i, j, k) )
    	{
    		// if the entity colliding flag is on, then we need to schedule an update for the new block ID
    		
    		world.scheduleBlockUpdate( i, j, k, world.getBlockId( i, j, k ), tickRate( world ) );
    	}
    }
    
    public void setBlockAsNotGlowing(World world, int i, int j, int k)
    {
    	int iMetaData = world.getBlockMetadata( i, j, k );
    	
    	BTWMod.isLensBeamBeingRemoved = true;
    	
    	world.setBlockAndMetadataWithNotify( i, j, k, BTWBlocks.detectorLogic.blockID, iMetaData );
    	
    	BTWMod.isLensBeamBeingRemoved = false;
    	
    	if ( isEntityCollidingFlagOn(world, i, j, k) )
    	{
    		// if the entity colliding flag is on, then we need to schedule an update for the new block ID
    		
    		world.scheduleBlockUpdate( i, j, k, world.getBlockId( i, j, k ), tickRate( world ) );
    	}
    }
    
    private boolean isEntityWithinBounds(World world, int i, int j, int k)
    {    	
        List list = world.getEntitiesWithinAABB( Entity.class, 
        		AxisAlignedBB.getAABBPool().getAABB( (double)i, (double)j, (double)k, 
				(double)(i + 1), (double)(j + 1), (double)(k + 1) ) );
        
        if( list != null && list.size() > 0 )
        {
            for(int listIndex = 0; listIndex < list.size(); listIndex++)
            {
            	Entity targetEntity = (Entity)list.get( listIndex );
            	
            	// client
            	if ( !( targetEntity instanceof EntityFX && !MinecraftServer.getIsServer()) && !( targetEntity instanceof BlockLiftedByPlatformEntity) )
        		// server
            	//if ( !( targetEntity instanceof FCEntityBlockLiftedByPlatform ) )
            	{
            		return true;            		
            	}
            }
        }
        
        return false;
    }
    
    private boolean checkForNeighboringDetector(World world, int i, int j, int k)
    {
    	for ( int iTempFacing = 0; iTempFacing <= 5; iTempFacing++ )
    	{
    		BlockPos tempPos = new BlockPos( i, j, k );
    		
    		tempPos.addFacingAsOffset(iTempFacing);
    		
    		if (world.getBlockId(tempPos.x, tempPos.y, tempPos.z) ==
				BTWBlocks.detectorBlock.blockID )
    		{    			
    			if (( (DetectorBlock)( BTWBlocks.detectorBlock) ).getFacing(world, tempPos.x, tempPos.y, tempPos.z) ==
					Block.getOppositeFacing(iTempFacing) )
    			{
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
    
    public void notifyNeighboringDetectorBlocksOfChange(World world, int i, int j, int k)
    {
    	// this function should be called instead of the normal "OnNotify" functions in world when setting the metadata
    	// to prevent weirdness with vanilla blocks.
    	
    	for ( int iFacing = 0; iFacing <= 5; iFacing++ )
    	{
    		BlockPos targetPos = new BlockPos( i, j, k );
    		targetPos.addFacingAsOffset(iFacing);
    		
    		int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
    		
    		if ( iTargetBlockID == BTWBlocks.detectorBlock.blockID )
    		{
    			Block.blocksList[iTargetBlockID].onNeighborBlockChange(world,
																	   targetPos.x, targetPos.y, targetPos.z, world.getBlockId(i, j, k));
			}
    	}
    }
    
    public boolean hasValidLensSource(World world, int i, int j, int k)
    {
    	for ( int iTempFacing = 0; iTempFacing <=5; iTempFacing++ )
    	{
    		if ( hasValidLensSourceToFacing(world, i, j, k, iTempFacing) )
    		{
    			return true;
    		}	
    	}
    	
    	return false;
    }
    
    public boolean hasValidLensSourceIgnoreFacing(World world, int i, int j, int k, int iIgnoreFacing)
    {
    	for ( int iTempFacing = 0; iTempFacing <=5; iTempFacing++ )
    	{
    		if ( iTempFacing != iIgnoreFacing )
    		{
	    		if ( hasValidLensSourceToFacing(world, i, j, k, iTempFacing) )
	    		{
	    			return true;
	    		}
    		}
    	}
    	
    	return false;
    }
    
    public boolean hasMultipleValidLensSources(World world, int i, int j, int k)
    {
    	int iLensCount = 0;
    	
    	for ( int iTempFacing = 0; iTempFacing <=5; iTempFacing++ )
    	{
    		if ( hasValidLensSourceToFacing(world, i, j, k, iTempFacing) )
    		{
    			iLensCount++;
    			
    			if ( iLensCount > 1 )
    			{
    				return true;
    			}
    		}	
    	}
    	
    	return false;
    }
    
    public boolean hasMultipleValidLensSourcesIgnoreFacing(World world, int i, int j, int k, int iIgnoreFacing)
    {
    	int iLensCount = 0;
    	
    	for ( int iTempFacing = 0; iTempFacing <=5; iTempFacing++ )
    	{
    		if ( iTempFacing != iIgnoreFacing )
    		{
	    		if ( hasValidLensSourceToFacing(world, i, j, k, iTempFacing) )
	    		{
	    			iLensCount++;
	    			
	    			if ( iLensCount > 1 )
	    			{
	    				return true;
	    			}
	    		}
    		}
    	}
    	
    	return false;
    }
    
    public int countValidLensSources(World world, int i, int j, int k)
    {
    	int iLensCount = 0;
    	
    	for ( int iTempFacing = 0; iTempFacing <=5; iTempFacing++ )
    	{
    		if ( hasValidLensSourceToFacing(world, i, j, k, iTempFacing) )
    		{
    			iLensCount++;
    		}	
    	}
    	
    	return iLensCount;
    }
    
    public int countValidLensSourcesIgnoreFacing(World world, int i, int j, int k, int iIgnoreFacing)
    {
    	int iLensCount = 0;
    	
    	for ( int iTempFacing = 0; iTempFacing <=5; iTempFacing++ )
    	{
    		if ( iTempFacing != iIgnoreFacing )
    		{
	    		if ( hasValidLensSourceToFacing(world, i, j, k, iTempFacing) )
	    		{
	    			iLensCount++;
	    		}
    		}
    	}
    	
    	return iLensCount;
    }
    
    public boolean hasValidLensSourceToFacing(World world, int i, int j, int k, int iFacing)
    {
    	return getRangeToValidLensSourceToFacing(world, i, j, k, iFacing) > 0;
    }
    
    /* 
     * returns 0 if no valid source within the maximum range of a lens
     */
    public int getRangeToValidLensSourceToFacing(World world, int i, int j, int k, int iFacing)
    {
    	BlockPos tempPos = new BlockPos( i, j, k );
    	
    	for (int iDistance = 1; iDistance <= LensBlock.LENS_MAX_RANGE; iDistance++ )
    	{
    		tempPos.addFacingAsOffset(iFacing);
    		
    		int iTempBlockID = world.getBlockId(tempPos.x, tempPos.y, tempPos.z);
    		
    		if ( iTempBlockID == BTWBlocks.lens.blockID )
    		{
    			LensBlock lensBlock = (LensBlock)(BTWBlocks.lens);
    			if (lensBlock.getFacing(world, tempPos.x, tempPos.y, tempPos.z) ==
					Block.getOppositeFacing(iFacing) )
    			{
					return iDistance;
    			}
    			
    			return 0;
    		}
    		else if ( !isLogicBlock(iTempBlockID) )
    		{
    			return 0;
    		}
    	}   	
    	
    	return 0;
    }
    
    public boolean verifyLitByLens(World world, int i, int j, int k)
    {
    	for ( int iTempFacing = 0; iTempFacing <=5; iTempFacing++ )
    	{
    		if ( hasValidLitLensSourceToFacing(world, i, j, k, iTempFacing) )
    		{
    			return true;
    		}	
    	}
    	
    	return false;
    }
    
    public boolean verifyLitByLensIgnoreFacing(World world, int i, int j, int k, int iIgnoreFacing)
    {
    	for ( int iTempFacing = 0; iTempFacing <=5; iTempFacing++ )
    	{
    		if ( iTempFacing != iIgnoreFacing )
    		{
	    		if ( hasValidLitLensSourceToFacing(world, i, j, k, iTempFacing) )
	    		{
	    			return true;
	    		}
    		}
    	}
    	
    	return false;
    }
    
    public boolean hasValidLitLensSourceToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
    {
    	return getRangeToValidLitLensSourceToFacing(blockAccess, i, j, k, iFacing) > 0;
    }
    
    /* 
     * returns 0 if no valid source within the maximum range of a lens
     */
    public int getRangeToValidLitLensSourceToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
    {
    	BlockPos tempPos = new BlockPos( i, j, k );
    	
    	for (int iDistance = 1; iDistance <= LensBlock.LENS_MAX_RANGE; iDistance++ )
    	{
    		tempPos.addFacingAsOffset(iFacing);
    		
    		int iTempBlockID = blockAccess.getBlockId(tempPos.x, tempPos.y, tempPos.z);
    		
    		if ( iTempBlockID == BTWBlocks.lens.blockID )
    		{
    			LensBlock lensBlock = (LensBlock)(BTWBlocks.lens);
    			if (lensBlock.getFacing(blockAccess, tempPos.x, tempPos.y, tempPos.z) ==
					Block.getOppositeFacing(iFacing) )
    			{
    				if ( lensBlock.isLit(blockAccess, tempPos.x, tempPos.y, tempPos.z) )
    				{
    					return iDistance;
    				}
    			}
    			
    			return 0;
    		}
    		else if ( isLogicBlock(iTempBlockID) )
    		{
    			if (!isLitFlagOn(blockAccess, tempPos.x, tempPos.y, tempPos.z) || isEntityCollidingFlagOn(blockAccess, tempPos.x, tempPos.y, tempPos.z) )
    			{    					
    				return 0;
    			}
    		}
    		else
    		{
    			return 0;
    		}
    	}   	
    	
    	return 0;
    }
    
    /*
     * The beam created does not include the block specified 
     */
    public void createLensBeamFromBlock(World world, int i, int j, int k, int iFacing, int iMaxRange)
    {
    	BlockPos tempPos = new BlockPos( i, j, k );
    	
    	for ( int iDistance = 1; iDistance <= iMaxRange; iDistance++ )
    	{
    		tempPos.addFacingAsOffset(iFacing);
    		
    		int iTempBlockID = world.getBlockId(tempPos.x, tempPos.y, tempPos.z);
    		
    		if ( iTempBlockID == 0 )
    		{
    			if ( !world.setBlock(tempPos.x, tempPos.y, tempPos.z, BTWBlocks.detectorLogic.blockID, 0, 0) )
    			{
    				// we couldn't set the block and have probably either hit the height limit or the edge of the loaded chunks.
    				// stop extending the beam beyond this point
    				
    				break;
    			}
    			
    	        if (LOGIC_DEBUG_DISPLAY)
    	        {
    	        	world.markBlockRangeForRenderUpdate(tempPos.x, tempPos.y, tempPos.z, tempPos.x, tempPos.y, tempPos.z);
    	        }
    		}
    		else if ( isLogicBlock(iTempBlockID) )
    		{
    			setIsIntersectionPointFlag(world, tempPos.x, tempPos.y, tempPos.z, true);
    			
    		}
    		else
    		{
    			break;
    		}
    	}    	
    }
    
    /*
     * The beam created does not include the block specified 
     */
    public void removeLensBeamFromBlock(World world, int i, int j, int k, int iFacing, int iMaxRange)
    {
    	BTWMod.isLensBeamBeingRemoved = true;
    	
    	BlockPos tempPos = new BlockPos( i, j, k );
    	int iOppositeFacing = Block.getOppositeFacing(iFacing);
    	
    	for (int iDistance = 1; iDistance <= LensBlock.LENS_MAX_RANGE; iDistance++ )
    	{
    		tempPos.addFacingAsOffset(iFacing);
    		
    		if ( isLogicBlock(world, tempPos.x, tempPos.y, tempPos.z) )
    		{
    			// Intersection points must be validated when a contributing beam is removed
    			
    			if ( isIntersectionPointFlagOn(world, tempPos.x, tempPos.y, tempPos.z) )
    			{	
    				if ( isDetectorLogicFlagOn(world, tempPos.x, tempPos.y, tempPos.z) )
    				{
    					if ( !hasValidLensSourceIgnoreFacing(world, tempPos.x, tempPos.y, tempPos.z, iOppositeFacing) )
    					{
        					setIsIntersectionPointFlag(world, tempPos.x, tempPos.y, tempPos.z, false);
    					}
    				}	
    				else if ( !hasMultipleValidLensSourcesIgnoreFacing(world, tempPos.x, tempPos.y, tempPos.z, iOppositeFacing) )
    				{
    					setIsIntersectionPointFlag(world, tempPos.x, tempPos.y, tempPos.z, false);
    				}
    				
    				if ( isLitFlagOn(world, tempPos.x, tempPos.y, tempPos.z) )
    				{
    					if ( !verifyLitByLensIgnoreFacing(world, tempPos.x, tempPos.y, tempPos.z, iOppositeFacing) )
    					{
    						unlightBlock(world, tempPos.x, tempPos.y, tempPos.z);
    					}
    					else if ( isBlockGlowing(world, tempPos.x, tempPos.y, tempPos.z) )
        		    	{
        		    		if ( !shouldBeGlowing(world, tempPos.x, tempPos.y, tempPos.z) )
        		    		{
            		    		setBlockAsNotGlowing(world, tempPos.x, tempPos.y, tempPos.z);
        		    		}
        		    	}						
    				}
    			}
    			else
    			{
    				if ( !isBlockGlowing(world, tempPos.x, tempPos.y, tempPos.z) )
    				{
	    				if ( !world.setBlock(tempPos.x, tempPos.y, tempPos.z, 0, 0, 0) )
	    				{
	        				// we couldn't set the block and have probably either hit the height limit or the edge of the loaded chunks.
	        				// stop removing the beam beyond this point
	    					
	    					break;
	    				}
    				}
    				else
    				{
	    				if ( !world.setBlockWithNotify(tempPos.x, tempPos.y, tempPos.z, 0) )
	    				{
	        				// we couldn't set the block and have probably either hit the height limit or the edge of the loaded chunks.
	        				// stop removing the beam beyond this point
	    					
	    					break;
	    				}
    				}
    				
        	        if (LOGIC_DEBUG_DISPLAY)
        	        {
        	        	world.markBlockRangeForRenderUpdate(tempPos.x, tempPos.y, tempPos.z, tempPos.x, tempPos.y, tempPos.z);
        	        }
    			}    			
    		}
    		else
    		{
    			break;
    		}
    	}
    	
    	BTWMod.isLensBeamBeingRemoved = false;
    }
    
    public void lightBlock(World world, int i, int j, int k)
    {
		setIsLitFlag(world, i, j, k, true);
		
		if ( isDetectorLogicFlagOn(world, i, j, k) )
		{
    		notifyNeighboringDetectorBlocksOfChange(world, i, j, k);
		}
    }
    
    public void unlightBlock(World world, int i, int j, int k)
    {
		setIsLitFlag(world, i, j, k, false);
		
    	if ( isBlockGlowing( world, i, j, k ) )
    	{
    		setBlockAsNotGlowing(world, i, j, k);
    	}
    	
		if ( isDetectorLogicFlagOn(world, i, j, k) )
		{
    		notifyNeighboringDetectorBlocksOfChange(world, i, j, k);
		}
    }
    
	public void changeStateToRegisterEntityCollision(World world, int i, int j, int k)
	{
		setEntityCollidingFlag(world, i, j, k, true);

		/*
		if ( IsDetectorLogicFlagOn( world, i, j, k ) )
		{
			NotifyNeighboringDetectorBlocksOfChange( world, i, j, k );
		}
		*/
		
		if ( isLitFlagOn(world, i, j, k) )
		{
			if ( !isBlockGlowing( world, i, j, k ) )
	    	{
	    		setBlockAsGlowing(world, i, j, k);
	    	}
			else
			{
				// mark the block as dirty due to entity collision blocking projection onto surfaces
				
	        	world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
			}
			
			// turn off any surrounding beams blocked by this one
			
			for ( int iTempFacing = 0; iTempFacing <=5; iTempFacing++ )
			{
				int iRangeToSource = getRangeToValidLitLensSourceToFacing(world, i, j, k, iTempFacing);
				
				if ( iRangeToSource > 0 )
				{
					int iBeamRangeRemaining = LensBlock.LENS_MAX_RANGE - iRangeToSource;
					
					if ( iBeamRangeRemaining > 0 )
					{
						turnBeamOffFromBlock(world, i, j, k, Block.getOppositeFacing(iTempFacing), iBeamRangeRemaining);
					}
				}
			}
		}		
	}
	
	public void changeStateToClearEntityCollision(World world, int i, int j, int k)
	{
		setEntityCollidingFlag(world, i, j, k, false);
		
		/*
		if ( IsDetectorLogicFlagOn( world, i, j, k ) )
		{
			NotifyNeighboringDetectorBlocksOfChange( world, i, j, k );			
		}
		*/
		
		// turn on any surrounding beams that were blocked by this one
		
		for ( int iTempFacing = 0; iTempFacing <=5; iTempFacing++ )
		{
			int iRangeToSource = getRangeToValidLitLensSourceToFacing(world, i, j, k, iTempFacing);
			
			if ( iRangeToSource > 0 )
			{
				int iBeamRangeRemaining = LensBlock.LENS_MAX_RANGE - iRangeToSource;
				
				if ( iBeamRangeRemaining > 0 )
				{
					turnBeamOnFromBlock(world, i, j, k, Block.getOppositeFacing(iTempFacing), iBeamRangeRemaining);
				}
			}
		}
		
		if ( isLitFlagOn(world, i, j, k) )
		{
			if ( isBlockGlowing( world, i, j, k ) )
	    	{
	    		if ( !shouldBeGlowing(world, i, j, k) )
	    		{
		    		setBlockAsNotGlowing(world, i, j, k);
	    		}
	    		else
	    		{
					// mark the block as dirty due to entity collision blocking projection onto surfaces
					
		        	world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
	    		}
	    	}
		}
	}
    
    /*
     * The beam affected does not include the block specified 
     */
    public void turnBeamOnFromBlock(World world, int i, int j, int k, int iFacing, int iMaxRange)
    {
    	BlockPos tempPos = new BlockPos( i, j, k );
    	
    	for ( int iDistance = 1; iDistance <= iMaxRange; iDistance++ )
    	{
    		tempPos.addFacingAsOffset(iFacing);
    		
    		int iTempBlockID = world.getBlockId(tempPos.x, tempPos.y, tempPos.z);
    		
    		if ( isLogicBlock(iTempBlockID) )
    		{
				lightBlock(world, tempPos.x, tempPos.y, tempPos.z);
				
    			if ( isEntityCollidingFlagOn(world, tempPos.x, tempPos.y, tempPos.z) )
    			{
    				// light can't propagate through entities
    				
    		    	if (isLogicBlock(world, tempPos.x, tempPos.y, tempPos.z) &&
						!isBlockGlowing(world, tempPos.x, tempPos.y, tempPos.z) )
    		    	{
    		    		setBlockAsGlowing(world, tempPos.x, tempPos.y, tempPos.z);
    		    	}    		    	
    				
    				break;
    			}    			
    		}
    		else
    		{
    			if ( !world.isAirBlock(tempPos.x, tempPos.y, tempPos.z) )
    			{
        			// create glowing block in previous
    				
    		    	BlockPos previousPos = new BlockPos(tempPos.x, tempPos.y, tempPos.z);
    		    	
    		    	previousPos.addFacingAsOffset(Block.getOppositeFacing(iFacing));
    		    	
    		    	if (isLogicBlock(world, previousPos.x, previousPos.y, previousPos.z) &&
						!isBlockGlowing(world, previousPos.x, previousPos.y, previousPos.z) )
    		    	{
    		    		setBlockAsGlowing(world, previousPos.x, previousPos.y, previousPos.z);
    		    	}    		    	
    			}    			
    			
    			break;
    		}
    	}    	
    }
    
    
    /*
     * The beam affected does not include the block specified 
     */
    public void turnBeamOffFromBlock(World world, int i, int j, int k, int iFacing, int iMaxRange)
    {
    	BlockPos tempPos = new BlockPos( i, j, k );
    	int iOppositeFacing = Block.getOppositeFacing(iFacing);
    	
    	for ( int iDistance = 1; iDistance <= iMaxRange; iDistance++ )
    	{
    		tempPos.addFacingAsOffset(iFacing);
    		
    		int iTempBlockID = world.getBlockId(tempPos.x, tempPos.y, tempPos.z);
    		
    		if ( isLogicBlock(iTempBlockID) )
    		{
    			if ( isIntersectionPointFlagOn(world, tempPos.x, tempPos.y, tempPos.z) )
    			{	
					if ( !verifyLitByLensIgnoreFacing(world, tempPos.x, tempPos.y, tempPos.z, iOppositeFacing) )
					{
						unlightBlock(world, tempPos.x, tempPos.y, tempPos.z);
					}
					else if ( isBlockGlowing(world, tempPos.x, tempPos.y, tempPos.z) )
    		    	{
    		    		if ( !shouldBeGlowing(world, tempPos.x, tempPos.y, tempPos.z) )
    		    		{
        		    		setBlockAsNotGlowing(world, tempPos.x, tempPos.y, tempPos.z);
    		    		}
    		    	}						
    			}
    			else
    			{
					unlightBlock(world, tempPos.x, tempPos.y, tempPos.z);
    			}
    			
    			if ( isEntityCollidingFlagOn(world, tempPos.x, tempPos.y, tempPos.z) )
    			{
    				// light can't propagate through entities
    				
    				break;
    			}
    		}
    		else
    		{
    			break;
    		}
    	}    	
    }

    protected boolean shouldBeProjectingToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
    {
    	if ( isEntityCollidingFlagOn(blockAccess, i, j, k) )
    	{
    		return false;
    	}
    	
    	BlockPos targetPos = new BlockPos( i, j, k, iFacing );
    	
		if ( blockAccess.isBlockNormalCube(targetPos.x, targetPos.y, targetPos.z) )
		{
			// we have a solid block neigboring that light can project onto.  Check if there's a beam hitting it.
			
			if ( hasValidLitLensSourceToFacing(blockAccess, i, j, k, Block.getOppositeFacing(iFacing)) )
			{
				return true;
			}				
		}
		
    	return false;
    }
    
    protected boolean shouldBeGlowingToFacing(World world, int i, int j, int k, int iFacing)
    {
    	BlockPos targetPos = new BlockPos( i, j, k );
		targetPos.addFacingAsOffset(iFacing);
    	
		if ( !world.isAirBlock(targetPos.x, targetPos.y, targetPos.z) )
		{
			// we have a neigboring block that can interrupt a beam.  Check if there's a beam hitting it.
			
			if ( hasValidLitLensSourceToFacing(world, i, j, k, Block.getOppositeFacing(iFacing)) )
			{
				return true;
			}				
		}
		
    	return false;
    }
    
    /*
     * Assumes the block is lit
     */
    private boolean shouldBeGlowing(World world, int i, int j, int k)
    {
    	if ( this.isEntityCollidingFlagOn(world, i, j, k) )
		{
    		return true;
		}
    	
    	for ( int iTempFacing = 0; iTempFacing <=5; iTempFacing++ )
    	{
    		if ( shouldBeGlowingToFacing(world, i, j, k, iTempFacing) )
    		{
    			return true;
    		}	
    	}
    	
    	return false;
    }
    
    static boolean isLogicBlock(IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iBlockID = blockAccess.getBlockId( i, j, k );
    	
    	return isLogicBlock(iBlockID);
    	
    }
    
    static boolean isLogicBlock(int iBlockID)
    {
    	return ( iBlockID ==  BTWBlocks.detectorLogic.blockID ||
			iBlockID ==  BTWBlocks.glowingDetectorLogic.blockID );
    }
    
	void propagateBeamsThroughBlock(World world, int i, int j, int k)
	{
		boolean bIsLit = false;
		int iSourceCount = 0;
		
		if ( !world.setBlock( i, j, k, BTWBlocks.detectorLogic.blockID, 0, 0 ) )
		{
			// we're probably on a chunk boundary or at the height limit.  Don't try to propagate
			
			return;
		}
		
        if (LOGIC_DEBUG_DISPLAY)
        {
        	world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        }		
		
		for ( int iTempFacing = 0; iTempFacing <= 5; iTempFacing++ )
		{
			int iRangeToSource = getRangeToValidLensSourceToFacing(world, i, j, k, iTempFacing);
			
			if ( iRangeToSource > 0 )
			{
				iSourceCount++;
				
				int iRangeRemaining = LensBlock.LENS_MAX_RANGE - iRangeToSource;
				
				if ( iRangeRemaining > 0 )
				{
					int iOppositeFacing = Block.getOppositeFacing(iTempFacing);
					
					createLensBeamFromBlock(world, i, j, k, iOppositeFacing, iRangeRemaining);
					
					if ( hasValidLitLensSourceToFacing(world, i, j, k, iTempFacing) )
					{
						turnBeamOnFromBlock(world, i, j, k, iOppositeFacing, iRangeRemaining);
						
						bIsLit = true;
					}
				}
				else
				{
					if ( hasValidLitLensSourceToFacing(world, i, j, k, iTempFacing) )
					{
						bIsLit = true;
					}
				}
			}
		}
		
		if ( bIsLit )
		{
			lightBlock(world, i, j, k);
			
    		if ( shouldBeGlowing(world, i, j, k) )
    		{
	    		setBlockAsGlowing(world, i, j, k);
    		}			
		}
		
		if ( iSourceCount > 1 )
		{
			setIsIntersectionPointFlag(world, i, j, k, true);
		}
	}
    
	public void fullyValidateBlock(World world, int i, int j, int k)
	{
		boolean bHasDetector = checkForNeighboringDetector(world, i, j, k);
		
		if (bHasDetector != isDetectorLogicFlagOn(world, i, j, k) )
		{
			setIsDetectorLogicFlag(world, i, j, k, bHasDetector);
		}
		
		boolean bShouldBeLit = false;
		int iNumLensSources = 0;
		
		// cycle through potential sources
		
		for ( int iTempFacing = 0; iTempFacing <= 5; iTempFacing++ )
		{
			int iRangeToSource = getRangeToValidLensSourceToFacing(world, i, j, k, iTempFacing);
			
			if ( iRangeToSource > 0 )
			{
				iNumLensSources++;
				
				if ( !bShouldBeLit )
				{
					// FCTODO: This can be optimized so as not to require the 2nd scan along the beam
					if ( hasValidLitLensSourceToFacing(world, i, j, k, iTempFacing) )
					{						
						bShouldBeLit = true;
					}
				}
				
				// check for beam propagation
				
				int iRangeRemaining = LensBlock.LENS_MAX_RANGE - iRangeToSource;
				
				if ( iRangeRemaining > 0 )
				{
					BlockPos targetPos = new BlockPos( i, j, k );
					
					targetPos.addFacingAsOffset(Block.getOppositeFacing(iTempFacing));
					
					if (world.getBlockId(targetPos.x, targetPos.y, targetPos.z) == 0 )
					{
						// this block should contain a logic block, but doesn't.
						
						propagateBeamsThroughBlock(world, targetPos.x, targetPos.y, targetPos.z);
					}
				}
			}				
		}
		
		if ( iNumLensSources == 0 && !isDetectorLogicFlagOn(world, i, j, k) )
		{
			// this is an orphaned logic block.  Kill it.
			
	    	BTWMod.isLensBeamBeingRemoved = true;
	    	
	    	removeSelf(world, i, j, k);
			
	    	BTWMod.isLensBeamBeingRemoved = false;
		}
		else
		{
			boolean bShouldBeIntersectionPoint = false;
			
			if ( iNumLensSources > 1 || ( iNumLensSources == 1 && isDetectorLogicFlagOn(world, i, j, k) ) )
			{						
				bShouldBeIntersectionPoint = true;
			}
			
			if (bShouldBeIntersectionPoint != isIntersectionPointFlagOn(world, i, j, k) )
			{
				setIsIntersectionPointFlag(world, i, j, k, bShouldBeIntersectionPoint);
			}
			
			if (bShouldBeLit != isLitFlagOn(world, i, j, k) )
			{
				if ( bShouldBeLit )
				{
					lightBlock(world, i, j, k);
				}
				else
				{
					unlightBlock(world, i, j, k);
				}
			}
		}
		
		if ( isLitFlagOn(world, i, j, k) )
		{
			// we don't have to worry about turning glowing off if it's not lit as that should be handle by the unlight code above
			
			boolean bShouldGlow = shouldBeGlowing(world, i, j, k);
			
			if ( bShouldGlow != this.isBlockGlowing( world, i, j, k ) )
			{
				if ( bShouldGlow )
				{
		    		setBlockAsGlowing(world, i, j, k);
				}
				else
				{
		    		setBlockAsNotGlowing(world, i, j, k);
				}
			}			
		}
	}
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "fcBlockLens_spotlight" );
    }	
    
    //------------- Custom Renderers ------------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	if (LOGIC_DEBUG_DISPLAY)
    	{
    		return renderDetectorLogicDebug(renderBlocks, renderBlocks.blockAccess, i, j, k, this);
    	}
    	else
    	{
        	return false;
    	}
    }

    @Environment(EnvType.CLIENT)
    public boolean renderDetectorLogicDebug
    ( 
		RenderBlocks renderBlocks, 
		IBlockAccess blockAccess, 
		int i, int j, int k, 
		Block block 
	)
    {
    	/*
    	int iTexture = blockIndexInTexture;
    	
    	if ( IsEntityCollidingFlagOn( blockAccess, i, j, k ) )
    	{
    		iTexture = m_iDebugTextureCollision;
    	}
    	else if ( IsLitFlagOn( blockAccess, i, j, k ) )
    	{
    		iTexture = m_iDebugTextureOn;
    	}
    	else
    	{
    		iTexture = m_iDebugTextureOff;
    	}

    	if ( !IsIntersectionPointFlagOn( blockAccess, i, j, k ) )
    	{
	        block.setBlockBounds( 0.5F - ( 2.0F / 16.0F), 0.5F - ( 2.0F / 16.0F), 0.5F - ( 2.0F / 16.0F), 
	        		0.5F + ( 2.0F / 16.0F), 0.5F + ( 2.0F / 16.0F), 0.5F + ( 2.0F / 16.0F) );
	        
	        FCClientUtilsRender.RenderStandardBlockWithTexture( renderBlocks, block, i, j, k, iTexture );
    	}
    	else
    	{
	        block.setBlockBounds( 0.5F - ( 6.0F / 16.0F), 0.5F - ( 2.0F / 16.0F), 0.5F - ( 2.0F / 16.0F), 
	        		0.5F + ( 6.0F / 16.0F), 0.5F + ( 2.0F / 16.0F), 0.5F + ( 2.0F / 16.0F) );
	        
	        FCClientUtilsRender.RenderStandardBlockWithTexture( renderBlocks, block, i, j, k, iTexture );
	        
	        block.setBlockBounds( 0.5F - ( 2.0F / 16.0F), 0.5F - ( 6.0F / 16.0F), 0.5F - ( 2.0F / 16.0F), 
	        		0.5F + ( 2.0F / 16.0F), 0.5F + ( 6.0F / 16.0F), 0.5F + ( 2.0F / 16.0F) );
	        
	        FCClientUtilsRender.RenderStandardBlockWithTexture( renderBlocks, block, i, j, k, iTexture );
	        
	        block.setBlockBounds( 0.5F - ( 2.0F / 16.0F), 0.5F - ( 2.0F / 16.0F), 0.5F - ( 6.0F / 16.0F), 
	        		0.5F + ( 2.0F / 16.0F), 0.5F + ( 2.0F / 16.0F), 0.5F + ( 6.0F / 16.0F) );
	        
	        FCClientUtilsRender.RenderStandardBlockWithTexture( renderBlocks, block, i, j, k, iTexture );
    	}
    	
    	if ( IsDetectorLogicFlagOn( blockAccess, i, j, k ) )
    	{
	        block.setBlockBounds( 0, 0, 0, 
	        		( 2.0F / 16.0F), ( 2.0F / 16.0F), ( 2.0F / 16.0F) );
	        
	        FCClientUtilsRender.RenderStandardBlockWithTexture( renderBlocks, block, i, j, k, iTexture );	        
    	}
        
        // restore block bounds
        
    	setBlockBounds( 0.0F, 0.0F, 0.0F, 
    			1.0F, 1.0F, 1.0F );
		*/
    	
    	return true;
    }

    @Environment(EnvType.CLIENT)
    public void renderDetectorLogicDebugInvBlock
    ( 
		RenderBlocks renderBlocks, 
		Block block, 
		int iItemDamage, 
		int iRenderType 
	)
    {
    	// no inv render
    }
}
