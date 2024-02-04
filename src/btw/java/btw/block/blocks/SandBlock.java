// FCMOD 
// Note that this file only applies to the sand blocks themselves, 
// not blocks that inherit from BlockSand

package btw.block.blocks;

import btw.item.BTWItems;
import net.minecraft.src.*;

public class SandBlock extends FallingFullBlock
{
    public SandBlock(int iBlockID )
    {
        super( iBlockID, Material.sand );
        
        setHardness( 0.5F );
        setShovelsEffectiveOn();
		setFilterableProperties(Item.FILTERABLE_FINE);
        
        setStepSound( soundSandFootstep );
        
        setUnlocalizedName( "sand" );
        
        setCreativeTab( CreativeTabs.tabBlock );
    }
    
    @Override
    public float getMovementModifier(World world, int i, int j, int k)
    {
    	return 0.8F;
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.sandPile.itemID, 6, 0, fChanceOfDrop);
		
		return true;
	}
    
	@Override
    public boolean canBePistonShoveled(World world, int i, int j, int k)
    {
    	return true;
    }
	
	@Override
    public boolean canReedsGrowOnBlock(World world, int i, int j, int k)
    {
    	return true;
    }
    
	@Override
    public boolean canCactusGrowOnBlock(World world, int i, int j, int k)
    {
    	return true;
    }
    
    //------------- Class Specific Methods ------------//    
    
	//----------- Client Side Functionality -----------//
}