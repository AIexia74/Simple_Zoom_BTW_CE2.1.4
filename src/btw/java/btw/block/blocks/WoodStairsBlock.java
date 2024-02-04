// FCMOD

package btw.block.blocks;

import btw.block.util.Flammability;
import btw.item.BTWItems;
import net.minecraft.src.*;

public class WoodStairsBlock extends StairsBlock
{
    public WoodStairsBlock(int iBlockID, Block referenceBlock, int iReferenceBlockMetadata)
    {
        super( iBlockID, referenceBlock, iReferenceBlockMetadata );
        
        setAxesEffectiveOn();
        
        setBuoyant();
        
		setFireProperties(Flammability.PLANKS);
    }
    
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2; // iron or better
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 2, 0,
                              fChanceOfDrop);
		
		return true;
	}
		
    @Override
    public int getFurnaceBurnTime(int iItemDamage)
    {
    	return referenceBlock.getFurnaceBurnTime(referenceBlockMetadata) * 3 / 4;
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//    
}