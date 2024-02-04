// FCMOD

package btw.block.model;

import btw.util.PrimitiveAABBWithBenefits;
import net.minecraft.src.Vec3;

public class BucketModel extends BlockModel
{
	public static final int ASSEMBLY_ID_BASE = 0;
	public static final int ASSEMBLY_ID_BODY = 1;
	public static final int ASSEMBLY_ID_RIM = 2;
	public static final int ASSEMBLY_ID_INTERIOR = 3;
	public static final int ASSEMBLY_ID_CONTENTS = 4;
	
	public static final double HEIGHT = (8D / 16D );
	public static final double WIDTH = (8D / 16D );
	public static final double HALF_WIDTH = (WIDTH / 2D );
	
	public static final double BASE_HEIGHT = (1D / 16D );
	public static final double BASE_WIDTH = (6D / 16D );
	public static final double BASE_HALF_WIDTH = (BASE_WIDTH / 2D );
	
	public static final double BODY_WIDTH = (7D / 16D );
	public static final double BODY_HALF_WIDTH = (BODY_WIDTH / 2D );
	
	public static final double RIM_HEIGHT = (1D / 16D );
	
	public static final double INTERIOR_HEIGHT = (HEIGHT - BASE_HEIGHT - (1.5D / 16D ) );
	public static final double INTERIOR_WIDTH = (6D / 16D );
	public static final double INTERIOR_HALF_WIDTH = (INTERIOR_WIDTH / 2D );
	
	private static final double MIND_THE_GAP = 0.001D; // slight offset used to avoid visual seams
	
	public BucketModel()
	{
		super();
	}
	
	@Override
    protected void initModel()
    {
		BlockModel tempModel;
		PrimitiveAABBWithBenefits tempBox;
		
		// base
		
		tempModel = new BlockModel(ASSEMBLY_ID_BASE);
		
		tempModel.addBox(0.5D - BASE_HALF_WIDTH, 0D, 0.5D - BASE_HALF_WIDTH,
						 0.5D + BASE_HALF_WIDTH, BASE_HEIGHT, 0.5D + BASE_HALF_WIDTH);
		
		addPrimitive(tempModel);
		
		// body
		
		tempModel = new BlockModel(ASSEMBLY_ID_BODY);
		
		tempModel.addBox(0.5D - BODY_HALF_WIDTH, BASE_HEIGHT, 0.5D - BODY_HALF_WIDTH,
						 0.5D + BODY_HALF_WIDTH, HEIGHT - RIM_HEIGHT, 0.5D + BODY_HALF_WIDTH);

		addPrimitive(tempModel);
		
		// Rim
		
		tempBox = new PrimitiveAABBWithBenefits(
				0.5D - HALF_WIDTH, HEIGHT - RIM_HEIGHT, 0.5D - HALF_WIDTH,
				0.5D + HALF_WIDTH, HEIGHT, 0.5D + HALF_WIDTH, ASSEMBLY_ID_RIM);
		
		addPrimitive(tempBox);
		
		// Interior
		
		tempBox = new PrimitiveAABBWithBenefits(
				0.5D + INTERIOR_HALF_WIDTH + MIND_THE_GAP, HEIGHT,
				0.5D + INTERIOR_HALF_WIDTH + MIND_THE_GAP,
				0.5D - INTERIOR_HALF_WIDTH - MIND_THE_GAP,
				HEIGHT - INTERIOR_HEIGHT,
				0.5D - INTERIOR_HALF_WIDTH - MIND_THE_GAP, ASSEMBLY_ID_INTERIOR);
		
		tempBox.setForceRenderWithColorMultiplier(true);

		addPrimitive(tempBox);
    }
	
	//------------- Class Specific Methods ------------//
    
    public static void offsetModelForFacing(BlockModel model, int iFacing)
    {
    	if ( iFacing != 1 )
    	{
			Vec3 offset = getOffsetForFacing(iFacing);
			
			model.translate(offset.xCoord, offset.yCoord, offset.zCoord);
    	}
    }

    /**
     * Assumes facing is not 1, where there should be no offset
     */
    public static Vec3 getOffsetForFacing(int iFacing)
    {
    	if ( iFacing == 0 )
    	{
    		return Vec3.createVectorHelper(0D, -1D + HEIGHT, 0D);
    	}
    	else
    	{    		
    		Vec3 offset = Vec3.createVectorHelper( 0D, -0.5D + HALF_WIDTH,
												   -1D + HEIGHT);
			
			offset.rotateAsVectorAroundJToFacing(iFacing);
			
			return offset;
    	}
    }
	
	//----------- Client Side Functionality -----------//
}
