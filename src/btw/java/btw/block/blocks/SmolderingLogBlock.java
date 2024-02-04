// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.client.render.util.RenderUtils;
import btw.entity.FallingBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class SmolderingLogBlock extends FallingBlock
{
	private static final int CHANCE_OF_DECAY = 5;

	private static final int CHANCE_OF_EXTINGUISH_IN_RAIN = 5;

	private static final float EXPLOSION_STRENGTH = 1F;

	public SmolderingLogBlock(int iBlockID)
	{
		super( iBlockID, BTWBlocks.logMaterial);

		setHardness( 2F );

		setAxesEffectiveOn();
		setChiselsEffectiveOn();

		setBuoyant();

		setTickRandomly( true );

		setStepSound( soundWoodFootstep );

		setUnlocalizedName( "fcBlockLogSmouldering" );
	}

	@Override
	public float getBlockHardness(World world, int i, int j, int k )
	{
		float fHardness = super.getBlockHardness( world, i, j, k );

		int iMetadata = world.getBlockMetadata( i, j, k );

		if ( getIsStump(world, i, j, k) )
		{   
			fHardness *= 3F;    		
		}

		return fHardness; 
	}

	@Override
	public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
	{
		return 0;
	}

	@Override
	public boolean getIsProblemToRemove(ItemStack toolStack, IBlockAccess blockAccess, int i, int j, int k)
	{
		return getIsStump(blockAccess, i, j, k);
	}

	@Override
	public boolean getCanBlockBeIncinerated(World world, int i, int j, int k)
	{
		return !getIsStump(world, i, j, k);
	}

	@Override
	public boolean canConvertBlock(ItemStack stack, World world, int i, int j, int k)
	{
		return getIsStump(world, i, j, k);
	}

	@Override
	public boolean convertBlock(ItemStack stack, World world, int i, int j, int k, int iFromSide)
	{
		if ( getIsStump(world.getBlockMetadata(i, j, k)) )
		{
			world.setBlockWithNotify( i, j, k, BTWBlocks.charredStump.blockID );

			return true;
		}   

		return false;
	}

	@Override
	public void updateTick( World world, int i, int j, int k, Random rand ) 
	{
		if ( !hasWaterToSidesOrTop(world, i, j, k) )
		{
			// prevent falling behavior for stumps and first level cinders

			int iMetadata = world.getBlockMetadata( i, j, k );

			if (!getIsStump(iMetadata) && getBurnLevel(iMetadata) > 0 )
			{
				super.updateTick( world, i, j, k, rand );
			}
		}
		else
		{
			// extinguish due to neighboring water blocks

			convertToCinders(world, i, j, k);

			world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );
		}
	}

	@Override
	public void randomUpdateTick(World world, int i, int j, int k, Random rand)
	{
		if (world.getGameRules().getGameRuleBooleanValue("doFireTick")) {
			if ( !checkForGoOutInRain(world, i, j, k) )
			{			
				FireBlock.checkForSmoulderingSpreadFromLocation(world, i, j, k);

				int iBurnLevel = getBurnLevel(world, i, j, k);

				if ( iBurnLevel == 0 )
				{
					if ( !FireBlock.hasFlammableNeighborsWithinSmoulderRange(world, i, j, k) )
					{
						int iMetadata = world.getBlockMetadata( i, j, k );

						iMetadata = setBurnLevel(iMetadata, 1);

						if ( isSupportedBySolidBlocks(world, i, j, k) )
						{
							// intentionally leaves the flag as true if it's already set

							iMetadata = setShouldSuppressSnapOnFall(iMetadata, true);
						}

						world.setBlockMetadataWithNotify( i, j, k, iMetadata );

						scheduleCheckForFall(world, i, j, k);
					}
				}
				else if (rand.nextInt(CHANCE_OF_DECAY) == 0 )
				{
					if ( iBurnLevel < 3 )
					{
						setBurnLevel(world, i, j, k, iBurnLevel + 1);
					}
					else
					{
						convertToCinders(world, i, j, k);
					}
				}
			}
		}
	}

	@Override
	public boolean getIsBlockWarm(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}

	@Override
	public boolean getCanBlockLightItemOnFire(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}

	@Override
	protected void onStartFalling( EntityFallingSand entity )
	{	
		if ( !getShouldSuppressSnapOnFall(entity.metadata) )
		{
			entity.worldObj.playAuxSFX( BTWEffectManager.SMOLDERING_LOG_FALL_EFFECT_ID,
					MathHelper.floor_double( entity.posX ), 
					MathHelper.floor_double( entity.posY ), 
					MathHelper.floor_double( entity.posZ ), 0 );

			// only makes the fall sound once

			entity.metadata = setShouldSuppressSnapOnFall(entity.metadata, true);
		}        
	}

	@Override
	public void onFallingUpdate(FallingBlockEntity entity)
	{
		if ( entity.worldObj.isRemote )
		{
			emitSmokeParticles(entity.worldObj, entity.posX, entity.posY, entity.posZ,
							   entity.worldObj.rand, getBurnLevel(entity.metadata));
		}
	}

	@Override
	public boolean onFinishedFalling(EntityFallingSand entity, float fFallDistance)
	{
		if ( !entity.worldObj.isRemote )
		{
			int i = MathHelper.floor_double( entity.posX );
			int j = MathHelper.floor_double( entity.posY );
			int k = MathHelper.floor_double( entity.posZ );

			int iFallDistance = MathHelper.ceiling_float_int( fFallDistance - 5.0F );

			if ( iFallDistance >= 0 )
			{	  
				if ( !Material.water.equals( entity.worldObj.getBlockMaterial( i, j, k ) ) )
				{	    			
					if ( entity.rand.nextInt( 5 ) < iFallDistance )
					{
						explode(entity.worldObj, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D);

						return false;
					}
				}
			}
		}

		return true;
	}

	@Override
	public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
	{
		return 1000; // always convert, never harvest
	}

	@Override
	public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
	{
		explode(world, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D);
	}

	//------------- Class Specific Methods ------------//

	public int getBurnLevel(IBlockAccess blockAccess, int i, int j, int k)
	{
		int iMetadata = blockAccess.getBlockMetadata( i, j, k );

		return getBurnLevel(iMetadata);
	}

	public int getBurnLevel(int iMetadata)
	{
		return iMetadata & 3;
	}

	public void setBurnLevel(World world, int i, int j, int k, int iLevel)
	{
		int iMetadata = setBurnLevel(world.getBlockMetadata(i, j, k), iLevel);

		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}

	public int setBurnLevel(int iMetadata, int iLevel)
	{
		iMetadata &= ~3;

		return iMetadata | iLevel;
	}

	public boolean getShouldSuppressSnapOnFall(IBlockAccess blockAccess, int i, int j, int k)
	{
		int iMetadata = blockAccess.getBlockMetadata( i, j, k );

		return getShouldSuppressSnapOnFall(iMetadata);
	}

	public boolean getShouldSuppressSnapOnFall(int iMetadata)
	{
		return ( iMetadata & 4 ) != 0;
	}

	public int setShouldSuppressSnapOnFall(int iMetadata, boolean bSnap)
	{
		if ( bSnap )
		{
			iMetadata |= 4;
		}
		else
		{
			iMetadata &= ~4;
		}

		return iMetadata;
	}

	public boolean getIsStump(IBlockAccess blockAccess, int i, int j, int k)
	{
		int iMetadata = blockAccess.getBlockMetadata( i, j, k );

		return getIsStump(iMetadata);
	}

	public boolean getIsStump(int iMetadata)
	{
		return ( iMetadata & 8 ) != 0;
	}

	public int setIsStump(int iMetadata, boolean bStump)
	{
		if ( bStump )
		{
			iMetadata |= 8;
		}
		else
		{
			iMetadata &= ~8;
		}

		return iMetadata;
	}

	private boolean checkForGoOutInRain(World world, int i, int j, int k)
	{
		if (world.rand.nextInt(CHANCE_OF_EXTINGUISH_IN_RAIN) == 0 )
		{
			if ( world.isRainingAtPos(i, j + 1, k) )
			{
				world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );

				convertToCinders(world, i, j, k);

				return true;
			}
		}

		return false;
	}

	private void convertToCinders(World world, int i, int j, int k)
	{
		if ( getIsStump(world, i, j, k) )
		{
			int iNewMetadata = BTWBlocks.woodCinders.setIsStump(0, true);

			world.setBlockAndMetadataWithNotify( i, j, k, BTWBlocks.woodCinders.blockID, iNewMetadata );
		}
		else
		{
			world.setBlockWithNotify( i, j, k, BTWBlocks.woodCinders.blockID );
		}
	}

	private void emitSmokeParticles(World world, double dCenterX, double dCenterY, double dCenterZ, Random rand, int iBurnLevel)
	{
		for ( int iTempCount = 0; iTempCount < 5; ++iTempCount )
		{
			double xPos = dCenterX - 0.60D + rand.nextDouble() * 1.2D;
			double yPos = dCenterY + 0.25D + rand.nextDouble() * 0.25D;
			double zPos = dCenterZ - 0.60D + rand.nextDouble() * 1.2D;

			if ( iBurnLevel > 0 )
			{
				world.spawnParticle( "fcwhitesmoke", xPos, yPos, zPos, 0D, 0D, 0D );
			}
			else
			{
				world.spawnParticle( "largesmoke", xPos, yPos, zPos, 0D, 0D, 0D );
			}
		}
	}

	@Override
	public void onBlockDestroyedByExplosion( World world, int i, int j, int k, Explosion explosion )
	{
		if ( !world.isRemote )
		{
			// explode without audio/visual effects to cut down on overhead

			explosion.addSecondaryExplosionNoFX((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, EXPLOSION_STRENGTH, true, false);
		}
	}

	private void explode(World world, double posX, double posY, double posZ)
	{
		world.newExplosionNoFX((Entity)null, posX, posY, posZ, EXPLOSION_STRENGTH, true, false);

		notifyNearbyAnimalsFinishedFalling(world, MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));

		world.playAuxSFX( BTWEffectManager.SMOLDERING_LOG_EXPLOSION_EFFECT_ID,
				MathHelper.floor_double( posX ), MathHelper.floor_double( posY ), MathHelper.floor_double( posZ ), 
				0 );
	}

	protected boolean isSupportedBySolidBlocks(World world, int i, int j, int k)
	{
		Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];

		return blockBelow != null && blockBelow.hasLargeCenterHardPointToFacing( world, i, j - 1, k, 1, false );
	}

	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconEmbers;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
	{
		super.registerIcons( register );

		iconEmbers = register.registerIcon("fcOverlayLogEmbers");
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
	{
		renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
				renderer.blockAccess, i, j, k) );

		return renderer.renderBlockLog( this, i, j, k );
	}


    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult)
	{
		if ( bFirstPassResult )
		{
			RenderUtils.renderBlockFullBrightWithTexture(renderBlocks, renderBlocks.blockAccess, i, j, k, iconEmbers);
		}
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
	{
		renderBlocks.renderBlockAsItemVanilla( this, iItemDamage, fBrightness );

		RenderUtils.renderInvBlockFullBrightWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, iconEmbers);
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void renderFallingBlock(RenderBlocks renderBlocks, int i, int j, int k, int iMetadata)
	{
		renderBlocks.setRenderAllFaces(true);

		renderBlocks.setRenderBounds(getFixedBlockBoundsFromPool());

		renderBlocks.renderStandardBlock( this, i, j, k );

		RenderUtils.renderBlockFullBrightWithTexture(renderBlocks, renderBlocks.blockAccess, i, j, k, iconEmbers);

		renderBlocks.setRenderAllFaces(false);
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random rand )
	{
		emitSmokeParticles(world, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, rand, getBurnLevel(world, i, j, k));

		if ( rand.nextInt( 24 ) == 0 )
		{
			float fVolume = 0.1F + rand.nextFloat() * 0.1F;

			world.playSound( i + 0.5D, j + 0.5D, k + 0.5D, "fire.fire", 
					fVolume, rand.nextFloat() * 0.7F + 0.3F, false );
		}	        
	}
}
