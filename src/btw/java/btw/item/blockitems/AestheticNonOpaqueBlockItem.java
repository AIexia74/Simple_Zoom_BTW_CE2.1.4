//FCMOD 

package btw.item.blockitems;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticNonOpaqueBlock;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class AestheticNonOpaqueBlockItem extends ItemBlock
{
    public AestheticNonOpaqueBlockItem(int iItemID )
    {
        super( iItemID );
        
        setMaxDamage( 0 );
        setHasSubtypes(true);
        
        setUnlocalizedName( "fcBlockAestheticNonOpaque" );
    }

    @Override
    public int getMetadata( int iItemDamage )
    {
		return iItemDamage;    	
    }
    
    @Override
    public String getUnlocalizedName( ItemStack itemstack )
    {
    	switch ( itemstack.getItemDamage() )
    	{
    		case AestheticNonOpaqueBlock.SUBTYPE_URN:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("urn").toString();
    			
    		case AestheticNonOpaqueBlock.SUBTYPE_COLUMN:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("column").toString();
    			
    		case AestheticNonOpaqueBlock.SUBTYPE_PEDESTAL_UP:
    		case AestheticNonOpaqueBlock.SUBTYPE_PEDESTAL_DOWN:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("pedestal").toString();
    			
    		case AestheticNonOpaqueBlock.SUBTYPE_TABLE:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("table").toString();
    			
    		case AestheticNonOpaqueBlock.SUBTYPE_WICKER_SLAB:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("wickerslab").toString();
                
    		case AestheticNonOpaqueBlock.SUBTYPE_WHITE_COBBLE_SLAB:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("whitecobbleslab").toString();
                
    		case AestheticNonOpaqueBlock.SUBTYPE_LIGHTNING_ROD:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("lightningrod").toString();
                
			default:
				
				return super.getUnlocalizedName();
    	}
    }
    
    // Much of the slab code in this file is ripped directly from FCItemBlockSlab, modified to handle the subtypes.  From this point forward, most of the code in this file
    // just deals with the slabs    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
        if ( itemStack.getItemDamage() == AestheticNonOpaqueBlock.SUBTYPE_LIGHTNING_ROD)
        {
        	BlockPos targetPos = new BlockPos( i, j, k );
        	
        	targetPos.addFacingAsOffset(iFacing);
        	
        	if ( AestheticNonOpaqueBlock.canLightningRodStay(world, targetPos.x, targetPos.y, targetPos.z) )
    		{
                return super.onItemUse( itemStack, player, world, i, j, k, iFacing, fClickX, fClickY, fClickZ );
    		}
        	
        	return false;
        }
        	
    	if ( itemStack.getItemDamage() != AestheticNonOpaqueBlock.SUBTYPE_WICKER_SLAB && itemStack.getItemDamage() != AestheticNonOpaqueBlock.SUBTYPE_WHITE_COBBLE_SLAB)
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

    @Override
    public float getBuoyancy(int iItemDamage)
    {
    	switch ( iItemDamage )
    	{
    		case AestheticNonOpaqueBlock.SUBTYPE_URN:
    		case AestheticNonOpaqueBlock.SUBTYPE_TABLE:
    		case AestheticNonOpaqueBlock.SUBTYPE_WICKER_SLAB:
    		case AestheticNonOpaqueBlock.SUBTYPE_GRATE:
    		case AestheticNonOpaqueBlock.SUBTYPE_WICKER:
    		case AestheticNonOpaqueBlock.SUBTYPE_SLATS:
    		case AestheticNonOpaqueBlock.SUBTYPE_WICKER_SLAB_UPSIDE_DOWN:
    			
    			return 1.0F;
    			
    	}
    	
    	return super.getBuoyancy(iItemDamage);
    }
    
    //------------- Class Specific Methods ------------//
    
    public boolean canCombineWithBlock( World world, int i, int j, int k, int iItemDamage )
    {
        int iBlockID = world.getBlockId( i, j, k );

        if ( iItemDamage == AestheticNonOpaqueBlock.SUBTYPE_WICKER_SLAB)
        {
	        if ( iBlockID == BTWBlocks.aestheticNonOpaque.blockID )
	        {
	        	int iBlockMetadata = world.getBlockMetadata( i, j, k );
	        	
	        	if ( iBlockMetadata == AestheticNonOpaqueBlock.SUBTYPE_WICKER_SLAB ||
	    			iBlockMetadata == AestheticNonOpaqueBlock.SUBTYPE_WICKER_SLAB_UPSIDE_DOWN)
	        	{
	            	return true;
	        	}
	        }
        }
        else if ( iItemDamage == AestheticNonOpaqueBlock.SUBTYPE_WHITE_COBBLE_SLAB)
        {
	        if ( iBlockID == BTWBlocks.aestheticNonOpaque.blockID )
	        {
	        	int iBlockMetadata = world.getBlockMetadata( i, j, k );
	        	
	        	if ( iBlockMetadata == AestheticNonOpaqueBlock.SUBTYPE_WHITE_COBBLE_SLAB ||
	    			iBlockMetadata == AestheticNonOpaqueBlock.SUBTYPE_WHITE_COBBLE_SLAB_UPSIDE_DOWN)
	        	{
	            	return true;
	        	}
	        }
        }
        
    	return false;
    }
    
    public boolean convertToFullBlock( World world, int i, int j, int k )
    {
        int iBlockID = world.getBlockId( i, j, k );
        
        if ( iBlockID == BTWBlocks.aestheticNonOpaque.blockID )
        {
        	int iBlockMetadata = world.getBlockMetadata( i, j, k );
        	
        	if ( iBlockMetadata == AestheticNonOpaqueBlock.SUBTYPE_WICKER_SLAB ||
    			iBlockMetadata == AestheticNonOpaqueBlock.SUBTYPE_WICKER_SLAB_UPSIDE_DOWN)
        	{
	        	int iTargetBlockID = BTWBlocks.aestheticOpaque.blockID;
	        	int iTargetMetadata = AestheticOpaqueBlock.SUBTYPE_WICKER;
	        	
	        	return world.setBlockAndMetadataWithNotify( i, j, k, iTargetBlockID, iTargetMetadata );
        	}
        	else if ( iBlockMetadata == AestheticNonOpaqueBlock.SUBTYPE_WHITE_COBBLE_SLAB ||
    			iBlockMetadata == AestheticNonOpaqueBlock.SUBTYPE_WHITE_COBBLE_SLAB_UPSIDE_DOWN)
        	{
	        	int iTargetBlockID = BTWBlocks.aestheticOpaque.blockID;
	        	int iTargetMetadata = AestheticOpaqueBlock.SUBTYPE_WHITE_COBBLE;
	        	
	        	return world.setBlockAndMetadataWithNotify( i, j, k, iTargetBlockID, iTargetMetadata );
        	}
        }
        	
    	return false;
    }
    
    public boolean isSlabUpsideDown(int iBlockID, int iMetadata)
    {
    	if ( iBlockID == BTWBlocks.aestheticNonOpaque.blockID )
    	{
    		if ( iMetadata == AestheticNonOpaqueBlock.SUBTYPE_WICKER_SLAB_UPSIDE_DOWN ||
    			iMetadata == AestheticNonOpaqueBlock.SUBTYPE_WHITE_COBBLE_SLAB_UPSIDE_DOWN)
    		{
    			return true;
    		}
    	}
    			
		return false;
    }

    @Environment(EnvType.CLIENT)
    public boolean attemptToCombineWithBlock( ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, boolean bFacingTest )
    {
        if ( canCombineWithBlock( world, i, j, k, itemStack.getItemDamage() ) )
        {    		
	        int iTargetBlockID = world.getBlockId( i, j, k );
	        Block targetBlock = Block.blocksList[iTargetBlockID];
	        
	        if ( targetBlock != null  )
	        {        
	        	int iTargetMetadata = world.getBlockMetadata( i, j, k );
	        	
	        	boolean bIsTargetUpsideDown = isSlabUpsideDown(iTargetBlockID, iTargetMetadata);
	        	
		        if ( !bFacingTest || ( iFacing == 1 && !bIsTargetUpsideDown ) || ( iFacing == 0 && bIsTargetUpsideDown ) )
		        {
		            if ( world.checkNoEntityCollision( Block.getFullBlockBoundingBoxFromPool(world, i, j, k)) )
		            {
		            	if ( convertToFullBlock( world, i, j, k ) )
		            	{		            		
			                world.playSoundEffect((float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, targetBlock.getStepSound(world, i, j, k).getPlaceSound(),
                                                  (targetBlock.getStepSound(world, i, j, k).getPlaceVolume() + 1.0F) / 2.0F, targetBlock.getStepSound(world, i, j, k).getPlacePitch() * 0.8F);
			            	
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
            
        	boolean bIsUpsideDown = isSlabUpsideDown(iTargetBlockID, iTargetMetadata);
        	
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