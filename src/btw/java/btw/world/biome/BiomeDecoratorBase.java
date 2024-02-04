package btw.world.biome;

import net.minecraft.src.World;

import java.util.Random;

public interface BiomeDecoratorBase {
	public void decorate(World world, Random rand, int x, int z);
}
