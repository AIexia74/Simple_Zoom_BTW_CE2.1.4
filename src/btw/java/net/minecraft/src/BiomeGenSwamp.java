package net.minecraft.src;

import btw.entity.mob.ChickenEntity;
import btw.entity.mob.PigEntity;
import btw.entity.mob.SlimeEntity;
import btw.entity.mob.WitchEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Random;

public class BiomeGenSwamp extends BiomeGenBase
{
    protected BiomeGenSwamp(int par1)
    {
        super(par1);
        this.theBiomeDecorator.treesPerChunk = 2;
        this.theBiomeDecorator.flowersPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 1;
        this.theBiomeDecorator.mushroomsPerChunk = 8;
        this.theBiomeDecorator.reedsPerChunk = 10;
        this.theBiomeDecorator.clayPerChunk = 1;
        this.theBiomeDecorator.waterlilyPerChunk = 4;
        this.waterColorMultiplier = 14745518;
        
        spawnableMonsterList.add( new SpawnListEntry( SlimeEntity.class, 1, 1, 1 ) );
        spawnableMonsterList.add( new SpawnListEntry( WitchEntity.class, 1, 1, 1 ) );
        
        // get rid of cows and sheep and cause other animals to spawn in lower numbers

        spawnableCreatureList.clear();
        
        spawnableCreatureList.add( new SpawnListEntry( ChickenEntity.class, 10, 2, 2 ) );
        spawnableCreatureList.add( new SpawnListEntry( PigEntity.class, 10, 2, 2 ) );
    }

    /**
     * Gets a WorldGen appropriate for this biome.
     */
    public WorldGenerator getRandomWorldGenForTrees(Random par1Random)
    {
        return this.worldGeneratorSwamp;
    }

    /**
     * Provides the basic grass color based on the biome temperature and rainfall
     */
    @Environment(EnvType.CLIENT)
    public int getBiomeGrassColor()
    {
        double var1 = (double)this.getFloatTemperature();
        double var3 = (double)this.getFloatRainfall();
        return ((ColorizerGrass.getGrassColor(var1, var3) & 16711422) + 5115470) / 2;
    }

    /**
     * Provides the basic foliage color based on the biome temperature and rainfall
     */
    @Environment(EnvType.CLIENT)
    public int getBiomeFoliageColor()
    {
        double var1 = (double)this.getFloatTemperature();
        double var3 = (double)this.getFloatRainfall();
        return ((ColorizerFoliage.getFoliageColor(var1, var3) & 16711422) + 5115470) / 2;
    }
    
    @Override
    public boolean canSlimesSpawnOnSurface() {
    	return true;
    }
}
