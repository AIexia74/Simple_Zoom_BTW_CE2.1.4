// FCMOD

package btw.world.biome;

import btw.entity.mob.*;
import net.minecraft.src.BiomeGenJungle;
import net.minecraft.src.SpawnListEntry;
import net.minecraft.src.World;

import java.util.Random;

public class JungleBiome extends BiomeGenJungle
{
	private static final int EXTRA_REEDS_PER_CHUNK = 100;
	
    public JungleBiome(int iBiomeID )
    {
        super( iBiomeID );
        
        spawnableCreatureList.clear();
        
        spawnableCreatureList.add( new SpawnListEntry( ChickenEntity.class, 10, 4, 4 ) );
        spawnableCreatureList.add( new SpawnListEntry( PigEntity.class, 10, 4, 4 ) );
        spawnableCreatureList.add( new SpawnListEntry( ChickenEntity.class, 10, 4, 4 ) );

        spawnableMonsterList.clear();
        
        spawnableMonsterList.add( new SpawnListEntry( JungleSpiderEntity.class, 2, 1, 1 ) );
        spawnableMonsterList.add( new SpawnListEntry( SpiderEntity.class, 10, 4, 4 ) );
        spawnableMonsterList.add( new SpawnListEntry( ZombieEntity.class, 10, 4, 4 ) );
        spawnableMonsterList.add( new SpawnListEntry( SkeletonEntity.class, 10, 4, 4 ) );
        spawnableMonsterList.add( new SpawnListEntry( CreeperEntity.class, 10, 4, 4 ) );
        spawnableMonsterList.add( new SpawnListEntry( SlimeEntity.class, 10, 4, 4 ) );
        spawnableMonsterList.add( new SpawnListEntry( EndermanEntity.class, 1, 1, 4 ) );
        spawnableMonsterList.add( new SpawnListEntry( OcelotEntity.class, 2, 1, 1 ) );
    }
    
    @Override
    public void decorate(World world, Random rand, int iChunkX, int iChunkZ )
    {
        super.decorate( world, rand, iChunkX, iChunkZ );
        
        // separate reed generation after other decorations to avoid messing with other random elements
        
        for (int iTempCount = 0; iTempCount < EXTRA_REEDS_PER_CHUNK; ++iTempCount )
        {
            int iXGen = iChunkX + rand.nextInt( 16 ) + 8;
            int iZGen = iChunkZ + rand.nextInt( 16 ) + 8;
            
            int iYGen = rand.nextInt( 128 );
            
            theBiomeDecorator.reedGen.generate( world, rand, iXGen, iYGen, iZGen);
        }
    }
}