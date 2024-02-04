// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class StoneBrickBlock extends BlockStoneBrick {
    public static final String[] stoneBrickTypesStratified = new String[] {
    		"default", "mossy", "cracked", "chiseled",
    		"default", "mossy", "cracked", "chiseled",
    		"default", "mossy", "cracked", "chiseled"
    		};
    
	public StoneBrickBlock(int iBlockID ) {
		super( iBlockID );

		setHardness( 2.25F );
		setResistance( 10F );

		setPicksEffectiveOn();

		setStepSound( soundStoneFootstep );

		setUnlocalizedName( "stonebricksmooth" );

		setTickRandomly( true );
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random )
	{
		int iMetadata = world.getBlockMetadata( i, j, k );
		int strataOffset = getStrata(iMetadata) << 2;

		if ( getStoneType(iMetadata) == 0 )
		{
			// check for drip conditions

			if ( !world.getBlockMaterial( i, j - 1, k ).blocksMovement() )
			{
				int iBlockAboveID = world.getBlockId( i, j + 1, k );

				if ( iBlockAboveID == Block.waterMoving.blockID || iBlockAboveID == Block.waterStill.blockID )
				{
					if ( random.nextInt( 15 ) == 0 )
					{
						world.setBlockMetadataWithNotify( i, j, k, 1 + strataOffset );

						world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
					}
				}
				else if ( iBlockAboveID == Block.lavaMoving.blockID || iBlockAboveID == Block.lavaStill.blockID )
				{
					if ( random.nextInt( 15 ) == 0 )
					{
						world.setBlockMetadataWithNotify( i, j, k, 2 + strataOffset );

						world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
					}
				}
			}			
		}
	}

	@Override
	public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
	{
		return BTWBlocks.looseStoneBrick.blockID;
	}

	@Override
	public int damageDropped( int iMetadata ) {
		return getStrata(iMetadata) << 2; // loose stone brick uses last 2 metadata
	}    

	@Override
	public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
	{
		dropBlockAsItem( world, i, j, k, iMetadata, 0 );
	}

	@Override
	public boolean hasMortar(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}

	@Override
	public boolean isBlockInfestable(EntityLiving entity, int metadata)
	{
		return (entity instanceof EntitySilverfish);
	}

	@Override
	public int getBlockIDOnInfest(EntityLiving entity, int metadata) {
		switch(metadata) {
		case 0:
			return BTWBlocks.infestedStoneBrick.blockID;
		case 1:
			return BTWBlocks.infestedMossyStoneBrick.blockID;
		case 2:
			return BTWBlocks.infestedCrackedStoneBrick.blockID;
		case 3:
			return BTWBlocks.infestedChiseledStoneBrick.blockID;
		case 4:
			return BTWBlocks.infestedMidStrataStoneBrick.blockID;
		case 5:
			return BTWBlocks.infestedMidStrataMossyStoneBrick.blockID;
		case 6:
			return BTWBlocks.infestedMidStrataCrackedStoneBrick.blockID;
		case 7:
			return BTWBlocks.infestedMidStrataChiseledStoneBrick.blockID;
		case 8:
			return BTWBlocks.infestedDeepStrataStoneBrick.blockID;
		case 9:
			return BTWBlocks.infestedDeepStrataMossyStoneBrick.blockID;
		case 10:
			return BTWBlocks.infestedDeepStrataCrackedStoneBrick.blockID;
		case 11:
			return BTWBlocks.infestedDeepStrataChiseledStoneBrick.blockID;
		default:
			return BTWBlocks.infestedStoneBrick.blockID;
		}

	}

	//------------- Class Specific Methods ------------//    

	/** normal mossy cracked chiseled */
	public int getStoneType(int metadata) {
		return metadata & 3;
	}
	
	/**
	 * returns 0 - 2 regardless of what metadata is used to store strata. 
	 * BEWARE: different blocks store strata differently
	 */
    public int getStrata( IBlockAccess blockAccess, int i, int j, int k ) {
		return getStrata( blockAccess.getBlockMetadata( i, j, k ) );
    }
    
	/**
	 * returns 0 - 2 regardless of what metadata is used to store strata. 
	 * BEWARE: different blocks store strata differently
	 */
    public int getStrata( int iMetadata ) {
    	return ( iMetadata & 12 ) >>> 2;
    }
	
	//----------- Client Side Functionality -----------//

	private Icon[] iconArray;

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		// used only by pick block
		return world.getBlockMetadata(x, y, z);
	}

    @Environment(EnvType.CLIENT)
	public Icon getIcon(int par1, int par2)
	{
		return this.iconArray[par2];
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int var4 = 0; var4 < 12; ++var4)
		{
			par3List.add(new ItemStack(par1, 1, var4));
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.iconArray = new Icon[12];
		for ( int strata = 0; strata <3; strata++) {

			for (int blockType = 0; blockType < field_94407_b.length; blockType++) {
				
				String name = field_94407_b[blockType];
				if (strata != 0)
					name += ( "_" + strata);
					
				this.iconArray[blockType + (strata << 2)] = par1IconRegister.registerIcon(name);
			}
		}
	}
}