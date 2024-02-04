//FCMOD

package btw.entity.model;

import btw.entity.mob.SheepEntity;
import btw.entity.mechanical.source.VerticalWindMillEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class WindMillVerticalSailsModel extends ModelBase
{
    public ModelRenderer components[];
    
    private static final int NUM_BLADES = 8;
    private static final int NUM_COMPONENTS = NUM_BLADES;
    
    private static final float LOCAL_PI = 3.141593F;
    
    private static final float SAIL_OFFSET_FROM_CENTER = (VerticalWindMillEntity.WIDTH / 2F * 16F ) + 0.5F;
    
    private static final int SAIL_LENGTH = (int)(VerticalWindMillEntity.HEIGHT * 16.0f ) - 8;
    private static final float HALF_SAIL_LENGTH = ((float) SAIL_LENGTH / 2F );
    
    private static final int SAIL_WIDTH = 20;
    private static final float HALF_SAIL_WIDTH = ((float) SAIL_WIDTH / 2F );
    
    private static final int SAIL_THICKNESS = 1;
    private static final float HALF_SAIL_THICKNESS = ((float) SAIL_THICKNESS / 2F );
    
    public WindMillVerticalSailsModel()
    {
        components = new ModelRenderer[NUM_COMPONENTS];
  
    	
    	for (int iTempBlade = 0; iTempBlade < NUM_BLADES; iTempBlade++ )
    	{
    		// sail 

            components[iTempBlade] = new ModelRenderer(this, 0, 0 );
    		
    		components[iTempBlade].setTextureSize(16, 16);
    		
    		components[iTempBlade].addBox(SAIL_OFFSET_FROM_CENTER - HALF_SAIL_THICKNESS, -HALF_SAIL_LENGTH, -(float) SAIL_WIDTH, SAIL_THICKNESS, SAIL_LENGTH,
                                          SAIL_WIDTH);
    		
    		components[iTempBlade].setRotationPoint(0F, 0F, 0F);

            components[iTempBlade].rotateAngleY = (LOCAL_PI * 2F ) * (float)iTempBlade / (float) NUM_BLADES;
    	}
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
    }
    
	//------------- Class Specific Methods ------------//
    
    public void render( float f, float f1, float f2, float f3, float f4, float fScale, VerticalWindMillEntity windMillEnt )
    {
        // render blades
        
        float fBrightness = 1.0F;        
        
        for (int i = 0; i < NUM_COMPONENTS; i++ )
        {
            int iBladeColor = windMillEnt.getBladeColor( i );
            
            GL11.glColor3f( fBrightness * SheepEntity.fleeceColorTable[iBladeColor][0],
        		fBrightness * SheepEntity.fleeceColorTable[iBladeColor][1],
        		fBrightness * SheepEntity.fleeceColorTable[iBladeColor][2]);
            
        	components[i].render(fScale);
        }
    }    
}