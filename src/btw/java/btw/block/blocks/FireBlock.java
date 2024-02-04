// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.texture.FireTexture;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class FireBlock extends BlockFire
{		
	public FireBlock(int iBlockID)
	{
		super( iBlockID );
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random )
	{
		if ( !canPlaceBlockAt( world, i, j, k ) )
		{
			world.setBlockWithNotify( i, j, k, 0 );

			return;
		}

		if ( doesBlockBelowExtiguishFire(world, i, j, k) )
		{
			world.setBlockWithNotify( i, j, k, 0);

			return;
		}

		int iBlockBelowID = world.getBlockId( i, j - 1, k );

		boolean bInfiniteBurn = false;

		if ( iBlockBelowID == Block.netherrack.blockID )
		{
			bInfiniteBurn = true;
		}
		else if ( world.provider.dimensionId == 1 && iBlockBelowID == Block.bedrock.blockID )
		{
			bInfiniteBurn = true;
		}
		else if ( hasInfiniteBurnNeighbor(world, i, j, k) )
		{
			bInfiniteBurn = true;
		}

		if ( !bInfiniteBurn && world.isRaining() && 
				(world.isRainingAtPos(i, j, k) || world.isRainingAtPos(i - 1, j, k) || world.isRainingAtPos(i + 1, j, k) ||
				 world.isRainingAtPos(i, j, k - 1) || world.isRainingAtPos(i, j, k + 1) ) )
		{
			world.setBlockWithNotify(i, j, k, 0);
		}
		else
		{
			if (world.getGameRules().getGameRuleBooleanValue("doFireTick")) {
				int iMetadata = world.getBlockMetadata(i, j, k);

				if ( iMetadata < 15 )
				{
					world.setBlockMetadata( i, j, k, iMetadata + random.nextInt( 3 ) / 2 );
				}

				world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) + random.nextInt( 10 ) );

				if ( !bInfiniteBurn && !canNeighborBurn( world, i, j, k ) )
				{
					if ( !world.doesBlockHaveSolidTopSurface( i, j - 1, k ) || iMetadata > 3 )
					{
						world.setBlockWithNotify( i, j, k, 0 );
					}
				}
				else if ( !bInfiniteBurn && !canBlockCatchFire( world, i, j - 1, k ) && iMetadata == 15 && random.nextInt(4) == 0 )
				{
					world.setBlockWithNotify(i, j, k, 0);
				}
				else
				{
					boolean bHighHumidity = world.isBlockHighHumidity(i, j, k);

					byte bDestroyModifier = 0;

					if (bHighHumidity)
					{
						bDestroyModifier = -50;
					}

					tryToDestroyBlockWithFire(world, i + 1, j, k, 300 + bDestroyModifier, random, iMetadata);
					tryToDestroyBlockWithFire(world, i - 1, j, k, 300 + bDestroyModifier, random, iMetadata);
					tryToDestroyBlockWithFire(world, i, j - 1, k, 250 + bDestroyModifier, random, iMetadata);
					tryToDestroyBlockWithFire(world, i, j + 1, k, 250 + bDestroyModifier, random, iMetadata);
					tryToDestroyBlockWithFire(world, i, j, k - 1, 300 + bDestroyModifier, random, iMetadata);
					tryToDestroyBlockWithFire(world, i, j, k + 1, 300 + bDestroyModifier, random, iMetadata);

					checkForFireSpreadFromLocation(world, i, j, k, random, iMetadata);
				}
			}
		}
	}

	@Override
	public boolean canBlockCatchFire(IBlockAccess blockAccess, int i, int j, int k )
	{
		// FCTODO: This should have a hook
		// special case this subblock for the time being so fire will display on it

		int iBlockID = blockAccess.getBlockId( i, j, k );

		if ( iBlockID == BTWBlocks.aestheticOpaque.blockID )
		{
			int iSubtype = blockAccess.getBlockMetadata( i, j, k );

			if ( iSubtype == AestheticOpaqueBlock.SUBTYPE_HELLFIRE)
			{
				return true;
			}
		}

		return super.canBlockCatchFire( blockAccess, i, j, k );
	}


	@Override
	public boolean getDoesFireDamageToEntities(World world, int i, int j, int k)
	{
		return true;
	}

	@Override
	public boolean getCanBlockLightItemOnFire(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}

	@Override
	public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k)
	{	 
		return null; // can't be picked up
	}

	//------------- Class Specific Methods ------------//

	public boolean hasInfiniteBurnNeighbor(World world, int i, int j, int k)
	{
		for ( int iTempFacing = 0; iTempFacing < 6; iTempFacing++ )
		{
			BlockPos targetPos = new BlockPos( i, j, k );

			targetPos.addFacingAsOffset(iTempFacing);

			int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);

			Block targetBlock = Block.blocksList[iTargetBlockID];

			if ( targetBlock != null && targetBlock.doesInfiniteBurnToFacing(world, targetPos.x, targetPos.y, targetPos.z, Block.getOppositeFacing(iTempFacing)) )
			{
				return true;
			}
		}

		return false;
	}

	public boolean doesBlockBelowExtiguishFire(World world, int i, int j, int k)
	{     	
		int iBlockBelowID = world.getBlockId( i, j - 1, k );

		Block blockBelow = Block.blocksList[iBlockBelowID];

		if ( blockBelow != null )
		{
			return blockBelow.doesExtinguishFireAbove(world, i, j - 1, k);
		}

		return false;
	}

	protected void tryToDestroyBlockWithFire(World world, int i, int j, int k, int iChanceToDestroy, Random random, int iSourceMetadata)
	{
		int iAbilityToCatchFire = abilityToCatchFire[world.getBlockId( i, j, k )];

		if ( random.nextInt(iChanceToDestroy) < iAbilityToCatchFire )
		{
			if (world.getGameRules().getGameRuleBooleanValue("doFireTick")) {
				onBlockDestroyedByFire(world, i, j, k, iSourceMetadata, false);
			}
		}
	}

	static protected void onBlockDestroyedByFire(World world, int i, int j, int k, int iFireAge, boolean bForcedFireSpread)
	{
		int iBlockID = world.getBlockId( i, j, k );

		Block block = Block.blocksList[iBlockID];

		if ( block != null )
		{
			block.onDestroyedByFire(world, i, j, k, iFireAge, bForcedFireSpread);
		}    	
	}

	static public void checkForFireSpreadFromLocation(World world, int i, int j, int k, Random rand, int iSourceFireAge)
	{
		if (world.getGameRules().getGameRuleBooleanValue("doFireTick")) {
			boolean bHighHumidity = world.isBlockHighHumidity(i, j, k);

			for ( int iTempI = i - 1; iTempI <= i + 1; ++iTempI )
			{
				for ( int iTempK = k - 1; iTempK <= k + 1; ++iTempK )
				{
					for ( int iTempJ = j - 1; iTempJ <= j + 4; ++iTempJ )
					{
						if ( iTempI != i || iTempJ != j || iTempK != k )
						{
							int iSpreadTopBound = 100;

							if ( iTempJ > j + 1 )
							{
								iSpreadTopBound += ( iTempJ - ( j + 1 ) ) * 100;
							}

							checkForFireSpreadToOneBlockLocation(world, iTempI, iTempJ, iTempK, rand, iSourceFireAge, bHighHumidity, iSpreadTopBound);
						}
					}
				}
			}
		}
	}

	/**
	 * Returns true if there's any flammable material within range
	 */
	static public void checkForSmoulderingSpreadFromLocation(World world, int i, int j, int k)
	{
		if (world.getGameRules().getGameRuleBooleanValue("doFireTick")) {
			boolean bHighHumidity = world.isBlockHighHumidity(i, j, k);

			for ( int iTempI = i - 1; iTempI <= i + 1; ++iTempI )
			{
				for ( int iTempK = k - 1; iTempK <= k + 1; ++iTempK )
				{
					for ( int iTempJ = j; iTempJ <= j + 1; ++iTempJ )
					{
						if ( iTempI != i || iTempJ != j || iTempK != k )
						{
							int iSpreadTopBound = 50; // increased chance in smaller area

							checkForFireSpreadToOneBlockLocation(world, iTempI, iTempJ, iTempK, world.rand,
																 0, bHighHumidity, iSpreadTopBound);
						}
					}
				}
			}
		}
	}

	static public boolean hasFlammableNeighborsWithinSmoulderRange(World world, int i, int j, int k)
	{
		for ( int iTempI = i - 1; iTempI <= i + 1; ++iTempI )
		{
			for ( int iTempK = k - 1; iTempK <= k + 1; ++iTempK )
			{
				for ( int iTempJ = j; iTempJ <= j + 1; ++iTempJ )
				{
					if ( iTempI != i || iTempJ != j || iTempK != k )
					{
						if ( isFlammableOrHasFlammableNeighbors(world, iTempI, iTempJ, iTempK) )
						{
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	static public boolean isFlammableOrHasFlammableNeighbors(World world, int i, int j, int k)
	{
		int iBlockID = world.getBlockId( i, j, k);
		Block block = Block.blocksList[iBlockID];

		if ( abilityToCatchFire[iBlockID] > 0 ||  
				chanceToEncourageFire[iBlockID] > 0 ||
			 ( block != null && block.getCanBeSetOnFireDirectly(world, i, j, k) ) ||
			 (canFireReplaceBlock(world, i, j, k) &&
				 (
								chanceToEncourageFire[world.getBlockId( i - 1, j, k )] > 0 || 				        
								chanceToEncourageFire[world.getBlockId( i + 1, j, k )] > 0 ||				        
								chanceToEncourageFire[world.getBlockId( i, j - 1, k )] > 0 ||				        
								chanceToEncourageFire[world.getBlockId( i, j + 1, k )] > 0 ||				        
								chanceToEncourageFire[world.getBlockId( i, j, k - 1 )] > 0 ||				        
								chanceToEncourageFire[world.getBlockId( i, j, k + 1 )] > 0 
								) ) )				        
		{
			return true;
		}

		return false;       		
	}

	static private void checkForFireSpreadToOneBlockLocation(World world, int iTempI, int iTempJ, int iTempK, Random rand, int iSourceFireAge,
															 boolean bHighHumidity, int iSpreadTopBound)
	{
		if (world.getGameRules().getGameRuleBooleanValue("doFireTick")) {
			int iNeighborChance = getChanceOfNeighborsEncouragingFireCustom(world, iTempI, iTempJ, iTempK);

			if ( iNeighborChance > 0 )
			{
				int iSpreadChance = (iNeighborChance + 61 ) / ( iSourceFireAge + 30 );

				if (bHighHumidity)
				{
					iSpreadChance /= 2;
				}

				if ( iSpreadChance > 0 && rand.nextInt( iSpreadTopBound ) <= iSpreadChance &&
					 ( !world.isRaining() || !world.isRainingAtPos(iTempI, iTempJ, iTempK)) &&
					 !world.isRainingAtPos(iTempI - 1, iTempJ, iTempK) && !world.isRainingAtPos(iTempI + 1, iTempJ, iTempK) &&
					 !world.isRainingAtPos(iTempI, iTempJ, iTempK - 1) && !world.isRainingAtPos(iTempI, iTempJ, iTempK + 1))
				{
					int iStartMetadata = iSourceFireAge + rand.nextInt( 5 ) / 4;

					if (iStartMetadata > 15)
					{
						iStartMetadata = 15;
					}

					if (world.getGameRules().getGameRuleBooleanValue("doFireTick")) {
						if ( canFireReplaceBlock(world, iTempI, iTempJ, iTempK) )
						{
							world.setBlockAndMetadataWithNotify( iTempI, iTempJ, iTempK, Block.fire.blockID, iStartMetadata );
						}
						else
						{
							Block block = Block.blocksList[world.getBlockId( iTempI, iTempJ, iTempK )];

							if ( block != null && block.getCanBeSetOnFireDirectly(world, iTempI, iTempJ, iTempK)  )
							{
								block.setOnFireDirectly(world, iTempI, iTempJ, iTempK);
							}
						}
					}
				}
			}
		}
	}

	static public void checkForFireSpreadAndDestructionToOneBlockLocation(World world, int i, int j, int k)
	{
		checkForFireSpreadAndDestructionToOneBlockLocation(world, i, j, k, world.rand, 0, 100);
	}

	static public void checkForFireSpreadAndDestructionToOneBlockLocation(World world, int i, int j, int k, Random rand, int iSourceFireAge, int iSpreadTopBound)
	{
		if (world.getGameRules().getGameRuleBooleanValue("doFireTick")) {
			int iAbilityToCatchFire = abilityToCatchFire[world.getBlockId( i, j, k )];

			boolean bHighHumidity = world.isBlockHighHumidity( i, j, k );

			int iChanceToDestroy = 250;

			if ( bHighHumidity )
			{
				iChanceToDestroy -=50;
			}

			if ( rand.nextInt(iChanceToDestroy) < iAbilityToCatchFire )
			{
				onBlockDestroyedByFire(world, i, j, k, iSourceFireAge, true);
			}
			else
			{        
				checkForFireSpreadToOneBlockLocation(world, i, j, k, rand, iSourceFireAge, bHighHumidity, iSpreadTopBound);
			}
		}
	}

	protected static int getChanceOfNeighborsEncouragingFireCustom(World world, int i, int j, int k)
	{
		// copied from BlockFire due to it being non-static

		if ( !canFireReplaceBlock(world, i, j, k) )
		{
			Block block = Block.blocksList[world.getBlockId( i, j, k )];

			if ( block != null && block.getCanBeSetOnFireDirectly(world, i, j, k)  )
			{
				return block.getChanceOfFireSpreadingDirectlyTo(world, i, j, k);
			}
			else
			{
				return 0;
			}
		}
		else
		{
			int iChance = getChanceToEncourageFire(world, i + 1, j, k, 0);

			iChance = getChanceToEncourageFire(world, i - 1, j, k, iChance);
			iChance = getChanceToEncourageFire(world, i, j - 1, k, iChance);
			iChance = getChanceToEncourageFire(world, i, j + 1, k, iChance);
			iChance = getChanceToEncourageFire(world, i, j, k - 1, iChance);
			iChance = getChanceToEncourageFire(world, i, j, k + 1, iChance);

			return iChance;
		}
	}

	public static int getChanceToEncourageFire(World par1World, int par2, int par3, int par4, int iPrevChance)
	{
		// just copied from BlockFire due to it being non-static

		int iChance = chanceToEncourageFire[par1World.getBlockId(par2, par3, par4)];

		return iChance > iPrevChance ? iChance : iPrevChance;
	}

	public static boolean canBlockBeDestroyedByFire(int iBlockID)
	{
		return abilityToCatchFire[iBlockID] > 0;
	}

	public static boolean canFireReplaceBlock(World world, int i, int j, int k)
	{
		Block block = Block.blocksList[world.getBlockId( i, j, k )];

		return block == null || world.getGameRules().getGameRuleBooleanValue("doFireTick") && block.getCanBlockBeReplacedByFire(world, i, j, k);
	}

	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] stokedFireTopTextureArray;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
	{
		super.registerIcons( register );

		stokedFireTopTextureArray = new Icon[] {
				register.registerIcon( "fcBlockFireStokedTopStub_0", new FireTexture( "fcBlockFireStokedTopStub_0", 0 ) ),
				register.registerIcon( "fcBlockFireStokedTopStub_1", new FireTexture( "fcBlockFireStokedTopStub_1", 1 ) )
		};
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
	{
		IBlockAccess blockAccess = renderer.blockAccess;

		renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
				renderer.blockAccess, i, j, k) );

		renderer.renderBlockFire( this, i, j, k );

		if ( blockAccess.getBlockId( i, j - 1, k ) == BTWBlocks.stokedFire.blockID )
		{
			Tessellator tessellator = Tessellator.instance;
			Icon texture1 = stokedFireTopTextureArray[0];
			Icon texture2 = stokedFireTopTextureArray[1];

			if ( ( ( i + k ) & 1 ) != 0 )
			{
				texture1 = stokedFireTopTextureArray[1];
				texture2 = stokedFireTopTextureArray[0];
			}

			tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

			tessellator.setBrightness( getMixedBrightnessForBlock(blockAccess, i, j, k ) );

			float fRenderHeight = 1.0F;

			double var18 = (double)i + 0.5D - 0.5D;
			double var20 = (double)i + 0.5D + 0.5D;
			double var22 = (double)k + 0.5D - 0.5D;
			double var24 = (double)k + 0.5D + 0.5D;
			double var26 = (double)i + 0.5D - 0.5D;
			double var28 = (double)i + 0.5D + 0.5D;
			double var30 = (double)k + 0.5D - 0.5D;
			double var32 = (double)k + 0.5D + 0.5D;

			double dMinU = (double)texture2.getMinU();
			double dMinV = (double)texture2.getMinV();
			double dMaxU = (double)texture2.getMaxU();
			double dMaxV = (double)texture2.getMaxV();

			tessellator.addVertexWithUV(var26, (double)((float)j + fRenderHeight), (double)(k + 0), dMinU, dMinV);
			tessellator.addVertexWithUV(var18, (double)(j + 0), (double)(k + 0), dMinU, dMaxV);
			tessellator.addVertexWithUV(var18, (double)(j + 0), (double)(k + 1), dMaxU, dMaxV);
			tessellator.addVertexWithUV(var26, (double)((float)j + fRenderHeight), (double)(k + 1), dMaxU, dMinV);
			tessellator.addVertexWithUV(var28, (double)((float)j + fRenderHeight), (double)(k + 1), dMinU, dMinV);
			tessellator.addVertexWithUV(var20, (double)(j + 0), (double)(k + 1), dMinU, dMaxV);
			tessellator.addVertexWithUV(var20, (double)(j + 0), (double)(k + 0), dMaxU, dMaxV);
			tessellator.addVertexWithUV(var28, (double)((float)j + fRenderHeight), (double)(k + 0), dMaxU, dMinV);

			dMinU = (double)texture1.getMinU();
			dMinV = (double)texture1.getMinV();
			dMaxU = (double)texture1.getMaxU();
			dMaxV = (double)texture1.getMaxV();

			tessellator.addVertexWithUV((double)(i + 0), (double)((float)j + fRenderHeight), var32, dMinU, dMinV);
			tessellator.addVertexWithUV((double)(i + 0), (double)(j + 0), var24, dMinU, dMaxV);
			tessellator.addVertexWithUV((double)(i + 1), (double)(j + 0), var24, dMaxU, dMaxV);
			tessellator.addVertexWithUV((double)(i + 1), (double)((float)j + fRenderHeight), var32, dMaxU, dMinV);
			tessellator.addVertexWithUV((double)(i + 1), (double)((float)j + fRenderHeight), var30, dMinU, dMinV);
			tessellator.addVertexWithUV((double)(i + 1), (double)(j + 0), var22, dMinU, dMaxV);
			tessellator.addVertexWithUV((double)(i + 0), (double)(j + 0), var22, dMaxU, dMaxV);
			tessellator.addVertexWithUV((double)(i + 0), (double)((float)j + fRenderHeight), var30, dMaxU, dMinV);
		}

		return true;    	
	}    
}