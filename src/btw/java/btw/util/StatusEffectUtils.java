package btw.util;

public class StatusEffectUtils {
	public enum HealthStatus {
		HEALTHY(0, ""),
		HURT(1, "hurt"),
		INJURED(2, "injured"),
		WOUNDED(3, "wounded"),
		CRIPPLED(4, true,"crippled"),
		DYING(5, true, true, "dying");
		
		public final int level;
		private final boolean preventsJumping;
		private final boolean blindsPlayer;
		private final String unlocalizedName;
		
		HealthStatus(int level, String unlocalizedName) {
			this(level, false, unlocalizedName);
		}
		
		HealthStatus(int level, boolean preventsJumping, String unlocalizedName) {
			this(level, preventsJumping, false, unlocalizedName);
		}
		
		HealthStatus(int level, boolean preventsJumping, boolean blindsPlayer, String unlocalizedName) {
			this.level = level;
			this.preventsJumping = preventsJumping;
			this.blindsPlayer = blindsPlayer;
			this.unlocalizedName = unlocalizedName;
		}
		
		public String getUnlocalizedName() {
			return "status.health." + this.unlocalizedName;
		}
		
		public boolean preventsJumping() {
			return preventsJumping;
		}
		
		public boolean blindsPlayer() {
			return blindsPlayer;
		}
	}
	
	public enum HungerStatus {
		FULL(0, ""),
		PECKISH(1, "peckish"),
		HUNGRY(2, "hungry"),
		FAMISHED(3, "famished"),
		EMACIATED(4, true,"emaciated"),
		STARVING(5, true, true, "starving");
		
		public final int level;
		private final boolean preventsJumping;
		private final boolean nauseatesPlayer;
		private final String unlocalizedName;
		
		HungerStatus(int level, String unlocalizedName) {
			this(level, false, unlocalizedName);
		}
		
		HungerStatus(int level, boolean preventsJumping, String unlocalizedName) {
			this(level, preventsJumping, false, unlocalizedName);
		}
		
		HungerStatus(int level, boolean preventsJumping, boolean nauseatesPlayer, String unlocalizedName) {
			this.level = level;
			this.preventsJumping = preventsJumping;
			this.nauseatesPlayer = nauseatesPlayer;
			this.unlocalizedName = unlocalizedName;
		}
		
		public String getUnlocalizedName() {
			return "status.hunger." + this.unlocalizedName;
		}
		
		public boolean preventsJumping() {
			return preventsJumping;
		}
		
		public boolean nauseatesPlayer() {
			return nauseatesPlayer;
		}
	}
	
	public enum FatStatus {
		SKINNY(0, ""),
		PLUMP(1, "plump"),
		CHUBBY(2, "chubby"),
		FAT(3, "fat"),
		OBESE(4, true,"obese");
		
		public final int level;
		private final boolean preventsJumping;
		private final String unlocalizedName;
		
		FatStatus(int level, String unlocalizedName) {
			this(level, false, unlocalizedName);
		}
		
		FatStatus(int level, boolean preventsJumping, String unlocalizedName) {
			this.level = level;
			this.preventsJumping = preventsJumping;
			this.unlocalizedName = unlocalizedName;
		}
		
		public String getUnlocalizedName() {
			return "status.fat." + this.unlocalizedName;
		}
		
		public boolean preventsJumping() {
			return preventsJumping;
		}
	}
}
