// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.item.items.ChiselItem;
import btw.item.util.ItemUtils;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class WorkStumpBlock extends Block
{
    public WorkStumpBlock(int iBlockID)
    {
        super( iBlockID, BTWBlocks.logMaterial);
        
		setHardness( 1.25F ); // log hardness
		setChiselsEffectiveOn();
		
		setFireProperties(Flammability.LOGS);
		
        setUnlocalizedName( "fcBlockWorkStump" );
    }
    
    @Override
    public float getBlockHardness(World world, int i, int j, int k )
    {
    	// doing it this way instead of just setting the hardness in the constructor to replicate behavior of log stumps
    	
        return super.getBlockHardness( world, i, j, k ) * 3; 
    }
    
	@Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
		// prevent access if solid block above
		
		if ( !world.isRemote && !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j + 1, k, 0) ) {
			int metadata = world.getBlockMetadata(i, j, k);
			
			if ((isFinishedWorkStump(metadata))) {
				player.displayGUIWorkbench(i, j, k);
			}
		}			
		
        return true;
    }
	
	private boolean isFinishedWorkStump(int metadata) {
		return (metadata & 8) == 0;
	}
	
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 6, 0, fChanceOfDrop);
		
		return true;
	}
	
	@Override
    public void dropBlockAsItemWithChance( World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier )
    {
        if ( !world.isRemote )
        {        
            dropBlockAsItem_do( world, i, j, k, new ItemStack( BTWItems.sawDust, 3, 0 ) );
            
            dropBlockAsItem_do( world, i, j, k, new ItemStack( Block.planks.blockID, 1, 0 ) );
        }
    }
	
    @Override
    public boolean canConvertBlock(ItemStack stack, World world, int i, int j, int k)
    {
    	return true;
    }
	
	@Override
	public boolean convertBlock(ItemStack stack, World world, int i, int j, int k, int iFromSide) {
		int oldMetadata = world.getBlockMetadata(i, j, k);
		
		int chewedLogID = LogBlock.chewedLogArray[oldMetadata & 3].blockID;
		
		if (!isFinishedWorkStump(oldMetadata) && isWorkStumpItemConversionTool(stack, world, i, j, k)) {
			world.playAuxSFX(BTWEffectManager.SHAFT_RIPPED_OFF_EFFECT_ID, i, j, k, 0);
			world.setBlockMetadataWithNotify(i, j, k, oldMetadata & 3);
			return true;
		}
		
		int newMetadata = BTWBlocks.oakChewedLog.setIsStump(0);
		
		world.setBlockAndMetadataWithNotify(i, j, k, chewedLogID, newMetadata);
		
		if (!world.isRemote) {
			ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k, new ItemStack(BTWItems.bark, 1, oldMetadata & 3), iFromSide);
		}
		
		return true;
	}
	
	public boolean isWorkStumpItemConversionTool(ItemStack stack, World world, int i, int j, int k)
	{
		if ( stack != null && stack.getItem() instanceof ChiselItem)
		{
			int iToolLevel = ((ChiselItem)stack.getItem()).toolMaterial.getHarvestLevel();
			
			return iToolLevel >= 2;
		}
		
		return false;
	}
	
	@Override
	public boolean getIsProblemToRemove(ItemStack toolStack, IBlockAccess blockAccess, int i, int j, int k) {
		int metadata = blockAccess.getBlockMetadata(i, j, k);
		
		return isFinishedWorkStump(metadata) || !isWorkStumpItemConversionTool(toolStack, (World) blockAccess, i, j, k);
	}
    
    @Override
    public boolean getDoesStumpRemoverWorkOnBlock(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k)
    {
    	return Block.wood.getStackRetrievedByBlockDispenser(world, i, j, k);
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    public static final String[] sideTextureNames = new String[] {
			"fcBlockWorkStumpOak",
			"fcBlockWorkStumpSpruce",
			"fcBlockWorkStumpBirch",
			"fcBlockWorkStumpJungle"
	};
	
	@Environment(EnvType.CLIENT)
	public static final String[] topTextureNames = new String[] {
			"fcBlockTrunkTop",
			"fcBlockTrunkTopSpruce",
			"fcBlockTrunkTopBirch",
			"fcBlockTrunkTopJungle"
	};

    @Environment(EnvType.CLIENT)
    private Icon[] iconSideArray;
    @Environment(EnvType.CLIENT)
    private Icon[] iconTopArray;

    @Override
	@Environment(EnvType.CLIENT)
	public Icon getIcon(int iSide, int iMetadata) {
		if (iSide > 1) {
			return iconSideArray[iMetadata & 3];
		}
		else if (iSide == 1) {
			if (!isFinishedWorkStump(iMetadata)) {
				return iconTopArray[iMetadata & 3];
			}
			else {
				return blockIcon;
			}
		}
		else {
			return iconTopArray[iMetadata & 3];
		}
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		blockIcon = register.registerIcon("workbench_top");

        iconSideArray = new Icon[sideTextureNames.length];
		iconTopArray = new Icon[sideTextureNames.length];

        for (int iTextureID = 0; iTextureID < iconSideArray.length; iTextureID++ )
        {
            iconSideArray[iTextureID] = register.registerIcon(sideTextureNames[iTextureID]);
			iconTopArray[iTextureID] = register.registerIcon(topTextureNames[iTextureID]);
        }
    }
}
