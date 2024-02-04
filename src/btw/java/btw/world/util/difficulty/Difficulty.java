package btw.world.util.difficulty;

import net.minecraft.src.StringTranslate;

public class Difficulty {
	public final String NAME;
	public final int ID;
	
	public Difficulty(String name) {
		this.NAME = name;
		this.ID = Difficulties.DIFFICULTY_LIST.size();
		Difficulties.DIFFICULTY_LIST.add(this);
	}
	
	public String getLocalizedName() {
		return StringTranslate.getInstance().translateKey("difficulty." + this.NAME + ".name");
	}
	
	//------ Block Behaviors ------//
	
	public float getHungerIntensiveActionCostMultiplier() {
		return 1;
	}
	
	public boolean shouldNetherCoalTorchesStartFires() {
		return true;
	}
	
	public float getNoToolBlockHardnessMultiplier() {
		return 1;
	}
	
	//------ Animal Behaviors ------//
	
	public boolean shouldBurningMobsDropCookedMeat() {
		return false;
	}
	
	public float getCowKickStrengthMultiplier() {
		return 1;
	}
	
	public boolean canMilkingStartleCows() {
		return true;
	}
	
	public boolean canAnimalsStarve() {
		return true;
	}
	
	public boolean shouldBlocksStartleAnimals() {
		return true;
	}
	
	//------ Mob Behaviors ------//
	
	public boolean areJungleSpidersHostile() {
		return true;
	}
	
	public boolean shouldReduceJungleSpiderFoodPoisoning() {
		return false;
	}
	
	public boolean shouldSquidsAttackDryPlayers() {
		return true;
	}
	
	public boolean shouldGhastsAngerPigmen() {
		return true;
	}
	
	//------ Player Behaviors ------//
	
	/**
	 * @return Number of subsequent deaths before items dropped on previous deaths are destroyed. -1 means items are never destroyed.
	 */
	public int getDeathCountBeforeItemDestruction() {
		return 1;
	}
	
	public float getHealthRegenDelayMultiplier() {
		return 1;
	}
	
	/**
	 * @return Offsets where status effects due to health, hunger, and fat begin. Positive numbers make effects trigger later, negative trigger earlier
	 */
	public int getStatusEffectOffset() {
		return 0;
	}
	
	/**
	 * @return The gap between where one health, hunger, or fat status effect and the next one apply.
	 */
	public int getStatusEffectStageGap() {
		return 1;
	}
	
	public boolean allowsPlacingBlocksInAir() {
		return false;
	}
	
	//------ World Behaviors ------//
	
	public boolean canWeedsKillPlants() {
		return true;
	}
	
	public boolean shouldLightningStartFires() {
		return true;
	}
	
	public float getAbandonmentRangeMultiplier() {
		return 1;
	}
	
	public boolean shouldHCSRangeIncrease() {
		return true;
	}
}
