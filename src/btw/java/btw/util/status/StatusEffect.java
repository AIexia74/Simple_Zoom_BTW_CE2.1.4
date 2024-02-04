package btw.util.status;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.PotionEffect;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StatusEffect {
	protected int level;
	protected StatusCategory category;
	
	protected float effectivenessMultiplier = 1;
	protected boolean affectsMovement;
	protected boolean affectsMiningSpeed;
	protected boolean affectsAttackDamage;
	
	protected boolean areEffectsMultiplicative;
	
	protected boolean preventsSprinting;
	protected boolean preventsJumping;
	
	protected Supplier<Optional<PotionEffect>> effectSupplier;
	
	protected Predicate<EntityPlayer> evaluator;
	
	protected String unlocalizedCategory;
	protected String unlocalizedName;
	
	protected StatusEffect() {}
	
	public int getLevel() {
		return this.level;
	}
	
	public StatusCategory getCategory() {
		return this.category;
	}
	
	public float getEffectivenessMultiplier() {
		return this.effectivenessMultiplier;
	}
	
	public boolean affectsMovement() {
		return this.affectsMovement;
	}
	
	public boolean affectsMiningSpeed() {
		return this.affectsMiningSpeed;
	}
	
	public boolean affectsAttackDamage() {
		return this.affectsAttackDamage;
	}
	
	public boolean areEffectsMultiplicative() {
		return this.areEffectsMultiplicative;
	}
	
	public boolean preventsSprinting() {
		return this.preventsSprinting;
	}
	
	public boolean preventsJumping() {
		return this.preventsJumping;
	}
	
	public Optional<PotionEffect> getPotionEffect() {
		return this.effectSupplier.get();
	}
	
	public boolean test(EntityPlayer player) {
		return this.evaluator.test(player);
	}
	
	public boolean isActive(EntityPlayer player) {
		return this.evaluator.test(player);
	}
	
	public String getUnlocalizedName() {
		if (this.unlocalizedName.equals("")) {
			return "status.none";
		}
		else {
			return "status." + this.unlocalizedCategory + "." + this.unlocalizedName;
		}
	}
}
