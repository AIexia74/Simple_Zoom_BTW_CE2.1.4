// FCMOD

package btw.client.render.entity;

import java.util.List;

import btw.block.BTWBlocks;
import btw.client.render.util.RenderUtils;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class MovingPlatformRenderer extends Render
{
    private RenderBlocks localRenderBlocks;
    
    public MovingPlatformRenderer()
    {
    	localRenderBlocks = new RenderBlocks();
        shadowSize = 0.0F;
    }
    
    @Override
    public void doRender(Entity entity, double x, double y, double z,
                         float fYaw, float renderPartialTicks ) // the last parameter comes from the client's timer
    {
        World world = entity.worldObj;
        
    	localRenderBlocks.blockAccess = world;
    	
        GL11.glPushMatrix();
        
        GL11.glTranslatef( (float)x, (float)y, (float)z );
        
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        
        int i = MathHelper.floor_double( entity.posX );
        int j = MathHelper.floor_double( entity.posY );
        int k = MathHelper.floor_double( entity.posZ );
        
        this.loadTexture("/terrain.png");
        
        Block block = BTWBlocks.platform;
  
    	// render sides
        
        List list = entity.worldObj.getEntitiesWithinAABB( MovingPlatformEntity.class,
        		AxisAlignedBB.getAABBPool().getAABB(
				entity.posX - 1.0F, entity.posY - 0.10F, entity.posZ - 0.10F,
				entity.posX - 0.9F, entity.posY + 0.10F, entity.posZ + 0.10F ) );
				
        if ( list == null || list.size() <= 0 )
        {        	
	        localRenderBlocks.setRenderBounds( 0.0001F, ( 1F / 16F ), 0.0001F, 
	        		( 1F / 16F ), 1F - ( 1F / 16F ), 0.9999F );
	        
	        RenderUtils.renderMovingBlock(localRenderBlocks, block,
                                          world, i, j, k);
        }
    
        list = entity.worldObj.getEntitiesWithinAABB( MovingPlatformEntity.class,
        		AxisAlignedBB.getAABBPool().getAABB(
				entity.posX - 0.10F, entity.posY - 0.10F, entity.posZ + 0.9F,
				entity.posX + 0.10F, entity.posY + 0.10F, entity.posZ + 1.00F ) );
				
        if ( list == null || list.size() <= 0 )
        {        	
	        localRenderBlocks.setRenderBounds( 0.0F, ( 1F / 16F ), 1F - ( 1F / 16F ), 
	        		1F, 1F - ( 1F / 16F ), 1F );
	        
	        RenderUtils.renderMovingBlock(localRenderBlocks, block,
                                          world, i, j, k);
        }
    
        list = entity.worldObj.getEntitiesWithinAABB( MovingPlatformEntity.class,
        		AxisAlignedBB.getAABBPool().getAABB(
				entity.posX + 0.9F, entity.posY - 0.10F, entity.posZ - 0.10F,
				entity.posX + 1.0F, entity.posY + 0.10F, entity.posZ + 0.10F ) );
				
        if ( list == null || list.size() <= 0 )
        {        	
	        localRenderBlocks.setRenderBounds( 1F - ( 1F / 16F ), ( 1F / 16F ), 0.0001F, 
		    		0.9999F, 1F - ( 1F / 16F ), 0.9999F );
	        
	        RenderUtils.renderMovingBlock(localRenderBlocks, block,
                                          world, i, j, k);
        }
    
        list = entity.worldObj.getEntitiesWithinAABB( MovingPlatformEntity.class,
        		AxisAlignedBB.getAABBPool().getAABB(
				entity.posX - 0.10F, entity.posY - 0.10F, entity.posZ - 1.0F,
				entity.posX + 0.10F, entity.posY + 0.10F, entity.posZ - 0.9F ) );
				
        if ( list == null || list.size() <= 0 )
        {        	
	        localRenderBlocks.setRenderBounds( 0, ( 1F / 16F ), 0F, 
		    		1.0F, 1F - ( 1F / 16F ), ( 1F / 16F ) );
	        
	        RenderUtils.renderMovingBlock(localRenderBlocks, block,
                                          world, i, j, k);
        }
        
        // render bottom
        
        localRenderBlocks.setRenderBounds( 0, 0, 0, 
	    		1, ( 1F / 16F ), 1 );
        
        RenderUtils.renderMovingBlock(localRenderBlocks, block,
                                      world, i, j, k);
        
        // render top
        
        localRenderBlocks.setRenderBounds( 0, 1F - ( 1F / 16F ), 0, 
	    		1, 1, 1 );
        
        RenderUtils.renderMovingBlock(localRenderBlocks, block,
                                      world, i, j, k);
        
        GL11.glEnable(2896 /*GL_LIGHTING*/);
        
        GL11.glPopMatrix();
    }
}