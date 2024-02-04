// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.MechanicalBlock;
import btw.client.fx.BTWEffectManager;
import btw.client.render.util.RenderUtils;
import btw.block.util.MechPowerUtils;
import btw.item.BTWItems;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class AxleBlock extends Block
{
	protected static final double AXLE_WIDTH = 0.25D;
	protected static final double AXLE_HALF_WIDTH = (AXLE_WIDTH / 2D );
	
	static public final int AXLE_TICK_RATE = 1;
	
	protected static final int[][] axleFacingsForAlignment = new int[][] {{0, 1 }, {2, 3 }, {4, 5 } };

	public AxleBlock(int iBlockID)
	{
        super( iBlockID, BTWBlocks.plankMaterial);

        setHardness( 2F );        
        setAxesEffectiveOn(true);
        
        setBuoyancy(1F);
        
    	initBlockBounds(0.5D - AXLE_HALF_WIDTH, 0.5D - AXLE_HALF_WIDTH, 0D,
						0.5D + AXLE_HALF_WIDTH, 0.5D + AXLE_HALF_WIDTH, 1D);
    	
        setStepSound( soundWoodFootstep );
    	
        setUnlocalizedName( "fcBlockAxle" );        

        setCreativeTab( CreativeTabs.tabRedstone );
    }
	
	@Override
    public int tickRate( World world )
    {
    	return AXLE_TICK_RATE;
    }	
    
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
    	return setAxisAlignmentInMetadataBasedOnFacing(iMetadata, iFacing);
    }
    
	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        super.onBlockAdded( world, i, j, k );
        
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    }
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
    	// should only be called when the block is first added, to force it to validate
    	// its power level for the first time.  All following changes are instantaneous
    	
        setPowerLevel(world, i, j, k, 0);
        
    	validatePowerLevel(world, i, j, k);
    }
    
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iAxis = getAxisAlignment(blockAccess, i, j, k);
    	
    	switch ( iAxis )
    	{
    		case 0:
    			
            	return AxisAlignedBB.getAABBPool().getAABB(
						0.5D - AXLE_HALF_WIDTH, 0D, 0.5D - AXLE_HALF_WIDTH,
						0.5D + AXLE_HALF_WIDTH, 1D, 0.5D + AXLE_HALF_WIDTH);
		    	
    		case 1:
    			
            	return AxisAlignedBB.getAABBPool().getAABB(
						0.5D - AXLE_HALF_WIDTH, 0.5D - AXLE_HALF_WIDTH, 0D,
						0.5D + AXLE_HALF_WIDTH, 0.5D + AXLE_HALF_WIDTH, 1D);
		    	
	    	default:
		    	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0D, 0.5D - AXLE_HALF_WIDTH, 0.5F - AXLE_HALF_WIDTH,
						1D, 0.5D + AXLE_HALF_WIDTH, 0.5F + AXLE_HALF_WIDTH);
    	}
    }
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
    	validatePowerLevel(world, i, j, k);
    }
    
	@Override
    public int getMobilityFlag()
    {
    	// set the block to be destroyed by pistons to avoid problems with the generation of infinite mechanical power loops
    	
        return 1;
    }
	
    @Override
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
    	return isAxleOrientedTowardsFacing(blockAccess, i, j, k, iFacing);
	}
    
	@Override
    public int getMechanicalPowerLevelProvidedToAxleAtFacing(World world, int i, int j, int k, int iFacing)
    {
		int iAlignment = getAxisAlignment(world, i, j, k);
		
		if ( ( iFacing >> 1 ) == iAlignment )
		{
			return getPowerLevel(world, i, j, k);
		}
		
    	return 0;
    }
	
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2; // iron or better
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.hempFibers.itemID, 4, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 2, 0, fChanceOfDrop);
		
		return true;
	}
	
	@Override
	public int getFacing(int iMetadata)
	{
    	return getAxisAlignmentFromMetadata(iMetadata) << 1;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		return setAxisAlignmentInMetadataBasedOnFacing(iMetadata, iFacing);
	}
	
	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{	
		int iAxisAlignment = getAxisAlignment(world, i, j, k);
		
		if ( !bReverse )
		{
			iAxisAlignment++;
			
			if ( iAxisAlignment > 2 )
			{
				iAxisAlignment = 0;
			}
		}
		else
		{
			iAxisAlignment--;
			
			if ( iAxisAlignment < 0 )
			{
				iAxisAlignment = 2;
			}
		}
		
		setAxisAlignment(world, i, j, k, iAxisAlignment);
		
        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        
        setPowerLevel(world, i, j, k, 0);
        
    	validatePowerLevel(world, i, j, k);
    	
        world.markBlockForUpdate( i, j, k );
        
    	return true;
	}
	
    @Override
    public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k)
    {
    	return world.doesBlockHaveSolidTopSurface( i, j - 1, k );
    }
    
    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return -1F;        
    }
    
    //------------- Class Specific Methods ------------//
    
    /* 
     * return value is 0 for y aligned (j), 1 for z (k), and 2 for x (i)
     */
    public int getAxisAlignment(IBlockAccess iBlockAccess, int i, int j, int k)
    {
    	return ( iBlockAccess.getBlockMetadata( i, j, k ) >> 2 );
    }
    
    /* 
     * return value is 0 for y aligned (j), 1 for z (k), and 2 for x (i)
     */
    public void setAxisAlignment(World world, int i, int j, int k, int iAxisAlignment)
    {
    	int iMetaData = world.getBlockMetadata( i, j, k ) & 3; // filter out any old alignment
    	
    	iMetaData |= ( iAxisAlignment << 2 );
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetaData );
    }

    /* 
     * return value is 0 for y aligned (j), 1 for z (k), and 2 for x (i)
     */
    public int getAxisAlignmentFromMetadata(int iMetadata)
    {
    	return ( iMetadata >> 2 );
    }
    
    public void setAxisAlignmentBasedOnFacing(World world, int i, int j, int k, int iFacing)
    {
    	int iAxis;
    	
    	switch ( iFacing )
    	{
    		case 0:
    		case 1:
    			
    			iAxis = 0;
    			
    			break;
    			
    		case 2:
    		case 3:
    			
    			iAxis = 1;
    			
    			break;
    			
    		default:
    			
    			iAxis = 2;
    			
    			break;    			
    	}
    	
    	int iMetaData = world.getBlockMetadata( i, j, k ) & 3; // filter out any old alignment
    	
    	iMetaData |= ( iAxis << 2 );
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetaData );
    }
    
    public int setAxisAlignmentInMetadataBasedOnFacing(int iMetadata, int iFacing)
    {
    	int iAxis;
    	
    	switch ( iFacing )
    	{
    		case 0:
    		case 1:
    			
    			iAxis = 0;
    			
    			break;
    			
    		case 2:
    		case 3:
    			
    			iAxis = 1;
    			
    			break;
    			
    		default:
    			
    			iAxis = 2;
    			
    			break;    			
    	}
    	
    	iMetadata &= 3; // filter out any old alignment
    	
    	iMetadata |= ( iAxis << 2 );
    	
        return iMetadata;
    }
    
    public int getPowerLevel(IBlockAccess iBlockAccess, int i, int j, int k)
    {
    	return getPowerLevelFromMetadata(iBlockAccess.getBlockMetadata(i, j, k));
    }
    
    public int getPowerLevelFromMetadata(int iMetadata)
    {
    	return iMetadata & 3;
    }
    
    public void setPowerLevel(World world, int i, int j, int k, int iPowerLevel)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k );
    	
    	iMetadata = setPowerLevelInMetadata(iMetadata, iPowerLevel);
    	
        world.setBlockMetadataWithNotifyNoClient( i, j, k, iMetadata );
    }
    
    public int setPowerLevelInMetadata(int iMetadata, int iPowerLevel)
    {
    	iPowerLevel &= 3;
    	
    	iMetadata &= 12; // filter out any old power
    	
    	iMetadata |= iPowerLevel;
    	
    	return iMetadata;
    }
    
    public void setPowerLevelWithoutNotify(World world, int i, int j, int k, int iPowerLevel)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k );
    	
    	iMetadata = setPowerLevelInMetadata(iMetadata, iPowerLevel);
    	
        world.setBlockMetadata( i, j, k, iMetadata );
    }
    
    public boolean isAxleOrientedTowardsFacing(IBlockAccess iBlockAccess, int i, int j, int k, int iFacing)
    {
    	int iAxis = getAxisAlignment(iBlockAccess, i, j, k);
    	
    	switch ( iAxis )
    	{
    		case 0:
    			
    			if ( iFacing == 0 || iFacing == 1 )
    			{
    				return true;
    			}
    			
    			break;
    			
    		case 1:
    			
    			if ( iFacing == 2 || iFacing == 3 )
    			{
    				return true;
    			}
    			
    			break;
    			
    		default: // 2
    			
    			if ( iFacing == 4 || iFacing == 5 )
    			{
    				return true;
    			}
    			
    			break;
    	}
    		
    	return false;
    }
    
	public void breakAxle(World world, int i, int j, int k)
	{
		if ( world.getBlockId( i, j, k ) == blockID )
		{
			dropComponentItemsOnBadBreak(world, i, j, k, world.getBlockMetadata(i, j, k), 1F);
			
	        world.playAuxSFX(BTWEffectManager.MECHANICAL_DEVICE_EXPLODE_EFFECT_ID, i, j, k, 0 );
	        
			world.setBlockWithNotify( i, j, k, 0 );
		}
	}

	protected void validatePowerLevel(World world, int i, int j, int k)
	{
		int iCurrentPower = getPowerLevel(world, i, j, k);
		int iAxis = getAxisAlignment(world, i, j, k);

		int iMaxNeighborPower = 0;
		int iGreaterPowerNeighbors = 0;
		
		for ( int iTempSourceIndex = 0; iTempSourceIndex < 2; iTempSourceIndex++ )
		{
			int iTempFacing = axleFacingsForAlignment[iAxis][iTempSourceIndex];
			BlockPos tempSourcePos = new BlockPos( i, j, k, iTempFacing );
			                                 
			int iTempBlockID = world.getBlockId(tempSourcePos.x, tempSourcePos.y, tempSourcePos.z);
			
			if ( iTempBlockID != 0 )
			{
				Block tempBlock = Block.blocksList[iTempBlockID];
				
				int iTempPowerLevel = tempBlock.getMechanicalPowerLevelProvidedToAxleAtFacing(
						world, tempSourcePos.x, tempSourcePos.y, tempSourcePos.z, Block.getOppositeFacing(iTempFacing));
				
				if ( iTempPowerLevel > iMaxNeighborPower )
				{
					iMaxNeighborPower = iTempPowerLevel;
				}
				
				if ( iTempPowerLevel > iCurrentPower )
				{
					iGreaterPowerNeighbors++;
				}
			}			
		}
		
		if ( iGreaterPowerNeighbors >= 2 )
		{
			// we're receiving power from more than one direction at once
			
			breakAxle(world, i, j, k);
			
			return;
		}

		int iNewPower = iCurrentPower;
		
		if ( iMaxNeighborPower > iCurrentPower )
		{
			if ( iMaxNeighborPower == 1 )
			{
				// the power has overextended.  Break this axle.
				
				breakAxle(world, i, j, k);
				
				return;
			}
			
			iNewPower = iMaxNeighborPower - 1;
		}
		else
		{
			iNewPower = 0;
		}

		if ( iNewPower != iCurrentPower )
		{
			setPowerLevel(world, i, j, k, iNewPower);
		}
	}
	
    private void emitAxleParticles(World world, int i, int j, int k, Random random)
    {
        for ( int counter = 0; counter < 2; counter++ )
        {
            float smokeX = (float)i + random.nextFloat();
            float smokeY = (float)j + random.nextFloat() * 0.5F + 0.625F;
            float smokeZ = (float)k + random.nextFloat();
            
            world.spawnParticle( "smoke", smokeX, smokeY, smokeZ, 0.0D, 0.0D, 0.0D );
        }
    }
    
    public void overpower(World world, int i, int j, int k)
    {
		int iAxis = getAxisAlignment(world, i, j, k);
		
		switch( iAxis )
		{
    		case 0:

    			overpowerBlockToFacing(world, i, j, k, iAxis, 0);
    			overpowerBlockToFacing(world, i, j, k, iAxis, 1);
    			
    			break;
    			
    		case 1:
    			
    			overpowerBlockToFacing(world, i, j, k, iAxis, 2);
    			overpowerBlockToFacing(world, i, j, k, iAxis, 3);
    			
    			break;
    			
    		default: // 2
    			
    			overpowerBlockToFacing(world, i, j, k, iAxis, 4);
    			overpowerBlockToFacing(world, i, j, k, iAxis, 5);
    			
    			break;
		}		
    }
    
    private void overpowerBlockToFacing(World world, int i, int j, int k, int iSourceAxis, int iFacing)
    {
    	BlockPos targetPos = new BlockPos( i, j, k );
    	
    	targetPos.addFacingAsOffset(iFacing);
    	
		int iTempBlockID = world.getBlockId(targetPos.x,
											targetPos.y, targetPos.z);
		
		if ( iTempBlockID == BTWBlocks.axle.blockID || iTempBlockID == BTWBlocks.axlePowerSource.blockID )
		{
			int iTempAxis = getAxisAlignment(world, targetPos.x,
											 targetPos.y, targetPos.z);
			
			if ( iTempAxis == iSourceAxis )
			{
				overpowerBlockToFacing(world, targetPos.x, targetPos.y, targetPos.z, iSourceAxis, iFacing);
			}
		}
		else if ( Block.blocksList[iTempBlockID] instanceof MechanicalBlock)
		{
			MechanicalBlock mechDevice = (MechanicalBlock)(Block.blocksList[iTempBlockID]);
			
			if ( mechDevice.canInputAxlePowerToFacing(world, targetPos.x,
													  targetPos.y, targetPos.z, Block.getOppositeFacing(iFacing)) )
			{				
				mechDevice.overpower(world, targetPos.x, targetPos.y, targetPos.z);
			}
		}
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    public Icon iconSide;
    @Environment(EnvType.CLIENT)
    public Icon iconSideOn;
    @Environment(EnvType.CLIENT)
    public Icon iconSideOnOverpowered;

    @Environment(EnvType.CLIENT)
    public boolean isPowerOnForCurrentRender; // temporary state variable assigned as each block is rendered
    @Environment(EnvType.CLIENT)
    public boolean isOverpoweredForCurrentRender; // temporary state variable assigned as each block is rendered

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "fcBlockAxle_end" );

		iconSide = register.registerIcon("fcBlockAxle_side");
		iconSideOn = register.registerIcon("fcBlockAxle_side_on");
		iconSideOnOverpowered = register.registerIcon("fcBlockAxle_side_on_fast");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	// used by item render
    	
		if ( iSide != 2 && iSide != 3 )
		{
			return iconSide;
		}
		
		return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
	{
		int iAxisAlignment = getAxisAlignment(blockAccess, i, j, k);
		
    	if ( iAxisAlignment == 0 )
    	{
    		if ( iSide >= 2 )
    		{
    			return getAxleSideTextureForOnState(isPowerOnForCurrentRender);
    		}
    	}
    	else if ( iAxisAlignment == 1 )
    	{
    		if ( iSide != 2 && iSide != 3 )
    		{
    			return getAxleSideTextureForOnState(isPowerOnForCurrentRender);
    		}
    	}
    	else if ( iSide < 4 )
		{
			return getAxleSideTextureForOnState(isPowerOnForCurrentRender);
		}
    	
    	return blockIcon;
	}

    @Environment(EnvType.CLIENT)
    public Icon getAxleSideTextureForOnState(boolean bIsOn)
    {
    	if ( bIsOn )
    	{
    		return iconSideOn;
    	}
    	
		return iconSide;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random random )
    {
    	if ( clientCheckIfPowered(world, i, j, k) )
    	{
    		emitAxleParticles(world, i, j, k, random);
    		
	        if ( random.nextInt( 200 ) == 0 )
	        {
	            world.playSound((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
	        		"random.chestopen", 0.075F, random.nextFloat() * 0.1F + 0.5F);
	        }
    	}
    }

    @Environment(EnvType.CLIENT)
    public boolean clientCheckIfPowered(IBlockAccess blockAccess, int i, int j, int k)
	{
		int iCurrentPower = getPowerLevel(blockAccess, i, j, k);
		int iAxis = getAxisAlignment(blockAccess, i, j, k);

		for ( int iTempFacingIndex = 0; iTempFacingIndex < 2; iTempFacingIndex++ )
		{
			BlockPos targetPos = new BlockPos( i, j, k );
			
			int iFacingOfCheck = axleFacingsForAlignment[iAxis][iTempFacingIndex];
			
			for ( int iTempDistance = 1; iTempDistance <= 3; iTempDistance++ )
			{
				targetPos.addFacingAsOffset(iFacingOfCheck);
				
				int iTempBlockID = blockAccess.getBlockId(targetPos.x,
														  targetPos.y, targetPos.z);
				
				if ( iTempBlockID == blockID && getAxisAlignment(blockAccess, targetPos.x,
																 targetPos.y, targetPos.z) == iAxis )
				{
					// we've found another aligned axle, just continue on our merry way
				}
				else if ( iTempBlockID == BTWBlocks.axlePowerSource.blockID && getAxisAlignment(blockAccess, targetPos.x,
																								targetPos.y, targetPos.z) == iAxis )
				{
					return true;
				}
				else
				{
					if ( MechPowerUtils.isPoweredGearBox(blockAccess,
														 targetPos.x, targetPos.y, targetPos.z) )
					{
						// we've found a powered Gear Box hooked up to this axle, 
						// so the axle should be considered to be on
						
						return true;
					}
					
					break;
				}
			}
		}
		
		return false;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
    	BlockPos myPos = new BlockPos(
				iNeighborI, iNeighborJ, iNeighborK, getOppositeFacing(iSide) );

    	if ( isAxleOrientedTowardsFacing(blockAccess, myPos.x, myPos.y, myPos.z, iSide) )
    	{
    		int iNeighborBlockID = blockAccess.getBlockId( iNeighborI, iNeighborJ, iNeighborK );
    		
    		if ( iNeighborBlockID == blockID )
    		{
    			if (getAxisAlignment(blockAccess, myPos.x, myPos.y, myPos.z) ==
					getAxisAlignment(blockAccess, iNeighborI, iNeighborJ, iNeighborK) )
    			{
    				return false;
    			}
    		}
    		else
    		{
    			return RenderUtils.shouldRenderNeighborFullFaceSide(blockAccess,
																	iNeighborI, iNeighborJ, iNeighborK, iSide);
    		}
    	}
    	
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void clientBreakBlock(World world, int i, int j, int k, int iBlockID, int iMetadata)
	{
        world.markBlockRangeForRenderUpdate( i - 3, j - 3, k - 3, i + 3, j + 3, k + 3);
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void clientBlockAdded(World world, int i, int j, int k)
	{
        world.markBlockRangeForRenderUpdate( i - 3, j - 3, k - 3, i + 3, j + 3, k + 3);
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderer.blockAccess;
    	
    	int iAlignment = getAxisAlignment(blockAccess, i, j, k);
    	
    	if ( iAlignment == 0 )
    	{
    		renderer.setUVRotateEast(1);
    		renderer.setUVRotateWest(1);
    		renderer.setUVRotateSouth(1);
    		renderer.setUVRotateNorth(1);
    		renderer.setUVRotateTop(0);
    		renderer.setUVRotateBottom(0);
    	}
    	else if ( iAlignment == 1 )
    	{
    		renderer.setUVRotateEast(0);
    		renderer.setUVRotateWest(0);
    		renderer.setUVRotateSouth(0);
    		renderer.setUVRotateNorth(3);
    		renderer.setUVRotateTop(2);
    		renderer.setUVRotateBottom(2);
    	}
    	else // 2
    	{
    		renderer.setUVRotateEast(0);
    		renderer.setUVRotateWest(3);
    		renderer.setUVRotateSouth(0);
    		renderer.setUVRotateNorth(0);
    		renderer.setUVRotateTop(3);
    		renderer.setUVRotateBottom(0);
    	}
    	
    	if ( clientCheckIfPowered(blockAccess, i, j, k) )
    	{
			isPowerOnForCurrentRender = true;
    	}
    	else
    	{
			isPowerOnForCurrentRender = false;
    	}

        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	renderer.renderStandardBlock( this, i, j, k );
    	
		renderer.clearUVRotation();
		
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderer, int iItemDamage, float fBrightness)
    {
    	renderer.setRenderBounds(getBlockBoundsFromPoolForItemRender(iItemDamage));
    	
		renderer.setUVRotateEast(0);
		renderer.setUVRotateWest(0);
		renderer.setUVRotateSouth(0);
		renderer.setUVRotateNorth(3);
		renderer.setUVRotateTop(2);
		renderer.setUVRotateBottom(2);
		
        RenderUtils.renderInvBlockWithMetadata(renderer, this,
                                               -0.5F, -0.5F, -0.5F, iItemDamage);
        
        renderer.clearUVRotation();
    }
}
