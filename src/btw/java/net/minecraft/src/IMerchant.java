// FCMOD: Class changes deprecated 10/02/2018

package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface IMerchant
{
    void setCustomer(EntityPlayer var1);

    EntityPlayer getCustomer();

    MerchantRecipeList getRecipes(EntityPlayer var1);

    @Environment(EnvType.CLIENT)
    void setRecipes(MerchantRecipeList var1);

    void useRecipe(MerchantRecipe var1);

    // FCMOD: Added
    public int getCurrentTradeLevel();
    public int getCurrentTradeXP();
    public int getCurrentTradeMaxXP();
    // END FCMOD
}
