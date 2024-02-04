// FCMOD

package btw.client.render.entity;

import btw.block.BTWBlocks;
import btw.block.blocks.AnchorBlock;
import btw.block.blocks.RopeBlock;
import btw.client.render.util.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class MovingAnchorRenderer extends Render
{
    private RenderBlocks localRenderBlocks;
    
    public MovingAnchorRenderer()
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
        
        Block block = BTWBlocks.anchor;
  
        // render the base
   
        double fHalfLength = 0.50F;
        double fHalfWidth = 0.50F;
        double dBlockHeight = AnchorBlock.anchorBaseHeight;
        
        localRenderBlocks.setRenderBounds( 0.5F - fHalfWidth, 0, 0.5F - fHalfLength, 
    		0.5F + fHalfWidth, dBlockHeight, 0.5F + fHalfLength );
        
        RenderUtils.renderMovingBlockWithMetadata(localRenderBlocks, block,
                                                  world, i, j, k, 1);
  
        // render the loop
        
        fHalfLength = 0.125F;
        fHalfWidth = 0.125F;
        dBlockHeight = 0.25F;
        
        localRenderBlocks.setRenderBounds( 0.5F - fHalfWidth, AnchorBlock.anchorBaseHeight, 0.5F - fHalfLength,
    		0.5F + fHalfWidth, AnchorBlock.anchorBaseHeight + dBlockHeight, 0.5F + fHalfLength);
        
        RenderUtils.renderMovingBlockWithTexture(localRenderBlocks, block,
                                                 world, i, j, k,
                                                 ((AnchorBlock) BTWBlocks.anchor).iconNub);  // Metal texture for the loop)
        
        // render the rope

        if ( world.getBlockId( i, j, k ) != BTWBlocks.ropeBlock.blockID )
        {
            
	        fHalfLength = RopeBlock.ROPE_WIDTH * 0.499F;
	        fHalfWidth = RopeBlock.ROPE_WIDTH * 0.499F;
	        
	        double yOffset = 1.0D - ( entity.posY - (double)j );
	        
	        //fBlockHeight = 1.0f;
	        
	        localRenderBlocks.setRenderBounds( 0.5F - fHalfWidth, AnchorBlock.anchorBaseHeight, 0.5F - fHalfLength,
	    		0.5F + fHalfWidth, (float)( yOffset + 1.0D )/*1.99F*/, 0.5F + fHalfLength );
	        
	        RenderUtils.renderMovingBlockWithTexture(localRenderBlocks, block,
                                                     world, i, j, k,
                                                     ((RopeBlock) BTWBlocks.ropeBlock).blockIcon);
        }
        
        GL11.glEnable(2896 /*GL_LIGHTING*/);
        
        GL11.glPopMatrix();
    }
}