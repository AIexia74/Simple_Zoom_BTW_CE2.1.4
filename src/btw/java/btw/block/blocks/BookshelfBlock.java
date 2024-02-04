// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.crafting.util.FurnaceBurnTime;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class BookshelfBlock extends Block
{
    public BookshelfBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.plankMaterial);
        
        setHardness( 1.5F );        
        setAxesEffectiveOn();
        
        setBuoyant();
        setFurnaceBurnTime(FurnaceBurnTime.WOOD_BASED_BLOCK);
		setFireProperties(Flammability.BOOKSHELVES);
		
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "bookshelf" );
        
        setCreativeTab( CreativeTabs.tabBlock );
    }
	
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2; // iron or better
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 2, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, Item.book.itemID, 3, 0, fChanceOfDrop);
		
		return true;
	}
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	if ( iSide < 2 )
    	{
    		return Block.planks.getBlockTextureFromSide( iSide );
    	}
    	
        return super.getIcon( iSide, iMetadata );
    }
}