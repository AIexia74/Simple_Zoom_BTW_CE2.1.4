// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.item.BTWItems;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

public class FenceBlockWood extends FenceBlock
{
    public FenceBlockWood(int iBlockID )
    {
        super( iBlockID, "wood", BTWBlocks.plankMaterial);

        setHardness( 1.5F );
        setResistance( 5F );
        
        setAxesEffectiveOn();
        
        setBuoyant();
        
		setFireProperties(Flammability.PLANKS);
		
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "fence" );
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
		
		return true;
	}
	
	//----------- Client Side Functionality -----------//
}
