// FCMOD

package btw.crafting.manager;

import btw.inventory.util.InventoryUtils;
import net.minecraft.src.*;

import java.util.*;

// Referenced classes of package net.minecraft.src:
//            RecipesTools, RecipesWeapons, RecipesIngots, RecipesFood, 
//            RecipesCrafting, RecipesArmor, RecipesDyes, ItemStack, 
//            Item, Block, RecipeSorter, ShapedRecipes, 
//            ShapelessRecipes, IRecipe, InventoryCrafting

public class SoulforgeCraftingManager
{
    private static final SoulforgeCraftingManager instance = new SoulforgeCraftingManager();
    private List recipes;

    public static final SoulforgeCraftingManager getInstance()
    {
        return instance;
    }

    private SoulforgeCraftingManager()
    {
        recipes = new ArrayList();        
    }

    public void addRecipe(ItemStack itemstack, Object aobj[])
    {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;
        if(aobj[i] instanceof String[])
        {
            String as[] = (String[])aobj[i++];
            for(int l = 0; l < as.length; l++)
            {
                String s2 = as[l];
                k++;
                j = s2.length();
                s = (new StringBuilder()).append(s).append(s2).toString();
            }

        } else
        {
            while(aobj[i] instanceof String) 
            {
                String s1 = (String)aobj[i++];
                k++;
                j = s1.length();
                s = (new StringBuilder()).append(s).append(s1).toString();
            }
        }
        HashMap hashmap = new HashMap();
        for(; i < aobj.length; i += 2)
        {
            Character character = (Character)aobj[i];
            ItemStack itemstack1 = null;
            if(aobj[i + 1] instanceof Item)
            {
                itemstack1 = new ItemStack((Item)aobj[i + 1]);
            } else
            if(aobj[i + 1] instanceof Block)
            {
                itemstack1 = new ItemStack((Block)aobj[i + 1], 1, InventoryUtils.IGNORE_METADATA);
            } else
            if(aobj[i + 1] instanceof ItemStack)
            {
                itemstack1 = (ItemStack)aobj[i + 1];
            }
            hashmap.put(character, itemstack1);
        }

        ItemStack aitemstack[] = new ItemStack[j * k];
        for(int i1 = 0; i1 < j * k; i1++)
        {
            char c = s.charAt(i1);
            if(hashmap.containsKey(Character.valueOf(c)))
            {
                aitemstack[i1] = ((ItemStack)hashmap.get(Character.valueOf(c))).copy();
            } else
            {
                aitemstack[i1] = null;
            }
        }

        recipes.add(new ShapedRecipes(j, k, aitemstack, itemstack));
    }

    public void addShapelessRecipe(ItemStack itemstack, Object aobj[])
    {
        ArrayList arraylist = new ArrayList();
        
        Object aobj1[] = aobj;
        int i = aobj1.length;
        
        for(int j = 0; j < i; j++)
        {
            Object obj = aobj1[j];
            if(obj instanceof ItemStack)
            {
                arraylist.add(((ItemStack)obj).copy());
                continue;
            }
            if(obj instanceof Item)
            {
                arraylist.add(new ItemStack((Item)obj));
                continue;
            }
            if(obj instanceof Block)
            {
                arraylist.add(new ItemStack((Block)obj));
            } else
            {
                throw new RuntimeException("Invalid shapeless recipe!");
            }
        }

        recipes.add(new ShapelessRecipes(itemstack, arraylist));
    }

    public ItemStack findMatchingRecipeStack(InventoryCrafting inventorycrafting, World world)
    {
        for(int i = 0; i < recipes.size(); i++)
        {
            IRecipe irecipe = (IRecipe)recipes.get(i);
            if(irecipe.matches(inventorycrafting, world))
            {
                return irecipe.getCraftingResult(inventorycrafting);
            }
        }

        return null;
    }
    
    public IRecipe findMatchingRecipe(InventoryCrafting inventory, World world)
    {
        for ( int iTempIndex = 0; iTempIndex < this.recipes.size(); ++iTempIndex)
        {
            IRecipe tempRecipe = (IRecipe)this.recipes.get(iTempIndex);

            if ( tempRecipe.matches( inventory, world ) )
            {
                return tempRecipe;
            }
        }

        return null;
    }
    
    public List getRecipeList()
    {
        return recipes;
    }
    
    /*
     * Returns true if the recipe was successfully removed
     */
    public boolean removeRecipe(ItemStack itemStack, Object recipeArray[])
    {
    	ShapedRecipes recipe = createRecipe(itemStack, recipeArray);
    	
    	int iMatchingIndex = getMatchingRecipeIndex(recipe);
    	
    	if ( iMatchingIndex >= 0 )
    	{
    		recipes.remove( iMatchingIndex );
    		
    		return true;
    	}
    	
    	return false;
    }
    
    /*
     * Returns true if the recipe was successfully removed
     */
    public boolean removeShapelessRecipe(ItemStack itemStack, Object recipeArray[])
    {
    	ShapelessRecipes recipe = createShapelessRecipe(itemStack, recipeArray);
    	
    	int iMatchingIndex = getMatchingRecipeIndex(recipe);
    	
    	if ( iMatchingIndex >= 0 )
    	{
    		recipes.remove( iMatchingIndex );
    		
    		return true;
    	}
    	
    	return false;
    }
    
    private int getMatchingRecipeIndex(IRecipe recipe)
    {
    	int iMatchingRecipeIndex = -1;
    	
        for ( int iIndex = 0; iIndex < recipes.size(); iIndex++ )
        {
            IRecipe tempRecipe = (IRecipe)(recipes.get( iIndex ));

            if ( tempRecipe.matches( recipe ) )
            {
                return iIndex;
            }            
        }

    	return -1;
    }
    
    private ShapedRecipes createRecipe(ItemStack par1ItemStack, Object par2ArrayOfObj[])
    {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (par2ArrayOfObj[i] instanceof String[])
        {
            String as[] = (String[])par2ArrayOfObj[i++];
            String as1[] = as;
            int l = as1.length;

            for (int j1 = 0; j1 < l; j1++)
            {
                String s2 = as1[j1];
                String s3 = s2;
                k++;
                j = s3.length();
                s = (new StringBuilder()).append(s).append(s3).toString();
            }
        }
        else
        {
            while (par2ArrayOfObj[i] instanceof String)
            {
                String s1 = (String)par2ArrayOfObj[i++];
                k++;
                j = s1.length();
                s = (new StringBuilder()).append(s).append(s1).toString();
            }
        }

        HashMap hashmap = new HashMap();

        for (; i < par2ArrayOfObj.length; i += 2)
        {
            Character character = (Character)par2ArrayOfObj[i];
            ItemStack itemstack = null;

            if (par2ArrayOfObj[i + 1] instanceof Item)
            {
                itemstack = new ItemStack((Item)par2ArrayOfObj[i + 1]);
            }
            else if (par2ArrayOfObj[i + 1] instanceof Block)
            {
                itemstack = new ItemStack((Block)par2ArrayOfObj[i + 1], 1, InventoryUtils.IGNORE_METADATA);
            }
            else if (par2ArrayOfObj[i + 1] instanceof ItemStack)
            {
                itemstack = (ItemStack)par2ArrayOfObj[i + 1];
            }

            hashmap.put(character, itemstack);
        }

        ItemStack aitemstack[] = new ItemStack[j * k];

        for (int i1 = 0; i1 < j * k; i1++)
        {
            char c = s.charAt(i1);

            if (hashmap.containsKey(Character.valueOf(c)))
            {
                aitemstack[i1] = ((ItemStack)hashmap.get(Character.valueOf(c))).copy();
            }
            else
            {
                aitemstack[i1] = null;
            }
        }

        return new ShapedRecipes(j, k, aitemstack, par1ItemStack );
    }
    
    private ShapelessRecipes createShapelessRecipe(ItemStack par1ItemStack, Object par2ArrayOfObj[])
    {
        ArrayList arraylist = new ArrayList();
        Object aobj[] = par2ArrayOfObj;
        int i = aobj.length;

        for (int j = 0; j < i; j++)
        {
            Object obj = aobj[j];

            if (obj instanceof ItemStack)
            {
                arraylist.add(((ItemStack)obj).copy());
                continue;
            }

            if (obj instanceof Item)
            {
                arraylist.add(new ItemStack((Item)obj));
                continue;
            }

            if (obj instanceof Block)
            {
                arraylist.add(new ItemStack((Block)obj));
            }
            else
            {
                throw new RuntimeException("Invalid shapeless recipe!");
            }
        }

        return new ShapelessRecipes( par1ItemStack, arraylist );
    }
}