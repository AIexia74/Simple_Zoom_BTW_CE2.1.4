// FCMOD

package btw.crafting.manager;

public class CrucibleStokedCraftingManager extends BulkCraftingManager
{
    private static final CrucibleStokedCraftingManager instance = new CrucibleStokedCraftingManager();
    
    public static final CrucibleStokedCraftingManager getInstance()
    {
        return instance;
    }

    private CrucibleStokedCraftingManager()
    {
    	super();    	
    }
}
