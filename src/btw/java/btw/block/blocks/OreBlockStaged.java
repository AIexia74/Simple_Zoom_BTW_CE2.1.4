// FCMOD

package btw.block.blocks;

import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.item.items.ChiselItem;
import btw.item.items.PickaxeItem;
import btw.item.items.ToolItem;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;

import java.util.Random;

public abstract class OreBlockStaged extends OreBlock
{
    public OreBlockStaged(int iBlockID )
    {
        super( iBlockID );
        
        setChiselsEffectiveOn();
    }
    
    @Override
    public boolean canConvertBlock(ItemStack stack, World world, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public boolean convertBlock(ItemStack stack, World world, int i, int j, int k, int iFromSide)
    {
    	int iOldMetadata = world.getBlockMetadata( i, j, k );
    	int iStrata = getStrata(iOldMetadata);
    	
		world.setBlockAndMetadataWithNotify(i, j, k, RoughStoneBlock.strataLevelBlockArray[iStrata].blockID, 4);
    	
    	if ( !world.isRemote )
    	{
    		int iLevel = getConversionLevelForTool(stack, world, i, j, k);

    		if ( iLevel > 0 )
    		{
		        world.playAuxSFX( BTWEffectManager.STONE_RIPPED_OFF_EFFECT_ID, i, j, k, 0 );
		        
	    		if ( iLevel >= 3 )
				{
	    			ejectItemsOnGoodPickConversion(stack, world, i, j, k, iOldMetadata, iFromSide);
				}
	    		else if ( iLevel == 2 )
	    		{
	    			ejectItemsOnStonePickConversion(stack, world, i, j, k, iOldMetadata, iFromSide);
	    		}
	    		else
	    		{
	    			ejectItemsOnChiselConversion(stack, world, i, j, k, iOldMetadata, iFromSide);
	    		}
    		}
    	}
    	
    	return true;
    }
    
	@Override
    public void dropBlockAsItemWithChance( World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier )
    {
		super.dropBlockAsItemWithChance( world, i, j, k, iMetadata, fChance, iFortuneModifier );
		
        if ( !world.isRemote )
        {
    		dropItemsIndividually(world, i, j, k, BTWItems.stone.itemID, 6, getStrata(iMetadata), 1F);
        }
    }
    
    @Override
    public int getEfficientToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getRequiredToolLevelForOre(blockAccess, i, j, k);
    }

    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iLevelForOre = getRequiredToolLevelForOre(blockAccess, i, j, k);
		int iLevelForStrata = getRequiredToolLevelForStrata(blockAccess, i, j, k);
		
		if ( iLevelForStrata > iLevelForOre )
		{
			return iLevelForStrata;
		}
		
		return iLevelForOre;		
    }
    
    //------------- Class Specific Methods ------------//	
    
    public abstract int idDroppedOnConversion(int iMetadata);
    
    public int damageDroppedOnConversion(int iMetadata)
    {
    	return 0;
    }
    
    public int quantityDroppedOnConversion(Random rand)
    {
        return 1;
    }

    public int idDroppedOnStonePickConversion(int iMetadata, Random rand, int iFortuneModifier)
    {
    	return idDropped( iMetadata, rand, iFortuneModifier );
    }
    
    public int damageDroppedOnStonePickConversion(int iMetadata)
    {
    	return damageDropped( iMetadata );
    }
    
    public int quantityDroppedOnStonePickConversion(Random rand)
    {
        return quantityDropped( rand );
    }

    protected void ejectItemsOnGoodPickConversion(ItemStack stack, World world, int i, int j, int k, int iOldMetadata, int iFromSide)
    {
        ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k,
                                                   new ItemStack( idDropped( iOldMetadata, world.rand, 0 ),
    		quantityDropped( world.rand ), 
    		damageDropped( iOldMetadata ) ), iFromSide);
    }
    
    protected void ejectItemsOnStonePickConversion(ItemStack stack, World world, int i, int j, int k, int iOldMetadata, int iFromSide)
    {
        ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k,
                                                   new ItemStack(idDroppedOnStonePickConversion(iOldMetadata, world.rand, 0),
																 quantityDroppedOnStonePickConversion(world.rand),
																 damageDroppedOnStonePickConversion(iOldMetadata) ), iFromSide);
    }
    
    protected void ejectItemsOnChiselConversion(ItemStack stack, World world, int i, int j, int k, int iOldMetadata, int iFromSide)
    {
        ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k,
                                                   new ItemStack(idDroppedOnConversion(iOldMetadata),
																 quantityDroppedOnConversion(world.rand),
																 damageDroppedOnConversion(iOldMetadata) ), iFromSide);
    }
    
    public int getRequiredToolLevelForOre(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 0;
    }

    /**
     * Returns 1, 2, or 3 depending on the level of the conversion tool.  0 if it can't convert
     */ 
    private int getConversionLevelForTool(ItemStack stack, World world, int i, int j, int k)
    {
    	if ( stack != null )
    	{
        	if ( stack.getItem() instanceof PickaxeItem)
        	{
        		int iToolLevel = ((ToolItem)stack.getItem()).toolMaterial.getHarvestLevel();
        		
        		if (iToolLevel >= getRequiredToolLevelForOre(world, i, j, k) )
        		{
        			if ( iToolLevel > 1 )
        			{
        				return 3;
        			}
        			
        			return 2;        				
        		}
        	}  
        	else if ( stack.getItem() instanceof ChiselItem)
        	{
        		int iToolLevel = ((ToolItem)stack.getItem()).toolMaterial.getHarvestLevel();
        		
        		if (iToolLevel >= getRequiredToolLevelForOre(world, i, j, k) )
        		{
        			return 1;
        		}
        	}  
    	}
    	
    	return 0;
    }
    
	//------------ Client Side Functionality ----------//
}
