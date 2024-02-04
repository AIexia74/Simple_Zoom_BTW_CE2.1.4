// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class OreBlock extends BlockOre
{
    public OreBlock(int iBlockID )
    {
        super( iBlockID );
        
        setPicksEffectiveOn();
    }

    @Override
    public boolean hasStrata()
    {
    	return true;
    }
    
    @Override
    public int getMetadataConversionForStrataLevel(int iLevel, int iMetadata)
    {
    	return iLevel;
    }
    
    @Override
    public float getBlockHardness(World world, int i, int j, int k )
    {
    	int iStrata = getStrata(world, i, j, k);
    	
    	if ( iStrata != 0 )
    	{
    		// normal ore has a hardness of 3
    		
	    	if ( iStrata == 1 )
	    	{
	    		return 4.0F;
	    	}
	    	else
	    	{
	    		return 6.0F; 
	    	}
    	}
    	
        return super.getBlockHardness( world, i, j, k );
    }
    
    @Override
    public float getExplosionResistance(Entity entity, World world, int i, int j, int k )
    {
    	int iStrata = getStrata(world, i, j, k);
    	
    	if ( iStrata != 0 )
    	{
    		// normal ore has a resistance of 5
    		
	    	if ( iStrata == 1 )
	    	{
	    		return 7F * ( 3.0F / 5.0F );
	    	}
	    	else
	    	{
	    		return  10F * ( 3.0F / 5.0F );
	    	}
    	}
    	
        return super.getExplosionResistance( entity, world, i, j, k );
    }
    
    //------------- Class Specific Methods ------------//
	
    public int getStrata(IBlockAccess blockAccess, int i, int j, int k)
    {
		return getStrata(blockAccess.getBlockMetadata(i, j, k));
    }
    
    public int getStrata(int iMetadata)
    {
    	return iMetadata & 3;
    }
    
    public int getRequiredToolLevelForStrata(IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iStrata = getStrata(blockAccess, i, j, k);
    	
    	if ( iStrata > 1 )
    	{
    		return iStrata + 1;
    	}
    	
    	return 2;
    }
    
    @Override
    public boolean isNaturalStone(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return true;
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconByMetadataArray = new Icon[16];

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

        iconByMetadataArray[0] = blockIcon;
        iconByMetadataArray[1] = register.registerIcon("fcBlock" + getUnlocalizedName2() + "Strata_1");
        iconByMetadataArray[2] = register.registerIcon("fcBlock" + getUnlocalizedName2() + "Strata_2");
		
		for ( int iTempIndex = 3; iTempIndex < 16; iTempIndex++ )
		{
            iconByMetadataArray[iTempIndex] = blockIcon;
		}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
        return iconByMetadataArray[iMetadata];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	return renderer.renderStandardFullBlock(this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult)
    {
        renderCookingByKiLnOverlay(renderBlocks, i, j, k, bFirstPassResult);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean doesItemRenderAsBlock(int iItemDamage)
    {
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list )
    {
        list.add( new ItemStack( iBlockID, 1, 0 ) );
        //list.add( new ItemStack( iBlockID, 1, 1 ) );
        //list.add( new ItemStack( iBlockID, 1, 2 ) );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockMovedByPiston(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	renderBlocks.renderStandardFullBlockMovedByPiston(this, i, j, k);
    }    
}