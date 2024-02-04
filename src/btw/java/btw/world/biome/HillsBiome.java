// FCMOD

package btw.world.biome;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

import java.util.Random;

public class HillsBiome extends BiomeGenBase
{
	private static final int NUM_SILVERFISH_CLUSTERS_PER_CHUNK = 7;
	private static final int NUM_SILVERFISH_BLOCKS_PER_CLUSTER = 8;

	protected WorldGenerator generatorSilverfish;
	protected WorldGenerator generatorSilverfishSecondStrata;
	protected WorldGenerator generatorSilverfishThirdStrata;

	public boolean hasSilverfishBeenInit = false;

	public HillsBiome(int iBiomeID)
	{
		super( iBiomeID );
	}

	public void decorate(World world, Random rand, int iChunkX, int iChunkZ )
	{
		super.decorate( world, rand, iChunkX, iChunkZ );

		if (!hasSilverfishBeenInit)
			initSilverfish();
		
		addEmeralds(world, rand, iChunkX, iChunkZ);

		addSilverfishBlocks(world, rand, iChunkX, iChunkZ);
	}

	public void addEmeralds(World world, Random rand, int iChunkX, int iChunkZ)
	{
		int iNumEmeralds = 3 + rand.nextInt( 6 );

		for ( int iTempCount = 0; iTempCount < iNumEmeralds; iTempCount++ )
		{
			int iTempI = iChunkX + rand.nextInt( 16 );
			int iTempJ = rand.nextInt( 28 ) + 4;
			int iTempK = iChunkZ + rand.nextInt( 16 );

			if ( world.getBlockId( iTempI, iTempJ, iTempK ) == Block.stone.blockID )
			{
				int iMetadata = 0;

				if ( iTempJ <= 48 + world.rand.nextInt( 2 ) )
				{
					int iStrataLevel = 1;

					if ( iTempJ <= 24 + world.rand.nextInt( 2 ) )
					{
						iStrataLevel = 2;                                				
					}

					iMetadata = Block.oreEmerald.getMetadataConversionForStrataLevel(
							iStrataLevel, 0);
				}

				world.setBlock( iTempI, iTempJ, iTempK, Block.oreEmerald.blockID, iMetadata, 2 );
			}
		}
	}

	public void addSilverfishBlocks(World world, Random rand, int iChunkX, int iChunkZ)
	{
		for (int iTempCount = 0; iTempCount < NUM_SILVERFISH_CLUSTERS_PER_CHUNK; iTempCount++ )
		{
			int iTempI = iChunkX + rand.nextInt( 16 );
			int iTempJ = rand.nextInt( 64 );
			int iTempK = iChunkZ + rand.nextInt( 16 );

			if ( iTempJ <= 48 + world.rand.nextInt( 2 ) )
			{
				if ( iTempJ <= 24 + world.rand.nextInt( 2 ) )
				{
					generatorSilverfishThirdStrata.generate(world, rand, iTempI, iTempJ, iTempK);
				}
				generatorSilverfishSecondStrata.generate(world, rand, iTempI, iTempJ, iTempK);
			}          
			generatorSilverfish.generate(world, rand, iTempI, iTempJ, iTempK);
		}
	}

	public void initSilverfish() {
		generatorSilverfish = new WorldGenMinable(BTWBlocks.infestedStone.blockID, NUM_SILVERFISH_BLOCKS_PER_CLUSTER);
		generatorSilverfishSecondStrata = new WorldGenMinable(BTWBlocks.infestedMidStrataStone.blockID, NUM_SILVERFISH_BLOCKS_PER_CLUSTER);
		generatorSilverfishThirdStrata = new WorldGenMinable(BTWBlocks.infestedDeepStrataStone.blockID, NUM_SILVERFISH_BLOCKS_PER_CLUSTER);
		
		this.hasSilverfishBeenInit = true;
	}
}