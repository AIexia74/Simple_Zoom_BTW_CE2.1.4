// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.minecraft.src.*;

import java.util.Random;

public class GravelBlock extends FallingFullBlock
{
    public GravelBlock(int iBlockID )
    {
        super( iBlockID, Material.sand );
        
        setHardness( 0.6F );
        setShovelsEffectiveOn();
		setFilterableProperties(Item.FILTERABLE_FINE);
        
        setStepSound( soundGravelFootstep );
        
        setUnlocalizedName( "gravel" );
        
        setCreativeTab( CreativeTabs.tabBlock );
    }

    public int idDropped( int iMetadata, Random rand, int  iFortuneModifier )
    {
    	// only drop gravel with fortune enchant...no flint
    	
        if ( iFortuneModifier > 0 || rand.nextInt( 10 ) != 0 )
        {
             return blockID;
        }

        return Item.flint.itemID;
    }
    
    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
    {
    	if ( world.rand.nextInt( 10 ) == 0 )
    	{
    		dropItemsIndividually(world, i, j, k, Item.flint.itemID, 1, 0, 1F);
    	}
    	else
    	{
    		super.onBlockDestroyedWithImproperTool(world, player, i, j, k, iMetadata);
    	}
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.gravelPile.itemID, 6, 0, fChanceOfDrop);
		
		return true;
	}
    
	@Override
    public boolean canBePistonShoveled(World world, int i, int j, int k)
    {
    	return true;
    }
	
    //------------- Class Specific Methods ------------//    
    
	//----------- Client Side Functionality -----------//
}