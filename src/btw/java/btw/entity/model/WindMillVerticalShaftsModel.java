//FCMOD

package btw.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import btw.entity.mechanical.source.VerticalWindMillEntity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

@Environment(EnvType.CLIENT)
public class WindMillVerticalShaftsModel extends ModelBase
{
    public ModelRenderer components[];
    
    private static final int NUM_BLADES = 8;
    private static final int NUM_COMPONENTS = NUM_BLADES;
    
    private static final float LOCAL_PI = 3.141593F;
    
    private static final float SHAFT_OFFSET_FROM_CENTER = (VerticalWindMillEntity.WIDTH / 2F * 16F );
    
    private static final int SHAFT_LENGTH = (int)(VerticalWindMillEntity.HEIGHT * 16F );
    
    private static final float SHAFT_HALF_LENGTH = ((float) SHAFT_LENGTH / 2F );
    
    private static final int SHAFT_WIDTH = 4;
    
    private static final float HALF_SHAFT_WIDTH = ((float) SHAFT_WIDTH / 2F );
    	
    public WindMillVerticalShaftsModel()
    {
        components = new ModelRenderer[NUM_COMPONENTS];
  
    	
    	for (int iTempBlade = 0; iTempBlade < NUM_BLADES; iTempBlade++ )
    	{
        	// shaft

            components[iTempBlade] = new ModelRenderer(this, 0, 0 );
    		
    		components[iTempBlade].setTextureSize(16, 16);
    		
    		components[iTempBlade].addBox(SHAFT_OFFSET_FROM_CENTER - HALF_SHAFT_WIDTH, -SHAFT_HALF_LENGTH, -HALF_SHAFT_WIDTH, SHAFT_WIDTH, SHAFT_LENGTH,
                                          SHAFT_WIDTH);
    		
    		components[iTempBlade].setRotationPoint(0F, 0F, 0F);

            components[iTempBlade].rotateAngleY = (LOCAL_PI * 2F ) * (float)iTempBlade / (float) NUM_BLADES;
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