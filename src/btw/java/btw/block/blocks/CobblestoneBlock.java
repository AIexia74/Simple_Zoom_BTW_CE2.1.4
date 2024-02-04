// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class CobblestoneBlock extends Block {
    public CobblestoneBlock(int iBlockID ) {
        super( iBlockID, Material.rock );
        
        setPicksEffectiveOn();
    }
	
    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier ) {
        return BTWBlocks.looseCobblestone.blockID;
    }
    
    @Override
	public int damageDropped(int metadata) {
		return getStrata(metadata) << 2; // loose cobble stores strata in last 2 bits
	}
	
    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata) {
        dropBlockAsItem( world, i, j, k, iMetadata, 0 );
    }
    
	@Override
	public boolean hasMortar(IBlockAccess blockAccess, int i, int j, int k) {
		return true;
	}

	@Override
	public boolean isBlockInfestable(EntityLiving entity, int metadata) {
		return (entity instanceof EntitySilverfish);
	}

    @Override
    public int getBlockIDOnInfest(EntityLiving entity, int metadata) {
    	int strata = getStrata(metadata);

    	if (strata == 1) {
    		return BTWBlocks.infestedMidStrataCobblestone.blockID;
    	}
    	else if (strata == 2) {
    		return BTWBlocks.infestedDeepstrataCobblestone.blockID;
    	}

    	return BTWBlocks.infestedCobblestone.blockID;
    }
	
	@Override
	public void dropItemsOnDestroyedByMiningCharge(World world, int x, int y, int z, int metadata) {
		if (!world.isRemote) {
			dropBlockAsItem_do(world, x, y, z, new ItemStack(Block.gravel));
		}
	}
    
	@Override
    public boolean canBeConvertedByMobSpawner(World world, int x, int y, int z) {
    	return true;
    }
    
	@Override
    public void convertBlockFromMobSpawner(World world, int x, int y, int z) {
		world.setBlockAndMetadataWithNotify(x, y, z, Block.cobblestoneMossy.blockID, getStrata(world.getBlockMetadata(x, y, z)));
	}

    //------------- Class Specific Methods ------------//    
    
	/** returns 0 - 2 regardless of what metadata is used to store strata. BEWARE: different blocks store strata differently */
    public int getStrata( IBlockAccess blockAccess, int i, int j, int k ) {
		return getStrata( blockAccess.getBlockMetadata( i, j, k ) );
    }
    
    /** returns 0 - 2 regardless of what metadata is used to store strata. BEWARE: different blocks store strata differently */
    public int getStrata( int iMetadata ) {
    	return iMetadata & 3;
    }
	
	//----------- Client Side Functionality -----------//
    private Icon iconByMetadataArray[] = new Icon[3];
    
    @Override
	public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list ) {
        list.add( new ItemStack( iBlockID, 1, 0 ) );
        list.add( new ItemStack( iBlockID, 1, 1 ) );
        list.add( new ItemStack( iBlockID, 1, 2 ) );
    }

	@Override
    public int getDamageValue(World world, int x, int y, int z) {
		// used only by pick block
		return world.getBlockMetadata(x, y, z);
    }
    
	@Override
    public void registerIcons( IconRegister register ) {
		super.registerIcons( register );
		
		iconByMetadataArray[0] = blockIcon;		
		iconByMetadataArray[1] = register.registerIcon( "fcBlockCobblestone_1" );
		iconByMetadataArray[2] = register.registerIcon( "fcBlockCobblestone_2" );
    }
	
	@Override
    public Icon getIcon( int iSide, int iMetadata ) {
        return iconByMetadataArray[getStrata(iMetadata)];    	
    }
}