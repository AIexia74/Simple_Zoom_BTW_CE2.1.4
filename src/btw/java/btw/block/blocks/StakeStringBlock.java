// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.item.util.ItemUtils;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class StakeStringBlock extends Block
{
	static public final double HEIGHT = (0.25D / 16D );
	static public final double HALF_HEIGHT = (HEIGHT / 2D );
	
	static public final double SELECTION_BOX_HEIGHT = (1D / 16D );
	static public final double SELECTION_BOX_HALF_HEIGHT = (SELECTION_BOX_HEIGHT / 2D );

	static private final long MIN_TIME_BETWEEN_LENGTH_DISPLAYS = (MiscUtils.TICKS_PER_SECOND * 10 );
	
	static long timeOfLastLengthDisplay = 0;
	static int lengthOfLastLengthDisplay = 0;
	
    public StakeStringBlock(int iBlockID )
    {
        super( iBlockID, Material.circuits );
    	
        setAxesEffectiveOn(true);

		setFireProperties(Flammability.EXTREME);
		
        setStepSound( soundClothFootstep );
        
        setUnlocalizedName( "fcBlockStakeString" );        
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
    public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
        MovingObjectPosition rayIntersectPos = null;
        MovingObjectPosition possibleIntersectPoints[] = new MovingObjectPosition[8];
        int iCurrentIntersectIndex = 0;
        
        for ( int iAxis = 0; iAxis < 3; iAxis++ )
        {
        	if ( getExtendsAlongAxis(world, i, j, k, iAxis) )
        	{
        		Vec3 boxMin = Vec3.createVectorHelper( 0, 0, 0 );
        		Vec3 boxMax = Vec3.createVectorHelper( 0, 0, 0 );
        		
        		getBlockBoundsForAxis(iAxis, boxMin, boxMax, SELECTION_BOX_HALF_HEIGHT);
        		
    	    	possibleIntersectPoints[iCurrentIntersectIndex] = MiscUtils.rayTraceWithBox(world, i, j, k, boxMin, boxMax, startRay, endRay);
    	    	
    	    	if ( possibleIntersectPoints[iCurrentIntersectIndex] != null )
    	    	{
    	    		iCurrentIntersectIndex++;
    	    	}
        	}
        }
        
        if ( iCurrentIntersectIndex > 0 )
        {
            // scan through the list of intersect points we have built, and check for the first intersection
            
	        iCurrentIntersectIndex--;
	        
	        double dMaxDistance = 0D;
	        
	        for ( ; iCurrentIntersectIndex >= 0; iCurrentIntersectIndex-- )
	        {
	            double dCurrentIntersectDistance = possibleIntersectPoints[iCurrentIntersectIndex].hitVec.squareDistanceTo( endRay );

	            if ( dCurrentIntersectDistance  > dMaxDistance )
	            {
	            	rayIntersectPos = possibleIntersectPoints[iCurrentIntersectIndex];
	            	dMaxDistance = dCurrentIntersectDistance ;
	            }
	        }
        }
        
        return rayIntersectPos;    	
    }
    
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool( World world, int i, int j, int k )
    {
        return null;
    }
    
	@Override
    public int idDropped( int iMetadata, Random random, int iFortuneModifier )
    {
        return Item.silk.itemID;
    }
	
	@Override
    public void dropBlockAsItemWithChance( World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier )
    {
        if ( !world.isRemote )
        {
	        for ( int iAxis = 0; iAxis < 3; iAxis++ )
	        {
	        	if ( getExtendsAlongAxisFromMetadata(iMetadata, iAxis) )
	        	{
	        		ItemUtils.dropSingleItemAsIfBlockHarvested(world, i, j, k, idDropped(iMetadata, world.rand, iFortuneModifier), damageDropped(iMetadata));
	        	}        	
	        }
        }
    }
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
		validateState(world, i, j, k);
    }
    
	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, 
    	int iFacing, float fXClick, float fYClick, float fZClick )
    {		
		int iLength = computeStringLength(world, i, j, k);
		double dInvertedLength = 64D - iLength;
		
		if ( !world.isRemote )
		{
	        float fPitch = (float)Math.pow( 2D, ( dInvertedLength - 32 ) / 32D );
	        
	        world.playSoundEffect( i + 0.5D, j + 0.5D, k + 0.5D, 
	        		"note.bass", 3F, fPitch - 0F );	        		
		}
		else
		{
	        world.spawnParticle( "note", (double)i + 0.5D, (double)j + 1.2D, (double)k + 0.5D, 
	        	dInvertedLength / 64D, 0D, 0D );

	    	long lCurrentTime = world.getWorldTime();
	    	long lDeltaTime = lCurrentTime - timeOfLastLengthDisplay;
	    	
	    	if ( lDeltaTime < 0 || lDeltaTime >= MIN_TIME_BETWEEN_LENGTH_DISPLAYS ||
				 lengthOfLastLengthDisplay != iLength )
    		{	    		
	    		player.addChatMessage( "Sounds like " + ( iLength + 1 ) + "." );

				timeOfLastLengthDisplay = lCurrentTime;
				lengthOfLastLengthDisplay = iLength;
    		}
		}			
		
		return true;
    }
	
    @Override
    public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k)
    {
    	return world.doesBlockHaveSolidTopSurface( i, j - 1, k );
    }
    
    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return -1F;        
    }
    
	//------------- Class Specific Methods ------------//
	
	public void setExtendsAlongAxis(World world, int i, int j, int k, int iAxis, boolean bExtends)
	{
		setExtendsAlongAxis(world, i, j, k, iAxis, bExtends, true);
	}
	
	public void setExtendsAlongAxis(World world, int i, int j, int k, int iAxis, boolean bExtends, boolean bNotify)
	{
		int iMetadata = world.getBlockMetadata( i, j, k ) & (~(1 << iAxis)); // filter out old value
		
		if ( bExtends )
		{
			iMetadata |= 1 << iAxis;
		}
		
		if ( bNotify )
		{
			world.setBlockMetadataWithNotify( i, j, k, iMetadata );
		}
		else
		{
			world.setBlockMetadataWithClient( i, j, k, iMetadata );
		}
	}
	
	public void setExtendsAlongFacing(World world, int i, int j, int k, int iFacing, boolean bExtends)
	{
		setExtendsAlongAxis(world, i, j, k, convertFacingToAxis(iFacing), bExtends);
	}
	
	public void setExtendsAlongFacing(World world, int i, int j, int k, int iFacing, boolean bExtends, boolean bNotify)
	{
		setExtendsAlongAxis(world, i, j, k, convertFacingToAxis(iFacing), bExtends, bNotify);
	}
	
	public boolean getExtendsAlongAxis(IBlockAccess blockAccess, int i, int j, int k, int iAxis)
	{
		return getExtendsAlongAxisFromMetadata(blockAccess.getBlockMetadata(i, j, k), iAxis);
	}
	
	public boolean getExtendsAlongAxisFromMetadata(int iMetadata, int iAxis)
	{
		return ( iMetadata & ( 1 << iAxis ) ) > 0;
	}
	
	public boolean getExtendsAlongFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
	{
		return getExtendsAlongAxis(blockAccess, i, j, k, convertFacingToAxis(iFacing));
	}
	
	public boolean getExtendsAlongOtherFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
	{
		return getExtendsAlongOtherAxis(blockAccess, i, j, k, convertFacingToAxis(iFacing));
	}
	
	public boolean getExtendsAlongOtherAxis(IBlockAccess blockAccess, int i, int j, int k, int iAxis)
	{
		return getExtendsAlongOtherAxisFromMetadata(blockAccess.getBlockMetadata(i, j, k), iAxis);
	}
	
	public boolean getExtendsAlongOtherAxisFromMetadata(int iMetadata, int iAxis)
	{
		// Filter out the axis being queried
		
		iMetadata &= ~( 1 << iAxis );
		
		return ( iMetadata & 7 ) != 0;		
	}
	
	static public int convertFacingToAxis(int iFacing)
	{
		if ( iFacing == 4 || iFacing == 5 )
		{
			return 0;
		}
		else if ( iFacing == 0 || iFacing == 1 )
		{
			return 1;
		}
		else
		{
			return 2;
		}
	}
	
	private void getBlockBoundsForAxis(int iAxis, Vec3 min, Vec3 max, double dHalfHeight)
	{
		if ( iAxis == 0 )
		{
			min.setComponents( 0F, 0.5F - dHalfHeight, 0.5F - dHalfHeight );
			max.setComponents( 1F, 0.5F + dHalfHeight, 0.5F + dHalfHeight );
		}
		else if ( iAxis == 1 )
		{
			min.setComponents( 0.5F - dHalfHeight, 0F, 0.5F - dHalfHeight ); 
			max.setComponents( 0.5F + dHalfHeight, 1F, 0.5F + dHalfHeight );
		}
		else // 2
		{
			min.setComponents( 0.5F - dHalfHeight, 0.5F - dHalfHeight, 0F ); 
    		max.setComponents( 0.5F + dHalfHeight, 0.5F + dHalfHeight, 1F );
		}
	}
	
	public void validateState(World world, int i, int j, int k)
	{
		int iValidAxisCount = 0;
		
		for ( int iTempAxis = 0; iTempAxis < 3; iTempAxis++ )
		{
			if ( getExtendsAlongAxis(world, i, j, k, iTempAxis) )
			{
				if ( hasValidAttachmentPointsAlongAxis(world, i, j, k, iTempAxis) )
				{
					iValidAxisCount++;
				}
				else
				{
					setExtendsAlongAxis(world, i, j, k, iTempAxis, false);
					
	        		ItemUtils.dropSingleItemAsIfBlockHarvested(world, i, j, k, Item.silk.itemID, 0);
				}
			}
		}
		
		if ( iValidAxisCount <= 0 )
		{
			// we no longer have any valid axis, destroy the block
			
			world.setBlockWithNotify( i, j, k, 0 );
		}
	}
	
	private boolean hasValidAttachmentPointsAlongAxis(World world, int i, int j, int k, int iAxis)
	{
		int iFacing1;
		int iFacing2;
		
		switch ( iAxis )
		{
			case 0: // i
				
				iFacing1 = 4;
				iFacing2 = 5;
				
				break;
				
			case 1: // j
				
				iFacing1 = 0;
				iFacing2 = 1;
				
				break;
				
			default: // 2 k
				
				iFacing1 = 2;
				iFacing2 = 3;
				
				break;				
		}
		
		return hasValidAttachmentPointToFacing(world, i, j, k, iFacing1) && hasValidAttachmentPointToFacing(world, i, j, k, iFacing2);
	}
	
	private boolean hasValidAttachmentPointToFacing(World world, int i, int j, int k, int iFacing)
	{
		BlockPos targetPos = new BlockPos( i, j, k );
		
		targetPos.addFacingAsOffset(iFacing);
		
		int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
		
		if ( iTargetBlockID == blockID )
		{
			if ( getExtendsAlongFacing(world, targetPos.x, targetPos.y, targetPos.z, iFacing) )
			{
				return true;
			}
		}
		else if ( iTargetBlockID == BTWBlocks.stake.blockID )
		{
			return true;
		}
		
		return false;
	}
	
	protected int computeStringLength(World world, int i, int j, int k)
	{
		int iLength = 0;
		
		for ( int iAxis = 0; iAxis < 3; iAxis++ )
		{
			int iAxisLength = computeStringLengthAlongAxis(world, i, j, k, iAxis);
			
			if ( iAxisLength > iLength )
			{
				iLength = iAxisLength;
			}
		}
		
		return iLength;
	}
	
	protected int computeStringLengthAlongAxis(World world, int i, int j, int k, int iAxis)
	{
		int iLength = 0;
		
		if ( getExtendsAlongAxis(world, i, j, k, iAxis) )
		{
			int iTempFacing = getFirstFacingForAxis(iAxis);
			
			iLength = computeStringLengthToFacing(world, i, j, k, iTempFacing);
			
			iTempFacing = Block.getOppositeFacing(iTempFacing);
			
			iLength += computeStringLengthToFacing(world, i, j, k, iTempFacing);
			
			iLength++; // for the block itself			
		}
		
		return iLength;
	}
	
	protected int computeStringLengthToFacing(World world, int i, int j, int k, int iFacing)
	{
		int iLength = 0;
		
		BlockPos tempPos = new BlockPos( i, j, k, iFacing );
		
		while (world.blockExists(tempPos.x, tempPos.y, tempPos.z) &&
			   world.getBlockId(tempPos.x, tempPos.y, tempPos.z) == blockID )
		{
			iLength++;
			
			tempPos.addFacingAsOffset(iFacing);
		}
		
		return iLength;
	}
	
	protected int getFirstFacingForAxis(int iAxis)
	{
		if ( iAxis == 0 )
		{
			return 4;
		}
		else if ( iAxis == 1 )
		{
			return 0;
		}
		else // iAxis == 2
		{
			return 2;
		}
	}
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		BlockPos myPos = new BlockPos(iNeighborI, iNeighborJ, iNeighborK,
                                      getOppositeFacing(iSide) );
		
		int iMetadata = blockAccess.getBlockMetadata(myPos.x, myPos.y, myPos.z);
		int iAxis = convertFacingToAxis(iSide);
	
		if ( getExtendsAlongAxisFromMetadata(iMetadata, iAxis) )
		{
			return getExtendsAlongOtherAxisFromMetadata(iMetadata, iAxis);
		}
		
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, int i, int j, int k )
    {
		double minXBox = (double)i + 0.5D - SELECTION_BOX_HALF_HEIGHT;
		double minYBox = (double)j + 0.5D - SELECTION_BOX_HALF_HEIGHT;
		double minZBox = (double)k + 0.5D - SELECTION_BOX_HALF_HEIGHT;
		double maxXBox = (double)i + 0.5D + SELECTION_BOX_HALF_HEIGHT;
		double maxYBox = (double)j + 0.5D + SELECTION_BOX_HALF_HEIGHT;
		double maxZBox = (double)k + 0.5D + SELECTION_BOX_HALF_HEIGHT;
		
		if ( getExtendsAlongAxis(world, i, j, k, 0) )
		{
			minXBox = (double)i;
			maxXBox = (double)i + 1D;
		}
		
		if ( getExtendsAlongAxis(world, i, j, k, 1) )
		{
			minYBox = (double)j;
			maxYBox = (double)j + 1D;
		}
		
		if ( getExtendsAlongAxis(world, i, j, k, 2) )
		{
			minZBox = (double)k;
			maxZBox = (double)k + 1D;
		}
		
        return AxisAlignedBB.getAABBPool().getAABB( minXBox, minYBox, minZBox, maxXBox, maxYBox, maxZBox );
    }

    @Environment(EnvType.CLIENT)
    private void setRenderBoundsForAxis(RenderBlocks renderBlocks, int iAxis)
	{
    	Vec3 min = Vec3.createVectorHelper( 0, 0, 0 );
    	Vec3 max = Vec3.createVectorHelper( 0, 0, 0 );
    	
    	getBlockBoundsForAxis(iAxis, min, max, HALF_HEIGHT);
    	
    	renderBlocks.setRenderBounds( (float)min.xCoord, (float)min.yCoord, (float)min.zCoord, (float)max.xCoord, (float)max.yCoord, (float)max.zCoord );    	
	}

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return idDropped( world.getBlockMetadata( i, j, k ), world.rand, 0 );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderBlocks.blockAccess;
    	
    	for ( int iAxis = 0; iAxis < 3; iAxis++ )
    	{
    		if ( getExtendsAlongAxis(blockAccess, i, j, k, iAxis) )
    		{
    			setRenderBoundsForAxis(renderBlocks, iAxis);
    			
    			renderBlocks.renderStandardBlock( this, i, j, k );
    		}
    	}
    	
    	return true;
    }
}
