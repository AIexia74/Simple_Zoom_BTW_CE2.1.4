// FCMOD

package btw.block.model;

import btw.util.PrimitiveQuad;
import net.minecraft.src.Vec3;

public class PistonShovelModel extends BlockModel
{
	public static final float SIDE_THICKNESS = (1F / 16F );
	public static final float SLOP_HEIGHT = (2F / 16F );
	public static final float BACK_SLOP_HEIGHT = (4F / 16F );
	public static final float SLOPE_COLLISION_HEIGHT = (0.25F / 16F );
	
	public static final float SLOPE_MIDDLE_MAJOR_GAP = (6F / 16F );
	
	public static final double MIND_THE_GAP = 0.001D;
	
	public BlockModel rayTraceModel; // simplified collision model to cut down on ray trace overhead
	public BlockModel collisionModel; // stair step model for climbing
	
	@Override
    protected void initModel()
    {
		rayTraceModel = new BlockModel();
		collisionModel = new BlockModel();
		
		// Side walls
		
    	addBox(0D, 0D, 0.5D, SIDE_THICKNESS, 1D, 1D);
    	addBox(0D, 0D, 0D, SIDE_THICKNESS, 0.5D, 0.5D);
    	
    	rayTraceModel.addBox(0D, 0D, 0.5D, SIDE_THICKNESS, 1D, 1D);
    	rayTraceModel.addBox(0D, 0D, 0D, SIDE_THICKNESS, 0.5D, 0.5D);
    	
    	addBox(1D - SIDE_THICKNESS, 0D, 0.5D, 1D, 1D, 1D);
    	addBox(1D - SIDE_THICKNESS, 0D, 0D, 1D, 0.5D, 0.5D);
    	
    	rayTraceModel.addBox(1D - SIDE_THICKNESS, 0D, 0.5D, 1D, 1D, 1D);
    	rayTraceModel.addBox(1D - SIDE_THICKNESS, 0D, 0D, 1D, 0.5D, 0.5D);

    	// Wedge slope
    	
    	addPrimitive(( new PrimitiveQuad(
    		Vec3.createVectorHelper(SIDE_THICKNESS, 0D, 0D),
    		Vec3.createVectorHelper(SIDE_THICKNESS, SLOP_HEIGHT, 1D - SLOPE_MIDDLE_MAJOR_GAP),
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, SLOP_HEIGHT, 1D - SLOPE_MIDDLE_MAJOR_GAP),
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, 0D, 0D)
    		) ).setIconIndex(1).setUVFractions(SIDE_THICKNESS, 0F, 1F - SIDE_THICKNESS, 1F - SLOPE_MIDDLE_MAJOR_GAP));
    	
    	// Wedge base
    	
    	addPrimitive(( new PrimitiveQuad(
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, 0D, 0D),
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, 0D, 1D - BACK_SLOP_HEIGHT),
    		Vec3.createVectorHelper(SIDE_THICKNESS, 0D, 1D - BACK_SLOP_HEIGHT),
    		Vec3.createVectorHelper(SIDE_THICKNESS, 0D, 0D)
    		) ).setIconIndex(2).setUVFractions(SIDE_THICKNESS, 0F, 1F - SIDE_THICKNESS, 1F - BACK_SLOP_HEIGHT));
    	
    	rayTraceModel.addBox(0D, 0D, 0D, 1D, SLOPE_COLLISION_HEIGHT, 1D - BACK_SLOP_HEIGHT);
    	
    	// Top Wedge slope
    	
    	addPrimitive(( new PrimitiveQuad(
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, 1D, 1D),
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, SLOPE_MIDDLE_MAJOR_GAP, 1D - SLOP_HEIGHT),
    		Vec3.createVectorHelper(SIDE_THICKNESS, SLOPE_MIDDLE_MAJOR_GAP, 1D - SLOP_HEIGHT),
    		Vec3.createVectorHelper(SIDE_THICKNESS, 1D, 1D)
    		) ).setIconIndex(1).setUVFractions(SIDE_THICKNESS, 0F, 1F - SIDE_THICKNESS, 1F - SLOPE_MIDDLE_MAJOR_GAP));
    	
    	// Top Wedge Base
    	
    	addPrimitive(( new PrimitiveQuad(
    		Vec3.createVectorHelper(SIDE_THICKNESS, 1D, 1D),
    		Vec3.createVectorHelper(SIDE_THICKNESS, 0 + BACK_SLOP_HEIGHT, 1D),
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, 0 + BACK_SLOP_HEIGHT, 1D),
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, 1D, 1D)
    		) ).setIconIndex(2).setUVFractions(SIDE_THICKNESS, 0F, 1F - SIDE_THICKNESS, 1F - BACK_SLOP_HEIGHT));
    	
    	rayTraceModel.addBox(0D, BACK_SLOP_HEIGHT, 1D - SLOPE_COLLISION_HEIGHT, 1D, 1D, 1D);
    	
    	// Front Middle slope
    	
    	addPrimitive(( new PrimitiveQuad(
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, SLOPE_MIDDLE_MAJOR_GAP, 1D - SLOP_HEIGHT),
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, SLOP_HEIGHT, 1D - SLOPE_MIDDLE_MAJOR_GAP),
    		Vec3.createVectorHelper(SIDE_THICKNESS, SLOP_HEIGHT, 1D - SLOPE_MIDDLE_MAJOR_GAP),
    		Vec3.createVectorHelper(SIDE_THICKNESS, SLOPE_MIDDLE_MAJOR_GAP, 1D - SLOP_HEIGHT)
			) ).setIconIndex(3).setUVFractions(SIDE_THICKNESS, 0F, 1F - SIDE_THICKNESS, (6F / 16F )));
    	
    	// Back Middle Slope
    	
    	addPrimitive(( new PrimitiveQuad(
    		Vec3.createVectorHelper(SIDE_THICKNESS, 0 + BACK_SLOP_HEIGHT, 1D),
    		Vec3.createVectorHelper(SIDE_THICKNESS, 0D, 1D - BACK_SLOP_HEIGHT),
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, 0D, 1D - BACK_SLOP_HEIGHT),
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, 0 + BACK_SLOP_HEIGHT, 1D)
			) ).setIconIndex(3).setUVFractions(SIDE_THICKNESS, 1F - (6F / 16F ), 1F - SIDE_THICKNESS, 1F));
    	
    	rayTraceModel.addPrimitive(( new PrimitiveQuad(
    		Vec3.createVectorHelper(SIDE_THICKNESS, 0 + BACK_SLOP_HEIGHT, 1D),
    		Vec3.createVectorHelper(SIDE_THICKNESS, 0D, 1D - BACK_SLOP_HEIGHT),
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, 0D, 1D - BACK_SLOP_HEIGHT),
    		Vec3.createVectorHelper(1D - SIDE_THICKNESS, 0 + BACK_SLOP_HEIGHT, 1D)
			) ).setIconIndex(3).setUVFractions(SIDE_THICKNESS, 1F - (6F / 16F ), 1F - SIDE_THICKNESS, 1F));
    	
    	// stair steps for collision
    	
    	collisionModel.addBox(0D, 0D, 0D, 1D, 0.5D, 1D);
    	collisionModel.addBox(0D, 0.5D, 0.5D, 1D, 1D, 1D);
    }    
}
