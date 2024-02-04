// FCMOD

package btw.item.items;

public class RottenFleshItem extends FoodItem
{
    public RottenFleshItem(int iItemID )
    {
        super( iItemID, 3, 0F, true, "rottenFlesh" );        

        setIncreasedFoodPoisoningEffect();
    }
}
