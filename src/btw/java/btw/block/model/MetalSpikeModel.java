// FCMOD

package btw.block.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.RenderBlocks;

public class MetalSpikeModel extends BlockModel
{
    protected static final double SHAFT_WIDTH = (1D / 16D );
    protected static final double SHAFT_HALF_WIDTH = (SHAFT_WIDTH / 2D );
    
    protected static final double BASE_WIDTH = (4D / 16D );
    protected static final double BASE_HALF_WIDTH = (BASE_WIDTH / 2D );
    protected static final double BASE_HEIGHT = (2D / 16D );
    protected static final double BASE_HALF_HEIGHT = (BASE_HEIGHT / 2D );
    
	protected static final double BALL_WIDTH = (3D / 16D );
    protected static final double BALL_HALF_WIDTH = (BALL_WIDTH / 2D );
    protected static final double BALL_VERTICAL_OFFSET = (10D / 16D );
    
    protected static final double CANDLE_HOLDER_WIDTH = (4D / 16D );
    protected static final double CANDLE_HOLDER_HALF_WIDTH = (CANDLE_HOLDER_WIDTH / 2D );
    protected static final double CANDLE_HOLDER_HEIGHT = (1D / 16D );
    protected static final double CANDLE_HOLDER_HALF_HEIGHT = (CANDLE_HOLDER_HEIGHT / 2D );
    
    protected static final double CANDLE_HOLDER_VERTICAL_OFFSET = (1D - CANDLE_HOLDER_HEIGHT);
    
    protected static final double CENTER_BRACE_WIDTH = (3D / 16D );
    protected static final double CENTER_BRACE_HALF_WIDTH = (CENTER_BRACE_WIDTH / 2D );
    protected static final double CENTER_BRACE_HEIGHT = (4D / 16D );
    protected static final double CENTER_BRACE_HALF_HEIGHT = (CENTER_BRACE_HEIGHT / 2D );
    protected static final double CENTER_BRACE_VERTICAL_OFFSET = (0.5D - CENTER_BRACE_HALF_HEIGHT);
    
    protected static final double SIDE_SUPPORT_LENGTH = (8D / 16D );
    protected static final double SIDE_SUPPORT_WIDTH = (1D / 16D );
    protected static final double SIDE_SUPPORT_HALF_WIDTH = (SIDE_SUPPORT_WIDTH / 2D );
    protected static final double SIDE_SUPPORT_HEIGHT = (2D / 16D );
    protected static final double SIDE_SUPPORT_HALF_HEIGHT = (SIDE_SUPPORT_HEIGHT / 2D );
    protected static final double SIDE_SUPPORT_VERTICAL_OFFSET = (0.5D - SIDE_SUPPORT_HALF_HEIGHT);
    
    protected static final double BOUNDING_BOX_WIDTH = (4D / 16D );
    protected static final double BOUNDING_BOX_HALF_WIDTH = (BOUNDING_BOX_WIDTH / 2D );
    
	public BlockModel modelBase;
	public BlockModel modelHolder;
	public BlockModel modelBall;
	public BlockModel modelCenterBrace;
	public BlockModel modelSideSupport;
 
	public BlockModel holderModelLarge;
	
	public AxisAlignedBB boxCollisionCenter;
	public AxisAlignedBB boxCollisionStrut;
	
	@Override
    protected void initModel()
    {
		// shaft
		
		addBox(0.5D - SHAFT_HALF_WIDTH, 0D, 0.5D - SHAFT_HALF_WIDTH,
			   0.5D + SHAFT_HALF_WIDTH, 1D, 0.5D + SHAFT_HALF_WIDTH);

		// base

		modelBase = new BlockModel();
		
		modelBase.addBox(0.5D - BASE_HALF_WIDTH, 0D, 0.5D - BASE_HALF_WIDTH,
						 0.5D + BASE_HALF_WIDTH, BASE_HEIGHT, 0.5D + BASE_HALF_WIDTH);
		
		// holder
    
		modelHolder = new BlockModel();
		
		modelHolder.addBox(0.5D - CANDLE_HOLDER_HALF_WIDTH, CANDLE_HOLDER_VERTICAL_OFFSET, 0.5D - CANDLE_HOLDER_HALF_WIDTH,
						   0.5D + CANDLE_HOLDER_HALF_WIDTH,
						   CANDLE_HOLDER_VERTICAL_OFFSET + CANDLE_HOLDER_HEIGHT,
						   0.5D + CANDLE_HOLDER_HALF_WIDTH);

		holderModelLarge = new BlockModel();

		holderModelLarge.addBox(
				0.5D - CANDLE_HOLDER_HALF_WIDTH,
				CANDLE_HOLDER_VERTICAL_OFFSET - 1/16D,
				0.5D - CANDLE_HOLDER_HALF_WIDTH,
				0.5D + CANDLE_HOLDER_HALF_WIDTH,
				CANDLE_HOLDER_VERTICAL_OFFSET + CANDLE_HOLDER_HEIGHT - 1/16D,
				0.5D + CANDLE_HOLDER_HALF_WIDTH);
		holderModelLarge.addBox(
				0.5D - CANDLE_HOLDER_HALF_WIDTH - 1/16D,
				CANDLE_HOLDER_VERTICAL_OFFSET,
				0.5D - CANDLE_HOLDER_HALF_WIDTH - 1/16D,
				0.5D + CANDLE_HOLDER_HALF_WIDTH + 1/16D,
				CANDLE_HOLDER_VERTICAL_OFFSET + CANDLE_HOLDER_HEIGHT,
				0.5D + CANDLE_HOLDER_HALF_WIDTH + 1/16D);
		
		// ball

		modelBall = new BlockModel();
		
		modelBall.addBox(0.5D - BALL_HALF_WIDTH, BALL_VERTICAL_OFFSET, 0.5D - BALL_HALF_WIDTH,
						 0.5D + BALL_HALF_WIDTH, BALL_VERTICAL_OFFSET + BALL_WIDTH,
						 0.5D + BALL_HALF_WIDTH);
		
		// center brace

		modelCenterBrace = new BlockModel();
		
		modelCenterBrace.addBox(0.5D - CENTER_BRACE_HALF_WIDTH, CENTER_BRACE_VERTICAL_OFFSET, 0.5D - CENTER_BRACE_HALF_WIDTH,
								0.5D + CENTER_BRACE_HALF_WIDTH,
								CENTER_BRACE_VERTICAL_OFFSET + CENTER_BRACE_HEIGHT,
								0.5D + CENTER_BRACE_HALF_WIDTH);
		
		// side support

		modelSideSupport = new BlockModel();
		
		modelSideSupport.addBox(0.5D - SIDE_SUPPORT_HALF_WIDTH, SIDE_SUPPORT_VERTICAL_OFFSET, 0D,
								0.5D + SIDE_SUPPORT_HALF_WIDTH,
								SIDE_SUPPORT_VERTICAL_OFFSET + SIDE_SUPPORT_HEIGHT, SIDE_SUPPORT_LENGTH);
		
		// collision volumes 

		boxCollisionCenter = new AxisAlignedBB(
				0.5D - BOUNDING_BOX_HALF_WIDTH, 0D, 0.5D - BOUNDING_BOX_HALF_WIDTH,
				0.5D + BOUNDING_BOX_HALF_WIDTH, 1D, 0.5D + BOUNDING_BOX_HALF_WIDTH);

		boxCollisionStrut = new AxisAlignedBB(
				0.5D - BOUNDING_BOX_HALF_WIDTH, 0D, 0D,
				0.5D + BOUNDING_BOX_HALF_WIDTH, 1D, 0.5D + BOUNDING_BOX_HALF_WIDTH);
    }
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void renderAsItemBlock(RenderBlocks renderBlocks, Block block, int iItemDamage)
    {
    	super.renderAsItemBlock(renderBlocks, block, iItemDamage);
    	
    	modelBase.renderAsItemBlock(renderBlocks, block, iItemDamage);
    	modelBall.renderAsItemBlock(renderBlocks, block, iItemDamage);
    }
}
