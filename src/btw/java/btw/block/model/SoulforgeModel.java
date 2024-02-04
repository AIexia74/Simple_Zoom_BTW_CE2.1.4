// FCMOD

package btw.block.model;

public class SoulforgeModel extends BlockModel
{
	public static final float ANVIL_BASE_HEIGHT = (2.0F / 16F );
	public static final float ANVIL_BASE_WIDTH = (8.0F / 16F );
	public static final float ANVIL_HALF_BASE_WIDTH = (ANVIL_BASE_WIDTH / 2F );
	public static final float ANVIL_SHAFT_HEIGHT = (7.0F / 16F );
	public static final float ANVIL_SHAFT_WIDTH = (4.0F / 16F );
	public static final float ANVIL_HALF_SHAFT_WIDTH = (ANVIL_SHAFT_WIDTH / 2.0F );
	public static final float ANVIL_TOP_HEIGHT = (7.0F / 16F );
	public static final float ANVIL_TOP_WIDTH = (6.0F / 16F );
	public static final float ANVIL_TOP_HALF_WIDTH = (ANVIL_TOP_WIDTH / 2F );
	
	@Override
    protected void initModel()
    {
    	// base
    	
    	addBox(0.5F - ANVIL_HALF_BASE_WIDTH, 0.0F, 0.0F,
			   0.5F + ANVIL_HALF_BASE_WIDTH, ANVIL_BASE_HEIGHT, 1.0F);
        
        // shaft 
        
    	addBox(0.5F - ANVIL_HALF_SHAFT_WIDTH, ANVIL_BASE_HEIGHT, 0.5F - ANVIL_HALF_SHAFT_WIDTH,
			   0.5F + ANVIL_HALF_SHAFT_WIDTH, ANVIL_BASE_HEIGHT + ANVIL_SHAFT_HEIGHT, 0.5F + ANVIL_HALF_SHAFT_WIDTH);
        
        // top
        
    	addBox(0.5F - ANVIL_TOP_HALF_WIDTH, 1.0F - ANVIL_TOP_HEIGHT, 0.5F - ANVIL_TOP_HALF_WIDTH,
			   0.5F + ANVIL_TOP_HALF_WIDTH, 1.0F, 0.5F + ANVIL_TOP_HALF_WIDTH);
        
        // back end
        
    	addBox(0.5F - ANVIL_TOP_HALF_WIDTH, 1.0F - ANVIL_TOP_HEIGHT + (3.0F / 16F ), 0.5F - (ANVIL_TOP_HALF_WIDTH + (1.0F / 16F ) ),
			   0.5F + ANVIL_TOP_HALF_WIDTH, 1.0F, 0.5F - ANVIL_TOP_HALF_WIDTH);
        
        addBox(0.5F - ANVIL_TOP_HALF_WIDTH, 1.0F - (1.0F / 16F ), 0F,
			   0.5F + ANVIL_TOP_HALF_WIDTH, 1.0F, 0.5F - (ANVIL_TOP_HALF_WIDTH + (1.0F / 16F ) ));
        
        // front end

        addBox(0.5F - ANVIL_TOP_HALF_WIDTH, 1.0F - ANVIL_TOP_HEIGHT + (3.0F / 16F ), 0.5F + ANVIL_TOP_HALF_WIDTH,
			   0.5F + ANVIL_TOP_HALF_WIDTH, 1.0F, 0.5F + ANVIL_TOP_HALF_WIDTH + (1.0F / 16F ));
        
        addBox(0.5F - ANVIL_TOP_HALF_WIDTH + (1.0F / 16F ), 1.0F - ANVIL_TOP_HEIGHT + (4.0F / 16F ), 0.5F + ANVIL_TOP_HALF_WIDTH + (1.0F / 16F ),
			   0.5F + ANVIL_TOP_HALF_WIDTH - (1.0F / 16F ), 1.0F, 0.5F + ANVIL_TOP_HALF_WIDTH + (3.0F / 16F ));
        
        addBox(0.5F - ANVIL_TOP_HALF_WIDTH + (2.0F / 16F ), 1.0F - ANVIL_TOP_HEIGHT + (5.0F / 16F ), 0.5F + ANVIL_TOP_HALF_WIDTH + (3.0F / 16F ),
			   0.5F + ANVIL_TOP_HALF_WIDTH - (2.0F / 16F ), 1.0F, 0.5F + ANVIL_TOP_HALF_WIDTH + (5.0F / 16F ));
    }    
}
