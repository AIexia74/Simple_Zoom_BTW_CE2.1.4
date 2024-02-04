// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.client.render.util.RenderUtils;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class StakeBlock extends Block
{
	private final double blockHeight = (10D / 16D );
	private final double blockWidth = (4D / 16D );
	private final double blockHalfWidth = (blockWidth / 2D );
	
    public StakeBlock(int iBlockID )
    {
        super( iBlockID, Material.wood );

        setHardness( 2F );
        setResistance( 5F );    	
        setAxesEffectiveOn(true);

		setFireProperties(Flammability.PLANKS);
        
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "fcBlockStake" );        
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
    public boolean canPlaceBlockAt(World world, int i, int j, int k )
    {
    	for ( int iFacing = 0; iFacing < 6; iFacing++ )
    	{
    		BlockPos targetPos = new BlockPos( i, j, k, iFacing );
    		
    		if ( WorldUtils.doesBlockHaveCenterHardpointToFacing(world, targetPos.x, targetPos.y, targetPos.z,
																 Block.getOppositeFacing(iFacing)) )
    		{
    			return true;
    		}
    	}
    	
    	return false;    	
    }
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
		BlockPos targetPos = new BlockPos( i, j, k, Block.getOppositeFacing(iFacing) );
		
		if ( !WorldUtils.doesBlockHaveCenterHardpointToFacing(world, targetPos.x, targetPos.y, targetPos.z, iFacing) )
		{
			iFacing = findValidFacing(world, i, j, k);
		}		
		
        return setFacing(iMetadata, iFacing);
    }
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
        AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(
				0.5D - blockHalfWidth, 0D, 0.5D - blockHalfWidth,
				0.5D + blockHalfWidth, blockHeight, 0.5D + blockHalfWidth);
        
        box.tiltToFacingAlongY(getFacing(blockAccess, i, j, k));
        
        return box;
    }
	
	@Override
    public int idDropped( int iMetaData, Random random, int iFortuneModifier )
    {
        return BTWItems.stake.itemID;
    }

	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
    	// pop the block off if it no longer has a valid anchor point
    	
    	int iFacing = getFacing(world, i, j, k);
    	
		BlockPos anchorPos = new BlockPos( i, j, k, Block.getOppositeFacing(iFacing) );
		
		if ( !WorldUtils.doesBlockHaveCenterHardpointToFacing(world, anchorPos.x, anchorPos.y, anchorPos.z, iFacing) )
    	{
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            world.setBlockWithNotify( i, j, k, 0 );
    	}    		
    }
    
	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
    	ItemStack equippedItem = player.getCurrentEquippedItem();
    	
		int iTargetFacing = Block.getOppositeFacing(MiscUtils.convertPlacingEntityOrientationToBlockFacingReversed(player));
    	
		if ( hasConnectedStringToFacing(world, i, j, k, iTargetFacing) )
		{
			// remove the connected string and drop it at the player's feet

			if ( !world.isRemote )
			{
				int iStringCount = clearStringToFacingNoDrop(world, i, j, k, iTargetFacing);
				
				if ( iStringCount > 0 )
				{
					ItemUtils.dropStackAsIfBlockHarvested(world, i, j, k, new ItemStack(Item.silk.itemID, iStringCount, 0 ));
				}
				
	            world.playSoundAtEntity( player, "random.bow", 0.25F, world.rand.nextFloat() * 0.4F + 1.2F );
			}
			
			return true;
		}
		else if ( equippedItem != null && equippedItem.getItem().itemID == Item.silk.itemID )
    	{
			int iStringStackSize = equippedItem.stackSize;
			
			if ( iStringStackSize > 0 )
			{
				int iStakeFacing = getFacing(world, i, j, k);
				
				// scan in the chosen direction for another stake

				int iDistanceToOtherStake = checkForValidConnectingStakeToFacing(world, i, j, k, iTargetFacing, iStringStackSize);
				
				if ( iDistanceToOtherStake <= 0 )
				{
					// check alternate facings as we didn't find anything in the primary
					
				    int iYawOctant = MathHelper.floor_double( (double)( ( player.rotationYaw * 8F ) / 360F ) ) & 7;
				    
				    if ( iYawOctant >= 0 && iYawOctant <= 3 )
				    {
				    	iTargetFacing = 4;
				    }
				    else
				    {
						iTargetFacing = 5;
				    }
				    
					iDistanceToOtherStake = checkForValidConnectingStakeToFacing(world, i, j, k, iTargetFacing, iStringStackSize);
					
					if ( iDistanceToOtherStake <= 0 )
					{
					    if ( iYawOctant >= 2 && iYawOctant <= 5 )
					    {
					    	iTargetFacing = 2;
					    }
					    else
					    {
					    	iTargetFacing = 3;
					    }
					}
					
					iDistanceToOtherStake = checkForValidConnectingStakeToFacing(world, i, j, k, iTargetFacing, iStringStackSize);
					
					// scan alternate facings
					
					if ( iDistanceToOtherStake <= 0 )
					{
						if ( player.rotationPitch > 0 )
						{
					    	iTargetFacing = 0;
						}
						else
						{
					    	iTargetFacing = 1;
						}
						
						iDistanceToOtherStake = checkForValidConnectingStakeToFacing(world, i, j, k, iTargetFacing, iStringStackSize);
					}
				}
				
				if ( iDistanceToOtherStake > 0 )
				{
					if ( !world.isRemote )
					{
						// place the string blocks
						
						StakeStringBlock stringBlock = (StakeStringBlock)(BTWBlocks.stakeString);
						
						BlockPos tempPos = new BlockPos( i, j, k );
						
						for ( int iTempDistance = 0; iTempDistance < iDistanceToOtherStake; iTempDistance++ )
						{
							tempPos.addFacingAsOffset(iTargetFacing);
							
							int iTargetBlockID = world.getBlockId(tempPos.x, tempPos.y, tempPos.z);
	
							if ( iTargetBlockID != stringBlock.blockID )
							{
								// no notify here as it will disrupt the strings still being placed
								
								world.setBlock(tempPos.x, tempPos.y, tempPos.z, stringBlock.blockID, 0, 2);
							}
							
							stringBlock.setExtendsAlongFacing(world, tempPos.x, tempPos.y, tempPos.z, iTargetFacing, true, false);
							
			                if ( !player.capabilities.isCreativeMode )
			                {
			                	equippedItem.stackSize--;
			                }
						}
						
						// cycle back through and give block change notifications
						
						tempPos = new BlockPos( i, j, k );
						
						for ( int iTempDistance = 0; iTempDistance < iDistanceToOtherStake; iTempDistance++ )
						{
							tempPos.addFacingAsOffset(iTargetFacing);
							
							world.notifyBlockChange(tempPos.x, tempPos.y, tempPos.z, BTWBlocks.stakeString.blockID);
						}
						
			            world.playSoundAtEntity( player, "random.bow", 0.25F, world.rand.nextFloat() * 0.2F + 0.8F );
					}
					else
					{
		                if ( !player.capabilities.isCreativeMode )
		                {
		                	equippedItem.stackSize -= iDistanceToOtherStake;
		                }
					}
				}
				
				return true;
			}
    	}
    	
    	return false;
    }
	
	@Override
	public int getFacing(int iMetadata)
	{
		// stake facing is opposite attachment point
		
    	return ( iMetadata & 7 );
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
    	iMetadata &= ~7; // filter out old facing
    	
    	iMetadata |= iFacing;
    	
        return iMetadata;
	}
	
	@Override
    public boolean canRotateAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iFacing)
    {
		return iFacing == Block.getOppositeFacing(getFacing(world, i, j, k));
    }
    
	@Override
    public int getNewMetadataRotatedAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iInitialFacing, int iRotatedFacing)
    {
		int iMetadata = world.getBlockMetadata( i, j, k );
		
		return setFacing(iMetadata, Block.getOppositeFacing(iRotatedFacing));
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
	
	/*
	 * returns the distance to the valid stake in the direction, 0 otherwise
	 */
	private int checkForValidConnectingStakeToFacing(World world, int i, int j, int k, int iFacing, int iMaxDistance)
	{
		StakeStringBlock stringBlock = (StakeStringBlock)(BTWBlocks.stakeString);
		BlockPos tempPos = new BlockPos( i, j, k );
		boolean bFoundOtherStake = false;
		
		for ( int iDistanceToOtherStake = 0; iDistanceToOtherStake <= iMaxDistance; iDistanceToOtherStake++ )
		{
			tempPos.addFacingAsOffset(iFacing);
			
			if ( !world.isAirBlock(tempPos.x, tempPos.y, tempPos.z) )
			{
				int iTargetBlockID = world.getBlockId(tempPos.x, tempPos.y, tempPos.z);
				
				if ( iTargetBlockID == blockID )
				{
					return iDistanceToOtherStake;
				}
				else if ( iTargetBlockID == stringBlock.blockID )
				{
					if ( stringBlock.getExtendsAlongFacing(world, tempPos.x, tempPos.y, tempPos.z, iFacing) )
					{
						return 0;
					}    									
				}
				else
				{
					Block tempBlock = Block.blocksList[iTargetBlockID];
					
					if ( !tempBlock.blockMaterial.isReplaceable() || tempBlock.blockMaterial.isLiquid() )
					{
						return 0;
					}
				}
			}    						
		}
		
		return 0;
	}
    	
	private int findValidFacing(World world, int i, int j, int k)
	{
    	for ( int iFacing = 0; iFacing < 6; iFacing++ )
    	{
    		BlockPos targetPos = new BlockPos( i, j, k, iFacing );
    		
    		if ( WorldUtils.doesBlockHaveCenterHardpointToFacing(world, targetPos.x, targetPos.y, targetPos.z,
																 Block.getOppositeFacing(iFacing)) )
    		{
    			return Block.getOppositeFacing(iFacing);
    		}
    	}
    	
    	return 0;    	
	}
	
	public boolean hasConnectedStringToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
	{		
        StakeStringBlock stringBlock = (StakeStringBlock)( BTWBlocks.stakeString);
    	BlockPos targetPos = new BlockPos( i, j, k );
    	
    	targetPos.addFacingAsOffset(iFacing);
    	
    	int iTargetBlockID = blockAccess.getBlockId(targetPos.x, targetPos.y, targetPos.z);
    	
    	if ( iTargetBlockID == stringBlock.blockID )
    	{
    		return stringBlock.getExtendsAlongFacing(blockAccess, targetPos.x, targetPos.y, targetPos.z, iFacing);
    	}
    	
		return false;
	}
	
	private int clearStringToFacingNoDrop(World world, int i, int j, int k, int iTargetFacing)
	{
		int iStringCount = 0;
		
		BlockPos tempPos = new BlockPos( i, j, k );
		
		do
		{
			tempPos.addFacingAsOffset(iTargetFacing);
			
			if (world.getBlockId(tempPos.x, tempPos.y, tempPos.z) == BTWBlocks.stakeString.blockID )
			{
				StakeStringBlock stringBlock = (StakeStringBlock) BTWBlocks.stakeString;
				
				if ( stringBlock.getExtendsAlongFacing(world, tempPos.x, tempPos.y, tempPos.z, iTargetFacing) )
				{
					if ( stringBlock.getExtendsAlongOtherFacing(world, tempPos.x, tempPos.y, tempPos.z, iTargetFacing) )
					{
						stringBlock.setExtendsAlongFacing(world, tempPos.x, tempPos.y, tempPos.z, iTargetFacing, false, false);
					}
					else
					{
						world.setBlock(tempPos.x, tempPos.y, tempPos.z, 0, 0, 2);
					}
				}
				else
				{					
					break;
				}
			}
			else
			{
				break;
			}
			
			iStringCount++;
		}
		while ( iStringCount < 64 );
		
		if ( iStringCount > 0 )
		{
			// cycle back through and provide notifications to surrounding blocks
			
			tempPos = new BlockPos( i, j, k );
			
			for ( int iTempCount = 0; iTempCount < iStringCount; iTempCount++ )
			{
				tempPos.addFacingAsOffset(iTargetFacing);
				
				world.notifyBlockChange(tempPos.x, tempPos.y, tempPos.z, BTWBlocks.stakeString.blockID);
			}
		}
		
		return iStringCount;
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private static final int TOP_TEXTURE_INDEX = 142;
    @Environment(EnvType.CLIENT)
    private static final int TOP_WITH_STRING_TEXTURE_INDEX = 143;
    @Environment(EnvType.CLIENT)
    private static final int SIDE_TEXTURE_INDEX = 144;
    @Environment(EnvType.CLIENT)
    private static final int SIDE_WITH_TOP_STRING_TEXTURE_INDEX = 145;

    @Environment(EnvType.CLIENT)
    private Icon iconTop;
    @Environment(EnvType.CLIENT)
    private Icon iconTopWithString;
    @Environment(EnvType.CLIENT)
    private Icon iconSide;
    @Environment(EnvType.CLIENT)
    private Icon iconSideWithString;
    @Environment(EnvType.CLIENT)
    private Icon iconString;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		Icon sideIcon = register.registerIcon( "fcBlockStake_side" );
		
		blockIcon = sideIcon;

		iconTop = register.registerIcon("fcBlockStake_top");
		iconTopWithString = register.registerIcon("fcBlockStake_top_string");
		iconSide = sideIcon;
		iconSideWithString = register.registerIcon("fcBlockStake_side_string");

		iconString = register.registerIcon("fcBlockStakeString");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
        int iFacing = getFacing(blockAccess, i, j, k);
		boolean bTopConnectedString = hasConnectedStringToFacing(blockAccess, i, j, k, iFacing);
        
        if ( iSide == iFacing || iSide == Block.getOppositeFacing(iFacing) )
        {
        	if ( !bTopConnectedString )
        	{
        		return iconTop;
        	}
        	else
        	{
        		return iconTopWithString;
        	}
        } 
        else
        {
        	if ( !bTopConnectedString )
        	{
        		return iconSide;
        	}
        	else
        	{
        		return iconSideWithString;
        	}
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return true;
    }

    @Environment(EnvType.CLIENT)
    private AxisAlignedBB getBoundsFromPoolForStringToFacing(int iFacing)
	{
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB( 
			0.5D - StakeStringBlock.HALF_HEIGHT, 0.5D, 0.5D - StakeStringBlock.HALF_HEIGHT,
    		0.5D + StakeStringBlock.HALF_HEIGHT, 1D, 0.5F + StakeStringBlock.HALF_HEIGHT);
		
		box.tiltToFacingAlongY(iFacing);
		
		return box;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return idDropped( world.getBlockMetadata( i, j, k ), world.rand, 0 );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderer.blockAccess;
    	
    	int iFacing = getFacing(blockAccess, i, j, k);
    	
    	if ( iFacing == 0 )
    	{
    		renderer.setUVRotateSouth(3);
    		renderer.setUVRotateNorth(3);
    		renderer.setUVRotateEast(3);
    		renderer.setUVRotateWest(3);
    	}
    	else if ( iFacing == 2 )
    	{
    		renderer.setUVRotateSouth(1);
    		renderer.setUVRotateNorth(2);
    	}
    	else if ( iFacing == 3 )
    	{
    		renderer.setUVRotateSouth(2);
    		renderer.setUVRotateNorth(1);
    		renderer.setUVRotateTop(3);
    		renderer.setUVRotateBottom(3);
    	}
    	else if ( iFacing == 4 )
    	{    		
    		renderer.setUVRotateEast(1);
    		renderer.setUVRotateWest(2);
    		renderer.setUVRotateTop(2);
    		renderer.setUVRotateBottom(1);
    	}
    	else if (  iFacing == 5 )
    	{
    		renderer.setUVRotateEast(2);
    		renderer.setUVRotateWest(1);
    		renderer.setUVRotateTop(1);
    		renderer.setUVRotateBottom(2);
    	}
    	
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
        renderer.renderStandardBlock( this, i, j, k );

        renderer.clearUVRotation();
        
        // render any attached string
        
        Icon stringTexture = iconString;
        
        for ( int iStringFacing = 0; iStringFacing < 6; iStringFacing++ )
        {
        	if ( hasConnectedStringToFacing(blockAccess, i, j, k, iStringFacing) )
        	{
        		renderer.setRenderBounds(getBoundsFromPoolForStringToFacing(
        			iStringFacing) );
    			
    			RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, stringTexture);
        	}
        }
        
    	return true;
    }
}
