//FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class DetectorBlock extends Block
{
    private final static int DETECTOR_TICK_RATE = 4;
    
    public DetectorBlock(int iBlockID )
    {
        super( iBlockID, Material.rock );
        
        setHardness( 3.5F );
        
        setStepSound( Block.soundStoneFootstep );
        
        setUnlocalizedName( "fcBlockDetectorBlock" );        
        
        setTickRandomly( true );        
        
        setCreativeTab( CreativeTabs.tabRedstone );
    }

	@Override
    public int tickRate( World world )
    {
        return DETECTOR_TICK_RATE;
    }    
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
        return setFacing(iMetadata, Block.getOppositeFacing(iFacing));
    }
    
	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityLiving, ItemStack stack )
	{
		int iFacing = MiscUtils.convertPlacingEntityOrientationToBlockFacingReversed(entityLiving);
		
		setFacing(world, i, j, k, iFacing);
	}
	
	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        super.onBlockAdded( world, i, j, k );
        
        this.setBlockOn(world, i, j, k, false);
        
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    }

	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int l )
    {
		if ( !world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
		{
			world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
		}
    }
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
    	boolean bPlacedLogic = placeDetectorLogicIfNecessary(world, i, j, k);
    	boolean bDetected = checkForDetection(world, i, j, k);
    	
        int iFacingDirection = getFacing(world, i, j, k);
        
        if ( iFacingDirection == 1 )
        {
	    	if ( !bDetected )
	    	{
            	// facing upwards...check for rain or snow

		    	if ( world.isPrecipitatingAtPos(i, j + 1, k) )
		    	{
		    		bDetected = true;		    		
		    	}		    	
            }
	    	
    		// upward facing blocks have to periodically poll for weather changes
    		// or they risk missing them.
    		
	    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );		    		
    	}
    	
    	if ( bDetected )
    	{
    		if ( !isBlockOn(world, i, j, k) )
    		{
        		setBlockOn(world, i, j, k, true);
    		}
    	}
    	else
    	{
    		if ( isBlockOn(world, i, j, k) )
    		{
	    		if ( !bPlacedLogic )
	    		{
	    			setBlockOn(world, i, j, k, false);
	    		}
	    		else
	    		{
	    			// if we just placed the logic block, then wait a tick until we turn off
	    			// to give it a chance to detect anything that might be there
	    			
	    	    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
	    		}
    		}
    	}
    }
    
	@Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
		if ( !world.isUpdateScheduledForBlock(i, j, k, blockID) )
		{
	        int iFacing = getFacing(world, i, j, k);
	        
	        if ( iFacing == 1 )
	        {			
	        	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
	        }
	        else
	        {
	        	if (checkForDetection(world, i, j, k) != isBlockOn(world, i, j, k) )
	        	{
		        	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
	        	}
	        }
		}
    }
	
	@Override
    public int isProvidingWeakPower( IBlockAccess iblockaccess, int i, int j, int k, int l )
    {
		if ( isBlockOn(iblockaccess, i, j, k) )
		{
			return 15;
		}
		
    	return 0;
    }
    
	@Override
    public int isProvidingStrongPower( IBlockAccess blockAccess, int i, int j, int k, int iFacing )
    {
    	// at present, the detectors don't indirectly power anything...they have to be hooked up directly.
    	
    	return 0;
    }
    
	@Override
    public boolean canProvidePower()
    {
        return true;
    }
    
    @Override
    public void onArrowCollide(World world, int i, int j, int k, EntityArrow arrow)
    {
		if ( !world.isRemote )
		{
	        int iFacingDirection = getFacing(world, i, j, k);
	        
			BlockPos logicBlockPos = new BlockPos( i, j, k );
			
			logicBlockPos.addFacingAsOffset(iFacingDirection);
			
			if (world.getBlockId(logicBlockPos.x, logicBlockPos.y, logicBlockPos.z) == BTWBlocks.detectorLogic.blockID )
			{
				BTWBlocks.detectorLogic.onEntityCollidedWithBlock(world, logicBlockPos.x, logicBlockPos.y, logicBlockPos.z, arrow);
			}
		}
    }
    
	@Override
	public int getFacing(int iMetadata)
	{
    	return ( iMetadata & (~1) ) >> 1;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
    	iMetadata = ( iMetadata & 1 ) | ( iFacing << 1 );
    	
		return iMetadata;
	}
	
	@Override
	public boolean rotateAroundJAxis(World world, int i, int j, int k, boolean bReverse)
	{
		if ( super.rotateAroundJAxis(world, i, j, k, bReverse) )
		{
	    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
	    	
	    	return true;
		}
		
		return false;
	}

	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{		
		int iFacing = getFacing(world, i, j, k);
		
		iFacing = Block.cycleFacing(iFacing, bReverse);
		
		setFacing(world, i, j, k, iFacing);
		
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    	
        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        
    	world.notifyBlockChange( i, j, k, blockID );
    	
    	return true;
	}
	
    //------------- Class Specific Methods ------------//
    
    public boolean isBlockOn(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return isBlockOnFromMetadata(blockAccess.getBlockMetadata(i, j, k));
    }
    
    public boolean isBlockOnFromMetadata(int iMetadata)
    {
    	return ( iMetadata & 1 ) > 0;
    }
    
    public void setBlockOn(World world, int i, int j, int k, boolean bOn)
    {
    	if (bOn != isBlockOn(world, i, j, k) )
    	{
	    	int iMetaData = world.getBlockMetadata(i, j, k);
	    	
	    	if ( bOn )
	    	{
	    		iMetaData = iMetaData | 1;
	    		
		        world.playAuxSFX( BTWEffectManager.REDSTONE_CLICK_EFFECT_ID, i, j, k, 0 );
	    	}
	    	else
	    	{
	    		iMetaData = iMetaData & (~1);
	    	}
	    	
	        world.setBlockMetadataWithNotify( i, j, k, iMetaData );
	        
	        // we have to notify in a radius as some redstone blocks get their power state from up to two blocks away
	        
            world.notifyBlocksOfNeighborChange( i, j - 1, k, blockID );
            world.notifyBlocksOfNeighborChange( i, j + 1, k, blockID );
            world.notifyBlocksOfNeighborChange( i - 1, j, k, blockID );
            world.notifyBlocksOfNeighborChange( i + 1, j, k, blockID );
            world.notifyBlocksOfNeighborChange( i, j, k - 1, blockID );
            world.notifyBlocksOfNeighborChange( i, j, k + 1, blockID );
	        
	        // the following forces a re-render (for texture change)
	        
	        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );    	
        }
    }

    /* 
     * returns true if a new logic block needed to be placed
     */
    public boolean placeDetectorLogicIfNecessary(World world, int i, int j, int k)
    {
        int iFacing = getFacing(world, i, j, k);
        
        BlockPos targetPos = new BlockPos( i, j, k );
        
        targetPos.addFacingAsOffset(iFacing);
        
        int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
        
        if ( iTargetBlockID == 0 )
        {
        	DetectorLogicBlock logicBlock = (DetectorLogicBlock)(BTWBlocks.detectorLogic);
        	
        	world.setBlock(targetPos.x, targetPos.y, targetPos.z, logicBlock.blockID, 0, 0);
        	
        	logicBlock.setIsDetectorLogicFlag(world, targetPos.x, targetPos.y, targetPos.z, true);
        	
        	// fully validate the block as there may also be beams passing through it
        	
        	logicBlock.fullyValidateBlock(world, targetPos.x, targetPos.y, targetPos.z);
        	
        	return true;
        }
        else if ( iTargetBlockID == BTWBlocks.detectorLogic.blockID ||
    		iTargetBlockID == BTWBlocks.glowingDetectorLogic.blockID )
        {
        	DetectorLogicBlock logicBlock = (DetectorLogicBlock)(BTWBlocks.detectorLogic);
        	
        	if ( !logicBlock.isDetectorLogicFlagOn(world, targetPos.x, targetPos.y, targetPos.z) )
        	{
        		// this logic block was created by a lens.  Flag it as detector logic and as an intersection point
        		
            	logicBlock.setIsDetectorLogicFlag(world, targetPos.x, targetPos.y, targetPos.z, true);
            	
            	// legacy test to support old pre-lens detector logic
            	
            	if ( logicBlock.hasValidLensSource(world, targetPos.x, targetPos.y, targetPos.z) )
            	{            		
            		logicBlock.setIsIntersectionPointFlag(world, targetPos.x, targetPos.y, targetPos.z, true);
            	}
        	}
        }
        
    	return false;
    }
	
	public boolean checkForDetection(World world, int i, int j, int k)
	{
        int iFacing = this.getFacing(world, i, j, k);
        
        BlockPos targetPos = new BlockPos( i, j, k );
        
        targetPos.addFacingAsOffset(iFacing);
        
        int targetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
        
        if ( targetBlockID > 0 )
        {
	        if ( DetectorLogicBlock.isLogicBlock(targetBlockID) )
	        {
	        	// check if the logic block is on
	        	
	        	DetectorLogicBlock logicBlock = (DetectorLogicBlock)(BTWBlocks.detectorLogic);
	        	
	        	if (logicBlock.isEntityCollidingFlagOn(world, targetPos.x, targetPos.y, targetPos.z) ||
					logicBlock.isLitFlagOn(world, targetPos.x, targetPos.y, targetPos.z) )
	        	{
	        		return true;
	        	}
	        	else 
	        	{
	        		// check for fully grown wheat below the logic block
	        		
	        		int iBlockBelowID = world.getBlockId(targetPos.x, targetPos.y - 1, targetPos.z);
	        		
	            	if ( iBlockBelowID == Block.crops.blockID &&
						 world.getBlockMetadata(targetPos.x, targetPos.y - 1, targetPos.z) >= 7 )
	    			{
	                	return true;
	    			}	        		
	        	}
	        }        
	        else
	        {
	        	if ( targetBlockID == BTWBlocks.lens.blockID )
	        	{
	        		// if we're pointing towards a Lens, we act as a light-level detecor
	        		
	        		LensBlock lensBlock = (LensBlock)(BTWBlocks.lens);
	        		
	        		if (lensBlock.getFacing(world, targetPos.x, targetPos.y, targetPos.z) == Block.getOppositeFacing(iFacing) )
	        		{
	        			return lensBlock.isLit(world, targetPos.x, targetPos.y, targetPos.z);
	        		}
	        	}
	        	
	        	return true;
	        }
        }
        
		return false;
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBySideArray = new Icon[6];
    @Environment(EnvType.CLIENT)
    private Icon iconFront;
    @Environment(EnvType.CLIENT)
    private Icon iconFrontOn;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        Icon topIcon = register.registerIcon( "fcBlockDetectorBlock_top" );
        
        blockIcon = topIcon; // for hit effects

		iconFront = register.registerIcon("fcBlockDetectorBlock_front");
		iconFrontOn = register.registerIcon("fcBlockDetectorBlock_front_on");

		iconBySideArray[0] = register.registerIcon("fcBlockDetectorBlock_bottom");
		iconBySideArray[1] = topIcon;
        
        Icon sideIcon = register.registerIcon( "fcBlockDetectorBlock_side" );

		iconBySideArray[2] = sideIcon;
		iconBySideArray[3] = sideIcon;
		iconBySideArray[4] = sideIcon;
		iconBySideArray[5] = sideIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		// for item render
		
		if ( iSide == 3 )
		{
			return iconFront;
		}
		
		return iconBySideArray[iSide];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
		int iMetadata = blockAccess.getBlockMetadata( i, j, k );
		
		int iFacing = getFacing(iMetadata);
		
		if ( iFacing == iSide )
		{
			if ( isBlockOnFromMetadata(iMetadata) )
			{
				return iconFrontOn;
			}
			else
			{
				return iconFront;
			}
		}
		
		return iconBySideArray[iSide];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World world, int i, int j, int k, Random random)
    {
        if( isBlockOn(world, i, j, k) )
        {
            int iFacingDirection = getFacing(world, i, j, k);
            
        	float targeti = i;
        	float targetj = j;
        	float targetk = k;
        	
        	float targeti2 = targeti;
        	float targetj2 = targetj;
        	float targetk2 = targetk;
        	
            if ( iFacingDirection == 0 )
            {
            	targetj2 = targetj = targetj - 0.2F; 
            	targetk2 = targetk = targetk + 0.25F;
            	
            	targeti += 0.33F;
            	targeti2 += 0.66F;
            } 
            else if ( iFacingDirection == 1 )
            {
            	targetj2 = targetj = targetj + 1.1F; 
            	targetk2 = targetk = targetk + 0.25F;
            	
            	targeti += 0.33F;
            	targeti2 += 0.66F;
            } 
            else if ( iFacingDirection == 3 )
        	{
            	targetj2 = targetj = targetj + 0.75F; 
            	targetk2 = targetk = targetk + 1.1F;
            	
            	targeti += 0.33F;
            	targeti2 += 0.66F;
        	}
        	else if ( iFacingDirection == 2 )
        	{
        		targetj2 = targetj = targetj + 0.75F; 
        		targetk2 = targetk = targetk - 0.1F;
        		
        		targeti += 0.33F;
        		targeti2 += 0.66F;         		
        	}
        	else if ( iFacingDirection == 5 )
        	{
        		targeti2 = targeti = targeti + 1.1F;
        		targetj2 = targetj = targetj + 0.75F;
        		
        		targetk += 0.33;
        		targetk2 += 0.66F; 
        	}
        	else
        	{
        		targeti2 = targeti = targeti - 0.1F;
        		targetj2 = targetj = targetj + 0.75F;
        		
        		targetk += 0.33F;
        		targetk2 += 0.66F; 
        	}
            
            targeti += ( random.nextFloat() - 0.5F) * 0.1F;
            targetj += ( random.nextFloat() - 0.5F) * 0.1F;
            targetk += ( random.nextFloat() - 0.5F) * 0.1F;            
            
            float f = 1F / 15F;
            
            float f1 = f * 0.6F + 0.4F;            
            float f2 = f * f * 0.7F - 0.5F;
            float f3 = f * f * 0.6F - 0.7F;
            
            if(f2 < 0.0F)
            {
                f2 = 0.0F;
            }
            if(f3 < 0.0F)
            {
                f3 = 0.0F;
            }
   
            if ( random.nextFloat() >= 0.5F )
            {
            	world.spawnParticle("reddust", targeti, targetj, targetk, f1, f2, f3);
            }
            else
            {
            	world.spawnParticle("reddust", targeti2, targetj2, targetk2, f1, f2, f3);
            }
        }
    }
}
