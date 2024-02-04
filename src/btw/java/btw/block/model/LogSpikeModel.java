// FCMOD

package btw.block.model;

import net.minecraft.src.AxisAlignedBB;

public class LogSpikeModel extends BlockModel
{
    private final static float RIM_WIDTH = (1F / 16F );
    
    private final static float LAYER_HEIGHT = (2F / 16F );
    private final static float FIRST_LAYER_HEIGHT = (3F / 16F );
    private final static float LAYER_WIDTH_GAP = (1F / 16F );
    
    private final static float SELECTION_HEIGHT = FIRST_LAYER_HEIGHT;
    private final static float SELECTION_WIDTH_GAP = RIM_WIDTH;
    
    public final static AxisAlignedBB boxSelection = new AxisAlignedBB(
            LogSpikeModel.SELECTION_WIDTH_GAP, 0F, LogSpikeModel.SELECTION_WIDTH_GAP,
    	1F - LogSpikeModel.SELECTION_WIDTH_GAP, LogSpikeModel.SELECTION_HEIGHT, 1F - LogSpikeModel.SELECTION_WIDTH_GAP);
    
	@Override
    protected void initModel()
    {
        addBox(RIM_WIDTH, 0, RIM_WIDTH, 1F - RIM_WIDTH, FIRST_LAYER_HEIGHT, 1F - RIM_WIDTH);
        
        for ( int iTempLayer = 1; iTempLayer <= 6; iTempLayer++ )
        {        
	        float fWidthGap = RIM_WIDTH + (LAYER_WIDTH_GAP * iTempLayer );
	        float fHeightGap = FIRST_LAYER_HEIGHT + (LAYER_HEIGHT * (iTempLayer - 1 ) );
	        
	        addBox(fWidthGap, fHeightGap, fWidthGap, 1F - fWidthGap, fHeightGap + LAYER_HEIGHT, 1F - fWidthGap);
        }        
    }
	
    //------------- Class Specific Methods ------------//
}
