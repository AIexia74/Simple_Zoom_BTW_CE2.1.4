// FCMOD

package btw.block.blocks;

import btw.client.render.util.RenderUtils;
import btw.block.util.RayTraceUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class PlanterBlockBase extends Block
{
	protected static final double PLANTER_WIDTH = (1D - (4D / 16D ) );
	protected static final double PLANTER_HALF_WIDTH = (PLANTER_WIDTH / 2D );
	
	protected static final double PLANTER_BAND_HEIGHT = (5D / 16D );
	protected static final double PLANTER_BAND_HALF_HEIGHT = (PLANTER_BAND_HEIGHT / 2D );
	
    protected PlanterBlockBase(int iBlockID )
    {
    	super( iBlockID, Material.glass );
    	
        setHardness( 0.6F );
        setPicksEffectiveOn(true);
        
        setTickRandomly( true );        
        
        setStepSound( soundGlassFootstep );        
        
        setUnlocalizedName( "fcBlockPlanterSoil" );     
        
        setCreativeTab( CreativeTabs.tabDecorations );
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
    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity )
    {
		if ( !world.isRemote && entity.isEntityAlive() && entity instanceof EntityItem )
		{
			EntityItem entityItem = (EntityItem)entity;
			ItemStack stack = entityItem.getEntityItem();			
			
			if ( stack.getItem().itemID == Item.dyePowder.itemID && 
				stack.getItemDamage() == 15 ) // bone meal
			{
				if ( attemptToApplyFertilizerTo(world, i, j, k) )
				{
					stack.stackSize--;
					
					if ( stack.stackSize <= 0 )
					{
						entityItem.setDead();
					}

		            world.playSoundEffect( i + 0.5D, j + 0.5D, k + 0.5D, "random.pop", 0.25F, 
	            		( ( world.rand.nextFloat() - world.rand.nextFloat() ) * 
            			0.7F + 1F ) * 2F );
				}
			}
		}
    }
	
	@Override
	public float getPlantGrowthOnMultiplier(World world, int i, int j, int k, Block plantBlock)
	{
		if ( getIsFertilizedForPlantGrowth(world, i, j, k) )
		{
			return 2F;
		}

		return 1F;
	}
	
    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	RayTraceUtils rayTrace = new RayTraceUtils( world, i, j, k, startRay, endRay );
    	
    	// bottom
    	
		rayTrace.addBoxWithLocalCoordsToIntersectionList(0.5F - PLANTER_HALF_WIDTH, 0F, 0.5F - PLANTER_HALF_WIDTH,
                                                         0.5F + PLANTER_HALF_WIDTH, 1F - PLANTER_BAND_HEIGHT, 0.5F + PLANTER_HALF_WIDTH);
    
    	// top
    	
		rayTrace.addBoxWithLocalCoordsToIntersectionList(0F, 1F - PLANTER_BAND_HEIGHT, 0F, 1F, 1F, 1F);
        
        return rayTrace.getFirstIntersection();
    }
    
    @Override
    public float getMovementModifier(World world, int i, int j, int k)
    {
    	return 1F;
    }
    
    @Override
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
    	return iFacing == 0 || super.hasCenterHardPointToFacing( blockAccess, i, j, k, iFacing, bIgnoreTransparency );
	}
    
    @Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return iFacing == 1;		
	}
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//

    @Environment(EnvType.CLIENT)
    protected Icon iconTopSoilWet;
    @Environment(EnvType.CLIENT)
    protected Icon iconTopSoilWetFertilized;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		blockIcon = register.registerIcon( "fcBlockPlanter" );

        iconTopSoilWet = register.registerIcon("fcBlockPlanter_top_wet");
        iconTopSoilWetFertilized = register.registerIcon("fcBlockPlanter_top_wet_fertilized");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }

    @Environment(EnvType.CLIENT)
    protected void renderFilledPlanterInvBlock(RenderBlocks renderer, Block block, int iItemDamage)
	{
    	// render bottom
    	
        renderer.setRenderBounds(0.5F - PLANTER_HALF_WIDTH, 0F, 0.5F - PLANTER_HALF_WIDTH,
                                 0.5F + PLANTER_HALF_WIDTH, 1F - PLANTER_BAND_HEIGHT, 0.5F + PLANTER_HALF_WIDTH);
    
        RenderUtils.renderInvBlockWithMetadata(renderer, block, -0.5F, -0.5F, -0.5F, iItemDamage);
        
    	// render top
    	
        renderer.setRenderBounds(0F, 1F - PLANTER_BAND_HEIGHT, 0F,
                                 1F, 1F, 1F );
    
        RenderUtils.renderInvBlockWithMetadata(renderer, block, -0.5F, -0.5F, -0.5F, iItemDamage);
    }

    @Environment(EnvType.CLIENT)
    protected boolean renderFilledPlanterBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	// render bottom
    	
        renderer.setRenderBounds(0.5F - PLANTER_HALF_WIDTH, 0F, 0.5F - PLANTER_HALF_WIDTH,
                                 0.5F + PLANTER_HALF_WIDTH, 1F - PLANTER_BAND_HEIGHT, 0.5F + PLANTER_HALF_WIDTH);
    
        renderer.renderStandardBlock( this, i, j, k );
    
    	// render top
    	
        renderer.setRenderBounds(0F, 1F - PLANTER_BAND_HEIGHT, 0F,
                                 1F, 1F, 1F );
    
        renderer.renderStandardBlock( this, i, j, k );
    
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, int i, int j, int k )
    {
    	// only display box around top portion
    	
        return AxisAlignedBB.getAABBPool().getAABB(0F, 1F - PLANTER_BAND_HEIGHT, 0F,
                                                   1F, 1F, 1F ).offset( i, j, k );
    }
}
