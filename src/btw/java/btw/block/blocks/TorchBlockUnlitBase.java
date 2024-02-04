// FCMOD

package btw.block.blocks;

import net.minecraft.src.EntityFallingSand;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

public abstract class TorchBlockUnlitBase extends TorchBlockBase
{
    protected TorchBlockUnlitBase(int iBlockID )
    {
    	super( iBlockID );
    	
        setCreativeTab( null );
    }
    
	@Override
    public boolean getCanBeSetOnFireDirectly(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return true;
    }
    
	@Override
    public boolean canBeCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
    	return true;
    }
    
	@Override
    public int getChanceOfFireSpreadingDirectlyTo(IBlockAccess blockAccess, int i, int j, int k)
    {
		return 60; // same chance as leaves and other highly flammable objects
    }
    
	@Override
    public boolean setOnFireDirectly(World world, int i, int j, int k)
    {
		world.setBlockAndMetadataWithNotify(i, j, k, getLitBlockID(), world.getBlockMetadata(i, j, k));
		
        world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
        	"mob.ghast.fireball", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F );
        
        return true;
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

	protected abstract int getLitBlockID();
	
	//----------- Client Side Functionality -----------//
}
