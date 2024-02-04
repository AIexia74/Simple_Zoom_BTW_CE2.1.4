// FCMOD

package btw.crafting.manager;

public class MillStoneCraftingManager extends BulkCraftingManager
{
    private static final MillStoneCraftingManager instance = new MillStoneCraftingManager();
    
    public static final MillStoneCraftingManager getInstance()
    {
        return instance;
    }

    private MillStoneCraftingManager()
    {
    	super();    	
    }
}