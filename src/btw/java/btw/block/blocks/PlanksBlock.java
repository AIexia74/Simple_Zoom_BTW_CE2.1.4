// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.crafting.util.FurnaceBurnTime;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class PlanksBlock extends Block
{
    public static final String[] woodTypes = new String[] {"oak", "spruce", "birch", "jungle", "blood" };
    public static final String[] woodTextureTypes = new String[] {"wood", "wood_spruce", "wood_birch", "wood_jungle", "fcBlockPlanks_blood" };

    public PlanksBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.plankMaterial);
        
        setAxesEffectiveOn();
        
        setHardness( 1F );
        setResistance( 5F );
        
		setFireProperties(Flammability.PLANKS);
		
        setBuoyant();
        
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName("wood");        
        
        setCreativeTab( CreativeTabs.tabBlock );
    }
    
    @Override
    public int damageDropped( int iMetadata )
    {
        return iMetadata;
    }
    
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2; // iron or better
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 2, 0, fChanceOfDrop);
		
		return true;
	}
    
    @Override
    public int getFurnaceBurnTime(int iItemDamage)
    {
    	return getFurnaceBurnTimeByWoodType(iItemDamage);
    }
    
    //------------- Class Specific Methods ------------//
	
	public static int getFurnaceBurnTimeByWoodType(int iWoodType)
	{
		if ( iWoodType == 0 ) // oak
		{
			return FurnaceBurnTime.PLANKS_OAK.burnTime;
		}
		else if ( iWoodType == 1 ) // spruce
		{
			return FurnaceBurnTime.PLANKS_SPRUCE.burnTime;
		}
		else if ( iWoodType == 2 ) // birch
		{
			return FurnaceBurnTime.PLANKS_BIRCH.burnTime;
		}
		else if ( iWoodType == 3 ) // jungle
		{
			return FurnaceBurnTime.PLANKS_JUNGLE.burnTime;
		}
		else // blood == 4
		{
			return FurnaceBurnTime.PLANKS_BLOOD.burnTime;
		}
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconArray;

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		return iconArray[iMetadata];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list )
    {
    	for (int iTempType = 0; iTempType < woodTypes.length; iTempType++ )
    	{
    		list.add( new ItemStack(iBlockID, 1, iTempType ) );
    	}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        iconArray = new Icon[woodTextureTypes.length];

        for (int iTempIndex = 0; iTempIndex < iconArray.length; ++iTempIndex )
        {
            iconArray[iTempIndex] = register.registerIcon(woodTextureTypes[iTempIndex]);
        }
    }
}
