// FCMOD

package btw.block.model;

import btw.util.PrimitiveAABBWithBenefits;

public class BucketFullModel extends BucketModel
{
	public static final double CONTENTS_HEIGHT = (HEIGHT - BASE_HEIGHT - (1.5D / 16D ) );
	public static final double CONTENTS_VERTICAL_OFFSET = (BASE_HEIGHT + (1D / 16D ) );
	public static final double CONTENTS_WIDTH = INTERIOR_WIDTH;
	public static final double CONTENTS_HALF_WIDTH = (CONTENTS_WIDTH / 2D );
	
	public BucketFullModel()
	{
		super();
	}
	
	@Override
    protected void initModel()
    {
		super.initModel();
		
		PrimitiveAABBWithBenefits tempBox = new PrimitiveAABBWithBenefits(
				0.5D - CONTENTS_HALF_WIDTH, CONTENTS_VERTICAL_OFFSET, 0.5D - CONTENTS_HALF_WIDTH,
				0.5D + CONTENTS_HALF_WIDTH, CONTENTS_VERTICAL_OFFSET + CONTENTS_HEIGHT,
				0.5D + CONTENTS_HALF_WIDTH, ASSEMBLY_ID_CONTENTS);
		
		addPrimitive(tempBox);
    }
}