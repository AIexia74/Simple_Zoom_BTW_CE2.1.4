// FCMOD

package btw.block.blocks;

import java.util.Random;

import btw.block.BTWBlocks;
import btw.block.MechanicalBlock;
import btw.client.render.util.RenderUtils;
import btw.block.util.MechPowerUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11; // client only

public abstract class VesselBlock extends BlockContainer
	implements MechanicalBlock
{
	private static final int TICK_RATE = 1;
	
	public static final double COLLISION_BOX_HEIGHT = 1F;
	
	public static final float MODEL_HEIGHT = 1F;
	public static final float MODEL_WIDTH = (1F - (2F / 16F ) );
	public static final float MODEL_HALF_WIDTH = MODEL_WIDTH / 2F;
	public static final float MODEL_BAND_HEIGHT = (12F / 16F );
	public static final float MODEL_BAND_HALF_HEIGHT = MODEL_BAND_HEIGHT / 2F;
	
    public VesselBlock(int iBlockID, Material material )
    {
        super( iBlockID, material );        
    }
    
	@Override
    public int tickRate( World world )
    {
    	return TICK_RATE;
    }
    
	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        super.onBlockAdded( world, i, j, k );
        
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
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
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k )
    {
    	return AxisAlignedBB.getAABBPool().getAABB((double)i, (double)j, (double)k,
												   (double)(i + 1), (double)j + COLLISION_BOX_HEIGHT, (double)k + 1);
    }
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
		// update mechanical power state
		
		boolean bWasPowered = getMechanicallyPoweredFlag(world, i, j, k);
		boolean bIsPowered = false;
		int iPoweredFromFacing = 0;
		
    	for ( int iFacing = 2; iFacing <= 5; iFacing++ )
    	{
			if (MechPowerUtils.isBlockPoweredByAxleToSide(world, i, j, k, iFacing) ||
				MechPowerUtils.isBlockPoweredByHandCrankToSide(world, i, j, k, iFacing) )
			{
				bIsPowered = true;
				iPoweredFromFacing = iFacing;
				
				breakPowerSourceThatOpposePoweredFacing(world, i, j, k, iPoweredFromFacing);
			}
    	}
    	
    	if ( bWasPowered != bIsPowered )
    	{
	        world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
	    		"step.gravel", 
	    		2.0F + ( rand.nextFloat() * 0.1F ),		// volume 
	    		0.5F + ( rand.nextFloat() * 0.1F ) );	// pitch
	        
    		setMechanicallyPoweredFlag(world, i, j, k, bIsPowered);
    		
    		if ( !bIsPowered )
    		{
    			// unpowered blocks face upwards
    			
    			setTiltFacing(world, i, j, k, 0);
    		}
    		else
    		{
    			setFacingBasedOnPoweredFromFacing(world, i, j, k, iPoweredFromFacing);
    		}
    	}    	
    }
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
		if ( !world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
		{
			world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
		}
    }
    
	@Override
    public int getFacing(IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iFacing = 1;
    	
    	if ( getMechanicallyPoweredFlag(blockAccess, i, j, k) )
    	{
        	iFacing = getTiltFacing(blockAccess, i, j, k);
    	}
    	
    	return iFacing;    	
    }
    
	@Override
    public void setFacing(World world, int i, int j, int k, int iFacing)
    {
    }

	@Override
	public int getFacing(int iMetadata)
	{
		return 0;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		return iMetadata;
	}
	
    //------------- FCIBlockMechanical -------------//
    
	@Override
    public boolean canOutputMechanicalPower()
    {
    	return false;
    }

	@Override
    public boolean canInputMechanicalPower()
    {
    	return true;
    }

	@Override
    public boolean isInputtingMechanicalPower(World world, int i, int j, int k)
    {
    	return MechPowerUtils.isBlockPoweredByAxle(world, i, j, k, this);
    }    

	@Override
	public boolean canInputAxlePowerToFacing(World world, int i, int j, int k, int iFacing)
	{
		return iFacing >= 2;
	}

	@Override
    public boolean isOutputtingMechanicalPower(World world, int i, int j, int k)
    {
    	return false;
    }
    
	@Override
	public void overpower(World world, int i, int j, int k)
	{
	}
	
    //------------- Class Specific Methods -------------//

	public int getTiltFacing(IBlockAccess iBlockAccess, int i, int j, int k)
	{
		return ( iBlockAccess.getBlockMetadata( i, j, k ) & 3 ) + 2;
	}
	
	public void setTiltFacing(World world, int i, int j, int k, int iFacing)
	{
		int iFlatFacing = iFacing - 2;
		
		if ( iFlatFacing < 0 )
		{
			iFlatFacing = 0;
		}
		
		int iMetaData = world.getBlockMetadata( i, j, k ) & (~3); // filter out old on state
    	
		iMetaData |= ( iFlatFacing & 3 );
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetaData );
        
		world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        
	}

	public boolean getMechanicallyPoweredFlag(IBlockAccess iBlockAccess, int i, int j, int k)
	{
		return ( iBlockAccess.getBlockMetadata( i, j, k ) & 4 ) > 0;
	}
    
	private void setMechanicallyPoweredFlag(World world, int i, int j, int k, boolean bFlag)
	{
		int iMetaData = world.getBlockMetadata( i, j, k ) & (~4); // filter out old on state
    	
		if ( bFlag )
		{
			iMetaData |= 4;
		}
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetaData );
	}
	
	private void setFacingBasedOnPoweredFromFacing(World world, int i, int j, int k, int iPoweredFromFacing)
	{
		int iNewFacing = Block.rotateFacingAroundY(iPoweredFromFacing, false);
		
		setTiltFacing(world, i, j, k, iNewFacing);
	}
	
	private void breakPowerSourceThatOpposePoweredFacing(World world, int i, int j, int k, int iPoweredFromFacing)
	{
		// break other axles that don't "fit" with the one that's primarily powering it
		
		int iOppositePoweredFromFacing = Block.getOppositeFacing(iPoweredFromFacing);
		
    	for ( int iFacing = 2; iFacing <= 5; iFacing++ )
    	{
    		if ( iFacing != iPoweredFromFacing )
    		{
        		boolean bShouldBreak = false;
            	
	    		if ( iFacing == iOppositePoweredFromFacing  )
	    		{
	    			if ( MechPowerUtils.isBlockPoweredByAxleToSide(world, i, j, k, iFacing) )
	    			{
	    				bShouldBreak = true;
	    			}
	    		}
	    		else if ( MechPowerUtils.doesBlockHaveFacingAxleToSide(world, i, j, k, iFacing) )
	    		{
	    			bShouldBreak = true;
	    		}
	    		
	    		if ( bShouldBreak )
	    		{
					BlockPos tempPos = new BlockPos( i, j, k );
					
					tempPos.addFacingAsOffset(iFacing);
					
					((AxleBlock) BTWBlocks.axle).breakAxle(world, tempPos.x, tempPos.y, tempPos.z);
	    		}
	    		
	        	// break powered hand cranks
	        	
	    		if ( MechPowerUtils.isBlockPoweredByHandCrankToSide(world, i, j, k, iFacing) )
	    		{
					BlockPos tempPos = new BlockPos( i, j, k );
					
					tempPos.addFacingAsOffset(iFacing);
					
					((HandCrankBlock) BTWBlocks.handCrank).breakCrankWithDrop(world, tempPos.x, tempPos.y, tempPos.z);
	    		}
    		}    		
    	}    	
	}

	public boolean isOpenSideBlocked(World world, int i, int j, int k)
	{
		int iFacing = getFacing(world, i, j, k);
		
		BlockPos targetPos = new BlockPos( i, j, k, iFacing );
		
		return world.isBlockNormalCube(targetPos.x, targetPos.y, targetPos.z);
	}

	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    protected Icon[] iconWideBandBySideArray = new Icon[6];
    @Environment(EnvType.CLIENT)
    protected Icon[] iconCenterColumnBySideArray = new Icon[6];
    @Environment(EnvType.CLIENT)
    protected Icon[] iconInteriorBySideArray = new Icon[6];

    @Environment(EnvType.CLIENT)
    private boolean renderingInterior = false; // temporary state variable used during rendering
    @Environment(EnvType.CLIENT)
    private boolean renderingWideBand = false; // temporary state variable used during rendering

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if (renderingInterior)
		{
			return iconInteriorBySideArray[iSide];
		}
		else if (renderingWideBand)
		{
			return iconWideBandBySideArray[iSide];
		}
		else
		{
			return iconCenterColumnBySideArray[iSide];
		}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
		int iTextureSide = iSide;
		
		if ( getMechanicallyPoweredFlag(blockAccess, i, j, k) )
		{
			int iFacing = getTiltFacing(blockAccess, i, j, k);
			
			if ( iFacing == iSide )
			{
	    		iTextureSide = 1;
			}
			else if ( iSide == Block.getOppositeFacing(iFacing) )
			{
	    		iTextureSide = 0;
			}
			else
			{
	    		iTextureSide = 2;
			}
		}		
		
		return getIcon( iTextureSide, 0 );
    }

    @Override
    @Environment(EnvType.CLIENT)
    abstract public void registerIcons( IconRegister register );

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random random )
    {
		if ( !getMechanicallyPoweredFlag(world, i, j, k) )
		{
			int iBlockUnderID  = world.getBlockId( i, j - 1, k );
			
	    	if ( iBlockUnderID == Block.fire.blockID || iBlockUnderID == BTWBlocks.stokedFire.blockID  )
	        {
	            for ( int counter = 0; counter < 1; counter++ )
	            {
	                float smokeX = (float)i + random.nextFloat();
	                float smokeY = (float)j + random.nextFloat() * 0.5F + 1.0F;
	                float smokeZ = (float)k + random.nextFloat();
	                
	                world.spawnParticle( "fcwhitesmoke", smokeX, smokeY, smokeZ, 0.0D, 0.0D, 0.0D );
	            }
	        }
		}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderBlocks.blockAccess;
    	
    	int iFacing = getFacing(blockAccess, i, j, k);

		renderingInterior = false;
    	
    	// render middle band

		renderingWideBand = true;
    	
    	RenderUtils.renderBlockWithBoundsAndTextureRotation(renderBlocks, this, i, j, k, iFacing,
															0F, 0.5F - MODEL_BAND_HALF_HEIGHT, 0F,
															1F, 0.5F + MODEL_BAND_HALF_HEIGHT, 1F);

    	// render center column (includes top and bottom "lip")

		renderingWideBand = false;
    	
    	RenderUtils.renderBlockWithBoundsAndTextureRotation(renderBlocks, this, i, j, k, iFacing,
															0.5F - MODEL_HALF_WIDTH, 0.0F, 0.5F - MODEL_HALF_WIDTH,
															0.5F + MODEL_HALF_WIDTH, MODEL_HEIGHT, 0.5F + MODEL_HALF_WIDTH);
    	
        // render the interior of the bin

		renderingInterior = true;
        
        Tessellator tesselator = Tessellator.instance;

        RenderUtils.setRenderBoundsToBlockFacing(renderBlocks, iFacing,
												 0.5F - MODEL_HALF_WIDTH, 0.0F, 0.5F - MODEL_HALF_WIDTH,
												 0.5F + MODEL_HALF_WIDTH, MODEL_HEIGHT, 0.5F + MODEL_HALF_WIDTH);
        
        RenderUtils.setTextureRotationBasedOnBlockFacing(renderBlocks, iFacing);
        
        tesselator.setBrightness( getMixedBrightnessForBlock( blockAccess, i, j, k ) );
        
        final float fInteriorBrightnessMultiplier = 0.66F;
        
        int iColorMultiplier = colorMultiplier( blockAccess, i, j, k );
        
        float iColorRed = (float)( iColorMultiplier >> 16 & 255 ) / 255.0F;
        float iColorGreen = (float)( iColorMultiplier >> 8 & 255 ) / 255.0F;
        float iColorBlue = (float)( iColorMultiplier & 255 ) / 255.0F;

        tesselator.setColorOpaque_F( fInteriorBrightnessMultiplier * iColorRed, fInteriorBrightnessMultiplier * iColorGreen, fInteriorBrightnessMultiplier * iColorBlue );
        
        final double dInteriorOffset = ( 4D / 16D ) - 0.001D; // slight extra bit to avoid texture glitches around edges
        
        renderBlocks.renderFaceXPos( this, (double)i - 1.0D + dInteriorOffset, (double)j, (double)k, getBlockTexture( blockAccess, i, j, k, 4 ) );        
        renderBlocks.renderFaceXNeg( this, (double)i + 1.0F - dInteriorOffset, (double)j, (double)k, getBlockTexture( blockAccess, i, j, k, 5 ) );
        renderBlocks.renderFaceZPos( this, (double)i, (double)j, (double)k - 1.0F + dInteriorOffset, getBlockTexture( blockAccess, i, j, k, 2 ) );
        renderBlocks.renderFaceZNeg( this, (double)i, (double)j, (double)k + 1.0F - dInteriorOffset, getBlockTexture( blockAccess, i, j, k, 3 ) );
        
        renderBlocks.renderFaceYPos( this, (double)i, (double)((float)j - 1.0F + dInteriorOffset ), (double)k, getBlockTexture( blockAccess, i, j, k, 0 ) );        
        renderBlocks.renderFaceYNeg( this, (double)i, (double)((float)j + 1.0F - dInteriorOffset ), (double)k, getBlockTexture( blockAccess, i, j, k, 1 ) );        
    	
		renderBlocks.clearUVRotation();
		
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
		renderingInterior = false;
    	
    	// render middle band

		renderingWideBand = true;
    	
    	renderBlocks.setRenderBounds(
				0F, 0.5F - MODEL_BAND_HALF_HEIGHT, 0F,
				1F, 0.5F + MODEL_BAND_HALF_HEIGHT, 1F);

        RenderUtils.renderInvBlockWithMetadata(renderBlocks, this, -0.5F, -0.5F, -0.5F, 0);
        
    	// render center column (includes top and bottom "lip")

		renderingWideBand = false;
    	
    	renderBlocks.setRenderBounds(
				0.5F - MODEL_HALF_WIDTH, 0.0F, 0.5F - MODEL_HALF_WIDTH,
				0.5F + MODEL_HALF_WIDTH, MODEL_HEIGHT, 0.5F + MODEL_HALF_WIDTH);
    	
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, this, -0.5F, -0.5F, -0.5F, 0);
        
    	// render interior

		renderingInterior = true;
    	
    	renderBlocks.setRenderBounds(
				0.5F - MODEL_HALF_WIDTH, 0.0F, 0.5F - MODEL_HALF_WIDTH,
				0.5F + MODEL_HALF_WIDTH, MODEL_HEIGHT, 0.5F + MODEL_HALF_WIDTH);
        
        Tessellator tessellator = Tessellator.instance;

        GL11.glTranslatef( -0.5F, -0.5F, -0.5F );
        
        final double dInteriorOffset = ( 4D / 16D ) - 0.001D; // slight extra bit to avoid texture glitches around edges
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderBlocks.renderFaceYPos(this, 0.0D, -1F + dInteriorOffset, 0.0D, getIcon( 0, 0 ) );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderBlocks.renderFaceZNeg(this, 0.0D, 0.0D, 1.0F - dInteriorOffset, getIcon( 3, 0 ) );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderBlocks.renderFaceZPos(this, 0.0D, 0.0D, -1.0F + dInteriorOffset, getIcon( 2, 0 ) );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderBlocks.renderFaceXNeg(this, 1.0F - dInteriorOffset, 0.0D, 0.0D, getIcon( 5, 0 ) );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXPos(this, -1.0D + dInteriorOffset, 0.0D, 0.0D, getIcon( 4, 0 ) );
        tessellator.draw();
        
        GL11.glTranslatef( 0.5F, 0.5F, 0.5F );        
    }    
}