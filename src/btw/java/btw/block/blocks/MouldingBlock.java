// FCMOD

package btw.block.blocks;

import btw.block.util.RayTraceUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class MouldingBlock extends Block
{
	protected static final double MOULDING_WIDTH = 0.5D;
	protected static final double MOULDING_HALF_WIDTH = (MOULDING_WIDTH / 2D );
	
	protected static final double MOULDING_LENGTH = 1D;
	
    private static final int[][] facingOfConnections = // indexed by axis and alignment
    {
    	{ -1, 4, -1, 5, 5, 4, 4, 5, -1, 4, -1, 5 }, // i Axis
    	{ 1, 1, 1, 1, -1, -1, -1, -1, 0, 0, 0, 0 }, // j Axis
    	{ 3, -1, 2, -1, 3, 3, 2, 2, 3, -1, 2, -1 }  // k Axis
    };

    private static final int[][] alignmentOffsetAlongAxis = // indexed by axis and alignment
    {
    	{ 0, 1, 0, -1, -1, 1, 1, -1, 0, 1, 0, -1 }, // i Axis
    	{ -1, -1, -1, -1, 0, 0, 0, 0, 1, 1, 1, 1 }, // j Axis
    	{ -1, 0, 1, 0, -1, -1, 1, 1, -1, 0, 1, 0 }  // k Axis
    };

	protected int matchingCornerBlockID;
	
	String textureName;
	
	protected MouldingBlock(int iBlockID, Material material, String sTextureName,
                            int iMatchingCornerBlockID, float fHardness, float fResistance,
                            StepSound stepSound, String name )
	{
        super( iBlockID, material );        
        
        setHardness( fHardness );
        setResistance( fResistance );
        
    	initBlockBounds(0.5D - MOULDING_HALF_WIDTH, 0.5D - MOULDING_HALF_WIDTH, 0D,
						0.5D + MOULDING_HALF_WIDTH, 0.5D + MOULDING_HALF_WIDTH, MOULDING_LENGTH);
    	
        setStepSound( stepSound );
        
        setUnlocalizedName( name );

		matchingCornerBlockID = iMatchingCornerBlockID;

		textureName = sTextureName;
	}
	
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    @Override
    public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k,
                                                  Vec3 startRay, Vec3 endRay )
    {
    	int iAlignment = getMouldingAlignment(world, i, j, k);
    	
    	RayTraceUtils rayTrace = new RayTraceUtils(
    		world, i, j, k, startRay, endRay );
    	
        // check for intersection with the base block
        
    	rayTrace.addBoxWithLocalCoordsToIntersectionList(getBlockBoundsFromPoolForAlignment(
    		iAlignment));
    	
    	// check for intersections with extensions
    	
        for ( int iAxis = 0; iAxis <= 2; iAxis++ )
        {
        	AxisAlignedBB tempBox = getBlockBoundsFromPoolForConnectingBlocksAlongAxis(
        		world, i, j, k, iAxis);
        	
        	if ( tempBox != null )
        	{
        		rayTrace.addBoxWithLocalCoordsToIntersectionList(tempBox);
        	}
        }
        
    	return rayTrace.getFirstIntersection();
    }
    
	@Override
    public void addCollisionBoxesToList( World world, int i, int j, int k, 
    	AxisAlignedBB intersectingBox, List list, Entity entity )
    {
    	int iAlignment = getMouldingAlignment(world, i, j, k);
    	
        // check for intersection with the base block
        
    	getBlockBoundsFromPoolForAlignment(iAlignment).offset(i, j, k).addToListIfIntersects(intersectingBox, list);
    	
    	// check for intersections with extensions
    	
        for ( int iAxis = 0; iAxis <= 2; iAxis++ )
        {
        	AxisAlignedBB tempBox = getBlockBoundsFromPoolForConnectingBlocksAlongAxis(
        		world, i, j, k, iAxis);
        	
        	if ( tempBox != null )
        	{
        		tempBox.offset( i, j, k ).addToListIfIntersects(intersectingBox, list);
        	}
        }
    }
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, 
    	float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
    	int iAlignment = 0;
    	
    	float fXOffsetFromCenter = Math.abs( fClickX - 0.5F );
    	float fYOffsetFromCenter = Math.abs( fClickY - 0.5F );
    	float fZOffsetFromCenter = Math.abs( fClickZ - 0.5F );
    	
    	switch ( iFacing )
    	{
    		case 0:
    			
    			if ( fXOffsetFromCenter > fZOffsetFromCenter )
    			{
    				if ( fClickX > 0.5F )
    				{    				
    					iAlignment = 9;
    				}
    				else
    				{
    					iAlignment = 11;
    				}
    			}
    			else
    			{
    				if ( fClickZ > 0.5F )
    				{    				
    					iAlignment = 10;
    				}
    				else
    				{
    					iAlignment = 8;
    				}
    			}
    			
    			break;
    			
    		case 1:
    			
    			if ( fXOffsetFromCenter > fZOffsetFromCenter )
    			{
    				if ( fClickX > 0.5F )
    				{    				
    					iAlignment = 1;
    				}
    				else
    				{
    					iAlignment = 3;
    				}
    			}
    			else
    			{
    				if ( fClickZ > 0.5F )
    				{    				
    					iAlignment = 2;
    				}
    				else
    				{
    					iAlignment = 0;
    				}
    			}
    			
    			break;
    			
    		case 2:
    			
    			if ( fXOffsetFromCenter > fYOffsetFromCenter )
    			{
    				if ( fClickX > 0.5F )
    				{    				
    					iAlignment = 6;
    				}
    				else
    				{
    					iAlignment = 7;
    				}
    			}
    			else
    			{
    				if ( fClickY > 0.5F )
    				{    				
    					iAlignment = 10;
    				}
    				else
    				{
    					iAlignment = 2;
    				}
    			}
    			
    			break;
    			
    		case 3:
    			
    			if ( fXOffsetFromCenter > fYOffsetFromCenter )
    			{
    				if ( fClickX > 0.5F )
    				{    				
    					iAlignment = 5;
    				}
    				else
    				{
    					iAlignment = 4;
    				}
    			}
    			else
    			{
    				if ( fClickY > 0.5F )
    				{    				
    					iAlignment = 8;
    				}
    				else
    				{
    					iAlignment = 0;
    				}
    			}
    			
    			break;
    			
    		case 4:
    			
    			if ( fZOffsetFromCenter > fYOffsetFromCenter )
    			{
    				if ( fClickZ > 0.5F )
    				{    				
    					iAlignment = 6;
    				}
    				else
    				{
    					iAlignment = 5;
    				}
    			}
    			else
    			{
    				if ( fClickY > 0.5F )
    				{    				
    					iAlignment = 9;
    				}
    				else
    				{
    					iAlignment = 1;
    				}
    			}
    			
    			break;
    			
    		default: // 5
    			
    			if ( fZOffsetFromCenter > fYOffsetFromCenter )
    			{
    				if ( fClickZ > 0.5F )
    				{    				
    					iAlignment = 7;
    				}
    				else
    				{
    					iAlignment = 4;
    				}
    			}
    			else
    			{
    				if ( fClickY > 0.5F )
    				{    				
    					iAlignment = 11;
    				}
    				else
    				{
    					iAlignment = 3;
    				}
    			}
    			
    			break;    			
    	}
    	
    	return setMouldingAlignmentInMetadata(iMetadata, iAlignment);
    }
    
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
    	int iAlignment = getMouldingAlignment(blockAccess, i, j, k);
    	
		return ( iAlignment < 8 );
	}
	
	@Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
    	int iAlignment = getMouldingAlignmentFromMetadata(iMetadata);
    	
		if ( bReverse )
		{
			iAlignment++;
			
			if ( iAlignment == 4 )
			{
				iAlignment = 0;
			}
			else if ( iAlignment == 8 )
			{
				iAlignment = 4;
			}
			else if ( iAlignment >= 12 )
			{
				iAlignment = 8;
			}			
		}
		else
		{
			iAlignment--;
			
			if ( iAlignment < 0 )
			{
				iAlignment = 3;
			}
			else if ( iAlignment == 3 )
			{
				iAlignment = 7;
			}
			else if ( iAlignment == 7 )
			{
				iAlignment = 11;
			}
		}
		
		iMetadata = setMouldingAlignmentInMetadata(iMetadata, iAlignment);
		
		return iMetadata;
	}

	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{		
		int iAlignment = getMouldingAlignment(world, i, j, k);
		
		if ( !bReverse )
		{
			iAlignment++;
			
			if ( iAlignment > 11 )
			{
				iAlignment = 0;
			}
		}
		else
		{
			iAlignment--;
			
			if ( iAlignment < 0 )
			{
				iAlignment = 11;				
			}
		}

		setMouldingAlignment(world, i, j, k, iAlignment);
		
        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        
        return true;
	}
	
	@Override
    public float mobSpawnOnVerticalOffset(World world, int i, int j, int k)
    {
		int iAlignment = getMouldingAlignment(world, i, j, k);
		
		if ( iAlignment < 4 )
		{
			return -0.5F;
		}
		
		return 0F;		
    }
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getBlockBoundsFromPoolForAlignment(getMouldingAlignment(blockAccess, i, j, k));
    }

    //------------- Class Specific Methods ------------//
    
    public AxisAlignedBB getBlockBoundsFromPoolForAlignment(int iAlignment)
    {    
    	AxisAlignedBB bounds = AxisAlignedBB.getAABBPool().getAABB( 0D, 0D, 0D, 1D, 1D, 1D ); 
    	
    	if ( iAlignment == 0 )
    	{
    		bounds.maxY = bounds.minY + MOULDING_WIDTH;
    		bounds.maxZ = bounds.minZ + MOULDING_WIDTH;
    	}
    	else if ( iAlignment == 1 )
    	{    		
    		bounds.minX += 1D - MOULDING_WIDTH;
    		bounds.maxY = bounds.minY + MOULDING_WIDTH;
    	}
    	else if ( iAlignment == 2 )
    	{
    		bounds.maxY = bounds.minY + MOULDING_WIDTH;
    		bounds.minZ += 1D - MOULDING_WIDTH;
    	}
    	else if ( iAlignment == 3 )
    	{
    		bounds.maxX = bounds.minX + MOULDING_WIDTH;
    		bounds.maxY = bounds.minY + MOULDING_WIDTH;
    	}
    	else if ( iAlignment == 4 )
    	{
    		bounds.maxX = bounds.minX + MOULDING_WIDTH;
    		bounds.maxZ = bounds.minZ + MOULDING_WIDTH;
    	}
    	else if ( iAlignment == 5 )
    	{
    		bounds.minX += 1D - MOULDING_WIDTH;
    		bounds.maxZ = bounds.minZ + MOULDING_WIDTH;
    	}
    	else if ( iAlignment == 6 )
    	{
    		bounds.minX += 1D - MOULDING_WIDTH;
    		bounds.minZ += 1D - MOULDING_WIDTH;
    	}
    	else if ( iAlignment == 7 )
    	{
    		bounds.maxX = bounds.minX + MOULDING_WIDTH;
    		bounds.minZ += 1D - MOULDING_WIDTH;
    	}
    	else if ( iAlignment == 8 )
    	{
    		bounds.minY += 1D - MOULDING_WIDTH;
    		bounds.maxZ = bounds.minZ + MOULDING_WIDTH;
    	}
    	else if ( iAlignment == 9 )
    	{
    		bounds.minX += 1D - MOULDING_WIDTH;
    		bounds.minY += 1D - MOULDING_WIDTH;
    	}
    	else if ( iAlignment == 10 )
    	{
    		bounds.minY += 1D - MOULDING_WIDTH;
    		bounds.minZ += 1D - MOULDING_WIDTH;
    	}
    	else // 11
    	{
    		bounds.maxX = bounds.minX + MOULDING_WIDTH;
    		bounds.minY += 1D - MOULDING_WIDTH;
    	}
    	
    	return bounds;
    }
    
    protected boolean isMouldingOfSameType(IBlockAccess blockAccess, int i, int j, int k)
    {    	
    	return blockAccess.getBlockId( i, j, k ) == blockID;
    }
    
    public int getMouldingAlignment(IBlockAccess iBlockAccess, int i, int j, int k)
	{
    	return getMouldingAlignmentFromMetadata(iBlockAccess.getBlockMetadata(i, j, k));
	}    
    
    public int getMouldingAlignmentFromMetadata(int iMetadata)
	{
    	return iMetadata;
	}    
    
    public void setMouldingAlignment(World world, int i, int j, int k, int iAlignment)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k );
    	
    	iMetadata = setMouldingAlignmentInMetadata(iMetadata, iAlignment);
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }
    
    public int setMouldingAlignmentInMetadata(int iMetadata, int iAlignment)
    {
        return iAlignment;
    }    
    
	private float clickOffsetFromCenter(float fClickPos)
	{
		return Math.abs( fClickPos - 0.5F );
	}
	
    static private void offsetCornerBoundingBoxAlongAxis(int iAxis, int iOffset, Vec3 min, Vec3 max)
    {
    	if ( iOffset > 0 )
    	{
    		if ( iAxis == 0 )
    		{
    			min.xCoord += 0.5D;
    			max.xCoord += 0.5D;
    		}
    		else if ( iAxis == 1 )
    		{
    			min.yCoord += 0.5D;
    			max.yCoord += 0.5D;
    		}
    		else // 2
    		{
    			min.zCoord += 0.5D;
    			max.zCoord += 0.5D;
    		}
    	}
    }
    
    private boolean isAlignedAlongAxis(IBlockAccess blockAccess, int i, int j, int k, int iAxis)
    {
		int iAlignment = getMouldingAlignment(blockAccess, i, j, k);
		
		// if the target moulding runs along the axis of connection 
		// it could not itself connect along it
		
		return facingOfConnections[iAxis][iAlignment] < 0;
    }
    
    /*
     * returns -1 if no connecting moulding present
     */
    private int getAlignmentOfConnectingMouldingAtLocation(IBlockAccess blockAccess,
														   int i, int j, int k, int iAlignmentToConnectTo, int iAxisToConnectAlong)
    {
    	int iBlockID = blockAccess.getBlockId( i, j, k );
    	
    	if (isMouldingOfSameType(blockAccess, i, j, k) &&
			isAlignedAlongAxis(blockAccess, i, j, k, iAxisToConnectAlong) )
    	{
    		int iTargetAlignment = getMouldingAlignment(blockAccess, i, j, k);

			for ( int iTempAxis = 0; iTempAxis <= 2; iTempAxis++ )
			{
				if ( iTempAxis != iAxisToConnectAlong )
				{
					if (facingOfConnections[iTempAxis][iTargetAlignment] ==
						facingOfConnections[iTempAxis][iAlignmentToConnectTo] )
					{
						return iTargetAlignment;
					}
				}
			}
    	}
    	
    	return -1;
    }
    
    /**
     * returns -1 if no connecting corner present
     */
    private int getConnectingCornerFacingAtLocation(IBlockAccess blockAccess, int i, int j, int k, int iAlignmentToConnectTo, int iAxisToConnectAlong)
    {
    	int iBlockID = blockAccess.getBlockId( i, j, k );

    	if (iBlockID == matchingCornerBlockID)
    	{
    		SidingAndCornerBlock corner = (SidingAndCornerBlock)Block.blocksList[iBlockID];
    		
        	int iMetadata = blockAccess.getBlockMetadata( i, j, k );
        	
    		if ( corner.getIsCorner(iMetadata) )
    		{    		
    	    	int iCornerFacing = corner.getFacing(iMetadata);
    	    	
	    		// make sure the corner is flush with the side the moulding is on
	    		
	    		if (alignmentOffsetAlongAxis[iAxisToConnectAlong][iAlignmentToConnectTo] ==
					corner.getCornerAlignmentOffsetAlongAxis(iCornerFacing, iAxisToConnectAlong) )
	    		{
					int iReturnValue = -1;
					
	    			for ( int iTempAxis = 0; iTempAxis <= 2; iTempAxis++ )
	    			{    				
	    				if ( iTempAxis != iAxisToConnectAlong )
	    				{
							if (alignmentOffsetAlongAxis[iTempAxis][iAlignmentToConnectTo] == 0 )
							{
								iReturnValue = iCornerFacing;
							}
							else
							{
					    		if (alignmentOffsetAlongAxis[iTempAxis][iAlignmentToConnectTo] !=
									corner.getCornerAlignmentOffsetAlongAxis(iCornerFacing,
																			 iTempAxis) )
					    		{
					    			// the corner does not match up
					    			
					    			return -1;
					    		}
							}
	    				}
	    			}
	    			
	    			return iReturnValue;
	    		}
    		}
    	}
    	
    	return -1;
    }
    
    private AxisAlignedBB getBlockBoundsFromPoolForConnectingBlocksAlongAxis(
    	IBlockAccess blockAccess, int i, int j, int k, int iAxis)
    {
    	int iAlignment = getMouldingAlignment(blockAccess, i, j, k);
    	
		int iConnectionToFacing = facingOfConnections[iAxis][iAlignment];
		
		if ( iConnectionToFacing >= 0 )
		{    		                              
    		BlockPos connectingPos = new BlockPos( i, j, k, iConnectionToFacing );
    		
    		int iConnectingAlignment = getAlignmentOfConnectingMouldingAtLocation(blockAccess,
																				  connectingPos.x, connectingPos.y, connectingPos.z, iAlignment, iAxis);
    		
    		if ( iConnectingAlignment >= 0 )
    		{
    			return getBlockBoundsFromPoolForConnectingMoulding(iConnectionToFacing,
																   iConnectingAlignment);
			}
    		else
    		{
    			int iConnectingFacing = getConnectingCornerFacingAtLocation(blockAccess,
																			connectingPos.x, connectingPos.y, connectingPos.z, iAlignment, iAxis);
    			
    			if ( iConnectingFacing >= 0 )
    			{
    				return getBlockBoundsFromPoolForConnectingCorner(iAxis,
																	 alignmentOffsetAlongAxis[iAxis][iAlignment], iConnectingFacing);
				}
    		}
		}
    	
    	return null;
    }
    
    private AxisAlignedBB getBlockBoundsFromPoolForConnectingMoulding(int iToFacing, int
    	iToAlignment)
    {    	
    	AxisAlignedBB box = getBlockBoundsFromPoolForAlignment(iToAlignment);

    	// clip the connecting molding's box to the direction of connection
    	
    	if ( iToFacing == 0 )
    	{
    		box.maxY = 0.5F;
    	}
    	else if ( iToFacing == 1 )
    	{
    		box.minY = 0.5F;
    	}
    	else if ( iToFacing == 2 )
    	{
    		box.maxZ = 0.5F;
    	}
    	else if ( iToFacing == 3 )
    	{
    		box.minZ = 0.5F;
    	}
    	else if ( iToFacing == 4 )
    	{
    		box.maxX = 0.5F;
    	}
    	else // if ( iToFacing == 5 )
    	{
    		box.minX = 0.5F;
    	}
    	
    	return box;
    }
    
    private AxisAlignedBB getBlockBoundsFromPoolForConnectingCorner(int iConnectingAxis,
																	int iOffsetAlongAxis, int iCornerFacing)
    {
    	Vec3 cornerMin = Vec3.createVectorHelper( 0, 0, 0 );
    	Vec3 cornerMax = Vec3.createVectorHelper( 0.5, 0.5, 0.5 );

    	offsetBoundingBoxForConnectingCorner(iConnectingAxis,
											 iOffsetAlongAxis, iCornerFacing, cornerMin, cornerMax);
    	
    	return AxisAlignedBB.getAABBPool().getAABB(         	
    		cornerMin.xCoord, cornerMin.yCoord, cornerMin.zCoord, 
			cornerMax.xCoord, cornerMax.yCoord, cornerMax.zCoord );    	
    }
    
    private void offsetBoundingBoxForConnectingCorner(int iConnectingAxis, int iOffsetAlongAxis, int iCornerFacing, Vec3 boundingMin, Vec3 boundingMax)
    {
        for ( int iTempAxis = 0; iTempAxis <= 2; iTempAxis++ )
        {
        	if ( iTempAxis == iConnectingAxis )
        	{
        		offsetCornerBoundingBoxAlongAxis(iTempAxis, -iOffsetAlongAxis, boundingMin, boundingMax);
        	}
        	else
        	{
        		offsetCornerBoundingBoxAlongAxis(iTempAxis,
												 SidingAndCornerBlock.getCornerAlignmentOffsetAlongAxis(
    				iCornerFacing, iTempAxis), boundingMin, boundingMax);
        	}
        }
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		blockIcon = register.registerIcon(textureName);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, int i, int j, int k )
	{
    	int iAlignment = getMouldingAlignment(world, i, j, k);
    	
    	// base block
    	
    	AxisAlignedBB box = getBlockBoundsFromPoolForAlignment(iAlignment);
    	
        // connecting mouldings and corners
        
        for ( int iAxis = 0; iAxis <= 2; iAxis++ )
        {
        	AxisAlignedBB tempBox = getBlockBoundsFromPoolForConnectingBlocksAlongAxis(
        		world, i, j, k, iAxis);
        	
        	if ( tempBox != null )
        	{
        		box.expandToInclude(tempBox);
        	}
        }
        
        return box.offset( i, j, k );
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderer.blockAccess;
    	
    	int iAlignment = getMouldingAlignment(blockAccess, i, j, k);
    	
    	// basic block
    	
    	renderer.setRenderBounds(getBlockBoundsFromPoolForAlignment(iAlignment));
    	
        renderer.renderStandardBlock( this, i, j, k );
        
        // connecting mouldings and corners
        
        for ( int iAxis = 0; iAxis <= 2; iAxis++ )
        {
        	AxisAlignedBB tempBox = getBlockBoundsFromPoolForConnectingBlocksAlongAxis(
        		blockAccess, i, j, k, iAxis);
        	
        	if ( tempBox != null )
        	{
            	renderer.setRenderBounds( tempBox );
            	
                renderer.renderStandardBlock( this, i, j, k );
        	}
        }
        
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return idDropped( world.getBlockMetadata( i, j, k ), world.rand, 0 );
    }	
}