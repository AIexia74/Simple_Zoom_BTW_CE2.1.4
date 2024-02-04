// FCMOD

package btw.block.blocks.legacy;

import btw.block.BTWBlocks;
import btw.block.blocks.PlanksBlock;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class LegacyCornerBlock extends Block
{
    public final static int NUM_SUBTYPES = 16;
    
	public static final int STONE_TEXTURE_ID = 1;
	public static final int WOOD_TEXTURE_ID = 4;
	
	private static final double CORNER_WIDTH = 0.5D;
	private static final double HALF_CORNER_WIDTH = (CORNER_WIDTH / 2D );
	
	private static final double CORNER_WIDTH_OFFSET = 1F - CORNER_WIDTH;

	public LegacyCornerBlock(int iBlockID)
	{
        super( iBlockID, Material.wood );

        setHardness( 1.5F );
        setAxesEffectiveOn(true);
        setPicksEffectiveOn(true);
        
        setBuoyancy(1F);
        
    	initBlockBounds(
				0.5F - HALF_CORNER_WIDTH, 0.5F - HALF_CORNER_WIDTH, 0.5F - HALF_CORNER_WIDTH,
				0.5F + HALF_CORNER_WIDTH, 0.5F + HALF_CORNER_WIDTH, 0.5F + HALF_CORNER_WIDTH);
    	
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "fcBlockCorner" );
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
    public int idDropped( int iMetadata, Random random, int iFortuneModifier )
    {
		if ( ( iMetadata & 8 ) == 0 ) // is wood
		{
			return BTWItems.woodCornerStubID;
		}
		else
		{
			return BTWBlocks.stoneSidingAndCorner.blockID;
		}		
    }
	
	@Override
	public int damageDropped( int iMetadata )
    {
		if ( ( iMetadata & 8 ) != 0 ) // is stone
		{
			return 1;
		}
		
		return 0;		
    }
    
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
            IBlockAccess blockAccess, int i, int j, int k)
    {
    	double dMinX = 0D;
    	double dMaxX = dMinX + CORNER_WIDTH;
    	
    	double dMinY = 0D;
    	double dMaxY = dMinY + CORNER_WIDTH;
    	
    	double dMinZ = 0D;
    	double dMaxZ = dMinZ + CORNER_WIDTH;
    	
    	if ( isXOffset(blockAccess, i, j, k) )
    	{
    		dMinX += CORNER_WIDTH_OFFSET;
    		dMaxX += CORNER_WIDTH_OFFSET;
    	}
    		
    	if ( isYOffset(blockAccess, i, j, k) )
    	{
    		dMinY += CORNER_WIDTH_OFFSET;
    		dMaxY += CORNER_WIDTH_OFFSET;
    	}
    	
    	if ( isZOffset(blockAccess, i, j, k) )
    	{
    		dMinZ += CORNER_WIDTH_OFFSET;
    		dMaxZ += CORNER_WIDTH_OFFSET;
    	}
    	
    	return AxisAlignedBB.getAABBPool().getAABB( dMinX, dMinY, dMinZ, dMaxX, dMaxY, dMaxZ );    	
	}
    
	private boolean isPlayerClickOffsetOnAxis(float fPlayerClick)
	{
		if ( fPlayerClick > 0F ) // should always be true
		{
			if ( fPlayerClick >= 0.5F )
			{
				return true;
			}
		}
		
		return false;
	}
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
    	boolean bIOffset = false;
    	boolean bJOffset = false;
    	boolean bKOffset = false;
    	
    	if ( iFacing == 0 )
    	{
    		bJOffset = true;

    		bIOffset = isPlayerClickOffsetOnAxis(fClickX);
    		bKOffset = isPlayerClickOffsetOnAxis(fClickZ);;
    	}
    	else if ( iFacing == 1 )
    	{
    		bIOffset = isPlayerClickOffsetOnAxis(fClickX);
    		bKOffset = isPlayerClickOffsetOnAxis(fClickZ);;
    	}
    	else if ( iFacing == 2 )
    	{
    		bKOffset = true;

    		bIOffset = isPlayerClickOffsetOnAxis(fClickX);
    		bJOffset = isPlayerClickOffsetOnAxis(fClickY);;
    	}
    	else if ( iFacing == 3 )
    	{
    		bIOffset = isPlayerClickOffsetOnAxis(fClickX);
    		bJOffset = isPlayerClickOffsetOnAxis(fClickY);;
    	}
    	else if ( iFacing == 4 )
    	{
    		bIOffset = true;

    		bJOffset = isPlayerClickOffsetOnAxis(fClickY);
    		bKOffset = isPlayerClickOffsetOnAxis(fClickZ);;
    	}
    	else if ( iFacing == 5 )
    	{
    		bJOffset = isPlayerClickOffsetOnAxis(fClickY);
    		bKOffset = isPlayerClickOffsetOnAxis(fClickZ);;
    	}
    	
    	return setCornerAlignmentInMetadata(iMetadata, bIOffset, bJOffset, bKOffset);
    }
    
	@Override
    public boolean doesBlockBreakSaw(World world, int i, int j, int k )
    {
		return getIsStone(world, i, j, k);
    }
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return !isYOffset(blockAccess, i, j, k);
	}
	
	@Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		boolean bIOffset = isXOffset(iMetadata);
		boolean bJOffset = isYOffset(iMetadata);
		boolean bKOffset = isZOffset(iMetadata);
		
		if ( bReverse )
		{
			if ( bIOffset )
			{
				if ( bKOffset )
				{
					bIOffset = false;
				}
				else
				{
					bKOffset = true;
				}
			}
			else
			{
				if ( bKOffset )
				{
					bKOffset = false;
				}
				else
				{
					bIOffset = true;
				}
			}
		}
		else
		{
			if ( bIOffset )
			{
				if ( bKOffset )
				{
					bKOffset = false;
				}
				else
				{
					bIOffset = false;
				}
			}
			else
			{
				if ( bKOffset )
				{
					bIOffset = true;
				}
				else
				{
					bKOffset = true;
				}
			}
		}
		
		return setCornerAlignmentInMetadata(iMetadata, bIOffset, bJOffset, bKOffset);
	}

	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{
		int iAlignment = getCornerAlignment(world, i, j, k);
		
		if ( !bReverse )
		{
			iAlignment++;
			
			if ( iAlignment > 7 )
			{
				iAlignment = 0;
			}
		}
		else
		{
			iAlignment--;
			
			if ( iAlignment < 0 )
			{
				iAlignment = 7;				
			}
		}

		setCornerAlignment(world, i, j, k, iAlignment);
		
        return true;
	}	
	
    @Override
    public int getFurnaceBurnTime(int iItemDamage)
    {
    	if ( iItemDamage == 0 )
    	{
    		return PlanksBlock.getFurnaceBurnTimeByWoodType(0) / 8;
    	}
    	
    	return 0;
    }
    
    //------------- Class Specific Methods ------------//
    
    // bit 2 indicates i offset.  bit 1 indicates j offset.  bit 0 indicates k offset
    public int getCornerAlignment(IBlockAccess iBlockAccess, int i, int j, int k)
	{
    	return ( ( iBlockAccess.getBlockMetadata( i, j, k ) ) & 7 );
	}
    
    public void setCornerAlignment(World world, int i, int j, int k, int iAlignment)
    {
    	int iMetaData = world.getBlockMetadata( i, j, k ) & 8; // filter out the old alignment
    	
    	iMetaData |= iAlignment;
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetaData );
    }
    
    public void setCornerAlignment(World world, int i, int j, int k,
								   boolean bIAligned, boolean bJAligned, boolean bKAligned)
    {
    	int iAlignment = 0;
    	
    	if ( bIAligned )
    	{
    		iAlignment |= 4;
    	}
    	
    	if ( bJAligned )
    	{
    		iAlignment |= 2;
    	}
    	
    	if ( bKAligned )
    	{
    		iAlignment |= 1;
    	}
    	
    	setCornerAlignment(world, i, j, k, iAlignment);
    }
    
    public int setCornerAlignmentInMetadata(int iMetadata, int iAlignment)
    {
    	iMetadata = iMetadata & 8; // filter out the old alignment
    	
    	iMetadata |= iAlignment;
    	
        return iMetadata;
    }
    
    public int setCornerAlignmentInMetadata(int iMetadata, boolean bIAligned, boolean bJAligned, boolean bKAligned)
    {
    	int iAlignment = 0;
    	
    	if ( bIAligned )
    	{
    		iAlignment |= 4;
    	}
    	
    	if ( bJAligned )
    	{
    		iAlignment |= 2;
    	}
    	
    	if ( bKAligned )
    	{
    		iAlignment |= 1;
    	}
    	
    	return setCornerAlignmentInMetadata(iMetadata, iAlignment);
    }
        
    public boolean isXOffset(IBlockAccess iBlockAccess, int i, int j, int k)
	{
    	return isXOffset(iBlockAccess.getBlockMetadata(i, j, k));
	}
    
    public boolean isXOffset(int iMetadata)
	{
    	return ( iMetadata & 4 ) > 0;
	}
    
    public boolean isYOffset(IBlockAccess iBlockAccess, int i, int j, int k)
	{
    	return isYOffset(iBlockAccess.getBlockMetadata(i, j, k));
	}
    
    public boolean isYOffset(int iMetadata)
	{
    	return ( iMetadata & 2 ) > 0;
	}
    
    public boolean isZOffset(IBlockAccess iBlockAccess, int i, int j, int k)
	{
    	return isZOffset(iBlockAccess.getBlockMetadata(i, j, k));
	}
    
    public boolean isZOffset(int iMetadata)
	{
    	return ( iMetadata & 1 ) > 0;
	}
    
    public boolean getIsStone(IBlockAccess iBlockAccess, int i, int j, int k)
    {
    	return ( ( iBlockAccess.getBlockMetadata( i, j, k ) ) & 8 ) > 0;
    }
    
    public void setIsStone(World world, int i, int j, int k, boolean bStone)
    {
    	int iMetaData = world.getBlockMetadata( i, j, k ) & 7; // filter out the old alignment
    	
    	if ( bStone )
    	{
    		iMetaData |= 8;
    	}
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetaData );
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    public Icon iconWood;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "stone" );
		iconWood = register.registerIcon("wood");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	if ( ( iMetadata & 8 ) > 0 )
    	{
    		return blockIcon;
    	}
    	else
    	{
    		return iconWood;
    	}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return idDropped( world.getBlockMetadata( i, j, k ), world.rand, 0 );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }
}