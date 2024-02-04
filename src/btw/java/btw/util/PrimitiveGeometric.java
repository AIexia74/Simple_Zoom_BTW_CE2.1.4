// FCMOD

package btw.util;

import btw.block.util.RayTraceUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

import com.prupe.mcpatcher.mal.block.RenderBlocksUtils;
import net.minecraft.src.*;

public abstract class PrimitiveGeometric
{
	public abstract PrimitiveGeometric makeTemporaryCopy();
	
	public abstract void rotateAroundYToFacing(int iFacing);
	
	public abstract void tiltToFacingAlongY(int iFacing);
	
	public abstract void addToRayTrace(RayTraceUtils rayTrace);
	
	public abstract void translate(double dDeltaX, double dDeltaY, double dDeltaZ);
	
    public void addIntersectingBoxesToCollisionList(World world, int i, int j, int k, AxisAlignedBB boxToIntersect, List collisionList)
    {
    	// not every primitive type will add boxes
    }
    
    public int getAssemblyID()
    {
    	// not every primitive type will support assembly IDs
    	
    	return -1;
    }
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
	public abstract boolean renderAsBlock(RenderBlocks renderBlocks, Block block, int i, int j, int k);
	
    @Environment(EnvType.CLIENT)
	public abstract boolean renderAsBlockWithColorMultiplier(RenderBlocks renderBlocks, Block block, int i, int j, int k, float fRed, float fGreen, float fBlue);
	
    @Environment(EnvType.CLIENT)
	public boolean renderAsBlockWithColorMultiplier(RenderBlocks renderBlocks, Block block, int x, int y, int z) {
        int colorMultiplier = block.colorMultiplier(renderBlocks.blockAccess, x, y, z);
        
        float red = (float) (colorMultiplier >> 16 & 255) / 255F;
        float green = (float) (colorMultiplier >> 8 & 255) / 255F;
        float blue = (float) (colorMultiplier & 255) / 255F;
        
        RenderBlocksUtils.setupColorMultiplierForceNoAO(block, renderBlocks.blockAccess, x, y, z, red, green, blue, true);
        
        return renderAsBlockWithColorMultiplier(renderBlocks, block, x, y, z, red, green, blue);
	}

    @Environment(EnvType.CLIENT)
    public abstract boolean renderAsBlockWithTexture(RenderBlocks renderBlocks, Block block, int i, int j, int k, Icon icon);

    @Environment(EnvType.CLIENT)
    public abstract boolean renderAsBlockFullBrightWithTexture(RenderBlocks renderBlocks, Block block, int i, int j, int k, Icon icon);

    @Environment(EnvType.CLIENT)
    public abstract void renderAsItemBlock(RenderBlocks renderBlocks, Block block, int iItemDamage);

    @Environment(EnvType.CLIENT)
    public abstract void renderAsFallingBlock(RenderBlocks renderBlocks, Block block, int i, int j, int k, int iMetadata);
}
