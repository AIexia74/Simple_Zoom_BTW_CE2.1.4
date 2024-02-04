package net.minecraft.src;

import btw.block.BTWBlocks;

import java.util.ArrayList;
import java.util.Random;

public class WorldGenReed extends WorldGenerator
{
	private static ArrayList<BiomeGenBase> validBiomeList = new ArrayList();
	
	public static boolean isBiomeValid(BiomeGenBase biome) {
		return validBiomeList.contains(biome);
	}
	
	public static void addBiomeToGenerator(BiomeGenBase biome) {
		validBiomeList.add(biome);
	}
	
    public boolean generate(World par1World, Random par2Random, int x, int y, int z)
    {
    	// FCMOD: Added
    	BiomeGenBase currentBiome = par1World.getBiomeGenForCoords( x, z );
    	
        boolean isValidBiome = isBiomeValid(currentBiome);
        // END FCMOD
        
        for (int var6 = 0; var6 < 20; ++var6)
        {
            int plantX = x + par2Random.nextInt(4) - par2Random.nextInt(4);
            int plantY = y;
            int plantZ = z + par2Random.nextInt(4) - par2Random.nextInt(4);

            if (par1World.isAirBlock(plantX, y, plantZ) && (par1World.getBlockMaterial(plantX - 1, y - 1, plantZ) == Material.water || par1World.getBlockMaterial(plantX + 1, y - 1, plantZ) == Material.water || par1World.getBlockMaterial(plantX, y - 1, plantZ - 1) == Material.water || par1World.getBlockMaterial(plantX, y - 1, plantZ + 1) == Material.water))
            {
                int var10 = 2 + par2Random.nextInt(par2Random.nextInt(3) + 1);
                
                // FCMOD: Added
                if ( !isValidBiome )
                {
                	// must occur after all random number generation to avoid messing up world gen
                	
                	continue;
                }

                for (int i = 0; i < var10; ++i)
                {
                	Block reedBlock;
                	
                	if (i == 0) {
                		reedBlock = BTWBlocks.sugarCaneRoots;
                	}
                	else {
                		reedBlock = BTWBlocks.sugarCane;
                	}
                	
                    if (reedBlock.canBlockStay(par1World, plantX, plantY + i, plantZ)) {
                        par1World.setBlock(plantX, plantY + i, plantZ, reedBlock.blockID, 0, 2);
                    }
                }
                // END FCMOD
            }
        }

        return true;
    }
}
