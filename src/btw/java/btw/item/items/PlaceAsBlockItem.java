// FCMOD

package btw.item.items;

import btw.client.fx.BTWEffectManager;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

public class PlaceAsBlockItem extends Item
{
	protected int blockID;
	protected int blockMetadata = 0;
	
	protected boolean requireNoEntitiesInTargetBlock = false;
	
    public PlaceAsBlockItem(int iItemID, int iBlockID )
    {
    	super( iItemID );

        blockID = iBlockID;
    }
    
    public PlaceAsBlockItem(int iItemID, int iBlockID, int iBlockMetadata )
    {
    	this( iItemID, iBlockID );

        blockMetadata = iBlockMetadata;
    }
    
    public PlaceAsBlockItem(int iItemID, int iBlockID, int iBlockMetadata, String sItemName )
    {
    	this( iItemID, iBlockID, iBlockMetadata );
    	
    	setUnlocalizedName( sItemName );
    }
    
    /**
     * This constructor should only be called by ItemBlock child class
     */
    protected PlaceAsBlockItem(int iItemID )
    {
    	super( iItemID );

        blockID = iItemID + 256;
        blockMetadata = 0;
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
        int iNewBlockID = getBlockIDToPlace(itemStack.getItemDamage(), iFacing, fClickX, fClickY, fClickZ);
        
        if ( itemStack.stackSize == 0 ||
        	( player != null && !player.canPlayerEdit( i, j, k, iFacing, itemStack ) ) ||        	
    		( j == 255 && Block.blocksList[iNewBlockID].blockMaterial.isSolid() ) )
        {
            return false;
        }
        
		BlockPos targetPos = new BlockPos( i, j, k );
		
		int iOldBlockID = world.getBlockId( i, j, k );
        Block oldBlock = Block.blocksList[iOldBlockID];
		
        if ( oldBlock != null )
        {
        	if ( oldBlock.isGroundCover() )
        	{
        		iFacing = 1;
        	}
        	else if ( !oldBlock.blockMaterial.isReplaceable() )
        	{
        		targetPos.addFacingAsOffset(iFacing);
        	}
        }

        if ((!requireNoEntitiesInTargetBlock || isTargetFreeOfObstructingEntities(world, targetPos.x, targetPos.y, targetPos.z) ) &&
			world.canPlaceEntityOnSide(iNewBlockID, targetPos.x, targetPos.y, targetPos.z, false, iFacing, player, itemStack) )
        {
            Block newBlock = Block.blocksList[iNewBlockID];
            
        	int iNewMetadata = getMetadata( itemStack.getItemDamage() );
        	
        	iNewMetadata = newBlock.onBlockPlaced(world, targetPos.x, targetPos.y, targetPos.z, iFacing, fClickX, fClickY, fClickZ, iNewMetadata);

        	iNewMetadata = newBlock.preBlockPlacedBy(world, targetPos.x, targetPos.y, targetPos.z, iNewMetadata, player);
            
            if ( world.setBlockAndMetadataWithNotify(targetPos.x, targetPos.y,
													 targetPos.z, iNewBlockID, iNewMetadata) )
            {
                if (world.getBlockId(targetPos.x, targetPos.y, targetPos.z) == iNewBlockID )
                {
                    newBlock.onBlockPlacedBy(world, targetPos.x, targetPos.y,
											 targetPos.z, player, itemStack);
                    
                    newBlock.onPostBlockPlaced(world, targetPos.x, targetPos.y, targetPos.z, iNewMetadata);
                    
                    // Panick animals when blocks are placed near them
                    world.notifyNearbyAnimalsOfPlayerBlockAddOrRemove(player, newBlock, targetPos.x, targetPos.y, targetPos.z);
                }
                
                playPlaceSound(world, targetPos.x, targetPos.y, targetPos.z, newBlock);
                
                itemStack.stackSize--;
            }
            
        	return true;    	
        }
        
    	return false;    	
    }
    
    @Override
    public int getMetadata( int iItemDamage )
    {
    	return blockMetadata;
    }
    
    @Override
    public boolean canItemBeUsedByPlayer(World world, int i, int j, int k, int iFacing, EntityPlayer player, ItemStack stack)
    {
    	return canPlaceItemBlockOnSide( world, i, j, k, iFacing, player, stack );
    }
    
    @Override
	public boolean onItemUsedByBlockDispenser(ItemStack stack, World world,
                                              int i, int j, int k, int iFacing)
	{
    	BlockPos targetPos = new BlockPos( i, j, k, iFacing );
    	int iTargetDirection = getTargetFacingPlacedByBlockDispenser(iFacing);
    	
    	int iBlockID = getBlockIDToPlace(stack.getItemDamage(), iTargetDirection,
                                         0.5F, 0.25F, 0.5F); // equivalent to lower half of the middle of the block
    	
    	Block newBlock = Block.blocksList[iBlockID];

    	if ( newBlock != null && world.canPlaceEntityOnSide(iBlockID,
															targetPos.x, targetPos.y, targetPos.z, true, iTargetDirection, null, stack) )
        {
    		int iBlockMetadata = getMetadata( stack.getItemDamage() );
        	
    		iBlockMetadata = newBlock.onBlockPlaced(
					world, targetPos.x, targetPos.y, targetPos.z, iTargetDirection,
					0.5F, 0.25F, 0.5F, // equivalent to lower half of the middle of the block
					iBlockMetadata );
        	
        	world.setBlockAndMetadataWithNotify(targetPos.x, targetPos.y, targetPos.z, iBlockID,
												iBlockMetadata );
        	
            newBlock.onPostBlockPlaced(world, targetPos.x, targetPos.y, targetPos.z,
									   iBlockMetadata );
            
            world.playAuxSFX( BTWEffectManager.BLOCK_PLACE_EFFECT_ID, i, j, k, iBlockID );

			return true;
        }       
    	
    	return false;
	}
	
	//------------- Class Specific Methods ------------//
    
    public int getBlockID()
    {
        return blockID;
    }

    public boolean canPlaceItemBlockOnSide( World world, int i, int j, int k, int iFacing, EntityPlayer player, ItemStack stack )
    {
        int iTargetBlockID = world.getBlockId( i, j, k );
        Block iTargetBlock = Block.blocksList[iTargetBlockID];
		BlockPos targetPos = new BlockPos( i, j, k );

        if ( iTargetBlock != null )
        {
        	if ( iTargetBlock.isGroundCover() )
        	{
        		iFacing = 1;
        	}
        	else if ( !iTargetBlock.blockMaterial.isReplaceable() )
        	{
        		targetPos.addFacingAsOffset(iFacing);
        	}
        }        

        // the following places the click spot right in the center, which while technically not correct, shouldn't
        // make much of a difference given vanilla ignores it entirely        
        int iNewBlockID = getBlockIDToPlace(stack.getItemDamage(), iFacing, 0.5F, 0.5F, 0.5F);
        
        return world.canPlaceEntityOnSide(iNewBlockID, targetPos.x, targetPos.y, targetPos.z, false, iFacing, (Entity)null, stack);
    }
    
    public PlaceAsBlockItem setAssociatedBlockID(int iBlockID)
    {
        blockID = iBlockID;
    	
    	return this;
    }
    
    public int getBlockIDToPlace(int iItemDamage, int iFacing, float fClickX, float fClickY, float fClickZ)
    {
    	return getBlockID();
    }
    
    protected boolean isTargetFreeOfObstructingEntities(World world, int i, int j, int k)
    {
    	AxisAlignedBB blockBounds = AxisAlignedBB.getAABBPool().getAABB(
        	(double)i, (double)j, (double)k, (double)(i + 1), (double)( j + 1 ), (double)(k + 1) );
    	
    	return world.checkNoEntityCollision( blockBounds );
    }
    
    protected void playPlaceSound(World world, int i, int j, int k, Block block)
    {
    	StepSound stepSound = block.getStepSound(world, i, j, k);
    	
        world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, stepSound.getPlaceSound(), 
        	( stepSound.getPlaceVolume() + 1F ) / 2F, stepSound.getPlacePitch() * 0.8F );
    }
    
	public int getTargetFacingPlacedByBlockDispenser(int iDispenserFacing)
    {
    	return Block.getOppositeFacing(iDispenserFacing);
    }
}
