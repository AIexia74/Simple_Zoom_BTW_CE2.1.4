// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import btw.block.blocks.UnfiredPotteryBlock;
import net.minecraft.src.*;

public class UnfiredNetherBrickItem extends PlaceAsBlockItem
{
    public UnfiredNetherBrickItem(int iItemID )
    {
    	super( iItemID, BTWBlocks.unfiredPottery.blockID,
    		UnfiredPotteryBlock.SUBTYPE_NETHER_BRICK);
    	
    	setNeutralBuoyant();
    	
    	setUnlocalizedName( "fcItemBrickNetherUnfired" );
    	
    	setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player )
    {
		if (player.timesCraftedThisTick == 0 && world.isRemote )
		{
			player.playSound( "mob.slime.attack", 0.25F, 
				(  world.rand.nextFloat() - world.rand.nextFloat() ) * 0.1F + 0.7F );
		}
		
		super.onCreated( stack, world, player );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
