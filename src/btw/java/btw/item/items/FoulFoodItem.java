// FCMOD

package btw.item.items;

public class FoulFoodItem extends FoodItem
{
	static private final int HEALTH_HEALED = 1;
	static private final float SATURATION_MODIFIER = 0F;

    public FoulFoodItem(int iItemID )
    {
        super(iItemID, HEALTH_HEALED, SATURATION_MODIFIER, false, "fcItemFoulFood");
        
        setIncreasedFoodPoisoningEffect();
    }    
}
