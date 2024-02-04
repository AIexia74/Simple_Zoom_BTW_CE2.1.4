// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class StoneBrickSlabBlock extends BlockHalfSlab {
    public StoneBrickSlabBlock(int iBlockID, boolean bDoubleSlab ) {
        super( iBlockID, bDoubleSlab, Material.rock );
        
        setPicksEffectiveOn();
        
        setHardness(2.0F);
        setResistance(10.0F);
        
        setStepSound(soundStoneFootstep);
        setUnlocalizedName("stoneSlab");
        
        setCreativeTab( CreativeTabs.tabBlock );
    }

    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier ) {
        return BTWBlocks.looseStoneBrickSlab.blockID;
    }
    
    @Override
	public int damageDropped(int metadata) {
		return getStrata(metadata) >>> 2; // loose stone brick stores strata in last 2 bits
	}
    
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
    	return 1000; // always convert, never harvest
    }
    
    @Override
    public String getFullSlabName( int iMetadata ) {
        return super.getUnlocalizedName() + ".smoothStoneBrick";
    }    
    
    @Override
    protected ItemStack createStackedBlock( int iMetadata ) {
        return new ItemStack( BTWBlocks.stoneBrickSlab.blockID, 2, getStrata(iMetadata) );
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
		int numItems = isDoubleSlab ? 2 : 1;
		dropItemsIndividually(world, i, j, k, BTWBlocks.looseStoneBrickSlab.blockID, numItems, getStrata(iMetadata) << 2, fChanceOfDrop);

		return true;
	}
    
    @Override
    public int idPicked(World par1World, int par2, int par3, int par4) {
    	return BTWBlocks.stoneBrickSlab.blockID;
    }
    
    @Override
    public int getDamageValue(World par1World, int x, int y, int z) {
        return getStrata(par1World, x, y, z);
    }

    @Override
	public boolean hasContactPointToFullFace(IBlockAccess blockAccess, int i, int j, int k, int iFacing) {
    	if ( !isDoubleSlab  && iFacing < 2 ) {
        	boolean bIsUpsideDown = getIsUpsideDown(blockAccess, i, j, k);
        	
        	return bIsUpsideDown == ( iFacing == 1 );
    	}    	
    		
		return true;
	}
	
    @Override
	public boolean hasContactPointToSlabSideFace(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIsSlabUpsideDown) {
		return isDoubleSlab || bIsSlabUpsideDown == getIsUpsideDown(blockAccess, i, j, k);
	}
	
	@Override
	public boolean hasMortar(IBlockAccess blockAccess, int i, int j, int k) {
		return true;
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
    
    @Override
	public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list ) {
        if ( !isDoubleSlab ) {
            for ( int i = 0; i < 3; i++) {
                list.add( new ItemStack( iBlockID, 1, i) );
            }
        }
    }
    
    @Override
    public void registerIcons( IconRegister register ) {}
    
	@Override
	public Icon getIcon(int iSide, int iMetadata) {
		return Block.stoneBrick.getIcon(iSide, (iMetadata & 7) << 2);
	}
}
