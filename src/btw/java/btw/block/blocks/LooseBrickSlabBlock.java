// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class LooseBrickSlabBlock extends MortarReceiverSlabBlock
{
    public LooseBrickSlabBlock(int iBlockID )
    {
        super( iBlockID, Material.rock );
        
        setHardness( 1F ); // setHardness( 2F ); regular brick
        setResistance( 5F ); // setResistance( 10F ); regular brick
        
        setPicksEffectiveOn();
        setChiselsEffectiveOn();
        
        setStepSound( Block.soundStoneFootstep );
        
        setUnlocalizedName( "fcBlockBrickLooseSlab" );
        
		setCreativeTab( CreativeTabs.tabBlock );
    }
    
	@Override
	public int getCombinedBlockID(int iMetadata)
	{
		return BTWBlocks.looseBrick.blockID;
	}

    @Override
    public boolean onMortarApplied(World world, int i, int j, int k)
    {
		int iNewMetadata = 4; // // metadata 4 is brick slab
		
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
        blockIcon = register.registerIcon( "fcBlockBrickLoose" );
    }
}