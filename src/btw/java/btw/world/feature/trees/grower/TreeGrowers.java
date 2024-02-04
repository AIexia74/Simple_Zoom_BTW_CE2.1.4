package btw.world.feature.trees.grower;

import net.minecraft.src.Block;

public class TreeGrowers {
	
	//------ Wood Types ------//
	
	public static final TreeWoodType OAK_WOOD_TYPE = new TreeWoodType(
			Block.wood.blockID, 0,
			Block.wood.blockID, 12,
			Block.leaves.blockID, 0);
	
	public static final TreeWoodType SPRUCE_WOOD_TYPE = new TreeWoodType(
			Block.wood.blockID, 1,
			Block.wood.blockID, 13,
			Block.leaves.blockID, 1);
	
	public static final TreeWoodType BIRCH_WOOD_TYPE = new TreeWoodType(
			Block.wood.blockID, 2,
			Block.wood.blockID, 14,
			Block.leaves.blockID, 2);
	
	public static final TreeWoodType JUNGLE_WOOD_TYPE = new TreeWoodType(
			Block.wood.blockID, 3,
			Block.wood.blockID, 15,
			Block.leaves.blockID, 3);
	
	public static final TreeWoodType JUNGLE_BUSH_WOOD_TYPE = new TreeWoodType(
			Block.wood.blockID, 3,
			Block.wood.blockID, 15,
			Block.leaves.blockID, 0);
	
	//------ Tree Growers ------//
	
	public static final AbstractTreeGrower OAK_TREE = new StandardTreeGrower("btw:oak", 4, 6, OAK_WOOD_TYPE);
	
	public static final AbstractTreeGrower BIG_OAK_TREE = new BigTreeGrower("btw:big_oak", 5, 16, OAK_WOOD_TYPE);
	
	public static final AbstractTreeGrower SWAMP_OAK_TREE = new SwampTreeGrower("btw:swamp_oak", 5, 8, OAK_WOOD_TYPE);
	
	public static final AbstractTreeGrower SPRUCE_TREE = new TaigaTreeGrower("btw:taiga", 6, 9, SPRUCE_WOOD_TYPE);
	
	public static final AbstractTreeGrower PINE_TREE = new TaigaPineTreeGrower("btw:taiga_pine", 7, 11, SPRUCE_WOOD_TYPE);
	
	public static final AbstractTreeGrower BIRCH_TREE = new StandardTreeGrower("btw:birch", 5, 7, BIRCH_WOOD_TYPE);
	
	public static final AbstractTreeGrower JUNGLE_TREE = new StandardTreeGrower("btw:jungle", 4, 10, JUNGLE_WOOD_TYPE, true, true);
	
	public static final AbstractTreeGrower BIG_JUNGLE_TREE = new JungleTreeGrower("btw:big_jungle", 10, 30, JUNGLE_WOOD_TYPE);
	
	public static final AbstractTreeGrower JUNGLE_BUSH = new BushGrower("btw:jungle_bush", JUNGLE_BUSH_WOOD_TYPE);
	
	public static class TreeWoodType {
		public final int woodBlockID;
		public final int woodMetadata;
		
		public final int stumpBlockID;
		public final int stumpMetadata;
		
		public final int leavesBlockID;
		public final int leavesMetadata;
		
		public TreeWoodType(int woodBlockID, int woodMetadata, int stumpBlockID, int stumpMetadata, int leavesBlockID, int leavesMetadata) {
			this.woodBlockID = woodBlockID;
			this.woodMetadata = woodMetadata;
			this.stumpBlockID = stumpBlockID;
			this.stumpMetadata = stumpMetadata;
			this.leavesBlockID = leavesBlockID;
			this.leavesMetadata = leavesMetadata;
		}
	}
}
