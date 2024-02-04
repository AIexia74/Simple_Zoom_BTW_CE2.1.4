// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.items.ChiselItem;
import btw.item.items.ShearsItem;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class WebBlock extends BlockWeb
{
    public WebBlock(int iBlockID )
    {
    	super( iBlockID );
        
        setAxesEffectiveOn(true);
        setChiselsEffectiveOn(true);
        
        setHardness( 4F );
        setBuoyant();
    	
    	setStepSound( BTWBlocks.stepSoundSquish);
        
        setUnlocalizedName( "web" );        
        
        setCreativeTab( null ); // creative tab has to be set for indivdual blocks, due to legacy vanilla block using this class
    }
    
	@Override
    public void harvestBlock(World world, EntityPlayer player, int i, int j, int k, int iMetadata )
    {
        if ( player.getCurrentEquippedItem() != null &&
             player.getCurrentEquippedItem().getItem() instanceof ShearsItem &&
             getDamageLevel(iMetadata) == 0 )
        {
            player.addStat( StatList.mineBlockStatArray[this.blockID], 1 );
            
            dropBlockAsItem_do( world, i, j, k, new ItemStack( BTWBlocks.web, 1, 0 ) );
        } 
        else
        {
            super.harvestBlock( world, player, i, j, k, iMetadata );
        }
    }

	@Override
    public int getEfficientToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 1;
    }
    
	@Override
    public int idDropped(int par1, Random par2Random, int par3)
    {
		// remove string drop so that cutting webs with swords or axes only destroys the web
		
        return 0;
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
    	int iDamageLevel = getDamageLevel(iOldMetadata);
    		
    	if ( iDamageLevel < 3 )
    	{
        	setDamageLevel(world, i, j, k, iDamageLevel + 1);
        	
        	return true;
    	}
    	else if (!world.isRemote && isEffectiveItemConversionTool(stack, world, i, j, k) )
    	{
            world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
            	"random.bow", 0.75F + world.rand.nextFloat() * 0.25F, world.rand.nextFloat() * 0.25F + 1.25F );
			
    		ItemUtils.dropStackAsIfBlockHarvested(world, i, j, k, new ItemStack(Item.silk, 1 ));
    	}
    	
    	return false;
    }

    //------------- Class Specific Methods ------------//
    
    public void setDamageLevel(World world, int i, int j, int k, int iDamageLevel)
    {
    	int iMetadata = setDamageLevel(world.getBlockMetadata(i, j, k), iDamageLevel);
    	
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }
    
    public int setDamageLevel(int iMetadata, int iDamageLevel)
    {
    	iMetadata &= (~3);
    	
    	return iMetadata | iDamageLevel;
    }
    
    public int getDamageLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getDamageLevel(blockAccess.getBlockMetadata(i, j, k));
    }
    
    public int getDamageLevel(int iMetadata)
    {
    	return iMetadata & 3;
    }
    
    public boolean isEffectiveItemConversionTool(ItemStack stack, World world, int i, int j, int k)
    {
    	if ( stack != null && stack.getItem() instanceof ChiselItem)
    	{
			int iToolLevel = ((ChiselItem)stack.getItem()).toolMaterial.getHarvestLevel();
			
			return iToolLevel >= getEfficientToolLevel(world, i, j, k);
    	}  
    	
    	return false;
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconByDamageArray = new Icon[4];

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		blockIcon = register.registerIcon( "web" );

        iconByDamageArray[0] = blockIcon;
        iconByDamageArray[1] = register.registerIcon("fcBlockWeb_1");
        iconByDamageArray[2] = register.registerIcon("fcBlockWeb_2");
        iconByDamageArray[3] = register.registerIcon("fcBlockWeb_3");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
        return iconByDamageArray[getDamageLevel(iMetadata)];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderCrossedSquares( this, i, j, k );
    }    
}
    
