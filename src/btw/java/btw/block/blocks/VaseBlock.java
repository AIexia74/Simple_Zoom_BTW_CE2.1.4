// FCMOD

package btw.block.blocks;

import btw.block.tileentity.VaseTileEntity;
import btw.client.render.util.RenderUtils;
import btw.inventory.util.InventoryUtils;
import btw.block.util.RayTraceUtils;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class VaseBlock extends BlockContainer
{
	public static final float VASE_BASE_WIDTH = (8.0F / 16F );
	public static final float VASE_BASE_HALF_WIDTH = VASE_BASE_WIDTH / 2.0F;
	public static final float VASE_BASE_HEIGHT = (1.0F / 16F );
	public static final float VASE_BODY_WIDTH = (10.0F / 16F );
	public static final float VASE_BODY_HALF_WIDTH = VASE_BODY_WIDTH / 2.0F;
	public static final float VASE_BODY_HEIGHT = (6.0F / 16F );
	public static final float VASE_NECK_BASE_WIDTH = (8.0F / 16F );
	public static final float VASE_NECK_BASE_HALF_WIDTH = VASE_NECK_BASE_WIDTH / 2.0F;
	public static final float VASE_NECK_BASE_HEIGHT = (1.0F / 16F );
	public static final float VASE_NECK_WIDTH = (4.0F / 16F );
	public static final float VASE_NECK_HALF_WIDTH = VASE_NECK_WIDTH / 2.0F;
	public static final float VASE_NECK_HEIGHT = (7.0F / 16F );
	public static final float VASE_TOP_WIDTH = (6.0F / 16F );
	public static final float VASE_TOP_HALF_WIDTH = VASE_TOP_WIDTH / 2.0F;
	public static final float VASE_TOP_HEIGHT = (1.0F / 16F );
	
    public VaseBlock(int iBlockID )
    {
        super( iBlockID, Material.glass );
        
        setHardness( 0F );
        
        setBuoyancy(1F);
        
    	initBlockBounds((0.5F - VASE_BODY_HALF_WIDTH), 0F, (0.5F - VASE_BODY_HALF_WIDTH),
                        (0.5F + VASE_BODY_HALF_WIDTH), 1F, (0.5F + VASE_BODY_HALF_WIDTH));
        
        setStepSound( soundGlassFootstep );        
        
        setUnlocalizedName( "fcBlockVase" );
        
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
    public int quantityDropped(Random random)
    {
        return 0;
    }

	@Override
    public int damageDropped( int i )
    {
        return i;
    }

	@Override
    public TileEntity createNewTileEntity(World world )
    {
        return new VaseTileEntity();
    }

	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
    	ItemStack playerEquippedItem = player.getCurrentEquippedItem();
    	
        if ( world.isRemote )
        {
            return true;
        } 
        else
        {
        	if ( playerEquippedItem != null )
        	{
        		if ( playerEquippedItem.stackSize > 0 )
        		{
                    VaseTileEntity tileEntityVase = (VaseTileEntity)world.getBlockTileEntity( i, j, k );
                    
                    int iTempStackSize = playerEquippedItem.stackSize;
        		
                    if ( InventoryUtils.addItemStackToInventory(tileEntityVase, playerEquippedItem) )
                    {
                    	player.destroyCurrentEquippedItem();
                    	
			            world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
			            		"random.pop", 0.25F, 
			            		((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			            
                    	return true;
                	}
                    else if ( playerEquippedItem.stackSize < iTempStackSize )
                    {
			            world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
			            		"random.pop", 0.25F, 
			            		((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			            
                        return true;                    	
                    }                    
        		}
        	}
        }
        
    	return false;
    }
    
	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
    	VaseTileEntity tileEntity = (VaseTileEntity)( world.getBlockTileEntity(i, j, k) );
    	
    	if ( tileEntity != null )
    	{
    		InventoryUtils.ejectInventoryContents(world, i, j, k, (IInventory)tileEntity);
    	}

        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
    }

	@Override
    protected boolean canSilkHarvest()
    {
		return true;
    }

	@Override
    public void onBlockHarvested( World world, int i, int j, int k, int iMetadata, EntityPlayer player )
    {
		if ( !world.isRemote && !EnchantmentHelper.getSilkTouchModifier( player ) )
		{
			checkForExplosion(world, i, j, k);
		}    	
    }
    
    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	RayTraceUtils rayTrace = new RayTraceUtils( world, i, j, k, startRay, endRay );
    	
    	// base 
    	
    	rayTrace.addBoxWithLocalCoordsToIntersectionList((0.5F - VASE_BASE_HALF_WIDTH), 0.0F, (0.5F - VASE_BASE_HALF_WIDTH),
                                                         (0.5F + VASE_BASE_HALF_WIDTH), VASE_BASE_HEIGHT, (0.5F + VASE_BASE_HALF_WIDTH));
        
    	// body 
    	
    	rayTrace.addBoxWithLocalCoordsToIntersectionList((0.5F - VASE_BODY_HALF_WIDTH), VASE_BASE_HEIGHT, (0.5F - VASE_BODY_HALF_WIDTH),
                                                         (0.5F + VASE_BODY_HALF_WIDTH), VASE_BASE_HEIGHT + VASE_BODY_HEIGHT, (0.5F + VASE_BODY_HALF_WIDTH));
        
    	// neck base 
    	
    	rayTrace.addBoxWithLocalCoordsToIntersectionList((0.5F - VASE_NECK_BASE_HALF_WIDTH), VASE_BASE_HEIGHT + VASE_BODY_HEIGHT, (0.5F -
                                                                                                                                   VASE_NECK_BASE_HALF_WIDTH),
                                                         (0.5F + VASE_NECK_BASE_HALF_WIDTH), VASE_BASE_HEIGHT + VASE_BODY_HEIGHT + VASE_NECK_BASE_HEIGHT, (0.5F + VASE_NECK_BASE_HALF_WIDTH));
        
        // neck 
    	
    	rayTrace.addBoxWithLocalCoordsToIntersectionList((0.5F - VASE_NECK_HALF_WIDTH), 1.0F - (VASE_TOP_HEIGHT + VASE_NECK_HEIGHT), (0.5F -
                                                                                                                                      VASE_NECK_HALF_WIDTH),
                                                         (0.5F + VASE_NECK_HALF_WIDTH), 1.0F - VASE_TOP_HEIGHT, (0.5F + VASE_NECK_HALF_WIDTH));
        
    	// top 
    	
    	rayTrace.addBoxWithLocalCoordsToIntersectionList((0.5F - VASE_TOP_HALF_WIDTH), 1.0F - VASE_TOP_HEIGHT, (0.5F - VASE_TOP_HALF_WIDTH),
                                                         (0.5F + VASE_TOP_HALF_WIDTH), 1.0F, (0.5F + VASE_TOP_HALF_WIDTH));
        
    	return rayTrace.getFirstIntersection();
    }
    
    @Override
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
    	return iFacing == 1;
	}
    
    @Override
    public void onArrowImpact(World world, int i, int j, int k, EntityArrow arrow)
    {
    	if ( !world.isRemote )
    	{
    		breakVase(world, i, j, k);
    	}
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
	
	public void breakVase(World world, int i, int j, int k)
	{
        world.playAuxSFX( 2001, i, j, k, blockID );
        
        checkForExplosion(world, i, j, k);
        
		world.setBlockWithNotify( i, j, k, 0 );
	}
	
	private boolean checkForExplosion(World world, int i, int j, int k)
	{
		// returns true if an explosion occurs
		
    	VaseTileEntity tileEntity = (VaseTileEntity)( world.getBlockTileEntity(i, j, k) );
    	
    	if ( tileEntity != null )
    	{
    		IInventory inventory = (IInventory)tileEntity;
    		
    		if (InventoryUtils.getFirstOccupiedStackOfItem(inventory, BTWItems.blastingOil.itemID) >= 0 )
    		{
    			InventoryUtils.clearInventoryContents(inventory);
    			
    	        world.createExplosion( null, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 1.5F, true );

    	        return true;
    		}
    	}

    	return false;
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconByColor = new Icon[16];

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        iconByColor[0] = register.registerIcon("fcBlockVase_c00");
        iconByColor[1] = register.registerIcon("fcBlockVase_c01");
        iconByColor[2] = register.registerIcon("fcBlockVase_c02");
        iconByColor[3] = register.registerIcon("fcBlockVase_c03");
        iconByColor[4] = register.registerIcon("fcBlockVase_c04");
        iconByColor[5] = register.registerIcon("fcBlockVase_c05");
        iconByColor[6] = register.registerIcon("fcBlockVase_c06");
        iconByColor[7] = register.registerIcon("fcBlockVase_c07");
        iconByColor[8] = register.registerIcon("fcBlockVase_c08");
        iconByColor[9] = register.registerIcon("fcBlockVase_c09");
        iconByColor[10] = register.registerIcon("fcBlockVase_c10");
        iconByColor[11] = register.registerIcon("fcBlockVase_c11");
        iconByColor[12] = register.registerIcon("fcBlockVase_c12");
        iconByColor[13] = register.registerIcon("fcBlockVase_c13");
        iconByColor[14] = register.registerIcon("fcBlockVase_c14");
        iconByColor[15] = register.registerIcon("fcBlockVase_c15");
        
        blockIcon = iconByColor[0]; // white for hit effects
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		// FCTODO: This is bad.  Clean it up.
    	// method largly copied from block cloth
		
    	if ( iMetadata != 0 )
    	{
    		iMetadata = 1 + ((~iMetadata) & 0xf);
    	}
    	
		return iconByColor[iMetadata];
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
    	return renderVaseBlock(renderBlocks, renderBlocks.blockAccess, i, j, k, this);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	renderInvBlock(renderBlocks, this, iItemDamage);
    }

    @Environment(EnvType.CLIENT)
    static public boolean renderVaseBlock
    (
    	RenderBlocks renderBlocks,
    	IBlockAccess blockAcces,
    	int i, int j, int k,
    	Block block
	)
    {
		// seperate static function so that this can be called externally by unfired pottery
		
    	// render base 
    	
        renderBlocks.setRenderBounds((0.5F - VASE_BASE_HALF_WIDTH), 0.0F, (0.5F - VASE_BASE_HALF_WIDTH),
                                     (0.5F + VASE_BASE_HALF_WIDTH), VASE_BASE_HEIGHT, (0.5F + VASE_BASE_HALF_WIDTH));
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
    	// render body 
    	
        renderBlocks.setRenderBounds((0.5F - VASE_BODY_HALF_WIDTH), VASE_BASE_HEIGHT, (0.5F - VASE_BODY_HALF_WIDTH),
                                     (0.5F + VASE_BODY_HALF_WIDTH), VASE_BASE_HEIGHT + VASE_BODY_HEIGHT, (0.5F + VASE_BODY_HALF_WIDTH));
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
    	// render neck base 
    	
        renderBlocks.setRenderBounds((0.5F - VASE_NECK_BASE_HALF_WIDTH), VASE_BASE_HEIGHT + VASE_BODY_HEIGHT, (0.5F - VASE_NECK_BASE_HALF_WIDTH),
                                     (0.5F + VASE_NECK_BASE_HALF_WIDTH), VASE_BASE_HEIGHT + VASE_BODY_HEIGHT + VASE_NECK_BASE_HEIGHT, (0.5F +
                                                                                                                                       VASE_NECK_BASE_HALF_WIDTH));
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
    	// render neck 
    	
        renderBlocks.setRenderBounds((0.5F - VASE_NECK_HALF_WIDTH), 1.0F - (VASE_TOP_HEIGHT + VASE_NECK_HEIGHT), (0.5F - VASE_NECK_HALF_WIDTH),
                                     (0.5F + VASE_NECK_HALF_WIDTH), 1.0F - VASE_TOP_HEIGHT, (0.5F + VASE_NECK_HALF_WIDTH));
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
    	// render top 
    	
        renderBlocks.setRenderBounds((0.5F - VASE_TOP_HALF_WIDTH), 1.0F - VASE_TOP_HEIGHT, (0.5F - VASE_TOP_HALF_WIDTH),
                                     (0.5F + VASE_TOP_HALF_WIDTH), 1.0F, (0.5F + VASE_TOP_HALF_WIDTH));
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
    	return true;
    }

    @Environment(EnvType.CLIENT)
    static public void renderInvBlock
    ( 
		RenderBlocks renderBlocks, 
		Block block, 
		int iItemDamage 
	)
    {
		// seperate static function so that this can be called externally by unfired pottery
		
    	// render base 
    	
        renderBlocks.setRenderBounds((0.5F - VASE_BASE_HALF_WIDTH), 0.0F, (0.5F - VASE_BASE_HALF_WIDTH),
                                     (0.5F + VASE_BASE_HALF_WIDTH), VASE_BASE_HEIGHT, (0.5F + VASE_BASE_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
        
    	// render body 
    	
        renderBlocks.setRenderBounds((0.5F - VASE_BODY_HALF_WIDTH), VASE_BASE_HEIGHT, (0.5F - VASE_BODY_HALF_WIDTH),
                                     (0.5F + VASE_BODY_HALF_WIDTH), VASE_BASE_HEIGHT + VASE_BODY_HEIGHT, (0.5F + VASE_BODY_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
        
    	// render neck base 
    	
        renderBlocks.setRenderBounds((0.5F - VASE_NECK_BASE_HALF_WIDTH), VASE_BASE_HEIGHT + VASE_BODY_HEIGHT, (0.5F - VASE_NECK_BASE_HALF_WIDTH),
                                     (0.5F + VASE_NECK_BASE_HALF_WIDTH), VASE_BASE_HEIGHT + VASE_BODY_HEIGHT + VASE_NECK_BASE_HEIGHT, (0.5F +
                                                                                                                                       VASE_NECK_BASE_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
        
        // render neck 
    	
        renderBlocks.setRenderBounds((0.5F - VASE_NECK_HALF_WIDTH), 1.0F - (VASE_TOP_HEIGHT + VASE_NECK_HEIGHT), (0.5F - VASE_NECK_HALF_WIDTH),
                                     (0.5F + VASE_NECK_HALF_WIDTH), 1.0F - VASE_TOP_HEIGHT, (0.5F + VASE_NECK_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
        
    	// render top 
    	
        renderBlocks.setRenderBounds((0.5F - VASE_TOP_HALF_WIDTH), 1.0F - VASE_TOP_HEIGHT, (0.5F - VASE_TOP_HALF_WIDTH),
                                     (0.5F + VASE_TOP_HALF_WIDTH), 1.0F, (0.5F + VASE_TOP_HALF_WIDTH));
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, iItemDamage);
   }
}