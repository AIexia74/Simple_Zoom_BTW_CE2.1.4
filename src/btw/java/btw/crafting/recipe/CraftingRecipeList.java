// FCMOD

package btw.crafting.recipe;

import btw.block.BTWBlocks;
import btw.block.blocks.*;
import btw.crafting.recipe.types.customcrafting.*;
import btw.inventory.util.InventoryUtils;
import btw.item.BTWItems;
import btw.item.blockitems.WoodMouldingDecorativeStubBlockItem;
import btw.item.blockitems.WoodSidingDecorativeStubBlockItem;
import btw.item.items.BoneCarvingItem;
import btw.item.items.ProgressiveCraftingItem;
import btw.item.items.WickerWeavingItem;
import btw.util.ColorUtils;
import net.minecraft.src.*;

public abstract class CraftingRecipeList {
	public static void addRecipes() {
		removeVanillaRecipes();
		
		addClusteredRecipes();
		
		addBlockRecipes();
		addItemRecipes();
		addDyeRecipes();
		addAlternateVanillaRecipes();
		addConversionRecipes();
		addLogChoppingRecipes();
		addTuningForkRecipes();
		addSubBlockRecipes();
		
		addLegacyConversionRecipes();
		addCustomRecipeClasses();
		
		addDebugRecipes();
	}
    
	private static void addClusteredRecipes() {
		// trying a different way of organizing here, trying to keep things simple for new blocks added
		
		// rotten flesh blocks
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.rottenFleshSlab, 6 ), new Object[] {
    		"###", 
    		'#', BTWBlocks.rottenFleshBlock
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.rottenFleshBlock), new Object[] {
    		new ItemStack( BTWBlocks.rottenFleshSlab),
    		new ItemStack( BTWBlocks.rottenFleshSlab)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.rottenFlesh, 9 ), new Object[] {
    		new ItemStack( BTWBlocks.rottenFleshBlock),
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.rottenFlesh, 4 ), new Object[] {
    		new ItemStack( BTWBlocks.rottenFleshSlab),
		});
		
		// bone blocks
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.boneSlab, 6 ), new Object[] {
    		"###", 
    		'#', new ItemStack( BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_BONE)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_BONE), new Object[] {
    		new ItemStack( BTWBlocks.boneSlab),
    		new ItemStack( BTWBlocks.boneSlab)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.bone, 9 ), new Object[] {
    		new ItemStack( BTWBlocks.aestheticOpaque, 1,
				AestheticOpaqueBlock.SUBTYPE_BONE)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.bone, 4 ), new Object[] {
    		new ItemStack( BTWBlocks.boneSlab),
		});
		
		// creeper oyster blocks
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.creeperOysterSlab, 6 ), new Object[] {
    		"###", 
    		'#', BTWBlocks.creeperOysterBlock
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.creeperOysterBlock), new Object[] {
    		new ItemStack( BTWBlocks.creeperOysterSlab),
    		new ItemStack( BTWBlocks.creeperOysterSlab)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.creeperOysters, 16 ), new Object[] {
    		new ItemStack( BTWBlocks.creeperOysterBlock),
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.creeperOysters, 8 ), new Object[] {
    		new ItemStack( BTWBlocks.creeperOysterSlab),
		});
		
		// Spider eye blocks
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.spiderEyeSlab, 6 ), new Object[] {
				"###",
				'#', BTWBlocks.spiderEyeBlock
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.spiderEyeBlock), new Object[] {
				new ItemStack( BTWBlocks.spiderEyeSlab),
				new ItemStack( BTWBlocks.spiderEyeSlab)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.spiderEye, 16 ), new Object[] {
				new ItemStack( BTWBlocks.spiderEyeBlock),
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.spiderEye, 8 ), new Object[] {
				new ItemStack( BTWBlocks.spiderEyeSlab),
		});
		
		// smoothstone stairs
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.stoneStairs, 6 ), new Object[] {
    		"#  ", 
    		"## ", 
    		"###", 
    		'#', new ItemStack(Block.stone, 1, 0)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.midStrataStoneStairs, 6 ), new Object[] {
			"#  ",
			"## ",
			"###",
			'#', new ItemStack( Block.stone, 1, 1)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.deepStrataStoneStairs, 6 ), new Object[] {
			"#  ",
			"## ",
			"###",
			'#', new ItemStack( Block.stone, 1, 2)
		});
		
		// blood wood stairs and slabs

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.bloodWoodStairs, 6 ), new Object[] {
    		"#  ", 
    		"## ", 
    		"###", 
    		'#', new ItemStack( Block.planks, 1, 4 )
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.bloodWoodStairs, 1 ), new Object[] {
            "# ", 
            "##", '#', new ItemStack( BTWItems.woodMouldingStubID, 1, 4 )
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.woodSingleSlab, 6, 4 ), new Object[] {
			"###", 
			Character.valueOf('#'), new ItemStack( Block.planks, 1, 4 )
		});
		
		// clustered recipe functions

		addEarlyGameRecipes();
		addToolRecipes();
		addLooseStoneRecipes();
		addLooseBrickRecipes();
		addLooseStoneBrickRecipes();
		addLooseNetherBrickRecipes();
		addTorchRecipes();
		addWickerRecipes();
		addStairRecipes();
		addWoolAndKnittingRecipes();
		addSawDustRecipes();
		addMeatCuringRecipes();
		addPaneRecipes();
		addSnowRecipes();
		addChickenFeedRecipes();
		addFishingRecipes();
		addDirtRecipes();
		addGravelRecipes();
		addSandRecipes();
		addMechanicalRecipes();
		addOreRecipes();
		addPastryRecipes();
	}
	
	private static void addEarlyGameRecipes()
	{
		// Chisels		
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.pointyStick), new Object[] {
    		new ItemStack( Item.stick )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sharpStone), new Object[] {
    		new ItemStack(BTWItems.stone, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.ironChisel), new Object[] {
    		"XX", 
    		"XX", 
    		'X', BTWItems.ironNugget
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.diamondChisel), new Object[] {
    		"X", 
    		'X', BTWItems.diamondIngot
		});
		
		// shears
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.diamondShears), new Object[] {
    		"X ",
    		" X",
    		'X', BTWItems.diamondIngot
		});

		// clubs
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.woodenClub), new Object[] {
    		"X", 
    		"X", 
    		'X', Item.stick
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.boneClub), new Object[] {
    		"X", 
    		"X", 
    		'X', Item.bone
		});
		
		// fire starters
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.firePlough), new Object[] {
    		"XX", 
    		'X', Item.stick
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.bowDrill), new Object[] {
    		new ItemStack( Item.stick ),
    		new ItemStack( Item.stick ),
    		new ItemStack( Item.silk )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.bowDrill), new Object[] {
    		new ItemStack( Item.stick ),
    		new ItemStack( Item.stick ),
    		new ItemStack( BTWItems.hempFibers)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.bowDrill), new Object[] {
				new ItemStack( Item.stick ),
				new ItemStack( Item.stick ),
				new ItemStack( BTWItems.sinew)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.unlitCampfire), new Object[] {
    		"XX", 
    		"XX", 
    		'X', Item.stick
		});
		
		RecipeManager.addShapelessRecipeWithSecondaryOutputIndicator(new ItemStack(Item.stick ), new ItemStack(Item.silk), new Object[] {
    		new ItemStack(Item.bow, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.idleOven), new Object[] {
    		"XX", 
    		"XX", 
    		'X', BTWBlocks.looseBrickSlab
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.coal ), new Object[] {
    		new ItemStack( BTWItems.coalDust),
    		new ItemStack( BTWItems.coalDust)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.clay ), new Object[] {
    		new ItemStack( BTWItems.clayPile),
    		new ItemStack( BTWItems.clayPile)
		});
		
		// ladders
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.ladder, 2 ), new Object[] {
			"#S#", 
			"###", 
			"#S#", 
			'#', Item.stick,
        	'S', Item.silk
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.ladder, 2 ), new Object[] {
			"#S#", 
			"###", 
			"#S#", 
			'#', Item.stick,
        	'S', BTWItems.hempFibers
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.ladder, 2 ), new Object[] {
				"#S#",
				"###",
				"#S#",
				'#', Item.stick,
				'S', BTWItems.sinew
		});
		
		// arrows
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.arrow , 2 ), new Object[] {
    		new ItemStack( Item.feather ),
    		new ItemStack( Item.stick ),
    		new ItemStack( Item.silk ),
    		new ItemStack( Item.flint )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.arrow , 2 ), new Object[] {
    		new ItemStack( Item.feather ),
    		new ItemStack( Item.stick ),
    		new ItemStack( BTWItems.hempFibers),
    		new ItemStack( Item.flint )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.arrow , 2 ), new Object[] {
				new ItemStack( Item.feather ),
				new ItemStack( Item.stick ),
				new ItemStack( BTWItems.sinew),
				new ItemStack( Item.flint )
		});
		
		//other
		RecipeManager.addLogChoppingRecipe(new ItemStack(BTWItems.mashedMelon, 2),
				null,
				new ItemStack(Block.melon));
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.melonSeeds, 2), new Object[] {
			new ItemStack(BTWItems.mashedMelon)
		});
	}

	private static void addToolRecipes()
	{
		addStoneToolRecipes();
		
		RecipeManager.addRecipe(new ItemStack(Item.hoeIron ), new Object[] {
			"X#", 
			" #", 
			" #",
			'#', Item.stick,
			'X', Item.ingotIron			
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.hoeDiamond ), new Object[] {
			"X#", 
			" #", 
			" #",
			'#', Item.stick,
			'X', BTWItems.diamondIngot
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.hoeGold ), new Object[] {
			"X#", 
			" #", 
			" #",
			'#', Item.stick,
			'X', Item.ingotGold			
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.diamondArmorPlate, 1 ), new Object[] {
    		"#X#",
    		" Y ",
    		'#', BTWItems.leatherStrap,
    		'X', BTWItems.diamondIngot,
    		'Y', BTWItems.padding
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.steelArmorPlate, 1 ), new Object[] {
			"#X#",
    		" Y ",
    		'#', BTWItems.leatherStrap,
    		'X', BTWItems.soulforgedSteelIngot,
    		'Y', BTWItems.padding
		});
	}
	
	private static void addStoneToolRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(Item.shovelStone ), new Object[] {
        	Item.stick, 
        	new ItemStack(BTWItems.stone, 1, InventoryUtils.IGNORE_METADATA),
        	Item.silk
    	});
        
		RecipeManager.addShapelessRecipe(new ItemStack(Item.shovelStone ), new Object[] {
        	Item.stick, 
        	new ItemStack(BTWItems.stone, 1, InventoryUtils.IGNORE_METADATA),
        	BTWItems.hempFibers
    	});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.shovelStone ), new Object[] {
				Item.stick,
				new ItemStack(BTWItems.stone, 1, InventoryUtils.IGNORE_METADATA),
				BTWItems.sinew
		});
        
		RecipeManager.addShapelessRecipe(new ItemStack(Item.axeStone ), new Object[] {
        	Item.stick, 
        	new ItemStack(BTWItems.stone, 1, InventoryUtils.IGNORE_METADATA),
        	new ItemStack(BTWItems.stone, 1, InventoryUtils.IGNORE_METADATA),
        	Item.silk
    	});
        
		RecipeManager.addShapelessRecipe(new ItemStack(Item.axeStone ), new Object[] {
        	Item.stick, 
        	new ItemStack(BTWItems.stone, 1, InventoryUtils.IGNORE_METADATA),
        	new ItemStack(BTWItems.stone, 1, InventoryUtils.IGNORE_METADATA),
        	BTWItems.hempFibers
    	});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.axeStone ), new Object[] {
				Item.stick,
				new ItemStack(BTWItems.stone, 1, InventoryUtils.IGNORE_METADATA),
				new ItemStack(BTWItems.stone, 1, InventoryUtils.IGNORE_METADATA),
				BTWItems.sinew
		});
        
		RecipeManager.addRecipe(new ItemStack(Item.pickaxeStone ), new Object[] {
        	"XXX", 
        	" #S", 
        	" # ",         	
        	'#', Item.stick, 
        	'X', new ItemStack(BTWItems.stone, 1, InventoryUtils.IGNORE_METADATA),
        	'S', Item.silk
    	});
        
		RecipeManager.addRecipe(new ItemStack(Item.pickaxeStone ), new Object[] {
        	"XXX", 
        	" #S", 
        	" # ",         	
        	'#', Item.stick, 
        	'X', new ItemStack(BTWItems.stone, 1, InventoryUtils.IGNORE_METADATA),
        	'S', BTWItems.hempFibers
    	});
		
		RecipeManager.addRecipe(new ItemStack(Item.pickaxeStone ), new Object[] {
				"XXX",
				" #S",
				" # ",
				'#', Item.stick,
				'X', new ItemStack(BTWItems.stone, 1, InventoryUtils.IGNORE_METADATA),
				'S', BTWItems.sinew
		});
	}
	
	private static void addLooseStoneRecipes() {

		/** Strata info:
		 * using separate blocks: stairs
		 * using last 2 bits: full block and slab
		 * using first 2 bits: itemstone
		 * 
		 */		

		int altBitStrata = 0;
		Block stairsType = BTWBlocks.looseCobblestoneStairs;
		
		for( int strata = 0; strata < 3; strata++) {
			
			altBitStrata = strata << 2;
			
			if (strata == 1) {
				stairsType = BTWBlocks.looseMidStrataCobblestoneStairs;
			}
			else if (strata == 2) {
				stairsType = BTWBlocks.looseDeepStrataCobblestoneStairs;
			}
			
			RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseCobblestone, 1, altBitStrata), new Object[] {
				new ItemStack( BTWItems.stone, 1, strata ),
				new ItemStack( BTWItems.stone, 1, strata ),
				new ItemStack( BTWItems.stone, 1, strata ),
				new ItemStack( BTWItems.stone, 1, strata ),
				new ItemStack( BTWItems.stone, 1, strata ),
				new ItemStack( BTWItems.stone, 1, strata ),
				new ItemStack( BTWItems.stone, 1, strata ),
				new ItemStack( BTWItems.stone, 1, strata )
			});
			
			RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseCobblestoneSlab, 1, altBitStrata ), new Object[] {
				new ItemStack( BTWItems.stone, 1, strata ),
				new ItemStack( BTWItems.stone, 1, strata ),
				new ItemStack( BTWItems.stone, 1, strata ),
				new ItemStack( BTWItems.stone, 1, strata )
			});
			
			RecipeManager.addRecipe(new ItemStack(stairsType, 1 ), new Object[] {
				"#  ",
				"## ",
				"###",
				'#', new ItemStack( BTWItems.stone, 1, strata )
			});
			
			RecipeManager.addRecipe(new ItemStack(stairsType, 8 ), new Object[] {
				"#  ",
				"## ",
				"###",
				'#', new ItemStack( BTWBlocks.looseCobblestone, 1, altBitStrata )
			});
			
			RecipeManager.addRecipe(new ItemStack(stairsType, 4 ), new Object[] {
				"# ",
				"##",
				'#', new ItemStack( BTWBlocks.looseCobblestone, 1, altBitStrata )
			});
			
			RecipeManager.addRecipe(new ItemStack(stairsType, 2 ), new Object[] {
				"# ",
				"##",
				'#', new ItemStack( BTWBlocks.looseCobblestoneSlab, 1, altBitStrata )
			});
			
			RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseCobblestone, 1, altBitStrata ), new Object[] {
				"X",
				"X",
				'X', new ItemStack( BTWBlocks.looseCobblestoneSlab, 4, altBitStrata )
			});
			
			RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseCobblestoneSlab, 4, altBitStrata ), new Object[] {
				"XX",
				'X', new ItemStack( BTWBlocks.looseCobblestone, 1, altBitStrata )
			});
			
			RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.stone, 8, strata ), new Object[] {
				new ItemStack( BTWBlocks.looseCobblestone, 1, altBitStrata )
			});
			
			RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.stone, 4, strata ), new Object[] {
				new ItemStack( BTWBlocks.looseCobblestoneSlab, 1,  altBitStrata )
			});
			
			RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.stone, 6, strata ), new Object[] {
				new ItemStack( stairsType, 1 )
			});
		}
	}
	
	private static void addLooseBrickRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.unfiredBrick), new Object[] {
			new ItemStack( Item.clay )
		});
			
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseBrick), new Object[] {
			new ItemStack( Item.brick ), 
			new ItemStack( Item.brick ), 
			new ItemStack( Item.brick ), 
			new ItemStack( Item.brick ), 
			new ItemStack( Item.brick ), 
			new ItemStack( Item.brick ), 
			new ItemStack( Item.brick ), 
			new ItemStack( Item.brick ) 
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseBrickSlab), new Object[] {
			new ItemStack( Item.brick ), 
			new ItemStack( Item.brick ), 
			new ItemStack( Item.brick ), 
			new ItemStack( Item.brick ) 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseBrickStairs), new Object[] {
    		"#  ", 
    		"## ", 
    		"###", 
    		'#', new ItemStack( Item.brick ) 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseBrickStairs, 8 ), new Object[] {
    		"#  ", 
    		"## ", 
    		"###", 
    		'#', new ItemStack( BTWBlocks.looseBrick)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseBrickStairs, 4 ), new Object[] {
    		"# ", 
    		"##", 
    		'#', new ItemStack( BTWBlocks.looseBrick)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseBrickStairs, 2 ), new Object[] {
    		"# ", 
    		"##", 
    		'#', new ItemStack( BTWBlocks.looseBrickSlab)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseBrick), new Object[] {
    		"X", 
    		"X", 
    		'X', BTWBlocks.looseBrickSlab
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseBrickSlab, 4 ), new Object[] {
    		"XX", 
    		'X', BTWBlocks.looseBrick
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.brick, 8 ), new Object[] {
			new ItemStack( BTWBlocks.looseBrick)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.brick, 4 ), new Object[] {
			new ItemStack( BTWBlocks.looseBrickSlab)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.brick, 6 ), new Object[] {
			new ItemStack( BTWBlocks.looseBrickStairs)
		});
	}
	
	private static void addLooseStoneBrickRecipes()
	{
		/** Strata info:
		 * using separate blocks: stairs
		 * using last 2 bits: full block and slab
		 * using first 2 bits: itemstone
		 * 
		 */		

		int altBitStrata = 0;
		Block stairsType = BTWBlocks.looseStoneBrickStairs;
		
		for( int strata = 0; strata < 3; strata++) {
			
			altBitStrata = strata << 2;
			
			if (strata == 1) {
				stairsType = BTWBlocks.looseMidStrataStoneBrickStairs;
			}
			else if (strata == 2) {
				stairsType = BTWBlocks.looseDeepStrataStoneBrickStairs;
			}
			
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseStoneBrick, 1, altBitStrata ), new Object[] {
			new ItemStack( BTWItems.stoneBrick, 1, strata ),
			new ItemStack( BTWItems.stoneBrick, 1, strata ),
			new ItemStack( BTWItems.stoneBrick, 1, strata ),
			new ItemStack( BTWItems.stoneBrick, 1, strata )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseStoneBrickSlab, 1, altBitStrata ), new Object[] {
			new ItemStack( BTWItems.stoneBrick, 1, strata ),
			new ItemStack( BTWItems.stoneBrick, 1, strata ),
		});
		
		RecipeManager.addRecipe(new ItemStack(stairsType, 2 ), new Object[] {
    		"#  ", 
    		"## ", 
    		"###", 
    		'#', new ItemStack( BTWItems.stoneBrick, 1, strata )
		});
		
		RecipeManager.addRecipe(new ItemStack(stairsType, 8 ), new Object[] {
    		"#  ", 
    		"## ", 
    		"###", 
    		'#', new ItemStack( BTWBlocks.looseStoneBrick, 1, altBitStrata )
		});
		
		RecipeManager.addRecipe(new ItemStack(stairsType ), new Object[] {
    		"# ", 
    		"##", 
    		'#', new ItemStack( BTWItems.stoneBrick, 1, strata )
		});
		
		RecipeManager.addRecipe(new ItemStack(stairsType, 4 ), new Object[] {
    		"# ", 
    		"##", 
    		'#', new ItemStack( BTWBlocks.looseStoneBrick, 1, altBitStrata )
		});
		
		RecipeManager.addRecipe(new ItemStack(stairsType, 2 ), new Object[] {
    		"# ", 
    		"##", 
    		'#', new ItemStack( BTWBlocks.looseStoneBrickSlab, 1, altBitStrata )
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseStoneBrick, 1, altBitStrata ), new Object[] {
    		"X", 
    		"X", 
    		'X', new ItemStack( BTWBlocks.looseStoneBrickSlab, 1, altBitStrata )
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseStoneBrickSlab, 4, altBitStrata ), new Object[] {
    		"XX", 
    		'X', new ItemStack( BTWBlocks.looseStoneBrick, 1, altBitStrata )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.stoneBrick, 4, strata ), new Object[] {
			new ItemStack( BTWBlocks.looseStoneBrick, 1, altBitStrata )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.stoneBrick, 2, strata ), new Object[] {
			new ItemStack( BTWBlocks.looseStoneBrickSlab, 1, altBitStrata )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.stoneBrick, 3, strata ), new Object[] {
			new ItemStack( stairsType ) 
		});

		// stone splitting with chisel
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.stone, 2, strata ), new Object[] {
    		new ItemStack(BTWItems.ironChisel, 1, InventoryUtils.IGNORE_METADATA),
    		new ItemStack( BTWItems.stoneBrick, 1, strata )
		});
		
		RecipeManager.addShapelessRecipeWithSecondaryOutputIndicator(
			new ItemStack( BTWItems.stoneBrick, 4, strata ),
			new ItemStack(BTWItems.gravelPile),
			new Object[] {	    		
					new ItemStack(BTWItems.ironChisel, 1, InventoryUtils.IGNORE_METADATA),
					new ItemStack( Block.stone, 1, strata )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.stone, 2, strata ), new Object[] {
    		new ItemStack(BTWItems.diamondChisel, 1, InventoryUtils.IGNORE_METADATA),
    		new ItemStack( BTWItems.stoneBrick, 1, strata )
		});
		
		RecipeManager.addShapelessRecipeWithSecondaryOutputIndicator(
			new ItemStack( BTWItems.stoneBrick, 4, strata),
			new ItemStack(BTWItems.gravelPile),
			new Object[] {	    		
					new ItemStack(BTWItems.diamondChisel, 1, InventoryUtils.IGNORE_METADATA),
					new ItemStack( Block.stone, 1, strata )
		});
		}
	}
	
	private static void addLooseNetherBrickRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.unfiredNetherBrick), new Object[] {
			new ItemStack( BTWItems.netherSludge)
		});
			
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseNetherBrick), new Object[] {
			new ItemStack( BTWItems.netherBrick),
			new ItemStack( BTWItems.netherBrick),
			new ItemStack( BTWItems.netherBrick),
			new ItemStack( BTWItems.netherBrick),
			new ItemStack( BTWItems.netherBrick),
			new ItemStack( BTWItems.netherBrick),
			new ItemStack( BTWItems.netherBrick),
			new ItemStack( BTWItems.netherBrick)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseNetherBrickSlab), new Object[] {
			new ItemStack( BTWItems.netherBrick),
			new ItemStack( BTWItems.netherBrick),
			new ItemStack( BTWItems.netherBrick),
			new ItemStack( BTWItems.netherBrick)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseNEtherBrickStairs), new Object[] {
    		"#  ", 
    		"## ", 
    		"###", 
    		'#', new ItemStack( BTWItems.netherBrick)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseNEtherBrickStairs, 8 ), new Object[] {
    		"#  ", 
    		"## ", 
    		"###", 
    		'#', new ItemStack( BTWBlocks.looseNetherBrick)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseNEtherBrickStairs, 4 ), new Object[] {
    		"# ", 
    		"##", 
    		'#', new ItemStack( BTWBlocks.looseNetherBrick)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseNEtherBrickStairs, 2 ), new Object[] {
    		"# ", 
    		"##", 
    		'#', new ItemStack( BTWBlocks.looseNetherBrickSlab)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseNetherBrick), new Object[] {
    		"X", 
    		"X", 
    		'X', BTWBlocks.looseNetherBrickSlab
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseNetherBrickSlab, 4 ), new Object[] {
    		"XX", 
    		'X', BTWBlocks.looseNetherBrick
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.netherBrick, 8 ), new Object[] {
			new ItemStack( BTWBlocks.looseNetherBrick)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.netherBrick, 4 ), new Object[] {
			new ItemStack( BTWBlocks.looseNetherBrickSlab)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.netherBrick, 6 ), new Object[] {
			new ItemStack( BTWBlocks.looseNEtherBrickStairs)
		});
	}
	
	private static void addTorchRecipes()
	{
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.infiniteUnlitTorch, 1 ), new Object[] {
			"#",
			"X",
            Character.valueOf('#'), BTWItems.nethercoal,
            Character.valueOf('X'), Item.stick 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.finiteUnlitTorch), new Object[] {
			"#",
			"X",
            Character.valueOf('#'), new ItemStack(Item.coal, 1, InventoryUtils.IGNORE_METADATA),
            Character.valueOf('X'), Item.stick 
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.infiniteBurningTorch), new Object[] {
			new ItemStack( Block.torchWood )
		});
	}
	
	private static void addWickerRecipes()
	{
		RecipeManager.addRecipe(new ItemStack(BTWItems.wickerWeaving, 1,
								WickerWeavingItem.WICKER_WEAVING_MAX_DAMAGE - 1 ), new Object[] {
    		"##", 
    		"##", 
    		'#', Item.reed 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.wickerBasket), new Object[] {
    		"##", 
    		"##", 
    		'#', BTWItems.wickerPane
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.wickerPane, 4 ), new Object[] {
			new ItemStack( BTWBlocks.wickerBasket)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.hamper), new Object[] {
    		"###", 
    		"#P#", 
    		"###", 
    		'#', BTWItems.wickerPane,
    		'P', Block.planks 
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.wickerPane), new Object[] {
    		new ItemStack( BTWBlocks.gratePane),
    		new ItemStack( BTWItems.wickerPane)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.wickerBlock), new Object[] {
    		"##", 
    		"##", 
    		'#', BTWBlocks.wickerPane
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.wickerSlab), new Object[] {
    		"##", 
    		'#', BTWBlocks.wickerPane
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.wickerSlab, 4 ), new Object[] {
    		"##", 
    		'#', BTWBlocks.wickerBlock
		});

		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.wickerPane, 4 ), new Object[] {
    		new ItemStack( BTWBlocks.wickerBlock)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.wickerPane, 2 ), new Object[] {
    		new ItemStack( BTWBlocks.wickerSlab)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.wickerBlock), new Object[] {
    		new ItemStack( BTWBlocks.wickerSlab),
    		new ItemStack( BTWBlocks.wickerSlab)
		});
		
		// conversion of deprecated blocks
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.wickerBlock), new Object[] {
    		new ItemStack( BTWBlocks.aestheticOpaque, 1,
    			AestheticOpaqueBlock.SUBTYPE_WICKER)
		});

		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.wickerSlab), new Object[] {
    		new ItemStack( BTWBlocks.aestheticNonOpaque, 1,
    			AestheticNonOpaqueBlock.SUBTYPE_WICKER_SLAB)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.wickerPane), new Object[] {
    		new ItemStack( BTWItems.legacyWickerPane)
		});

		RecipeManager.addRecipe(new ItemStack(Item.carrotOnAStick, 1), new Object[] {
				"# ",
				" X",
				'#', Item.fishingRod,
				'X', BTWItems.carrot
		});
	}
	
	private static void addStairRecipes()
	{
		RecipeManager.addRecipe(new ItemStack(Block.stairsWoodOak, 6), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.planks, 1, 0)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsWoodBirch, 6), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.planks, 1, 2)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsWoodSpruce, 6), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.planks, 1, 1)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsWoodJungle, 6), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.planks, 1, 3)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsBrick, 6), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.brick)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsCobblestone, 6), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.cobblestone, 1, 0)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.midStrataCobblestoneStairs, 6), new Object[] {
				"#  ", 
				"## ", 
				"###", 
				'#', new ItemStack(Block.cobblestone, 1, 1)
			});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.deepStrataCobblestoneStairs, 6), new Object[] {
				"#  ", 
				"## ", 
				"###", 
				'#', new ItemStack(Block.cobblestone, 1, 2)
			});
		
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsNetherBrick, 6), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.netherBrick)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsNetherQuartz, 6), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.blockNetherQuartz)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsSandStone, 6), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.sandStone)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsStoneBrick, 6), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.stoneBrick, 1, 0)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.deepStrataStoneBrickStairs, 6), new Object[] {
				"#  ", 
				"## ", 
				"###", 
				'#', new ItemStack(Block.stoneBrick, 1, 2)
			});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.deepStrataStoneBrickStairs, 6), new Object[] {
				"#  ", 
				"## ", 
				"###", 
				'#', new ItemStack(Block.stoneBrick, 1, 2)
			});
	}
	
	private static void addWoolAndKnittingRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.knittingNeedles), new Object[] {
    		new ItemStack( BTWItems.pointyStick, 1, 0 ),
    		new ItemStack( BTWItems.pointyStick, 1, 0 )
		});
		
		RecipeManager.addShapedRecipeWithCustomClass(WoolArmorRecipe.class, new ItemStack(BTWItems.woolHelmet), new Object[] {
    		"##", 
    		'#', new ItemStack(BTWItems.woolKnit, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addShapedRecipeWithCustomClass(WoolArmorRecipe.class, new ItemStack(BTWItems.woolChest), new Object[] {
    		"##", 
    		"##", 
    		'#', new ItemStack(BTWItems.woolKnit, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addShapedRecipeWithCustomClass(WoolArmorRecipe.class, new ItemStack(BTWItems.woolLeggings), new Object[] {
    		"##", 
    		"# ", 
    		'#', new ItemStack(BTWItems.woolKnit, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addShapedRecipeWithCustomClass(WoolArmorRecipe.class, new ItemStack(BTWItems.woolLeggings), new Object[] {
    		"# ", 
    		"##", 
    		'#', new ItemStack(BTWItems.woolKnit, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addShapedRecipeWithCustomClass(WoolBlockRecipe.class, new ItemStack(Block.cloth ), new Object[] {
    		" # ", 
    		"#W#", 
    		" # ", 
    		'#', new ItemStack(BTWItems.wool, 1, InventoryUtils.IGNORE_METADATA),
    		'W', new ItemStack( BTWBlocks.wickerBlock)
		});
		
		for ( int iTempColor = 0; iTempColor < 16; iTempColor++ )
		{
			RecipeManager.addRecipe(new ItemStack(BTWBlocks.woolSlab, 6, iTempColor ), new Object[] {
	    		"###", 
	    		'#', new ItemStack( Block.cloth, 1, iTempColor )
			});
			
			RecipeManager.addShapelessRecipe(new ItemStack(Block.cloth, 1, iTempColor ), new Object[] {
				new ItemStack( BTWBlocks.woolSlab, 1, iTempColor ),
				new ItemStack( BTWBlocks.woolSlab, 1, iTempColor )
			});
		}
	}
	
	private static void addSawDustRecipes()
	{
		// wood items to saw dust as backup method of burning in campfire & brick furnace
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sawDust, 2 ), new Object[] {
			new ItemStack(BTWItems.firePlough, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sawDust, 2 ), new Object[] {
			new ItemStack(BTWItems.bowDrill, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sawDust, 2 ), new Object[] {
			new ItemStack(BTWItems.knittingNeedles, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sawDust, 4 ), new Object[] {
			new ItemStack(BTWItems.knitting, 1, InventoryUtils.IGNORE_METADATA)
		});
	}
	
	private static void addMeatCuringRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.curedMeat), new Object[] {
			new ItemStack( BTWItems.rawMutton), new ItemStack( BTWItems.nitre) });
			
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.curedMeat), new Object[] {
			new ItemStack( Item.chickenRaw ), new ItemStack( BTWItems.nitre) });
			
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.curedMeat), new Object[] {
			new ItemStack( Item.beefRaw ), new ItemStack( BTWItems.nitre) });
			
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.curedMeat), new Object[] {
			new ItemStack( Item.fishRaw ), new ItemStack( BTWItems.nitre) });
			
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.curedMeat), new Object[] {
			new ItemStack( Item.porkRaw ), new ItemStack( BTWItems.nitre) });
			
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.curedMeat), new Object[] {
			new ItemStack( BTWItems.rawWolfChop), new ItemStack( BTWItems.nitre) });
			
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.curedMeat), new Object[] {
			new ItemStack( BTWItems.rawMysteryMeat), new ItemStack( BTWItems.nitre) });
			
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.curedMeat), new Object[] {
			new ItemStack( BTWItems.rawLiver), new ItemStack( BTWItems.nitre) });
	}
	
	private static void addPaneRecipes()
	{
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.gratePane, 1 ), new Object[] {
    		"S#S", 
    		"###", 
    		"S#S", 
    		'#', new ItemStack( Item.stick ),
    		'S', new ItemStack( Item.silk )
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.gratePane, 1 ), new Object[] {
    		"S#S", 
    		"###", 
    		"S#S", 
    		'#', new ItemStack( Item.stick ),
    		'S', new ItemStack( BTWItems.hempFibers)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.gratePane, 1 ), new Object[] {
				"S#S",
				"###",
				"S#S",
				'#', new ItemStack( Item.stick ),
				'S', new ItemStack( BTWItems.sinew)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.slatsPane, 1 ), new Object[] {
    		"##", 
    		"##", 
    		'#', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		// conversion of deprecated items
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.gratePane), new Object[] {
    		new ItemStack( BTWItems.legacyGrate)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.slatsPane), new Object[] {
    		new ItemStack( BTWItems.legacySlats)
		});
	}
	
	private static void addSnowRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseSnowSlab), new Object[] {
			new ItemStack( Item.snowball ),
			new ItemStack( Item.snowball ),
			new ItemStack( Item.snowball ),
			new ItemStack( Item.snowball )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.snowball, 4 ), new Object[] {
    		new ItemStack( BTWBlocks.looseSnowSlab)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.snowball, 4 ), new Object[] {
    		new ItemStack( BTWBlocks.solidSnowSlab)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseSnow), new Object[] {
			new ItemStack( Item.snowball ),
			new ItemStack( Item.snowball ),
			new ItemStack( Item.snowball ),
			new ItemStack( Item.snowball ),
			new ItemStack( Item.snowball ),
			new ItemStack( Item.snowball ),
			new ItemStack( Item.snowball ),
			new ItemStack( Item.snowball )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseSnow), new Object[] {
			new ItemStack( BTWBlocks.looseSnowSlab),
			new ItemStack( BTWBlocks.looseSnowSlab)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.snowball, 8 ), new Object[] {
    		new ItemStack( BTWBlocks.looseSnow)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.snowball, 8 ), new Object[] {
    		new ItemStack( BTWBlocks.solidSnow)
		});
	}
	
	private static void addChickenFeedRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.chickenFeed), new Object[] {
    		new ItemStack( Item.dyePowder, 1, 15 ), // bone meal 
    		new ItemStack( Item.seeds )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.chickenFeed), new Object[] {
    		new ItemStack( Item.dyePowder, 1, 15 ), // bone meal 
    		new ItemStack( BTWItems.wheatSeeds)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.chickenFeed), new Object[] {
    		new ItemStack( Item.dyePowder, 1, 15 ), // bone meal 
    		new ItemStack( BTWItems.hempSeeds)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.chickenFeed), new Object[] {
    		new ItemStack( Item.dyePowder, 1, 15 ), // bone meal 
    		new ItemStack( Item.pumpkinSeeds )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.chickenFeed), new Object[] {
    		new ItemStack( Item.dyePowder, 1, 15 ), // bone meal 
    		new ItemStack( Item.melonSeeds )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.chickenFeed), new Object[] {
    		new ItemStack( Item.dyePowder, 1, 15 ), // bone meal 
    		new ItemStack(BTWItems.carrotSeeds)
		});
		
		RecipeManager.addCauldronRecipe(new ItemStack(BTWItems.chickenFeed), new ItemStack[] {
				new ItemStack(Item.dyePowder, 1, 15),
				new ItemStack(Item.seeds)
		});
		
		RecipeManager.addCauldronRecipe(new ItemStack(BTWItems.chickenFeed), new ItemStack[] {
				new ItemStack(Item.dyePowder, 1, 15),
				new ItemStack(BTWItems.wheatSeeds)
		});
		
		RecipeManager.addCauldronRecipe(new ItemStack(BTWItems.chickenFeed), new ItemStack[] {
				new ItemStack(Item.dyePowder, 1, 15),
				new ItemStack(BTWItems.hempSeeds)
		});
		
		RecipeManager.addCauldronRecipe(new ItemStack(BTWItems.chickenFeed), new ItemStack[] {
				new ItemStack(Item.dyePowder, 1, 15),
				new ItemStack(Item.pumpkinSeeds)
		});
		
		RecipeManager.addCauldronRecipe(new ItemStack(BTWItems.chickenFeed), new ItemStack[] {
				new ItemStack(Item.dyePowder, 1, 15),
				new ItemStack(Item.melonSeeds)
		});
		
		RecipeManager.addCauldronRecipe(new ItemStack(BTWItems.chickenFeed), new ItemStack[] {
				new ItemStack(Item.dyePowder, 1, 15),
				new ItemStack(BTWItems.carrotSeeds)
		});
	}
	
	private static void addFishingRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(Item.fishingRod ), new Object[] {
			new ItemStack( Item.stick ), 
			new ItemStack( Item.silk ),
			new ItemStack( Item.silk ),
			new ItemStack( BTWItems.ironNugget)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.fishingRod ), new Object[] {
			new ItemStack( Item.stick ), 
			new ItemStack( Item.silk ),
			new ItemStack( Item.silk ),
			new ItemStack( BTWItems.boneFishHook)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.fishingRod ), new Object[] {
				new ItemStack( Item.stick ),
				new ItemStack( BTWItems.sinew ),
				new ItemStack( BTWItems.sinew ),
				new ItemStack( BTWItems.ironNugget)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.fishingRod ), new Object[] {
				new ItemStack( Item.stick ),
				new ItemStack( BTWItems.sinew ),
				new ItemStack( BTWItems.sinew ),
				new ItemStack( BTWItems.boneFishHook)
		});

		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.boneCarving, 1,
										 BoneCarvingItem.DEFAULT_MAX_DAMAGE - 1 ), new Object[] {
			new ItemStack( Item.bone )
		});
	}
	
	private static void addDirtRecipes()
	{
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.looseDirtSlab, 4 ), new Object[] {
    		"##", 
    		'#', new ItemStack( BTWBlocks.looseDirt)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseDirtSlab, 1 ), new Object[] {
			new ItemStack( BTWItems.dirtPile),
			new ItemStack( BTWItems.dirtPile),
			new ItemStack( BTWItems.dirtPile),
			new ItemStack( BTWItems.dirtPile)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseDirt, 1 ), new Object[] {
			new ItemStack( BTWItems.dirtPile),
			new ItemStack( BTWItems.dirtPile),
			new ItemStack( BTWItems.dirtPile),
			new ItemStack( BTWItems.dirtPile),
			new ItemStack( BTWItems.dirtPile),
			new ItemStack( BTWItems.dirtPile),
			new ItemStack( BTWItems.dirtPile),
			new ItemStack( BTWItems.dirtPile)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseDirt), new Object[] {
    		new ItemStack( BTWBlocks.looseDirtSlab),
    		new ItemStack( BTWBlocks.looseDirtSlab)
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.dirtSlab, 4 ), new Object[] {
    		"##", 
    		'#', new ItemStack( Block.dirt )
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.dirtSlab, 4, DirtSlabBlock.SUBTYPE_PACKED_EARTH), new Object[] {
    		"EE", 
    		'E', new ItemStack( BTWBlocks.aestheticEarth, 1, AestheticOpaqueEarthBlock.SUBTYPE_PACKED_EARTH)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Block.dirt ), new Object[] {
    		new ItemStack( BTWBlocks.dirtSlab),
    		new ItemStack( BTWBlocks.dirtSlab)
		});

		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseDirt, 2 ), new Object[] {
    		new ItemStack( BTWBlocks.aestheticEarth, 1, AestheticOpaqueEarthBlock.SUBTYPE_PACKED_EARTH)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.aestheticEarth, 1, AestheticOpaqueEarthBlock.SUBTYPE_PACKED_EARTH), new Object[] {
    		new ItemStack( BTWBlocks.dirtSlab, 1, DirtSlabBlock.SUBTYPE_PACKED_EARTH),
    		new ItemStack( BTWBlocks.dirtSlab, 1, DirtSlabBlock.SUBTYPE_PACKED_EARTH)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.looseDirt, 1 ), new Object[] {
			new ItemStack( BTWBlocks.dirtSlab, 1, DirtSlabBlock.SUBTYPE_PACKED_EARTH)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.dirtPile, 8 ), new Object[] {
			new ItemStack( Block.dirt, 1 )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.dirtPile, 8 ), new Object[] {
			new ItemStack( BTWBlocks.looseDirt, 1 )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.dirtPile, 4 ), new Object[] {
    		new ItemStack( BTWBlocks.dirtSlab),
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.dirtPile, 4 ), new Object[] {
			new ItemStack( BTWBlocks.looseDirtSlab, 1 )
		});
	}
	
	private static void addGravelRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.sandAndGravelSlab, 1, SandAndGravelSlabBlock.SUBTYPE_GRAVEL), new Object[] {
    		new ItemStack( BTWItems.gravelPile),
    		new ItemStack( BTWItems.gravelPile),
    		new ItemStack( BTWItems.gravelPile),
    		new ItemStack( BTWItems.gravelPile)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Block.gravel ), new Object[] {
			new ItemStack( BTWItems.gravelPile),
			new ItemStack( BTWItems.gravelPile),
			new ItemStack( BTWItems.gravelPile),
			new ItemStack( BTWItems.gravelPile),
			new ItemStack( BTWItems.gravelPile),
			new ItemStack( BTWItems.gravelPile),
			new ItemStack( BTWItems.gravelPile),
			new ItemStack( BTWItems.gravelPile)
		});

		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.gravelPile, 8 ), new Object[] {
			new ItemStack( Block.gravel )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.gravelPile, 4 ), new Object[] {
			new ItemStack( BTWBlocks.sandAndGravelSlab, 1, SandAndGravelSlabBlock.SUBTYPE_GRAVEL)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.sandAndGravelSlab, 4, SandAndGravelSlabBlock.SUBTYPE_GRAVEL), new Object[] {
    		"##", 
    		'#', new ItemStack( Block.gravel )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Block.gravel ), new Object[] {
    		new ItemStack( BTWBlocks.sandAndGravelSlab, 1, SandAndGravelSlabBlock.SUBTYPE_GRAVEL),
    		new ItemStack( BTWBlocks.sandAndGravelSlab, 1, SandAndGravelSlabBlock.SUBTYPE_GRAVEL)
		});
	}
	
	private static void addSandRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.sandAndGravelSlab, 1, SandAndGravelSlabBlock.SUBTYPE_SAND), new Object[] {
			new ItemStack( BTWItems.sandPile),
			new ItemStack( BTWItems.sandPile),
			new ItemStack( BTWItems.sandPile),
			new ItemStack( BTWItems.sandPile)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Block.sand ), new Object[] {
			new ItemStack( BTWItems.sandPile),
			new ItemStack( BTWItems.sandPile),
			new ItemStack( BTWItems.sandPile),
			new ItemStack( BTWItems.sandPile),
			new ItemStack( BTWItems.sandPile),
			new ItemStack( BTWItems.sandPile),
			new ItemStack( BTWItems.sandPile),
			new ItemStack( BTWItems.sandPile)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sandPile, 4 ), new Object[] {
			new ItemStack( BTWBlocks.sandAndGravelSlab, 1, SandAndGravelSlabBlock.SUBTYPE_SAND)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sandPile, 8 ), new Object[] {
			new ItemStack( Block.sand )
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.sandAndGravelSlab, 4, SandAndGravelSlabBlock.SUBTYPE_SAND), new Object[] {
    		"##", 
    		'#', new ItemStack( Block.sand )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Block.sand ), new Object[] {
    		new ItemStack( BTWBlocks.sandAndGravelSlab, 1, SandAndGravelSlabBlock.SUBTYPE_SAND),
    		new ItemStack( BTWBlocks.sandAndGravelSlab, 1, SandAndGravelSlabBlock.SUBTYPE_SAND)
		});
	}
	
	private static void addMechanicalRecipes()
	{
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.axle), new Object[] {
    		"#X#", 
    		'#', Block.planks, 
    		'X', BTWItems.rope
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.axle), new Object[] {
    		"#X#", 
    		'#', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', BTWItems.rope
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.gearBox), new Object[] {
    		"#X#", 
    		"XYX", 
    		"#X#", 
    		'#', Block.planks, 
    		'X', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
    		'Y', BTWBlocks.axle
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.gearBox), new Object[] {
    		"#X#", 
    		"XYX", 
    		"#X#", 
    		'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
    		'Y', BTWBlocks.axle
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.redstoneClutch), new Object[] {
    		"#X#", 
    		"XYX", 
    		"#X#", 
    		'#', Block.planks, 
    		'X', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
    		'Y', BTWItems.redstoneLatch
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.redstoneClutch), new Object[] {
    		"#X#", 
    		"XYX", 
    		"#X#", 
    		'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
    		'Y', BTWItems.redstoneLatch
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.handCrank), new Object[] {
    		"  Y", 
    		" Y ", 
    		"#X#", 
    		'#', new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA),
    		'X', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
    		'Y', Item.stick 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.millstone), new Object[] {
    		"YYY", 
    		"YYY", 
    		"YXY", 
    		'X', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
    		'Y', new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA)
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.turntable), new Object[] {
    		"###", 
    		"ZXZ", 
    		"ZYZ", 
    		'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', Item.pocketSundial, 
    		'Y', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
    		'Z', new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.bellows), new Object[] {
    		"###", 
    		"XXX", 
    		"YZY", 
    		'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', BTWItems.tannedLeather,
    		'Y', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
    		'Z', BTWItems.belt
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.bellows), new Object[] {
    		"###", 
    		"XXX", 
    		"YZY", 
    		'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', BTWItems.cutTannedLeather,
    		'Y', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
    		'Z', BTWItems.belt
		});
	}
	
	private static void addOreRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.ironOreChunk), new Object[] {
    		new ItemStack( BTWItems.ironOrePile),
    		new ItemStack( BTWItems.ironOrePile)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.ironOreChunk), new Object[] {
    		new ItemStack( Block.oreIron )
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.ironOreChunkStorage), new Object[] {
    		"###", 
    		"###", 
    		"###", 
    		'#', BTWItems.ironOreChunk
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.ironOreChunk, 9 ), new Object[] {
    		new ItemStack( BTWBlocks.ironOreChunkStorage)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.goldOreChunk), new Object[] {
    		new ItemStack( BTWItems.goldOrePile),
    		new ItemStack( BTWItems.goldOrePile)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.goldOreChunk), new Object[] {
    		new ItemStack( Block.oreGold )
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.goldOreChunkStorage), new Object[] {
    		"###", 
    		"###", 
    		"###", 
    		'#', BTWItems.goldOreChunk
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.goldOreChunk, 9 ), new Object[] {
    		new ItemStack( BTWBlocks.goldOreChunkStorage)
		});
	}
	
	private static void addPastryRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.breadDough), new Object[] {
    		new ItemStack( BTWItems.flour),
    		new ItemStack( BTWItems.flour),
    		new ItemStack( BTWItems.flour)
		});
        
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.unbakedCookies, 1 ),
						   new Object[] {
			new ItemStack( BTWItems.chocolate),
			new ItemStack( BTWItems.flour),
			new ItemStack( BTWItems.flour),
			new ItemStack( BTWItems.flour),
			new ItemStack( BTWItems.flour)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.unbakedPumpkinPie, 1 ),
						   new Object[] {
    		new ItemStack( Item.sugar ),
    		new ItemStack( BTWBlocks.freshPumpkin),
    		new ItemStack( BTWItems.rawEgg),
    		new ItemStack( BTWItems.flour),
    		new ItemStack( BTWItems.flour),
    		new ItemStack( BTWItems.flour),
		});
		
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.unbakedCake, 1 ),
						   new Object[] {
            new ItemStack( Item.sugar ), 
            new ItemStack( Item.sugar ), 
            new ItemStack( Item.sugar ), 
            new ItemStack( Item.bucketMilk ),
            new ItemStack( Item.bucketMilk ),
            new ItemStack( BTWItems.flour),
            new ItemStack( BTWItems.flour),
            new ItemStack( BTWItems.flour),
            new ItemStack( BTWItems.rawEgg)
        });
	}
	
	private static void addBlockRecipes()
	{		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.aestheticEarth, 1,
								AestheticOpaqueEarthBlock.SUBTYPE_DUNG), new Object[] {
    		"###", 
    		"###", 
    		"###", 
    		'#', BTWItems.dung
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.aestheticOpaque, 1,
								AestheticOpaqueBlock.SUBTYPE_HELLFIRE), new Object[] {
	    		"###", 
	    		"###", 
	    		"###", 
	    		'#', BTWItems.concentratedHellfire
			});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.aestheticOpaque, 1,
								AestheticOpaqueBlock.SUBTYPE_PADDING), new Object[] {
	    		"###", 
	    		"###", 
	    		"###", 
	    		'#', BTWItems.padding
			});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.aestheticOpaque, 1,
								AestheticOpaqueBlock.SUBTYPE_SOAP), new Object[] {
	    		"###", 
	    		"###", 
	    		"###", 
	    		'#', BTWItems.soap
			});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.aestheticOpaque, 1,
								AestheticOpaqueBlock.SUBTYPE_ROPE), new Object[] {
	    		"###", 
	    		"###", 
	    		"###", 
	    		'#', BTWItems.rope
			});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.aestheticOpaque, 1,
								AestheticOpaqueBlock.SUBTYPE_FLINT), new Object[] {
	    		"###", 
	    		"###", 
	    		"###", 
	    		'#', Item.flint
			});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.aestheticOpaque, 1,
								AestheticOpaqueBlock.SUBTYPE_ENDER_BLOCK), new Object[] {
    		"###", 
    		"###", 
    		"###", 
    		'#', Item.enderPearl
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.miningCharge, 3 ), new Object[] {
    		"XYX", 
    		"###", 
    		"###", 
    		'#', BTWItems.dynamite,
    		'X', BTWItems.rope,
    		'Y', Item.slimeBall
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.aestheticVegetation, 1, AestheticVegetationBlock.SUBTYPE_VINE_TRAP), new Object[] {
	    		"###", 
	    		'#', Block.vine
			});

		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.soulforge, 1 ), new Object[] {
    		new ItemStack( Item.netherStar ),
    		new ItemStack( BTWItems.soulFlux),
    		new ItemStack( BTWBlocks.dormandSoulforge)
		});
        
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.lightBlockOff, 1 ), new Object[] {
        	" # ", 
        	"#X#", 
        	" Y ", 
        	'#', Block.thinGlass, 
        	'X', BTWItems.filament,
        	'Y', Item.redstone 
        });
        
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.hibachi), new Object[] {
    		"XXX", 
    		"#Z#", 
    		"#Y#", 
    		'#', new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA),
    		'X', BTWItems.concentratedHellfire,
    		'Y', Item.redstone, 
    		'Z', BTWItems.element
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.hopper), new Object[] {
    		"# #", 
    		"XYX", 
    		" Z ", 
    		'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
    		'Y', Block.pressurePlatePlanks,
    		'Z', new ItemStack(BTWItems.woodCornerStubID, 1, InventoryUtils.IGNORE_METADATA)
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.saw), new Object[] {
    		"YYY", 
    		"XZX", 
    		"#X#", 
    		'#', Block.planks, 
    		'X', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
    		'Y', Item.ingotIron,
    		'Z', BTWItems.belt
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.saw), new Object[] {
    		"YYY", 
    		"XZX", 
    		"#X#", 
    		'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
    		'Y', Item.ingotIron,
    		'Z', BTWItems.belt
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.platform), new Object[] {
    		"#X#", 
    		" # ", 
    		"#X#", 
    		'#', Block.planks, 
    		'X', BTWBlocks.wickerPane
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.platform), new Object[] {
    		"X#X", 
    		" X ", 
    		"X#X", 
    		'#', BTWBlocks.wickerPane,
    		'X', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.pulley), new Object[] {
    		"#Y#", 
    		"XZX", 
    		"#Y#", 
    		'#', Block.planks,
    		'X', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
            'Y', Item.ingotIron, 
    		'Z', BTWItems.redstoneLatch
		});
		
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.pulley), new Object[] {
    		"#Y#", 
    		"XZX", 
    		"#Y#", 
    		'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
            'Y', Item.ingotIron, 
    		'Z', BTWItems.redstoneLatch
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.cauldron), new Object[] {
    		"#Y#", 
    		"#X#", 
    		"###", 
    		'#', Item.ingotIron, 
    		'X', Item.bucketWater, 
    		'Y', Item.bone 
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.rail, 12 ), new Object[] {
            "X X", 
            "XSX", 
            "X X", 
            'X', BTWItems.ironNugget,
            'S', Item.stick
        });
		
		RecipeManager.addRecipe(new ItemStack(Block.railPowered, 6 ), new Object[] {
            "X X", 
            "XSX", 
            "XRX", 
            'X', BTWItems.ironNugget,
            'S', Item.stick,
            'R', BTWItems.redstoneLatch
        });

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.woodenDetectorRail, 6 ), new Object[] {
            "X X", 
            "X#X", 
            "XRX", 
            'X', BTWItems.ironNugget,
            'R', Item.redstone, 
            '#', Block.pressurePlatePlanks
        });
		
		RecipeManager.addRecipe(new ItemStack(Block.railDetector, 6 ), new Object[] {
            "X X", 
            "X#X", 
            "XRX", 
            'X', BTWItems.ironNugget,
            'R', Item.redstone, 
            '#', Block.pressurePlateStone
        });
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.steelDetectorRail, 6 ), new Object[] {
            "X X", 
            "X#X", 
            "XRX", 
            'X', BTWItems.ironNugget,
            'R', Item.redstone, 
            '#', BTWBlocks.steelPressurePlate
        });
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.planterWithSoil), new Object[] {
    		new ItemStack( BTWBlocks.planter),
    		new ItemStack( BTWBlocks.looseDirt)
		});

		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.planterWithSoil), new Object[] {
			new ItemStack( BTWBlocks.planter, 1, PlanterBlock.TYPE_SOIL)
		});

		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.planterWithSoil), new Object[] {
			new ItemStack( BTWBlocks.planter, 1, PlanterBlock.TYPE_SOIL_FERTILIZED)
		});

		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.planter, 1, PlanterBlock.TYPE_SOUL_SAND), new Object[] {
    		new ItemStack( BTWBlocks.planter),
    		new ItemStack( Block.slowSand )
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.screwPump), new Object[] {
    		"XGX", 
    		"WSW", 
    		"WgW", 
    		'W', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'g', new ItemStack(BTWItems.gear, 1, InventoryUtils.IGNORE_METADATA),
    		'S', BTWItems.screw,
    		'G', BTWBlocks.gratePane,
    		'X', BTWItems.glue
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.lens), new Object[] {
    		"GDG", 
    		"G G", 
    		"G#G", 
    		'#', Block.glass, 
    		'G', Item.ingotGold, 
    		'D', Item.diamond 
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.lens), new Object[] {
    		"G#G", 
    		"G G", 
    		"GDG", 
    		'#', Block.glass, 
    		'G', Item.ingotGold, 
    		'D', Item.diamond 
		});

		RecipeManager.addRecipe(new ItemStack(BTWBlocks.aestheticOpaque, 2, AestheticOpaqueBlock.SUBTYPE_BARREL), new Object[] {
    		"###", 
    		"#X#", 
    		"###", 
    		'#', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', BTWItems.glue
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.whiteStoneStairs, 6, 0 ), new Object[] {
    		"#  ", 
    		"## ", 
    		"###", 
    		'#', new ItemStack( BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_WHITE_STONE)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.whiteStoneStairs, 6, 8 ), new Object[] {
    		"#  ", 
    		"## ", 
    		"###", 
    		'#', new ItemStack( BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_WHITE_COBBLE)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.aestheticNonOpaque, 6, AestheticNonOpaqueBlock.SUBTYPE_WHITE_COBBLE_SLAB), new Object[] {
    		"###", 
    		'#', new ItemStack( BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_WHITE_COBBLE)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.skull.itemID, 1, 5 ), new Object[] {
    		new ItemStack( BTWItems.soulUrn),
    		new ItemStack( BTWItems.soulFlux),
    		new ItemStack( Item.skull.itemID, 1, 1 )
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.pistonShovel), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', Item.ingotIron 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.ironSpike), new Object[] {
    		"n", 
    		"n", 
    		"I", 
    		'n', BTWItems.ironNugget,
    		'I', Item.ingotIron 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.lightningRod), new Object[] {
    		"n", 
    		"n", 
    		"I", 
    		'n', Item.goldNugget, 
    		'I', Item.ingotGold
		});
	}
	
	private static void addItemRecipes()
	{
		RecipeManager.addRecipe(new ItemStack(BTWItems.gear, 2 ), new Object[] {
    		" X ", 
    		"X#X", 
    		" X ", 
    		'#', Block.planks, 
    		'X', Item.stick 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.rope, 1 ), new Object[] {
    		"##", 
    		"##", 
    		"##", 
    		'#', BTWItems.hempFibers
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.anchor), new Object[] {
    		" X ", 
    		"###", 
    		'#', new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA),
    		'X', Item.ingotIron 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.waterWheel), new Object[] {
			"###",
    		"# #", 
    		"###", 
    		'#', BTWItems.woodenBlade
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.windMillBlade), new Object[] {
    		"###", 
    		"XXX", 
    		'#', BTWItems.fabric,
    		'X', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.windMillBlade), new Object[] {
    		"###", 
    		"XXX", 
    		'#', BTWItems.fabric,
    		'X', Block.planks 
		});
		
		RecipeManager.addShapelessRecipeWithSecondaryOutputIndicator(new ItemStack(BTWItems.windMillBlade), new ItemStack(BTWItems.windMillBlade, 3), new Object[] {
			new ItemStack( BTWItems.windMill)
		});
		
		RecipeManager.addShapelessRecipeWithSecondaryOutputIndicator(new ItemStack(BTWItems.windMillBlade), new ItemStack(BTWItems.windMillBlade, 7), new Object[] {
			new ItemStack( BTWItems.verticalWindMill)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.windMill), new Object[] {
			" # ",
    		"# #", 
    		" # ", 
    		'#', BTWItems.windMillBlade
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.fabric, 1 ), new Object[] {
    		"###", 
    		"###", 
    		"###", 
    		'#', BTWItems.hempFibers
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.dung, 9 ), new Object[] {
    		new ItemStack( BTWBlocks.aestheticEarth, 1,
				AestheticOpaqueEarthBlock.SUBTYPE_DUNG)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.concentratedHellfire, 9 ), new Object[] {
    		new ItemStack( BTWBlocks.aestheticOpaque, 1,
				AestheticOpaqueBlock.SUBTYPE_HELLFIRE)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.padding, 9 ), new Object[] {
    		new ItemStack( BTWBlocks.aestheticOpaque, 1,
				AestheticOpaqueBlock.SUBTYPE_PADDING)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.soap, 9 ), new Object[] {
    		new ItemStack( BTWBlocks.aestheticOpaque, 1,
				AestheticOpaqueBlock.SUBTYPE_SOAP)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.rope, 9 ), new Object[] {
    		new ItemStack( BTWBlocks.aestheticOpaque, 1,
				AestheticOpaqueBlock.SUBTYPE_ROPE)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.flint, 9 ), new Object[] {
    		new ItemStack( BTWBlocks.aestheticOpaque, 1,
				AestheticOpaqueBlock.SUBTYPE_FLINT)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.enderPearl, 9 ), new Object[] {
    		new ItemStack( BTWBlocks.aestheticOpaque, 1,
				AestheticOpaqueBlock.SUBTYPE_ENDER_BLOCK)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.belt, 1 ), new Object[] {
    		" # ", 
    		"# #", 
    		" # ", 
    		'#', BTWItems.leatherStrap
		});

		RecipeManager.addRecipe(new ItemStack(BTWItems.woodenBlade, 1 ), new Object[] {
    		"#  ", 
    		"#X#", 
    		"#  ", 
    		'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', BTWItems.glue
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.haft, 1 ), new Object[] {
    		"Y", 
    		"X", 
    		"#", 
    		'#', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', BTWItems.glue,
    		'Y', BTWItems.leatherStrap,
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.bow, 1 ), new Object[] {
				"ST ",
				"S T",
				"ST ",
				'S', BTWItems.sinew,
				'T', Item.stick
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.compositeBow, 1 ), new Object[] {
    		"X#Y", 
    		"ZX#", 
    		"X#Y", 
    		'#', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', Item.bone, 
    		'Y', BTWItems.glue,
    		'Z', Item.silk, 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.compositeBow, 1 ), new Object[] {
				"X#Y",
				"ZX#",
				"X#Y",
				'#', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA),
				'X', Item.bone,
				'Y', BTWItems.glue,
				'Z', BTWItems.sinew,
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.broadheadArrow, 4 ), new Object[] {
    		new ItemStack( Item.feather ),
    		new ItemStack( Item.stick ),
    		new ItemStack( Item.silk ),
    		new ItemStack( BTWItems.broadheadArrowHead)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.broadheadArrow, 4 ), new Object[] {
    		new ItemStack( Item.feather ),
    		new ItemStack( Item.stick ),
    		new ItemStack( BTWItems.hempFibers),
    		new ItemStack( BTWItems.broadheadArrowHead)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.broadheadArrow, 4 ), new Object[] {
				new ItemStack( Item.feather ),
				new ItemStack( Item.stick ),
				new ItemStack( BTWItems.sinew),
				new ItemStack( BTWItems.broadheadArrowHead)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.gimpHelmet), new Object[] {
    		"###", 
    		"#I#", 
    		'#', BTWItems.tannedLeather,
    		'I', Item.ingotIron 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.gimpChest), new Object[] {
    		"# #", 
    		"I#I", 
    		"###", 
    		'#', BTWItems.tannedLeather,
    		'I', Item.ingotIron 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.gimpLeggings), new Object[] {
    		"#I#", 
    		"# #", 
    		"# #", 
    		'#', BTWItems.tannedLeather,
    		'I', Item.ingotIron 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.gimpBoots), new Object[] {
    		"# #", 
    		"I I", 
    		'#', BTWItems.tannedLeather,
    		'I', Item.ingotIron 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.gimpHelmet), new Object[] {
    		"###", 
    		"#I#", 
    		'#', BTWItems.cutTannedLeather,
    		'I', Item.ingotIron 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.gimpChest), new Object[] {
    		"# #", 
    		"I#I", 
    		"###", 
    		'#', BTWItems.cutTannedLeather,
    		'I', Item.ingotIron 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.gimpLeggings), new Object[] {
    		"#I#", 
    		"# #", 
    		"# #", 
    		'#', BTWItems.cutTannedLeather,
    		'I', Item.ingotIron 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.gimpBoots), new Object[] {
    		"# #", 
    		"I I", 
    		'#', BTWItems.cutTannedLeather,
    		'I', Item.ingotIron 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.padding), new Object[] {
    		"C", 
    		"W", 
    		"C", 
    		'C', BTWItems.fabric,
    		'W', new ItemStack(BTWItems.wool, 1, InventoryUtils.IGNORE_METADATA)
        });
        
		RecipeManager.addRecipe(new ItemStack(BTWItems.dynamite, 2 ), new Object[] {
    		"PF", 
    		"PN", 
    		"PS", 
    		'P', Item.paper, 
    		'F', BTWItems.fuse,
    		'N', BTWItems.blastingOil,
    		'S', BTWItems.sawDust
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.dynamite, 2 ), new Object[] {
    		"PF", 
    		"PN", 
    		"PS", 
    		'P', Item.paper, 
    		'F', BTWItems.fuse,
    		'N', BTWItems.blastingOil,
    		'S', BTWItems.soulDust
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.breedingHarness), new Object[] {
    		"SLS", 
    		"LLL", 
    		"SLS", 
    		'S', BTWItems.leatherStrap,
    		'L', BTWItems.tannedLeather
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.breedingHarness), new Object[] {
    		"SLS", 
    		"LLL", 
    		"SLS", 
    		'S', BTWItems.leatherStrap,
    		'L', BTWItems.cutTannedLeather
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.candle, 4, 16), new Object[] {
    		"H", 
    		"T", 
    		'H', BTWItems.hempFibers,
    		'T', BTWItems.tallow
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.candle, 4, 16), new Object[] {
    		new ItemStack(BTWItems.legacyCandle)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.screw), new Object[] {
    		"n# ", 
    		" #n", 
    		"n# ", 
    		'#', new ItemStack(Item.ingotIron),
    		'n', new ItemStack(BTWItems.ironNugget)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.ocularOfEnder, 1, 0 ), new Object[] {
    		"ggg", 
    		"gEg", 
    		"ggg", 
    		'g', Item.goldNugget, 
    		'E', Item.enderPearl 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.enderSpectacles), new Object[] {
    		"OSO", 
    		'S', BTWItems.leatherStrap,
    		'O', BTWItems.ocularOfEnder
		});

		RecipeManager.addRecipe(new ItemStack(BTWItems.stake, 2 ), new Object[] {
    		"S", 
    		"M", 
    		'M', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'S', new ItemStack( Item.silk )
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.verticalWindMill), new Object[] {
			"SSS",
    		"S S", 
    		"SSS", 
    		'S', BTWItems.windMillBlade
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 2 ), new Object[] {
    		new ItemStack( Item.bread ),
    		new ItemStack( Item.beefCooked )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 2 ), new Object[] {
    		new ItemStack( Item.bread ),
    		new ItemStack( Item.porkCooked )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 2 ), new Object[] {
    		new ItemStack( Item.bread ),
    		new ItemStack( Item.chickenCooked )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 2 ), new Object[] {
    		new ItemStack( Item.bread ),
    		new ItemStack( Item.fishCooked )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 2 ), new Object[] {
    		new ItemStack( Item.bread ),
    		new ItemStack( BTWItems.cookedWolfChop)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 2 ), new Object[] {
    		new ItemStack( Item.bread ),
    		new ItemStack( BTWItems.cookedMutton)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 2 ), new Object[] {
    		new ItemStack( Item.bread ),
    		new ItemStack( BTWItems.cookedMysteryMeat)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.steakAndPotatoes, 2 ), new Object[] {
    		new ItemStack( Item.bakedPotato ),
    		new ItemStack( Item.beefCooked )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.steakAndPotatoes, 2 ), new Object[] {
    		new ItemStack( BTWItems.boiledPotato),
    		new ItemStack( Item.beefCooked )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.hamAndEggs, 2 ), new Object[] {
    		new ItemStack( BTWItems.hardBoiledEgg),
    		new ItemStack( Item.porkCooked )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.hamAndEggs, 2 ), new Object[] {
    		new ItemStack( BTWItems.friedEgg),
    		new ItemStack( Item.porkCooked )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.steakDinner, 3 ), new Object[] {
    		new ItemStack( BTWItems.boiledPotato),
    		new ItemStack( BTWItems.cookedCarrot),
    		new ItemStack( Item.beefCooked )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.steakDinner, 3 ), new Object[] {
    		new ItemStack( Item.bakedPotato ),
    		new ItemStack( BTWItems.cookedCarrot),
    		new ItemStack( Item.beefCooked )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.porkDinner, 3 ), new Object[] {
    		new ItemStack( BTWItems.boiledPotato),
    		new ItemStack( BTWItems.cookedCarrot),
    		new ItemStack( Item.porkCooked )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.porkDinner, 3 ), new Object[] {
    		new ItemStack( Item.bakedPotato ),
    		new ItemStack( BTWItems.cookedCarrot),
    		new ItemStack( Item.porkCooked )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.wolfDinner, 3 ), new Object[] {
    		new ItemStack( BTWItems.boiledPotato),
    		new ItemStack( BTWItems.cookedCarrot),
    		new ItemStack( BTWItems.cookedWolfChop)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.wolfDinner, 3 ), new Object[] {
    		new ItemStack( Item.bakedPotato ),
    		new ItemStack( BTWItems.cookedCarrot),
    		new ItemStack( BTWItems.cookedWolfChop)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.rawKebab, 3 ), new Object[] {
    		new ItemStack( BTWItems.brownMushroom),
    		new ItemStack( BTWItems.carrot),
    		new ItemStack( BTWItems.rawMutton),
    		new ItemStack( Item.stick ),
		});
		
		RecipeManager.addShapelessRecipeWithSecondaryOutputIndicator(new ItemStack(Block.pumpkin ), new ItemStack(Item.pumpkinSeeds, 4), new Object[] {
			new ItemStack( BTWBlocks.freshPumpkin)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.rawMushroomOmelet, 2 ), new Object[] {
    		new ItemStack( BTWItems.brownMushroom),
    		new ItemStack( BTWItems.brownMushroom),
    		new ItemStack( BTWItems.brownMushroom),
    		new ItemStack( BTWItems.rawEgg)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.rawScrambledEggs, 2 ), new Object[] {
    		new ItemStack( BTWItems.rawEgg),
    		new ItemStack( Item.bucketMilk )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.rawScrambledEggs, 2 ), new Object[] {
    		new ItemStack( BTWItems.hardBoiledEgg),
    		new ItemStack( Item.bucketMilk )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.rawScrambledEggs, 2 ), new Object[] {
    		new ItemStack( BTWItems.friedEgg),
    		new ItemStack( Item.bucketMilk )
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.helmetChain ), new Object[] {
    		"###", 
    		"# #", 
    		'#', BTWItems.mail
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.plateChain ), new Object[] {
    		"# #", 
    		"###", 
    		"###", 
    		'#', BTWItems.mail
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.legsChain ), new Object[] {
    		"###", 
    		"# #", 
    		"# #", 
    		'#', BTWItems.mail
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.bootsChain ), new Object[] {
    		"# #", 
    		"# #", 
    		'#', BTWItems.mail
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.paddedHelmet), new Object[] {
    		"###", 
    		"# #", 
    		'#', BTWItems.padding
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.paddedChest), new Object[] {
    		"# #", 
    		"###", 
    		"###", 
    		'#', BTWItems.padding
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.paddedLeggings), new Object[] {
    		"###", 
    		"# #", 
    		"# #", 
    		'#', BTWItems.padding
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.tannedLeatherHelmet), new Object[] {
    		"###", 
    		"# #", 
    		'#', BTWItems.tannedLeather
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.tannedLeatherChest), new Object[] {
    		"# #", 
    		"###", 
    		"###", 
    		'#', BTWItems.tannedLeather
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.tannedLeatherLeggings), new Object[] {
    		"###", 
    		"# #", 
    		"# #", 
    		'#', BTWItems.tannedLeather
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.tannedLeatherBoots), new Object[] {
    		"# #", 
    		"# #", 
    		'#', BTWItems.tannedLeather
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.tannedLeatherHelmet), new Object[] {
    		"###", 
    		"# #", 
    		'#', BTWItems.cutTannedLeather
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.tannedLeatherChest), new Object[] {
    		"# #", 
    		"###", 
    		"###", 
    		'#', BTWItems.cutTannedLeather
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.tannedLeatherLeggings), new Object[] {
    		"###", 
    		"# #", 
    		"# #", 
    		'#', BTWItems.cutTannedLeather
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.tannedLeatherBoots), new Object[] {
    		"# #", 
    		"# #", 
    		'#', BTWItems.cutTannedLeather
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.diamondIngot), new Object[] {
    		new ItemStack( Item.ingotIron ),
    		new ItemStack( Item.diamond ),
    		new ItemStack( BTWItems.creeperOysters)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.cutLeather, 2 ), new Object[] {
    		new ItemStack(Item.shears, 1, InventoryUtils.IGNORE_METADATA),
    		new ItemStack( Item.leather )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.cutTannedLeather, 2 ), new Object[] {
    		new ItemStack(Item.shears, 1, InventoryUtils.IGNORE_METADATA),
    		new ItemStack( BTWItems.tannedLeather)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.cutScouredLeather, 2 ), new Object[] {
    		new ItemStack(Item.shears, 1, InventoryUtils.IGNORE_METADATA),
    		new ItemStack( BTWItems.scouredLeather)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.leatherStrap, 4 ), new Object[] {
    		new ItemStack(Item.shears, 1, InventoryUtils.IGNORE_METADATA),
    		new ItemStack( BTWItems.cutTannedLeather)
		});

		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.cutLeather, 2), new Object[] {
				new ItemStack(BTWItems.diamondShears, 1, 32767),
				new ItemStack(Item.leather)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.cutTannedLeather, 2), new Object[] {
				new ItemStack(BTWItems.diamondShears, 1, 32767),
				new ItemStack(BTWItems.tannedLeather)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.cutScouredLeather, 2), new Object[] {
				new ItemStack(BTWItems.diamondShears, 1, 32767),
				new ItemStack(BTWItems.scouredLeather)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.leatherStrap, 4), new Object[] {
				new ItemStack(BTWItems.diamondShears, 1, 32767),
				new ItemStack(BTWItems.cutTannedLeather)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.cutLeather, 2), new Object[] {
				new ItemStack(BTWItems.diamondShears, 1, 32767),
				new ItemStack(Item.leather)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.cutTannedLeather, 2), new Object[] {
				new ItemStack(BTWItems.diamondShears, 1, 32767),
				new ItemStack(BTWItems.tannedLeather)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.cutScouredLeather, 2), new Object[] {
				new ItemStack(BTWItems.diamondShears, 1, 32767),
				new ItemStack(BTWItems.scouredLeather)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.leatherStrap, 4), new Object[] {
				new ItemStack(BTWItems.diamondShears, 1, 32767),
				new ItemStack(BTWItems.cutTannedLeather)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.goldenDung), new Object[] {
    		"ggg", 
    		"gDg", 
    		"ggg", 
    		'D', BTWItems.dung,
    		'g', Item.goldNugget,
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.redstoneLatch), new Object[] {
    		"ggg", 
    		" r ", 
    		'g', Item.goldNugget,
    		'r', Item.redstone
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.milkChocolateBucket), new Object[] {
    		new ItemStack( Item.bucketMilk ),
    		new ItemStack( Item.dyePowder, 1, 3 ) // Cocoa powder
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.stumpRemover, 2 ), new Object[] {
    		new ItemStack( BTWItems.creeperOysters),
    		new ItemStack( BTWItems.redMushroom),
    		new ItemStack( Item.rottenFlesh )
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.redstoneEye, 2 ), new Object[] {
    		"###", 
    		"GGG", 
    		" R ", 
    		'#', new ItemStack( Item.dyePowder, 1, 4 ), 
    		'G', Item.goldNugget, 
    		'R', Item.redstone 
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.comparator), new Object[] {
	    	" R ",
    		"RER",
    		"SSS",
    		'E', BTWItems.redstoneEye,
    		'S', new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA),
    		'R', Block.torchRedstoneActive
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.corpseEye, 1 ), new Object[] {
	    		new ItemStack( BTWItems.soulUrn),
	    		new ItemStack( Item.enderPearl ),
	    		new ItemStack( BTWItems.rawMysteryMeat)
			});
		
		// Recipe awaiting renaming functionality to be enabled
		//RecipeManager.addShapelessRecipe(new ItemStack(FCBetterThanWolves.fcItemNameTag), new Object[] {	    		
	    //	new ItemStack(Item.leather),
	    //	new ItemStack(FCBetterThanWolves.fcItemNuggetIron)
		//});
		
		//RecipeManager.addShapelessRecipe(new ItemStack(FCBetterThanWolves.fcItemNameTag), new Object[] {	    		
	    //	new ItemStack(FCBetterThanWolves.fcItemLeatherCut),
	    //	new ItemStack(FCBetterThanWolves.fcItemNuggetIron)
		//});
	}
	
	private static void addDyeRecipes()
	{
		// only to 14 to avoid white on white conversion
        for ( int i = 0; i < 15; i++ )
        {
            RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.wool, 1, i ), new Object[] {
                new ItemStack( Item.dyePowder, 1, i ), 
                new ItemStack( BTWItems.wool, 1, 15 )
            });
        }
        
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.candle, 1, 3 ), new Object[] {
            new ItemStack( BTWItems.dung),
            new ItemStack( BTWItems.candle, 1, 16 )
        });
        
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.wool, 1, 3 ), new Object[] {
            new ItemStack( BTWItems.dung),
            new ItemStack( BTWItems.wool, 1, 15 )
        });
        
        for ( int i = 0; i < 15; i++ )
        {
        	RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.candle, 1, i), new Object[] {
                    new ItemStack(Item.dyePowder, 1, i), 
                    new ItemStack(BTWItems.candle, 1, 16)
            });
        	
            RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.vase, 1, BlockCloth.getBlockFromDye(i) ), new Object[] {
                new ItemStack( Item.dyePowder, 1, i ), 
                new ItemStack( Item.itemsList[BTWBlocks.vase.blockID], 1, 0 )
            });
            
            RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.woolSlab, 1, BlockCloth.getBlockFromDye(i) ), new Object[] {
                new ItemStack( Item.dyePowder, 1, i ), 
                new ItemStack( Item.itemsList[BTWBlocks.woolSlab.blockID], 1, 0 )
            });
        }
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.candle, 1, ColorUtils.WHITE.colorID), new Object[] {
				new ItemStack( Item.dyePowder, 1, ColorUtils.WHITE.colorID),
				new ItemStack( BTWItems.candle, 1, 16 )
		});
		
        RecipeManager.addShapelessRecipe(new ItemStack(Block.cloth, 1, 12 ), new Object[] {
            new ItemStack( BTWItems.dung),
            new ItemStack( Item.itemsList[Block.cloth.blockID], 1, 0 )
        });
        
        RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.vase, 1, 12 ), new Object[] {
            new ItemStack( BTWItems.dung),
            new ItemStack( Item.itemsList[BTWBlocks.vase.blockID], 1, 0 )
        });
        
        RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.woolSlab, 1, 12 ), new Object[] {
            new ItemStack( BTWItems.dung),
            new ItemStack( Item.itemsList[BTWBlocks.woolSlab.blockID], 1, 0 )
        });
	}
	
	private static void addAlternateVanillaRecipes()
	{
		// Alternate recipes for vanilla items

		RecipeManager.addRecipe(new ItemStack(Block.pistonBase, 1 ), new Object[] {
            "#I#", 
            "XYX",
            "XZX",
            Character.valueOf('#'), new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
            Character.valueOf('I'), Item.ingotIron,
            Character.valueOf('X'), new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA),
            Character.valueOf('Y'), BTWItems.soulUrn,
            Character.valueOf('Z'), BTWItems.redstoneLatch
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.fenceGate, 1 ), new Object[] {
            "#X#", 
            Character.valueOf('#'), new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA),
            Character.valueOf('X'), new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsWoodOak, 1 ), new Object[] {
            "# ", 
            "##", 
            Character.valueOf('#'), new ItemStack( BTWItems.woodMouldingStubID, 1, 0 )
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsWoodSpruce, 1 ), new Object[] {
            "# ", 
            "##", 
            Character.valueOf('#'), new ItemStack( BTWItems.woodMouldingStubID, 1, 1 )
		});

		RecipeManager.addRecipe(new ItemStack(Block.stairsWoodBirch, 1 ), new Object[] {
            "# ", 
            "##", 
            Character.valueOf('#'), new ItemStack( BTWItems.woodMouldingStubID, 1, 2 )
		});

		RecipeManager.addRecipe(new ItemStack(Block.stairsWoodJungle, 1 ), new Object[] {
            "# ", 
            "##", 
            Character.valueOf('#'), new ItemStack( BTWItems.woodMouldingStubID, 1, 3 )
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsStoneBrick ), new Object[] {
            "# ", 
            "##", 
            Character.valueOf('#'), new ItemStack( BTWBlocks.stoneBrickMouldingAndDecorative)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.midStrataStoneBrickStairs), new Object[] {
	            "# ", 
	            "##", 
	            Character.valueOf('#'), new ItemStack( BTWBlocks.midStrataStoneBrickMouldingAndDecorative)
			});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.deepStrataStoneBrickStairs), new Object[] {
	            "# ", 
	            "##", 
	            Character.valueOf('#'), new ItemStack( BTWBlocks.deepStrataStoneBrickMouldingAndDecorative)
			});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.whiteStoneStairs), new Object[] {
            "# ", 
            "##", 
            Character.valueOf('#'), new ItemStack( BTWBlocks.whiteStoneMouldingAndDecroative)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsNetherBrick ), new Object[] {
            "# ", 
            "##", 
            Character.valueOf('#'), new ItemStack( BTWBlocks.netherBrickMouldingAndDecorative)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsBrick ), new Object[] {
            "# ", 
            "##", 
            Character.valueOf('#'), new ItemStack( BTWBlocks.brickMouldingAndDecorative)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsSandStone ), new Object[] {
            "# ", 
            "##", 
            Character.valueOf('#'), new ItemStack( BTWBlocks.sandstoneMouldingAndDecorative)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stairsNetherQuartz ), new Object[] {
            "# ", 
            "##", 
            Character.valueOf('#'), new ItemStack( BTWBlocks.blackStoneMouldingAndDecorative)
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.sign, 3 ), new Object[] {
            "#", 
            "X", 
            Character.valueOf('#'), new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
            Character.valueOf('X'), new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.doorWood, 1 ), new Object[] {
            "##", 
            "##", 
            "##", 
            Character.valueOf('#'), new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.trapdoor, 1 ), new Object[] {
			"WW#", 
			"WW#", 
			Character.valueOf('#'), Item.stick, 
			Character.valueOf('W'), Block.planks
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.trapdoor, 2 ), new Object[] {
			"WW#", 
			"WW#", 
			Character.valueOf('#'), Item.stick, 
            Character.valueOf('W'), new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.boat, 1 ), new Object[] {
            "# #", 
            "###", 
            Character.valueOf('#'), new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA)
		});
        
		RecipeManager.addRecipe(new ItemStack(Block.bookShelf ), new Object[] {
    		"###", 
    		"XXX", 
    		"###", 
    		'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
    		'X', Item.book
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.chest), new Object[] {
            "###", 
            "# #", 
            "###", 
    		'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA)
        });
		
		RecipeManager.addRecipe(new ItemStack(Item.minecartCrate, 1 ), new Object[] {
			"A", 
			"B", 
			'A', BTWBlocks.chest,
			'B', Item.minecartEmpty
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.redstoneRepeater, 1 ), new Object[] {
        	"#X#", 
        	"III", 
        	'#', Block.torchRedstoneActive, 
        	'X', Item.pocketSundial, 
        	'I', new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA) });
        
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.infernalEnchanter), new Object[] {
    		"CBC", 
    		"SES", 
    		"SSS", 
    		'S', BTWItems.soulforgedSteelIngot,
    		'C', new ItemStack( BTWItems.candle, 1, 0 ),
    		'E', Block.enchantmentTable,
    		'B', Item.bone
		});
		
        RecipeManager.addShapelessRecipe(new ItemStack(Item.stick ), new Object[] {
        	new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA)
        });
        
		RecipeManager.addRecipe(new ItemStack(Block.jukebox, 1 ), new Object[] {
			"###", 
			"#X#", 
			"###", 
			'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
			'X', Item.diamond
        });
		
		RecipeManager.addRecipe(new ItemStack(Block.music, 1 ), new Object[] {
			"###", 
			"#X#", 
			"###", 
			'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
			'X', BTWItems.redstoneLatch
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.tnt, 1), new Object[] {
			"GFG", 
			"GBG", 
			"GGG", 
			'B', new ItemStack( BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_BARREL),
			'G', Item.gunpowder,
			'F', BTWItems.fuse
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.gunpowder ), new Object[] {
            new ItemStack( BTWItems.nitre),
            new ItemStack( BTWItems.brimstone),
            new ItemStack( BTWItems.coalDust),
        });
		
		RecipeManager.addRecipe(new ItemStack(Block.anvil, 1 ), new Object[] {
			"iii", 
			" i ", 
			"iii", 
			'i', Item.ingotIron
		});
		
		// chiseled stone brick
		
		for (int strata = 0; strata < 3; strata++) {
			RecipeManager.addSoulforgeRecipe(new ItemStack(Block.stoneBrick, 12, 3 + (strata << 2)), new Object[] {
		    		"####", 
		    		"#  #", 
		    		"#  #", 
		    		"####", 
		    		'#', new ItemStack(Block.stoneBrick, 1, strata << 2)
				});
		}
		
		RecipeManager.addRecipe(new ItemStack(Item.bowlEmpty, 6 ), new Object[] {
            "# #", 
            " # ", 
            Character.valueOf('#'), new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.compass, 1 ), new Object[] {
			" # ", 
			"#X#", 
			" # ", 
			'#', BTWItems.ironNugget,
			'X', Item.redstone
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.pocketSundial, 1 ), new Object[] {
			" # ", 
			"#X#", 
			" # ", 
			'#', Item.goldNugget, 
			'X', Item.netherQuartz });
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.flintAndSteel, 1 ), new Object[] {
			new ItemStack( BTWItems.ironNugget),
			new ItemStack( Item.flint )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.clay, 9 ), new Object[] {
            new ItemStack( BTWBlocks.unfiredClay),
        });
		
		RecipeManager.addRecipe(new ItemStack(Item.bucketEmpty, 1 ), new Object[] {
			"# #", 
			"# #", 
			"###", 
			'#', BTWItems.ironNugget
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.silk, 2), new Object[] {
            new ItemStack(BTWBlocks.web),
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.webUntangling, 1, ProgressiveCraftingItem.DEFAULT_MAX_DAMAGE - 1), new Object[] {
				new ItemStack(BTWItems.tangledWeb),
				new ItemStack(BTWItems.sharpStone)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.silk, 2), new Object[] {
				new ItemStack(BTWItems.tangledWeb),
				new ItemStack(Item.shears, 1, InventoryUtils.IGNORE_METADATA)
		});
		RecipeManager.addShapelessRecipe(new ItemStack(Item.silk, 2), new Object[] {
				new ItemStack(BTWItems.tangledWeb),
				new ItemStack(BTWItems.diamondShears, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingBeef, 1, ProgressiveCraftingItem.DEFAULT_MAX_DAMAGE), new Object[] {
				new ItemStack(Item.beefRaw),
				new ItemStack(Item.beefRaw),
				new ItemStack(BTWItems.sharpStone)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingBeef, 1, ProgressiveCraftingItem.DEFAULT_MAX_DAMAGE), new Object[] {
				new ItemStack(Item.beefCooked),
				new ItemStack(Item.beefCooked),
				new ItemStack(BTWItems.sharpStone)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingWolf, 1, ProgressiveCraftingItem.DEFAULT_MAX_DAMAGE), new Object[] {
				new ItemStack(BTWItems.rawWolfChop),
				new ItemStack(BTWItems.rawWolfChop),
				new ItemStack(BTWItems.sharpStone)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingWolf, 1, ProgressiveCraftingItem.DEFAULT_MAX_DAMAGE), new Object[] {
				new ItemStack(BTWItems.cookedWolfChop),
				new ItemStack(BTWItems.cookedWolfChop),
				new ItemStack(BTWItems.sharpStone)
		});
		
		// high effeciency leather recipes
		
		RecipeManager.addRecipe(new ItemStack(Item.helmetLeather ), new Object[] {
    		"###",
    		"# #",
    		'#', BTWItems.cutLeather
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.plateLeather ), new Object[] {
    		"# #",
    		"###", 
    		"###", 
    		'#', BTWItems.cutLeather
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.legsLeather ), new Object[] {
    		"###", 
    		"# #", 
    		"# #", 
    		'#', BTWItems.cutLeather
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.bootsLeather ), new Object[] {
    		"# #", 
    		"# #", 
    		'#', BTWItems.cutLeather
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.writableBook, 1 ), new Object[] {
			Item.paper, 
			Item.paper, 
			Item.paper, 
			BTWItems.cutLeather,
			new ItemStack( Item.dyePowder, 1, 0 ), 
			Item.feather
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.writableBook, 1 ), new Object[] {
			Item.paper, 
			Item.paper, 
			Item.paper, 
			Item.leather,
			new ItemStack( Item.dyePowder, 1, 0 ), 
			Item.feather
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.itemFrame, 1 ), new Object[] {
			"mmm", 
			"mlm", 
			"mmm", 
			'm', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA),
			'l', BTWItems.cutLeather
		});
        
		RecipeManager.addRecipe(new ItemStack(Item.itemFrame, 1 ), new Object[] {
			"mmm", 
			"mlm", 
			"mmm", 
			'm', Item.stick,
			'l', BTWItems.cutLeather
		});
        
		RecipeManager.addRecipe(new ItemStack(Item.helmetDiamond ), new Object[] {
			"XXX", 
			"XYX", 
			'X', BTWItems.diamondIngot,
			'Y', BTWItems.diamondArmorPlate
		});
	   
		RecipeManager.addRecipe(new ItemStack(Item.plateDiamond ), new Object[] {
			"Y Y", 
			"XXX", 
			"XXX", 
			'X', BTWItems.diamondIngot,
			'Y', BTWItems.diamondArmorPlate
		});
	   
		RecipeManager.addRecipe(new ItemStack(Item.legsDiamond ), new Object[] {
			"XXX", 
			"Y Y", 
			"Y Y", 
			'X', BTWItems.diamondIngot,
			'Y', BTWItems.diamondArmorPlate
		});
	   
		RecipeManager.addRecipe(new ItemStack(Item.bootsDiamond ), new Object[] {
			"X X", 
			"X X", 
			'X', BTWItems.diamondIngot
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.swordDiamond ), new Object[] {
			"X", 
			"X", 
			"#",
			'#', Item.stick,
			'X', BTWItems.diamondIngot
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.pickaxeDiamond ), new Object[] {
			"XXX", 
			" # ", 
			" # ",
			'#', Item.stick,
			'X', BTWItems.diamondIngot
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.shovelDiamond ), new Object[] {
			"X", 
			"#", 
			"#",
			'#', Item.stick,
			'X', BTWItems.diamondIngot
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.lever, 1 ), new Object[] {
			"X", 
			"#", 
			"r",
			'#', new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA),
			'X', Item.stick, 
			'r', Item.redstone 
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.stoneButton, 1 ), new Object[] {
        	"#",
        	"r",
    		'#', new ItemStack( Item.itemsList[BTWBlocks.stoneSidingAndCorner.blockID], 1, 1 ),
			'r', Item.redstone 
    	});
		
		RecipeManager.addRecipe(new ItemStack(Block.stoneButton, 1 ), new Object[] {
        	"#",
        	"r",
    		'#', new ItemStack( Item.itemsList[BTWBlocks.midStrataStoneSidingAndCorner.blockID], 1, 1 ),
			'r', Item.redstone 
    	});
		
		RecipeManager.addRecipe(new ItemStack(Block.stoneButton, 1 ), new Object[] {
        	"#",
        	"r",
    		'#', new ItemStack( Item.itemsList[BTWBlocks.deepStrataStoneSidingAndCorner.blockID], 1, 1 ),
			'r', Item.redstone 
    	});
		
		RecipeManager.addRecipe(new ItemStack(Block.woodenButton, 1 ), new Object[] {
        	"#", 
        	"r",
    		'#', new ItemStack(BTWItems.woodCornerStubID, 1, InventoryUtils.IGNORE_METADATA),
			'r', Item.redstone 
    	});
        
		RecipeManager.addRecipe(new ItemStack(Block.pressurePlateStone, 1 ), new Object[] {
        	"#", 
        	"r",
    		'#', new ItemStack( Item.itemsList[BTWBlocks.stoneSidingAndCorner.blockID], 1, 0 ),
			'r', Item.redstone 
    	});
		
		RecipeManager.addRecipe(new ItemStack(Block.pressurePlateStone, 1 ), new Object[] {
	        	"#", 
	        	"r",
	    		'#', new ItemStack( Item.itemsList[BTWBlocks.midStrataStoneSidingAndCorner.blockID], 1, 0 ),
				'r', Item.redstone 
	    	});
		
		RecipeManager.addRecipe(new ItemStack(Block.pressurePlateStone, 1 ), new Object[] {
	        	"#", 
	        	"r",
	    		'#', new ItemStack( Item.itemsList[BTWBlocks.deepStrataStoneSidingAndCorner.blockID], 1, 0 ),
				'r', Item.redstone 
	    	});
	        
		RecipeManager.addRecipe(new ItemStack(Block.pressurePlatePlanks, 1 ), new Object[] {
        	"#", 
        	"r",
    		'#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA),
			'r', Item.redstone 
    	});
		
		RecipeManager.addRecipe(new ItemStack(Item.doorIron, 1 ), new Object[] {
			"##r", 
			"## ", 
			"##r", 
			'#', Item.ingotIron,
			'r', BTWItems.redstoneLatch
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.dispenser, 1 ), new Object[] {
			"###", 
			"#X#", 
			"#R#", 
			'#', new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA),
			'X', Item.bow, 
			'R', BTWItems.redstoneLatch
		});
		
		RecipeManager.addRecipe(new ItemStack(Block.music, 1 ), new Object[] {
			"###", 
			"#X#", 
			"###", 
			'#', Block.planks, 
			'X', BTWItems.redstoneLatch
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Block.pumpkinLantern, 1 ), new Object[] {
			new ItemStack( Block.pumpkin ), 
			new ItemStack(BTWItems.candle, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWBlocks.unfiredClay, 1 ), new Object[] {
			"###", 
			"###", 
			"###", 
			'#', Item.clay
		});
        
		// axe recipe rebalance
		
        RecipeManager.addRecipe(new ItemStack(Item.axeIron ), new Object[] {
        	"X ", 
        	"X#", 
        	" #",
        	'#', Item.stick, 
        	'X', Item.ingotIron
    	});
        
        RecipeManager.addRecipe(new ItemStack(Item.axeGold ), new Object[] {
        	"X ", 
        	"X#", 
        	" #",
        	'#', Item.stick, 
        	'X', Item.ingotGold
    	});
        
		RecipeManager.addRecipe(new ItemStack(Item.axeDiamond ), new Object[] {
			"X ", 
			"X#", 
			" #", 
			'#', Item.stick,
			'X', BTWItems.diamondIngot
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.stick, 2 ), new Object[] {
            new ItemStack(Block.planks, 1, InventoryUtils.IGNORE_METADATA)
        });
		
		RecipeManager.addRecipe(new ItemStack(Item.bed), new Object[] {
			"###",
			"XXX",
			'#', BTWItems.padding,
			'X', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.bedroll), new Object[] {
				new ItemStack(BTWItems.woolKnit, 1, InventoryUtils.IGNORE_METADATA),
				new ItemStack(BTWItems.woolKnit, 1, InventoryUtils.IGNORE_METADATA),
				new ItemStack(Item.silk)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.bedroll), new Object[] {
				new ItemStack(BTWItems.woolKnit, 1, InventoryUtils.IGNORE_METADATA),
				new ItemStack(BTWItems.woolKnit, 1, InventoryUtils.IGNORE_METADATA),
				new ItemStack(BTWItems.hempFibers)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.bedroll), new Object[] {
				new ItemStack(BTWItems.woolKnit, 1, InventoryUtils.IGNORE_METADATA),
				new ItemStack(BTWItems.woolKnit, 1, InventoryUtils.IGNORE_METADATA),
				new ItemStack(BTWItems.sinew)
		});
		
		// map creation and zooming
		
		RecipeManager.addRecipe(new ItemStack(Item.emptyMap, 1 ), new Object[] {
			"#S#", 
			"#X#", 
			"#S#", 
			'#', Item.paper, 
			'X', Item.compass,
			'S', BTWItems.soulUrn
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.emptyMap, 1, 1 ), new Object[] {
			"###", 
			"#X#", 
			"###", 
			'#', Item.paper, 
			'X', new ItemStack( Item.emptyMap, 1, 0 )
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.emptyMap, 1, 2 ), new Object[] {
			"###", 
			"#X#", 
			"###", 
			'#', Item.paper, 
			'X', new ItemStack( Item.emptyMap, 1, 1 )
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.emptyMap, 1, 3 ), new Object[] {
			"###", 
			"#X#", 
			"###", 
			'#', Item.paper, 
			'X', new ItemStack( Item.emptyMap, 1, 2 )
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.emptyMap, 1, 4 ), new Object[] {
			"###", 
			"#X#", 
			"###", 
			'#', Item.paper, 
			'X', new ItemStack( Item.emptyMap, 1, 3 )
		});
	}
	
	private static void addConversionRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.hempFibers, 9 ), new Object[] {
    		new ItemStack( BTWItems.fabric)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.hempFibers, 6 ), new Object[] {
    		new ItemStack( BTWItems.rope)
		});
		
		//Legacy support
		RecipeManager.addShapelessRecipe(new ItemStack(Block.cobblestone ), new Object[] {
    		new ItemStack( Block.stoneSingleSlab, 1, 3 ), // metadata 3 is cobble slab
    		new ItemStack( Block.stoneSingleSlab, 1, 3 )
		});

		RecipeManager.addShapelessRecipe(new ItemStack(Block.brick ), new Object[] {
    		new ItemStack( Block.stoneSingleSlab, 1, 4 ), // metadata 4 is brick slab
    		new ItemStack( Block.stoneSingleSlab, 1, 4 )
		});
		
		//Legacy support
		RecipeManager.addShapelessRecipe(new ItemStack(Block.stoneBrick ), new Object[] {
    		new ItemStack( Block.stoneSingleSlab, 1, 5 ), // metadata 5 is stone brick slab
    		new ItemStack( Block.stoneSingleSlab, 1, 5 )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Block.netherBrick ), new Object[] {
    		new ItemStack( Block.stoneSingleSlab, 1, 6 ), // metadata 6 is nether brick slab
    		new ItemStack( Block.stoneSingleSlab, 1, 6 )
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stoneSingleSlab, 6, 3), new Object[] {
				"###",
				'#', Block.cobblestone
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stoneSingleSlab, 6, 5), new Object[] {
				"###",
				'#', Block.stoneBrick
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stoneSingleSlab, 6, 0), new Object[] {
				"###",
				'#', Block.stone
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.cobblestoneWall, 6, 0), new Object[] {
				"###",
				"###",
				'#', Block.cobblestone
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.cobblestoneWall, 6, 1), new Object[] {
				"###",
				"###",
				'#', Block.cobblestoneMossy
		});
		
		for (int strata = 0; strata < 3; strata++) {
			// Cobblestone slabs
			RecipeManager.addRecipe(new ItemStack(BTWBlocks.cobblestoneSlab, 6, strata), new Object[] {
					"###",
					'#', new ItemStack(Block.cobblestone, 1, strata)
			});
			
			RecipeManager.addShapelessRecipe(new ItemStack(Block.cobblestone, 1, strata), new Object[] {
		    		new ItemStack(BTWBlocks.cobblestoneSlab, 1, strata),
		    		new ItemStack(BTWBlocks.cobblestoneSlab, 1, strata)
			});
			
			// Cobblestone Walls
			RecipeManager.addRecipe(new ItemStack(Block.cobblestoneWall, 6, strata << 2), new Object[] {
					"###",
					"###",
					'#', new ItemStack(Block.cobblestone, 1, strata)
			});
			
			RecipeManager.addRecipe(new ItemStack(Block.cobblestoneWall, 6, (strata << 2) + 1), new Object[] {
					"###",
					"###",
					'#', new ItemStack(Block.cobblestoneMossy, 1, strata)
			});
			
			// Stone Slabs
			RecipeManager.addRecipe(new ItemStack(BTWBlocks.stoneSlab, 6, strata), new Object[] {
					"###",
					'#', new ItemStack(Block.stone, 1, strata)
			});
			
			// Stone Brick Slabs
			RecipeManager.addRecipe(new ItemStack(BTWBlocks.stoneBrickSlab, 6, strata), new Object[] {
					"###",
					'#', new ItemStack(Block.stoneBrick, 1, strata << 2)
			});
			
			RecipeManager.addShapelessRecipe(new ItemStack(Block.stoneBrick, 1, strata << 2), new Object[] {
		    		new ItemStack(BTWBlocks.stoneBrickSlab, 1, strata),
		    		new ItemStack(BTWBlocks.stoneBrickSlab, 1, strata)
			});
			
			// Stone Slabs
		}
		
		for ( int iTempWoodType = 0; iTempWoodType < 5; iTempWoodType++ )
		{
			RecipeManager.addShapelessRecipe(new ItemStack(Block.planks, 1, iTempWoodType ), new Object[] {
	    		new ItemStack( Block.woodSingleSlab, 1, iTempWoodType ),
	    		new ItemStack( Block.woodSingleSlab, 1, iTempWoodType )
			});
		}
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.clay, 8 ), new Object[] {
    		new ItemStack( BTWBlocks.unfiredPottery, 1, UnfiredPotteryBlock.SUBTYPE_CRUCIBLE)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.clay, 6 ), new Object[] {
    		new ItemStack( BTWBlocks.unfiredPottery, 1, UnfiredPotteryBlock.SUBTYPE_PLANTER)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.clay, 4 ), new Object[] {
    		new ItemStack( BTWBlocks.unfiredPottery, 1, UnfiredPotteryBlock.SUBTYPE_VASE)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.clay, 2 ), new Object[] {
    		new ItemStack( BTWBlocks.unfiredPottery, 1, UnfiredPotteryBlock.SUBTYPE_URN)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.clay, 1 ), new Object[] {
    		new ItemStack( BTWBlocks.unfiredPottery, 1, UnfiredPotteryBlock.SUBTYPE_MOULD)
		});
		
		RecipeManager.addRecipe(new ItemStack(Item.ingotIron, 1 ), new Object[] {
			"###", 
			"###", 
			"###", 
			'#', new ItemStack( BTWItems.ironNugget),
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.ironNugget, 9 ), new Object[] {
			new ItemStack( Item.ingotIron ) 
		});
		
		RecipeManager.addRecipe(new ItemStack(BTWItems.soulforgedSteelIngot, 1 ), new Object[] {
			"###", 
			"###", 
			"###", 
			'#', new ItemStack( BTWItems.steelNugget),
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.steelNugget, 9 ), new Object[] {
			new ItemStack( BTWItems.soulforgedSteelIngot)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.soulSandPile, 4 ), new Object[] {
			new ItemStack( Block.slowSand, 1 )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Block.slowSand, 1 ), new Object[] {
			new ItemStack( BTWItems.soulSandPile),
			new ItemStack( BTWItems.soulSandPile),
			new ItemStack( BTWItems.soulSandPile),
			new ItemStack( BTWItems.soulSandPile)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(Item.book, 3 ), new Object[] {
			new ItemStack( Block.bookShelf )
		});
		
		RecipeManager.addShapelessRecipeWithSecondaryOutputIndicator(
				new ItemStack(BTWItems.wheatSeeds, 2),
				new ItemStack(BTWItems.straw),
				new Object[] {
					new ItemStack(BTWItems.wheat)
				}
		);
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.wheatSeeds), new Object[] {
			new ItemStack( Item.seeds )
		});
	}
	
	private static void addLogChoppingRecipes() {
		for (int i = 0; i < 4; i++) {
			RecipeManager.addLogChoppingRecipe(new ItemStack(Block.planks, 2, i), 
					new ItemStack[] {
							new ItemStack(BTWItems.bark, 1, i),
							new ItemStack(BTWItems.sawDust, 2, 0),
					}, 
					new ItemStack(Item.stick, 2), 
					new ItemStack[] {
							new ItemStack(BTWItems.bark, 1, i),
							new ItemStack(BTWItems.sawDust, 4, 0),
					}, 
					new ItemStack(Block.wood, 1, i));
		}
		
		RecipeManager.addLogChoppingRecipe(new ItemStack(Block.planks, 2, 4), 
				new ItemStack[] {
						new ItemStack(BTWItems.bark, 1, 4),
						new ItemStack(BTWItems.sawDust, 1, 0),
						new ItemStack(BTWItems.soulDust, 1, 0),
				}, 
				new ItemStack(Item.stick, 2), 
				new ItemStack[] {
						new ItemStack(BTWItems.bark, 1, 4),
						new ItemStack(BTWItems.sawDust, 3, 0),
						new ItemStack(BTWItems.soulDust, 1, 0),
				}, 
				new ItemStack(BTWBlocks.bloodWoodLog, 1, 0));
	}
	
	private static void addTuningForkRecipes()
	{
		for ( int iTempDamage = 0; iTempDamage < 24; iTempDamage++ )
		{
			RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.tuningFork, 1, iTempDamage + 1 ), new Object[] {
	    		new ItemStack( BTWItems.tuningFork, 1, iTempDamage )
			});
		}
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.tuningFork, 1, 0 ), new Object[] {
    		new ItemStack( BTWItems.tuningFork, 1, 24 )
		});
	}	
	    	
	private static void addSubBlockRecipes()
	{
		addWoodSubBlockRecipes();
		
		addSubBlockRecipesOfType(Block.stone, 0,
								 BTWBlocks.stoneSidingAndCorner,
								 BTWBlocks.stoneMouldingAndDecorative, true);
		
		addSubBlockRecipesOfType(Block.stone, 1,
								 BTWBlocks.midStrataStoneSidingAndCorner,
								 BTWBlocks.midStrataStoneMouldingAndDecorative, true);
		
		addSubBlockRecipesOfType(Block.stone, 2,
								 BTWBlocks.deepStrataStoneSidingAndCorner,
								 BTWBlocks.deepStrataStoneMouldingAndDecorative, true);
		
		addSubBlockRecipesOfType(Block.stoneBrick, 0,
								 BTWBlocks.stoneBrickSidingAndCorner,
								 BTWBlocks.stoneBrickMouldingAndDecorative, true);
		
		addSubBlockRecipesOfType(Block.stoneBrick, 4,
								 BTWBlocks.midStrataStoneBrickSidingAndCorner,
								 BTWBlocks.midStrataStoneBrickMouldingAndDecorative, true);
		
		addSubBlockRecipesOfType(Block.stoneBrick, 8,
								 BTWBlocks.deepStrataStoneBrickSidingAndCorner,
								 BTWBlocks.deepStrataStoneBrickMouldingAndDecorative, true);
		
		addSubBlockRecipesOfType(BTWBlocks.aestheticOpaque, AestheticOpaqueBlock.SUBTYPE_WHITE_STONE,
								 BTWBlocks.whiteStoneSidingAndCorner,
								 BTWBlocks.whiteStoneMouldingAndDecroative, true);
		
		addSubBlockRecipesOfType(Block.netherBrick, 0,
								 BTWBlocks.netherBrickSidingAndCorner,
								 BTWBlocks.netherBrickMouldingAndDecorative, false);
		
		// high effeciency for nether brick fences
		
		RecipeManager.addRecipe(new ItemStack(Block.netherFence, 2 ), new Object[] {
            "###", 
            Character.valueOf('#'), new ItemStack( BTWBlocks.netherBrickMouldingAndDecorative, 1, 0 )
		});
		
		addSubBlockRecipesOfType(Block.brick, 0,
								 BTWBlocks.brickSidingAndCorner, BTWBlocks.brickMouldingAndDecorative, true);
		
		addSubBlockRecipesOfType(Block.sandStone, InventoryUtils.IGNORE_METADATA, 0,
								 BTWBlocks.sandstoneSidingAndCorner, BTWBlocks.sandstoneMouldingAndDecorative, true);
		
		addSubBlockRecipesOfType(Block.blockNetherQuartz, 0,
								 BTWBlocks.blackStoneSidingAndCorner, BTWBlocks.blackStoneMouldingAndDecorative, true);
	}
	
	public static void addSubBlockRecipesOfType(Block fullBlock, int iFullBlockMetadata, Block sidingAndCornerBlock, Block mouldingBlock, boolean bIncludeFence)
	{
		addSubBlockRecipesOfType(fullBlock, iFullBlockMetadata, iFullBlockMetadata, sidingAndCornerBlock, mouldingBlock, bIncludeFence);
	}
	
	public static void addSubBlockRecipesOfType(Block fullBlock, int iFullBlockInputMetadata, int iFullBlockOutputMetadata, Block sidingAndCornerBlock, Block mouldingBlock, boolean bIncludeFence)
	{
		// sub block creation recipes
		
		RecipeManager.addSoulforgeRecipe(new ItemStack(sidingAndCornerBlock, 8, 0 ), new Object[] {
    		"####", 
    		'#', new ItemStack( fullBlock, 1, iFullBlockInputMetadata )
		});
		
		RecipeManager.addSoulforgeRecipe(new ItemStack(mouldingBlock, 8, 0 ), new Object[] {
    		"####", 
    		'#', new ItemStack( sidingAndCornerBlock, 1, 0 )
		});
		
		RecipeManager.addSoulforgeRecipe(new ItemStack(sidingAndCornerBlock, 8, 1 ), new Object[] {
    		"####", 
    		'#', new ItemStack( mouldingBlock, 1, 0 )
		});
		
		RecipeManager.addRecipe(new ItemStack(mouldingBlock, 1, MouldingAndDecorativeBlock.SUBTYPE_COLUMN), new Object[] {
    		"M", 
    		"M", 
    		"M", 
    		'M', new ItemStack( mouldingBlock, 1, 0 )
		});
		
		RecipeManager.addRecipe(new ItemStack(mouldingBlock, 6, MouldingAndDecorativeBlock.SUBTYPE_PEDESTAL_UP), new Object[] {
    		" S ", 
    		"###", 
    		"###", 
    		'#', new ItemStack( fullBlock, 1, iFullBlockInputMetadata ), 
    		'S', new ItemStack( sidingAndCornerBlock, 8, 0 )
		});
		
		RecipeManager.addRecipe(new ItemStack(mouldingBlock, 4, MouldingAndDecorativeBlock.SUBTYPE_TABLE), new Object[] {
    		"###", 
    		" X ", 
    		" X ", 
    		'#', new ItemStack( sidingAndCornerBlock, 1, 0 ), 
    		'X', new ItemStack( mouldingBlock, 1, 0 )
		});
		
		RecipeManager.addRecipe(new ItemStack(sidingAndCornerBlock, 4, SidingAndCornerAndDecorativeBlock.SUBTYPE_BENCH), new Object[] {
    		"###", 
    		" X ", 
    		'#', new ItemStack( sidingAndCornerBlock, 1, 0 ), 
    		'X', new ItemStack( mouldingBlock, 1, 0 )
		});
		
		if ( bIncludeFence )
		{
			RecipeManager.addRecipe(new ItemStack(sidingAndCornerBlock, 6, SidingAndCornerAndDecorativeBlock.SUBTYPE_FENCE), new Object[] {
	            "###", 
	            "###", 
	            Character.valueOf('#'), new ItemStack( fullBlock, 1, iFullBlockInputMetadata ) 
			});
			
			RecipeManager.addRecipe(new ItemStack(sidingAndCornerBlock, 2, SidingAndCornerAndDecorativeBlock.SUBTYPE_FENCE), new Object[] {
	            "###", 
	            Character.valueOf('#'), new ItemStack( mouldingBlock, 1, 0 ) 
			});
		}
		
		// sub block combine recipes
		
		RecipeManager.addShapelessRecipe(new ItemStack(fullBlock, 1, iFullBlockOutputMetadata ), new Object[] {
			new ItemStack( sidingAndCornerBlock, 1, 0 ),
			new ItemStack( sidingAndCornerBlock, 1, 0 ) 
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(sidingAndCornerBlock, 1, 0 ), new Object[] {
			new ItemStack( mouldingBlock, 1, 0 ),
			new ItemStack( mouldingBlock, 1, 0 ) 
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(mouldingBlock, 1, 0 ), new Object[] {
			new ItemStack( sidingAndCornerBlock, 1, 1 ),
			new ItemStack( sidingAndCornerBlock, 1, 1 ) 
		});
	}
	
	private static void addLegacyConversionRecipes()
	{
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.woodMouldingDecorativeStubID, 1,
										 (WoodMouldingDecorativeStubBlockItem.TYPE_TABLE << 2 ) ), new Object[] {
			new ItemStack( BTWBlocks.aestheticNonOpaque, 1, AestheticNonOpaqueBlock.SUBTYPE_TABLE)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.stoneMouldingAndDecorative, 1, MouldingAndDecorativeBlock.SUBTYPE_PEDESTAL_UP), new Object[] {
			new ItemStack( BTWBlocks.aestheticNonOpaque, 1, AestheticNonOpaqueBlock.SUBTYPE_PEDESTAL_UP)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.stoneMouldingAndDecorative, 1, MouldingAndDecorativeBlock.SUBTYPE_COLUMN), new Object[] {
			new ItemStack( BTWBlocks.aestheticNonOpaque, 1, AestheticNonOpaqueBlock.SUBTYPE_COLUMN)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.woodMouldingStubID, 1, 0 ), new Object[] {
			new ItemStack( BTWBlocks.oakWoodMouldingAndDecorative)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.stoneSidingAndCorner, 1, 1 ), new Object[] {
			new ItemStack( BTWBlocks.legacyStoneAndOakCorner, 1, 8 )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.woodCornerStubID, 1, 0 ), new Object[] {
			new ItemStack( BTWBlocks.legacyStoneAndOakCorner, 1, 0 )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.stoneSidingAndCorner, 1, 0 ), new Object[] {
			new ItemStack( BTWBlocks.legacyStoneAndOakSiding, 1, 0 )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.woodSidingStubID, 1, 0 ), new Object[] {
			new ItemStack( BTWBlocks.legacyStoneAndOakSiding, 1, 1 )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.aestheticEarth, 1, AestheticOpaqueEarthBlock.SUBTYPE_DUNG), new Object[] {
			new ItemStack( BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_DUNG)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.brownMushroom), new Object[] {
			new ItemStack( Block.mushroomBrown ) 
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.redMushroom), new Object[] {
			new ItemStack( Block.mushroomRed ) 
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.workbench), new Object[] {
    		new ItemStack( Block.workbench )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.chest), new Object[] {
    		new ItemStack( Block.chest )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.web), new Object[] {
    		new ItemStack( Block.web )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.ladder), new Object[] {
			new ItemStack( Block.ladder ) 
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.solidSnow), new Object[] {
    		new ItemStack( Block.blockSnow )
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.carrot), new Object[] {
    		new ItemStack(Item.carrot)
		});
		
		RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.grassSlab), new Object[] {
			new ItemStack( BTWBlocks.dirtSlab, 1, DirtSlabBlock.SUBTYPE_GRASS)
		});
		
		for (int i = 0; i < 4; i++) {
			RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.oakSapling), new Object[] {
					new ItemStack(Block.sapling, 1, i << 2)
			});
			
			RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.spruceSapling), new Object[] {
					new ItemStack(Block.sapling, 1, 1 | (i << 2))
			});
			
			RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.birchSapling), new Object[] {
					new ItemStack(Block.sapling, 1, 2 | (i << 2))
			});
			
			RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.jungleSapling), new Object[] {
					new ItemStack(Block.sapling, 1, 3 | (i << 2))
			});
		}
	}

	private static void addCustomRecipeClasses()
	{
		CraftingManager.getInstance().getRecipeList().add( new FishingRodBaitingRecipe() );
		//CraftingManager.getInstance().getRecipeList().add( new FCRecipesLogChopping() );
		CraftingManager.getInstance().getRecipeList().add( new KnittingRecipe() );
	}
	
	private static void addWoodSubBlockRecipes() {
		// wood sub-blocks
		
		for (int iWoodType = 0; iWoodType <= 4; iWoodType++) {
			RecipeManager.addRecipe(new ItemStack(BTWItems.woodMouldingDecorativeStubID, 6, WoodMouldingDecorativeStubBlockItem
							.getItemDamageForType(iWoodType, WoodMouldingDecorativeStubBlockItem.TYPE_PEDESTAL)),
					new Object[]{" S ", "###", "###", '#', new ItemStack(Block.planks, 1, iWoodType), 'S',
							new ItemStack(BTWItems.woodSidingStubID, 1, iWoodType)});
			RecipeManager.addRecipe(new ItemStack(BTWItems.woodMouldingDecorativeStubID, 1, WoodMouldingDecorativeStubBlockItem
							.getItemDamageForType(iWoodType, WoodMouldingDecorativeStubBlockItem.TYPE_COLUMN)),
					new Object[]{"M", "M", "M", 'M', new ItemStack(BTWItems.woodMouldingStubID, 1, iWoodType)});
			
			RecipeManager.addRecipe(new ItemStack(BTWItems.woodMouldingDecorativeStubID, 4, WoodMouldingDecorativeStubBlockItem
							.getItemDamageForType(iWoodType, WoodMouldingDecorativeStubBlockItem.TYPE_TABLE)),
					new Object[]{"###", " X ", " X ", '#', new ItemStack(BTWItems.woodSidingStubID, 1, iWoodType), 'X',
							new ItemStack(BTWItems.woodMouldingStubID, 1, iWoodType)});
			RecipeManager.addRecipe(new ItemStack(BTWItems.woodSidingDecorativeStubID, 4, WoodSidingDecorativeStubBlockItem
							.getItemDamageForType(iWoodType, WoodSidingDecorativeStubBlockItem.TYPE_BENCH)),
					new Object[]{"###", " X ", '#', new ItemStack(BTWItems.woodSidingStubID, 1, iWoodType), 'X',
							new ItemStack(BTWItems.woodMouldingStubID, 1, iWoodType)});
			
			if (iWoodType == 0) {
				// Recipes for regular vanilla fences
				
				RecipeManager.addRecipe(new ItemStack(Block.fence, 6), new Object[]{"###", "###", '#', new ItemStack(Block.planks, 1, iWoodType),});
				
				RecipeManager.addRecipe(new ItemStack(Block.fence, 2),
						new Object[]{"###", Character.valueOf('#'), new ItemStack(BTWItems.woodMouldingStubID, 1, iWoodType)});
			}
			else {
				RecipeManager.addRecipe(new ItemStack(BTWItems.woodSidingDecorativeStubID, 6, WoodSidingDecorativeStubBlockItem
								.getItemDamageForType(iWoodType, WoodSidingDecorativeStubBlockItem.TYPE_FENCE)),
						new Object[]{"###", "###", '#', new ItemStack(Block.planks, 1, iWoodType),});
				
				RecipeManager.addRecipe(new ItemStack(BTWItems.woodSidingDecorativeStubID, 2, WoodSidingDecorativeStubBlockItem
								.getItemDamageForType(iWoodType, WoodSidingDecorativeStubBlockItem.TYPE_FENCE)),
						new Object[]{"###", Character.valueOf('#'), new ItemStack(BTWItems.woodMouldingStubID, 1, iWoodType)});
			}
			
			// sub-block combine recipes
			
			RecipeManager.addShapelessRecipe(new ItemStack(Block.planks, 1, iWoodType), new Object[]{new ItemStack(BTWItems.woodSidingStubID, 1, iWoodType),
					new ItemStack(BTWItems.woodSidingStubID, 1, iWoodType)});
			
			RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.woodSidingStubID, 1, iWoodType),
					new Object[]{new ItemStack(BTWItems.woodMouldingStubID, 1, iWoodType),
							new ItemStack(BTWItems.woodMouldingStubID, 1, iWoodType)});
			
			RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.woodMouldingStubID, 1, iWoodType),
					new Object[]{new ItemStack(BTWItems.woodCornerStubID, 1, iWoodType),
							new ItemStack(BTWItems.woodCornerStubID, 1, iWoodType)});
		}
	}

	private static void removeVanillaRecipes()
	{
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.bread, 1 ), new Object[] {
            "###", 
            '#', Item.wheat
        });
		
		RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.dyePowder, 3, 15 ), new Object[] {
	        Item.bone
        });
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.sugar, 1 ), new Object[] {
		    "#", '#', Item.reed
		});

		RecipeManager.removeVanillaRecipe(new ItemStack(Item.cake, 1 ), new Object[] {
			"AAA", 
			"BEB", 
			"CCC", 
			'A', Item.bucketMilk, 
			'B', Item.sugar, 
			'C', Item.wheat, 
			'E', Item.egg
		});
		
		RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.dyePowder, 2, 11 ), new Object[] {
            Block.plantYellow
        });
        
		RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.dyePowder, 2, 1 ), new Object[] {
            Block.plantRed
        });
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.tnt, 1 ), new Object[] {
			"X#X", 
			"#X#", 
			"X#X", 
			'X', Item.gunpowder, 
			'#', Block.sand
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.cookie, 8 ), new Object[] {
 			"#X#", 
 			'X', new ItemStack(Item.dyePowder, 1, 3), // cocoa beans 
 			'#', Item.wheat
 		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.anvil, 1 ), new Object[] {
			"III", 
			" i ", 
			"iii", 
			'I', Block.blockIron, 
			'i', Item.ingotIron
		});
		
		RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.bowlSoup ), new Object[] {
			Block.mushroomBrown, 
			Block.mushroomRed, 
			Item.bowlEmpty
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.melon ), new Object[] {
			"MMM", 
			"MMM", 
			"MMM", 
			'M', Item.melon 
		});
		
		RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.pumpkinPie ), new Object[] {
			Block.pumpkin, 
			Item.sugar, 
			Item.egg
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.pumpkinSeeds, 4 ), new Object[] {
			"M", 
			'M', Block.pumpkin
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.compass, 1 ), new Object[] {
			" # ", 
			"#X#", 
			" # ", 
			'#', Item.ingotIron, 
			'X', Item.redstone
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.pocketSundial, 1 ), new Object[] {
			" # ", 
			"#X#", 
			" # ", 
			'#', Item.ingotGold, 
			'X', Item.redstone });
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.flintAndSteel, 1 ), new Object[] {
			"A ", 
			" B", 
			'A', Item.ingotIron, 
			'B', Item.flint
		});
		
		RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.fermentedSpiderEye ), new Object[] {
			Item.spiderEye, 
			Block.mushroomBrown, 
			Item.sugar
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.torchWood, 4 ), new Object[] {
			"X", 
			"#", 
			'X', Item.coal, 
			'#', Item.stick 
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.torchWood, 4 ), new Object[] {
			"X", 
			"#", 
			'X', new ItemStack( Item.coal, 1, 1 ), 
			'#', Item.stick 
		});
		
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.bucketEmpty, 1 ), new Object[] {
        	"# #", 
        	" # ", 
        	'#', Item.ingotIron
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.redstoneRepeater, 1 ), new Object[] {
        	"#X#", 
        	"III", 
        	'#', Block.torchRedstoneActive, 
        	'X', Item.redstone, 
        	'I', Block.stone });
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.snow, 6 ), new Object[] {
        	"###", 
        	'#', Block.blockSnow
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.dropper, 1 ), new Object[] {
        	"###", 
        	"# #", 
        	"#R#", 
        	'#', Block.cobblestone, 
        	'R', Item.redstone
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.stoneButton, 1 ), new Object[] {
        	"#", 
        	'#', 
        	Block.stone});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.woodenButton, 1 ), new Object[] {
        	"#", 
        	'#', Block.planks
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.pressurePlateStone, 1 ), new Object[] {
        	"##", 
        	'#', Block.stone 
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.pressurePlatePlanks, 1 ), new Object[] {
        	"##", 
        	'#', Block.planks
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.pressurePlateIron, 1 ), new Object[] {
    		"##", 
    		'#', Item.ingotIron
		});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.pressurePlateGold, 1 ), new Object[] {
        	"##", 
        	'#', Item.ingotGold
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.daylightSensor ), new Object[] {
        	"GGG", 
        	"QQQ", 
        	"WWW", 
        	'G', Block.glass, 
        	'Q', Item.netherQuartz, 
        	'W', Block.woodSingleSlab
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.hopperBlock ), new Object[] {
        	"I I", 
        	"ICI", 
        	" I ", 
        	'I', Item.ingotIron, 
        	'C', Block.chest
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.rail, 16 ), new Object[] {
        	"X X", 
        	"XSX", 
        	"X X", 
        	'X', Item.ingotIron, 
        	'S', Item.stick });
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.railPowered, 6 ), new Object[] {
        	"X X", 
        	"XSX", 
        	"XRX", 
        	'X', Item.ingotGold, 
        	'R', Item.redstone, 
        	'S', Item.stick });
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.railDetector, 6 ), new Object[] {
        	"X X", 
        	"XSX", 
        	"XRX", 
        	'X', Item.ingotIron, 
        	'R', Item.redstone, 
        	'S', Block.pressurePlateStone });
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.railActivator, 6 ), new Object[] {
        	"XSX", 
        	"X#X", 
        	"XSX", 
        	'X', Item.ingotIron, 
        	'#', Block.torchRedstoneActive, 
        	'S', Item.stick });
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.comparator, 1 ), new Object[] {
        	" # ", 
        	"#X#", 
        	"III", 
        	'#', Block.torchRedstoneActive, 
        	'X', Item.netherQuartz, 
        	'I', Block.stone
    	});

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.minecartTnt, 1 ), new Object[] {
        	"A", 
        	"B", 
        	'A', Block.tnt, 
        	'B', Item.minecartEmpty
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.minecartHopper, 1 ), new Object[] {
        	"A", 
        	"B", 
        	'A', Block.hopperBlock, 
        	'B', Item.minecartEmpty
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.chestTrapped ), new Object[] {
        	"#-", 
        	'#', Block.chest, 
        	'-', Block.tripWireSource
    	});

        // remove diamond tool & armor recipes
        
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.helmetDiamond ), new Object[] {
			"XXX", 
			"X X", 
			'X', Item.diamond
		});
	   
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.plateDiamond ), new Object[] {
			"X X", 
			"XXX", 
			"XXX", 
			'X', Item.diamond
		});
	   
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.legsDiamond ), new Object[] {
			"XXX", 
			"X X", 
			"X X", 
			'X', Item.diamond
		});
	   
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.bootsDiamond ), new Object[] {
			"X X", 
			"X X", 
			'X', Item.diamond
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.swordDiamond ), new Object[] {
			"X", 
			"X", 
			"#",
			'#', Item.stick,
			'X', Item.diamond
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.pickaxeDiamond ), new Object[] {
			"XXX", 
			" # ", 
			" # ",
			'#', Item.stick,
			'X', Item.diamond			
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.shovelDiamond ), new Object[] {
			"X", 
			"#", 
			"#",
			'#', Item.stick,
			'X', Item.diamond			
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.hoeDiamond ), new Object[] {
			"XX", 
			" #", 
			" #",
			'#', Item.stick,
			'X', Item.diamond			
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.fishingRod, 1 ), new Object[] {
			"  #", 
			" #X", 
			"# X", 
			'#', Item.stick, 
			'X', Item.silk
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.cloth, 1 ), new Object[] {
			"##", 
			"##", 
			'#', Item.silk 
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.planks, 4, 0 ), new Object[] {
			"#", 
			'#', new ItemStack( Block.wood, 1, 0 )
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.planks, 4, 1 ), new Object[] {
			"#", 
			'#', new ItemStack( Block.wood, 1, 1 )
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.planks, 4, 2 ), new Object[] {
			"#", 
			'#', new ItemStack( Block.wood, 1, 2 ) 
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.planks, 4, 3 ), new Object[] {
			"#", 
			'#', new ItemStack( Block.wood, 1, 3 )
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.lever, 1 ), new Object[] {
			"X", 
			"#", 
			'#', Block.cobblestone, 
			'X', Item.stick });
        
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.doorIron, 1 ), new Object[] {
			"##", 
			"##", 
			"##", 
			'#', Item.ingotIron
		});
        
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.tripWireSource, 2 ), new Object[] {
			"I", 
			"S", 
			"#", 
			'#', Block.planks, 
			'S', Item.stick, 
			'I', Item.ingotIron 
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.dispenser, 1 ), new Object[] {
			"###", 
			"#X#", 
			"#R#", 
			'#', Block.cobblestone, 
			'X', Item.bow, 
			'R', Item.redstone
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.music, 1 ), new Object[] {
			"###", 
			"#X#", 
			"###", 
			'#', Block.planks, 
			'X', Item.redstone
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.enchantmentTable, 1 ), new Object[] {
			" B ", 
			"D#D", 
			"###", 
			'#', Block.obsidian, 
			'B', Item.book, 
			'D', Item.diamond 
		});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.swordWood ), new Object[] {
        	"X", 
        	"X", 
        	"#", 
        	'#', Item.stick, 
        	'X', Block.planks
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.axeWood ), new Object[] {
        	"XX", 
        	"X#", 
        	" #",
        	'#', Item.stick, 
        	'X', Block.planks
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.pickaxeWood ), new Object[] {
        	"XXX", 
        	" # ", 
        	" # ",         	
        	'#', Item.stick, 
        	'X', Block.planks
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.shovelWood ), new Object[] {
            "X", 
            "#", 
            "#",
        	'#', Item.stick, 
        	'X', Block.planks
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.hoeWood ), new Object[] {
            "XX", 
            " #", 
            " #",
        	'#', Item.stick, 
        	'X', Block.planks
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.swordStone ), new Object[] {
        	"X", 
        	"X", 
        	"#", 
        	'#', Item.stick, 
        	'X', Block.cobblestone
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.axeStone ), new Object[] {
        	"XX", 
        	"X#", 
        	" #",
        	'#', Item.stick, 
        	'X', Block.cobblestone
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.pickaxeStone ), new Object[] {
        	"XXX", 
        	" # ", 
        	" # ",         	
        	'#', Item.stick, 
        	'X', Block.cobblestone 
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.shovelStone ), new Object[] {
            "X", 
            "#", 
            "#",
        	'#', Item.stick, 
        	'X', Block.cobblestone 
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.hoeStone ), new Object[] {
            "XX", 
            " #", 
            " #",
        	'#', Item.stick, 
        	'X', Block.cobblestone
    	});
        
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.axeIron ), new Object[] {
        	"XX", 
        	"X#", 
        	" #",
        	'#', Item.stick, 
        	'X', Item.ingotIron
    	});
        
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.hoeIron ), new Object[] {
			"XX", 
			" #", 
			" #",
			'#', Item.stick,
			'X', Item.ingotIron			
		});
		
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.axeGold ), new Object[] {
        	"XX", 
        	"X#", 
        	" #",
        	'#', Item.stick, 
        	'X', Item.ingotGold
    	});
        
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.hoeGold ), new Object[] {
			"XX", 
			" #", 
			" #",
			'#', Item.stick,
			'X', Item.ingotGold			
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.axeDiamond ), new Object[] {
			"XX", 
			"X#", 
			" #", 
			'#', Item.stick,
			'X', Item.diamond			
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.arrow, 4 ), new Object[] {
			"X", 
			"#", 
			"Y", 
			'Y', Item.feather, 
			'X', Item.flint, 
			'#', Item.stick
		});
        
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.pistonBase, 1 ), new Object[] {
			"TTT", 
			"#X#", 
			"#R#", 
			'#', Block.cobblestone, 
			'X', Item.ingotIron, 
			'R', Item.redstone, 
			'T', Block.planks
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.brewingStand, 1 ), new Object[] {
			" B ", 
			"###", 
			'#', Block.cobblestone, 
			'B', Item.blazeRod
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.emptyMap, 1 ), new Object[] {
			"###", 
			"#X#", 
			"###", 
			'#', Item.paper, 
			'X', Item.compass
		});
        
		RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.eyeOfEnder, 1 ), new Object[] {
			Item.enderPearl, 
			Item.blazePowder
		});
        
		RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.blazePowder, 2), new Object[] {
			Item.blazeRod
		});
        
		RecipeManager.removeVanillaRecipe(new ItemStack(Item.bed, 1 ), new Object[] {
			"###", 
			"XXX", 
			'#', Block.cloth, 
			'X', Block.planks
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.fence, 2 ), new Object[] {
			"###", 
			"###", 
			'#', Item.stick
		});

		RecipeManager.removeVanillaRecipe(new ItemStack(Block.trapdoor, 2 ), new Object[] {
			"###", 
			"###", 
			'#', Block.planks
		});

		RecipeManager.removeVanillaRecipe(new ItemStack(Block.pumpkinLantern, 1 ), new Object[] {
			"A", 
			"B", 
			'A', Block.pumpkin, 'B', Block.torchWood 
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.blockClay, 1 ), new Object[] {
			"##", 
			"##", 
			'#', Item.clay
		});

		RecipeManager.removeVanillaRecipe(new ItemStack(Block.brick, 1 ), new Object[] {
			"##", 
			"##", 
			'#', Item.brick
		});
        
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.ladder, 3 ), new Object[] {
			"# #", 
			"###", 
			"# #", 
			'#', Item.stick
		});

		RecipeManager.removeVanillaRecipe(new ItemStack(Block.furnaceIdle ), new Object[] {
			"###", 
			"# #", 
			"###", 
			'#', Block.cobblestone
		});

		RecipeManager.removeVanillaRecipe(new ItemStack(Block.sandStone ), new Object[] {
			"##", 
			"##", 
			'#', Block.sand
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.blockSnow, 1 ), new Object[] {
			"##", 
			"##", 
			'#', Item.snowball
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stairsWoodOak, 4 ), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.planks, 1, 0)
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stairsWoodBirch, 4 ), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.planks, 1, 2)
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stairsWoodSpruce, 4 ), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.planks, 1, 1)
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stairsWoodJungle, 4 ), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.planks, 1, 3)
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stairsBrick, 4), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.brick)
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stairsCobblestone, 4), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.cobblestone)
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stairsNetherBrick, 4), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.netherBrick)
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stairsNetherQuartz, 4), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.blockNetherQuartz)
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stairsSandStone, 4), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.sandStone)
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stairsStoneBrick, 4), new Object[] {
			"#  ", 
			"## ", 
			"###", 
			'#', new ItemStack(Block.stoneBrick)
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.workbench ), new Object[] {
			"##", 
			"##", 
			'#', Block.planks 
		});

		RecipeManager.removeVanillaRecipe(new ItemStack(Block.chest ), new Object[] {
			"###", 
			"# #", 
			"###", 
			'#', Block.planks
		});
		
		RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.book, 1 ), new Object[] {
			Item.paper, 
			Item.paper, 
			Item.paper, 
			Item.leather
		});
		
		RecipeManager.removeVanillaRecipe(new ItemStack(Block.stoneBrick, 4 ), new Object[] {
			"##", 
			"##", 
			'#', Block.stone 
		});

		RecipeManager.removeVanillaRecipe(new ItemStack(Item.carrotOnAStick, 1), new Object[] {
				"# ",
				" X",
				'#', Item.fishingRod,
				'X', Item.carrot
		});
		
		
	}
	
	private static void addDebugRecipes()
    {
		// Debug Recipes (disable for release)

		/*
		AddVanillaRecipe( new ItemStack( Block.glass, 60, 0 ), new Object[] {
			"#",
			'#', Block.dirt 
		} );
		*/
    }	
}