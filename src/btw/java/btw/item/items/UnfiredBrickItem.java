// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class UnfiredBrickItem extends PlaceAsBlockItem
{
    public UnfiredBrickItem(int iItemID )
    {
    	super( iItemID, BTWBlocks.placedUnfiredBrick.blockID );

        requireNoEntitiesInTargetBlock = true; // so that it isn't immediately kicked away
    	
    	setUnlocalizedName( "fcItemBrickUnfired" );
    	
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
