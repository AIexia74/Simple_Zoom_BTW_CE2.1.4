// FCMOD

package btw.world.biome;

import net.minecraft.src.BiomeGenDesert;

import java.util.Random;

public class DesertBiome extends BiomeGenDesert
{
    public DesertBiome(int iBiomeID )
    {
        super( iBiomeID );
    }

    @Override
    public boolean canLightningStrikeInBiome()
    {
    	// Can't rain in deserts, but lightning can still strike
    	
    	return true;
    }
}