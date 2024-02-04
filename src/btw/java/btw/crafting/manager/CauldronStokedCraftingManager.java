// FCMOD

package btw.crafting.manager;

public class CauldronStokedCraftingManager extends BulkCraftingManager
{
    private static final CauldronStokedCraftingManager instance = new CauldronStokedCraftingManager();
    
    public static final CauldronStokedCraftingManager getInstance()
    {
        return instance;
    }

    private CauldronStokedCraftingManager()
    {
    	super();    	
    }
}

