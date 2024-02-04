// FCMOD

package btw.world.biome;

import btw.entity.mob.GhastEntity;
import btw.entity.mob.MagmaCubeEntity;
import btw.entity.mob.ZombiePigmanEntity;
import net.minecraft.src.BiomeGenHell;
import net.minecraft.src.SpawnListEntry;

public class NetherBiome extends BiomeGenHell
{
    public NetherBiome(int iBiomeID )
    {
        super( iBiomeID );
        
        spawnableMonsterList.clear();
        
        spawnableMonsterList.add( new SpawnListEntry( GhastEntity.class, 50, 4, 4 ) );
        spawnableMonsterList.add( new SpawnListEntry( ZombiePigmanEntity.class, 100, 4, 4 ) );
        spawnableMonsterList.add( new SpawnListEntry( MagmaCubeEntity.class, 1, 4, 4 ) );
    }
}
