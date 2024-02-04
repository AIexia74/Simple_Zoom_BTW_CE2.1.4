// FCMOD

package btw.block.util;

public enum Flammability
{
	NONE( 0, 0 ),
	MINIMAL( 5, 5 ),
	MEDIUM( 5, 20 ),
	HIGH( 30, 60 ),
	EXTREME( 60, 100 ),
	QUICKCATCHSLOWBURN( 15, 100 ),
	MEDIUMCATCHQUICKBURN( 30, 20 );
	
	public static final Flammability
		LOGS = MINIMAL,
		PLANKS = MEDIUM,
		LEAVES = HIGH,
		CLOTH = HIGH,
		CROPS = HIGH,
		GRASS = EXTREME,
		WICKER = EXTREME,
		EXPLOSIVES = QUICKCATCHSLOWBURN,
		VINES = QUICKCATCHSLOWBURN,
		BOOKSHELVES = MEDIUMCATCHQUICKBURN; 
	
	public final int chanceToEncourageFire;
	public final int abilityToCatchFire;
	
    private Flammability(int iChanceToEncourageFire, int iAbilityToCatchFire )
    {
		chanceToEncourageFire = iChanceToEncourageFire;
		abilityToCatchFire = iAbilityToCatchFire;
    }
}
