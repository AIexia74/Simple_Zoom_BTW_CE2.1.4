// FCMOD (client only)

package btw.client.render.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@Environment(EnvType.CLIENT)
public class RenderUtils
{
    static public void renderStandardBlockWithTexture(RenderBlocks renderBlocks, Block block, int i, int j, int k, Icon texture)
    {
        boolean bHasOverride = renderBlocks.hasOverrideBlockTexture();
        
        if ( !bHasOverride )
        {
        	renderBlocks.setOverrideBlockTexture( texture );
        }
        
        renderBlocks.renderStandardBlock( block, i, j, k );
        
        if ( !bHasOverride )
        {
        	renderBlocks.clearOverrideBlockTexture();
        }
    }
    
    static public void renderBlockWithBoundsAndTextureRotation(RenderBlocks renderBlocks, Block block, int i, int j, int k, int iFacing,
                                                               float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
    	setRenderBoundsToBlockFacing(renderBlocks, iFacing, minX, minY, minZ, maxX, maxY, maxZ);
    		
    	RenderUtils.renderBlockWithTextureRotation(renderBlocks, block, i, j, k, iFacing);
    }
    
    static public void setRenderBoundsWithAxisAlignment(RenderBlocks renderBlocks,
                                                        float fMinMinor, float fMinY, float fMinMajor, float fMaxMinor, float fMaxY, float fMaxMajor, boolean bIAligned)
    {
    	if ( !bIAligned )
    	{
    		renderBlocks.setRenderBounds( fMinMinor, fMinY, fMinMajor, fMaxMinor, fMaxY, fMaxMajor );
    	}
    	else
    	{
    		renderBlocks.setRenderBounds( fMinMajor, fMinY, fMinMinor, fMaxMajor, fMaxY, fMaxMinor );
    	}
    }
    
    static public void setRenderBoundsToBlockFacing(RenderBlocks renderBlocks, int iBlockFacing, float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
    	float newMinX = minX;
    	float newMinY = minY;
    	float newMinZ = minZ;

    	float newMaxX = maxX;
    	float newMaxY = maxY;
    	float newMaxZ = maxZ;
    	
    	switch ( iBlockFacing )
    	{
    		case 0:
    			
    			newMinY = 1.0F - maxY;
    			newMaxY = 1.0F - minY;
    			
    			break;
    			
    		case 2:
    			
    			newMinZ = 1.0F - maxY;
    			newMaxZ = 1.0F - minY;
    			
    			newMinY = minZ;
    			newMaxY = maxZ;
    			
    			break;
    			
    		case 3:
    			
    			newMinZ = minY;
    			newMaxZ = maxY;
    			
    			newMinY = 1.0F - maxZ;
    			newMaxY = 1.0F - minZ;
    			
    			break;
    			
    		case 4:
    			
    			newMinX = 1.0F - maxY;
    			newMaxX = 1.0F - minY;
    			
    			newMinY = minX;
    			newMaxY = maxX;
    			
    			break;
    			
    		case 5:
    			
    			newMinX = minY;
    			newMaxX = maxY;
    			
    			newMinY = 1.0F - maxX;
    			newMaxY = 1.0F - minX;
    			
    			break;    			
    	}
    	
    	renderBlocks.setRenderBounds( newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ );        
    }
    
    static public void renderBlockWithTextureRotation(RenderBlocks renderBlocks, Block block, int i, int j, int k, int iBlockFacing)
    {
    	setTextureRotationBasedOnBlockFacing(renderBlocks, iBlockFacing);
    	
    	renderBlocks.renderStandardBlock( block, i, j, k );
    	
		renderBlocks.clearUVRotation();
    }
    
    static public void setTextureRotationBasedOnBlockFacing(RenderBlocks renderBlocks, int iBlockFacing)
    {
    	switch ( iBlockFacing )
    	{
		    case 0:
		    	
		    	renderBlocks.setUVRotateEast(3);
		    	renderBlocks.setUVRotateWest(3);
		    	renderBlocks.setUVRotateSouth(3);
		    	renderBlocks.setUVRotateNorth(3);
		    	
		        break;
		
		    case 2:
		    	
		    	renderBlocks.setUVRotateSouth(1);
		    	renderBlocks.setUVRotateNorth(2);
		    	
		    	renderBlocks.setUVRotateEast(3); // top
		    	renderBlocks.setUVRotateWest(3); // bottom
		    	
		        break;
		
		    case 3:
		    	
		    	renderBlocks.setUVRotateSouth(2);
		    	renderBlocks.setUVRotateNorth(1);
		    	renderBlocks.setUVRotateTop(3);
		    	renderBlocks.setUVRotateBottom(3);
		    	
		        break;
		
		    case 4:
		    	
		    	renderBlocks.setUVRotateEast(1);
		    	renderBlocks.setUVRotateWest(2);
		    	renderBlocks.setUVRotateTop(2);
		    	renderBlocks.setUVRotateBottom(1);
		    	
		    	renderBlocks.setUVRotateNorth(2); // top
		    	renderBlocks.setUVRotateSouth(1); // bottom
		    	
		        break;
		
		    case 5:
		    	
		    	renderBlocks.setUVRotateEast(2);
		    	renderBlocks.setUVRotateWest(1);
		    	renderBlocks.setUVRotateTop(1);
		    	renderBlocks.setUVRotateBottom(2);
		    	
		    	renderBlocks.setUVRotateSouth(1); // top
		    	renderBlocks.setUVRotateNorth(2); // bottom
		    	
		        break;
		}		
    }
    
    static public void renderBlockInteriorWithBoundsAndTextureRotation(RenderBlocks renderBlocks, Block block, int i, int j, int k, int iFacing,
                                                                       float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
    	setRenderBoundsToBlockFacing(renderBlocks, iFacing, minX, minY, minZ, maxX, maxY, maxZ);
    		
    	setTextureRotationBasedOnBlockFacing(renderBlocks, iFacing);
    	
    	RenderUtils.renderBlockWithTextureRotation(renderBlocks, block, i, j, k, iFacing);
    	
		renderBlocks.clearUVRotation();
    }
    
    static public void renderMovingBlockWithTexture(RenderBlocks renderBlocks, Block block, World world, int i, int j, int k, Icon texture)
    {
        float f = 0.5F;
        float f1 = 1.0F;
        float f2 = 0.8F;
        float f3 = 0.6F;
        
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness( block.getMixedBrightnessForBlock( world, i, j, k ) );
        
        float f4 = 1.0F;
        float f5 = 1.0F;
        
        if(f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f * f5, f * f5, f * f5);
        renderBlocks.renderFaceYNeg(block, -0.5D, -0.5D, -0.5D, texture );
        f5 = 1.0F;
        if(f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f1 * f5, f1 * f5, f1 * f5);
        renderBlocks.renderFaceYPos(block, -0.5D, -0.5D, -0.5D, texture );
        f5 = 1.0F;
        if(f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
        renderBlocks.renderFaceZNeg(block, -0.5D, -0.5D, -0.5D, texture );
        f5 = 1.0F;
        if(f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
        renderBlocks.renderFaceZPos(block, -0.5D, -0.5D, -0.5D, texture );
        f5 = 1.0F;
        if(f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
        renderBlocks.renderFaceXNeg(block, -0.5D, -0.5D, -0.5D, texture );
        f5 = 1.0F;
        if(f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
        renderBlocks.renderFaceXPos(block, -0.5D, -0.5D, -0.5D, texture );
        tessellator.draw();
    }  
    
    static public void renderMovingBlock(RenderBlocks renderBlocks, Block block, World world, int i, int j, int k)
    {
    	renderMovingBlockWithMetadata(renderBlocks, block, world, i, j, k, 0);
    }
    
    static public void renderMovingBlockWithMetadata(RenderBlocks renderBlocks, Block block, IBlockAccess blockAccess, int i, int j, int k, int iMetadata)
    {
        float f = 0.5F;
        float f1 = 1.0F;
        float f2 = 0.8F;
        float f3 = 0.6F;
        
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness( block.getMixedBrightnessForBlock( blockAccess, i, j, k ) );
        
        float f4 = 1.0F;
        float f5 = 1.0F;
        if(f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f * f5, f * f5, f * f5);
        renderBlocks.renderFaceYNeg(block, -0.5D, -0.5D, -0.5D, block.getIcon( 0, iMetadata ) );
        f5 = 1.0F;
        if(f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f1 * f5, f1 * f5, f1 * f5);
        renderBlocks.renderFaceYPos(block, -0.5D, -0.5D, -0.5D, block.getIcon( 1, iMetadata ) );
        f5 = 1.0F;
        if(f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
        renderBlocks.renderFaceZNeg(block, -0.5D, -0.5D, -0.5D, block.getIcon( 2, iMetadata ) );
        f5 = 1.0F;
        if(f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
        renderBlocks.renderFaceZPos(block, -0.5D, -0.5D, -0.5D, block.getIcon( 3, iMetadata ) );
        f5 = 1.0F;
        if(f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
        renderBlocks.renderFaceXNeg(block, -0.5D, -0.5D, -0.5D, block.getIcon( 4, iMetadata ) );
        f5 = 1.0F;
        if(f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
        renderBlocks.renderFaceXPos(block, -0.5D, -0.5D, -0.5D, block.getIcon( 5, iMetadata ) );
        tessellator.draw();
    }
    
    static public void renderInvBlockWithTexture(RenderBlocks renderBlocks, Block block, float fXOffset, float fYOffset, float fZOffset, Icon texture)
    {
        Tessellator tessellator = Tessellator.instance;

        GL11.glTranslatef( fXOffset, fYOffset, fZOffset );
        
        tessellator.startDrawingQuads();
        
        tessellator.setNormal(0.0F, -1F, 0.0F);
        renderBlocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, texture );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderBlocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, texture );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderBlocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, texture );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderBlocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, texture );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderBlocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, texture );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, texture );
        tessellator.draw();
        
        GL11.glTranslatef( -fXOffset, -fYOffset, -fZOffset );
    }
    
    static public void renderInvBlockWithMetadata(RenderBlocks renderBlocks, Block block, float fXOffset, float fYOffset, float fZOffset, int iMetaData)
    {
        Tessellator tessellator = Tessellator.instance;

        GL11.glTranslatef( fXOffset, fYOffset, fZOffset );
        
        tessellator.startDrawingQuads();
        
        tessellator.setNormal(0.0F, -1F, 0.0F);
        renderBlocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon( 0, iMetaData ) );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderBlocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon( 1, iMetaData ) );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderBlocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon( 2, iMetaData ) );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderBlocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon( 3, iMetaData ) );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderBlocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon( 4, iMetaData ) );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon( 5, iMetaData ) );
        tessellator.draw();
        
        GL11.glTranslatef( -fXOffset, -fYOffset, -fZOffset );
    }
    
    static public boolean renderBlockFullBrightWithTexture
    ( 
		RenderBlocks renderBlocks, 
		IBlockAccess blockAccess, 
		int i, int j, int k, 
		Icon texture
	)
    {
        Tessellator tessellator = Tessellator.instance;
        
        // render bottom
        
    	tessellator.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
    	tessellator.setBrightness( blockAccess.getLightBrightnessForSkyBlocks( i, j, k, 15 ) );
        renderBlocks.renderFaceYNeg(null, i, j, k, texture );
        
        // render top
        
    	tessellator.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
    	tessellator.setBrightness( blockAccess.getLightBrightnessForSkyBlocks( i, j, k, 15 ) );
        renderBlocks.renderFaceYPos(null, i, j, k, texture );
        
        // render east
        
    	tessellator.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
    	tessellator.setBrightness( blockAccess.getLightBrightnessForSkyBlocks( i, j, k, 15 ) );
        renderBlocks.renderFaceZNeg(null, i, j, k, texture );
        
        // render west
        
    	tessellator.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
    	tessellator.setBrightness( blockAccess.getLightBrightnessForSkyBlocks( i, j, k, 15 ) );            
        renderBlocks.renderFaceZPos(null, i, j, k, texture );
        
        // render north
        
    	tessellator.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
    	tessellator.setBrightness( blockAccess.getLightBrightnessForSkyBlocks( i, j, k, 15 ) );
        renderBlocks.renderFaceXNeg(null, i, j, k, texture );
        
        // render south
        
    	tessellator.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
    	tessellator.setBrightness( blockAccess.getLightBrightnessForSkyBlocks( i, j, k, 15 ) );
        renderBlocks.renderFaceXPos(null, i, j, k, texture );

    	return true;
    }
    
    static public void renderInvBlockFullBrightWithTexture(RenderBlocks renderBlocks, Block block, float fXOffset, float fYOffset, float fZOffset, Icon texture)
    {
    	IBlockAccess blockAccess = renderBlocks.blockAccess;
        Tessellator tessellator = Tessellator.instance;

        GL11.glTranslatef( fXOffset, fYOffset, fZOffset );
        
        tessellator.startDrawingQuads();        
        tessellator.setNormal(0.0F, -1F, 0.0F);
    	tessellator.setColorOpaque_F( 1F, 1F, 1F );
    	tessellator.setBrightness( 240 );
        renderBlocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, texture );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
    	tessellator.setColorOpaque_F( 1F, 1F, 1F );
    	tessellator.setBrightness( 240 );
        renderBlocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, texture );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
    	tessellator.setColorOpaque_F( 1F, 1F, 1F );
    	tessellator.setBrightness( 240 );
        renderBlocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, texture );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
    	tessellator.setColorOpaque_F( 1F, 1F, 1F );
    	tessellator.setBrightness( 240 );
        renderBlocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, texture );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
    	tessellator.setColorOpaque_F( 1F, 1F, 1F );
    	tessellator.setBrightness( 240 );
        renderBlocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, texture );
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
    	tessellator.setColorOpaque_F( 1F, 1F, 1F );
    	tessellator.setBrightness( 240 );
        renderBlocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, texture );
        tessellator.draw();
        
        GL11.glTranslatef( -fXOffset, -fYOffset, -fZOffset );
    }
    
    static public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int i, int j, int k, int iNeighborSide)
    {
        Block block = Block.blocksList[blockAccess.getBlockId( i, j, k )];
        
        if ( block != null )
        {
        	return block.shouldRenderNeighborFullFaceSide(blockAccess, i, j, k, iNeighborSide);
        }
        
        return true;
    }
    
    static public boolean shouldRenderNeighborHalfSlabSide(IBlockAccess blockAccess, int i, int j, int k, int iNeighborSlabSide, boolean bNeighborUpsideDown)
    {
        Block block = Block.blocksList[blockAccess.getBlockId( i, j, k )];
        
        if ( block != null )
        {
        	return block.shouldRenderNeighborHalfSlabSide(blockAccess, i, j, k, iNeighborSlabSide, bNeighborUpsideDown);
        }
        
        return true;
    }
    
    public static void drawSecondaryCraftingOutputIndicator(Minecraft mc, int iDisplayX, int iDisplayY)
    {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        
        String sIndicator = "+";    			
		
        // black (0) outline of text
        
        mc.fontRenderer.drawString( sIndicator, iDisplayX + 1, iDisplayY, 0 );            
        mc.fontRenderer.drawString( sIndicator, iDisplayX - 1, iDisplayY, 0 );
        
        mc.fontRenderer.drawString( sIndicator, iDisplayX, iDisplayY + 1, 0 );
        mc.fontRenderer.drawString( sIndicator, iDisplayX, iDisplayY - 1, 0 );
        
        // text itself in the same color as vanilla xp display (8453920)
        
        mc.fontRenderer.drawString( sIndicator, iDisplayX, iDisplayY, 8453920 );            
        
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}
