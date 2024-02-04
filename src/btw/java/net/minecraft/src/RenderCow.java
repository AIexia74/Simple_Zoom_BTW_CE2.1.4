package net.minecraft.src;

import btw.entity.model.CowUdderModel;
import btw.entity.mob.CowEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class RenderCow extends RenderLiving
{
    public RenderCow(ModelBase par1ModelBase, float par2)
    {
        super(par1ModelBase, par2);
        // FCMOD: Added (client only)        
        modelUdder = new CowUdderModel();
        
        setRenderPassModel(modelUdder);
        // END FCMOD
    }

    public void renderCow(EntityCow par1EntityCow, double par2, double par4, double par6, float par8, float par9)
    {
        super.doRenderLiving(par1EntityCow, par2, par4, par6, par8, par9);
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderCow((EntityCow)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderCow((EntityCow)par1Entity, par2, par4, par6, par8, par9);
        // FCMOD: Added (client only)
        //RenderKickAttackDebug( (FCEntityCow)par1Entity, par2, par4, par6, par8, par9 );
        // END FCMOD
    }
    
    // FCMOD: Added (client only)
    CowUdderModel modelUdder;
    
    protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
    {
    	if ( par2 == 0 && ((CowEntity)par1EntityLiving).gotMilk() )
    	{
    		loadTexture( "/btwmodtex/cow_udder.png" );
    		
    		return 1;
    	}
    	
        return -1;
    }
    
    public void renderKickAttackDebug(CowEntity cow, double dRenderX, double dRenderY, double dRenderZ, float par8, float dPartialTick)
    {
    		
		Vec3 worldTipPos = cow.computeKickAttackCenter();
		
		double dLocalSourcePosX = dRenderX;
		double dLocalSourcePosY = dRenderY + ( cow.height / 2F );
		double dLocalSourcePosZ = dRenderZ;
		
		double dLocalTipPosX = ( worldTipPos.xCoord - cow.posX ) + dRenderX;
		double dLocalTipPosY = ( worldTipPos.yCoord - cow.posY ) + dRenderY;
		double dLocalTipPosZ = ( worldTipPos.zCoord - cow.posZ ) + dRenderZ;
		
        Tessellator tesslator = Tessellator.instance;
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        
        tesslator.startDrawing(3);
        
		if (cow.kickAttackInProgressCounter >= 0 )
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
        
        tesslator.startDrawing(3);
        
		if (cow.kickAttackInProgressCounter >= 0 )
		{
            tesslator.setColorOpaque_I( 0xFFFF00 );
		}
		else
		{
            tesslator.setColorOpaque_I( 0xFFFFFF );
		}

        tesslator.addVertex( dLocalTipPosX + cow.KICK_ATTACK_TIP_COLLISION_HALF_WIDTH, dLocalTipPosY, dLocalTipPosZ);
        tesslator.addVertex( dLocalTipPosX - cow.KICK_ATTACK_TIP_COLLISION_HALF_WIDTH, dLocalTipPosY, dLocalTipPosZ);
        
        tesslator.draw();
        
        tesslator.startDrawing(3);
        
		if (cow.kickAttackInProgressCounter >= 0 )
		{
            tesslator.setColorOpaque_I( 0xFFFF00 );
		}
		else
		{
            tesslator.setColorOpaque_I( 0xFFFFFF );
		}

        tesslator.addVertex( dLocalTipPosX, dLocalTipPosY, dLocalTipPosZ + cow.KICK_ATTACK_TIP_COLLISION_HALF_WIDTH);
        tesslator.addVertex( dLocalTipPosX, dLocalTipPosY, dLocalTipPosZ - cow.KICK_ATTACK_TIP_COLLISION_HALF_WIDTH);
        
        tesslator.draw();
        
        tesslator.startDrawing(3);
        
		if (cow.kickAttackInProgressCounter >= 0 )
		{
            tesslator.setColorOpaque_I( 0xFFFF00 );
		}
		else
		{
            tesslator.setColorOpaque_I( 0xFFFFFF );
		}

        tesslator.addVertex(dLocalTipPosX, dLocalTipPosY + cow.KICK_ATTACK_TIP_COLLISION_HALF_HEIGHT, dLocalTipPosZ);
        tesslator.addVertex(dLocalTipPosX, dLocalTipPosY - cow.KICK_ATTACK_TIP_COLLISION_HALF_HEIGHT, dLocalTipPosZ);
        
        tesslator.draw();
        
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        
    }
    // END FCMOD
}
