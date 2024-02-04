// FCMOD

////////////////
// DEPRECATED //
////////////////

// This class only exists for addon compatibility, and is no longer used
// FCCraftingRecipeAxeChopping replaces the functionality this class provided

package btw.crafting.recipe.types.customcrafting;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.items.AxeItem;
import net.minecraft.src.*;

@Deprecated
public class LegacyLogChoppingRecipe implements IRecipe
{
	public boolean matches(InventoryCrafting craftingInventory, World world )
	{
		ItemStack axeStack = null;
		ItemStack logStack = null;

		for ( int iTempSlot = 0; iTempSlot < craftingInventory.getSizeInventory(); ++iTempSlot )
		{
			ItemStack tempStack = craftingInventory.getStackInSlot( iTempSlot );

			if ( tempStack != null )
			{
				if ( isAxe(tempStack) )
				{
					if ( axeStack == null )
					{
						axeStack = tempStack;
					}
					else
					{
						return false;
					}
				}
				else if ( isLog(tempStack) )
				{
					if ( logStack == null )
					{
						logStack = tempStack;
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
		}

		return axeStack != null && logStack != null;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	public ItemStack getCraftingResult( InventoryCrafting craftingInventory )
	{
		ItemStack axeStack = null;
		ItemStack logStack = null;

		for ( int iTempSlot = 0; iTempSlot < craftingInventory.getSizeInventory(); ++iTempSlot )
		{
			ItemStack tempStack = craftingInventory.getStackInSlot( iTempSlot );

			if ( tempStack != null )
			{
				if ( isAxe(tempStack) )
				{
					if ( axeStack == null )
					{
						axeStack = tempStack;
					}
					else
					{
						return null;
					}
				}
				else if ( isLog(tempStack) )
				{
					if ( logStack == null )
					{
						logStack = tempStack;                        
					}
					else
					{
						return null;
					}
				}
				else
				{
					return null;
				}
			}
		}

		if ( logStack != null && axeStack != null )
		{
			ItemStack resultStack = null;
			AxeItem axeItem = (AxeItem)axeStack.getItem();

			if ( axeItem.toolMaterial.getHarvestLevel() <= 1 ) // wood, stone & gold
			{
				resultStack = new ItemStack( Item.stick, 2 );
			}
			else
			{
				int iLogID = logStack.itemID;

				if ( iLogID == BTWBlocks.bloodWoodLog.blockID )
				{
					resultStack = new ItemStack( Block.planks.blockID, 2, 4 );
				}
				else
				{
					resultStack = new ItemStack( Block.planks.blockID, 2, logStack.getItemDamage() );
				}
			}

			return resultStack;
		}

		return null;
	}

	@Override
	public int getRecipeSize()
	{
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return null;
	}

	@Override
	public boolean matches( IRecipe recipe )
	{
		return false;
	}

	@Override
	public boolean hasSecondaryOutput()
	{
		return true;
	}

	@Override
	public ItemStack[] getSecondaryOutput(IInventory inventory) {
		ItemStack result = this.getCraftingResult((InventoryCrafting) inventory);
		ItemStack[] outputs;
		
		ItemStack logStack = null;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack craftingStack = inventory.getStackInSlot(i);

			if (craftingStack != null && isLog(craftingStack)) {
				logStack = craftingStack.copy();
			}
		}
		
		if (logStack.itemID == BTWBlocks.bloodWoodLog.blockID) { //Blood wood
			outputs = new ItemStack[3];

			if (result.itemID == Block.planks.blockID) {
				outputs[0] = new ItemStack(BTWItems.sawDust, 1);
				outputs[1] = new ItemStack(BTWItems.soulDust, 1);
				outputs[2] = new ItemStack(BTWItems.bark, 1, logStack.getItemDamage());
			}
			else {
				outputs[0] = new ItemStack(BTWItems.sawDust, 3);
				outputs[1] = new ItemStack(BTWItems.soulDust, 1);
				outputs[2] = new ItemStack(BTWItems.bark, 1, logStack.getItemDamage());
			}
		}
		else {
			outputs = new ItemStack[2];

			if (result.itemID == Block.planks.blockID) {
				outputs[0] = new ItemStack(BTWItems.sawDust, 2);
				outputs[1] = new ItemStack(BTWItems.bark, 1, logStack.getItemDamage());
			}
			else {
				outputs[0] = new ItemStack(BTWItems.sawDust, 4);
				outputs[1] = new ItemStack(BTWItems.bark, 1, logStack.getItemDamage());
			}
		}

		return outputs;
	}

	//------------- Class Specific Methods ------------//

	private boolean isAxe(ItemStack stack)
	{
		int iItemID = stack.itemID;

		if ( stack.getItem() instanceof AxeItem)
		{
			return true;
		}

		return false;
	}

	private boolean isLog(ItemStack stack)
	{
		int iItemID = stack.itemID;

		if ( iItemID == BTWBlocks.bloodWoodLog.blockID ||
				iItemID == Block.wood.blockID )
		{
			return true;
		}

		return false;
	}
}