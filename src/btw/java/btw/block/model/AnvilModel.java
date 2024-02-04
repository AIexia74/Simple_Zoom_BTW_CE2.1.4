// FCMOD

package btw.block.model;

import net.minecraft.src.AxisAlignedBB;

public class AnvilModel extends BlockModel
{
	private static final double BASE_WIDTH = 0.75D;
	private static final double BASE_HALF_WIDTH = (BASE_WIDTH / 2D );
	
	private static final double BASE_HEIGHT = 0.25D;
	
	private static final double BASE_TOP_WIDTH = 0.5D;
	private static final double BASE_TOP_HALF_WIDTH = (BASE_TOP_WIDTH / 2D );
	
	private static final double BASE_TOP_DEPTH = (10D / 16D );
	private static final double BASE_TOP_HALF_DEPTH = (BASE_TOP_DEPTH / 2D );
	
	private static final double BASE_TOP_HEIGHT = (1D / 16D );
	
	private static final double SHAFT_WIDTH = 0.25D;
	private static final double SHAFT_HALF_WIDTH = (SHAFT_WIDTH / 2D );
	
	private static final double SHAFT_DEPTH = 0.5D;
	private static final double SHAFT_HALF_DEPTH = (SHAFT_DEPTH / 2D );
	
	private static final double SHAFT_HEIGHT = (5D / 16D );
	
	private static final double TOP_WIDTH = (10D / 16D );
	private static final double TOP_HALF_WIDTH = (TOP_WIDTH / 2D );
	
	private static final double TOP_DEPTH = 1D;
	private static final double TOP_HALF_DEPTH = (TOP_DEPTH / 2D );
	
	private static final double TOP_HEIGHT = (6D / 16D );

	public AxisAlignedBB boxSelection;
	
	@Override
    protected void initModel()
    {
		addBox(0.5D - BASE_HALF_WIDTH, 0D, 0.5D - BASE_HALF_WIDTH,
			   0.5D + BASE_HALF_WIDTH, BASE_HEIGHT, 0.5D + BASE_HALF_WIDTH);
		
		double dRunningHeight = BASE_HEIGHT;
		
		addBox(0.5D - BASE_TOP_HALF_WIDTH, dRunningHeight, 0.5D - BASE_TOP_HALF_DEPTH,
			   0.5D + BASE_TOP_HALF_WIDTH, dRunningHeight + BASE_TOP_HEIGHT, 0.5D + BASE_TOP_HALF_DEPTH);
		
		dRunningHeight += BASE_TOP_HEIGHT;
		
		addBox(0.5D - SHAFT_HALF_WIDTH, dRunningHeight, 0.5D - SHAFT_HALF_DEPTH,
			   0.5D + SHAFT_HALF_WIDTH, dRunningHeight + SHAFT_HEIGHT, 0.5D + SHAFT_HALF_DEPTH);
		
		dRunningHeight += SHAFT_HEIGHT;
		
		addBox(0.5D - TOP_HALF_WIDTH, dRunningHeight, 0.5D - TOP_HALF_DEPTH,
			   0.5D + TOP_HALF_WIDTH, dRunningHeight + TOP_HEIGHT, 0.5D + TOP_HALF_DEPTH);

		boxSelection = new AxisAlignedBB(0.5D - TOP_HALF_WIDTH, 1D - TOP_HEIGHT, 0.5D - TOP_HALF_DEPTH,
										   0.5D + TOP_HALF_WIDTH, 1D, 0.5D + TOP_HALF_DEPTH); // same as vanilla
    }
}
