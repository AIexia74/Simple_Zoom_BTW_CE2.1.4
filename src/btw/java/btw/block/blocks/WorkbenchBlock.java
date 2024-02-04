// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.crafting.util.FurnaceBurnTime;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

public class WorkbenchBlock extends BlockWorkbench
{
    public WorkbenchBlock(int iBlockID)
    {
        super( iBlockID );
        
    	setBlockMaterial(BTWBlocks.plankMaterial);
    	
        setHardness( 1.5F );
        
    	// Note that there is no appropriate tool to harvest this block
    	
        setBuoyant();
    	setFurnaceBurnTime(FurnaceBurnTime.WOOD_BASED_BLOCK);
        
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "workbench" );
        
        setCreativeTab( null ); 
    }
    
	@Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
		// prevent access if solid block above
		
		if ( WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j + 1, k, 0) )
		{
			return true;				
		}			
		
		return super.onBlockActivated( world, i, j, k, player, iFacing, fClickX, fClickY, fClickZ );
    }
	
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 3, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, Item.stick.itemID, 1, 0, fChanceOfDrop);
		
		return true;
	}
	
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}
