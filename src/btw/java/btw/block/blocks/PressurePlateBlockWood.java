// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.minecraft.src.EnumMobType;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

public class PressurePlateBlockWood extends PressurePlateBlock
{
    public PressurePlateBlockWood(int iBlockID)
    {
    	super( iBlockID, "wood", BTWBlocks.plankMaterial, EnumMobType.everything );
    	
    	setHardness( 0.5F );
    	
        setAxesEffectiveOn();
        
    	setBuoyant();
    	
    	setStepSound( soundWoodFootstep );
    	
    	setUnlocalizedName( "pressurePlate" );    	
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