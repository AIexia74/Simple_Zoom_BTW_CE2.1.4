// FCMOD

package btw.crafting.manager;

public class CauldronCraftingManager extends BulkCraftingManager
{
    private static final CauldronCraftingManager instance = new CauldronCraftingManager();
    
    public static final CauldronCraftingManager getInstance()
    {
        return instance;
    }

    private CauldronCraftingManager()
    {
    	super();    	
    }
}

