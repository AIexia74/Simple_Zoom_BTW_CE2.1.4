// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class FurnaceBlock extends BlockFurnace
{
    public FurnaceBlock(int iBlockID, boolean bIsLit)
    {
        super( iBlockID, bIsLit );
        
        setStepSound( soundStoneFootstep );
        
        setHardness( 3F );
        setResistance( 5.83F ); // odd value to match vanilla hardness of 3.5F
        
        if ( !bIsLit )
        {
        	setCreativeTab( CreativeTabs.tabDecorations );
        }
        else
        {
        	setLightValue( 0.875F );        
    	}
        
        setUnlocalizedName( "furnace" );        
    }
    
    @Override
    public int quantityDropped( Random rand )
    {
        return 12 + rand.nextInt( 5 );
    }

    @Override
    public int idDropped( int iMetaData, Random random, int iFortuneModifier )
    {
        return BTWItems.stone.itemID;
    }
    
	@Override
    public void harvestBlock(World world, EntityPlayer player, int i, int j, int k, int iMetadata )
    {
		// override to handle funkiness of silk-touching a container block
		
        player.addStat( StatList.mineBlockStatArray[this.blockID], 1 );
        player.addExhaustion( 0.025F );

        if ( EnchantmentHelper.getSilkTouchModifier( player ) )
        {
            ItemStack dropStack = new ItemStack(iddroppedsilktouch(), 1, 0 );

            if ( dropStack != null )
            {
                dropBlockAsItem_do( world, i, j, k, dropStack );
            }
        }
        else
        {
            int iFortuneModifier = EnchantmentHelper.getFortuneModifier( player );
            
            dropBlockAsItem( world, i, j, k, iMetadata, iFortuneModifier );
        }
    }
    
	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
		/*
		if ( isActive )
		{
	    	ItemStack stack = player.getCurrentEquippedItem();
	    	
    		if ( stack != null && stack.getItem().GetCanItemBeSetOnFireOnUse( stack.getItemDamage() ) )
    		{
    			return false;
    		}
    	}
    	*/
    	
    	return super.onBlockActivated( world, i, j, k, player, iFacing, fXClick, fYClick, fZClick );
    }
	
    @Override
    public boolean getCanBlockLightItemOnFire(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return isActive;
    }
    
    @Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
    @Override
	public boolean canTransmitRotationHorizontallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
    @Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
    
    @Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		int iFacing = iMetadata & 7;
		
		iFacing = Block.rotateFacingAroundY(iFacing, bReverse);
		
		return ( iMetadata & (~7) ) | iFacing;
	}
	
	//------------- Class Specific Methods ------------//
	
	protected int iddroppedsilktouch()
	{
		return Block.furnaceIdle.blockID;
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    protected Icon iconFullFront;
    @Environment(EnvType.CLIENT)
    protected Icon iconFullFrontLit;

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		int iMetadataStripped = iMetadata & 7;
		boolean bHasContents = ( iMetadata & 8 ) != 0;
		
		if ( iMetadataStripped == iSide && bHasContents )
		{
			if ( isActive )
			{
				return iconFullFrontLit;
			}
			else
			{
				return iconFullFront;
			}
		}
		
		return super.getIcon( iSide, iMetadataStripped );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons(  register );

        iconFullFront = register.registerIcon("fcBlockFurnaceFullFront");
        iconFullFrontLit = register.registerIcon("fcBlockFurnaceFullFrontLit");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random rand )
    {
        if ( isActive && rand.nextInt( 3 ) == 0 )
        {
            world.playSound( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, "fire.fire", 
            	0.25F + rand.nextFloat() * 0.25F, 
            	0.5F + rand.nextFloat() * 0.25F, false );
        }
        
        super.randomDisplayTick( world, i, j, k, rand );
    }
}