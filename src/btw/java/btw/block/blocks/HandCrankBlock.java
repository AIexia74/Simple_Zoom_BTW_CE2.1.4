// FCMOD

package btw.block.blocks;

import btw.block.MechanicalBlock;
import btw.client.fx.BTWEffectManager;
import btw.client.render.util.RenderUtils;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import btw.block.util.RayTraceUtils;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class HandCrankBlock extends Block
	implements MechanicalBlock
{
	private static final int HAND_CRANK_TICK_RATE = 3;
	private static final int HAND_CRANK_DELAY_BEFORE_RESET = 15;
	
	private static final double BASE_HEIGHT = (4D / 16D );
	
	private static final double SHAFT_SELECTION_WIDTH = (4D / 16D );
	private static final double SHAFT_SELECTION_HALF_WIDTH = (SHAFT_SELECTION_WIDTH / 2D );
	
	public HandCrankBlock(int iBlockID)
	{
        super( iBlockID, Material.circuits );

        setHardness( 0.5F );
        setPicksEffectiveOn();

        initBlockBounds(0D, 0D, 0D, 1D, 1D, 1D);
        	
        setStepSound( soundWoodFootstep );
        setUnlocalizedName( "fcBlockHandCrank" );
        
        setTickRandomly( true );

        setCreativeTab( CreativeTabs.tabRedstone );
	}

	@Override
    public int tickRate( World world )
    {
    	return HAND_CRANK_TICK_RATE;
    }
    
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool( World world, int i, int j, int k )
    {
        return null;        
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
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
        return canRestAtPosition(world, i, j, k) && super.canPlaceBlockAt(world, i, j, k);
    }
    
	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
        int iMetaData = world.getBlockMetadata( i, j, k );

        if ( iMetaData == 0 )
        {
        	if ( player.getFoodStats().getFoodLevel() > 18 )
        	{
				float exhaustionAmount = 2.0F;
				player.addExhaustion(exhaustionAmount * world.getDifficulty().getHungerIntensiveActionCostMultiplier());
	        	
	            if ( !world.isRemote )
	            {	            
		        	if ( !checkForOverpower(world, i, j, k) )
		        	{
			        	world.setBlockMetadataWithNotify( i, j, k, 1 );
				        
				        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
				        
						world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
								"random.click", 1.0F, 2.0F );
				        
				        world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
				        
				        world.markBlockForUpdate( i, j, k );
		        	}
		        	else
		        	{
		        		breakCrankWithDrop(world, i, j, k);
		    		}
	            }
        	}
        	else
        	{
                if( world.isRemote )
                {
                	player.addChatMessage( this.getUnlocalizedName() + ".playerExhausted" );
                	
                	return false;
                }
        	}
        	
        	return true;
        }
	        
        return false;
    }
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
        int iMetaData = world.getBlockMetadata( i, j, k );

    	if ( iMetaData > 0 )
    	{
    		if ( iMetaData < 7 )
    		{
    			if ( iMetaData <= 6 )
    			{
    				world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
    						"random.click", 1.0F, 2.0F );
    			}
    			
    			if ( iMetaData <= 5 )
    			{
    				world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) + iMetaData );
    			}
    			else
    			{
    				world.scheduleBlockUpdate(i, j, k, blockID, HAND_CRANK_DELAY_BEFORE_RESET);
    			}
    	        
    			// no notify here as it's not an actual state-change, just an internal timer update
            	world.setBlockMetadata( i, j, k, iMetaData + 1 );            	
    		}
    		else
    		{
            	world.setBlockMetadataWithNotify( i, j, k, 0 );
            	
		        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
		        
		        world.markBlockForUpdate( i, j, k ); // mutliplayer update
		        
    	        world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
    		    		"random.click", 0.3F, 0.7F );    	        
    		}
    	}
    }    
    
	@Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
		// check if the hand crank is currently on
		
        int iMetaData = world.getBlockMetadata( i, j, k );

    	if ( iMetaData > 0 )
    	{
			// verify we have a tick already scheduled to prevent jams on chunk load
			
			if ( !world.isUpdateScheduledForBlock(i, j, k, blockID) )
			{
		        world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );        
			}
		}
    }
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {    	
        if ( !canRestAtPosition(world, i, j, k) )
        {
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            
            world.setBlockWithNotify( i, j, k, 0 );
        }
    }
    
    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, 
    	Vec3 startRay, Vec3 endRay )
    {
    	RayTraceUtils rayTrace = new RayTraceUtils(
    		world, i, j, k, startRay, endRay );
    	
    	rayTrace.addBoxWithLocalCoordsToIntersectionList(getBaseBoundsFromPool());
    	
    	rayTrace.addBoxWithLocalCoordsToIntersectionList(getShaftSelectionBoundsFromPool());
    	
    	return rayTrace.getFirstIntersection();
    }
    
    //------------- FCIMechanicalDevice ------------//
    
	@Override
    public boolean canOutputMechanicalPower()
    {
    	return true;
    }

	@Override
    public boolean canInputMechanicalPower()
    {
    	return false;
    }

	@Override
	public boolean canInputAxlePowerToFacing(World world, int i, int j, int k, int iFacing)
	{
		return false;
	}
	
	@Override
    public boolean isInputtingMechanicalPower(World world, int i, int j, int k)
    {
    	return false;
    }

	@Override
    public boolean isOutputtingMechanicalPower(World world, int i, int j, int k)
    {	
    	return world.getBlockMetadata( i, j, k ) > 0;
    }
    
	@Override
	public void overpower(World world, int i, int j, int k)
	{
	}
	
    //------------- Class Specific Methods ------------//
    
	public boolean canRestAtPosition(World world, int i, int j, int k)
	{
        return WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true);
	}
    
    public boolean checkForOverpower(World world, int i, int j, int k)
    {
    	int iNumPotentialDevicesToPower = 0;
    	
    	for ( int iTempFacing = 0; iTempFacing <=5; iTempFacing++ )
    	{
    		if ( iTempFacing == 1 )
    		{
    			continue;
    		}
    		
    		BlockPos tempPos = new BlockPos( i, j, k );
    		
    		tempPos.addFacingAsOffset(iTempFacing);
    		
    		int iTempBlockID = world.getBlockId(tempPos.x, tempPos.y, tempPos.z);
    		
    		Block tempBlock = Block.blocksList[iTempBlockID];
    		
    		if ( tempBlock != null && ( tempBlock instanceof MechanicalBlock) )
    		{
    			MechanicalBlock tempDevice = (MechanicalBlock)tempBlock;
    			
    			if ( tempDevice.canInputMechanicalPower() )
    			{
    				iNumPotentialDevicesToPower++;
    			}
    		}
    	}
    	
    	return ( iNumPotentialDevicesToPower > 1 );
    }
    
    public void breakCrankWithDrop(World world, int i, int j, int k)
    {
    	ItemUtils.ejectSingleItemWithRandomOffset(world, i, j, k, Item.stick.itemID, 0);
    	ItemUtils.ejectSingleItemWithRandomOffset(world, i, j, k, BTWItems.gear.itemID, 0);
		
    	dropItemsIndividually(world, i, j, k, BTWItems.stone.itemID, 12, 0, 0.75F);
    	
        world.playAuxSFX( BTWEffectManager.MECHANICAL_DEVICE_EXPLODE_EFFECT_ID, i, j, k, 0 );
        
		world.setBlockWithNotify( i, j, k, 0 );
    }
    
    protected AxisAlignedBB getBaseBoundsFromPool()
    {
    	return AxisAlignedBB.getAABBPool().getAABB(
                0D, 0D, 0D, 1D, BASE_HEIGHT, 1D);
    }
    
    protected AxisAlignedBB getShaftSelectionBoundsFromPool()
    {
    	return AxisAlignedBB.getAABBPool().getAABB(
                0D, BASE_HEIGHT, 0.5D - SHAFT_SELECTION_HALF_WIDTH,
                1D, 1D, 0.5D + SHAFT_SELECTION_HALF_WIDTH);
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBySideArray = new Icon[6];

    @Environment(EnvType.CLIENT)
    private Icon iconShaft;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "stone" ); // for hit effects

        iconShaft = register.registerIcon("fcBlockHandCrank_shaft");

        iconBySideArray[0] = register.registerIcon("fcBlockHandCrank_bottom");
        iconBySideArray[1] = register.registerIcon("fcBlockHandCrank_top");
        
        Icon sideIcon = register.registerIcon( "fcBlockHandCrank_side" );

        iconBySideArray[2] = sideIcon;
        iconBySideArray[3] = sideIcon;
        iconBySideArray[4] = sideIcon;
        iconBySideArray[5] = sideIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		return iconBySideArray[iSide];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
		if ( iSide != 1 )
		{
			return super.shouldSideBeRendered( blockAccess, i, j, k, iSide );
		}
		
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderBlocks.blockAccess;
    	
        Tessellator tessellator = Tessellator.instance;
        
        // render the base
        
        renderBlocks.setRenderBounds(getBaseBoundsFromPool());
        
        renderBlocks.renderStandardBlock( this, i, j, k );
        
        // render shaft
 
        float fBlockBrightness = getBlockBrightness( blockAccess, i, j, k );
        
        if (this.getLightValue(blockAccess, i, j, k) > 0)
        {
        	fBlockBrightness = 1.0F;
        }
        
        tessellator.setColorOpaque_F( fBlockBrightness, fBlockBrightness, fBlockBrightness );
        
        Icon iTextureID = this.iconShaft;
        
        if ( renderBlocks.hasOverrideBlockTexture() )
        {
        	iTextureID = renderBlocks.getOverrideTexture();
        }
        
        double f4 = (double)iTextureID.getMinU();
        double f5 = (double)iTextureID.getMaxU();
        double f6 = (double)iTextureID.getMinV();
        double f7 = (double)iTextureID.getMaxV();
        
        Vec3 avec3d[] = new Vec3[8];
        
        float f8 = 0.0625F;
        float f9 = 0.0625F;
        float f10 = 0.90F;
        
        avec3d[0] = Vec3.createVectorHelper(-f8, 0.0D, -f9);
        avec3d[1] = Vec3.createVectorHelper(f8, 0.0D, -f9);
        avec3d[2] = Vec3.createVectorHelper(f8, 0.0D, f9);
        avec3d[3] = Vec3.createVectorHelper(-f8, 0.0D, f9);
        avec3d[4] = Vec3.createVectorHelper(-f8, f10, -f9);
        avec3d[5] = Vec3.createVectorHelper(f8, f10, -f9);
        avec3d[6] = Vec3.createVectorHelper(f8, f10, f9);
        avec3d[7] = Vec3.createVectorHelper(-f8, f10, f9);
        
        boolean bIsOn = blockAccess.getBlockMetadata(i, j, k) > 0;
        
        for ( int i2 = 0; i2 < 8; i2++ )
        {
            if ( bIsOn )
            {
                avec3d[i2].zCoord -= 0.0625D;
                avec3d[i2].rotateAroundX(0.35F);
            } 
            else
            {
                avec3d[i2].zCoord += 0.0625D;
                avec3d[i2].rotateAroundX(-0.35F);
            }
            
            avec3d[i2].rotateAroundY(1.570796F);
            
            avec3d[i2].xCoord += (double)i + 0.5D;
            avec3d[i2].yCoord += (float)j + 0.125F;
            avec3d[i2].zCoord += (double)k + 0.5D;
        }

        Vec3 vec3d = null;
        Vec3 vec3d1 = null;
        Vec3 vec3d2 = null;
        Vec3 vec3d3 = null;
        
        for ( int j2 = 0; j2 < 6; j2++ )
        {
            if ( j2 == 0 )
            {
                f4 = (double)iTextureID.getInterpolatedU(7.0D);
                f5 = (double)iTextureID.getInterpolatedU(9.0D);
                f6 = (double)iTextureID.getMinV();
                f7 = iTextureID.getInterpolatedV(2.0D);
            } 
            else if ( j2 == 2 )
            {
                f4 = (double)iTextureID.getInterpolatedU(7.0D);
                f5 = (double)iTextureID.getInterpolatedU(9.0D);
                f6 = (double)iTextureID.getMinV();
                f7 = (double)iTextureID.getMaxV();
            }
            
            if ( j2 == 0 )
            {
                vec3d = avec3d[0];
                vec3d1 = avec3d[1];
                vec3d2 = avec3d[2];
                vec3d3 = avec3d[3];
            } 
            else if ( j2 == 1 )
            {
                vec3d = avec3d[7];
                vec3d1 = avec3d[6];
                vec3d2 = avec3d[5];
                vec3d3 = avec3d[4];
            } 
            else if ( j2 == 2 )
            {
                vec3d = avec3d[1];
                vec3d1 = avec3d[0];
                vec3d2 = avec3d[4];
                vec3d3 = avec3d[5];
            } 
            else if ( j2 == 3 )
            {
                vec3d = avec3d[2];
                vec3d1 = avec3d[1];
                vec3d2 = avec3d[5];
                vec3d3 = avec3d[6];
            } 
            else if ( j2 == 4 )
            {
                vec3d = avec3d[3];
                vec3d1 = avec3d[2];
                vec3d2 = avec3d[6];
                vec3d3 = avec3d[7];
            } 
            else if ( j2 == 5 )
            {
                vec3d = avec3d[0];
                vec3d1 = avec3d[3];
                vec3d2 = avec3d[7];
                vec3d3 = avec3d[4];
            }
            
            tessellator.addVertexWithUV(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, f4, f7);
            tessellator.addVertexWithUV(vec3d1.xCoord, vec3d1.yCoord, vec3d1.zCoord, f5, f7);
            tessellator.addVertexWithUV(vec3d2.xCoord, vec3d2.yCoord, vec3d2.zCoord, f5, f6);
            tessellator.addVertexWithUV(vec3d3.xCoord, vec3d3.yCoord, vec3d3.zCoord, f4, f6);
        }

        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
        // render the base
        
        renderBlocks.setRenderBounds(getBaseBoundsFromPool());
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, this, -0.5F, -0.5F, -0.5F, 0);

        // render the shaft
        
        float fHalfWidth = ( 1.0F / 16.0F );
        
        renderBlocks.setRenderBounds( 
        	0.5F - fHalfWidth, BASE_HEIGHT, 0.5F - fHalfWidth,
    		0.5F + fHalfWidth, 1F, 0.5F + fHalfWidth );
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, iconShaft);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, int i, int j, int k )
	{
		return getShaftSelectionBoundsFromPool().offset(i, j, k);
	}
}