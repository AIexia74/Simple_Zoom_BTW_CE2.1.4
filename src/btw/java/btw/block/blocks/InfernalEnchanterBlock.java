// FCMOD

package btw.block.blocks;

import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.block.tileentity.InfernalEnchanterTileEntity;
import btw.client.render.util.RenderUtils;
import btw.inventory.BTWContainers;
import btw.inventory.container.InfernalEnchanterContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class InfernalEnchanterBlock extends BlockContainer
{
    public static final float BLOCK_HEIGHT = 0.50F;
    public static final float CANDLE_HEIGHT = 0.25F;

	private static final float BLOCK_HARDNESS = 100F;
	private static final float BLOCK_EXPLOSION_RESISTANCE = 2000F;
	
	private static final int HORIZONTAL_BOOK_SHELF_CHECK_DISTANCE = 8;
	private static final int VERTICAL_POSITIVE_BOOK_SHELF_CHECK_DISTANCE = 8;
	private static final int VERTICAL_NEGATIVE_BOOK_SHELF_CHECK_DISTANCE = 8;
	
    public InfernalEnchanterBlock(int iBlockID)
    {
        super( iBlockID, BTWBlocks.soulforgedSteelMaterial);
        
        initBlockBounds(0F, 0F, 0F, 1F, BLOCK_HEIGHT, 1F);
        
        setLightOpacity( 0 );
        
        setHardness(BLOCK_HARDNESS);
        setResistance(BLOCK_EXPLOSION_RESISTANCE);
        
        setStepSound( soundMetalFootstep );
        
        setUnlocalizedName( "fcBlockInfernalEnchanter" );
        
        setCreativeTab( CreativeTabs.tabDecorations );
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
    public TileEntity createNewTileEntity(World world )
    {
        return new InfernalEnchanterTileEntity();
    }
    
	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
        if ( !world.isRemote )
        {
        	if ( player instanceof EntityPlayerMP ) // should always be true
        	{
        		InfernalEnchanterContainer container = new InfernalEnchanterContainer( player.inventory, world, i, j, k );
        		
        		BTWMod.serverOpenCustomInterface( (EntityPlayerMP)player, container, BTWContainers.infernalEnchanterContainerID);
        	}
        }
        
        return true;
    }
    
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public boolean canTransmitRotationHorizontallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
    //------------- Class Specific Methods ------------//

	private boolean isValidBookshelf(World world, int i, int j, int k)
	{
		int iBlockID = world.getBlockId( i, j, k );
		
		if ( iBlockID == Block.bookShelf.blockID )
		{
			// check around the bookshelf for an empty block to provide access
			
			if ( world.isAirBlock( i + 1, j, k ) ||
				world.isAirBlock( i - 1, j, k ) ||
				world.isAirBlock( i, j, k + 1 ) ||
				world.isAirBlock( i, j, k - 1 ) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBySideArray = new Icon[6];

    @Environment(EnvType.CLIENT)
    private Icon iconCandle;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        Icon bottomIcon = register.registerIcon( "fcBlockInfernalEnchanter_bottom" );
        
        blockIcon = bottomIcon; // for hit effects

        iconBySideArray[0] = bottomIcon;
        iconBySideArray[1] = register.registerIcon("fcBlockInfernalEnchanter_top");
        
        Icon sideIcon = register.registerIcon( "fcBlockInfernalEnchanter_side" );

        iconBySideArray[2] = sideIcon;
        iconBySideArray[3] = sideIcon;
        iconBySideArray[4] = sideIcon;
        iconBySideArray[5] = sideIcon;

        iconCandle = register.registerIcon("fcBlockCandle_c00");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		return iconBySideArray[iSide];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random random )
    {
        super.randomDisplayTick( world, i, j, k, random );
        
        displayMagicLetters(world, i, j, k, random);
    }

    @Environment(EnvType.CLIENT)
    private void displayMagicLetters(World world, int i, int j, int k, Random rand)
    {
    	TileEntity tileEntity = world.getBlockTileEntity( i, j, k );
    	
    	if ( tileEntity != null && tileEntity instanceof InfernalEnchanterTileEntity)
    	{
    		InfernalEnchanterTileEntity enchanterEntity = (InfernalEnchanterTileEntity)tileEntity;
    		
    		if ( enchanterEntity.playerNear)
    		{
		        for ( int iTempCount = 0; iTempCount < 64; iTempCount++ )
		        {
		        	int iTargetI = rand.nextInt(HORIZONTAL_BOOK_SHELF_CHECK_DISTANCE * 2 + 1) - HORIZONTAL_BOOK_SHELF_CHECK_DISTANCE + i;
		        	
		        	int iTargetJ= rand.nextInt(VERTICAL_POSITIVE_BOOK_SHELF_CHECK_DISTANCE + VERTICAL_NEGATIVE_BOOK_SHELF_CHECK_DISTANCE + 1) -
                                  VERTICAL_NEGATIVE_BOOK_SHELF_CHECK_DISTANCE + j;
		        	
		        	int iTargetK = rand.nextInt(HORIZONTAL_BOOK_SHELF_CHECK_DISTANCE * 2 + 1) - HORIZONTAL_BOOK_SHELF_CHECK_DISTANCE + k;
		        	
		            if ( isValidBookshelf(world, iTargetI, iTargetJ, iTargetK) )
		            {
		            	Vec3 velocity = Vec3.createVectorHelper( (double)( iTargetI - i  ), (double)( iTargetJ - j ), (double)( iTargetK - k ) );
		            	
		            	// oddly, the following specifies the dest of the particles, not the origins
		            	
			            world.spawnParticle( "enchantmenttable", (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
		            		velocity.xCoord, velocity.yCoord, velocity.zCoord );
		            }
		        }
    		}
    	}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderer.blockAccess;
    	
    	// render base
    	
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
        renderer.renderStandardBlock( this, i, j, k );

        // render candles
        
        renderer.setRenderBounds(( 1.0F / 16.0F ), BLOCK_HEIGHT, (1.0F / 16.0F ), (3.0F / 16.0F ), BLOCK_HEIGHT + CANDLE_HEIGHT, (3.0F / 16.0F ));
        
        RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, iconCandle);
        
        renderer.setRenderBounds(( 13.0F / 16.0F ), BLOCK_HEIGHT, (1.0F / 16.0F ), (15.0F / 16.0F ), BLOCK_HEIGHT + CANDLE_HEIGHT, (3.0F / 16.0F ));
        
        RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, iconCandle);
        
        renderer.setRenderBounds(( 1.0F / 16.0F ), BLOCK_HEIGHT, (13.0F / 16.0F ), (3.0F / 16.0F ), BLOCK_HEIGHT + CANDLE_HEIGHT, (15.0F / 16.0F ));
        
        RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, iconCandle);
        
        renderer.setRenderBounds(( 13.0F / 16.0F ), BLOCK_HEIGHT, (13.0F / 16.0F ), (15.0F / 16.0F ), BLOCK_HEIGHT + CANDLE_HEIGHT, (15.0F / 16.0F ));
        
        RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, iconCandle);
        
    	return true;
    }    
}