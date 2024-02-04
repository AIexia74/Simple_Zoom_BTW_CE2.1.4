package btw.world.util.difficulty;

public class RelaxedDifficulty extends Difficulty {
	public RelaxedDifficulty(String name) {
		super(name);
	}
	
	//------ Block Behaviors ------//
	
	@Override
	public float getHungerIntensiveActionCostMultiplier() {
		return 0.5F;
	}
	
	@Override
	public boolean shouldNetherCoalTorchesStartFires() {
		return false;
	}
	
	@Override
	public float getNoToolBlockHardnessMultiplier() {
		return 0.75F;
	}
	
	//------ Animal Behaviors ------//
	
	@Override
	public boolean shouldBurningMobsDropCookedMeat() {
		return true;
	}
	
	@Override
	public float getCowKickStrengthMultiplier() {
		return 0.5F;
	}
	
	@Override
	public boolean canMilkingStartleCows() {
		return false;
	}
	
	@Override
	public boolean canAnimalsStarve() {
		return false;
	}
	
	@Override
	public boolean shouldBlocksStartleAnimals() {
		return false;
	}
	
	//------ Mob Behaviors ------//
	
	@Override
	public boolean areJungleSpidersHostile() {
		return false;
	}
	
	@Override
	public boolean shouldReduceJungleSpiderFoodPoisoning() {
		return true;
	}
	
	@Override
	public boolean shouldSquidsAttackDryPlayers() {
		return false;
	}
	
	@Override
	public boolean shouldGhastsAngerPigmen() {
		return false;
	}
	
	//------ Player Behaviors ------//
	
	/**
	 * @return Number of subsequent deaths before items dropped on previous deaths are destroyed. -1 means items are never destroyed.
	 */
	@Override
	public int getDeathCountBeforeItemDestruction() {
		return -1;
	}
	
	@Override
	public float getHealthRegenDelayMultiplier() {
		return 0.6F;
	}
	
	@Override
	public int getStatusEffectOffset() {
		return 0;
	}
	
	@Override
	public int getStatusEffectStageGap() {
		return 2;
	}
	
	@Override
	public boolean allowsPlacingBlocksInAir() {
		return true;
	}
	
	//------ World Behaviors ------//
	
	@Override
	public boolean canWeedsKillPlants() {
		return false;
	}
	
	@Override
	public boolean shouldLightningStartFires() {
		return false;
	}
	
	@Override
	public float getAbandonmentRangeMultiplier() {
		return 0.5F;
	}
	
	@Override
	public boolean shouldHCSRangeIncrease() {
		return false;
	}
}
