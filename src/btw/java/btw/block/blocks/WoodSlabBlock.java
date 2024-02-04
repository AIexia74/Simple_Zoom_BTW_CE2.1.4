// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class WoodSlabBlock extends BlockHalfSlab
{
    public static final String[] woodType = new String[] {"oak", "spruce", "birch", "jungle", "blood" };

    public WoodSlabBlock(int iBlockID, boolean bDoubleSlab )
    {
        super( iBlockID, bDoubleSlab, BTWBlocks.plankMaterial);
        
        setHardness( 1F );
        setResistance( 5F );
        setAxesEffectiveOn();
        
        setBuoyant();
        
		setFireProperties(Flammability.PLANKS);
		
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "woodSlab" );
        
        setCreativeTab( CreativeTabs.tabBlock );
    }

    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return Block.woodSingleSlab.blockID;
    }
    
    @Override
    public String getFullSlabName( int iMetadata )
    {
        return super.getUnlocalizedName() + "." + woodType[iMetadata];
    }    
    
    @Override
    protected ItemStack createStackedBlock( int iMetadata )
    {
        return new ItemStack( Block.woodSingleSlab.blockID, 2, iMetadata & 7 );
    }

    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2; // iron or better
    }
    
    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
    {
    	int iNumItems = isDoubleSlab ? 2 : 1;
    	
    	for ( int iTempCount = 0; iTempCount < iNumItems; iTempCount++ )
    	{
			dropBlockAsItem_do( world, i, j, k, new ItemStack( BTWItems.sawDust) );
    	}
    }
    
    @Override
    public int getFurnaceBurnTime(int iItemDamage)
    {
    	int iBurnTime = PlanksBlock.getFurnaceBurnTimeByWoodType(iItemDamage);
    	
    	if ( !isDoubleSlab )
    	{
    		iBurnTime >>= 1;
    	}
    	
    	return iBurnTime;
    }    
		
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list )
    {
        if ( iBlockID != Block.woodDoubleSlab.blockID )
        {
            for ( int iTempDamage = 0; iTempDamage <= 4; ++iTempDamage)
            {
                list.add( new ItemStack( iBlockID, 1, iTempDamage) );
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register ) {}

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
        return Block.planks.getIcon( iSide, iMetadata & 7 );
    }
}
