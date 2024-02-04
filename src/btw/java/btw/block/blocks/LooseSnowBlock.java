// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.render.util.RenderUtils;
import btw.util.MiscUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class LooseSnowBlock extends FallingBlock
{
	public static final float HARDNESS = 0.2F;
	
	public static final int CHANCE_OF_HARDENING_INCREMENT = 1;
	public static final int CHANCE_OF_MELTING_INCREMENT = 1;

    public LooseSnowBlock(int iBlockID )
    {
        super( iBlockID, Material.craftedSnow );
        
    	setHardness(HARDNESS);
        setShovelsEffectiveOn();
        
    	setBuoyant();
    	
    	setStepSound( soundSnowFootstep );
        
        setUnlocalizedName( "fcBlockSnowLoose" );
        
        setTickRandomly( true );
		
        setLightOpacity( 4 );
        Block.useNeighborBrightness[iBlockID] = true;
        
		setCreativeTab( CreativeTabs.tabBlock );
    }
    
	@Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return Item.snowball.itemID;
    }

    public int quantityDropped( Random rand )
    {
        return 8;
    }

    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
    {
        dropBlockAsItem( world, i, j, k, iMetadata, 0 );
    }
    
    @Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        if ( world.provider.isHellWorld )
        {
        	// melt instantly in the nether
        	
        	world.setBlockToAir( i, j, k );
        	
            world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
            	"random.fizz", 0.5F, 2.6F + ( world.rand.nextFloat() - world.rand.nextFloat() ) * 0.8F );
            
            for ( int iTempCount = 0; iTempCount < 8; iTempCount++ )
            {
                world.spawnParticle( "largesmoke", (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 
                	0D, 0D, 0D );
            }
        }
        else
        {
        	scheduleCheckForFall(world, i, j, k);
        }
    }
    
    @Override
    public void updateTick( World world, int i, int j, int k, Random rand ) 
    {
    	if (!hasStickySnowNeighborInContact(world, i, j, k) || hasFallingBlockRestingOn(world, i, j, k) )
    	{
    		checkForFall(world, i, j, k);
    	}
    }
    
	@Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
		if ( MiscUtils.isIKInColdBiome(world, i, k) )
		{
			if (rand.nextInt(CHANCE_OF_HARDENING_INCREMENT) == 0 )
			{
				int iHardeningLevel = getHardeningLevel(world, i, j, k);
				
				if ( iHardeningLevel < 7 )
				{
					setHardeningLevel(world, i, j, k, iHardeningLevel + 1);
				}
				else
				{
					convertToSolidSnow(world, i, j, k);
				}
			}
		}
		else
		{
			if (rand.nextInt(CHANCE_OF_MELTING_INCREMENT) == 0 )
			{
				int iHardeningLevel = getHardeningLevel(world, i, j, k);
				
				if ( iHardeningLevel > 0 )
				{
					setHardeningLevel(world, i, j, k, iHardeningLevel - 1);
				}
				else
				{
					melt(world, i, j, k);
				}
			}
		}
    }
	
	@Override
    public void onPlayerWalksOnBlock(World world, int i, int j, int k, EntityPlayer player)
    {
		checkForFall(world, i, j, k);
    }
    
    @Override
    public float getMovementModifier(World world, int i, int j, int k)
    {
    	return 0.8F;
    }
    
    @Override
    public boolean getCanBeSetOnFireDirectly(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public boolean getCanBeSetOnFireDirectlyByItem(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return false;
    }
    
    @Override
    public boolean setOnFireDirectly(World world, int i, int j, int k)
    {
		melt(world, i, j, k);
		
    	return true;
    }
    
    @Override
    public int getChanceOfFireSpreadingDirectlyTo(IBlockAccess blockAccess, int i, int j, int k)
    {
		return 60; // same chance as leaves and other highly flammable objects
    }
    
    @Override
    public boolean canBePistonShoveled(World world, int i, int j, int k)
    {
    	return true;
    }
    
    //------------- Class Specific Methods ------------//
    
    public int getHardeningLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getHardeningLevel(blockAccess.getBlockMetadata(i, j, k));
    }
    
    public int getHardeningLevel(int iMetadata)
    {
    	return ( iMetadata & 14 ) >> 1;
    }

    public void setHardeningLevel(World world, int i, int j, int k, int iLevel)
    {
    	int iMetadata = setHardeningLevel(world.getBlockMetadata(i, j, k), iLevel);
    	
    	world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }
    	
    public int setHardeningLevel(int iMetadata, int iLevel)
    {
    	iMetadata &= ~14;
    	
    	return iMetadata | ( iLevel << 1 );
    }
    
	private void convertToSolidSnow(World world, int i, int j, int k)
	{
		world.setBlockWithNotify( i, j, k, BTWBlocks.solidSnow.blockID );
	}
	
	private void melt(World world, int i, int j, int k)
	{
		MiscUtils.placeNonPersistentWaterMinorSpread(world, i, j, k);
	}
	
	//------------ Client Side Functionality ----------//    

    @Environment(EnvType.CLIENT)
    private Icon[] iconsHardening;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

        iconsHardening = new Icon[8];

        for ( int iTempIndex = 0; iTempIndex < 8; iTempIndex++ )
        {
            iconsHardening[iTempIndex] = register.registerIcon("fcOverlaySnowLoose_" + iTempIndex);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult)
    {
    	if ( bFirstPassResult )
    	{
	    	int iHardeningLevel = getHardeningLevel(renderBlocks.blockAccess, i, j, k);
	    	
    		if ( iHardeningLevel >= 0 && iHardeningLevel <= 7 )
    		{
        		renderBlockWithTexture(renderBlocks, i, j, k, iconsHardening[iHardeningLevel]);
    		}
    	}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	renderBlocks.renderBlockAsItemVanilla( this, iItemDamage, fBrightness );
    	
        RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, iconsHardening[0]);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderFallingBlock(RenderBlocks renderBlocks, int i, int j, int k, int iMetadata)
    {
    	renderBlocks.setRenderAllFaces(true);
    	
        renderBlocks.setRenderBounds(getFixedBlockBoundsFromPool());
        
        renderBlocks.renderStandardBlock( this, i, j, k );
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, i, j, k, iconsHardening[getHardeningLevel(iMetadata)]);
        
    	renderBlocks.setRenderAllFaces(false);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random rand )
    {
    	emitHardeningParticles(world, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, rand);
    }

    @Environment(EnvType.CLIENT)
    private void emitHardeningParticles(World world, double dCenterX, double dCenterY, double dCenterZ, Random rand)
    {
        for ( int iTempCount = 0; iTempCount < 1; ++iTempCount )
        {
            double xPos = dCenterX - 0.60D + rand.nextDouble() * 1.2D;
            double yPos = dCenterY - 0.60D + rand.nextDouble() * 1.2D;
            double zPos = dCenterZ - 0.60D + rand.nextDouble() * 1.2D;
        
        	world.spawnParticle( "fcwhitecloud", xPos, yPos, zPos, 0D, 0D, 0D );
        }
    }
}