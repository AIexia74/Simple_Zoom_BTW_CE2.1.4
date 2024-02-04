// FCMOD

package btw.world.biome;

import btw.entity.mob.CowEntity;
import btw.entity.mob.PigEntity;
import btw.entity.mob.SheepEntity;
import net.minecraft.src.BiomeGenSnow;
import net.minecraft.src.SpawnListEntry;

public class SnowBiome extends BiomeGenSnow
{
    public SnowBiome(int iBiomeID )
    {
        super( iBiomeID );

        spawnableCreatureList.clear();

        // no chickens in the snow
        
        spawnableCreatureList.add( new SpawnListEntry( SheepEntity.class, 12, 4, 4 ) );
        spawnableCreatureList.add( new SpawnListEntry( PigEntity.class, 10, 4, 4 ) );
        spawnableCreatureList.add( new SpawnListEntry( CowEntity.class, 8, 4, 4 ) );
    }
}
