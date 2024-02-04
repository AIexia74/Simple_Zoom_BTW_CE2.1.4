// FCMOD

package btw.item.items;

public class HighResolutionFoodItem extends FoodItem
{
    public HighResolutionFoodItem(int iItemID, int iHungerHealed, float fSaturationModifier, boolean bWolfMeat, String sItemName )
    {
        super( iItemID, iHungerHealed, fSaturationModifier, bWolfMeat, sItemName );        
    }
    
    public HighResolutionFoodItem(int iItemID, int iHungerHealed, float fSaturationModifier, boolean bWolfMeat, String sItemName, boolean bZombiesConsume )
    {
        super( iItemID, iHungerHealed, fSaturationModifier, bWolfMeat, sItemName, bZombiesConsume );
    }
    
    @Override
    public int getHungerRestored()
    {
    	// override of super function which multiplies the value
    	
    	return getHealAmount();
    }
}
