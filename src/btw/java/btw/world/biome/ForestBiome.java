// FCMOD

package btw.world.biome;

import btw.entity.mob.*;
import net.minecraft.src.BiomeGenForest;
import net.minecraft.src.SpawnListEntry;

public class ForestBiome extends BiomeGenForest
{
    public ForestBiome(int iBiomeID )
    {
        super( iBiomeID );
        
        spawnableCreatureList.clear();
        
        spawnableCreatureList.add( new SpawnListEntry( SheepEntity.class, 12, 4, 4 ) );
        spawnableCreatureList.add( new SpawnListEntry( PigEntity.class, 10, 4, 4 ) );
        spawnableCreatureList.add( new SpawnListEntry( ChickenEntity.class, 10, 4, 4 ) );
        spawnableCreatureList.add( new SpawnListEntry( CowEntity.class, 8, 4, 4 ) );
        
        spawnableCreatureList.add( new SpawnListEntry( WolfEntity.class, 5, 4, 4 ) );
    }
}
