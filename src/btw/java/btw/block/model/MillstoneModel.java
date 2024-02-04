// FCMOD

package btw.block.model;

import net.minecraft.src.AxisAlignedBB;

public class MillstoneModel extends BlockModel
{
	public static final double BASE_HEIGHT = (9D / 16D );
	
	public static final double MID_MIN_Y = (7D / 16D );
	public static final double MID_MAX_Y = 1D;
	public static final double MID_WIDTH_GAP = (1D / 16D );
	
	public static final double TOP_MIN_Y = (11D / 16D );
	public static final double TOP_HEIGHT = (4D / 16D );
	
	public AxisAlignedBB boxBase;
	public AxisAlignedBB boxSelection;
	
	@Override
    protected void initModel()
    {
		// base

		boxBase = new AxisAlignedBB(0D, 0D, 0D,
									1D, BASE_HEIGHT, 1D );
		
		// middle
		
		addBox(MID_WIDTH_GAP, MID_MIN_Y, MID_WIDTH_GAP,
			   1D - MID_WIDTH_GAP, MID_MAX_Y, 1D - MID_WIDTH_GAP);
		
		// top
		
		addBox(0D, TOP_MIN_Y, 0D,
			   1D, TOP_MIN_Y + TOP_HEIGHT, 1D);
		
		// selection

		boxSelection = new AxisAlignedBB(0D, 0D, 0D,
										 1D, 1D - ( 1D / 16D ), 1D );
    }
}
