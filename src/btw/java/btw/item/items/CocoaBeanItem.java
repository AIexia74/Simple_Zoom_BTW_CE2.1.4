// FCMOD

package btw.item.items;

import btw.client.fx.BTWEffectManager;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class CocoaBeanItem extends FoodItem
{
	static public final int HUNGER_HEALED = 1;
	static public final float SATURATION_MODIFIER = 0F;
	static public final String ITEM_NAME = "fcItemCocoaBeans";
	
    public CocoaBeanItem(int iItemID )
    {
    	super(iItemID, HUNGER_HEALED, SATURATION_MODIFIER, false, ITEM_NAME);
    	
        setBellowsBlowDistance(1);
		setFilterableProperties(FILTERABLE_SMALL);
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
    	if ( attemptPlaceOn(world, i, j, k, iFacing,
                            fClickX, fClickY, fClickZ) )
    	{
            if ( player == null || !player.capabilities.isCreativeMode)
            {
            	itemStack.stackSize--;
            }
            
            return true;
    	}
    	
    	return false;
    }
    
    protected boolean attemptPlaceOn(World world, int i, int j, int k, int iFacing,
                                     float fClickX, float fClickY, float fClickZ)
    {
        int iTargetBlockID = world.getBlockId( i, j, k );
        int iTargetMetadata = world.getBlockMetadata( i, j, k );

        if ( iFacing >= 2 && iTargetBlockID == Block.wood.blockID &&
        	BlockLog.limitToValidMetadata( iTargetMetadata ) == 3 )
        {
            BlockPos targetPos = new BlockPos( i, j, k, iFacing );
            
            if ( world.isAirBlock(targetPos.x, targetPos.y, targetPos.z) )
            {
            	int iCocoaBlockID = Block.cocoaPlant.blockID;
            	
            	int iMetadata = Block.blocksList[iCocoaBlockID].onBlockPlaced(world,
																			  targetPos.x, targetPos.y, targetPos.z, iFacing, fClickX, fClickY, fClickZ, 0);
            	
            	world.setBlockAndMetadataWithNotify(targetPos.x,
													targetPos.y, targetPos.z, iCocoaBlockID, iMetadata);
            	
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public int getHungerRestored()
    {
    	// do not multiply the heal amount so that cocoa beans only restore a bit of hunger
    	
    	return getHealAmount();
    }
    
    @Override
	public boolean onItemUsedByBlockDispenser(ItemStack stack, World world,
                                              int i, int j, int k, int iFacing)
	{
        BlockPos block2AwayPos = new BlockPos( i, j, k, iFacing );
        
        block2AwayPos.addFacingAsOffset(iFacing);
        
    	if ( attemptPlaceOn(world, block2AwayPos.x, block2AwayPos.y, block2AwayPos.z,
							Block.getOppositeFacing(iFacing), 0F, 0F, 0F) )
    	{
            world.playAuxSFX( BTWEffectManager.BLOCK_PLACE_EFFECT_ID, i, j, k,
            	Block.cocoaPlant.blockID );
            
			return true;
    	}
    	
    	return false;
	}
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        itemIcon = register.registerIcon( "dyePowder_brown" );
    }
}
