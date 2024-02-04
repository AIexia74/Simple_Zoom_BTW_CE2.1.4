// FCMOD

package btw.block.blocks;

import net.minecraft.src.*;

public class DoorBlockWood extends DoorBlock
{
    public DoorBlockWood(int iBlockID)
    {
        super( iBlockID, Material.wood );
        
        setHardness( 1.5F );
        setBuoyant();
        
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "doorWood" );
        
        disableStats();        
    }
    
    @Override
    public boolean canPathThroughBlock(IBlockAccess blockAccess, int i, int j, int k, Entity entity, PathFinder pathFinder)
    {
    	if ( !pathFinder.CanPathThroughClosedWoodDoor() )
    	{
    		// note: getBlocksMovement() is misnamed and returns if the block *doesn't* block movement
    		
	    	if (!pathFinder.canPathThroughOpenWoodDoor() || !getBlocksMovement(blockAccess, i, j, k) )
	    	{
	    		return false;
	    	}
    	}
    	
    	return true;
    }
    
    @Override
    public boolean isBreakableBarricade(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return true;
    }

    @Override
    public boolean isBreakableBarricadeOpen(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return isDoorOpen( blockAccess, i, j, k );
    }    
    
    @Override
    public void onPoweredBlockChange(World par1World, int par2, int par3, int par4, boolean par5)
    {
    	// override to remove the ability of wooden doors to react to redstone signal
    }
    
    @Override
    public void onAIOpenDoor(World world, int i, int j, int k, boolean bOpen)
    {
    	super.onPoweredBlockChange( world, i, j, k, bOpen );    	
    }
}
