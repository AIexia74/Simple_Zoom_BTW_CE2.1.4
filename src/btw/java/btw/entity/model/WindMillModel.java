//FCMOD (client only)

package btw.entity.model;

import btw.entity.mob.SheepEntity;
import btw.entity.mechanical.source.WindMillEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class WindMillModel extends ModelBase
{
    public ModelRenderer windMillComponents[];
    
    private static final int NUM_WIND_MILL_COMPONENTS = 8;
    
    private static final float LOCAL_PI = 3.141593F;
    
    private static final float BLADE_OFFSET_FROM_CENTER = 15.0f;
    private static final int BLADE_LENGTH = (int)((WindMillEntity.HEIGHT * 8.0f ) - BLADE_OFFSET_FROM_CENTER) - 3;
    private static final int BLADE_WIDTH = 16;
    
    private static final float SHAFT_OFFSET_FROM_CENTER = 2.5f;
    private static final int SHAFT_LENGTH = (int)((WindMillEntity.HEIGHT * 8.0f ) - SHAFT_OFFSET_FROM_CENTER) - 2;
    private static final int SHAFT_WIDTH = 4;
    	
    public WindMillModel()
    {
    	windMillComponents = new ModelRenderer[NUM_WIND_MILL_COMPONENTS];
  
    	// shafts
    	
    	for ( int i = 0; i < 4; i++ )
    	{
    		windMillComponents[i] = new ModelRenderer( this, 0, 0 );
    		
    		windMillComponents[i].addBox(SHAFT_OFFSET_FROM_CENTER, -(float) SHAFT_WIDTH / 2.0f, -(float) SHAFT_WIDTH / 2.0f, SHAFT_LENGTH, SHAFT_WIDTH,
                                         SHAFT_WIDTH);
    		
    		windMillComponents[i].setRotationPoint( 0F, 0F, 0F );
        	
    		windMillComponents[i].rotateAngleZ = LOCAL_PI * (float)(i - 4 ) / 2.0F ;
    	}    	
    	
    	// blades
    	
    	for ( int i = 4; i < 8; i++ )
    	{
    		windMillComponents[i] = new ModelRenderer( this, 0, 15 );
    		windMillComponents[i].addBox(BLADE_OFFSET_FROM_CENTER, 1.75f/*-(float)iBladeWidth / 2.0f*/, 1.0F, BLADE_LENGTH, BLADE_WIDTH, 1);
    		windMillComponents[i].setRotationPoint( 0F, 0F, 0F );
        	
    		windMillComponents[i].rotateAngleX = -LOCAL_PI / 12.0F ; // 15 degrees
    		windMillComponents[i].rotateAngleZ = LOCAL_PI * (float)i / 2.0F ;
    	}
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
    }
    
	//------------- Class Specific Methods ------------//
    
    public void render( float f, float f1, float f2, float f3, float f4, float f5, WindMillEntity windMillEnt )
    {
    	// render shafts
    	
        for ( int i = 0; i < 4; i++ )
        {
        	windMillComponents[i].render( f5 );
        }
        
        // render blades
        
        float fBrightness = 1.0F;//windMillEnt.getBrightness(f);	// render change brought about in 1.0(?)        
        
        for ( int i = 4; i < 8; i++ )
        {
            int iBladeColor = windMillEnt.getBladeColor( i - 4 );
            
            GL11.glColor3f( fBrightness * SheepEntity.fleeceColorTable[iBladeColor][0],
        		fBrightness * SheepEntity.fleeceColorTable[iBladeColor][1],
        		fBrightness * SheepEntity.fleeceColorTable[iBladeColor][2]);
            
        	windMillComponents[i].render( f5 );        	
        }
    }    
}