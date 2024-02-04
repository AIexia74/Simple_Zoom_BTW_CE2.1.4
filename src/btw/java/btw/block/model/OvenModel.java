// FCMOD

package btw.block.model;

public class OvenModel extends BlockModel
{
	public static final float BASE_HEIGHT = (6F / 16F );
	
	public static final float SIDE_WIDTH = (4F / 16F );
	public static final float HALF_SIDE_WIDTH = (SIDE_WIDTH / 2F );
	
	public static final float TOP_THICKNESS = (4F / 16F );
	public static final float HALF_TOP_THICKNESS = (TOP_THICKNESS / 2F );
	
	public static final float MIND_THE_GAP = 0.001F;
	
	@Override
    protected void initModel()
    {
    	// interior
    	
		/*
    	AddBox( SideWidth - MindTheGap, BaseHeight - MindTheGap, 0F,
    		1F - ( SideWidth - MindTheGap ), 1F - ( TopThickness - MindTheGap), 1F - SideWidth );
		*/
		
		// inverted interior
		
    	addBox(1F - (SIDE_WIDTH - MIND_THE_GAP), 1F - (TOP_THICKNESS - MIND_THE_GAP), 1F - SIDE_WIDTH,
			   SIDE_WIDTH - MIND_THE_GAP, BASE_HEIGHT - MIND_THE_GAP, 0F);
    }    
}
