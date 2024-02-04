//FCMOD 

package btw.item.blockitems;

import btw.block.BTWBlocks;
import btw.block.blocks.CompanionCubeBlock;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class CompanionCubeBlockItem extends ItemBlock
{
    public CompanionCubeBlockItem(int iItemID )
    {
        super( iItemID );
        
        setMaxDamage( 0 );
        setHasSubtypes( true );
        
        setUnlocalizedName( "fcCompanionCube" );
    }

    @Override
    public int getMetadata( int iItemDamage )
    {
    	if ( getIsSlab( iItemDamage ) )
    	{
    		return 8;
    	}
    	
        return 0;
    }
    
    @Override
    public String getUnlocalizedName( ItemStack itemstack )
    {
    	if ( itemstack.getItemDamage() > 0 )
    	{
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("slab").toString();
    	}
    	else
    	{
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("cube").toString();
    	}
    }
    
    // Much of the slab code in this file is ripped directly from FCItemBlockSlab, modified to handle the subtypes.  From this point forward, most of the code in this file
    // just deals with the slabs
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
    	if ( !getIsSlab( itemStack.getItemDamage() ) )
    	{
            return super.onItemUse( itemStack, player, world, i, j, k, iFacing, fClickX, fClickY, fClickZ );
            
    	}
    	
        if ( itemStack.stackSize == 0)
        {
            return false;
        }

        if ( !player.canPlayerEdit( i, j, k, iFacing, itemStack ) )
        {
            return false;
        }

        if ( attemptToCombineWithBlock( itemStack, player, world, i, j, k, iFacing, true ) )
        {
        	return true;
        }

    	BlockPos targetPos = new BlockPos( i, j, k );
    	
    	targetPos.addFacingAsOffset(iFacing);
    	
        if ( attemptToCombineWithBlock(itemStack, player, world, targetPos.x, targetPos.y, targetPos.z, iFacing, false) )
        {
            return true;
        }
        else
        {
            return super.onItemUse( itemStack, player, world, i, j, k, iFacing, fClickX, fClickY, fClickZ );
        }
    }
    
    //------------- Class Specific Methods ------------//
    
    public boolean getIsSlab( int iItemDamage )
    {
    	return iItemDamage != 0;
    }
    
    public boolean canCombineWithBlock( World world, int i, int j, int k, int iItemDamage )
    {
        int iBlockID = world.getBlockId( i, j, k );

        if ( getIsSlab( iItemDamage ) )
        {
	        if ( iBlockID == BTWBlocks.companionCube.blockID )
	        {
	        	CompanionCubeBlock companionCube = (CompanionCubeBlock)(Block.blocksList[iBlockID]);

	        	if ( companionCube != null )
	        	{
	        		if ( companionCube.getIsSlab(world, i, j, k) )
	        		{
		            	return true;
		        	}
	        	}
	        }
        }
        
    	return false;
    }
    
    public boolean convertToFullBlock( EntityPlayer player, World world, int i, int j, int k )
    {
        int iBlockID = world.getBlockId( i, j, k );
        
        if ( iBlockID == BTWBlocks.companionCube.blockID )
        {
        	CompanionCubeBlock companionCube = (CompanionCubeBlock)(Block.blocksList[iBlockID]);

        	if ( companionCube != null )
        	{
    			if ( companionCube.getIsSlab(world, i, j, k) )
    			{
                    // Companion cubes love to be reassembled                   

		        	companionCube.spawnHearts(world, i, j, k);
		    		
	                world.playSoundEffect( (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, companionCube.stepSound.getPlaceSound(), 
                		( companionCube.stepSound.getPlaceVolume() + 1.0F) / 2.0F, companionCube.stepSound.getPlacePitch() * 0.8F);
	            	
		        	int iTargetBlockID = BTWBlocks.companionCube.blockID;
		        	int iTargetMetadata = 0; 
		        	
					int iFacing = MiscUtils.convertPlacingEntityOrientationToBlockFacingReversed(player);
					
					iTargetMetadata = companionCube.setFacing(iTargetMetadata, iFacing);
			        
		        	return world.setBlockAndMetadataWithNotify( i, j, k, iTargetBlockID, iTargetMetadata );
    			}
        	}
        }
        	
    	return false;
    }
    
    public boolean isSlabUpsideDown( int iBlockID, int iMetadata )
    {
    	if ( iBlockID == BTWBlocks.companionCube.blockID )
    	{
        	CompanionCubeBlock companionCube = (CompanionCubeBlock)(Block.blocksList[iBlockID]);

        	if ( companionCube != null )
        	{
    			return companionCube.getIsUpsideDownSlabFromMetadata(iMetadata);
        	}
    	}
    			
		return false;
    }
	
    public boolean attemptToCombineWithBlock( ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, boolean bFacingTest )
    {
        if ( canCombineWithBlock( world, i, j, k, itemStack.getItemDamage() ) )
        {    		
	        int iTargetBlockID = world.getBlockId( i, j, k );
	        Block targetBlock = Block.blocksList[iTargetBlockID];
	        
	        if ( targetBlock != null  )
	        {        
	        	int iTargetMetadata = world.getBlockMetadata( i, j, k );
	        	
	        	boolean bIsTargetUpsideDown = isSlabUpsideDown( iTargetBlockID, iTargetMetadata );
	        	
		        if ( !bFacingTest || ( iFacing == 1 && !bIsTargetUpsideDown ) || ( iFacing == 0 && bIsTargetUpsideDown ) )
		        {
		            if ( world.checkNoEntityCollision( targetBlock.getCollisionBoundingBoxFromPool( world, i, j, k ) ) )
		            {
		            	if ( convertToFullBlock( player, world, i, j, k ) )
		            	{		            		
			                itemStack.stackSize--;
			                
			            	Block newBlock = Block.blocksList[world.getBlockId( i, j, k )];
			            	
			            	if ( newBlock != null )
			            	{
			    	            // Panick animals when blocks are placed near them
			    	            world.notifyNearbyAnimalsOfPlayerBlockAddOrRemove(player, newBlock, i, j, k);
			            	}
		            	}
		            }
		            
		            // if placement fails due to entity collision, still "use" the item without placing the block
		            
		            return true;
		        }
	        }
        }
        
        return false;
    }
    
	//----------- Client Side Functionality -----------//
	
    @Override
    public boolean canPlaceItemBlockOnSide( World world, int i, int j, int k, int iFacing, EntityPlayer player, ItemStack itemStack )
    {
    	BlockPos targetPos = new BlockPos( i, j, k );
    	
        if ( canCombineWithBlock(world, targetPos.x, targetPos.y, targetPos.z, itemStack.getItemDamage()) )
        {
            int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
            int iTargetMetadata = world.getBlockMetadata(targetPos.x, targetPos.y, targetPos.z);
            
        	boolean bIsUpsideDown = isSlabUpsideDown( iTargetBlockID, iTargetMetadata );
        	
            if ( ( iFacing == 1 && !bIsUpsideDown ) || ( iFacing == 0 && bIsUpsideDown ) )
            {
            	return true;
            }
        }
        
        targetPos.addFacingAsOffset(iFacing);
        
        if ( canCombineWithBlock(world, targetPos.x, targetPos.y, targetPos.z, itemStack.getItemDamage()) )
        {
        	return true;
        }
        
        return super.canPlaceItemBlockOnSide( world, i, j, k, iFacing, player, itemStack );
    }
}