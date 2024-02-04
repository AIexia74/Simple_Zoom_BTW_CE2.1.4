// FCMOD

package btw.block.blocks;

import btw.block.tileentity.MobSpawnerTileEntity;
import net.minecraft.src.*;

public class MobSpawnerBlock extends BlockMobSpawner
{
    public MobSpawnerBlock(int iBlockID)
    {
        super( iBlockID );
        
        setHardness( 5F );
        
        setStepSound( soundMetalFootstep );
        
        setUnlocalizedName( "mobSpawner" );
        
        disableStats();
    }
    
    @Override
    public TileEntity createNewTileEntity(World world )
    {
        return new MobSpawnerTileEntity();
    }
    
    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k)
    {	 
    	return null; // can't be picked up
    }
    
    public int getMobilityFlag()
    {
        return 2;
    }
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
