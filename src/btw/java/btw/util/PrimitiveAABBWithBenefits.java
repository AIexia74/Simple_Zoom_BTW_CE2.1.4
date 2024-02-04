// FCMOD

package btw.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.RenderBlocks;

public class PrimitiveAABBWithBenefits extends AxisAlignedBB
{
	private int assemblyID = -1; // used to identify objects within a parent block model
	private boolean forceRenderWithColorMultiplier = false;
	
    protected PrimitiveAABBWithBenefits(double dXMin, double dYMin, double dZMin,
                                        double dXMax, double dYMax, double dZMax )
    {
    	super( dXMin, dYMin, dZMin, dXMax, dYMax, dZMax  );
    }
    
    public PrimitiveAABBWithBenefits(double dXMin, double dYMin, double dZMin,
                                     double dXMax, double dYMax, double dZMax, int iAssemblyID)
    {
    	super( dXMin, dYMin, dZMin, dXMax, dYMax, dZMax );

        assemblyID = iAssemblyID;
    }
    
    @Override
    public PrimitiveAABBWithBenefits makeTemporaryCopy()
    {
    	PrimitiveAABBWithBenefits tempCopy =
    		new PrimitiveAABBWithBenefits(minX, minY, minZ, maxX, maxY, maxZ, assemblyID);

    	tempCopy.forceRenderWithColorMultiplier = forceRenderWithColorMultiplier;
    	
    	return tempCopy; 
    }
    
    @Override
    public int getAssemblyID()
    {
    	return assemblyID;
    }
    
	//------------- Class Specific Methods ------------//

    public void setForceRenderWithColorMultiplier(boolean bForce)
    {
        forceRenderWithColorMultiplier = bForce;
    }
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderAsBlock(RenderBlocks renderBlocks, Block block, int i, int j, int k)
	{	
    	if ( !forceRenderWithColorMultiplier)
    	{
    		return super.renderAsBlock(renderBlocks, block, i, j, k);
    	}
    	else
    	{
    		return renderAsBlockWithColorMultiplier(renderBlocks, block, i, j, k);
    	}
    }    
}
