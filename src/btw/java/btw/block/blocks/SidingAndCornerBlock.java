//FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.render.util.RenderUtils;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class SidingAndCornerBlock extends Block
{
	protected static final double SIDING_HEIGHT = 0.5D;
	
	protected static final double CORNER_WIDTH = 0.5D;
	protected static final double CORNER_WIDTH_OFFSET = (1D - CORNER_WIDTH);
	
	String textureName;
	
	protected SidingAndCornerBlock(int iBlockID, Material material, String sTextureName, float fHardness, float fResistance, StepSound stepSound, String name )
	{
        super( iBlockID, material );

        setHardness( fHardness );
        setResistance( fResistance );
        
        setStepSound( stepSound );
        setUnlocalizedName( name );

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
    public int damageDropped( int iMetadata )
    {
		return ( iMetadata & 1 );
    }
    
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
            IBlockAccess blockAccess, int i, int j, int k)
    {
        int iFacing = getFacing(blockAccess, i, j, k);
        
        if ( !getIsCorner(blockAccess, i, j, k) )
        {
        	AxisAlignedBB sidingBox = AxisAlignedBB.getAABBPool().getAABB(
					0D, 0D, 0D, 1D, SIDING_HEIGHT, 1D);
        	
        	sidingBox.tiltToFacingAlongY(iFacing);
        	
        	return sidingBox;
        }
        else
        {        
        	AxisAlignedBB cornerBox = AxisAlignedBB.getAABBPool().getAABB(
					0D, 0D, 0D, CORNER_WIDTH, CORNER_WIDTH, CORNER_WIDTH);
        	
	    	if ( isCornerFacingXOffset(iFacing) )
	    	{
	    		cornerBox.minX += CORNER_WIDTH_OFFSET;
	    		cornerBox.maxX += CORNER_WIDTH_OFFSET;
	    	}
	    		
	    	if ( isCornerFacingYOffset(iFacing) )
	    	{
	    		cornerBox.minY += CORNER_WIDTH_OFFSET;
	    		cornerBox.maxY += CORNER_WIDTH_OFFSET;
	    	}
	    	
	    	if ( isCornerFacingZOffset(iFacing) )
	    	{
	    		cornerBox.minZ += CORNER_WIDTH_OFFSET;
	    		cornerBox.maxZ += CORNER_WIDTH_OFFSET;
	    	}

	    	return cornerBox;
        }
    }
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
		if ( !getIsCorner(iMetadata) )
		{
	        int iSlabFacing = iFacing;
	        
	        return setFacing(iMetadata, iSlabFacing);
		}
		else
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
	    	
	        return setCornerFacingInMetadata(iMetadata, bIOffset, bJOffset, bKOffset);
		}
    }
    
    @Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
    	if ( !getIsCorner(blockAccess, i, j, k) )
    	{
	    	int iBlockFacing = getFacing(blockAccess, i, j, k);
	    	
	    	return iFacing == Block.getOppositeFacing(iBlockFacing);
    	}
    	
    	return false;
	}
    
    @Override
    public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k)
    {
    	if ( super.canGroundCoverRestOnBlock(world, i, j, k) )
    	{
    		return true;
    	}
    	else if ( !getIsCorner(world, i, j, k) )
    	{
            int iFacing = getFacing(world, i, j, k);
            
            if ( iFacing == 1 )
            {
            	return true;
            }            
    	}
    	
    	return false;
    }
    
    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k)
    {
    	if ( !getIsCorner(blockAccess, i, j, k) )
    	{
            int iFacing = getFacing(blockAccess, i, j, k);
            
            if ( iFacing == 1 )
            {
            	return -0.5F;
            }            
    	}
    	
    	return 0F;
    }
    
	@Override
	public int getFacing(int iMetadata)
	{
    	return ( iMetadata >> 1 );
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
    	iMetadata &= 1; // filter out the previous facing
    	
    	iMetadata |= ( iFacing << 1 );
    	
		return iMetadata;
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{	
		int iFacing = getFacing(blockAccess, i, j, k);
		
		if ( !getIsCorner(blockAccess, i, j, k) )
		{
			return iFacing != 0;
		}
		else
		{		
			return !isCornerFacingYOffset(iFacing);
		}
	}
	
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		if ( !getIsCorner(blockAccess, i, j, k) )
		{
			int iFacing = getFacing(blockAccess, i, j, k);
			
			if ( iFacing > 1 )
			{
				// sideways facing blocks can transmit
				
				return true;
			}
		}

		return false;
	}
	
	@Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		int iFacing = getFacing(iMetadata);
		
		if ( (iMetadata & 1 ) == 0 ) // corner test
		{
			return super.rotateMetadataAroundJAxis(iMetadata, bReverse);
		}
		else
		{
			boolean bIOffset = isCornerFacingXOffset(iFacing);
			boolean bJOffset = isCornerFacingYOffset(iFacing);
			boolean bKOffset = isCornerFacingZOffset(iFacing);
			
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
			
			return setCornerFacingInMetadata(iMetadata, bIOffset, bJOffset, bKOffset);
		}
	}

	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{		
		int iFacing = getFacing(world, i, j, k);
		
		if ( !getIsCorner(world, i, j, k) )
		{		
			iFacing = Block.cycleFacing(iFacing, bReverse);
	
		}
		else
		{
			if ( !bReverse )
			{
				iFacing++;
				
				if ( iFacing > 7 )
				{
					iFacing = 0;
				}
			}
			else
			{
				iFacing--;
				
				if ( iFacing < 0 )
				{
					iFacing = 7;				
				}
			}
		}
		
		setFacing(world, i, j, k, iFacing);
        
        return true;
	}
	
	@Override
    public float mobSpawnOnVerticalOffset(World world, int i, int j, int k)
    {
		int iFacing = getFacing(world, i, j, k);
		
		if ( getIsCorner(world, i, j, k) )
		{
			if ( !isCornerFacingYOffset(iFacing) )
        	{
				return -0.5F;			
        	}
		}
		else if ( iFacing == 1 )
		{
			return -0.5F;			
		}
		
		return 0F;
    }
	
    //------------- Class Specific Methods ------------//

	public boolean getIsCorner(IBlockAccess iBlockAccess, int i, int j, int k)
	{
		return getIsCorner(iBlockAccess.getBlockMetadata(i, j, k));
	}
   
	public boolean getIsCorner(int iMetadata)
	{
		return ( iMetadata & 1 ) > 0;
	}
   
	public boolean isCornerFacingXOffset(int iFacing)
	{
		return ( iFacing & 4 ) > 0;
	}
   
	public boolean isCornerFacingYOffset(int iFacing)
	{
		return ( iFacing & 2 ) > 0;
	}
   
	public boolean isCornerFacingZOffset(int iFacing)
	{
		return ( iFacing & 1 ) > 0;
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
    
    public void setCornerFacing(World world, int i, int j, int k,
								boolean bIAligned, boolean bJAligned, boolean bKAligned)
    {
    	int iFacing = 0;
    	
    	if ( bIAligned )
    	{
    		iFacing |= 4;
    	}
    	
    	if ( bJAligned )
    	{
    		iFacing |= 2;
    	}
    	
    	if ( bKAligned )
    	{
    		iFacing |= 1;
    	}
    	
    	setFacing(world, i, j, k, iFacing);
    }
    
    public int setCornerFacingInMetadata(int iMetadata, boolean bIAligned, boolean bJAligned, boolean bKAligned)
    {
    	int iFacing = 0;
    	
    	if ( bIAligned )
    	{
    		iFacing |= 4;
    	}
    	
    	if ( bJAligned )
    	{
    		iFacing |= 2;
    	}
    	
    	if ( bKAligned )
    	{
    		iFacing |= 1;
    	}
    	
    	return setFacing(iMetadata, iFacing);
    }
    
    /**
     * iAxis = 0 is i axis, 1 is j, 2 is k
     */
    public static int getCornerAlignmentOffsetAlongAxis(int iCornerFacing, int iAxis)
    {
    	if ( ( iCornerFacing & ( 4 >> iAxis ) ) > 0 )
    	{
    		return 1;
    	}
    	
    	return -1;
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
    public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list )
    {
        list.add( new ItemStack( iBlockID, 1, 0 ) );
        list.add( new ItemStack( iBlockID, 1, 1 ) );        
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

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	if ( blockID == BTWItems.woodSidingStubID ||
			( ( iItemDamage & 1 ) == 0 && blockID != BTWItems.woodCornerStubID) ) // corner test
    	{
    		renderBlocks.setRenderBounds(0F, 0F, 0F, SIDING_HEIGHT, 1F, 1F);
    	}
    	else
    	{
    		renderBlocks.setRenderBounds( 
        		0.5F - (CORNER_WIDTH * 0.5F ), 0.5F - (CORNER_WIDTH * 0.5F ), 0.5F - (CORNER_WIDTH * 0.5F ),
        		0.5F + (CORNER_WIDTH * 0.5F ), 0.5F + (CORNER_WIDTH * 0.5F ), 0.5F + (CORNER_WIDTH * 0.5F ));
    	}
    	
    	if ( blockID == BTWItems.woodSidingStubID ||
			blockID == BTWItems.woodCornerStubID)
    	{
    		Icon woodTexture;
    		
	        switch ( iItemDamage )
	        {
	            case 1: // spruce
	            	
	            	woodTexture = BTWBlocks.spruceWoodSidingAndCorner.blockIcon;
	            	
	            	break;

	            case 2: // birch
	            	
	            	woodTexture = BTWBlocks.birchWoodSidingAndCorner.blockIcon;

	            	break;

	            case 3: // jungle
	            	
	            	woodTexture = BTWBlocks.jungleWoodSidingAndCorner.blockIcon;
	                
	            	break;

	            case 4: // blood
	            	
	            	woodTexture = BTWBlocks.bloodWoodSidingAndCorner.blockIcon;
	                
	            	break;

	            default: // oak
	            	
	            	woodTexture = BTWBlocks.oakWoodSidingAndCorner.blockIcon;
	        }
	        
    		RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, woodTexture);
    	}
    	else
    	{    		
    		RenderUtils.renderInvBlockWithMetadata(renderBlocks, this, -0.5F, -0.5F, -0.5F, 0);
    	}
    }    
}