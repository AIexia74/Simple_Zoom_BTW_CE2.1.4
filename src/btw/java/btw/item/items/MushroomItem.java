// FCMOD

package btw.item.items;

import btw.world.util.BlockPos;
import net.minecraft.src.*;

public class MushroomItem extends FoodItem
{
	static public final int BROWN_MUSHROOM_HUNGER_HEALED = 1;
	static public final float BROWN_MUSHROOM_SATURATION_MODIFIER = 0F;
	static public final String BROWN_MUSHROOM_ITEM_NAME = "fcItemMushroomBrown";
	
	static public final int RED_MUSHROOM_HUNGER_HEALED = 1;
	static public final float RED_MUSHROOM_SATURATION_MODIFIER = 0F;
	static public final String RED_MUSHROOM_ITEM_NAME = "fcItemMushroomRed";
	
	public final int placedBlockID;
	
    public MushroomItem(int iItemID, int iHungerHealed, float fSaturationModifier, String sItemName, int iPlacedBlockID )
    {
    	super( iItemID, iHungerHealed, fSaturationModifier, false, sItemName );

		placedBlockID = iPlacedBlockID;
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
        if ( iFacing == 1 )
        {
	        if ( player == null || ( player.canPlayerEdit( i, j, k, iFacing, itemStack ) && 
	        	player.canPlayerEdit( i, j + 1, k, iFacing, itemStack ) ) )
	        {
	            Block placedBlock = Block.blocksList[placedBlockID];
	            
	            if ( world.isAirBlock( i, j + 1, k ) && placedBlock != null && 
	            	placedBlock.canPlaceBlockAt( world, i, j + 1, k ) )
	            {
	                world.setBlockWithNotify(i, j + 1, k, placedBlockID);
	                
	                world.playSoundEffect( i + 0.5D, j + 0.5D, k + 0.5D, 
	                	Block.soundGrassFootstep.getPlaceSound(), 
	                	( Block.soundGrassFootstep.getPlaceVolume() + 1F ) / 2F, 
	                	Block.soundGrassFootstep.getPlacePitch() * 0.8F );
	                
	                --itemStack.stackSize;
	                
	                return true;
	            }
	        }
        }
        
        return false;
    }
    
    @Override
	public boolean onItemUsedByBlockDispenser(ItemStack stack, World world,
                                              int i, int j, int k, int iFacing)
	{
    	BlockPos targetPos = new BlockPos( i, j, k, iFacing );
        Block placedBlock = Block.blocksList[placedBlockID];
    	
        if (world.isAirBlock(targetPos.x, targetPos.y, targetPos.z) && placedBlock != null &&
			placedBlock.canPlaceBlockAt(world, targetPos.x, targetPos.y, targetPos.z) )
        {
            world.setBlockWithNotify(targetPos.x, targetPos.y, targetPos.z, placedBlockID);
            
            world.playSoundEffect(targetPos.x + 0.5D, targetPos.y + 0.5D, targetPos.z + 0.5D,
								  Block.soundGrassFootstep.getPlaceSound(),
            	( Block.soundGrassFootstep.getPlaceVolume() + 1F ) / 2F, 
            	Block.soundGrassFootstep.getPlacePitch() * 0.8F );
            
            return true;
        }
        
        return false;
	}
    
    @Override
    public int getHungerRestored()
    {
    	// do not multiply the heal amount so that mushrooms only restore a bit of hunger
    	
    	return getHealAmount();
    }
    
    //------------- Class Specific Methods ------------//

	//----------- Client Side Functionality -----------//    
}