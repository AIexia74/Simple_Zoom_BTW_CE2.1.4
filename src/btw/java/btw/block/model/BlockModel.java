// FCMOD

package btw.block.model;

import btw.util.PrimitiveGeometric;
import btw.block.util.RayTraceUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

public class BlockModel extends PrimitiveGeometric
{
	private List<PrimitiveGeometric> primitiveList;
	
	private int assemblyID = -1; // used to identify models within an assembly
	private int activePrimitiveID = -1; // used when rendering to track which primitive is being processed
	
	public BlockModel()
	{
		primitiveList = new LinkedList();
		
		initModel();
	}
	
	public BlockModel(int iAssemblyID )
	{
		this();
		
		setAssemblyID(iAssemblyID);
	}
	
    @Override
    public BlockModel makeTemporaryCopy()
    {
    	BlockModel newModel = new BlockModel(getAssemblyID() );
    	
    	makeTemporaryCopyOfPrimitiveList(newModel);
    	
    	return newModel;
    }
    
	/**
	 * Yaws the model around the J axis. Assumes that the model's initial facing is along the negative K axis (facing 2)
	 */	
    @Override
	public void rotateAroundYToFacing(int iFacing)
	{
		if ( iFacing > 2 )
		{
	    	Iterator<PrimitiveGeometric> tempIterator = primitiveList.iterator();
	    	
	    	while ( tempIterator.hasNext() )
	    	{
	    		tempIterator.next().rotateAroundYToFacing(iFacing);
	    	}
		}
	}

	/**
	 * "Tilts" the model towards the desired facing.  Takes the up vector and either yaws or rolls it towards the specified axis.
	 */	
    @Override
	public void tiltToFacingAlongY(int iFacing)
	{
		if ( iFacing != 1 )
		{
	    	Iterator<PrimitiveGeometric> tempIterator = primitiveList.iterator();
	    	
	    	while ( tempIterator.hasNext() )
	    	{
	    		tempIterator.next().tiltToFacingAlongY(iFacing);
	    	}
		}
	}
    
    @Override
	public void translate(double dDeltaX, double dDeltaY, double dDeltaZ)
    {
    	Iterator<PrimitiveGeometric> tempIterator = primitiveList.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		tempIterator.next().translate(dDeltaX, dDeltaY, dDeltaZ);
    	}
    }
	
    @Override
    public void addToRayTrace(RayTraceUtils rayTrace)
    {
    	Iterator<PrimitiveGeometric> tempIterator = primitiveList.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		tempIterator.next().addToRayTrace(rayTrace);
    	}
    }
    
    @Override
    public void addIntersectingBoxesToCollisionList(World world, int i, int j, int k, AxisAlignedBB boxToIntersect, List collisionList)
    {
    	Iterator<PrimitiveGeometric> tempIterator = primitiveList.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		tempIterator.next().addIntersectingBoxesToCollisionList(world, i, j, k,
                                                                    boxToIntersect, collisionList);
    	}
    }

    @Override
    public int getAssemblyID()
    {
    	return assemblyID;
    }
    
	//------------- Class Specific Methods ------------//
	
    protected void initModel()
    {
    }

    public void makeTemporaryCopyOfPrimitiveList(BlockModel modelTo)
    {
    	Iterator<PrimitiveGeometric> tempIterator = primitiveList.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		modelTo.primitiveList.add(tempIterator.next().makeTemporaryCopy());
    	}
    }
    
	public void addPrimitive(PrimitiveGeometric primitive)
	{
		primitiveList.add(primitive);
	}
	
	public void addBox(double dMinX, double dMinY, double dMinZ, double dMaxX, double dMaxY, double dMaxZ)
	{
		primitiveList.add(new AxisAlignedBB(dMinX, dMinY, dMinZ, dMaxX, dMaxY, dMaxZ ));
	}
	
    public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay)
    {
    	RayTraceUtils rayTrace = new RayTraceUtils( world, i, j, k, startRay, endRay );

    	addToRayTrace(rayTrace);
    	
        return rayTrace.getFirstIntersection();
    }
    
    public int getActivePrimitiveID()
    {
    	return activePrimitiveID;
    }
    
    public void setAssemblyID(int iAssemblyID)
    {
		assemblyID = iAssemblyID;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderAsBlock(RenderBlocks renderBlocks, Block block, int i, int j, int k)
	{	
    	Iterator<PrimitiveGeometric> tempIterator = primitiveList.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		PrimitiveGeometric tempPrimitive = tempIterator.next();

			activePrimitiveID = tempPrimitive.getAssemblyID();
    		
    		tempPrimitive.renderAsBlock(renderBlocks, block, i, j, k);
        }

		activePrimitiveID = -1;
		
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderAsBlockWithColorMultiplier(RenderBlocks renderBlocks, Block block, int i, int j, int k, float fRed, float fGreen, float fBlue)
	{
    	Iterator<PrimitiveGeometric> tempIterator = primitiveList.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		PrimitiveGeometric tempPrimitive = tempIterator.next();

			activePrimitiveID = tempPrimitive.getAssemblyID();
    		
    		tempPrimitive.renderAsBlockWithColorMultiplier(renderBlocks, block, i, j, k, fRed, fGreen, fBlue);
        }

		activePrimitiveID = -1;
        
        return true;        
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderAsBlockWithTexture(RenderBlocks renderBlocks, Block block, int i, int j, int k, Icon icon)
	{	
    	Iterator<PrimitiveGeometric> tempIterator = primitiveList.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		PrimitiveGeometric tempPrimitive = tempIterator.next();

			activePrimitiveID = tempPrimitive.getAssemblyID();

    		tempPrimitive.renderAsBlockWithTexture(renderBlocks, block, i, j, k, icon);
        }

		activePrimitiveID = -1;
        
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderAsBlockFullBrightWithTexture(RenderBlocks renderBlocks, Block block, int i, int j, int k, Icon icon)
	{
    	Iterator<PrimitiveGeometric> tempIterator = primitiveList.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		PrimitiveGeometric tempPrimitive = tempIterator.next();

			activePrimitiveID = tempPrimitive.getAssemblyID();

    		tempPrimitive.renderAsBlockFullBrightWithTexture(renderBlocks, block, i, j, k, icon);
        }

		activePrimitiveID = -1;
        
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderAsItemBlock(RenderBlocks renderBlocks, Block block, int iItemDamage)
    {	
    	Iterator<PrimitiveGeometric> tempIterator = primitiveList.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		PrimitiveGeometric tempPrimitive = tempIterator.next();

			activePrimitiveID = tempPrimitive.getAssemblyID();

    		tempPrimitive.renderAsItemBlock(renderBlocks, block, iItemDamage);
        }

		activePrimitiveID = -1;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderAsFallingBlock(RenderBlocks renderBlocks, Block block, int i, int j, int k, int iMetadata)
	{	
    	Iterator<PrimitiveGeometric> tempIterator = primitiveList.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		PrimitiveGeometric tempPrimitive = tempIterator.next();

			activePrimitiveID = tempPrimitive.getAssemblyID();
    		
    		tempPrimitive.renderAsFallingBlock(renderBlocks, block, i, j, k, iMetadata);
        }

		activePrimitiveID = -1;
    }
    
}