// FCMOD

package btw.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.IconRegister;
import net.minecraft.src.ItemFood;
import net.minecraft.src.Potion;

public class FoodItem extends ItemFood
{
	static public final int FOOD_POISIONING_STANDARD_DURATION = 60; // in seconds
	static public final float FOOD_POISONING_STANDARD_CHANCE = 0.3F;
	
	static public final int FOOD_POISIONING_INCREASED_DURATION = 60; // in seconds
	static public final float FOOD_POISONING_INCREASED_CHANCE = 0.8F;
	
	static public final int DONUT_HUNGER_HEALED = 1;
	static public final float DONUT_SATURATION_MODIFIER = 0.5F;
	static public final String DONUT_ITEM_NAME = "fcItemDonut";

	static public final int DOG_FOOD_HUNGER_HEALED = 3;
	static public final float DOG_FOOD_SATURATION_MODIFIER = 0F;
	static public final String DOG_FOOD_ITEM_NAME = "fcItemKibble";
	
	static public final int RAW_EGG_HUNGER_HEALED = 2;
	static public final float RAW_EGG_SATURATION_MODIFIER = 0.25F;
	static public final String RAW_EGG_ITEM_NAME = "fcItemEggRaw";
	
	static public final int FRIED_EGG_HUNGER_HEALED = 3;
	static public final float FRIED_EGG_SATURATION_MODIFIER = 0.25F;
	static public final String FRIED_EGG_ITEM_NAME = "fcItemEggFried";
	
	static public final int BOILED_POTATO_HUNGER_HEALED = 2;
	static public final float BOILED_POTATO_SATURATION_MODIFIER = 0F;
	static public final String BOILED_POTATO_ITEM_NAME = "fcItemPotatoBoiled";
	
	static public final int COOKED_CARROT_HUNGER_HEALED = 2;
	static public final float COOKED_CARROT_SATURATION_MODIFIER = 0F;
	static public final String COOKED_CARROT_ITEM_NAME = "fcItemCarrotCooked";
	
	static public final int TASTY_SANDWICH_HUNGER_HEALED = 5;
	static public final float TASTY_SANDWICH_SATURATION_MODIFIER = 0.25F;
	static public final String TASTY_SANDWICH_ITEM_NAME = "fcItemSandwichTasty";
	
	static public final int STEAK_AND_POTATOES_HUNGER_HEALED = 6;
	static public final float STEAK_AND_POTATOES_SATURATION_MODIFIER = 0.25F;
	static public final String STEAK_AND_POTATOES_ITEM_NAME = "fcItemSteakAndPotatoes";
	
	static public final int HAM_AND_EGGS_HUNGER_HEALED = 6;
	static public final float HAM_AND_EGGS_SATURATION_MODIFIER = 0.25F;
	static public final String HAM_AND_EGGS_ITEM_NAME = "fcItemHamAndEggs";
	
	static public final int STEAK_DINNER_HUNGER_HEALED = 8;
	static public final float STEAK_DINNER_SATURATION_MODIFIER = 0.25F;
	static public final String STEAK_DINNER_ITEM_NAME = "fcItemDinnerSteak";
	
	static public final int PORK_DINNER_HUNGER_HEALED = 8;
	static public final float PORK_DINNER_SATURATION_MODIFIER = 0.25F;
	static public final String PORK_DINNER_ITEM_NAME = "fcItemDinnerPork";
	
	static public final int WOLF_DINNER_HUNGER_HEALED = 8;
	static public final float WOLF_DINNER_SATURATION_MODIFIER = 0.25F;
	static public final String WOLF_DINNER_ITEM_NAME = "fcItemDinnerWolf";
	
	static public final int RAW_KEBAB_HUNGER_HEALED = 6;
	static public final float RAW_KEBAB_SATURATION_MODIFIER = 0.25F;
	static public final String RAW_KEBAB_ITEM_NAME = "fcItemKebabRaw";
	
	static public final int COOKED_KEBAB_HUNGER_HEALED = 8;
	static public final float COOKED_KEBAB_SATURATION_MODIFIER = 0.25F;
	static public final String COOKED_KEBAB_ITEM_NAME = "fcItemKebabCooked";
	
	static public final int CHICKEN_SOUP_HUNGER_HEALED = 8;
	static public final float CHICKEN_SOUP_SATURATION_MODIFIER = 0.25F;
	static public final String CHICKEN_SOUP_ITEM_NAME = "fcItemSoupChicken";
	
	static public final int FISH_SOUP_HUNGER_HEALED = 5;
	static public final float FISH_SOUP_SATURATION_MODIFIER = 0.25F;
	static public final String FISH_SOUP_ITEM_NAME = "fcItemChowder";
	
	static public final int HEARTY_STEW_HUNGER_HEALED = 10;
	static public final float HEARTY_STEW_SATURATION_MODIFIER = 0.25F;
	static public final String HEARTY_STEW_ITEM_NAME = "fcItemStewHearty";
	
	static public final int RAW_MUSHROOM_OMELET_HUNGER_HEALED = 3;
	static public final float RAW_MUSHROOM_OMELET_SATURATION_MODIFIER = 0.25F;
	static public final String RAW_MUSHROOM_OMELET_ITEM_NAME = "fcItemMushroomOmletRaw";
	
	static public final int COOKED_MUSHROOM_OMELET_HUNGER_HEALED = 4;
	static public final float COOKED_MUSHROOM_OMELET_SATURATION_MODIFIER = 0.25F;
	static public final String COOKED_MUSHROOM_OMELET_ITEM_NAME = "fcItemMushroomOmletCooked";
	
	static public final int RAW_SCRAMBLED_EGGS_HUNGER_HEALED = 3;
	static public final float RAW_SCRAMBLED_EGGS_SATURATION_MODIFIER = 0.25F;
	static public final String RAW_SCRAMBLED_EGGS_ITEM_NAME = "fcItemEggScrambledRaw";
	
	static public final int COOKED_SCRAMBLED_EGGS_HUNGER_HEALED = 4;
	static public final float COOKED_SCRAMBLED_EGGS_SATURATION_MODIFIER = 0.25F;
	static public final String COOKED_SCRAMBLED_EGGS_ITEM_NAME = "fcItemEggScrambledCooked";
	
	static public final int CREEPER_OYSTERS_HUNGER_HEALED = 2;
	static public final float CREEPER_OYSTERS_SATURATION_MODIFIER = 0.8F;
	static public final String CREEPER_OYSTERS_ITEM_NAME = "fcItemCreeperOysters";
	
	static public final int BAT_WING_HUNGER_HEALED = 1;
	static public final float BAT_WING_SATURATION_MODIFIER = 0.8F;
	static public final String BAT_WING_ITEM_NAME = "fcItemBatWing";
	
	static public final int CHOCOLATE_HUNGER_HEALED = 2;
	static public final float CHOCOLATE_SATURATION_MODIFIER = 0.5F;
	static public final String CHOCOLATE_ITEM_NAME = "fcItemChocolate";
	
	static public final int MUTTON_RAW_HUNGER_HEALED = 3;
	static public final int MUTTON_COOKED_HUNGER_HEALED = 4;
	static public final float MUTTON_SATURATION_MODIFIER = 0.25F;
	
	static public final int BEAST_LIVER_RAW_HUNGER_HEALED = 5;
	static public final int BEAST_LIVER_COOKED_HUNGER_HEALED = 6;
	static public final float BEAST_LIVER_SATURATION_MODIFIER = 0.5F;
	
	static public final int CHICKEN_RAW_HUNGER_HEALED = 3;
	static public final int CHICKEN_COOKED_HUNGER_HEALED = 4;
	static public final float CHICKEN_SATURATION_MODIFIER = 0.25F;
	
	static public final int BEEF_RAW_HUNGER_HEALED = 4;
	static public final int BEEF_COOKED_HUNGER_HEALED = 5;
	static public final float BEEF_SATURATION_MODIFIER = 0.25F;
	
	static public final int FISH_RAW_HUNGER_HEALED = 3;
	static public final int FISH_COOKED_HUNGER_HEALED = 4;
	static public final float FISH_SATURATION_MODIFIER = 0.25F;
	
	static public final int PORK_CHOP_RAW_HUNGER_HEALED = 4;
	static public final int PORK_CHOP_COOKED_HUNGER_HEALED = 5;
	static public final float PORK_CHOP_SATURATION_MODIFIER = 0.25F;
	
	static public final int MEAT_CURED_HUNGER_HEALED = 3;
	static public final float MEAT_CURED_SATURATION_MODIFIER = 0.25F;
	
	static public final int MEAT_BURNED_HUNGER_HEALED = 2;
	static public final float MEAT_BURNED_SATURATION_MODIFIER = 0.25F;
	
	private String iconNameOverride = null;
	
    public FoodItem(int iItemID, int iHungerHealed, float fSaturationModifier, boolean bWolfMeat, String sItemName )
    {
        super( iItemID, iHungerHealed, fSaturationModifier, bWolfMeat );
        
        setUnlocalizedName( sItemName );    
    }
    
    public FoodItem(int iItemID, int iHungerHealed, float fSaturationModifier, boolean bWolfMeat, String sItemName, boolean bZombiesConsume )
    {
        super( iItemID, iHungerHealed, fSaturationModifier, bWolfMeat, bZombiesConsume );
        
        setUnlocalizedName( sItemName );    
    }    
    
    //------------- Class Specific Methods ------------//
    
    public FoodItem setStandardFoodPoisoningEffect()
    {
    	setPotionEffect(Potion.hunger.id, FOOD_POISIONING_STANDARD_DURATION, 0, FOOD_POISONING_STANDARD_CHANCE);
    	
    	return this;
    }
    
    public FoodItem setIncreasedFoodPoisoningEffect()
    {
    	setPotionEffect(Potion.hunger.id, FOOD_POISIONING_INCREASED_DURATION, 0, FOOD_POISONING_INCREASED_CHANCE);

    	return this;
    }

    public FoodItem setIconName(String sName)
    {
		iconNameOverride = sName;
    	
    	return this;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		if (iconNameOverride != null )
		{
			itemIcon = register.registerIcon(iconNameOverride);
		}
		else
		{
			super.registerIcons( register );
		}
    }
}