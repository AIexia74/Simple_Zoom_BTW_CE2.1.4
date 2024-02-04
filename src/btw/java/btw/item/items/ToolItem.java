// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import btw.crafting.util.FurnaceBurnTime;
import btw.block.tileentity.PlacedToolTileEntity;
import btw.item.PlaceableAsItem;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

/**
 * Class to fully replace ItemTool due to large amount of refactoring that was applied to that class
 */
public abstract class ToolItem extends Item implements PlaceableAsItem
{
    protected float efficiencyOnProperMaterial = 4F;
    protected int damageVsEntity;
    public EnumToolMaterial toolMaterial;

    protected ToolItem(int iITemID, int iBaseEntityDamage, EnumToolMaterial par3EnumToolMaterial )
    {
        super(iITemID);
        
        maxStackSize = 1;        
        toolMaterial = par3EnumToolMaterial;
        
        setMaxDamage( par3EnumToolMaterial.getMaxUses() );
        efficiencyOnProperMaterial = par3EnumToolMaterial.getEfficiencyOnProperMaterial();
        damageVsEntity = iBaseEntityDamage + par3EnumToolMaterial.getDamageVsEntity();
    	
    	if ( toolMaterial == EnumToolMaterial.WOOD )
    	{
    		setBuoyant();
        	setfurnaceburntime(FurnaceBurnTime.WOOD_TOOLS);
        	setIncineratedInCrucible();
    	}
    	
    	setInfernalMaxEnchantmentCost(toolMaterial.getInfernalMaxEnchantmentCost());
    	setInfernalMaxNumEnchants(toolMaterial.getInfernalMaxNumEnchants());
    	
        setCreativeTab( CreativeTabs.tabTools );
    }

    @Override
    public boolean hitEntity( ItemStack stack, EntityLiving defendingEntity, EntityLiving attackingEntity )
    {
        stack.damageItem( 2, attackingEntity );
        
        return true;
    }

    @Override
    public boolean onBlockDestroyed( ItemStack stack, World world, int iBlockID, int i, int j, int k, EntityLiving usingEntity )
    {
        if ( Block.blocksList[iBlockID].getBlockHardness( world, i, j, k ) > 0F )
        {
            stack.damageItem( 1, usingEntity );
        }

        return true;
    }

    @Override
    public int getDamageVsEntity( Entity entity )
    {
        return damageVsEntity;
    }

    @Override
    public int getItemEnchantability()
    {
        return toolMaterial.getEnchantability();
    }

    
    @Override
    public boolean isEnchantmentApplicable(Enchantment enchantment)
    {
    	if ( enchantment.type == EnumEnchantmentType.digger )
    	{
    		return true;
    	}
    	
    	return super.isEnchantmentApplicable(enchantment);
    }
    
    @Override
    public float getStrVsBlock( ItemStack stack, World world, Block block, int i, int j, int k )
    {
    	if ( isEfficientVsBlock(stack, world, block, i, j, k) )
		{
            return efficiencyOnProperMaterial;
		}

    	return super.getStrVsBlock( stack, world, block, i, j, k );
    }
    
    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int i, int j, int k)
    {
    	if ( !block.blockMaterial.isToolNotRequired() )
    	{
    		if ( canHarvestBlock( stack, world, block, i, j, k ) )
    		{
    			return true;
    		}
    	}
    	
    	return isToolTypeEfficientVsBlockType(block);
    }
    
    @Override
    public void onCreated( ItemStack stack, World world, EntityPlayer player ) 
    {
		if (player.timesCraftedThisTick == 0 && world.isRemote )
		{
			if ( toolMaterial == EnumToolMaterial.WOOD )
			{
				player.playSound( "mob.zombie.woodbreak", 0.1F, 1.25F + ( world.rand.nextFloat() * 0.25F ) );
			}
			else if ( toolMaterial == EnumToolMaterial.STONE )
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
    
    @Override
    public boolean canItemBeUsedByPlayer(World world, int i, int j, int k, int iFacing, EntityPlayer player, ItemStack stack)
    {
    	return !player.isLocalPlayerAndHittingBlock();
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

    						if ( !world.isRemote )
    						{
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
	
    abstract public boolean isToolTypeEfficientVsBlockType(Block block);
    
    public ToolItem setDamageVsEntity(int iDamage)
    {
    	damageVsEntity = iDamage;
    	
    	return this;
    }

	public boolean canToolStickInBlock(ItemStack stack, Block block, World world, int i, int j, int k) {
		return isEfficientVsBlock(stack, world, block, i, j, k) || block.canToolStickInBlockSpecialCase(world, i, j, k, this);
	}

	public void playPlacementSound(ItemStack stack, Block blockStuckIn, World world, int i, int j, int k)
    {
        world.playSoundEffect( (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, blockStuckIn.getStepSound(world, i, j, k).getStepSound(),
                               (blockStuckIn.getStepSound(world, i, j, k).getVolume() + 1F) / 2F, blockStuckIn.getStepSound(world, i, j, k).getPitch() * 0.8F);
    }

	public boolean getCanBePlacedAsBlock()
    {
    	return true;
    }
    
    public float getVisualVerticalOffsetAsBlock()
    {
    	return 0.75F;
    }
    
    public float getVisualHorizontalOffsetAsBlock()
    {
    	return 0.5F;
    }
    
    public float getVisualRollOffsetAsBlock()
    {
    	return 0F;
    }
    
    public float getBlockBoundingBoxHeight()
    {
    	return 0.75F;
    }
    
    public float getBlockBoundingBoxWidth()
    {
    	return 0.75F;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }
}