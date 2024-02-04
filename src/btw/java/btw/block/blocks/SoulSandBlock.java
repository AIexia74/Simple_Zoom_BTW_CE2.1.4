// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class SoulSandBlock extends BlockSoulSand
{
    public SoulSandBlock(int iBlockID )
    {
        super( iBlockID );
        
        setShovelsEffectiveOn();
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.soulSandPile.itemID,
                              3, 0, fChanceOfDrop);
		
		return true;
	}
	
    @Override
    public boolean canItemPassIfFilter(ItemStack filteredItem)
    {
    	return filteredItem.itemID == BTWItems.groundNetherrack.itemID ||
	    	filteredItem.itemID == BTWItems.soulDust.itemID ||
	    	filteredItem.itemID == Item.lightStoneDust.itemID;
    }
    
    @Override
    public boolean canNetherWartGrowOnBlock(World world, int i, int j, int k)
    {
    	return true;
    }
    
    //------------- Class Specific Methods ------------//    
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon filterIcon;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

        filterIcon = register.registerIcon("fcBlockHopper_soulsand");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getHopperFilterIcon()
    {
    	return filterIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	return renderer.renderStandardFullBlock(this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean doesItemRenderAsBlock(int iItemDamage)
    {
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockMovedByPiston(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	renderBlocks.renderStandardFullBlockMovedByPiston(this, i, j, k);
    }    
}