package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

public class StructureScatteredFeatureStart extends StructureStart
{
	private static ArrayList<BiomeGenBase> desertBiomeList = new ArrayList();
	private static ArrayList<BiomeGenBase> jungleBiomeList = new ArrayList();
	private static ArrayList<BiomeGenBase> swampBiomeList = new ArrayList();
	
	public static void addDesertBiome(BiomeGenBase biome) {
		desertBiomeList.add(biome);
	}
	
	public static void addJungleBiome(BiomeGenBase biome) {
		jungleBiomeList.add(biome);
	}
	
	public static void addSwampBiome(BiomeGenBase biome) {
		swampBiomeList.add(biome);
	}
	
	public StructureScatteredFeatureStart(World world, Random rand, int chunkX, int chunkZ)
	{
		BiomeGenBase biome = world.getBiomeGenForCoords(chunkX * 16 + 8, chunkZ * 16 + 8);

		if (swampBiomeList.contains(biome))
		{
			ComponentScatteredFeatureSwampHut var7 = new ComponentScatteredFeatureSwampHut(rand, chunkX * 16, chunkZ * 16);
			this.components.add(var7);
		}
		else if (desertBiomeList.contains(biome))
		{
			ComponentScatteredFeatureDesertPyramid var8 = new ComponentScatteredFeatureDesertPyramid(rand, chunkX * 16, chunkZ * 16);
			this.components.add(var8);
		}
		else if(jungleBiomeList.contains(biome))
		{
			ComponentScatteredFeatureJunglePyramid var6 = new ComponentScatteredFeatureJunglePyramid(rand, chunkX * 16, chunkZ * 16);
			this.components.add(var6);
		}

		this.updateBoundingBox();
	}
}