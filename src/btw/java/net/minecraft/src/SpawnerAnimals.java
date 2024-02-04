package net.minecraft.src;

import btw.entity.mob.WitchEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class SpawnerAnimals
{
	/**
	 * Given a chunk, find a random position in it.
	 */
	protected static ChunkPosition getRandomSpawningPointInChunk(World par0World, int par1, int par2)
	{
		Chunk var3 = par0World.getChunkFromChunkCoords(par1, par2);
		int var4 = par1 * 16 + par0World.rand.nextInt(16);
		int var5 = par2 * 16 + par0World.rand.nextInt(16);
		int var6 = par0World.rand.nextInt(var3 == null ? par0World.getActualHeight() : var3.getTopFilledSegment() + 16 - 1);
		return new ChunkPosition(var4, var6, var5);
	}

	/**
	 * adds all chunks within the spawn radius of the players to eligibleChunksForSpawning. pars: the world,
	 * hostileCreatures, passiveCreatures. returns number of eligible chunks.
	 */
	public static final int findChunksForSpawning(WorldServer par0WorldServer, boolean par1, boolean par2, boolean par3)
	{
		if (!par1 && !par2)
		{
			return 0;
		}
		else
		{
			LinkedList<ChunkCoordIntPair> activeChunks = par0WorldServer.getActiveChunksCoordsList();

			int var4;
			int var7;

			var4 = 0;
			EnumCreatureType[] var33 = EnumCreatureType.values();
			var7 = var33.length;

			for (int var34 = 0; var34 < var7; ++var34)
			{
				EnumCreatureType var35 = var33[var34];

				if ( ( !var35.getPeacefulCreature() || par2 ) &&
						( var35.getPeacefulCreature() || par1 ) && 
						( !var35.getAnimal() || par3 ) &&
					 par0WorldServer.countEntitiesThatApplyToSpawnCap(var35.getCreatureClass()) <=
						var35.getMaxNumberOfCreature() * activeChunks.size() / 256 )
				{
					Iterator var37 = activeChunks.iterator();
					label110:

						while (var37.hasNext())
						{
							ChunkCoordIntPair var36 = (ChunkCoordIntPair)var37.next();

							{
								ChunkPosition var38 = getRandomSpawningPointInChunk(par0WorldServer, var36.chunkXPos, var36.chunkZPos);
								int var13 = var38.x;
								int var14 = var38.y;
								int var15 = var38.z;

								if (!par0WorldServer.isBlockNormalCube(var13, var14, var15) &&
									canCreatureTypeSpawnInMaterial(var35,
																   par0WorldServer.getBlockMaterial( var13, var14, var15 )) )
								{
									int var16 = 0;
									int var17 = 0;

									while (var17 < 3)
									{
										int var18 = var13;
										int var19 = var14;
										int var20 = var15;
										byte var21 = 6;
										SpawnListEntry var22 = null;
										int var23 = 0;

										while (true)
										{
											if (var23 < 4)
											{
												label103:
												{
												var18 += par0WorldServer.rand.nextInt(var21) - par0WorldServer.rand.nextInt(var21);
												// FCNOTE: The following results in 0 all the time.  
												// Bug?  Check if leaves world bounds if fix?
												var19 += par0WorldServer.rand.nextInt(1) - par0WorldServer.rand.nextInt(1);
												var20 += par0WorldServer.rand.nextInt(var21) - par0WorldServer.rand.nextInt(var21);

												if (canCreatureTypeSpawnAtLocation(var35, par0WorldServer, var18, var19, var20))
												{
													float var24 = (float)var18 + 0.5F;
													float var25 = (float)var19;
													// FCMOD: Added
													var25 += getVerticalOffsetForPos(var35, par0WorldServer, var18, var19, var20);
													var25 += 0.01F; // wiggle
													// END FCMOD
													float var26 = (float)var20 + 0.5F;

													if (par0WorldServer.getClosestPlayer((double)var24, (double)var25, (double)var26, 24.0D) == null)
													{
														// FCCHUNK: Decide on updates around original spawn
														// FCMOD: Removed distance check from creature to 
														// original spawn which prevents spawning 
														// within 24 blocks (root of 576)
														{
															if (var22 == null)
															{
																var22 = par0WorldServer.spawnRandomCreature(var35, var18, var19, var20);

																if (var22 == null)
																{
																	break label103;
																}
															}

															EntityLiving var39;

															try
															{
																Class entityClass = var22.entityClass;
																entityClass = EntityList.getRegisteredReplacement(entityClass);
																
																var39 = (EntityLiving)entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {par0WorldServer});
															}
															catch (Exception var31)
															{
																var31.printStackTrace();
																return var4;
															}

															// FCMOD: Added
															var39.preInitCreature();
															// END FCMOD

															var39.setLocationAndAngles((double)var24, (double)var25, (double)var26, par0WorldServer.rand.nextFloat() * 360.0F, 0.0F);

															if (var39.getCanSpawnHere())
															{
																++var16;
																par0WorldServer.spawnEntityInWorld(var39);
																creatureSpecificInit(var39, par0WorldServer, var24, var25, var26);

																if (var16 >= var39.getMaxSpawnedInChunk())
																{
																	continue label110;
																}
															}

															var4 += var16;
														}
													}
												}

												++var23;
												continue;
												}
											}

											++var17;
											break;
										}
									}
								}
							}
						}
				}
			}

			return var4;
		}
	}

	/**
	 * determines if a skeleton spawns on a spider, and if a sheep is a different color
	 */
	private static void creatureSpecificInit(EntityLiving par0EntityLiving, World par1World, float par2, float par3, float par4)
	{
		par0EntityLiving.initCreature();
	}

	/**
	 * Called during chunk generation to spawn initial creatures.
	 */
	public static void performWorldGenSpawning(World par0World, BiomeGenBase par1BiomeGenBase, int par2, int par3, int par4, int par5, Random par6Random)
	{
		List var7 = par1BiomeGenBase.getSpawnableList(EnumCreatureType.creature);

		if (!var7.isEmpty())
		{
			while (par6Random.nextFloat() < par1BiomeGenBase.getSpawningChance())
			{
				SpawnListEntry var8 = (SpawnListEntry)WeightedRandom.getRandomItem(par0World.rand, var7);
				int var9 = var8.minGroupCount + par6Random.nextInt(1 + var8.maxGroupCount - var8.minGroupCount);
				int var10 = par2 + par6Random.nextInt(par4);
				int var11 = par3 + par6Random.nextInt(par5);
				int var12 = var10;
				int var13 = var11;

				for (int var14 = 0; var14 < var9; ++var14)
				{
					boolean var15 = false;

					for (int var16 = 0; !var15 && var16 < 4; ++var16)
					{
						int var17 = par0World.getTopSolidOrLiquidBlock(var10, var11);

						if (checkIfAnimalCanSpawn(var8, par0World, var10, var17, var11))
						{
							float var18 = (float)var10 + 0.5F;
							float var19 = (float)var17;
							float var20 = (float)var11 + 0.5F;
							EntityLiving var21;

							try
							{
								Class entityClass = var8.entityClass;
								entityClass = EntityList.getRegisteredReplacement(entityClass);
								
								var21 = (EntityLiving)var8.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {par0World});
							}
							catch (Exception var23)
							{
								var23.printStackTrace();
								continue;
							}

							// FCMOD: Added
							var21.preInitCreature();
							// END FCMOD
							var21.setLocationAndAngles((double)var18, (double)var19, (double)var20, par6Random.nextFloat() * 360.0F, 0.0F);
							par0World.spawnEntityInWorld(var21);
							creatureSpecificInit(var21, par0World, var18, var19, var20);
							var15 = true;
						}

						var10 += par6Random.nextInt(5) - par6Random.nextInt(5);

						for (var11 += par6Random.nextInt(5) - par6Random.nextInt(5); var10 < par2 || var10 >= par2 + par4 || var11 < par3 || var11 >= par3 + par4; var11 = var13 + par6Random.nextInt(5) - par6Random.nextInt(5))
						{
							var10 = var12 + par6Random.nextInt(5) - par6Random.nextInt(5);
						}
					}
				}
			}
		}
	}

	public static boolean checkIfAnimalCanSpawn(SpawnListEntry entry, World world, int x, int y, int z) {
		return canEntitySpawnDuringWorldGen(entry.entityClass, world, x, y, z);
	}

	public static boolean canCreatureTypeSpawnAtLocation( EnumCreatureType type, 
			World world, int i, int j, int k )
	{
		if ( type.getCreatureMaterial() == Material.water )
		{
			return world.getBlockMaterial( i, j, k ).isLiquid() && 
					world.getBlockMaterial( i, j - 1, k ).isLiquid() && 
					!world.isBlockNormalCube( i, j + 1, k );
		}

		if ( !world.isBlockNormalCube( i, j, k ) && 
				!world.getBlockMaterial( i, j, k ).isLiquid() )
		{
			Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];

			return blockBelow != null && blockBelow.canMobsSpawnOn(world, i, j - 1, k);
		}

		return false;
	}

	public static float getVerticalOffsetForPos(EnumCreatureType type,
												World world, int i, int j, int k)
	{
		if ( type.getCreatureMaterial() != Material.water )
		{
			Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];

			if ( blockBelow != null )
			{
				return blockBelow.mobSpawnOnVerticalOffset(world, i, j - 1, k);
			}
		}

		return 0F;
	}

	public static boolean canCreatureTypeSpawnInMaterial(EnumCreatureType type, Material material)
	{
		if ( material == Material.water )
		{
			return type.getCreatureMaterial() == Material.water;
		}
		else
		{
			return type.getCreatureMaterial() != Material.water;
		}
	}
    
    //------ World Gen Spawning Behavior ------//
    private static SpawnBehavior defaultSpawnBehavior;
    private static Map<Class<? extends EntityLiving>, SpawnBehavior> worldGenSpawnBehaviors = new HashMap();
    
    static {
    	defaultSpawnBehavior = new SpawnBehavior() {
			@Override
			public boolean canSpawnAtLocationDuringWorldGen(World world, int x, int y, int z) {
				// modified and trimmed down version of canCreatureTypeSpawnAtLocation() so that animals 
		    	// don't initially spawn buried in leaves and to reduce the overall number of tests 
		    	// performed
		    	
		    	int blockID = world.getBlockId(x, y, z);
		    	
		    	if (!Block.isNormalCube(blockID) && !world.getBlockMaterial(x, y, z).isLiquid() && blockID != Block.leaves.blockID)
		    	{
		    		int blockAboveID = world.getBlockId(x, y + 1, z);
		    		
		    		if (!Block.isNormalCube(blockAboveID) && blockAboveID != Block.leaves.blockID)
		    		{
			            int blockBelowID = world.getBlockId(x, y - 1, z);
			            
			            return Block.isNormalCube(blockBelowID) && blockBelowID != Block.bedrock.blockID;
		    		}
		    	}
		        
		        return false;
			}
		};
		
		setWorldGenSpawnBehavior(WitchEntity.class, new SpawnBehavior() {
			@Override
			public boolean canSpawnAtLocationDuringWorldGen(World world, int x, int y, int z) {
				// modified version of default spawn behavior that allows
				// witches to spawn over water and leaves to maximize chances they'll find a spot
				// around generated witch huts
		    	
		    	int blockID = world.getBlockId(x, y, z);
		    	
		    	if (!Block.isNormalCube(blockID) && !world.getBlockMaterial(x, y, z).isLiquid() && blockID != Block.leaves.blockID)
		    	{
		    		int blockAboveID = world.getBlockId(x, y + 1, z);
		    		
		    		if (!Block.isNormalCube(blockAboveID) && blockAboveID != Block.leaves.blockID)
		    		{
			            int blockBelowID = world.getBlockId(x, y - 1, z);
			            
			            return blockBelowID != Block.bedrock.blockID && (Block.isNormalCube(blockBelowID) || world.getBlockMaterial(x, y - 1, z) == Material.water || blockBelowID == Block.leaves.blockID);
		    		}
		    	}
		        
		        return false;
			}
		});
    }
    
    public static boolean canEntitySpawnDuringWorldGen(Class<? extends EntityLiving> entityClass, World world, int x, int y, int z) {
    	SpawnBehavior spawnBehavior = worldGenSpawnBehaviors.get(entityClass);
    	
    	if (spawnBehavior != null) {
    		return spawnBehavior.canSpawnAtLocationDuringWorldGen(world, x, y, z);
    	}
    	else {
    		return defaultSpawnBehavior.canSpawnAtLocationDuringWorldGen(world, x, y, z);
    	}
    }
    
    public static void setWorldGenSpawnBehavior(Class<? extends EntityLiving> entityClass, SpawnBehavior spawnBehavior) {
    	worldGenSpawnBehaviors.put(entityClass, spawnBehavior);
    }
    
    public static void removeCustomWorldGenSpawnBehavior(Class<? extends EntityLiving> entityClass) {
    	worldGenSpawnBehaviors.remove(entityClass);
    }
	
	public static abstract class SpawnBehavior {
		public abstract boolean canSpawnAtLocationDuringWorldGen(World world, int x, int y, int z);
	}
	
	// END FCMOD
}