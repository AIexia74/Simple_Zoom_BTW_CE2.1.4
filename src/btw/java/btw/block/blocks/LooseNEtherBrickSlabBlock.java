// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class LooseNEtherBrickSlabBlock extends MortarReceiverSlabBlock
{
    public LooseNEtherBrickSlabBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.netherRockMaterial);
        
        setHardness( 1F ); // setHardness( 2F ); regular nether brick
        setResistance( 5F ); // setResistance( 10F ); regular nether brick
        
        setPicksEffectiveOn();
        
        setStepSound( Block.soundStoneFootstep );
        
        setUnlocalizedName( "fcBlockNetherBrickLooseSlab" );
        
		setCreativeTab( CreativeTabs.tabBlock );
    }
    
	@Override
	public int getCombinedBlockID(int iMetadata)
	{
		return BTWBlocks.looseNetherBrick.blockID;
	}

    @Override
    public boolean onMortarApplied(World world, int i, int j, int k)
    {
		int iNewMetadata = 6; // nether brick slab
		
		if ( getIsUpsideDown(world, i, j, k) )
		{
			iNewMetadata |= 8;
		}
		
		world.setBlockAndMetadataWithNotify( i, j, k, Block.stoneSingleSlab.blockID, iNewMetadata );
		
		return true;
    }
    
    //------------- Class Specific Methods ------------//    
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "fcBlockNetherBrickLoose" );
    }
}