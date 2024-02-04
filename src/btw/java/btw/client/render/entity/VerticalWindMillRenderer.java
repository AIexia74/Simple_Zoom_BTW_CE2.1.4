//FCMOD

package btw.client.render.entity;

import btw.entity.model.WindMillVerticalFrameModel;
import btw.entity.model.WindMillVerticalSailsModel;
import btw.entity.model.WindMillVerticalShaftsModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import btw.entity.mechanical.source.VerticalWindMillEntity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Render;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class VerticalWindMillRenderer extends Render
{
    protected WindMillVerticalShaftsModel modelShafts;
    protected WindMillVerticalSailsModel modelSails;
    protected WindMillVerticalFrameModel modelFrame;
    
    public VerticalWindMillRenderer()
    {
        shadowSize = 0.0F;

        modelShafts = new WindMillVerticalShaftsModel();
        modelSails = new WindMillVerticalSailsModel();
        modelFrame = new WindMillVerticalFrameModel();
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
    	
    	VerticalWindMillEntity entityWindMill = (VerticalWindMillEntity)entity;
    	
        // rock the wind mill for damage
        float deltaTime = (float)entityWindMill.timeSinceHit - renderPartialTicks;
        float deltaDamage = (float)entityWindMill.currentDamage - renderPartialTicks;
        float rotateForDamage = 0.0F;
        
        if ( deltaDamage < 0.0F)
        {
            deltaDamage = 0.0F;
        }
        
        if ( deltaDamage > 0.0F )
        {
        	rotateForDamage = ( (MathHelper.sin( deltaTime ) * deltaTime * deltaDamage) / 40F ) * 
    			(float)entityWindMill.rockDirection;
        }
        
        GL11.glPushMatrix();
        GL11.glTranslatef( (float)x, (float)y, (float)z );
        
        GL11.glScalef( 1.0F, 1.0F, 1.0F );//(-1F, -1F, 1.0F);
        
        GL11.glRotatef(entityWindMill.rotation, 0.0F, 1.0F, 0.0F);
        
        GL11.glRotatef( rotateForDamage, 0.0F, 1.0F, 0.0F);

        loadTexture("/btwmodtex/fcwindmillwoodver.png");
        
        modelShafts.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, entityWindMill);
        
        loadTexture("/btwmodtex/fcwindmillwoodhrz.png");
        
        modelFrame.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, entityWindMill);
        
        loadTexture("/btwmodtex/fcwindmillcloth.png");
        
        modelSails.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, entityWindMill);
        
        GL11.glPopMatrix();
    }    
}