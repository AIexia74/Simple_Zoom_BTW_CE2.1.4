// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.item.items.ChiselItem;
import btw.item.items.PickaxeItem;
import btw.item.items.ToolItem;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class StoneBlock extends FullBlock
{
    public StoneBlock(int iBlockID )
    {
        super( iBlockID, Material.rock );
        
        setHardness( 2.25F );
        setResistance( 10F );
        
        setPicksEffectiveOn();
        setChiselsEffectiveOn();
        
        setStepSound( soundStoneFootstep );
        
        setUnlocalizedName( "stone" );        
        
        setCreativeTab( CreativeTabs.tabBlock );
    }
    
    @Override
    public float getBlockHardness(World world, int i, int j, int k )
    {
    	int iStrata = getStrata(world, i, j, k);
    	
    	if ( iStrata != 0 )
    	{
    		// normal stone has a hardness of 2.25
    		
	    	if ( iStrata == 1 )
	    	{
	    		return 3.0F;
	    	}
	    	else
	    	{
	    		return 4.5F; 
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
    		// normal stone has a resistance of 10
    		
	    	if ( iStrata == 1 )
	    	{
	    		return 13F * ( 3.0F / 5.0F );
	    	}
	    	else
	    	{
	    		return  20F * ( 3.0F / 5.0F );
	    	}
    	}
    	
        return super.getExplosionResistance( entity, world, i, j, k );
    }
    
    @Override
    public int idDropped( int iMetaData, Random random, int iFortuneModifier )
    {
        return BTWBlocks.looseCobblestone.blockID;
    }
    
    @Override
	public int damageDropped(int metadata)
	{
		return getStrata(metadata) << 2; // loose cobblestone uses last 2 bits for strata
	}
    
	@Override
    public void dropBlockAsItemWithChance( World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier )
    {
		super.dropBlockAsItemWithChance( world, i, j, k, iMetadata, fChance, iFortuneModifier );
		
        if ( !world.isRemote )
        {
        	dropBlockAsItem_do( world, i, j, k, new ItemStack(BTWItems.stone, 1, getStrata(iMetadata) ));
        	
        	if ( !getIsCracked(iMetadata) )
        	{
            	dropBlockAsItem_do( world, i, j, k, new ItemStack( BTWItems.gravelPile) );
        	}
        }
    }
	
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.stone.itemID, 5, getStrata(iMetadata), fChanceOfDrop);
		
		int iNumGravel = getIsCracked(iMetadata) ? 2 : 3;
		
		dropItemsIndividually(world, i, j, k, BTWItems.gravelPile.itemID, iNumGravel,
							  0, fChanceOfDrop);
		
		return true;
	}
	
    @Override
    public boolean canConvertBlock(ItemStack stack, World world, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public boolean convertBlock(ItemStack stack, World world, int i, int j, int k, int iFromSide)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k );
    	int iStrata = getStrata(iMetadata);
    	
    	int iToolLevel = getConversionLevelForTool(stack, world, i, j, k);
    	
    	if ( getIsCracked(iMetadata) )
    	{
    		world.setBlockAndMetadataWithNotify(i, j, k, RoughStoneBlock.strataLevelBlockArray[iStrata].blockID, 0);
    		
        	if ( !world.isRemote && iToolLevel > 0 )
        	{
    	        world.playAuxSFX( BTWEffectManager.STONE_RIPPED_OFF_EFFECT_ID, i, j, k, 0 );
    	        
                ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k,
                                                           new ItemStack( BTWItems.stone, 1 , iStrata), iFromSide);
        	}
    	}
    	else
    	{
        	if ( iToolLevel == 2 )
        	{
        		// level 2 is stone pick on top strata stone, which has its own thing going on
        		
        		world.setBlockAndMetadataWithNotify(i, j, k, RoughStoneBlock.strataLevelBlockArray[iStrata].blockID, 4);
    	        
    			if ( !world.isRemote )
    			{
        	        world.playAuxSFX( BTWEffectManager.STONE_RIPPED_OFF_EFFECT_ID, i, j, k, 0 );
	    	        
                    ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k,
                                                               new ItemStack( BTWItems.stone, 3 , iStrata), iFromSide);
                    
	                ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k,
                                                               new ItemStack( BTWItems.gravelPile, 1 ), iFromSide);
    			}
    			
        	}
        	else if ( iToolLevel == 3 )
    		{
        		// level 3 is iron chisel on first two strata, resulting in stone brick
        		
        		world.setBlockAndMetadataWithNotify(i, j, k,
                                                    RoughStoneBlock.strataLevelBlockArray[iStrata].blockID, 2);
        		
    			if ( !world.isRemote )
    			{
        	        world.playAuxSFX( BTWEffectManager.STONE_RIPPED_OFF_EFFECT_ID, i, j, k, 0 );
	    	        
                    ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k,
                                                               new ItemStack( BTWItems.stoneBrick, 1, iStrata ), iFromSide);
                    
	                ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k,
                                                               new ItemStack( BTWItems.gravelPile, 1 ), iFromSide);
    			}
    		}
    		else
    		{
    			if ( !world.isRemote )
    			{
	    	        world.playAuxSFX( BTWEffectManager.GRAVEL_RIPPED_OFF_EFFECT_ID, i, j, k, 0 );
	    	        
	                ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k,
                                                               new ItemStack( BTWItems.gravelPile, 1 ), iFromSide);
    			}
                
    			setIsCracked(world, i, j, k, true);
    		}
    	}
    	
    	return true;
    }

    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iStrata = getStrata(blockAccess, i, j, k);
    	
    	if ( iStrata > 1 )
    	{
    		return iStrata + 1;
    	}
    	
    	return 2;
    }
    
    @Override
    public int getEfficientToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iStrata = getStrata(blockAccess, i, j, k);
    	
    	if ( iStrata > 0 )
    	{
    		return iStrata + 1;
    	}
    	
    	return 0;
    }
    
    @Override
    public boolean isNaturalStone(IBlockAccess blockAccess, int i, int j, int k)
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
    	int strata = getStrata(metadata);

    	if (strata == 1) {
    		return BTWBlocks.infestedMidStrataStone.blockID;
    	}
    	else if (strata == 2) {
    		return BTWBlocks.infestedDeepStrataStone.blockID;
    	}

    	return BTWBlocks.infestedStone.blockID;
    }
    
    //------------- Class Specific Methods ------------//

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
        		
        		if (iToolLevel >= getEfficientToolLevel(world, i, j, k) )
        		{
        			return 2;        				
        		}
        	}  
        	else if ( stack.getItem() instanceof ChiselItem)
        	{
        		int iToolLevel = ((ToolItem)stack.getItem()).toolMaterial.getHarvestLevel();
        		
        		if (iToolLevel >= getEfficientToolLevel(world, i, j, k) )
        		{
            		if (iToolLevel >= getUberToolLevel(world, i, j, k) )
            		{
            			return 3;
            		}
            		
        			return 1;
        		}
        	}  
    	}
    	
    	return 0;
    }
    
    public int getStrata(IBlockAccess blockAccess, int i, int j, int k)
    {
		return getStrata(blockAccess.getBlockMetadata(i, j, k));
    }
    
    public int getStrata(int iMetadata)
    {
    	return iMetadata & 3;
    }
    
	public void setIsCracked(World world, int i, int j, int k, boolean bCracked)
	{
		int iMetadata = setIsCracked(world.getBlockMetadata(i, j, k), bCracked);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	public int setIsCracked(int iMetadata, boolean bIsCracked)
	{
		if ( bIsCracked )
		{
			iMetadata |= 4;
		}
		else
		{
			iMetadata &= (~4);
		}
		
		return iMetadata;
	}
    
	public boolean getIsCracked(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getIsCracked(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public boolean getIsCracked(int iMetadata)
	{
		return ( iMetadata & 4 ) != 0;
	}
	
    public int getUberToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2;
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconByMetadataArray = new Icon[16];

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list )
    {
        list.add( new ItemStack( iBlockID, 1, 0 ) );
        list.add( new ItemStack( iBlockID, 1, 1 ) );
        list.add( new ItemStack( iBlockID, 1, 2 ) );
        //list.add( new ItemStack( iBlockID, 1, 4 ) );
        //list.add( new ItemStack( iBlockID, 1, 5 ) );
        //list.add( new ItemStack( iBlockID, 1, 6 ) );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getDamageValue(World world, int x, int y, int z) {
		// used only by pick block
		return world.getBlockMetadata(x, y, z);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconByMetadataArray[0] = blockIcon;
		iconByMetadataArray[1] = register.registerIcon("fcBlockStone_1");
		iconByMetadataArray[2] = register.registerIcon("fcBlockStone_2");
		iconByMetadataArray[3] = blockIcon;

		iconByMetadataArray[4] = register.registerIcon("fcBlockStone_cracked");;
		iconByMetadataArray[5] = register.registerIcon("fcBlockStone_1_cracked");;
		iconByMetadataArray[6] = register.registerIcon("fcBlockStone_2_cracked");;
		iconByMetadataArray[7] = blockIcon;
		
		for ( int iTempIndex = 8; iTempIndex < 16; iTempIndex++ )
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
}