// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class LensBlock extends Block
{
	public static final int LENS_MAX_RANGE = 128;
	
    private final static int LENS_TICK_RATE = 1;
    
    private final static float MIN_TRIGGER_LIGHT_VALUE = 12;
    
    public LensBlock(int iBlockID )
    {
        super( iBlockID, Material.iron );
        
        setHardness( 3.5F );
        
        setTickRandomly( true );        
        
        setStepSound( Block.soundStoneFootstep );
        
        setUnlocalizedName( "fcBlockLens" );
        
        setCreativeTab( CreativeTabs.tabRedstone );
    }
    
	@Override
    public int tickRate( World world )
    {
        return LENS_TICK_RATE;
    }    
    
	@Override
    public void onBlockAdded( World world, int i, int j, int k ) 
	{
        world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );		
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
    public void onPostBlockPlaced( World world, int i, int j, int k, int iMetadata ) 
    {    	
		setupBeam(world, i, j, k);
    }	
    
	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
    	removeBeam(world, i, j, k);
    	
        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
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
    	int iFacing = getFacing(world, i, j, k);
    	
		boolean bIsLightDetector = isDirectlyFacingBlockDetector(world, i, j, k);
		
		if ( !bIsLightDetector )
		{
	    	boolean bLightOn = checkForInputLight(world, i, j, k);
	    	
	    	if (isLit(world, i, j, k) != bLightOn )
	    	{
	    		setLit(world, i, j, k, bLightOn);
	    		
	    		if ( bLightOn )
	    		{
	    			turnBeamOn(world, i, j, k);
	    		}
	    		else
	    		{
	    			turnBeamOff(world, i, j, k);
	    		}
	    	}
	    	
	    	// check if there is an air-block directly in front, which indicates a block was just removed from there and the
	    	// beam needs to be propagated
	    	
	    	BlockPos targetPos = new BlockPos( i, j, k );
	    	
	    	targetPos.addFacingAsOffset(iFacing);
	    	
	    	if (world.getBlockId(targetPos.x, targetPos.y, targetPos.z) == 0 )
	    	{
	        	DetectorLogicBlock logicBlock = (DetectorLogicBlock)(BTWBlocks.detectorLogic);
	        	
	        	logicBlock.propagateBeamsThroughBlock(world, targetPos.x, targetPos.y, targetPos.z);
	    	}
		}
		else
		{			
	    	BlockPos sourcePos = new BlockPos( i, j, k );
	    	
	    	sourcePos.addFacingAsOffset(Block.getOppositeFacing(iFacing));
	    	
	    	int iSourceLightValue = world.getBlockLightValue(sourcePos.x, sourcePos.y, sourcePos.z);
	    	
	    	boolean bShouldBeOn =  iSourceLightValue >= 8;
	    	
	    	if (isLit(world, i, j, k) != bShouldBeOn )
	    	{
	    		setLit(world, i, j, k, bShouldBeOn);
	    	}
	    	
			// schedule another update immediately to check for light changes
			
	    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );	    	
		}
    }    
    
	@Override
    public int getMobilityFlag()
    {
    	// set the block as immobile so that piston pushing won't cause regeneration of the entire beam
    	
        return 2;
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
	public boolean canRotateOnTurntable(IBlockAccess iBlockAccess, int i, int j, int k)
	{
		return false;
	}
	
	@Override
	public boolean canTransmitRotationHorizontallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}
	
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}
	
	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{		
		int iFacing = getFacing(world, i, j, k);
		
		iFacing = Block.cycleFacing(iFacing, bReverse);

		setFacing(world, i, j, k, iFacing);
		
        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        
        return true;
	}
	
    //------------- Class Specific Methods ------------//
    
    public boolean isLit(IBlockAccess iblockaccess, int i, int j, int k)
    {
    	return ( iblockaccess.getBlockMetadata(i, j, k) & 1 ) > 0;
    }
    
    public void setLit(World world, int i, int j, int k, boolean bOn)
    {
    	if (bOn != isLit(world, i, j, k) )
    	{
	    	int iMetaData = world.getBlockMetadata(i, j, k);
	    	
	    	if ( bOn )
	    	{
	    		iMetaData = iMetaData | 1;	    		
	    	}
	    	else
	    	{
	    		iMetaData = iMetaData & (~1);
	    	}
	    	
	        world.setBlockMetadataWithNotify( i, j, k, iMetaData );
	        
	        world.notifyBlocksOfNeighborChange( i, j, k, blockID );
	        
	        // the following forces a re-render (for texture change)
	        
	        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );    	
        }
    }
    
    private boolean checkForInputLight(World world, int i, int j, int k)
    {
    	int iFacing = getFacing(world, i, j, k);
    	int iTargetFacing = Block.getOppositeFacing(iFacing);
    	
    	BlockPos targetPos = new BlockPos( i, j, k );
    	
    	targetPos.addFacingAsOffset(iTargetFacing);
    	
    	int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
    	
    	if ( iTargetBlockID > 0 )
    	{
    		if (Block.getLightValueForBlock(world, i, j, k, Block.blocksList[iTargetBlockID]) > MIN_TRIGGER_LIGHT_VALUE)
    		{
    			if ( iTargetBlockID != BTWBlocks.glowingDetectorLogic.blockID )
    			{
    				return true;
    			}
    			else
    			{
    				// only power the lens with another lens if it is facing directly into it.
    				
    				DetectorLogicBlock logicBlock = (DetectorLogicBlock)(BTWBlocks.glowingDetectorLogic);
    				
    				if ( logicBlock.shouldBeProjectingToFacing(world, targetPos.x, targetPos.y, targetPos.z, iFacing) )
    				{
    					return true;
    				}
    			}
    		}
    		else if ( iTargetBlockID == blockID )
    		{
    			// Lenses can feed directly into each other
    			
    			if ( isLit(world, targetPos.x, targetPos.y, targetPos.z) )
    			{
    				if (getFacing(world, targetPos.x, targetPos.y, targetPos.z) == iFacing )
    				{
    					return true;
    				}
    			}
    		}
    	}
    	
    	return false;
    }
    
    public void setupBeam(World world, int i, int j, int k)
    {
    	DetectorLogicBlock logicBlock = (DetectorLogicBlock)(BTWBlocks.detectorLogic);
    	
    	logicBlock.createLensBeamFromBlock(world, i, j, k, getFacing(world, i, j, k), LENS_MAX_RANGE);
    }

    private void removeBeam(World world, int i, int j, int k)
    {
    	DetectorLogicBlock logicBlock = (DetectorLogicBlock)(BTWBlocks.detectorLogic);
    	
    	logicBlock.removeLensBeamFromBlock(world, i, j, k, getFacing(world, i, j, k), LENS_MAX_RANGE);
    }
    
	private void turnBeamOn(World world, int i, int j, int k)
	{
    	DetectorLogicBlock logicBlock = (DetectorLogicBlock)(BTWBlocks.detectorLogic);
    	
    	logicBlock.turnBeamOnFromBlock(world, i, j, k, getFacing(world, i, j, k), LENS_MAX_RANGE);
	}

	private void turnBeamOff(World world, int i, int j, int k)
	{
    	DetectorLogicBlock logicBlock = (DetectorLogicBlock)(BTWBlocks.detectorLogic);
    	
    	logicBlock.turnBeamOffFromBlock(world, i, j, k, getFacing(world, i, j, k), LENS_MAX_RANGE);
	}
	
	private boolean isDirectlyFacingBlockDetector(World world, int i, int j, int k)
	{
		int iFacing = getFacing(world, i, j, k);
		
    	BlockPos targetPos = new BlockPos( i, j, k );
    	
    	targetPos.addFacingAsOffset(iFacing);
    	
    	int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
    	
		if ( iTargetBlockID == BTWBlocks.detectorBlock.blockID )
		{
			int iDetectorFacing = ((DetectorBlock)(BTWBlocks.detectorBlock)).getFacing(world, targetPos.x, targetPos.y, targetPos.z);
			
			if ( iDetectorFacing == Block.getOppositeFacing(iFacing) )
			{
				return true;
			}
		}
		
		return false;
	}
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconOutput;
    @Environment(EnvType.CLIENT)
    private Icon iconInput;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconOutput = register.registerIcon("fcBlockLens_output");
		iconInput = register.registerIcon("fcBlockLens_input");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		// item render
		
        if ( iSide == 5 )
        {
            return iconOutput;
        } 
        else if ( iSide == 4 )
        {
            return iconInput;
        } 
        
        return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
        int iFacing = getFacing(blockAccess, i, j, k);
        
        if ( iSide == iFacing )
        {
            return iconOutput;
        }
        else if ( iSide == Block.getOppositeFacing(iFacing) )
        {
            return iconInput;
        }
        
        return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderer.blockAccess;
    	
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	blockAccess, i, j, k) );
        
    	if ( !isLit(blockAccess, i, j, k) )
    	{
            return renderer.renderStandardBlock( this, i, j, k );
    	}
    	else
    	{
        	return renderLitLens(renderer, blockAccess, i, j, k, this);
    	}    		
    }

    @Environment(EnvType.CLIENT)
    public boolean renderLitLens
    ( 
		RenderBlocks renderer, 
		IBlockAccess blockAccess, 
		int i, int j, int k, 
		Block block 
	)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	boolean flag = false;
    	int iFacing = getFacing(blockAccess, i, j, k);
    	
        int iColorMultiplier = block.colorMultiplier(blockAccess, i, j, k);
        float f = (float)(iColorMultiplier >> 16 & 0xff) / 255F;
        float f1 = (float)(iColorMultiplier >> 8 & 0xff) / 255F;
        float f2 = (float)(iColorMultiplier & 0xff) / 255F;
        
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * f;
        float f8 = f4 * f1;
        float f9 = f4 * f2;
        
        float f10 = f3 * f;
        float f11 = f5 * f;
        float f12 = f6 * f;
        float f13 = f3 * f1;
        float f14 = f5 * f1;
        float f15 = f6 * f1;
        float f16 = f3 * f2;
        float f17 = f5 * f2;
        float f18 = f6 * f2;
        
        Tessellator tessellator = Tessellator.instance;
        
        // render bottom
        
        if (renderer.getRenderAllFaces() || block.shouldSideBeRendered(blockAccess, i, j - 1, k, 0))
        {
            if ( iFacing == 0 )
            {
            	tessellator.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
            	tessellator.setBrightness( blockAccess.getLightBrightnessForSkyBlocks( i, j, k, 15 ) );
            }
            else
            {
	            tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, i, j - 1, k));
	            tessellator.setColorOpaque_F(f10, f13, f16);
            }
            renderer.renderFaceYNeg(block, i, j, k, block.getBlockTexture(blockAccess, i, j, k, 0));
            flag = true;
        }
        
        // render top
        
        if (renderer.getRenderAllFaces() || block.shouldSideBeRendered(blockAccess, i, j + 1, k, 1))
        {
            if ( iFacing == 1 )
            {
            	tessellator.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
            	tessellator.setBrightness( blockAccess.getLightBrightnessForSkyBlocks( i, j, k, 15 ) );
            }
            else
            {
	            tessellator.setBrightness( block.getMixedBrightnessForBlock(blockAccess, i, j + 1, k) );
	            tessellator.setColorOpaque_F(f7, f8, f9);
            }
            renderer.renderFaceYPos(block, i, j, k, block.getBlockTexture(blockAccess, i, j, k, 1));
            flag = true;
        }
        
        // render east
        
        if (renderer.getRenderAllFaces() || block.shouldSideBeRendered(blockAccess, i, j, k - 1, 2))
        {
            if ( iFacing == 2 )
            {
            	tessellator.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
            	tessellator.setBrightness( blockAccess.getLightBrightnessForSkyBlocks( i, j, k, 15 ) );
            }
            else
            {
	            tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, i, j, k - 1));
	            tessellator.setColorOpaque_F(f11, f14, f17);
            }
            
            renderer.renderFaceZNeg(block, i, j, k, block.getBlockTexture(blockAccess, i, j, k, 2));
            flag = true;
        }
        
        // render west
        
        if (renderer.getRenderAllFaces() || block.shouldSideBeRendered(blockAccess, i, j, k + 1, 3))
        {
            if ( iFacing == 3 )
            {
            	tessellator.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
            	tessellator.setBrightness( blockAccess.getLightBrightnessForSkyBlocks( i, j, k, 15 ) );
            }
            else
            {
	            tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, i, j, k + 1));
	            tessellator.setColorOpaque_F(f11, f14, f17);
            }
            
            renderer.renderFaceZPos(block, i, j, k, block.getBlockTexture(blockAccess, i, j, k, 3));
            flag = true;
        }
        
        // render north
        
        if (renderer.getRenderAllFaces() || block.shouldSideBeRendered(blockAccess, i - 1, j, k, 4))
        {
            if ( iFacing == 4 )
            {
            	tessellator.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
            	tessellator.setBrightness( blockAccess.getLightBrightnessForSkyBlocks( i, j, k, 15 ) );
            }
            else
            {
	            tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, i - 1, j, k));
	            tessellator.setColorOpaque_F(f12, f15, f18);
            }
            renderer.renderFaceXNeg(block, i, j, k, block.getBlockTexture(blockAccess, i, j, k, 4));
            flag = true;
        }
        
        // render south
        
        if (renderer.getRenderAllFaces() || block.shouldSideBeRendered(blockAccess, i + 1, j, k, 5))
        {
            if ( iFacing == 5 )
            {
            	tessellator.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
            	tessellator.setBrightness( blockAccess.getLightBrightnessForSkyBlocks( i, j, k, 15 ) );
            }
            else
            {
	            tessellator.setBrightness( block.getMixedBrightnessForBlock(blockAccess, i + 1, j, k) );
	            tessellator.setColorOpaque_F(f12, f15, f18);
            }
            renderer.renderFaceXPos(block, i, j, k, block.getBlockTexture(blockAccess, i, j, k, 5));
            flag = true;
        }
        
    	return flag;
    }
}
