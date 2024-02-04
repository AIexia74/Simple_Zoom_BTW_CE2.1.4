// FCMOD

package btw.util.hardcorespawn;

import java.util.ArrayList;

import btw.BTWMod;
import btw.world.util.WorldUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class HardcoreSpawnUtils
{
	//Timer set to 9 minutes so if you die once night falls you respawn in a new location
	public static final int HARDCORE_SPAWN_TIME_BETWEEN_REASSIGNMENTS = 10800;

	private static final double BASE_RADIUS = 2000D;
	private static final double BASE_EXCLUSION_RADIUS = 1000D;
	private static final double RAPID_RESPAWN_RADIUS = 100D;

	private final static double ABANDONED_VILLAGE_RADIUS = 2250D;
	private final static double PARTIALLY_ABADONED_VILLAGE_RADIUS = 3000D;

	private final static double LOOTED_TEMPLE_RADIUS = 2250D;

	private static final double LARGE_BIOMES_MULTIPLIER = 4D;
	
	private static final int SPAWN_ATTEMPT_COUNT = 20;

	public static ArrayList<BiomeGenBase> blacklistedBiomes = new ArrayList();
	
	static {
		blacklistedBiomes.add(BiomeGenBase.jungle);
		blacklistedBiomes.add(BiomeGenBase.jungleHills);
		blacklistedBiomes.add(BiomeGenBase.ocean);
		blacklistedBiomes.add(BiomeGenBase.frozenOcean);
		blacklistedBiomes.add(BiomeGenBase.river);
		blacklistedBiomes.add(BiomeGenBase.beach);
	}

	/** 
	 * player respawn maximum radius from original spawn
	 */
	public static double getPlayerSpawnRadius(World world) {
		return BASE_RADIUS * getWorldTypeRadiusMultiplier(world) * getGameProgressRadiusMultiplier(world);
	}

	/** 
	 * player respawn minimum radius from original spawn
	 */
	public static double getPlayerSpawnExclusionRadius(World world) {
		return BASE_EXCLUSION_RADIUS * getWorldTypeRadiusMultiplier(world);
	}

	/** 
	 * specifies the radius in which a player will respawn, if returning to the same spawn point after multiple deaths
	 */	
	public static double getPlayerMultipleRespawnRadius() {
		return RAPID_RESPAWN_RADIUS;
	}

	public static double getAbandonedVillageRadius(World world) {
		return ABANDONED_VILLAGE_RADIUS * getWorldTypeRadiusMultiplier(world) * getWorldDifficultyRadiusMultiplier(world);
	}

	public static double getPartiallyAbandonedVillageRadius(World world) {
		return PARTIALLY_ABADONED_VILLAGE_RADIUS * getWorldTypeRadiusMultiplier(world) * getWorldDifficultyRadiusMultiplier(world);
	}

	public static double getLootedTempleRadius(World world) {
		return LOOTED_TEMPLE_RADIUS * getWorldTypeRadiusMultiplier(world) * getWorldDifficultyRadiusMultiplier(world);
	}

	public static double getWorldTypeRadiusMultiplier(World world) {
		if (world.worldInfo.getTerrainType() == WorldType.LARGE_BIOMES && BTWMod.increaseLargeBiomeHCS) {
			return LARGE_BIOMES_MULTIPLIER;
		}

		return 1D;
	}
	
	public static double getWorldDifficultyRadiusMultiplier(World world) {
		return world.worldInfo.getDifficulty().getAbandonmentRangeMultiplier();
	}

	public static double getGameProgressRadiusMultiplier(World world) {
		if (!world.worldInfo.getDifficulty().shouldHCSRangeIncrease()) {
			return 1D;
		}
		else if (WorldUtils.gameProgressHasEndDimensionBeenAccessedServerOnly()) {
			return 2.5D;
		}
		else if (WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()) {
			return 2D;
		}
		else if (WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()) {
			return 1.5D;
		}

		return 1D;
	}

	public static void handleHardcoreSpawn(MinecraftServer server, EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer) {
		WorldServer newWorld = server.worldServerForDimension(newPlayer.dimension);

		if (oldPlayer.playerConqueredTheEnd) {
			returnPlayerToOriginalSpawn(newWorld, newPlayer);
			return;
		}

		long overworldTime = WorldUtils.getOverworldTimeServerOnly();
		long timeOfLastPlayerSpawnAssignment = oldPlayer.getTimeOfLastSpawnAssignment();
		long deltaTimeSinceLastRespawnAssignment = overworldTime - timeOfLastPlayerSpawnAssignment; 

		boolean softRespawn = false;

		if (timeOfLastPlayerSpawnAssignment > 0 &&
				deltaTimeSinceLastRespawnAssignment >= 0 &&
			deltaTimeSinceLastRespawnAssignment < HARDCORE_SPAWN_TIME_BETWEEN_REASSIGNMENTS)
		{
			// multiple respawns in a short period of time results in different behavior
			softRespawn = true;

			newPlayer.health = 10; // start the player hurt

			int foodLevel = oldPlayer.foodStats.getFoodLevel();
			foodLevel -= 6; // knock off a 1 pip food penalty
			
			// Limit food decrease so the player cannot spawn below peckish 
			if (foodLevel < 24) {
				foodLevel = 24;
			}

			newPlayer.foodStats.setFoodLevel(foodLevel);            
		}
		if (!softRespawn) {
			//death location only updates when true HC spawn
			newPlayer.sendChatToPlayer(StringTranslate.getInstance().translateKey("message.death.newSpawn"));
			
        	newPlayer.deathCount++;
			
        	newPlayer.lastDeathDimension = oldPlayer.dimension;
        	newPlayer.lastDeathLocationX = MathHelper.floor_double(oldPlayer.posX);
        	newPlayer.lastDeathLocationY = MathHelper.floor_double(oldPlayer.boundingBox.minY);
        	newPlayer.lastDeathLocationZ = MathHelper.floor_double(oldPlayer.posZ);
		}
		else {
			newPlayer.sendChatToPlayer(StringTranslate.getInstance().translateKey("message.death.oldSpawn"));
		}

		if (!WorldUtils.gameProgressHasNetherBeenAccessedServerOnly() || BTWMod.alwaysSpawnTogether) {
			// early game, players are tied to respawning together
			SpawnLocation recentLocation = newWorld.getSpawnLocationList().getMostRecentSpawnLocation();

			if (recentLocation != null) {
				long lDeltaTime = overworldTime - recentLocation.spawnTime;

				if (lDeltaTime > 0 && lDeltaTime < HardcoreSpawnUtils.HARDCORE_SPAWN_TIME_BETWEEN_REASSIGNMENTS) {
					if (assignPlayerToOldSpawnPosWithVariance(newWorld, newPlayer, new ChunkCoordinates(
								recentLocation.posX,
								recentLocation.posY,
								recentLocation.posZ),
															  recentLocation.spawnTime))
					{
						return;
					}
				}
			}
		}

		ChunkCoordinates oldSpawnPos = oldPlayer.hardcoreSpawnChunk;

		// if a day has passed since the last spawn assignment, assign a new one

		if (oldSpawnPos == null || !softRespawn || !assignPlayerToOldSpawnPosWithVariance(newWorld, newPlayer, oldSpawnPos, timeOfLastPlayerSpawnAssignment)) {
			if (!assignNewHardcoreSpawnLocation(newWorld, server, newPlayer)) {
				returnPlayerToOriginalSpawn(newWorld, newPlayer);
				return;
			}
		}

		ChunkCoordinates newSpawnPos = newPlayer.hardcoreSpawnChunk;

		if (newSpawnPos != null) {
			newWorld.getSpawnLocationList().addPointIfNotAlreadyPresent(
					newSpawnPos.posX,
					newSpawnPos.posY,
					newSpawnPos.posZ, newPlayer.getTimeOfLastSpawnAssignment());
		}
	}

	public static boolean assignNewHardcoreSpawnLocation(World world, MinecraftServer server, EntityPlayerMP player) {
		boolean locationFound = false;
		boolean blacklistedLocationFound = false;

		double spawnRadius = HardcoreSpawnUtils.getPlayerSpawnRadius(world);
		double exclusionRadius = HardcoreSpawnUtils.getPlayerSpawnExclusionRadius(world);
		double spawnDeltaRadius = spawnRadius - exclusionRadius;

		double exclusionRadiusSq = exclusionRadius * exclusionRadius;
		double deltaSquaredRadii = (spawnRadius * spawnRadius) - exclusionRadiusSq; 

		for (int attempts = 0; attempts < SPAWN_ATTEMPT_COUNT; attempts++) {
			// distance used formula: dist = sqrt(rnd()*(R2^2-R1^2)+R1^2) to obtain even distribution
			// The shape involved here (2D doughnut) is called an 'Annulus' 
			double spawnDistance = Math.sqrt(world.rand.nextDouble() * deltaSquaredRadii + exclusionRadiusSq);

			double spawnYaw = world.rand.nextDouble() * Math.PI * 2D;

			double xOffset = -Math.sin(spawnYaw) * spawnDistance; 
			double zOffset = Math.cos(spawnYaw) * spawnDistance;

			int newSpawnX = MathHelper.floor_double(xOffset) + world.worldInfo.getSpawnX();
			int newSpawnZ = MathHelper.floor_double(zOffset) + world.worldInfo.getSpawnZ();

			int newSpawnY = world.getTopSolidOrLiquidBlock(newSpawnX, newSpawnZ);
			
			BiomeGenBase respawnBiome = world.getBiomeGenForCoords(newSpawnX, newSpawnZ);
			
			
			boolean isBiomeBlacklisted = blacklistedBiomes.contains(respawnBiome);

			if (newSpawnY >= world.provider.getAverageGroundLevel()) {
				Material targetMaterial = world.getBlockMaterial(newSpawnX, newSpawnY, newSpawnZ);

				if (targetMaterial == null || !targetMaterial.isLiquid()) {
					player.setLocationAndAngles((double)newSpawnX + 0.5D, (double)newSpawnY + 1.5D, (double)newSpawnZ + 0.5D, world.rand.nextFloat() * 360F, 0.0F);

					bumpPlayerPosUpwardsUntilValidSpawnReached(player);

					long overworldTime = WorldUtils.getOverworldTimeServerOnly();

					// Checks for player count of 0 since dead player doesn't count
					if (BTWMod.isSinglePlayerNonLan() || MinecraftServer.getServer().getCurrentPlayerCount() == 0) {
						// set the time to the next morning if this is single player
						overworldTime = ((overworldTime / 24000L) + 1) * 24000L;

						for (int i = 0; i < MinecraftServer.getServer().worldServers.length; ++i) {
							WorldServer tempServer = MinecraftServer.getServer().worldServers[i];

							tempServer.setWorldTime(overworldTime);

							if (tempServer.worldInfo.isThundering()) {
								// remove any storms
								tempServer.worldInfo.setThundering(false);

								server.getConfigurationManager().sendPacketToAllPlayers(new Packet70GameEvent(8, 0));
							}
						}	        	        
					}

					player.setTimeOfLastSpawnAssignment(overworldTime);

					ChunkCoordinates newSpawnPos = new ChunkCoordinates(
							MathHelper.floor_double(player.posX),
							MathHelper.floor_double(player.posY),
							MathHelper.floor_double(player.posZ));

					player.hardcoreSpawnChunk = newSpawnPos;

					// Prefer biomes which are not blacklisted, but if no other biomes are found then spawn the player anyway
					if (isBiomeBlacklisted) {
						blacklistedLocationFound = true;
					}
					else {
						locationFound = true;
						break;
					}
				}
			}
		}

		return locationFound || blacklistedLocationFound;
	}

	private static boolean assignPlayerToOldSpawnPosWithVariance(World world, EntityPlayerMP player, ChunkCoordinates spawnPos, long timeOfLastPlayerSpawnAssignment) {
		for (int i = 0; i < 20; i++) {
			// square root is used on distance to get an even distriubtion of points at any circumference,
			// with more points as you move further from the origin to compensate for increased circumference

			double spawnDistance = Math.sqrt(world.rand.nextDouble()) * getPlayerMultipleRespawnRadius();

			double spawnYaw = world.rand.nextDouble() * Math.PI * 2D;

			double xOffset = -Math.sin(spawnYaw) * spawnDistance; 
			double zOffset = Math.cos(spawnYaw) * spawnDistance;

			int newSpawnX = MathHelper.floor_double(xOffset) + spawnPos.posX;
			int newSpawnZ = MathHelper.floor_double(zOffset) + spawnPos.posZ;

			int newSpawnY = world.getTopSolidOrLiquidBlock(newSpawnX, newSpawnZ);

			if (newSpawnY >= world.provider.getAverageGroundLevel()) {
				Material targetMaterial = world.getBlockMaterial(newSpawnX, newSpawnY, newSpawnZ);

				if (targetMaterial == null || !targetMaterial.isLiquid()) {	        	
					player.setLocationAndAngles(newSpawnX + 0.5D, newSpawnY + 1.5D, newSpawnZ + 0.5D, world.rand.nextFloat() * 360F, 0.0F);

					bumpPlayerPosUpwardsUntilValidSpawnReached(player);

					player.setTimeOfLastSpawnAssignment(timeOfLastPlayerSpawnAssignment);

					player.hardcoreSpawnChunk = spawnPos;

					return true;	        		
				}
			}
		}

		return assignPlayerToOldSpawnPos(world, player, spawnPos, timeOfLastPlayerSpawnAssignment);
	}

	private static boolean assignPlayerToOldSpawnPos(World world, EntityPlayerMP player, ChunkCoordinates spawnPos, long timeOfLastPlayerSpawnAssignment) {
		int spawnX = MathHelper.floor_double(spawnPos.posX);
		int spawnY = MathHelper.floor_double(spawnPos.posZ);
		int spawnZ = world.getTopSolidOrLiquidBlock(spawnX, spawnY);

		player.setLocationAndAngles(spawnX + 0.5F, spawnZ + 1.5F, spawnY + 0.5F, world.rand.nextFloat() * 360F, 0.0F);

		Material targetMaterial = world.getBlockMaterial(spawnX, spawnZ + 1, spawnY);

		if (offsetPlayerPositionUntilValidSpawn(world, player)) {
			bumpPlayerPosUpwardsUntilValidSpawnReached(player);

			player.setTimeOfLastSpawnAssignment(timeOfLastPlayerSpawnAssignment);

			ChunkCoordinates newSpawnPos = new ChunkCoordinates(
					MathHelper.floor_double(player.posX),
					MathHelper.floor_double(player.posY),
					MathHelper.floor_double(player.posZ));

			player.hardcoreSpawnChunk = newSpawnPos;

			return true;
		}

		return false;
	}

	private static boolean offsetPlayerPositionUntilValidSpawn(World world, EntityPlayerMP player) {
		int spawnX = MathHelper.floor_double(player.posX);
		int spawnZ = MathHelper.floor_double(player.posZ);        

		for (int i = 0; i < 20; i++) {
			int spawnY = world.getTopSolidOrLiquidBlock(spawnX, spawnZ);

			Material targetMaterial = world.getBlockMaterial(spawnX, spawnY, spawnZ);

			if (targetMaterial == null || !targetMaterial.isLiquid()) {
				player.setLocationAndAngles((double)spawnX + 0.5D, player.posY, (double)spawnZ + 0.5D, player.rotationYaw, player.rotationPitch);

				return true;
			}

			spawnX += world.rand.nextInt(11) - 5;
			spawnZ += world.rand.nextInt(11) - 5;
		}

		return false;
	}

	private static void returnPlayerToOriginalSpawn(World world, EntityPlayerMP player) {
		ChunkCoordinates spawnPos = world.getSpawnPoint();

		int spawnX = spawnPos.posX;
		int spawnY = spawnPos.posY;
		int spawnZ = spawnPos.posZ;

		if (!world.provider.hasNoSky && world.getWorldInfo().getGameType() != EnumGameType.ADVENTURE) {
			spawnX += world.rand.nextInt(20) - 10;
			spawnY = world.getTopSolidOrLiquidBlock(spawnX, spawnZ);
			spawnZ += world.rand.nextInt(20) - 10;
		}

		player.setLocationAndAngles((double)spawnX + 0.5D, (double)spawnY + 1.5D, (double)spawnZ + 0.5D, 0.0F, 0.0F);

		bumpPlayerPosUpwardsUntilValidSpawnReached(player);

		player.setTimeOfLastSpawnAssignment(0);

		player.hardcoreSpawnChunk = null;
	}

	private static void bumpPlayerPosUpwardsUntilValidSpawnReached(EntityPlayerMP player) {
		do {
			if (player.posY <= 0.0D) {
				break;
			}

			player.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);

			if (player.worldObj.getCollidingBoundingBoxes(player, player.boundingBox).isEmpty()) {
				break;
			}

			player.posY++;
		}
		while (true);
	}

	public static boolean isInLootedTempleRadius(World world, int x, int z) {
		int spawnX = world.getWorldInfo().getSpawnX();
		int spawnZ = world.getWorldInfo().getSpawnZ();

		double deltaX = (double)(spawnX - x);
		double deltaZ = (double)(spawnZ - z);

		double distSqFromSpawn = deltaX * deltaX + deltaZ * deltaZ;    	
		double lootedRadius = getLootedTempleRadius(world);

		return distSqFromSpawn < lootedRadius * lootedRadius;
	}
}
