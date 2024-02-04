// FCMOD

package btw.block.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.RenderBlocks;

public class FenceModel extends BlockModel
{
    protected static final double POST_WIDTH = (4D / 16D );
    protected static final double POST_HALF_WIDTH = (POST_WIDTH / 2D );
    
    protected static final double ITEM_POSTS_WIDTH = (4D / 16D );
    protected static final double ITEM_POSTS_HALF_WIDTH = (ITEM_POSTS_WIDTH / 2D );
    protected static final double ITEM_POSTS_BORDER_GAP = (2D / 16D );
    
    protected static final double STRUT_WIDTH = (2D / 16D );
    protected static final double STRUT_HALF_WIDTH = (STRUT_WIDTH / 2D );
    protected static final double STRUT_HEIGHT = (3D / 16D );
    protected static final double STRUT_BOTTOM_VERTICAL_OFFSET = (6D / 16D );
    protected static final double STRUT_TOP_VERTICAL_OFFSET = (12D / 16D );
    
	public BlockModel modelStruts;
	public BlockModel modelItem;
    
	public AxisAlignedBB boxCollisionCenter;
	public AxisAlignedBB boxCollisionStruts;
	
	public AxisAlignedBB boxBoundsCenter;
	
	@Override
    protected void initModel()
    {
		// center
		
		addBox(0.5D - POST_HALF_WIDTH, 0D, 0.5D - POST_HALF_WIDTH,
			   0.5D + POST_HALF_WIDTH, 1D, 0.5D + POST_HALF_WIDTH);
		
		// support (-k aligned)

		modelStruts = new BlockModel();
		
		modelStruts.addBox(0.5D - STRUT_HALF_WIDTH, STRUT_BOTTOM_VERTICAL_OFFSET, 0D,
							 0.5D + STRUT_HALF_WIDTH, STRUT_BOTTOM_VERTICAL_OFFSET + STRUT_HEIGHT, 0.5D);
		
		modelStruts.addBox(0.5D - STRUT_HALF_WIDTH, STRUT_TOP_VERTICAL_OFFSET, 0D,
							 0.5D + STRUT_HALF_WIDTH, STRUT_TOP_VERTICAL_OFFSET + STRUT_HEIGHT, 0.5D);
		
		// item posts

		modelItem = new BlockModel();
		
		modelItem.addBox(0.5D - ITEM_POSTS_HALF_WIDTH, 0D, 0D,
						   0.5D + ITEM_POSTS_HALF_WIDTH, 1D, ITEM_POSTS_WIDTH);
		
		modelItem.addBox(0.5D - ITEM_POSTS_HALF_WIDTH, 0D, 1D - ITEM_POSTS_WIDTH,
						   0.5D + ITEM_POSTS_HALF_WIDTH, 1D, 1D);
		
		// item struts
		// these are a bit weird in that they extend beyond the block boundaries, 
		// which is copying how the vanilla renderer does it
		
		modelItem.addBox(0.5D - STRUT_HALF_WIDTH, STRUT_BOTTOM_VERTICAL_OFFSET, -ITEM_POSTS_BORDER_GAP,
						   0.5D + STRUT_HALF_WIDTH, STRUT_BOTTOM_VERTICAL_OFFSET + STRUT_HEIGHT, 1D + ITEM_POSTS_BORDER_GAP);
		
		modelItem.addBox(0.5D - STRUT_HALF_WIDTH, STRUT_TOP_VERTICAL_OFFSET, -ITEM_POSTS_BORDER_GAP,
						   0.5D + STRUT_HALF_WIDTH, STRUT_TOP_VERTICAL_OFFSET + STRUT_HEIGHT, 1D + ITEM_POSTS_BORDER_GAP);
		
		// collision volumes 

		boxCollisionCenter = new AxisAlignedBB(
				0.5D - POST_HALF_WIDTH, 0D, 0.5D - POST_HALF_WIDTH,
				0.5D + POST_HALF_WIDTH, 1.5D, 0.5D + POST_HALF_WIDTH);

		boxCollisionStruts = new AxisAlignedBB(
				0.5D - POST_HALF_WIDTH, 0D, 0D,
				0.5D + POST_HALF_WIDTH, 1.5D, 0.5D );
		
		// selection volume

		boxBoundsCenter = new AxisAlignedBB(
				0.5D - POST_HALF_WIDTH, 0D, 0.5D - POST_HALF_WIDTH,
				0.5D + POST_HALF_WIDTH, 1D, 0.5D + POST_HALF_WIDTH);
    }
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void renderAsItemBlock(RenderBlocks renderBlocks, Block block, int iItemDamage)
    {
    	modelItem.renderAsItemBlock(renderBlocks, block, iItemDamage);
    }
}
