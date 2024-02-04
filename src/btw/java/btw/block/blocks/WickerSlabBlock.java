// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class WickerSlabBlock extends SlabBlock
{
    public WickerSlabBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.wickerMaterial);
        
        setHardness( 0.5F );        
        setAxesEffectiveOn();
        
        setBuoyant();
        
		setFireProperties(Flammability.WICKER);
		
        setStepSound( soundGrassFootstep );        
        
        setUnlocalizedName( "fcBlockWickerSlab" );
        
        setCreativeTab( CreativeTabs.tabBlock );
    }
    
	@Override
    public boolean doesBlockBreakSaw(World world, int i, int j, int k )
    {
		return false;
    }
	
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k,
                                                int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.wickerPane.itemID,
                              1, 0, fChanceOfDrop);
		
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID,
                              3, 0, fChanceOfDrop);
		
		return true;
	}
	
	@Override
	public int getCombinedBlockID(int iMetadata)
	{
		return BTWBlocks.wickerBlock.blockID;
	}
	
	@Override
    public boolean canToolsStickInBlock(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "fcBlockWicker" );
    }
}
