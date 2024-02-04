// FCMOD

package btw.item.items;

import btw.world.util.BlockPos;
import net.minecraft.src.*;

public class SeedItem extends Item
{
	protected final int cropBlockID;
	
    public SeedItem(int iItemID, int iCropBlockID )
    {
    	super( iItemID );

		cropBlockID = iCropBlockID;
    	
        setBuoyant();
        setBellowsBlowDistance(2);
        setIncineratedInCrucible();
		setFilterableProperties(FILTERABLE_FINE);
    	
    	setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
        if ( iFacing == 1 )
        {
	        if ( player == null || ( player.canPlayerEdit( i, j, k, iFacing, itemStack ) && 
	        	player.canPlayerEdit( i, j + 1, k, iFacing, itemStack ) ) )
	        {
	            Block cropBlock = Block.blocksList[cropBlockID];
	        	
	            if ( cropBlock != null && cropBlock.canPlaceBlockAt( world, i, j + 1, k ) )
	            {
	                world.setBlockWithNotify(i, j + 1, k, cropBlockID);
	                
	                world.playSoundEffect( i + 0.5D, j + 0.5D, k + 0.5D, 
	                	Block.soundGrassFootstep.getPlaceSound(), 
	                	( Block.soundGrassFootstep.getPlaceVolume() + 1F ) / 2F, 
	                	Block.soundGrassFootstep.getPlacePitch() * 0.8F );
	                
	                itemStack.stackSize--;
	                
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
        Block cropBlock = Block.blocksList[cropBlockID];
    	
        if ( cropBlock != null && cropBlock.canPlaceBlockAt(world,
															targetPos.x, targetPos.y, targetPos.z) )
        {
            world.setBlockWithNotify(targetPos.x, targetPos.y, targetPos.z, cropBlockID);
            
            world.playSoundEffect(targetPos.x + 0.5D, targetPos.y + 0.5D, targetPos.z + 0.5D,
								  Block.soundGrassFootstep.getPlaceSound(),
            	( Block.soundGrassFootstep.getPlaceVolume() + 1F ) / 2F, 
            	Block.soundGrassFootstep.getPlacePitch() * 0.8F );
            
            return true;
        }
        
        return false;
	}
    
    //------------- Class Specific Methods ------------//

	//----------- Client Side Functionality -----------//    
}
