// FCMOD

package btw.block.blocks;

import btw.block.util.RayTraceUtils;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class StairsBlockBase extends Block
{
    protected StairsBlockBase(int iBlockID, Material material )
    {
        super( iBlockID, material );
        
        setLightOpacity( 255 );
        
        Block.useNeighborBrightness[iBlockID] = true;        
        
        setCreativeTab( CreativeTabs.tabDecorations );
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
    public int getRenderType()
    {
        return 10;
    }

    @Override
    public void onBlockAdded(World world, int i, int j, int k )
    {
    	notifyAllNearbyBlocksFlat(world, i, j, k);
    }

    @Override
    public void breakBlock(World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
    	notifyAllNearbyBlocksFlat(world, i, j, k);
    }
    
    @Override
    public int preBlockPlacedBy(World world, int i, int j, int k, int iMetadata, EntityLiving entityBy)
    {
        int iFlatFacing = MathHelper.floor_float( ( entityBy.rotationYaw * 4.0F / 360.0F ) + 0.5F ) & 3;

        if ( iFlatFacing == 0 )
        {
        	iMetadata = setDirection(iMetadata, 2);
        }
        else if ( iFlatFacing == 1 )
        {
        	iMetadata = setDirection(iMetadata, 1);
        }
        else if ( iFlatFacing == 2 )
        {
        	iMetadata = setDirection(iMetadata, 3);
        }
        else // iFlatFacing == 3
        {
        	iMetadata = setDirection(iMetadata, 0);
        }
        
        
        return validateMetadataForLocation(world, i, j, k, iMetadata);
    }

    @Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
		if ( iFacing == 0 || ( iFacing != 1 && fClickY > 0.5D ) )
		{
    		iMetadata = setUpsideDown(iMetadata);
		}
    	
        return iMetadata;
    }

    @Override
    public void addCollisionBoxesToList( World world, int i, int j, int k, 
    	AxisAlignedBB intersectingBox, List list, Entity entity )
    {
    	AxisAlignedBB baseBox = getBoundsFromPoolForBase(world, i, j, k).offset(i, j, k);
    	
    	baseBox.addToListIfIntersects(intersectingBox, list);
        
    	AxisAlignedBB secondaryBox = AxisAlignedBB.getAABBPool().getAABB( 
    		0D, 0D, 0D, 1D, 1D, 1D );
    	
        boolean bIsFullStep = getBoundsForSecondaryPiece(world, i, j, k, secondaryBox);
        
        secondaryBox.offset( i, j, k );
        
        secondaryBox.addToListIfIntersects(intersectingBox, list);

        if ( bIsFullStep )
        {
        	AxisAlignedBB tertiaryBox = AxisAlignedBB.getAABBPool().getAABB( 
        		0D, 0D, 0D, 1D, 1D, 1D );
        	
        	int iTertiaryFacing = getBoundsForTertiaryPiece(world, i, j, k, tertiaryBox);
        	
        	if ( iTertiaryFacing >= 0 )
        	{
        		tertiaryBox.offset( i, j, k );
                
        		tertiaryBox.addToListIfIntersects(intersectingBox, list);
        	}
        }
    }

    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, 
    	Vec3 startVec, Vec3 endVec )
    {    	
    	RayTraceUtils rayTrace = new RayTraceUtils( world, i, j, k, startVec, endVec );
    	
    	AxisAlignedBB baseBox = getBoundsFromPoolForBase(world, i, j, k);
    	
    	rayTrace.addBoxWithLocalCoordsToIntersectionList(baseBox);
        
    	AxisAlignedBB secondaryBox = AxisAlignedBB.getAABBPool().getAABB( 
    		0D, 0D, 0D, 1D, 1D, 1D );
    	
        boolean bIsFullStep = getBoundsForSecondaryPiece(world, i, j, k, secondaryBox);
        
        rayTrace.addBoxWithLocalCoordsToIntersectionList(secondaryBox);

        if ( bIsFullStep )
        {
        	AxisAlignedBB tertiaryBox = AxisAlignedBB.getAABBPool().getAABB( 
        		0D, 0D, 0D, 1D, 1D, 1D );
        	
        	int iTertiaryFacing = getBoundsForTertiaryPiece(world, i, j, k, tertiaryBox);
        	
        	if ( iTertiaryFacing >= 0 )
        	{
        		rayTrace.addBoxWithLocalCoordsToIntersectionList(tertiaryBox);
        	}
        }
    	
        return rayTrace.getFirstIntersection();
    }
    
    @Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
    	if ( iFacing == 0 )
    	{
    		return !getIsUpsideDown(blockAccess, i, j, k);
    	}
    	else if ( iFacing == 1 )
    	{
    		return getIsUpsideDown(blockAccess, i, j, k);
    	}
    	else 
    	{
    		int iBlockFacing = convertDirectionToFacing(getDirection(blockAccess, i, j, k));
    		
    		if ( iFacing == iBlockFacing )
    		{
    			// the backside will only be partial if another stair block sits next to it, so it's safe to say that it's always a hardpoint
    			
    			return true;
    		}
    		else if ( iFacing != Block.getOppositeFacing(iBlockFacing) )
    		{
    			return hasSecondaryFullSurfaceToFacing(blockAccess, i, j, k, iFacing);
    		}
    	}    	
    	
		return false;
	}
    
    @Override
    public boolean isStairBlock()
    {
    	return true;
    }

    @Override
    protected boolean canSilkHarvest()
    {
        return true;
    }
    
    @Override
	public boolean hasContactPointToFullFace(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
	{
    	return true;
	}
	
    @Override
	public boolean hasContactPointToSlabSideFace(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIsSlabUpsideDown)
	{
		int iStairFacing = convertDirectionToFacing(getDirection(blockAccess, i, j, k));
		
		if ( iFacing == Block.getOppositeFacing(iStairFacing) )
		{
			return getIsUpsideDown(blockAccess, i, j, k) == bIsSlabUpsideDown;
		}
		
		return true;
	}
    
    @Override
	public boolean hasContactPointToStairNarrowVerticalFace(IBlockAccess blockAccess, int i, int j, int k, int iFacing, int iStairFacing)
	{
		boolean bIsUpsideDown = getIsUpsideDown(blockAccess, i, j, k);
		
		if ( bIsUpsideDown == ( iFacing == 1 ) )
		{
			return true;
		}
			
		int iMyStairFacing = convertDirectionToFacing(getDirection(blockAccess, i, j, k));
		
		return iMyStairFacing != Block.getOppositeFacing(iStairFacing);
	}
	
    @Override
    public boolean hasNeighborWithMortarInContact(World world, int i, int j, int k)
    {
    	int iFacing = convertDirectionToFacing(getDirection(world, i, j, k));
    	boolean bIsUpsideDown = getIsUpsideDown(world, i, j, k);
    	
    	return hasNeighborWithMortarInContact(world, i, j, k, iFacing, bIsUpsideDown);
    	
    }
    
    @Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
    @Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
    @Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		int iDirection = ( iMetadata & 3 ) + 2;
		
		iDirection = Block.rotateFacingAroundY(iDirection, !bReverse);
		
		return ( iMetadata & (~3) ) | ( iDirection - 2 );		
	}
	
    @Override
    public boolean canMobsSpawnOn(World world, int i, int j, int k)
    {
        return blockMaterial.getMobsCanSpawnOn(world.provider.dimensionId);
    }

    //------------- Class Specific Methods ------------//
    
    protected int validateMetadataForLocation(World world, int i, int j, int k, int iMetadata)
    {
    	return iMetadata;
    }
    
    public boolean hasNeighborWithMortarInContact(World world, int i, int j, int k, int iFacing, boolean bIsUpsideDown)
    {
    	if ( !bIsUpsideDown )
    	{
    		if (WorldUtils.hasNeighborWithMortarInFullFaceContactToFacing(world, i, j, k, 0) ||
                WorldUtils.hasNeighborWithMortarInStairNarrowVerticalContactToFacing(world, i, j, k, 1, iFacing) )
			{
				return true;
			}
    	}
    	else
    	{
    		if (WorldUtils.hasNeighborWithMortarInFullFaceContactToFacing(world, i, j, k, 1) ||
                WorldUtils.hasNeighborWithMortarInStairNarrowVerticalContactToFacing(world, i, j, k, 0, iFacing) )
			{
				return true;
			}
    	}
    	
		int iHalfBlockFacing = Block.getOppositeFacing(iFacing);

    	for ( int iTempFacing = 2; iTempFacing < 6; iTempFacing++ )
    	{
    		if ( iTempFacing == iHalfBlockFacing )
    		{
	    		if ( WorldUtils.hasNeighborWithMortarInSlabSideContactToFacing(world, i, j, k, iTempFacing, bIsUpsideDown) )
				{
					return true;
				}
    		}
    		else
    		{
        		if ( WorldUtils.hasNeighborWithMortarInStairShapedContactToFacing(world, i, j, k, iTempFacing) )
    			{
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
    
    protected int convertDirectionToFacing(int iDirection)
    {
    	int iFacing = 5 - iDirection;
    	
    	return iFacing;    	
    }

    protected AxisAlignedBB getBoundsFromPoolForBase(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getBoundsFromPoolForBase(blockAccess.getBlockMetadata(i, j, k));
    }
    
    protected AxisAlignedBB getBoundsFromPoolForBase(int iMetadata)
    {
    	if ( getIsUpsideDown(iMetadata) )
        {
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F );
        }
        else
        {
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F );
        }
    }

    protected boolean getBoundsForSecondaryPiece(IBlockAccess blockAccess, int i, int j, int k, AxisAlignedBB box)
    {
        int iMetadata = blockAccess.getBlockMetadata(i, j, k);
        
        int iBlockDir = getDirection(iMetadata);
        boolean bUpsideDown = getIsUpsideDown(iMetadata);
        
        box.minY = 0.5F;
        box.maxY = 1.0F;

        if ( bUpsideDown )
        {
        	box.minY = 0.0F;
        	box.maxY = 0.5F;
        }

        box.minX = 0.0F;
        box.minZ = 0.0F;
        
        box.maxX = 1.0F;
        box.maxZ = 0.5F;
        
        boolean bIsFullStep = true;
        
        if ( iBlockDir == 0 )
        {
        	box.minX = 0.5F;
        	box.maxZ = 1.0F;
            
            int iNeighborID = blockAccess.getBlockId(i + 1, j, k);
            int iNeighborMetadata = blockAccess.getBlockMetadata(i + 1, j, k);

            if (isStairBlock(iNeighborID) && (iMetadata & 4) == (iNeighborMetadata & 4))
            {
                int iNeighborDir = iNeighborMetadata & 3;

                if (iNeighborDir == 3 && !this.isStairBlockWithMetadata(blockAccess, i, j, k + 1, iMetadata))
                {
                	box.maxZ = 0.5F;
                    bIsFullStep = false;
                }
                else if (iNeighborDir == 2 && !this.isStairBlockWithMetadata(blockAccess, i, j, k - 1, iMetadata))
                {
                	box.minZ = 0.5F;
                    bIsFullStep = false;
                }
            }
        }
        else if (iBlockDir == 1)
        {
        	box.maxX = 0.5F;
        	box.maxZ = 1.0F;
            
            int iNeighborID = blockAccess.getBlockId(i - 1, j, k);
            int iNeighborMetadata = blockAccess.getBlockMetadata(i - 1, j, k);

            if (isStairBlock(iNeighborID) && (iMetadata & 4) == (iNeighborMetadata & 4))
            {
                int iNeighborDir = iNeighborMetadata & 3;

                if (iNeighborDir == 3 && !this.isStairBlockWithMetadata(blockAccess, i, j, k + 1, iMetadata))
                {
                	box.maxZ = 0.5F;
                    bIsFullStep = false;
                }
                else if (iNeighborDir == 2 && !this.isStairBlockWithMetadata(blockAccess, i, j, k - 1, iMetadata))
                {
                	box.minZ = 0.5F;
                    bIsFullStep = false;
                }
            }
        }
        else if (iBlockDir == 2)
        {
        	box.minZ = 0.5F;
        	box.maxZ = 1.0F;
            
            int iNeighborID = blockAccess.getBlockId(i, j, k + 1);
            int iNeighborMetadata = blockAccess.getBlockMetadata(i, j, k + 1);

            if (isStairBlock(iNeighborID) && (iMetadata & 4) == (iNeighborMetadata & 4))
            {
                int iNeighborDir = iNeighborMetadata & 3;

                if (iNeighborDir == 1 && !this.isStairBlockWithMetadata(blockAccess, i + 1, j, k, iMetadata))
                {
                	box.maxX = 0.5F;
                    bIsFullStep = false;
                }
                else if (iNeighborDir == 0 && !this.isStairBlockWithMetadata(blockAccess, i - 1, j, k, iMetadata))
                {
                	box.minX = 0.5F;
                    bIsFullStep = false;
                }
            }
        }
        else if (iBlockDir == 3)
        {
            int iNeighborID = blockAccess.getBlockId(i, j, k - 1);
            int iNeighborMetadata = blockAccess.getBlockMetadata(i, j, k - 1);

            if (isStairBlock(iNeighborID) && (iMetadata & 4) == (iNeighborMetadata & 4))
            {
                int iNeighborDir = iNeighborMetadata & 3;

                if (iNeighborDir == 1 && !this.isStairBlockWithMetadata(blockAccess, i + 1, j, k, iMetadata))
                {
                	box.maxX = 0.5F;
                    bIsFullStep = false;
                }
                else if (iNeighborDir == 0 && !this.isStairBlockWithMetadata(blockAccess, i - 1, j, k, iMetadata))
                {
                	box.minX = 0.5F;
                    bIsFullStep = false;
                }
            }
        }

        return bIsFullStep;
    }

    protected AxisAlignedBB getBoundsFromPoolForSecondaryPiece(int iMetadata)
    {
    	AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB( 0D, 0D, 0D, 1D, 1D, 1D );
    	
        int iBlockDir = getDirection(iMetadata);
        boolean bUpsideDown = getIsUpsideDown(iMetadata);
        
        box.minY = 0.5F;
        box.maxY = 1.0F;

        if ( bUpsideDown )
        {
        	box.minY = 0.0F;
        	box.maxY = 0.5F;
        }

        box.minX = 0.0F;
        box.minZ = 0.0F;
        
        box.maxX = 1.0F;
        box.maxZ = 0.5F;
        
        if ( iBlockDir == 0 )
        {
        	box.minX = 0.5F;
        	box.maxZ = 1.0F;            
        }
        else if (iBlockDir == 1)
        {
        	box.maxX = 0.5F;
        	box.maxZ = 1.0F;
        }
        else if (iBlockDir == 2)
        {
        	box.minZ = 0.5F;
        	box.maxZ = 1.0F;
        }
    	
        return box;        
    }
    
    /**
     * Returns the facing of the piece if it exists, -1 otherwise
     */
    protected int getBoundsForTertiaryPiece(IBlockAccess blockAccess, int i, int j, int k, AxisAlignedBB box)
    {
        int iMetadata = blockAccess.getBlockMetadata(i, j, k);
        
        int iBlockDir = getDirection(iMetadata);
        boolean bUpsideDown = getIsUpsideDown(iMetadata);
        int iFacing = -1;
        
        box.minY = 0.5F;
        box.maxY = 1.0F;

        if ( bUpsideDown )
        {
        	box.minY = 0.0F;
        	box.maxY = 0.5F;
        }

        box.minX = 0.0F;
        box.minZ = 0.5F;
        
        box.maxX = 0.5F;
        box.maxZ = 1.0F;
        
        if (iBlockDir == 0)
        {
            int iNeighborBlockID = blockAccess.getBlockId(i - 1, j, k);
            int iNeighborMetadata = blockAccess.getBlockMetadata(i - 1, j, k);

            if (isStairBlock(iNeighborBlockID) && (iMetadata & 4) == (iNeighborMetadata & 4))
            {
            	int iNeighborDir = iNeighborMetadata & 3;

                if (iNeighborDir == 3 && !this.isStairBlockWithMetadata(blockAccess, i, j, k - 1, iMetadata))
                {
                	box.minZ = 0.0F;
                	box.maxZ = 0.5F;
                    iFacing = 2;
                }
                else if (iNeighborDir == 2 && !this.isStairBlockWithMetadata(blockAccess, i, j, k + 1, iMetadata))
                {
                	box.minZ = 0.5F;
                	box.maxZ = 1.0F;
                    iFacing = 3;
                }
            }
        }
        else if (iBlockDir == 1)
        {
        	int iNeighborBlockID = blockAccess.getBlockId(i + 1, j, k);
        	int iNeighborMetadata = blockAccess.getBlockMetadata(i + 1, j, k);

            if (isStairBlock(iNeighborBlockID) && (iMetadata & 4) == (iNeighborMetadata & 4))
            {
            	box.minX = 0.5F;
            	box.maxX = 1.0F;
                
                int iNeighborDir = iNeighborMetadata & 3;

                if (iNeighborDir == 3 && !this.isStairBlockWithMetadata(blockAccess, i, j, k - 1, iMetadata))
                {
                	box.minZ = 0.0F;
                	box.maxZ = 0.5F;
                    iFacing = 2;
                }
                else if (iNeighborDir == 2 && !this.isStairBlockWithMetadata(blockAccess, i, j, k + 1, iMetadata))
                {
                	box.minZ = 0.5F;
                	box.maxZ = 1.0F;
                    iFacing = 3;
                }
            }
        }
        else if (iBlockDir == 2)
        {
        	int iNeighborBlockID = blockAccess.getBlockId(i, j, k - 1);
        	int iNeighborMetadata = blockAccess.getBlockMetadata(i, j, k - 1);

            if (isStairBlock(iNeighborBlockID) && (iMetadata & 4) == (iNeighborMetadata & 4))
            {
            	box.minZ = 0.0F;
            	box.maxZ = 0.5F;
                
                int iNeighborDir = iNeighborMetadata & 3;

                if (iNeighborDir == 1 && !this.isStairBlockWithMetadata(blockAccess, i - 1, j, k, iMetadata))
                {
                    iFacing = 4;
                }
                else if (iNeighborDir == 0 && !this.isStairBlockWithMetadata(blockAccess, i + 1, j, k, iMetadata))
                {
                	box.minX = 0.5F;
                	box.maxX = 1.0F;
                    iFacing = 5;
                }
            }
        }
        else if (iBlockDir == 3)
        {
        	int iNeighborBlockID = blockAccess.getBlockId(i, j, k + 1);
        	int iNeighborMetadata = blockAccess.getBlockMetadata(i, j, k + 1);

            if (isStairBlock(iNeighborBlockID) && (iMetadata & 4) == (iNeighborMetadata & 4))
            {
            	int iNeighborDir = iNeighborMetadata & 3;

                if (iNeighborDir == 1 && !this.isStairBlockWithMetadata(blockAccess, i - 1, j, k, iMetadata))
                {
                    iFacing = 4;
                }
                else if (iNeighborDir == 0 && !this.isStairBlockWithMetadata(blockAccess, i + 1, j, k, iMetadata))
                {
                	box.minX = 0.5F;
                	box.maxX = 1.0F;
                    iFacing = 5;
                }
            }
        }

        return iFacing;
    }

    private boolean hasSecondaryFullSurfaceToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
    {
    	AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB( 0D, 0D, 0D, 1D, 1D, 1D );
    	
    	boolean bHasFullStep = getBoundsForSecondaryPiece(blockAccess, i, j, k, box);
    	
    	if ( bHasFullStep )
    	{
    		if (iFacing == getBoundsForTertiaryPiece(blockAccess, i, j, k, box) )
    		{
    			return true;
    		}
    	}
    		
    	return false;
    }
    
    protected boolean isStairBlock(int iBlockID)
    {
    	Block block = Block.blocksList[iBlockID];
    	
    	if ( block != null )
    	{
    		return block.isStairBlock();
    	}
    	
    	return false;
    }

    protected boolean getIsUpsideDown(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getIsUpsideDown(blockAccess.getBlockMetadata(i, j, k));
    }
    
    protected boolean getIsUpsideDown(int iMetadata)
    {
        return ( iMetadata & 4 ) != 0;
    }
    
    protected int setUpsideDown(int iMetadata)
    {
    	return iMetadata | 4;
    }
    
    protected int setIsUpsideDown(int iMetadata, boolean bUpsideDown)
    {
    	if ( bUpsideDown )
    	{
        	iMetadata |= 4;
    	}
    	else
    	{
        	iMetadata &= ~4;
    	}
    	
    	return iMetadata;
    }
    
    protected void setIsUpsideDown(World world, int i, int j, int k, boolean bUpsideDown)
	{
    	int iMetadata = setIsUpsideDown(world.getBlockMetadata(i, j, k), bUpsideDown);
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
    
    protected void setDirection(World world, int i, int j, int k, int iDirection)
	{
        int iMetadata = setDirection(world.getBlockMetadata(i, j, k), iDirection);

        world.SetBlockMetadataWithNotify( i, j, k, iMetadata, 2 );
	}
    
    protected int setDirection(int iMetadata, int iDirection)
	{
        iMetadata &= ~3;
        
        return iMetadata | iDirection;	
    }
    
    protected int getDirection(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getDirection(blockAccess.getBlockMetadata(i, j, k));
    }
    
    protected int getDirection(int iMetadata)
	{
    	return iMetadata & 3;
	}
    
    protected boolean isStairBlockWithMetadata(IBlockAccess blockAccess, int i, int j, int k, int iMetadata)
    {
        return WorldUtils.isStairBlock(blockAccess, i, j, k) && blockAccess.getBlockMetadata(i, j, k) == iMetadata;
    }

    protected void notifyAllNearbyBlocksFlat(World world, int i, int j, int k)
    {
    	// notify to reshape neighboring stairs and anything that might be connected to them
    	
        world.notifyBlockOfNeighborChange( i - 1, j, k, blockID );
        world.notifyBlockOfNeighborChange( i - 2, j, k, blockID );
        world.notifyBlockOfNeighborChange( i + 1, j, k, blockID );
        world.notifyBlockOfNeighborChange( i + 2, j, k, blockID );
        
        world.notifyBlockOfNeighborChange( i, j, k - 1, blockID );
        world.notifyBlockOfNeighborChange( i, j, k - 2, blockID );
        world.notifyBlockOfNeighborChange( i, j, k + 1, blockID );
        world.notifyBlockOfNeighborChange( i, j, k + 2, blockID );
        
        world.notifyBlockOfNeighborChange( i - 1, j, k - 1, blockID );
        world.notifyBlockOfNeighborChange( i - 1, j, k + 1, blockID );
        world.notifyBlockOfNeighborChange( i + 1, j, k - 1, blockID );
        world.notifyBlockOfNeighborChange( i + 1, j, k + 1, blockID );
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private boolean renderingBase = false;

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderBlockStairs( renderer, i, j, k );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
    	if (!renderingBase && iSide < 2 )
    	{
    		BlockPos myPos = new BlockPos(iNeighborI, iNeighborJ, iNeighborK,
                                          getOppositeFacing(iSide) );
    		
    		if ( getIsUpsideDown(blockAccess, myPos.x, myPos.y, myPos.z) )
    		{
    			if ( iSide == 1 )
    			{
    				return false;
    			}
    		}
    		else
    		{
    			if ( iSide == 0 )
    			{
    				return false;
    			}
    		}
    	}
    	
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderNeighborHalfSlabSide(IBlockAccess blockAccess, int i, int j, int k, int iNeighborSlabSide, boolean bNeighborUpsideDown)
    {
		boolean bUpsideDown = getIsUpsideDown(blockAccess, i, j, k);
		
		if ( bUpsideDown == bNeighborUpsideDown )
		{
			return false;
		}
		
		int iBlockFacing = convertDirectionToFacing(getDirection(blockAccess, i, j, k));
		
		return iNeighborSlabSide != Block.getOppositeFacing(iBlockFacing);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int i, int j, int k, int iNeighborSide)
    {
    	if ( iNeighborSide < 2 )
    	{
    		boolean bUpsideDown = getIsUpsideDown(blockAccess, i, j, k);
    		
    		if ( iNeighborSide == 0 )
    		{
    			return !bUpsideDown;
    		}
    		else // iNeighborSide == 1
    		{
    			return bUpsideDown;
    		}    			
    	}
    	
		int iBlockFacing = convertDirectionToFacing(getDirection(blockAccess, i, j, k));
		
		return iNeighborSide != Block.getOppositeFacing(iBlockFacing);
    }

    @Environment(EnvType.CLIENT)
    private boolean renderBlockStairs( RenderBlocks renderBlocks, int i, int j, int k )
    {
        renderingBase = true;
    	
    	renderBlocks.setRenderBounds(getBoundsFromPoolForBase(
    		renderBlocks.blockAccess, i, j, k) );
        
        renderBlocks.renderStandardBlock( this, i, j, k );

        renderingBase = false;
    	
    	AxisAlignedBB secondaryBox = AxisAlignedBB.getAABBPool().getAABB( 
    		0D, 0D, 0D, 1D, 1D, 1D );
    	
        boolean bIsFullStep = getBoundsForSecondaryPiece(renderBlocks.blockAccess,
                                                         i, j, k, secondaryBox);
        
        renderBlocks.setRenderBounds( secondaryBox );
        
        renderBlocks.renderStandardBlock( this, i, j, k );

    	AxisAlignedBB tertiaryBox = AxisAlignedBB.getAABBPool().getAABB( 
    		0D, 0D, 0D, 1D, 1D, 1D );
    	
    	int iTertiaryFacing = getBoundsForTertiaryPiece(renderBlocks.blockAccess,
                                                        i, j, k, tertiaryBox);
    	
    	if ( iTertiaryFacing >= 0 )
    	{
        	renderBlocks.setRenderBounds( tertiaryBox );
        	
        	renderBlocks.renderStandardBlock( this, i, j, k );
        }

        return true;
    }
}