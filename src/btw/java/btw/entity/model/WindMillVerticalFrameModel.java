//FCMOD

package btw.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import btw.entity.mechanical.source.VerticalWindMillEntity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

@Environment(EnvType.CLIENT)
public class WindMillVerticalFrameModel extends ModelBase
{
    public ModelRenderer components[];
    
    private static final int NUM_BLADES = 8;
    private static final int NUM_COMPONENTS = NUM_BLADES * 4;
    
    private static final float LOCAL_PI = 3.141593F;
    
    private static final float SPOKES_OFFSET_FROM_CENTER = 2;
    
    private static final int SPOKES_LENGTH = (int)(VerticalWindMillEntity.WIDTH / 2.0F * 16F ) - 3;

    private static final float HALF_SPOKES_LENGTH = ((float) SPOKES_LENGTH / 2F );

    private static final int SPOKES_WIDTH = 2;
    
    private static final float HALF_SPOKES_WIDTH = ((float) SPOKES_WIDTH / 2F );
    
    private static final float SPOKES_VERTICAL_OFFSET = (VerticalWindMillEntity.HEIGHT / 2F * 16F ) - 2.5F;
    
    private static final float RIM_OFFSET_FROM_CENTER = VerticalWindMillEntity.WIDTH / 2.0F * 16F - 4.5F;
    	
    private static final int RIM_SEGMENT_LENGTH = (int)(VerticalWindMillEntity.WIDTH / 2.0F * 16F ) - 18;
    
    private static final float HALF_RIM_SEGMENT_LENGTH = ((float) RIM_SEGMENT_LENGTH / 2F );
    
    public WindMillVerticalFrameModel()
    {
		components = new ModelRenderer[NUM_COMPONENTS];
    	
    	for (int iTempBlade = 0; iTempBlade < NUM_BLADES; iTempBlade++ )
    	{
        	// bottom spokes

			components[iTempBlade] = new ModelRenderer(this, 0, 0 );
    		
    		components[iTempBlade].setTextureSize(16, 16);
    		
    		components[iTempBlade].addBox(SPOKES_OFFSET_FROM_CENTER, -HALF_SPOKES_WIDTH - SPOKES_VERTICAL_OFFSET, -HALF_SPOKES_WIDTH, SPOKES_LENGTH,
										  SPOKES_WIDTH, SPOKES_WIDTH);
    		
    		components[iTempBlade].setRotationPoint(0F, 0F, 0F);

			components[iTempBlade].rotateAngleY = (LOCAL_PI * 2F ) * (float)iTempBlade / (float) NUM_BLADES;
    		
    		// top spokes

			components[iTempBlade + NUM_BLADES] = new ModelRenderer(this, 0, 0 );
    		
    		components[iTempBlade + NUM_BLADES].setTextureSize(16, 16);
    		
    		components[iTempBlade + NUM_BLADES].addBox(SPOKES_OFFSET_FROM_CENTER, -HALF_SPOKES_WIDTH + SPOKES_VERTICAL_OFFSET, -HALF_SPOKES_WIDTH,
													   SPOKES_LENGTH, SPOKES_WIDTH, SPOKES_WIDTH);
    		
    		components[iTempBlade + NUM_BLADES].setRotationPoint(0F, 0F, 0F);

			components[iTempBlade + NUM_BLADES].rotateAngleY = (LOCAL_PI * 2F ) * (float)iTempBlade / (float) NUM_BLADES;
    		
    		// bottom rim

			components[iTempBlade + NUM_BLADES * 2] = new ModelRenderer(this, 0, 0 );
    		
    		components[iTempBlade + NUM_BLADES * 2].setTextureSize(16, 16);
    		
    		components[iTempBlade + NUM_BLADES * 2].addBox(RIM_OFFSET_FROM_CENTER - HALF_SPOKES_WIDTH, -HALF_SPOKES_WIDTH - SPOKES_VERTICAL_OFFSET, -HALF_RIM_SEGMENT_LENGTH,
														   SPOKES_WIDTH, SPOKES_WIDTH, RIM_SEGMENT_LENGTH);
    		
    		components[iTempBlade + NUM_BLADES * 2].setRotationPoint(0F, 0F, 0F);

			components[iTempBlade + NUM_BLADES * 2].rotateAngleY = (LOCAL_PI * 2F ) * (float)iTempBlade / (float) NUM_BLADES + (LOCAL_PI * 2F / 16F );
    		
    		// top rim

			components[iTempBlade + NUM_BLADES * 3] = new ModelRenderer(this, 0, 0 );
    		
    		components[iTempBlade + NUM_BLADES * 3].setTextureSize(16, 16);
    		
    		components[iTempBlade + NUM_BLADES * 3].addBox(RIM_OFFSET_FROM_CENTER - HALF_SPOKES_WIDTH, -HALF_SPOKES_WIDTH + SPOKES_VERTICAL_OFFSET, -HALF_RIM_SEGMENT_LENGTH,
														   SPOKES_WIDTH, SPOKES_WIDTH, RIM_SEGMENT_LENGTH);
    		
    		components[iTempBlade + NUM_BLADES * 3].setRotationPoint(0F, 0F, 0F);

			components[iTempBlade + NUM_BLADES * 3].rotateAngleY = (LOCAL_PI * 2F ) * (float)iTempBlade / (float) NUM_BLADES + (LOCAL_PI * 2F / 16F );
    	}
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
    }
    
    //************* Class Specific Methods ************//
    
    public void render( float f, float f1, float f2, float f3, float f4, float fScale, VerticalWindMillEntity windMillEnt )
    {
        for (int i = 0; i < NUM_COMPONENTS; i++ )
        {
        	components[i].render(fScale);
        }        
    }    
}