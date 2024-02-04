// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import btw.crafting.util.FurnaceBurnTime;
import btw.block.tileentity.PlacedToolTileEntity;
import btw.item.PlaceableAsItem;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

public class SwordItem extends ItemSword implements PlaceableAsItem
{
    private final EnumToolMaterial material;

    public SwordItem(int iItemID, EnumToolMaterial material )
    {
    	super( iItemID, material );

		this.material = material;
    	
    	if (this.material == EnumToolMaterial.WOOD )
    	{
        	setBuoyant();
        	setfurnaceburntime(FurnaceBurnTime.WOOD_TOOLS);
        	setIncineratedInCrucible();
    	}
    	
    	setInfernalMaxEnchantmentCost(this.material.getInfernalMaxEnchantmentCost());
    	setInfernalMaxNumEnchants(this.material.getInfernalMaxNumEnchants());
    }
    
    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int i, int j, int k )
    {
    	return isEfficientVsBlock(stack, world, block, i, j, k);
    }
    
    @Override
    public float getStrVsBlock( ItemStack stack, World world, Block block, int i, int j, int k )
    {
        if ( isEfficientVsBlock(stack, world, block, i, j, k) )
        {
            return 15.0F;
        }
        else
        {
            Material material = block.blockMaterial;
            
            if ( material == Material.plants || material == Material.vine || material == Material.coral || material != Material.leaves || material != Material.pumpkin )
            {
            	return 1.5F;
            }            
        }
    	
    	return super.getStrVsBlock( stack, world, block, i, j, k );
    }
    
    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int i, int j, int k)
    {
        return block.blockID == Block.web.blockID || block.blockID == BTWBlocks.web.blockID;
    }
    
    @Override
    public boolean isEnchantmentApplicable(Enchantment enchantment)
    {
    	if ( enchantment.type == EnumEnchantmentType.weapon )
    	{
    		return true;
    	}
    	
    	return super.isEnchantmentApplicable(enchantment);
    }
    
    @Override
    public void onCreated( ItemStack stack, World world, EntityPlayer player ) 
    {
		if (player.timesCraftedThisTick == 0 && world.isRemote )
		{
			if (material == EnumToolMaterial.WOOD )
			{
				player.playSound( "mob.zombie.woodbreak", 0.1F, 1.25F + ( world.rand.nextFloat() * 0.25F ) );
			}
			else if (material == EnumToolMaterial.STONE )
			{
				player.playSound( "random.anvil_land", 0.5F, world.rand.nextFloat() * 0.25F + 1.75F );
			}
			else
			{
				player.playSound( "random.anvil_use", 0.5F, world.rand.nextFloat() * 0.25F + 1.25F );
			}			
		}
		
    	super.onCreated( stack, world, player );
    }  
    
    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
    	if (!par3EntityPlayer.isUsingSpecialKey())
    	{
    		par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
    	}
        
        return par1ItemStack;
    }
    
    @Override
    public boolean onItemUse( ItemStack stack, EntityPlayer player, World world, int i, int j, int k, int iFacing, 
    		float fClickX, float fClickY, float fClickZ )
    {
    	if (player.isUsingSpecialKey())
    	{
    		if ( player != null && player.canPlayerEdit( i, j, k, iFacing, stack ) && getCanBePlacedAsBlock() )
    		{
    			BlockPos placementPos = new BlockPos( i, j, k );
    			BlockPos stuckInPos = new BlockPos( i, j, k );

    			if ( !WorldUtils.isReplaceableBlock(world, i, j, k) )
    			{
    				placementPos.addFacingAsOffset(iFacing);
    			}
    			else
    			{
    				iFacing = 1;
    				stuckInPos.addFacingAsOffset(0);
    			}

    			if (WorldUtils.doesBlockHaveCenterHardpointToFacing(world, stuckInPos.x, stuckInPos.y, stuckInPos.z, iFacing, true) &&
					BTWBlocks.placedTool.canPlaceBlockAt(world, placementPos.x, placementPos.y, placementPos.z) )
    			{
    				Block blockStuckIn = Block.blocksList[world.getBlockId(stuckInPos.x, stuckInPos.y, stuckInPos.z)];

    				if ( blockStuckIn != null &&
						 blockStuckIn.canToolsStickInBlock(world, stuckInPos.x, stuckInPos.y, stuckInPos.z) &&
						 canToolStickInBlock(stack, blockStuckIn, world, stuckInPos.x, stuckInPos.y, stuckInPos.z) )
    				{
    					int iTargetFacing;
    					int iTargetFacingLevel;

    					if ( iFacing >= 2 )
    					{
    						iTargetFacing = Block.getOppositeFacing(iFacing);
    						iTargetFacingLevel = 2;
    					}
    					else
    					{
    						iTargetFacing = MiscUtils.convertOrientationToFlatBlockFacing(player);
    						iTargetFacingLevel = Block.getOppositeFacing(iFacing);
    					}

    					int iMetadata = BTWBlocks.placedTool.setFacing(0, iTargetFacing);

    					iMetadata = BTWBlocks.placedTool.setVerticalOrientation(iMetadata, iTargetFacingLevel);

    					world.setBlockAndMetadataWithNotify(placementPos.x, placementPos.y, placementPos.z,
															BTWBlocks.placedTool.blockID, iMetadata );

    					TileEntity targetTileEntity = world.getBlockTileEntity(placementPos.x, placementPos.y, placementPos.z);

    					if ( targetTileEntity != null && targetTileEntity instanceof PlacedToolTileEntity)
    					{
    						((PlacedToolTileEntity)targetTileEntity).setToolStack(stack);

							if (!world.isRemote) {
								playPlacementSound(stack, blockStuckIn, world, placementPos.x, placementPos.y, placementPos.z);
							}

    						stack.stackSize--;

    						return true;
    					}
    				}
    			}
    		}
    	}

    	return false;
    }

    
    //------------- Class Specific Methods ------------//
    
	protected boolean canToolStickInBlock(ItemStack stack, Block block, World world, int i, int j, int k) {
		return block.areShovelsEffectiveOn() || block.canToolStickInBlockSpecialCase(world, i, j, k, this);
	}
    
    protected void playPlacementSound(ItemStack stack, Block blockStuckIn, World world, int i, int j, int k)
    {
        world.playSoundEffect( (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, blockStuckIn.getStepSound(world, i, j, k).getPlaceSound(),
                               (blockStuckIn.getStepSound(world, i, j, k).getPlaceVolume() + 1F) / 2F, blockStuckIn.getStepSound(world, i, j, k).getPlacePitch() * 0.8F);
    }
    
    protected boolean getCanBePlacedAsBlock()
    {
    	return true;
    }
    
    public float getVisualVerticalOffsetAsBlock()
    {
    	return 0.4F;
    }
    
    public float getVisualHorizontalOffsetAsBlock()
    {
    	return 0.85F;
    }
    
    public float getVisualRollOffsetAsBlock()
    {
    	return -45F;
    }
    
    public float getBlockBoundingBoxHeight()
    {
    	return 0.75F;
    }
    
    public float getBlockBoundingBoxWidth()
    {
    	return 0.75F;
    }
    
    
	//------------ Client Side Functionality ----------//
}
