// FCMOD

package btw.block.blocks;

import btw.client.render.util.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class PlanterBlock extends PlanterBlockBase
{
	// non linear types due to legacy code that had an odd system of metadata usage
	
	public static final int TYPE_EMPTY = 0;
	public static final int TYPE_SOIL = 1; // deprecated
	public static final int TYPE_SOIL_FERTILIZED = 2; // deprecated
	public static final int TYPE_SOUL_SAND = 8;
	public static final int TYPE_GRASS_0 = 9; // deprecated
	public static final int TYPE_GRASS_1 = 11; // deprecated
	public static final int TYPE_GRASS_2 = 13; // deprecated
	public static final int TYPE_GRASS_3 = 15; // deprecated
	
    public PlanterBlock(int iBlockID )
    {
        super( iBlockID );  
        
        setUnlocalizedName( "fcBlockPlanter" );     
    }

	@Override
    public int damageDropped( int iMetadata )
    {
		if (iMetadata == TYPE_GRASS_1 || iMetadata == TYPE_GRASS_2 ||
			iMetadata == TYPE_GRASS_3)
		{
			iMetadata = TYPE_GRASS_0;
		}
		
    	return iMetadata;
    }
    
	@Override
    public void updateTick(World world, int i, int j, int k, Random random)
    {
		int iPlanterType = getPlanterType(world, i, j, k);
		
    	if (iPlanterType == TYPE_GRASS_0 || iPlanterType == TYPE_GRASS_1 ||
			iPlanterType == TYPE_GRASS_2 || iPlanterType == TYPE_GRASS_3)
    	{
        	int iOldGrowthState = getGrassGrowthState(world, i, j, k);
        	int iNewGrowthState = 0;
        	
	    	if ( world.isAirBlock( i, j + 1, k ) )
	    	{
	        	// grass planters with nothing in them will eventually sprout flowers or tall grass
	    		  
	    		if ( world.getBlockLightValue(i, j + 1, k) >= 8 )
	    		{
		    		iNewGrowthState = iOldGrowthState;
		    		
		    		if ( true )//random.nextInt( 12 ) == 0 )
		    		{
			    		iNewGrowthState++;
			    		
			    		if ( iNewGrowthState > 3 )
			    		{
			    			iNewGrowthState = 0;
			    			
			    			int iPlantType = random.nextInt( 4 );
			    			
			    			if ( iPlantType == 0 )
			    			{
			    				world.setBlockWithNotify( i, j + 1, k, Block.plantRed.blockID );
			    			}
			    			else if ( iPlantType == 1 )
			    			{
			    				world.setBlockWithNotify( i, j + 1, k, Block.plantYellow.blockID );
			    			}
			    			else
			    			{
			    				world.setBlockAndMetadataWithNotify( i, j + 1, k, Block.tallGrass.blockID, 1 );
			    			}
			    		}
		    		}
	    		}
	    	}
	    	
	    	// Spread grass
	    	
	        if ( world.getBlockLightValue( i, j + 1, k ) >= 9 )
	        {
	            for ( int tempCount = 0; tempCount < 4; tempCount++ )
	            {
	                int tempi = ( i + random.nextInt(3)) - 1;
	                int tempj = ( j + random.nextInt(5)) - 3;
	                int tempk = ( k + random.nextInt(3)) - 1;
	                
	                int iTempBlockAboveID = world.getBlockId( tempi, tempj + 1, tempk );

	                if ( world.getBlockId( tempi, tempj, tempk ) == Block.dirt.blockID && 
                		world.getBlockLightValue( tempi, tempj + 1, tempk ) >= 4 && 
                		Block.lightOpacity[iTempBlockAboveID] <= 2 )
	                {
	                	world.setBlockWithNotify( tempi, tempj, tempk, Block.grass.blockID );
	                }
	            }
	        }
	    	
	    	if ( iNewGrowthState != iOldGrowthState )
	    	{
	    		setGrassGrowthState(world, i, j, k, iNewGrowthState);
	    	}
    	}    	
    }
    
	@Override
	public boolean attemptToApplyFertilizerTo(World world, int i, int j, int k)
	{
		int iPlanterType = getPlanterType(world, i, j, k);

		if (iPlanterType == TYPE_SOIL)
		{
			setPlanterType(world, i, j, k, TYPE_SOIL_FERTILIZED);
			
			return true;
		}
		
		return false;
	}
	
    @Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		if (getPlanterType(blockAccess, i, j, k) == TYPE_EMPTY)
		{
			return false;
    	}
		
		return super.hasLargeCenterHardPointToFacing( blockAccess, i, j, k, iFacing,
			bIgnoreTransparency );		
	}
    
	@Override
    public boolean canDomesticatedCropsGrowOnBlock(World world, int i, int j, int k)
    {
		int iPlanterType = getPlanterType(world, i, j, k);
    	
    	return iPlanterType == TYPE_SOIL || iPlanterType == TYPE_SOIL_FERTILIZED;
    }
    
	@Override
    public boolean canReedsGrowOnBlock(World world, int i, int j, int k)
    {
		int iPlanterType = getPlanterType(world, i, j, k);
		
    	return iPlanterType != TYPE_EMPTY && iPlanterType != TYPE_SOUL_SAND;
    }
    
	@Override
    public boolean canSaplingsGrowOnBlock(World world, int i, int j, int k)
    {
		int iPlanterType = getPlanterType(world, i, j, k);
		
    	return iPlanterType != TYPE_EMPTY && iPlanterType != TYPE_SOUL_SAND;
    }
    
	@Override
    public boolean canWildVegetationGrowOnBlock(World world, int i, int j, int k)
    {
		int iPlanterType = getPlanterType(world, i, j, k);
		
    	return iPlanterType != TYPE_EMPTY && iPlanterType != TYPE_SOUL_SAND;
    }
    
	@Override
    public boolean canNetherWartGrowOnBlock(World world, int i, int j, int k)
    {
		return getPlanterType(world, i, j, k) == TYPE_SOUL_SAND;
    }
    
	@Override
    public boolean canCactusGrowOnBlock(World world, int i, int j, int k)
    {
		int iPlanterType = getPlanterType(world, i, j, k);
		
    	return iPlanterType != TYPE_EMPTY && iPlanterType != TYPE_SOUL_SAND;
    }
    
	@Override
	public boolean isBlockHydratedForPlantGrowthOn(World world, int i, int j, int k)
	{
		int iPlanterType = getPlanterType(world, i, j, k);
		
    	return iPlanterType == TYPE_SOIL || iPlanterType == TYPE_SOIL_FERTILIZED;
	}
	
	@Override
	public boolean isConsideredNeighbouringWaterForReedGrowthOn(World world, int i, int j, int k)
	{
		int iPlanterType = getPlanterType(world, i, j, k);
		
    	return iPlanterType == TYPE_SOIL || iPlanterType == TYPE_SOIL_FERTILIZED ||
			   super.isConsideredNeighbouringWaterForReedGrowthOn(world, i, j, k);
	}
	
	@Override
	public boolean getIsFertilizedForPlantGrowth(World world, int i, int j, int k)
	{
		return getPlanterType(world, i, j, k) == TYPE_SOIL_FERTILIZED;
	}
	
	@Override
	public void notifyOfFullStagePlantGrowthOn(World world, int i, int j, int k, Block plantBlock)
	{
		int iPlanterType = getPlanterType(world, i, j, k);
		
		if (iPlanterType == TYPE_SOIL_FERTILIZED)
		{
			setPlanterType(world, i, j, k, TYPE_SOIL);
		}
	}
	
    //------------- Class Specific Methods ------------//

	public int getPlanterType(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getPlanterTypeFromMetadata(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public void setPlanterType(World world, int i, int j, int k, int iType)
	{
        world.setBlockMetadataWithNotify( i, j, k, iType );
	}
	
	public int getPlanterTypeFromMetadata(int iMetadata)
	{
		return iMetadata;
	}
	
	public int getGrassGrowthState(IBlockAccess blockAccess, int i, int j, int k)
	{
        int iPlanterType = getPlanterType(blockAccess, i, j, k);

        switch ( iPlanterType )
        {
        	case TYPE_GRASS_0:
        		
        		return 0;
        		
        	case TYPE_GRASS_1:
        		
        		return 1;
        		
        	case TYPE_GRASS_2:
        		
        		return 2;
        		
        	case TYPE_GRASS_3:
        		
        		return 3;
        		
    		default:
    			
    			return 0;       		
        }
	}
	
	public void setGrassGrowthState(World world, int i, int j, int k, int iGrowthState)
	{
		int iPlanterType = TYPE_GRASS_0;
		
		if ( iGrowthState == 1 )
		{
			iPlanterType = TYPE_GRASS_1;
		}
		else if ( iGrowthState == 2 )
		{
			iPlanterType = TYPE_GRASS_2;
		}
		else if ( iGrowthState == 3 )
		{
			iPlanterType = TYPE_GRASS_3;
		}
		
		setPlanterType(world, i, j, k, iPlanterType);
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconTopGrass;
    @Environment(EnvType.CLIENT)
    private Icon iconTopSoulSand;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconTopGrass = register.registerIcon("fcBlockPlanter_top_grass");
		iconTopSoulSand = register.registerIcon("fcBlockPlanter_top_soulsand");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		int iPlanterType = getPlanterTypeFromMetadata(iMetadata);
		
		if ( iSide == 1 && iPlanterType != TYPE_EMPTY)
		{
			if (iPlanterType == TYPE_SOIL)
			{
	            return iconTopSoilWet;
			}
			else if (iPlanterType == TYPE_SOUL_SAND)
	        {
	        	return iconTopSoulSand;
	        }
	        else if (iPlanterType == TYPE_SOIL_FERTILIZED)
	        {
	        	return iconTopSoilWetFertilized;
        	}
	        else
	        {
	        	return iconTopGrass;
	        }
		}        
    	
        return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(blockID, 1, TYPE_EMPTY));
        list.add(new ItemStack(blockID, 1, TYPE_SOUL_SAND));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderBlocks.blockAccess;
    	
		int iPlanterType = getPlanterType(blockAccess, i, j, k);

		if (iPlanterType == TYPE_EMPTY)
		{
			return renderEmptyPlanterBlock(renderBlocks, blockAccess, i, j, k, this);
		}
		else
		{
			return renderFilledPlanterBlock(renderBlocks, i, j, k);
		}
    }

    @Environment(EnvType.CLIENT)
    static public boolean renderEmptyPlanterBlock
    ( 
		RenderBlocks renderBlocks, 
		IBlockAccess blockAccess, 
		int i, int j, int k, 
		Block block 
	)
    {
    	// render sides
    	
        renderBlocks.setRenderBounds((0.5F - PLANTER_HALF_WIDTH), 0.0F, (0.5F - PLANTER_HALF_WIDTH),
									 (0.5F - PLANTER_HALF_WIDTH) + 0.125F, 1.0F - PLANTER_BAND_HEIGHT, (0.5F + PLANTER_HALF_WIDTH) - 0.125F);
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
        renderBlocks.setRenderBounds((0.5F - PLANTER_HALF_WIDTH), 0.0F, (0.5F + PLANTER_HALF_WIDTH) - 0.125F,
									 (0.5F + PLANTER_HALF_WIDTH) - 0.125F, 1.0F - PLANTER_BAND_HEIGHT, (0.5F + PLANTER_HALF_WIDTH));
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
        renderBlocks.setRenderBounds((0.5F + PLANTER_HALF_WIDTH) - 0.125F, 0.0F, (0.5F - PLANTER_HALF_WIDTH) + 0.125F,
									 (0.5F + PLANTER_HALF_WIDTH), 1.0F - PLANTER_BAND_HEIGHT, (0.5F + PLANTER_HALF_WIDTH));
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
        renderBlocks.setRenderBounds((0.5F - PLANTER_HALF_WIDTH) + 0.125F, 0.0F, (0.5F - PLANTER_HALF_WIDTH),
									 (0.5F + PLANTER_HALF_WIDTH), 1.0F - PLANTER_BAND_HEIGHT, (0.5F - PLANTER_HALF_WIDTH) + 0.125F);
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
        // render bottom
        
        renderBlocks.setRenderBounds((0.5F - PLANTER_HALF_WIDTH) + 0.125F, 0.0F, (0.5F - PLANTER_HALF_WIDTH) + 0.125F,
									 (0.5F + PLANTER_HALF_WIDTH) - 0.125F, 2.0F / 16F, (0.5F + PLANTER_HALF_WIDTH) - 0.125F);
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
    	// render band around top
    	
        renderBlocks.setRenderBounds(0.0F, 1.0F - PLANTER_BAND_HEIGHT, 0.0F,
									 0.125F, 1.0F, 0.875F );
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
        renderBlocks.setRenderBounds(0.0F, 1.0F - PLANTER_BAND_HEIGHT, 0.875F,
									 0.875F, 1.0F, 1.0F );
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
        renderBlocks.setRenderBounds(0.875F, 1.0F - PLANTER_BAND_HEIGHT, 0.125F,
									 1.0F, 1.0F, 1.0F );
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
        renderBlocks.setRenderBounds(0.125F, 1.0F - PLANTER_BAND_HEIGHT, 0.0F,
									 1.0F, 1.0F, 0.125F );
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
		if (iItemDamage == TYPE_EMPTY)
		{
			renderEmptyPlanterInvBlock(renderBlocks, this, iItemDamage);
		}
		else
		{
			renderFilledPlanterInvBlock(renderBlocks, this, iItemDamage);
		}		
    }

    @Environment(EnvType.CLIENT)
    static public void renderEmptyPlanterInvBlock
    ( 
		RenderBlocks renderBlocks, 
		Block block, 
		int iItemDamage 
	)
    {
    	// render sides
    	
        renderBlocks.setRenderBounds((0.5F - PLANTER_HALF_WIDTH), 0.0F, (0.5F - PLANTER_HALF_WIDTH),
									 (0.5F - PLANTER_HALF_WIDTH) + 0.125F, 1.0F - PLANTER_BAND_HEIGHT, (0.5F + PLANTER_HALF_WIDTH) - 0.125F);
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
        
        renderBlocks.setRenderBounds((0.5F - PLANTER_HALF_WIDTH), 0.0F, (0.5F + PLANTER_HALF_WIDTH) - 0.125F,
									 (0.5F + PLANTER_HALF_WIDTH) - 0.125F, 1.0F - PLANTER_BAND_HEIGHT, (0.5F + PLANTER_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
        
        renderBlocks.setRenderBounds((0.5F + PLANTER_HALF_WIDTH) - 0.125F, 0.0F, (0.5F - PLANTER_HALF_WIDTH) + 0.125F,
									 (0.5F + PLANTER_HALF_WIDTH), 1.0F - PLANTER_BAND_HEIGHT, (0.5F + PLANTER_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
        
        renderBlocks.setRenderBounds((0.5F - PLANTER_HALF_WIDTH) + 0.125F, 0.0F, (0.5F - PLANTER_HALF_WIDTH),
									 (0.5F + PLANTER_HALF_WIDTH), 1.0F - PLANTER_BAND_HEIGHT, (0.5F - PLANTER_HALF_WIDTH) + 0.125F);
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
        
        // render bottom
        
        renderBlocks.setRenderBounds((0.5F - PLANTER_HALF_WIDTH) + 0.125F, 0.0F, (0.5F - PLANTER_HALF_WIDTH) + 0.125F,
									 (0.5F + PLANTER_HALF_WIDTH) - 0.125F, 2.0F / 16F, (0.5F + PLANTER_HALF_WIDTH) - 0.125F);
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
        
    	// render band around top
    	
        renderBlocks.setRenderBounds(0.0F, 1.0F - PLANTER_BAND_HEIGHT, 0.0F,
									 0.125F, 1.0F, 0.875F );
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
        
        renderBlocks.setRenderBounds(0.0F, 1.0F - PLANTER_BAND_HEIGHT, 0.875F,
									 0.875F, 1.0F, 1.0F );
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
        
        renderBlocks.setRenderBounds(0.875F, 1.0F - PLANTER_BAND_HEIGHT, 0.125F,
									 1.0F, 1.0F, 1.0F );
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
        
        renderBlocks.setRenderBounds(0.125F, 1.0F - PLANTER_BAND_HEIGHT, 0.0F,
									 1.0F, 1.0F, 0.125F );
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
    }
    
}