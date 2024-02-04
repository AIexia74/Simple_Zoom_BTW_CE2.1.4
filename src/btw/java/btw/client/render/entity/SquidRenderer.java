// FCMOD

package btw.client.render.entity;

import btw.entity.model.SquidModel;
import btw.entity.model.SquidTentacleModel;
import btw.entity.mob.SquidEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@Environment(EnvType.CLIENT)
public class SquidRenderer extends RenderLiving
{
    SquidTentacleModel tentacleAttackModel = new SquidTentacleModel();
    
    public SquidRenderer()
    {
        super( new SquidModel(), 0.7F ); // second param is shadow size
    }
    
    @Override
    protected float handleRotationFloat(EntityLiving entity, float par2 )
    {
    	SquidEntity squid = (SquidEntity)entity;
    	
        return squid.prevTentacleAngle + (squid.tentacleAngle - squid.prevTentacleAngle) * par2;
    }
    
    @Override
    protected void rotateCorpse(EntityLiving entity, float par2, float par3, float par4)
    {
    	SquidEntity squid = (SquidEntity)entity;
    	
        float var5 = squid.prevSquidPitch + (squid.squidPitch - squid.prevSquidPitch) * par4;
        float var6 = squid.prevSquidYaw + (squid.squidYaw - squid.prevSquidYaw) * par4;
        
        GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        GL11.glRotatef(180.0F - par3, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(var5, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(var6, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, -1.2F, 0.0F);
    }

    @Override
    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        super.doRenderLiving(par1EntityLiving, par2, par4, par6, par8, par9);
    }
    
    @Override
    public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9)
    {
        super.doRenderLiving( (EntityLiving)entity, par2, par4, par6, par8, par9);;
    	
        renderTentacleAttack((SquidEntity)entity, par2, par4, par6, par8, par9);
    }
    
    //------------- Class Specific Methods ------------//
    
    public void renderTentacleAttack(SquidEntity squid, double dRenderX, double dRenderY, double dRenderZ, float par8, float dPartialTick)
    {
    	int iAttackProgressCounter = squid.tentacleAttackInProgressCounter;
    	
    	if ( iAttackProgressCounter > 0 )
    	{
    		float fPartialAttackProgress = (float)( iAttackProgressCounter - 1 ) + dPartialTick;
    		
    		Vec3 worldTipPos = squid.computeTentacleAttackTip(fPartialAttackProgress);
    		
    		if ( squid.isHeadCrab() )
    		{
    			dRenderY -= squid.height * 2F / 3F;
    		}
    		
    		double dLocalSourcePosX = dRenderX;
    		double dLocalSourcePosY = dRenderY;    		
    		double dLocalSourcePosZ = dRenderZ;
    		
    		if ( !squid.isHeadCrab() )
    		{
    			dLocalSourcePosY += squid.height / 2F;
    		}
    		
    		double dLocalTipPosX = ( worldTipPos.xCoord - squid.posX ) + dRenderX;
    		double dLocalTipPosY = ( worldTipPos.yCoord - squid.posY ) + dRenderY;
    		double dLocalTipPosZ = ( worldTipPos.zCoord - squid.posZ ) + dRenderZ;
    		
    		double dDeltaX = dLocalTipPosX - dLocalSourcePosX;
    		double dDeltaY = dLocalTipPosY - dLocalSourcePosY;
    		double dDeltaZ = dLocalTipPosZ - dLocalSourcePosZ;
    		
            double dTentacleLength = MathHelper.sqrt_double( dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ );
            double dFlatTentacleLength = MathHelper.sqrt_double( dDeltaX * dDeltaX + dDeltaZ * dDeltaZ );
            
            Tessellator tesslator = Tessellator.instance;
            
            // line test for debugging
            /*
            {
	            GL11.glDisable(GL11.GL_TEXTURE_2D);
	            GL11.glDisable(GL11.GL_LIGHTING);
	            tesslator.startDrawing(3);
				if ( iAttackProgressCounter <= ( squid.m_iTentacleAttackDuration >> 1 ) )
				{
		            tesslator.setColorOpaque_I( 0xFF0000 );
				}
				else
				{
		            tesslator.setColorOpaque_I(0);
				}
	
	            tesslator.addVertex( dLocalSourcePosX, dLocalSourcePosY, dLocalSourcePosZ );
	            tesslator.addVertex( dLocalTipPosX, dLocalTipPosY, dLocalTipPosZ );
	            
	            tesslator.draw();
	            GL11.glEnable(GL11.GL_LIGHTING);
	            GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
            */
            
            // model render
            
            GL11.glPushMatrix();
            
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glTranslatef( (float)dRenderX, (float)dRenderY, (float)dRenderZ );
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            
            loadTexture("/mob/squid.png");

            float fTentacleYaw = (float)( Math.atan2( dDeltaX, dDeltaZ) * 180D / Math.PI );
            float fTentaclePitch = (float)( Math.atan2( dFlatTentacleLength, dDeltaY) * 180D / Math.PI );            
            
            float fScaleTentacleWidth = (float)( 1.0D - ( 0.6D * squid.getAttackProgressSin(fPartialAttackProgress) ) );
            
            if ( fScaleTentacleWidth > 1F )
            {
            	fScaleTentacleWidth = 1F;
            }
            
            float fScaleTentacleLength = (float)dTentacleLength;
            
            tentacleAttackModel.render(squid, fScaleTentacleWidth, fScaleTentacleLength, fScaleTentacleWidth, fTentacleYaw, fTentaclePitch, 0.0625F);
            
            GL11.glPopMatrix();
    	}
    }
}
