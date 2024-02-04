// FCMOD

package btw.inventory.container;

import btw.block.BTWBlocks;
import btw.block.tileentity.AnvilTileEntity;
import btw.crafting.manager.SoulforgeCraftingManager;
import btw.item.BTWItems;
import net.minecraft.src.*;

public class SoulforgeContainer extends Container
{
    public InventoryCrafting craftMatrix;
    public IInventory craftResult;
    
    private World localWorld;
    
    private int anvilX;
    private int anvilY;
    private int anvilZ;
    
    private static final double MAX_INTERACTION_DISTANCE = 8D;
    private static final double MAX_INTERACTION_DISTANCE_SQ =
    	(MAX_INTERACTION_DISTANCE * MAX_INTERACTION_DISTANCE);
    
    private static final int CRAFTING_GRID_WIDTH = 4;
    private static final int CRAFTING_GRID_HEIGHT = 4;
    private static final int CRAFTING_GRID_SIZE = (CRAFTING_GRID_HEIGHT * CRAFTING_GRID_WIDTH);
    
    private static final int SLOT_SCREEN_WIDTH = 18;
    private static final int SLOT_SCREEN_HEIGHT = 18;
    
    private static final int GRID_SCREEN_POS_X = 23;
    private static final int GRID_SCREEN_POS_Y = 17;
    
    private static final int PLAYER_INVENTORY_SCREEN_POS_X = 8;
    private static final int PLAYER_INVENTORY_SCREEN_POS_Y = 102;
    
    private static final int PLAYER_HOTBAR_SCREEN_POS_Y = (PLAYER_INVENTORY_SCREEN_POS_Y + 58 );
    
    private static final int PLAYER_INVENTORY_MIN_SLOT = (CRAFTING_GRID_SIZE + 1 );
    private static final int PLAYER_INVENTORY_MAX_SLOT = (PLAYER_INVENTORY_MIN_SLOT + 27 - 1 );
    
    private static final int PLAYER_HOTBAR_MIN_SLOT = (PLAYER_INVENTORY_MAX_SLOT + 1 );
    private static final int PLAYER_HOTBAR_MAX_SLOT = (PLAYER_HOTBAR_MIN_SLOT + 9 - 1 );
    
    /*
     * world, i, j, & k are only relevant on the server
     */
    public SoulforgeContainer(InventoryPlayer inventoryplayer, World world, int i, int j, int k )
    {
        craftMatrix = new InventoryCrafting(this, CRAFTING_GRID_WIDTH, CRAFTING_GRID_HEIGHT);
        craftResult = new InventoryCraftResult();

        localWorld = world;

        anvilX = i;
        anvilY = j;
        anvilZ = k;
        
        // add crafting output slot
        
        addSlotToContainer( new SlotCrafting( inventoryplayer.player, craftMatrix, craftResult, 0, 135, 44 ) );

        // add slots for the crafting grid
        
        for (int tempSlotY = 0; tempSlotY < CRAFTING_GRID_HEIGHT; tempSlotY++ )
        {
            for (int tempSlotX = 0; tempSlotX < CRAFTING_GRID_WIDTH; tempSlotX++ )
            {
            	addSlotToContainer( new Slot(craftMatrix, tempSlotX + tempSlotY * CRAFTING_GRID_WIDTH,
                                             GRID_SCREEN_POS_X + tempSlotX * SLOT_SCREEN_WIDTH,
                                             GRID_SCREEN_POS_Y + tempSlotY * SLOT_SCREEN_HEIGHT));
            }

        }

        // add slots for the player inventory
        
        for ( int tempSlotY = 0; tempSlotY < 3; tempSlotY++ )
        {
            for ( int tempSlotX = 0; tempSlotX < 9; tempSlotX++ )
            {
            	addSlotToContainer( new Slot(inventoryplayer,
            		tempSlotX + tempSlotY * 9 + 9,
                                             PLAYER_INVENTORY_SCREEN_POS_X + tempSlotX * SLOT_SCREEN_WIDTH,
                                             PLAYER_INVENTORY_SCREEN_POS_Y + tempSlotY * SLOT_SCREEN_HEIGHT));
            }
        }        

        // add slots for the player hot-bar
        
        for ( int tempSlotX = 0; tempSlotX < 9; tempSlotX++ )
        {
        	addSlotToContainer( new Slot(inventoryplayer, tempSlotX,
                                         PLAYER_INVENTORY_SCREEN_POS_X + tempSlotX * SLOT_SCREEN_WIDTH, PLAYER_HOTBAR_SCREEN_POS_Y));
        }

        if ( world != null && !world.isRemote )
        {
	        // transfer existing Mould items to the crafting grid
        	// legacy for old moulds hanging around
        	
	        AnvilTileEntity tileEntityAnvil = (AnvilTileEntity)world.getBlockTileEntity( i, j, k );
	        
	        if ( tileEntityAnvil != null )
	        {
		        for (int tempSlotY = 0; tempSlotY < CRAFTING_GRID_HEIGHT; tempSlotY++ )
		        {
		            for (int tempSlotX = 0; tempSlotX < CRAFTING_GRID_WIDTH; tempSlotX++ )
		            {
		            	if ( tileEntityAnvil.doesSlotContainMould(tempSlotX, tempSlotY) )
		            	{
		                    ItemStack mouldStack = new ItemStack( BTWItems.mould);
		
		                    Slot slot = (Slot)inventorySlots.get(tempSlotX + tempSlotY * CRAFTING_GRID_WIDTH + 1);
		                    
		                    slot.putStack( mouldStack );
		            	}
		            }
		        }
		        
		        tileEntityAnvil.clearMouldContents();
	        }
        }
        
        onCraftMatrixChanged( craftMatrix );
    }

	@Override
    public void onCraftMatrixChanged(IInventory iinventory)
    {
    	ItemStack craftedStack = CraftingManager.getInstance().findMatchingRecipe(craftMatrix, localWorld);
        IRecipe recipe = CraftingManager.getInstance().findMatchingIRecipe(this.craftMatrix, localWorld);
    	
    	if ( craftedStack == null )
    	{
    		craftedStack = SoulforgeCraftingManager.getInstance().findMatchingRecipeStack(craftMatrix, localWorld);
            recipe = SoulforgeCraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, localWorld);
    	}
    	
        craftResult.setInventorySlotContents( 0, craftedStack );
        
        ((SlotCrafting) this.getSlot(0)).setRecipe(recipe);
    }

	@Override
    public void onCraftGuiClosed( EntityPlayer entityplayer )
    {
        super.onCraftGuiClosed( entityplayer );
        
        if (localWorld != null && !localWorld.isRemote )
        {
	        for (int i = 0; i < CRAFTING_GRID_SIZE; i++ )
	        {
	            ItemStack itemstack = craftMatrix.getStackInSlot( i );
	            
	            if ( itemstack != null )
	            {
	        		entityplayer.dropPlayerItem( itemstack );
	            }
	        }
        }
    }

	@Override
    public boolean canInteractWith( EntityPlayer entityplayer )
    {
		if (localWorld == null || localWorld.isRemote )
		{
			return true;
		}
		
        if (localWorld.getBlockId(anvilX, anvilY, anvilZ) !=
            BTWBlocks.soulforge.blockID )
        {
            return false;
        }
        
        return entityplayer.getDistanceSq((double) anvilX + 0.5D, (double) anvilY + 0.5D,
                                          (double) anvilZ + 0.5D) <= MAX_INTERACTION_DISTANCE_SQ;
    }

	@Override
    public ItemStack transferStackInSlot( EntityPlayer player, int iSlotClicked )
    {
        ItemStack oldStackInSlotClicked = null;
        
        Slot slot = (Slot)inventorySlots.get( iSlotClicked );
        
        if ( slot != null && slot.getHasStack() )
        {
            ItemStack newStackInSlotClicked = slot.getStack();
            oldStackInSlotClicked = newStackInSlotClicked.copy();
            
            if ( iSlotClicked == 0 )
            {	
            	// crafting result slot
            	
                if ( !mergeItemStack(newStackInSlotClicked, PLAYER_INVENTORY_MIN_SLOT, PLAYER_HOTBAR_MAX_SLOT + 1, true) )
                {
                    return null;
                }
            } 
            else if (iSlotClicked > CRAFTING_GRID_SIZE && iSlotClicked <= CRAFTING_GRID_SIZE + 27 + 9 )
            {
            	// player inventory & hot-bar
            	
            	if ( !mergeItemStack(newStackInSlotClicked, 1, CRAFTING_GRID_SIZE + 1, false) )
            	{
            		return null;
            	}
            } 
            else
            {
            	// crafting grid
            	
                if ( !mergeItemStack(newStackInSlotClicked, PLAYER_INVENTORY_MIN_SLOT, PLAYER_HOTBAR_MAX_SLOT + 1, true) )
            	{
            		return null;
            	}
            }
            
            if(newStackInSlotClicked.stackSize == 0)
            {
                slot.putStack(null);
            } 
            else
            {
                slot.onSlotChanged();
            }
            
            if ( newStackInSlotClicked.stackSize != oldStackInSlotClicked.stackSize )
            {
                slot.onPickupFromSlot( player, newStackInSlotClicked);
            } 
            else
            {
                return null;
            }
        }
        
        return oldStackInSlotClicked;
    }
}