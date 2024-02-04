// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class LooseStoneBrickBlock extends LavaReceiverBlock
{
    public LooseStoneBrickBlock(int iBlockID )
    {
        super( iBlockID, Material.rock );
        
        setHardness( 1F ); // setHardness( 2.25F ); regular stone brick
        setResistance( 5F ); // setResistance( 10F ); regular stone brick
        
        setPicksEffectiveOn();
        
        setStepSound( soundStoneFootstep );
        
        setUnlocalizedName( "fcBlockStoneBrickLoose" );        
        
		setCreativeTab( CreativeTabs.tabBlock );
    }
    
	@Override
	public int damageDropped(int metadata) {
		return getStrata(metadata) << 2; // this block stores strata in last 2 bits
	}
    
    @Override
    public boolean onMortarApplied(World world, int i, int j, int k) {
		world.setBlockAndMetadataWithNotify( i, j, k, Block.stoneBrick.blockID,  getStrata( world, i, j, k) << 2);
		
		return true;
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//    

    @Environment(EnvType.CLIENT)
    private Icon iconLavaCracks;
    private Icon iconByMetadataArray[] = new Icon[3];
    
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register ) {
        super.registerIcons( register );

        iconLavaCracks = register.registerIcon("fcOverlayStoneBrickLava");

        iconByMetadataArray[0] = blockIcon;
        iconByMetadataArray[1] = register.registerIcon("fcBlockStoneBrickLoose_1");
        iconByMetadataArray[2] = register.registerIcon("fcBlockStoneBrickLoose_2");

    }

    @Override
    @Environment(EnvType.CLIENT)
    protected Icon getLavaCracksOverlay() {
    	return iconLavaCracks;
    } 

    
    @Override
	public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list ) {
        list.add( new ItemStack( iBlockID, 1, 0 ) );
        list.add( new ItemStack( iBlockID, 1, 4 ) );
        list.add( new ItemStack( iBlockID, 1, 8 ) );
    }

	@Override
    public int getDamageValue(World world, int x, int y, int z) {
		// used only by pick block
		return world.getBlockMetadata(x, y, z);
    }

	
	@Override
    public Icon getIcon( int iSide, int iMetadata ) {
        return iconByMetadataArray[getStrata(iMetadata)];
    }
}