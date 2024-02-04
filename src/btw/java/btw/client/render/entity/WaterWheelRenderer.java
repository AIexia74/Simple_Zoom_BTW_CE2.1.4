// FCMOD

package btw.client.render.entity;

import btw.entity.model.WaterWheelModel;
import btw.entity.mechanical.source.WaterWheelEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class WaterWheelRenderer extends Render
{
    protected ModelBase modelWaterWheel;

    public WaterWheelRenderer()
    {
        shadowSize = 0.0F;
        modelWaterWheel = new WaterWheelModel();
    }
    
    @Override
    public void doRender(Entity entity, double x, double y, double z,
                         float fYaw, float renderPartialTicks ) // the last parameter comes from the client's timer
    {
    	// do not render unless the chunks around are loaded
    	
    	int i = MathHelper.floor_double( entity.posX );
    	int j = MathHelper.floor_double( entity.posY );
    	int k = MathHelper.floor_double( entity.posZ );
    	
    	if ( !entity.worldObj.checkChunksExist( i - 16, j - 16, k - 16, i + 16, j + 16, k + 16 ) )
    	{
    		return;
    	}
    	
    	WaterWheelEntity entityWaterWheel = (WaterWheelEntity)entity;
    	
        GL11.glPushMatrix();
        GL11.glTranslatef( (float)x, (float)y, (float)z );
        
        loadTexture("/btwmodtex/fcwaterwheelent.png");
        GL11.glScalef( 1.0F, 1.0F, 1.0F );//(-1F, -1F, 1.0F);
        
        // rock the wheel for damage
        float deltaTime = (float)entityWaterWheel.timeSinceHit - renderPartialTicks;
        float deltaDamage = (float)entityWaterWheel.currentDamage - renderPartialTicks;
        float rotateForDamage = 0.0F;
        
        if ( deltaDamage < 0.0F)
        {
            deltaDamage = 0.0F;
        }
        
        if ( deltaDamage > 0.0F )
        {
        	rotateForDamage = ( (MathHelper.sin( deltaTime ) * deltaTime * deltaDamage) / 40F ) * 
    			(float)entityWaterWheel.rockDirection;
        }
        
        if ( entityWaterWheel.alignedToX)
    	{
            GL11.glRotatef(entityWaterWheel.rotation, 1.0F, 0.0F, 0.0F);
            
            GL11.glRotatef( rotateForDamage, 0.0F, 0.0F, 1.0F);
            
            GL11.glRotatef( 90F, 0.0F, 1.0F, 0.0F );            
    	}
        else
        {
            GL11.glRotatef(entityWaterWheel.rotation, 0.0F, 0.0F, 1.0F);
            
            GL11.glRotatef( rotateForDamage, 1.0F, 0.0F, 0.0F);
        }    	
        
        modelWaterWheel.render( entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        
        GL11.glPopMatrix();
    }    
}