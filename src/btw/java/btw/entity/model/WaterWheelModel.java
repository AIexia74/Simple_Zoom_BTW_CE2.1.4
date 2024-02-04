// FCMOD

package btw.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

@Environment(EnvType.CLIENT)
public class WaterWheelModel extends ModelBase
{
    public ModelRenderer waterWheelComponents[];
    
    private static final int NUM_WATER_WHEEL_COMPONENTS = 16;
    
    private static final float LOCAL_PI = 3.141593F;
    private static final float STRUT_DIST_FROM_CENT = 30.0F;

    public WaterWheelModel()
    {
    	waterWheelComponents = new ModelRenderer[NUM_WATER_WHEEL_COMPONENTS];
  
    	// blades
    	
    	for ( int i = 0; i < 8; i++ )
    	{
        	waterWheelComponents[i] = new ModelRenderer( this, 0, 0 );
        	waterWheelComponents[i].addBox( 2.50F, -1F, -7F, 36, 2, 14);
        	waterWheelComponents[i].setRotationPoint( 0F, 0F, 0F);
        	
        	waterWheelComponents[i].rotateAngleZ = LOCAL_PI * (float)i / 4.0F ;
    	}
    	
    	// struts
    	
    	for ( int i = 0; i < 8; i++ )
    	{
        	waterWheelComponents[i + 8] = new ModelRenderer( this, 0, 0 );
        	waterWheelComponents[i + 8].addBox( 0.0F, -1F, -6F, 23, 2, 12);
        	
        	float fRotationAngle = LOCAL_PI * 0.25F * i;
        	
        	waterWheelComponents[i + 8].setRotationPoint(STRUT_DIST_FROM_CENT * MathHelper.cos(fRotationAngle),
                                                         STRUT_DIST_FROM_CENT * MathHelper.sin(fRotationAngle), 0F);
        	
        	waterWheelComponents[i + 8].rotateAngleZ = (LOCAL_PI * 0.625F ) + (0.25F * LOCAL_PI * i );
    	}    		
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5 )
    {
        for(int i = 0; i < NUM_WATER_WHEEL_COMPONENTS; i++)
        {
        	waterWheelComponents[i].render( f5 );
        }
    }
    
    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
    }
}