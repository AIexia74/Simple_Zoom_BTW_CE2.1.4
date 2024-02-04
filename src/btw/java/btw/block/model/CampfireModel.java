// FCMOD

package btw.block.model;

public class CampfireModel extends BlockModel
{
    private static final float LOG_THICKNESS = (2F / 16F );
    
    private static final float MINOR_BORDER_WIDTH = (1F / 16F );
    private static final float MAJOR_BORDER_WIDTH = (0F / 16F );
    
    private static final float LAYER_WIDTH_OFFSET = (1F / 16F );
    private static final float LAYER_LENGTH_OFFSET = (1F / 16F );
    
    private static final int NUM_LAYERS = 4;
    
    public BlockModel modelInSnow;
    
	@Override
    protected void initModel()
    {
		modelInSnow = new BlockModel();
		
        for (int iTempLayer = 0; iTempLayer < NUM_LAYERS; iTempLayer++ )
        {
        	float fLayerMultiplier = (float)iTempLayer;
        	
        	float fMinY = fLayerMultiplier * LOG_THICKNESS;
        	float fMaxY = fMinY + LOG_THICKNESS;
        	
        	float fMinorOffset = 0F;
        	float fMajorOffset = 0F;
        	
        	if ( iTempLayer > 1 )
        	{
        		fMajorOffset = LAYER_LENGTH_OFFSET * (fLayerMultiplier - 1F );
        	}
    		fMinorOffset = LAYER_WIDTH_OFFSET * ( fLayerMultiplier );
        	
        	float fMinorAxisMin = MINOR_BORDER_WIDTH + fMinorOffset;
        	float fMinorAxisMax = fMinorAxisMin + LOG_THICKNESS;
        	
        	float fMajorAxisMin = MAJOR_BORDER_WIDTH + fMajorOffset;
        	float fMajorAxisMax = 1F - fMajorAxisMin;
        	
        	if ( ( iTempLayer & 1 ) == 0 )
        	{
        		addBox(fMajorAxisMin, fMinY, fMinorAxisMin, fMajorAxisMax, fMaxY, fMinorAxisMax);
        		addBox(fMajorAxisMin, fMinY, 1F - fMinorAxisMax, fMajorAxisMax, fMaxY, 1F - fMinorAxisMin);
        		
        		if ( iTempLayer != 0 )
        		{
        			modelInSnow.addBox(fMajorAxisMin, fMinY, fMinorAxisMin, fMajorAxisMax, fMaxY, fMinorAxisMax);
        			modelInSnow.addBox(fMajorAxisMin, fMinY, 1F - fMinorAxisMax, fMajorAxisMax, fMaxY, 1F - fMinorAxisMin);
        		}
        	}
        	else
        	{
        		addBox(fMinorAxisMin, fMinY, fMajorAxisMin, fMinorAxisMax, fMaxY, fMajorAxisMax);
        		addBox(1F - fMinorAxisMax, fMinY, fMajorAxisMin, 1F - fMinorAxisMin, fMaxY, fMajorAxisMax);
        		
        		modelInSnow.addBox(fMinorAxisMin, fMinY, fMajorAxisMin, fMinorAxisMax, fMaxY, fMajorAxisMax);
        		modelInSnow.addBox(1F - fMinorAxisMax, fMinY, fMajorAxisMin, 1F - fMinorAxisMin, fMaxY, fMajorAxisMax);
        	}
        }
    }
}
