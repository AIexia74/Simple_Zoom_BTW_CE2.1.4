package btw.util.status;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.PotionEffect;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StatusEffectBuilder {
	public static final int STANDARD_POTION_EFFECT_LENGTH = 80; // Measured in ticks
	
	private final int level;
	private final StatusCategory category;
	
	private float effectivenessMultiplier = 1;
	private boolean affectsMovement = false;
	private boolean affectsMiningSpeed = false;
	private boolean affectsAttackDamage = false;
	
	private boolean areEffectsMultiplicative = false;
	
	private boolean preventsSprinting = false;
	private boolean preventsJumping = false;
	
	private Supplier<Optional<PotionEffect>> effectSupplier = Optional::empty;
	
	private Predicate<EntityPlayer> evaluator = player -> false;
	
	private String unlocalizedCategory = "";
	private String unlocalizedName = "";
	
	StatusEffectBuilder(int level, StatusCategory category) {
		this.level = level;
		this.category = category;
	}
	
	public StatusEffectBuilder setEffectivenessMultiplier(float effectivenessMultiplier) {
		this.effectivenessMultiplier = effectivenessMultiplier;
		return this;
	}
	
	public StatusEffectBuilder setAffectsMovement() {
		this.affectsMovement = true;
		return this;
	}
	
	public StatusEffectBuilder setAffectsMiningSpeed() {
		this.affectsMiningSpeed = true;
		return this;
	}
	
	public StatusEffectBuilder setAffectsAttackDamage() {
		this.affectsAttackDamage = true;
		return this;
	}
	
	public StatusEffectBuilder setEffectsMultiplicative() {
		this.areEffectsMultiplicative = true;
		return this;
	}
	
	public StatusEffectBuilder setPreventsSprinting() {
		this.preventsSprinting = true;
		return this;
	}
	
	public StatusEffectBuilder setPreventsJumping() {
		this.preventsJumping = true;
		return this;
	}
	
	public StatusEffectBuilder setPotionEffect(int potionEffectID, int effectLevel) {
		this.effectSupplier = () -> Optional.of(new PotionEffect(potionEffectID, STANDARD_POTION_EFFECT_LENGTH, effectLevel, true));
		return this;
	}
	
	public StatusEffectBuilder setEvaluator(Predicate<EntityPlayer> evaluator) {
		this.evaluator = evaluator;
		return this;
	}
	
	public StatusEffectBuilder setUnlocalizedName(String category, String name) {
		this.unlocalizedCategory = category;
		this.unlocalizedName = name;
		return this;
	}
	
	public StatusEffect build() {
		StatusEffect statusEffect = new StatusEffect();
		
		statusEffect.level = this.level;
		statusEffect.category = this.category;
		
		statusEffect.effectivenessMultiplier = this.effectivenessMultiplier;
		statusEffect.affectsMovement = this.affectsMovement;
		statusEffect.affectsMiningSpeed = this.affectsMiningSpeed;
		statusEffect.affectsAttackDamage = this.affectsAttackDamage;
		
		statusEffect.areEffectsMultiplicative = this.areEffectsMultiplicative;
		
		statusEffect.preventsSprinting = this.preventsSprinting;
		statusEffect.preventsJumping = this.preventsJumping;
		
		statusEffect.effectSupplier = this.effectSupplier;
		
		if (this.evaluator != null) {
			statusEffect.evaluator = this.evaluator;
		}
		else {
			throw new IllegalStateException("StatusEffect cannot be built without an evaluator");
		}
		
		statusEffect.unlocalizedCategory = this.unlocalizedCategory;
		statusEffect.unlocalizedName = this.unlocalizedName;
		
		PlayerStatusEffects.STATUS_EFFECT_LIST.computeIfAbsent(this.category, k -> new HashMap<>());
		PlayerStatusEffects.STATUS_EFFECT_LIST.get(this.category).put(this.level, statusEffect);
		return statusEffect;
	}
}
