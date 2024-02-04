// FCMOD

package btw.block.blocks;

import btw.block.tileentity.FiniteTorchTileEntity;
import btw.client.fx.BTWEffectManager;
import btw.item.blockitems.FiniteBurningTorchBlockItem;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class FiniteBurningTorchBlock extends TorchBlockBurningBase
	implements ITileEntityProvider
{
	private boolean isBeingCrushed = false;
	
    public FiniteBurningTorchBlock(int iBlockID)
    {
    	super( iBlockID );
    	
        isBlockContainer = true;
    	
        setLightValue( 0.875F );
        
    	setUnlocalizedName( "fcBlockTorchFiniteBurning" );
    	
        setCreativeTab( null );
    }   

	@Override
    public TileEntity createNewTileEntity(World world )
    {
        return new FiniteTorchTileEntity();
    }
	
	@Override
    public void dropBlockAsItemWithChance( World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier )
	{
		// prevents normal drop
	}
    
    @Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
        
        if ( !world.isRemote )
        {
            FiniteTorchTileEntity tileEntity = (FiniteTorchTileEntity)world.getBlockTileEntity( i, j, k );

            if ( tileEntity != null )
            {
	            int iBurnCountdown = tileEntity.burnTimeCountdown;
	            
	            if ( iBurnCountdown > 0 && !isBeingCrushed)
	            {
	        		int iNewItemDamage = (int)(FiniteBurningTorchBlockItem.DAMAGE_TO_BURN_TIME_RATIO *
											   (float)(FiniteTorchTileEntity.MAX_BURN_TIME - tileEntity.burnTimeCountdown) );
	        		
	        		// the below has a minimum of 1 damage to ensure damage bar is initially displayed
	        		iNewItemDamage = MathHelper.clamp_int( iNewItemDamage, 1, FiniteBurningTorchBlockItem.MAX_DAMAGE - 1);
	        		
		            ItemStack stack = new ItemStack( blockID, 1, iNewItemDamage );
		            
		            long iExpiryTime = WorldUtils.getOverworldTimeServerOnly() + (long)iBurnCountdown;
		            
		            stack.setTagCompound( new NBTTagCompound() );
		            stack.getTagCompound().setLong( "outTime", iExpiryTime);
		
		            dropBlockAsItem_do( world, i, j, k, stack );
	            }
            }
        }

		isBeingCrushed = false;
        
        world.removeBlockTileEntity(i, j, k);
    }
    
	@Override
	public void onBlockPlacedBy( World world, int i, int j, int k, EntityLiving entity, ItemStack stack )
	{
        TileEntity tileEntity = world.getBlockTileEntity( i, j, k );

        if ( tileEntity != null && tileEntity instanceof FiniteTorchTileEntity)
        {
            if ( stack.hasTagCompound() && stack.getTagCompound().hasKey( "outTime" ) )
            {
            	long lExpiryTime = stack.getTagCompound().getLong( "outTime" );
            	
            	int iCountDown = (int)( lExpiryTime - WorldUtils.getOverworldTimeServerOnly() );
            	
            	if ( iCountDown < 0 || iCountDown > FiniteTorchTileEntity.MAX_BURN_TIME)
            	{
            		iCountDown = FiniteTorchTileEntity.MAX_BURN_TIME;
            	}
            	
            	((FiniteTorchTileEntity)tileEntity).burnTimeCountdown = iCountDown;
            	
            	if ( iCountDown < FiniteTorchTileEntity.SPUTTER_TIME)
            	{
            		setIsSputtering(world, i, j, k, true);
            	}
            }
        }
	}

	@Override
    public boolean canBeCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
    	return true;
    }
    
	@Override
    public void onCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
		isBeingCrushed = true;
    }
    
    @Override
	public void onFluidFlowIntoBlock(World world, int i, int j, int k, BlockFluid newBlock)
	{
        world.playAuxSFX(BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );

		isBeingCrushed = true;
	}

	@Override
    public boolean onRotatedAroundBlockOnTurntableToFacing(World world, int i, int j, int k, int iFacing)
    {
		world.setBlockToAir( i, j, k );
		
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
    
	public void setIsSputtering(World world, int i, int j, int k, boolean bSputtering)
	{
		int iMetadata = setIsSputtering(world.getBlockMetadata(i, j, k), bSputtering);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	static public int setIsSputtering(int iMetadata, boolean bIsSputtering)
	{
		if ( bIsSputtering )
		{
			iMetadata |= 8;
		}
		else
		{
			iMetadata &= (~8);
		}
		
		return iMetadata;
	}
    
	public boolean getIsSputtering(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getIsSputtering(blockAccess.getBlockMetadata(i, j, k));
	}
	
	static public boolean getIsSputtering(int iMetadata)
	{
		return ( iMetadata & 8 ) != 0;
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconSputtering;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconSputtering = register.registerIcon("fcBlockTorchFiniteSputtering");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if ( getIsSputtering(iMetadata) )
		{
			return iconSputtering;
		}
		
		return super.getIcon( iSide, iMetadata );
    }
}
    
