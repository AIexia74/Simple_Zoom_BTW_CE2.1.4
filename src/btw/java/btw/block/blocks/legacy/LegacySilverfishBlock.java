package btw.block.blocks.legacy;

import btw.client.fx.BTWEffectManager;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class LegacySilverfishBlock extends BlockSilverfish
{
	private static final int HATCH_FREQUENCY = 1200;

	public LegacySilverfishBlock(int iBlockID )
	{
		super( iBlockID );

		setHardness( 1.5F );

		setPicksEffectiveOn();
		setChiselsEffectiveOn();

		setTickRandomly( true );
		
		setCreativeTab(null);

		setUnlocalizedName( "monsterStoneEgg" );
	}

	@Override
	public void randomUpdateTick(World world, int i, int j, int k, Random rand)
	{
		if ( world.provider.dimensionId == 1 )
		{
			if (rand.nextInt(HATCH_FREQUENCY) == 0 )
			{
				// block destroy FX

				int iMetadata = world.getBlockMetadata( i, j, k );

				world.playAuxSFX( BTWEffectManager.DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID, i, j, k, blockID + ( iMetadata << 12 ) );

				world.setBlockWithNotify( i, j, k, 0 );

				int iNumSilverfish = 1;

				if ( rand.nextInt( 2 ) == 0 )
				{
					iNumSilverfish++;
				}

				for ( int iTempCount = 0; iTempCount < iNumSilverfish; iTempCount++ )
				{
					EntitySilverfish silverfish = (EntitySilverfish) EntityList.createEntityOfType(EntitySilverfish.class, world);

					silverfish.setLocationAndAngles( (double)i + 0.5D, (double)j, (double)k + 0.5D, 0.0F, 0.0F);

					world.spawnEntityInWorld( silverfish );
				}

				ItemUtils.dropSingleItemAsIfBlockHarvested(world, i, j, k, Block.gravel.blockID, 0);

				ItemUtils.dropSingleItemAsIfBlockHarvested(world, i, j, k, Item.clay.itemID, 0);
			}
		}
	}

	@Override
	protected ItemStack createStackedBlock( int iMetadata )
	{
		Block block = Block.stone;
		int iItemDamage = 0;

		if (iMetadata == 1)
		{
			block = Block.cobblestone;
		}
		else if (iMetadata == 2)
		{
			block = Block.stoneBrick;
		}
		else if ( iMetadata == 14 )
		{
			iItemDamage = 1;
		}
		else if ( iMetadata == 15 )
		{
			iItemDamage = 2;
		}

		return new ItemStack( block, 1, iItemDamage );
	}

	@Override
	public boolean hasStrata()
	{
		return true;
	}

	@Override
	public int getMetadataConversionForStrataLevel(int iLevel, int iMetadata)
	{
		if ( iMetadata == 0 ) // regular stone block
		{
			if ( iLevel == 1 )
			{
				iMetadata = 14;
			}
			else if ( iLevel == 2 )
			{
				iMetadata = 15;
			}
		}

		return iMetadata;
	}

	public static int getMetadataConversionOnInfest(int iBlockID, int iMetadata)
	{
		int iNewMetadata = 0;

		if ( iBlockID == Block.cobblestone.blockID )
		{
			iNewMetadata = 1;
		}
		else if ( iBlockID == Block.stoneBrick.blockID )
		{
			iNewMetadata = 2;
		}
		else if ( iMetadata == 1 )
		{
			iNewMetadata = 14;    		
		}
		else if ( iMetadata == 2 )
		{
			iNewMetadata = 15;    		
		}

		return iNewMetadata;
	}

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random rand )
	{
		if ( rand.nextInt( 32 ) == 0 )
		{
			world.playSound( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, "mob.silverfish.step", 
					rand.nextFloat() * 0.05F + 0.2F, rand.nextFloat() * 1.0F + 0.5F, false );
		}
	}

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
	{
		if ( iMetadata == 1 )
		{
			return Block.cobblestone.getBlockTextureFromSide( iSide );
		}
		else if ( iMetadata == 2 )
		{
			return Block.stoneBrick.getBlockTextureFromSide( iSide );
		}
		else if ( iMetadata == 14 )
		{
			return Block.stone.getIcon( iSide, 1 );
		}
		else if ( iMetadata == 15 )
		{
			return Block.stone.getIcon( iSide, 2 );
		}

		return Block.stone.getBlockTextureFromSide( iSide );
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
	{
		return renderer.renderStandardFullBlock(this, i, j, k);
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean doesItemRenderAsBlock(int iItemDamage)
	{
		return true;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockMovedByPiston(RenderBlocks renderBlocks, int i, int j, int k)
	{
		renderBlocks.renderStandardFullBlockMovedByPiston(this, i, j, k);
	}    
}
