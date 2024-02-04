// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class FlowerPotBlock extends BlockFlowerPot
{
	protected static final double HEIGHT = (6 / 16D );
	protected static final double WIDTH = (6 / 16D );
	protected static final double HALF_WIDTH = (WIDTH / 2D );
	
    public FlowerPotBlock(int iBlockID )
    {
        super( iBlockID );
        
        initBlockBounds(0.5F - HALF_WIDTH, 0.0F, 0.5F - HALF_WIDTH,
                        0.5F + HALF_WIDTH, HEIGHT, 0.5F + HALF_WIDTH);
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iSide, float fClickX, float fClickY, float fClickZ )
    {
        ItemStack playerStack = player.inventory.getCurrentItem();

        if ( playerStack != null && world.getBlockMetadata(i, j, k) == 0 )
        {
            int iMetadataForStack = getMetadataForItemStack(playerStack);

            if ( iMetadataForStack > 0 )
            {
                world.SetBlockMetadataWithNotify( i, j, k, iMetadataForStack, 2 );

                if ( !player.capabilities.isCreativeMode )
                {
                	playerStack.stackSize--;
                	
                	if ( playerStack.stackSize <= 0 )
                	{                	
                		player.inventory.setInventorySlotContents( player.inventory.currentItem, null );
                	}
                }

                return true;
            }
        }
        
        return false;
    }

    @Override
    public int getDamageValue( World world, int i, int j, int k )
    {
        ItemStack stack = getPlantStackForMetadata(world.getBlockMetadata(i, j, k));
        
        if ( stack == null )
        {
        	return Item.flowerPot.itemID;
        }
        
        return stack.getItemDamage();
    }

    @Override
    public void dropBlockAsItemWithChance( World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier )
    {
    	// this portion copied from Block class since we can't call super.super
    	
        if ( !world.isRemote )
        {
            int iQuantityDropped = quantityDroppedWithBonus( iFortuneModifier, world.rand );

            for ( int iDropCount = 0; iDropCount < iQuantityDropped; ++iDropCount )
            {
                if ( world.rand.nextFloat() <= fChance )
                {
                    int itemID = idDropped( iMetadata, world.rand, iFortuneModifier );

                    if (itemID > 0)
                    {
                        dropBlockAsItem_do( world, i, j, k, new ItemStack( itemID, 1, damageDropped(iMetadata ) ) );
                    }
                }
            }
        }

        if ( iMetadata > 0 )
        {
            ItemStack stack = getPlantStackForMetadata(iMetadata);

            if ( stack != null )
            {
                dropBlockAsItem_do( world, i, j, k, stack );
            }
        }
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

    private ItemStack getPlantStackForMetadata(int iMetadata)
    {
    	if ( iMetadata == 7 )
    	{
            return new ItemStack( BTWItems.redMushroom);
    	}
    	else if ( iMetadata == 8 )
    	{
            return new ItemStack( BTWItems.brownMushroom);
    	}
    	else
    	{
    		return getPlantForMeta( iMetadata );
    	}
    }

    private int getMetadataForItemStack(ItemStack stack)
    {
        int itemID = stack.getItem().itemID;
        
        if ( itemID == BTWItems.redMushroom.itemID )
        {
        	return 7;
        }
        else if ( itemID == BTWItems.brownMushroom.itemID )
        {
        	return 8;
        }
        else
        {
        	return getMetaForPlant( stack );
        }
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderBlockFlowerpot(this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
    	if ( iSide == 0 )
    	{
            return !blockAccess.isBlockOpaqueCube(i, j, k);
    	}
    	
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        ItemStack stack = getPlantStackForMetadata(world.getBlockMetadata(i, j, k));
        
        if ( stack == null )
        {
        	return Item.flowerPot.itemID;
        }
        
        return stack.itemID;
    }
}
