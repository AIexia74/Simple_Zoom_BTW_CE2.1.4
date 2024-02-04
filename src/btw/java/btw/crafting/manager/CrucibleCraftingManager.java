// FCMOD

package btw.crafting.manager;

public class CrucibleCraftingManager extends BulkCraftingManager
{
    private static final CrucibleCraftingManager instance = new CrucibleCraftingManager();
    
    public static final CrucibleCraftingManager getInstance()
    {
        return instance;
    }

    private CrucibleCraftingManager()
    {
    	super();    	
    }
}
