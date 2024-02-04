// FCMOD

package btw.block.blocks.legacy;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticNonOpaqueBlock;
import btw.client.render.util.RenderUtils;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class LegacyCandleBlock extends Block
{
	private static final double STICK_HEIGHT = (6D / 16D );
	private static final double STICK_WIDTH = (2D / 16D );
	private static final double STICK_HALF_WIDTH = (STICK_WIDTH / 2D );
	
	private static final double WICK_HEIGHT = (1D / 16D );
	private static final double WICK_WIDTH = (0.5D / 16D );
	private static final double WICK_HALF_WIDTH = (WICK_WIDTH / 2D );
	
    public LegacyCandleBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.legacyCandleMaterial);
        
        setHardness( 0F );    	
    	setPicksEffectiveOn(true);
        setAxesEffectiveOn(true);
        
        setLightValue( 1F );
        
    	initBlockBounds(0.5D - STICK_HALF_WIDTH, 0D, 0.5D - STICK_HALF_WIDTH,
                        0.5D + STICK_HALF_WIDTH, STICK_HEIGHT, 0.5D + STICK_HALF_WIDTH);
    	
        setStepSound( soundStoneFootstep );
        
        setUnlocalizedName( "fcBlockCandle" );
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
    public boolean canPlaceBlockAt(World world, int i, int j, int k )
    {
		int iBlockBelowID = world.getBlockId( i, j - 1, k );
		int iBlockBelowMetadata = world.getBlockMetadata( i, j - 1, k ) ;
		
		if ( iBlockBelowID == BTWBlocks.aestheticNonOpaque.blockID && iBlockBelowMetadata == AestheticNonOpaqueBlock.SUBTYPE_LIGHTNING_ROD)
		{
			return true;
		}

		return WorldUtils.doesBlockHaveSmallCenterHardpointToFacing(world, i, j - 1, k, 1, true);
    }
    
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k )
    {
		return null;
    }
	
	@Override
    public int idDropped( int iMetaData, Random random, int iFortuneModifier )
    {
        return BTWItems.candle.itemID;
    }
	
	@Override
    public int damageDropped( int iMetadata )
    {
    	return iMetadata;
    }
	
	@Override
	public int quantityDropped(Random rand) {
		return 4;
	}
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
    	// pop the block off if it no longer has a valid anchor point
    	
		if ( !canPlaceBlockAt( world, i, j, k ) )
		{
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            world.setBlockWithNotify( i, j, k, 0 );
		}
    }
	
    @Override
	public boolean isBlockRestingOnThatBelow(IBlockAccess blockAccess, int i, int j, int k)
	{
        return true;
	}

    @Override
    public boolean getCanBlockLightItemOnFire(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return true;
    }
    
	@Override
	public void onNeighborDisrupted(World world, int i, int j, int k, int iToFacing)
	{
		if ( iToFacing == 0 )
		{
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            world.setBlockWithNotify( i, j, k, 0 );
		}
	}
	
	//------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private boolean renderingWick = false;

    @Environment(EnvType.CLIENT)
    private Icon[] iconByColor = new Icon[16];
    @Environment(EnvType.CLIENT)
    private Icon iconWick;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        iconByColor[0] = register.registerIcon("fcBlockCandle_c00");
        iconByColor[1] = register.registerIcon("fcBlockCandle_c01");
        iconByColor[2] = register.registerIcon("fcBlockCandle_c02");
        iconByColor[3] = register.registerIcon("fcBlockCandle_c03");
        iconByColor[4] = register.registerIcon("fcBlockCandle_c04");
        iconByColor[5] = register.registerIcon("fcBlockCandle_c05");
        iconByColor[6] = register.registerIcon("fcBlockCandle_c06");
        iconByColor[7] = register.registerIcon("fcBlockCandle_c07");
        iconByColor[8] = register.registerIcon("fcBlockCandle_c08");
        iconByColor[9] = register.registerIcon("fcBlockCandle_c09");
        iconByColor[10] = register.registerIcon("fcBlockCandle_c10");
        iconByColor[11] = register.registerIcon("fcBlockCandle_c11");
        iconByColor[12] = register.registerIcon("fcBlockCandle_c12");
        iconByColor[13] = register.registerIcon("fcBlockCandle_c13");
        iconByColor[14] = register.registerIcon("fcBlockCandle_c14");
        iconByColor[15] = register.registerIcon("fcBlockCandle_c15");

        iconWick = register.registerIcon("fcBlockCandleWick");
        
        blockIcon = iconByColor[0]; // black for hit effects
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		return iconByColor[iMetadata];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		if ( iSide == 0 )
		{
			if (renderingWick)
			{
				return false;
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
    public int idPicked( World world, int i, int j, int k )
    {
        return idDropped( world.getBlockMetadata( i, j, k ), world.rand, 0 );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random rand)
    {
        double xPos = i + 0.5D;
        double yPos = j + 0.1D + STICK_HEIGHT;
        double zPos = k + 0.5D;

        // tripple flame so that it's a little more steady
        
        world.spawnParticle( "fcsmallflame", xPos, yPos, zPos, 0D, 0D, 0D );
        world.spawnParticle( "fcsmallflame", xPos, yPos, zPos, 0D, 0D, 0D );
        world.spawnParticle( "fcsmallflame", xPos, yPos, zPos, 0D, 0D, 0D );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderStandardBlock( this, i, j, k );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k,
                                      boolean bFirstPassResult)
    {
    	if ( bFirstPassResult )
    	{
            renderingWick = true;
    		
            renderBlocks.setRenderBounds(
                    0.5D - WICK_HALF_WIDTH, STICK_HEIGHT, 0.5D - WICK_HALF_WIDTH,
                    0.5D + WICK_HALF_WIDTH, STICK_HEIGHT + WICK_HEIGHT, 0.5D + WICK_HALF_WIDTH);
        
            RenderUtils.renderBlockFullBrightWithTexture(renderBlocks,
                                                         renderBlocks.blockAccess, i, j, k, iconWick);

            renderingWick = false;
    	}
    }
}