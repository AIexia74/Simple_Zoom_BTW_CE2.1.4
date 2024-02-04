// FCMOD

package btw.block.model;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Vec3;

public class HamperModel extends BlockModel
{
	public static final double BASE_HEIGHT = (13D / 16D );
	public static final double BASE_WIDTH = (14D / 16D );
	public static final double BASE_DEPTH = (12D / 16D );

	public static final double BASE_BOTTOM_HEIGHT = (2D / 16D );
	public static final double BASE_BOTTOM_WIDTH_GAP = (1D / 16D );
	
	public static final double BASE_HALF_WIDTH = (BASE_WIDTH / 2D );
	public static final double BASE_HALF_DEPTH = (BASE_DEPTH / 2D );
	
	public static final double LID_HEIGHT = (3D / 16D );
	public static final double LID_WIDTH = (BASE_WIDTH + (2D / 16D ) );
	public static final double LID_DEPTH = (BASE_DEPTH + (2D / 16D ) );

	public static final double LID_HALF_WIDTH = (LID_WIDTH / 2D );
	public static final double LID_HALF_DEPTH = (LID_DEPTH / 2D );
	
	private static final float LID_LAYER_HEIGHT = (1F / 16F );
	private static final float LID_LAYER_WIDTH_GAP = (1F / 16F );
	
	private static final double LID_MIN_X = 0.5D - LID_HALF_WIDTH;
	private static final double LID_MAX_X = 0.5D + LID_HALF_WIDTH;
	
	private static final double LID_MIN_Z = 0.5D - LID_HALF_DEPTH;
	private static final double LID_MAX_Z = 0.5D + LID_HALF_DEPTH;
	
	private static final int NUM_LID_LAYERS = 3;
	
    private static final Vec3 lidRotationPoint = Vec3.createVectorHelper(8F / 16F, 13F / 16F, 14F / 16F);
    
	public BlockModel lid;
	
	public AxisAlignedBB selectionBox;
	public AxisAlignedBB selectionBoxOpen;
	
	@Override
    protected void initModel()
    {
		// base
		
		addBox(0.5D - BASE_HALF_WIDTH, BASE_BOTTOM_HEIGHT, 0.5D - BASE_HALF_DEPTH,
			   0.5D + BASE_HALF_WIDTH, BASE_HEIGHT, 0.5D + BASE_HALF_DEPTH);
		
		addBox(0.5D - BASE_HALF_WIDTH + BASE_BOTTOM_WIDTH_GAP, 0D, 0.5D - BASE_HALF_DEPTH + BASE_BOTTOM_WIDTH_GAP,
			   0.5D + BASE_HALF_WIDTH - BASE_BOTTOM_WIDTH_GAP, BASE_BOTTOM_HEIGHT, 0.5D + BASE_HALF_DEPTH - BASE_BOTTOM_WIDTH_GAP);
		
		// lid

		lid = new BlockModel();

		for (double iTempLayer = 0; iTempLayer < NUM_LID_LAYERS; iTempLayer++ )
		{
			double WidthOffset = iTempLayer * LID_LAYER_WIDTH_GAP;
			double HeighOffset = iTempLayer * LID_LAYER_HEIGHT;
			
			lid.addBox(0.5D - LID_HALF_WIDTH + WidthOffset,
					   BASE_HEIGHT + HeighOffset,
					   0.5D - LID_HALF_DEPTH + WidthOffset,
					   0.5D + LID_HALF_WIDTH - WidthOffset,
					   BASE_HEIGHT + LID_LAYER_HEIGHT + HeighOffset,
					   0.5D + LID_HALF_DEPTH - WidthOffset);
		}
		
		// interior

		selectionBox = new AxisAlignedBB(0.5D - BASE_HALF_WIDTH, BASE_BOTTOM_HEIGHT, 0.5D - BASE_HALF_DEPTH,
										 0.5D + BASE_HALF_WIDTH, BASE_HEIGHT + (LID_LAYER_HEIGHT * 2F ), 0.5D + BASE_HALF_DEPTH);

		selectionBoxOpen = new AxisAlignedBB(0.5D - BASE_HALF_WIDTH, BASE_BOTTOM_HEIGHT, 0.5D - BASE_HALF_DEPTH,
											 0.5D + BASE_HALF_WIDTH, BASE_HEIGHT, 0.5D + BASE_HALF_DEPTH);
	}
	
	public Vec3 getLidRotationPoint()
	{
    	return lidRotationPoint;
	}	
}
