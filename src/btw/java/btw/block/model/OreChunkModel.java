// FCMOD

package btw.block.model;

public class OreChunkModel extends BlockModel
{
    protected static final double BASE_WIDTH = (3D / 16D );
    protected static final double BASE_HALF_WIDTH = (BASE_WIDTH / 2D );
    protected static final double BASE_HEIGHT = (0.5D / 16D );
    
    protected static final double CENTER_WIDTH = (4D / 16D );
    protected static final double CENTER_HALF_WIDTH = (CENTER_WIDTH / 2D );
    protected static final double CENTER_HEIGHT = (3D / 16D );
    
    protected static final double TOP_WIDTH = (2D / 16D );
    protected static final double TOP_HALF_WIDTH = (TOP_WIDTH / 2D );
    protected static final double TOP_HEIGHT = (0.5D / 16D );
    protected static final double TOP_VERTICAL_OFFSET = (BASE_HEIGHT + CENTER_HEIGHT);
    protected static final double TOP_X_OFFSET = ( 0D );
    protected static final double TOP_Z_OFFSET = (0.5D / 16D );
    
    protected static final double FRINGE_X_WIDTH = (5D / 16D );
    protected static final double FRINGE_X_HALF_WIDTH = (FRINGE_X_WIDTH / 2D );
    protected static final double FRINGE_X_DEPTH = (3D / 16D );
    protected static final double FRINGE_X_HALF_DEPTH = (FRINGE_X_DEPTH / 2D );
    protected static final double FRINGE_X_HEIGHT = (1.5D / 16D );
    protected static final double FRINGE_X_VERTICAL_OFFSET = (BASE_HEIGHT + (0.5D / 16D ) );
    
    protected static final double FRINGE_Z_WIDTH = (3D / 16D );
    protected static final double FRINGE_Z_HALF_WIDTH = (FRINGE_Z_WIDTH / 2D );
    protected static final double FRINGE_Z_DEPTH = (5D / 16D );
    protected static final double FRINGE_Z_HALF_DEPTH = (FRINGE_Z_DEPTH / 2D );
    protected static final double FRINGE_Z_HEIGHT = (1.5D / 16D );
    protected static final double FRINGE_Z_VERTICAL_OFFSET = (BASE_HEIGHT + (1D / 16D ) );
    
    public static final double BOUNDING_BOX_WIDTH = (CENTER_WIDTH);
    public static final double BOUNDING_BOX_HALF_WIDTH = (BOUNDING_BOX_WIDTH / 2D );
    public static final double BOUNDING_BOX_HEIGHT = (CENTER_HEIGHT);
    public static final double BOUNDING_BOX_VERTICAL_OFFSET = (BASE_HEIGHT);
    
	@Override
    protected void initModel()
    {
		super.initModel();
		
		// base
		
		addBox(0.5D - BASE_HALF_WIDTH, 0D, 0.5D - BASE_HALF_WIDTH,
               0.5D + BASE_HALF_WIDTH, BASE_HEIGHT, 0.5D + BASE_HALF_WIDTH);
		
		// center
		
		addBox(0.5D - CENTER_HALF_WIDTH, BASE_HEIGHT, 0.5D - CENTER_HALF_WIDTH,
               0.5D + CENTER_HALF_WIDTH, BASE_HEIGHT + CENTER_HEIGHT, 0.5D + CENTER_HALF_WIDTH);
		
		// top
		
		addBox(0.5D - TOP_HALF_WIDTH + TOP_X_OFFSET, TOP_VERTICAL_OFFSET,
               0.5D - TOP_HALF_WIDTH + TOP_Z_OFFSET,
               0.5D + TOP_HALF_WIDTH + TOP_X_OFFSET, TOP_VERTICAL_OFFSET + TOP_HEIGHT,
               0.5D + TOP_HALF_WIDTH + TOP_Z_OFFSET);
		
		// fringe
		
		addBox(0.5D - FRINGE_X_HALF_WIDTH, FRINGE_X_VERTICAL_OFFSET, 0.5D - FRINGE_X_HALF_DEPTH,
               0.5D + FRINGE_X_HALF_WIDTH, FRINGE_X_VERTICAL_OFFSET + FRINGE_X_HEIGHT,
               0.5D + FRINGE_X_HALF_DEPTH);
		
		addBox(0.5D - FRINGE_Z_HALF_WIDTH, FRINGE_Z_VERTICAL_OFFSET, 0.5D - FRINGE_Z_HALF_DEPTH,
               0.5D + FRINGE_Z_HALF_WIDTH, FRINGE_Z_VERTICAL_OFFSET + FRINGE_Z_HEIGHT,
               0.5D + FRINGE_Z_HALF_DEPTH);
    }
}