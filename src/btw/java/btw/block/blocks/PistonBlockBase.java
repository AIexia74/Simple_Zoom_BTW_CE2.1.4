// FCMOD

package btw.block.blocks;

import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class PistonBlockBase extends BlockPistonBase
{
    public PistonBlockBase(int iBlockID, boolean bIsSticky )
    {
        super( iBlockID, bIsSticky );
        
        setPicksEffectiveOn(true);
        
        initBlockBounds(0D, 0D, 0D, 1D, 1D, 1D);
    }
    
    @Override
    public boolean canContainPistonPackingToFacing(World world, int i, int j, int k, int iFacing)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k );
    	
    	if ( isExtended( iMetadata ) )
    	{
    		if (Block.getOppositeFacing(getOrientation(iMetadata)) != iFacing )
    		{
    			return false;
    		}
    	}
    	
    	return true;    	
    }
    
    @Override
	public int getFacing(int iMetadata)
	{
    	// piston facing is in the direction of the push
    	
		return iMetadata & 7;
	}
	
    @Override
	public int setFacing(int iMetadata, int iFacing)
	{
    	iMetadata &= ~7;
    	
		return iMetadata | iFacing;
	}
	
    @Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return !isExtended( blockAccess.getBlockMetadata( i, j, k ) );
	}
	
    @Override
	public boolean canTransmitRotationHorizontallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return !isExtended( blockAccess.getBlockMetadata( i, j, k ) );
	}
	
    @Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return !isExtended( blockAccess.getBlockMetadata( i, j, k ) );
	}
	
    @Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		if ( !isExtended( iMetadata ) )
		{
			return super.rotateMetadataAroundJAxis(iMetadata, bReverse);
		}
		
		return iMetadata;
	}
	
    @Override
    public boolean canBlockBePushedByPiston(World world, int i, int j, int k, int iToFacing)
    {
    	return !isExtended( world.getBlockMetadata( i, j, k ) );
    }
    
    @Override
    protected void updatePistonState( World world, int i, int j, int k )
    {
    	validatePistonState(world, i, j, k);
    	
    	super.updatePistonState( world, i, j, k );
    }
    
    @Override
    protected boolean canExtend( World world, int i, int j, int k, int iToFacing )
    {
        int iOffsetI = i + Facing.offsetsXForSide[iToFacing];
        int iOffsetJ = j + Facing.offsetsYForSide[iToFacing];
        int iOffsetK = k + Facing.offsetsZForSide[iToFacing];
        
        int iDist = 0;

        while ( iDist < 13 )
        {
            if ( iOffsetJ <= 0 || iOffsetJ >= 255 )
            {
                return false;
            }

            Block tempBlock = blocksList[world.getBlockId(iOffsetI, iOffsetJ, iOffsetK)];

            if ( tempBlock != null )
            {
                if ( !tempBlock.canBlockBePushedByPiston(world, iOffsetI, iOffsetJ, iOffsetK, iToFacing) )
                {
                    return false;
                }
                else
                {
                	int iMobility = tempBlock.getMobilityFlag();
                	
                	int iShovelEjectDirection = getPistonShovelEjectionDirection(world, iOffsetI, iOffsetJ, iOffsetK, iToFacing);
                	
	                if ( iMobility != 1 && iShovelEjectDirection < 0 )
	                {
	                    if ( iDist == 12 )
	                    {
	                        return false;
	                    }
	
	                    iOffsetI += Facing.offsetsXForSide[iToFacing];
	                    iOffsetJ += Facing.offsetsYForSide[iToFacing];
	                    iOffsetK += Facing.offsetsZForSide[iToFacing];
	                    
	                    ++iDist;
	                    
	                    continue;
	                }
                }
            }
            
        	return true;
        }
        
        return true;
    }
	
	@Override
	protected boolean tryExtend(World world, int x, int y, int z, int facingTo) {
		int offsetX = x + Facing.offsetsXForSide[facingTo];
		int offsetY = y + Facing.offsetsYForSide[facingTo];
		int offsetZ = z + Facing.offsetsZForSide[facingTo];
		
		int distance = 0;
		
		while (true) {
			if (distance < 13) {
				if (offsetY <= 0 || offsetY >= 255) {
					return false;
				}
				
				int movingBlockID = world.getBlockId(offsetX, offsetY, offsetZ);
				Block movingBlock = blocksList[movingBlockID];
				
				if (movingBlock != null) {
					if (!movingBlock.canBlockBePushedByPiston(world, offsetX, offsetY, offsetZ, facingTo)) {
						return false;
					}
					else {
						int mobilityFlag = movingBlock.getMobilityFlag();
						
						int shovelEjectDirection = getPistonShovelEjectionDirection(world, offsetX, offsetY, offsetZ, facingTo);
						
						if (mobilityFlag != 1 && shovelEjectDirection < 0) {
							if (distance == 12) {
								return false;
							}
							
							offsetX += Facing.offsetsXForSide[facingTo];
							offsetY += Facing.offsetsYForSide[facingTo];
							offsetZ += Facing.offsetsZForSide[facingTo];
							++distance;
							
							continue;
						}
						
						int movingBlockMetadata = world.getBlockMetadata(offsetX, offsetY, offsetZ);
						
						if (shovelEjectDirection >= 0) {
							movingBlockMetadata = movingBlock.adjustMetadataForPistonMove(movingBlockMetadata);
							
							int ejectX = offsetX + Facing.offsetsXForSide[shovelEjectDirection];
							int ejectY = offsetY + Facing.offsetsYForSide[shovelEjectDirection];
							int ejectZ = offsetZ + Facing.offsetsZForSide[shovelEjectDirection];
							
							onShovelEjectIntoBlock(world, ejectX, ejectY, ejectZ);
							
							world.setBlock(ejectX, ejectY, ejectZ, Block.pistonMoving.blockID, movingBlockMetadata, 4);
							
							world.setBlockTileEntity(ejectX, ejectY, ejectZ,
									PistonBlockMoving.getShoveledTileEntity(movingBlockID, movingBlockMetadata, shovelEjectDirection));
						}
						else {
							movingBlock.onBrokenByPistonPush(world, offsetX, offsetY, offsetZ, movingBlockMetadata);
						}
						
						world.setBlockToAir(offsetX, offsetY, offsetZ);
					}
				}
			}
			
			int previousOffsetX = offsetX;
			int previousOffsetY = offsetY;
			int previousOffsetZ = offsetZ;
			
			int blockCounter = 0;
			int[] blockIDList;
			int movingX;
			int movingY;
			int movingZ;
			
			for (blockIDList = new int[13]; offsetX != x || offsetY != y || offsetZ != z; offsetZ = movingZ) {
				movingX = offsetX - Facing.offsetsXForSide[facingTo];
				movingY = offsetY - Facing.offsetsYForSide[facingTo];
				movingZ = offsetZ - Facing.offsetsZForSide[facingTo];
				int movingBlockID = world.getBlockId(movingX, movingY, movingZ);
				int movingBlockMetadata = world.getBlockMetadata(movingX, movingY, movingZ);
				
				NBTTagCompound tileEntityData = getBlockTileEntityData(world, movingX, movingY, movingZ);
				world.removeBlockTileEntity(movingX, movingY, movingZ);
				
				if (movingBlockID == this.blockID && movingX == x && movingY == y && movingZ == z) {
					world.setBlock(offsetX, offsetY, offsetZ, Block.pistonMoving.blockID, facingTo | (this.isSticky ? 8 : 0), 4);
					world.setBlockTileEntity(offsetX, offsetY, offsetZ,
							BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, facingTo | (this.isSticky ? 8 : 0), facingTo, true, false));
				}
				else {
					if (Block.blocksList[movingBlockID] != null) {
						movingBlockMetadata = Block.blocksList[movingBlockID].adjustMetadataForPistonMove(movingBlockMetadata);
					}
					
					world.setBlock(offsetX, offsetY, offsetZ, Block.pistonMoving.blockID, movingBlockMetadata, 4);
					world.setBlockTileEntity(offsetX, offsetY, offsetZ, BlockPistonMoving.getTileEntity(movingBlockID, movingBlockMetadata, facingTo, true, false));
					if (tileEntityData != null) {
						((TileEntityPiston) world.getBlockTileEntity(offsetX, offsetY, offsetZ)).storeTileEntity(tileEntityData);
					}
				}
				
				blockIDList[blockCounter++] = movingBlockID;
				offsetX = movingX;
				offsetY = movingY;
			}
			
			offsetX = previousOffsetX;
			offsetY = previousOffsetY;
			offsetZ = previousOffsetZ;
			
			for (blockCounter = 0; offsetX != x || offsetY != y || offsetZ != z; offsetZ = movingZ) {
				movingX = offsetX - Facing.offsetsXForSide[facingTo];
				movingY = offsetY - Facing.offsetsYForSide[facingTo];
				movingZ = offsetZ - Facing.offsetsZForSide[facingTo];
				world.notifyBlocksOfNeighborChange(movingX, movingY, movingZ, blockIDList[blockCounter++]);
				offsetX = movingX;
				offsetY = movingY;
			}
			
			return true;
		}
	}
	

	
	@Override
    public void setBlockBoundsBasedOnState( IBlockAccess blockAccess, int i, int j, int k )
    {
    	// override to deprecate parent
    }
	
	@Override
    public void setBlockBoundsForItemRender() 
    {
    	// override to deprecate parent
    }	
    
	@Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
        int iMetadata = blockAccess.getBlockMetadata( i, j, k );

        if ( isExtended( iMetadata ) )
        {
            switch ( getOrientation( iMetadata ) )
            {
                case 0:
                	
                	return AxisAlignedBB.getAABBPool().getAABB(         	
                		0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F );

                case 1:
                	
                	return AxisAlignedBB.getAABBPool().getAABB(         	
                		0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F );

                case 2:
                	
                	return AxisAlignedBB.getAABBPool().getAABB(         	
                		0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F );

                case 3:
                	
                	return AxisAlignedBB.getAABBPool().getAABB(         	
                		0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);

                case 4:
                	
                	return AxisAlignedBB.getAABBPool().getAABB(         	
                		0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F );

                case 5:
                	
                	return AxisAlignedBB.getAABBPool().getAABB(         	
                		0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F );
            }
        }
        
    	return super.getBlockBoundsFromPoolBasedOnState(blockAccess, i, j, k);
    }
    
	@Override
    public void addCollisionBoxesToList( World world, int i, int j, int k, 
    	AxisAlignedBB intersectingBox, List list, Entity entity )
    {
		getCollisionBoundingBoxFromPool( world, i, j, k ).addToListIfIntersects(
			intersectingBox, list);
    }
    
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool( World world, int i, int j, int k )
    {
    	return getBlockBoundsFromPoolBasedOnState(world, i, j, k).offset(i, j, k);
    }

    @Override
    public boolean canSupportFallingBlocks(IBlockAccess blockAccess, int i, int j, int k)
    {
    	// NOTE: The piston base transforms into a PistonMoving on retraction making
    	// implementing regular hardpoints more difficult than usual, so just support
    	// falling blocks instead.
    	
    	return true;    	
    }
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
		// handles BD placement
		
        return getOppositeFacing(iFacing);
    }
	
	@Override
	public int preBlockPlacedBy(World world, int i, int j, int k, int iMetadata,
                                EntityLiving entityBy)
	{
		int facing = determineOrientation( world, i, j, k, entityBy );
    
		if(entityBy.isUsingSpecialKey()) {
			facing = Facing.oppositeSide[facing];
		}

		return facing;    	
	}

	@Override
	public void onBlockPlacedBy( World world, int i, int j, int k, 
		EntityLiving entityBy, ItemStack stack )
    {
		// override vanilla orientation and update code
    }
	
	@Override
    public void onPostBlockPlaced( World world, int i, int j, int k, int iMetadata ) 
    {    	
        if ( !world.isRemote )
        {
            updatePistonState( world, i, j, k );
        }
    }
	
    //------------- Class Specific Methods ------------//

    protected void validatePistonState(World world, int i, int j, int k)
    {
    	// checks for jams that sometimes occur on chunk load, and corrects them
    	
        int iMetadata = world.getBlockMetadata( i, j, k );

        if ( !isExtended( iMetadata ) )
        {
        	int iFacing = getOrientation( iMetadata );
        	
        	BlockPos targetPos = new BlockPos( i, j, k, iFacing );
        	int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
        	
        	if ( iTargetBlockID == Block.pistonExtension.blockID )
        	{
        		int iTargetMetadata = world.getBlockMetadata(targetPos.x, targetPos.y, targetPos.z);
        		
        		if ( BlockPistonExtension.getDirectionMeta( iTargetMetadata ) == iFacing )
        		{
        			// we've got an unextended piston base facing a piston arm coming out of the 
        			// facing the same direction, indicating a jam
        			
        			// don't perform a neighbor notify, as this may occur on a neighbor block change.
                    world.SetBlockMetadataWithNotify( i, j, k, iMetadata | 8, 2 );
        		}
        	}        	
        }
    }
    
    protected int getPistonShovelEjectionDirection(World world, int i, int j, int k, int iToFacing)
    {
        Block block = Block.blocksList[world.getBlockId( i, j, k )];
        
    	if ( block!= null && block.canBePistonShoveled(world, i, j, k) )
    	{
        	int iOppFacing = Block.getOppositeFacing(iToFacing);
        	
            int iShovelI = i + Facing.offsetsXForSide[iOppFacing];
            int iShovelJ = j + Facing.offsetsYForSide[iOppFacing];
            int iShovelK = k + Facing.offsetsZForSide[iOppFacing];
            
            Block shovelBlock = Block.blocksList[world.getBlockId( iShovelI, iShovelJ, iShovelK )];
            
            if ( shovelBlock != null )
            {
            	int iShovelEjectDirection = shovelBlock.getPistonShovelEjectDirection(world, iShovelI, iShovelJ, iShovelK, iToFacing);
            	
        		if ( iShovelEjectDirection >= 0 && canShovelEjectToFacing(world, i, j, k, iShovelEjectDirection))
        		{
    				return iShovelEjectDirection;
        		}
            }            
    	}
    	
        return -1;
    }
    
    protected boolean canShovelEjectToFacing(World world, int i, int j, int k, int iFacing)
    {
    	int iDestI = i + Facing.offsetsXForSide[iFacing];
    	int iDestJ = j + Facing.offsetsYForSide[iFacing];
    	int iDestK = k + Facing.offsetsZForSide[iFacing];
    	
    	Block destBlock = Block.blocksList[world.getBlockId( iDestI, iDestJ, iDestK )];
    	
    	if ( destBlock != null )
    	{
    		return destBlock.getMobilityFlag() == 1;
    	}
    	
    	return true;
    }
    
	protected void onShovelEjectIntoBlock(World world, int i, int j, int k)
	{
    	Block block = Block.blocksList[world.getBlockId( i, j, k )];
    	
    	if ( block != null && block.getMobilityFlag() == 1 )
    	{
        	block.dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
        	
            world.setBlockToAir( i, j, k );    	                    
    	}
	}
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    public static boolean isRenderingExtendedBase = false;

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
        int iFacing = getOrientation( iMetadata ); 
        
        if ( iFacing > 5 )
        {
            return topIcon;
        }
        else if ( iSide == iFacing )
    	{
    		if ( !isRenderingExtendedBase)
        	{
        		return topIcon;
        	}
        	else
        	{
        		return innerTopIcon;
        	}        	
    	}
    	else if ( iSide == this.getOppositeFacing(iFacing) )
		{
    		return  bottomIcon;
		}
    	
    	return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderPistonBase( this, i, j, k, false );
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
