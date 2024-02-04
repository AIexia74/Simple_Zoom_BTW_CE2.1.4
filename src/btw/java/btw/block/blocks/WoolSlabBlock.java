// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class WoolSlabBlock extends SlabBlock
{
	private boolean isUpsideDown;
	
    public final static int NUM_SUBTYPES = 16;
    
    public WoolSlabBlock(int iBlockID, boolean bIsUpsideDown )
    {
        super( iBlockID, Material.cloth );

        setHardness( 0.8F );
        
        setBuoyancy(1F);

        isUpsideDown = bIsUpsideDown;

        if ( !bIsUpsideDown )
        {        	
            initBlockBounds(0F, 0F, 0F, 1F, 0.5F, 1F);
        }
        else
        {
        	initBlockBounds(0F, 0.5F, 0F, 1F, 1F, 1F);
        }
        
        setStepSound( soundClothFootstep );
        
        setUnlocalizedName( "fcBlockWoolSlab" );

		setCreativeTab( CreativeTabs.tabBlock );
    }

	@Override
    public int idDropped( int iMetaData, Random random, int iFortuneModifier )
    {
		// override so that a normal slab is dropped regardless of whether this one is upside down or not
		
        return BTWBlocks.woolSlab.blockID;
    }
	
	@Override
    public int damageDropped( int i )
    {
        return i;
    }
    
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
	
	@Override
    protected boolean canSilkHarvest()
    {
		// to prevent silk touch from overriding and potential harvesting an upside down slab
		
        return false;
    }    
    
    //------------- FCBlockSlab ------------//
    
	@Override
    public boolean getIsUpsideDown(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return isUpsideDown;
    }
    
	@Override
    public boolean getIsUpsideDown(int iMetadata)
    {
    	return isUpsideDown;
    }
    
	@Override
    public void setIsUpsideDown(World world, int i, int j, int k, boolean bUpsideDown)
    {
		if (isUpsideDown != bUpsideDown )
		{
			int iNewBlockID = BTWBlocks.woolSlabTop.blockID;
			int iMetadata = world.getBlockMetadata( i, j, k );
			
			if ( blockID == BTWBlocks.woolSlabTop.blockID )
			{
				iNewBlockID = BTWBlocks.woolSlab.blockID;
			}
			
			world.setBlockAndMetadataWithNotify( i, j, k, iNewBlockID, iMetadata );
		}
    }
	
	@Override
    public int setIsUpsideDown(int iMetadata, boolean bUpsideDown)
	{
		// this can't be done given the 2 different block types used here
		
		return iMetadata;
	}
	
	@Override
	public int getCombinedBlockID(int iMetadata)
	{
		return Block.cloth.blockID;
	}
	
	@Override
	public int getCombinedMetadata(int iMetadata)
	{
		return iMetadata;
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconByColorArray;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        iconByColorArray = new Icon[16];

        for (int iColor = 0; iColor < this.iconByColorArray.length; ++iColor )
        {
            iconByColorArray[iColor] = register.registerIcon("cloth_" + iColor);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
        return iconByColorArray[iMetadata % iconByColorArray.length];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list )
    {
		if ( !isUpsideDown)
		{
			for (int iSubtype = 0; iSubtype < NUM_SUBTYPES; iSubtype++ )
			{
				list.add( new ItemStack( iBlockID, 1, iSubtype ) );
			}
		}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return idDropped( world.getBlockMetadata( i, j, k ), world.rand, 0 );
    }
}