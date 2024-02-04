// FCMOD

package btw.util;

import btw.block.util.RayTraceUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11; // client only

public class PrimitiveQuad extends PrimitiveGeometric
{
	private Vec3[] vertices = new Vec3[4];
	
	private float minUFrac = 0F;
	private float minVFrac = 0F;
	private float maxUFrac = 1F;
	private float maxVFrac = 1F;
	
	private int iconIndex = 0;
	
	public PrimitiveQuad(Vec3 vertex1, Vec3 vertex2, Vec3 vertex3, Vec3 vertex4 )
	{
		vertices[0] = vertex1;
		vertices[1] = vertex2;
		vertices[2] = vertex3;
		vertices[3] = vertex4;
	}

	@Override
	public void rotateAroundYToFacing(int iFacing)
	{
		vertices[0].rotateAsBlockPosAroundJToFacing(iFacing);
		vertices[1].rotateAsBlockPosAroundJToFacing(iFacing);
		vertices[2].rotateAsBlockPosAroundJToFacing(iFacing);
		vertices[3].rotateAsBlockPosAroundJToFacing(iFacing);
	}
	
	@Override
	public void tiltToFacingAlongY(int iFacing)
	{
		vertices[0].tiltAsBlockPosToFacingAlongJ(iFacing);
		vertices[1].tiltAsBlockPosToFacingAlongJ(iFacing);
		vertices[2].tiltAsBlockPosToFacingAlongJ(iFacing);
		vertices[3].tiltAsBlockPosToFacingAlongJ(iFacing);
	}
	
    @Override
	public void translate(double dDeltaX, double dDeltaY, double dDeltaZ)
    {
    	vertices[0].addVector(dDeltaX, dDeltaY, dDeltaZ);
    	vertices[1].addVector(dDeltaX, dDeltaY, dDeltaZ);
    	vertices[2].addVector(dDeltaX, dDeltaY, dDeltaZ);
    	vertices[3].addVector(dDeltaX, dDeltaY, dDeltaZ);
    }
    
	@Override
	public void addToRayTrace(RayTraceUtils rayTrace)
	{
    	rayTrace.addQuadWithLocalCoordsToIntersectionList(this, vertices[0]);
	}
	
	@Override
	public PrimitiveQuad makeTemporaryCopy()
	{
		PrimitiveQuad newQuad = new PrimitiveQuad(
			Vec3.createVectorHelper(vertices[0]),
			Vec3.createVectorHelper(vertices[1]),
			Vec3.createVectorHelper(vertices[2]),
			Vec3.createVectorHelper(vertices[3]) );
		
		newQuad.setUVFractions(minUFrac, minVFrac, maxUFrac, maxVFrac);
		
		newQuad.setIconIndex(iconIndex);
		
		return newQuad;
	}
	
    //------------- Class Specific Methods ------------//

	private static final double MIND_THE_GAP = 0.0001D;
	
	public boolean isPointOnPlaneWithinBounds(Vec3 point)
	{
		Vec3 minBounds = Vec3.createVectorHelper(vertices[0]);
		Vec3 maxBounds = Vec3.createVectorHelper(vertices[0]);
		
		computeBounds(minBounds, maxBounds);
		
		if ( 
			( maxBounds.xCoord - minBounds.xCoord < MIND_THE_GAP ||
			  ( point.xCoord >= minBounds.xCoord && point.xCoord <= maxBounds.xCoord )
			) &&
			( maxBounds.yCoord - minBounds.yCoord < MIND_THE_GAP ||
			  ( point.yCoord >= minBounds.yCoord && point.yCoord <= maxBounds.yCoord )
			) &&
			( maxBounds.zCoord - minBounds.zCoord < MIND_THE_GAP ||
			  ( point.zCoord >= minBounds.zCoord && point.zCoord <= maxBounds.zCoord )
			)
		)
		{
			return true;
		}
		
		return false;
	}
	
	/*
	 * Assumes min and max are set to point 0 when called
	 */
	public void computeBounds(Vec3 min, Vec3 max)
	{
		for ( int iTempCount = 1; iTempCount <= 3; iTempCount++ )
		{
			Vec3 tempPoint = vertices[iTempCount];
			
			if ( tempPoint.xCoord < min.xCoord )
			{
				min.xCoord = tempPoint.xCoord;
			}
			else if ( tempPoint.xCoord > max.xCoord )
			{
				max.xCoord = tempPoint.xCoord;
			}			
			
			if ( tempPoint.yCoord < min.yCoord )
			{
				min.yCoord = tempPoint.yCoord;
			}
			else if ( tempPoint.yCoord > max.yCoord )
			{
				max.yCoord = tempPoint.yCoord;
			}			
			
			if ( tempPoint.zCoord < min.zCoord )
			{
				min.zCoord = tempPoint.zCoord;
			}
			else if ( tempPoint.zCoord > max.zCoord )
			{
				max.zCoord = tempPoint.zCoord;
			}			
		}		
	}

	public Vec3 computeNormal()
	{
		Vec3 vec1 = vertices[0].subtractFrom(vertices[1]);
		Vec3 vec2 = vertices[0].subtractFrom(vertices[3]);
		
		return vec1.crossProduct( vec2 ); 
	}
	
	public PrimitiveQuad setUVFractions(float fMinUFrac, float fMinVFrac, float fMaxVFrac, float fMaxUFrac)
	{
		minUFrac = fMinUFrac;
		minVFrac = fMinVFrac;
		maxVFrac = fMaxVFrac;
		maxUFrac = fMaxUFrac;
		
		return this;
	}
	
	public PrimitiveQuad setIconIndex(int iIconIndex)
	{
		iconIndex = iIconIndex;
		
		return this;
	}
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private void addVertices(int i, int j, int k, Icon icon)
	{
    	double dUDelta = ( icon.getMaxU() - icon.getMinU() );
    	double dVDelta = ( icon.getMaxV() - icon.getMinV() );
    	
        double dMinU = icon.getMinU() + dUDelta * minUFrac;
        double dMinV = icon.getMinV() + dVDelta * minVFrac;
        double dMaxU = icon.getMinU() + dUDelta * maxUFrac;
        double dMaxV = icon.getMinV() + dVDelta * maxVFrac;

        Tessellator.instance.addVertexWithUV(i + vertices[0].xCoord, j + vertices[0].yCoord, k + vertices[0].zCoord, dMinU, dMinV);
        Tessellator.instance.addVertexWithUV(i + vertices[1].xCoord, j + vertices[1].yCoord, k + vertices[1].zCoord, dMinU, dMaxV);
        Tessellator.instance.addVertexWithUV(i + vertices[2].xCoord, j + vertices[2].yCoord, k + vertices[2].zCoord, dMaxU, dMaxV);
        Tessellator.instance.addVertexWithUV(i + vertices[3].xCoord, j + vertices[3].yCoord, k + vertices[3].zCoord, dMaxU, dMinV);
	}

    @Environment(EnvType.CLIENT)
    public boolean renderAsBlock(RenderBlocks renderBlocks, Block block, int i, int j, int k)
	{
        Icon icon = block.getIconByIndex(iconIndex);
        
        Tessellator.instance.setBrightness( block.getMixedBrightnessForBlock( renderBlocks.blockAccess, i, j, k ) );
        
        Tessellator.instance.setColorOpaque_F( 1F, 1F, 1F );
        
        addVertices(i, j, k, icon);
        
		return true;
	}

    @Environment(EnvType.CLIENT)
    public boolean renderAsBlockWithColorMultiplier(RenderBlocks renderBlocks, Block block, int i, int j, int k, float fRed, float fGreen, float fBlue)
	{
        Icon icon = block.getIconByIndex(iconIndex);
        
        Tessellator.instance.setBrightness( block.getMixedBrightnessForBlock( renderBlocks.blockAccess, i, j, k ) );
        
        Tessellator.instance.setColorOpaque_F( fRed, fGreen, fBlue );
        
        addVertices(i, j, k, icon);
        
		return true;
	}

    @Environment(EnvType.CLIENT)
    public boolean renderAsBlockWithTexture(RenderBlocks renderBlocks, Block block, int i, int j, int k, Icon icon)
	{
        Tessellator.instance.setBrightness( block.getMixedBrightnessForBlock( renderBlocks.blockAccess, i, j, k ) );
        
        Tessellator.instance.setColorOpaque_F( 1F, 1F, 1F );
        
        addVertices(i, j, k, icon);
        
		return true;
	}

    @Environment(EnvType.CLIENT)
    public boolean renderAsBlockFullBrightWithTexture(RenderBlocks renderBlocks, Block block, int i, int j, int k, Icon icon)
	{
		Tessellator.instance.setBrightness( renderBlocks.blockAccess.getLightBrightnessForSkyBlocks( i, j, k, 15 ) );
        
        Tessellator.instance.setColorOpaque_F( 1F, 1F, 1F );
        
        addVertices(i, j, k, icon);
        
		return true;
	}

    @Environment(EnvType.CLIENT)
    public void renderAsItemBlock(RenderBlocks renderBlocks, Block block, int iItemDamage)
	{
        Tessellator tessellator = Tessellator.instance;

        GL11.glTranslatef( -0.5F, -0.5F, -0.5F );
        
        tessellator.startDrawingQuads();
        
        Vec3 normal = computeNormal().normalize();
        
        //tessellator.setNormal( 0.0F, 1F, 0.0F ); // used to determine brightness as if oriented straight up.
        tessellator.setNormal( (float)normal.xCoord, (float)normal.yCoord, (float)normal.zCoord );
        
        Icon icon = block.getIconByIndex(iconIndex);
        
    	double dUDelta = ( icon.getMaxU() - icon.getMinU() );
    	double dVDelta = ( icon.getMaxV() - icon.getMinV() );
    	
        double dMinU = icon.getMinU() + dUDelta * minUFrac;
        double dMinV = icon.getMinV() + dVDelta * minVFrac;
        double dMaxU = icon.getMinU() + dUDelta * maxUFrac;
        double dMaxV = icon.getMinV() + dVDelta * maxVFrac;

        tessellator.addVertexWithUV(vertices[0].xCoord, vertices[0].yCoord, vertices[0].zCoord, dMinU, dMinV);
        tessellator.addVertexWithUV(vertices[1].xCoord, vertices[1].yCoord, vertices[1].zCoord, dMinU, dMaxV);
        tessellator.addVertexWithUV(vertices[2].xCoord, vertices[2].yCoord, vertices[2].zCoord, dMaxU, dMaxV);
        tessellator.addVertexWithUV(vertices[3].xCoord, vertices[3].yCoord, vertices[3].zCoord, dMaxU, dMinV);
        
        tessellator.draw();        
        
        GL11.glTranslatef( 0.5F, 0.5F, 0.5F );
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void renderAsFallingBlock(RenderBlocks renderBlocks, Block block, int i, int j, int k, int iMetadata)
	{
    	renderAsBlock(renderBlocks, block, i, j, k);
	}
}
