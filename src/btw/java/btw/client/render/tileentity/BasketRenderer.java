// FCMOD (client only)

package btw.client.render.tileentity;

import btw.block.blocks.BasketBlock;
import btw.block.model.BlockModel;
import btw.block.tileentity.BasketTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class BasketRenderer extends TileEntitySpecialRenderer
{
    protected RenderBlocks localRenderBlocks = new RenderBlocks();

    public void renderTileEntityAt(TileEntity tileEntity, double xCoord, double yCoord, double zCoord, float fPartialTickCount )
    {
    	BasketTileEntity basket = (BasketTileEntity)tileEntity;
    	
    	Block block = Block.blocksList[basket.worldObj.getBlockId(
    		basket.xCoord, basket.yCoord, basket.zCoord )];
    	
    	// it looks like it's possible for the block and tile entity to get out of sync during
    	// rendering, hence the instanceof test
    	
    	if ( block instanceof BasketBlock)
    	{
    		renderBasketLidAsBlock(basket, (BasketBlock)block, xCoord, yCoord, zCoord,
								   fPartialTickCount);
    	}
    }
    
	//------------- Class Specific Methods ------------//
	
    private void renderBasketLidAsBlock(BasketTileEntity tileEntity, BasketBlock block,
										double xCoord, double yCoord, double zCoord, float fPartialTickCount)
    {
    	int iMetadata = tileEntity.worldObj.getBlockMetadata( tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord );
    	
    	// it looks like it's possible for the block and tile entity to get out of sync during
    	// rendering, hene the instanceof test
    	
    	if ( block.getIsOpen(iMetadata) )
    	{
	        int iFacing = block.getFacing(iMetadata);
	
	        GL11.glPushMatrix();	
	        GL11.glTranslatef( (float)xCoord, (float)yCoord, (float)zCoord );	        
            GL11.glDisable( GL11.GL_LIGHTING );
	        
	        bindTextureByName( "/terrain.png" );

			localRenderBlocks.blockAccess = tileEntity.worldObj;
            
            Tessellator.instance.startDrawingQuads();
            
            Tessellator.instance.setTranslation( -(double)tileEntity.xCoord, -(double)tileEntity.yCoord, -(double)tileEntity.zCoord );
            
            BlockModel transformedModel = block.getLidModel(iMetadata).makeTemporaryCopy();
            
	    	// all rotation needs to be done through the block model so that the block renderer will know which face is
	    	// oriented towards which direction for lighting
	    	
			transformedModel.rotateAroundYToFacing(iFacing);
            
			localRenderBlocks.setUVRotateTop(block.convertFacingToTopTextureRotation(iFacing));
			localRenderBlocks.setUVRotateBottom(block.convertFacingToBottomTextureRotation(iFacing));
			
            block.openLidBrightness = block.getMixedBrightnessForBlock(
            		tileEntity.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
            
            block.renderingOpenLid = true;

			transformedModel.renderAsBlockWithColorMultiplier(localRenderBlocks,
															  block, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
	        
            block.renderingOpenLid = false;
            
	        Tessellator.instance.setTranslation( 0D, 0D, 0D );
	        
	        float fOpenRatio = getCurrentOpenRatio(tileEntity, fPartialTickCount);
	        float fLidAngle = fOpenRatio * 45F;
	        
	        Vec3 lidRotationPoint = Vec3.createVectorHelper( block.getLidRotationPoint());
	        
	        lidRotationPoint.rotateAsBlockPosAroundJToFacing(iFacing);
	        
	        GL11.glTranslatef( (float)lidRotationPoint.xCoord, (float)lidRotationPoint.yCoord, (float)lidRotationPoint.zCoord );
	        
	        if ( iFacing == 2 )
	        {
		        GL11.glRotatef( fLidAngle, 1F, 0F, 0F );
	        }
	        else if ( iFacing == 3 )
	        {
		        GL11.glRotatef( -fLidAngle, 1F, 0F, 0F );
	        }
	        else if ( iFacing == 4 )
	        {
		        GL11.glRotatef( -fLidAngle, 0F, 0F, 1F );
	        }
	        else if ( iFacing == 5 )
	        {
		        GL11.glRotatef( fLidAngle, 0F, 0F, 1F );
	        }
	        
	        GL11.glTranslatef( -(float)lidRotationPoint.xCoord, -(float)lidRotationPoint.yCoord, -(float)lidRotationPoint.zCoord );	        
	        
	        Tessellator.instance.draw();
    
	        localRenderBlocks.clearUVRotation();
			
            GL11.glEnable(GL11.GL_LIGHTING);
	        GL11.glPopMatrix();
    	}
    }
    
    protected float getCurrentOpenRatio(BasketTileEntity basket, float fPartialTickCount)
    {
        float fOpenRatio = basket.prevLidOpenRatio + (basket.lidOpenRatio - basket.prevLidOpenRatio) * fPartialTickCount;
        
        fOpenRatio = 1.0F - fOpenRatio;
        fOpenRatio = 1.0F - fOpenRatio * fOpenRatio * fOpenRatio;
        
        return fOpenRatio;        
    }
}
