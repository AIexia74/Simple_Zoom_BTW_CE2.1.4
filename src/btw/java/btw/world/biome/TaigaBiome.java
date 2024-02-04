// FCMOD

package btw.world.biome;

import btw.entity.mob.CowEntity;
import btw.entity.mob.PigEntity;
import btw.entity.mob.SheepEntity;
import btw.entity.mob.WolfEntity;
import net.minecraft.src.BiomeGenTaiga;
import net.minecraft.src.SpawnListEntry;

public class TaigaBiome extends BiomeGenTaiga
{
    public TaigaBiome(int iBiomeID )
    {
        super( iBiomeID );
        
        spawnableCreatureList.clear();
        
        spawnableCreatureList.add( new SpawnListEntry( SheepEntity.class, 12, 4, 4 ) );
        spawnableCreatureList.add( new SpawnListEntry( PigEntity.class, 10, 4, 4 ) );
        spawnableCreatureList.add( new SpawnListEntry( CowEntity.class, 8, 4, 4 ) );
        
        spawnableCreatureList.add( new SpawnListEntry( WolfEntity.class, 8, 4, 4 ) );
    }
}
