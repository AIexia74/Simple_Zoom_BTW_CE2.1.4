// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.minecraft.src.*;

import java.util.Random;

public class ClayBlock extends Block
{
    public ClayBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.naturalClayMaterial);
        
        setShovelsEffectiveOn();
        
        setStepSound( BTWBlocks.stepSoundSquish);
        
        setCreativeTab( CreativeTabs.tabBlock ); // weirdness due to use of item as legacy clay
    }

    @Override
    public int idDropped( int iMetaData, Random rand, int iFortuneModifier )
    {
        return Item.clay.itemID;
    }

	@Override
    public void dropBlockAsItemWithChance(World world, int i, int j, int k, int iMetaData, float fChance, int iFortuneModifier )
    {
        super.dropBlockAsItemWithChance(world, i, j, k, iMetaData, fChance, iFortuneModifier );
        
        if ( !world.isRemote )
        {
    		dropItemsIndividually(world, i, j, k, BTWItems.dirtPile.itemID, 6, 0, fChance);
        }
    }

	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChance)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.clayPile.itemID, 1, 0, fChance);
		
		dropItemsIndividually(world, i, j, k, BTWItems.dirtPile.itemID, 4, 0, fChance);
		
		return true;
	}
	
	@Override
	public int quantityDroppedWithBonus(int par1, Random par2Random)
    {
        if (par1 > 0 && this.blockID != this.idDropped(0, par2Random, par1))
        {
            int var3 = par2Random.nextInt(par1 + 2) - 1;

            if (var3 < 0)
            {
                var3 = 0;
            }

            return this.quantityDropped(par2Random) * (var3 + 1);
        }
        else
        {
            return this.quantityDropped(par2Random);
        }
    }
	
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}
	
	@Override
    public boolean canBePistonShoveled(World world, int i, int j, int k)
    {
    	return true;
    }
	
    //------------- Class Specific Methods ------------//    
	
	//----------- Client Side Functionality -----------//
}
