// FCMOD

package btw.block.blocks;

import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.block.MechanicalBlock;
import btw.block.tileentity.PulleyTileEntity;
import btw.client.fx.BTWEffectManager;
import btw.inventory.BTWContainers;
import btw.inventory.container.PulleyContainer;
import btw.inventory.util.InventoryUtils;
import btw.block.util.MechPowerUtils;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class PulleyBlock extends BlockContainer
	implements MechanicalBlock
{
	private static final int PULLEY_TICK_RATE = 10;
	
	public PulleyBlock(int iBlockID)
	{
        super( iBlockID, BTWBlocks.plankMaterial);

        setHardness( 2F );
        
        setAxesEffectiveOn(true);
        
        setBuoyancy(1F);
        
		setFireProperties(Flammability.PLANKS);
		
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "fcBlockPulley" );
        
        setTickRandomly( true );
        
        setCreativeTab( CreativeTabs.tabRedstone );
	}
    
	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
        if ( !world.isRemote )
        {
            PulleyTileEntity tileEntityPulley = (PulleyTileEntity)world.getBlockTileEntity( i, j, k );
            
        	if ( player instanceof EntityPlayerMP ) // should always be true
        	{
        		PulleyContainer container = new PulleyContainer( player.inventory, tileEntityPulley );
        		
        		BTWMod.serverOpenCustomInterface( (EntityPlayerMP)player, container, BTWContainers.pulleyContainerID);
        	}
        }
        
        return true;
    }
    
	@Override
    public TileEntity createNewTileEntity( World world )
    {
        return new PulleyTileEntity();
    }

	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        super.onBlockAdded( world, i, j, k );
        
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    }

	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
    	TileEntity tileEntity = world.getBlockTileEntity(i, j, k);

    	if ( tileEntity != null )
    	{
    		InventoryUtils.ejectInventoryContents(world, i, j, k, (IInventory)tileEntity);
    	}

        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
    }
    
	@Override
    public int tickRate( World world )
    {
    	return PULLEY_TICK_RATE;
    }
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
    	boolean bReceivingPower = isInputtingMechanicalPower(world, i, j, k);
    	boolean bOn = isBlockOn(world, i, j, k);
    	
    	boolean bStateChanged = false;
    	
    	if ( bOn != bReceivingPower )
    	{
    		/*
	        world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
	    		"step.gravel", 
	    		2.0F + ( random.nextFloat() * 0.1F ),		// volume 
	    		0.5F + ( random.nextFloat() * 0.1F ) );	// pitch
	        
	        EmitPulleyParticles( world, i, j, k, random );
    		*/
	        
    		setBlockOn(world, i, j, k, bReceivingPower);
    		
    		bStateChanged = true;
    	}
    	
    	boolean bRedstoneOn = isRedstoneOn(world, i, j, k);
    	boolean bReceivingRedstone = world.isBlockGettingPowered( i, j, k ) || 
    		world.isBlockGettingPowered( i, j + 1, k );
    	
    	if ( bRedstoneOn != bReceivingRedstone )
    	{
    		/*
	        world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
	    		"step.gravel", 
	    		2.0F + ( random.nextFloat() * 0.1F ),		// volume 
	    		0.5F + ( random.nextFloat() * 0.1F ) );	// pitch
	        
	        EmitPulleyParticles( world, i, j, k, random );
    		*/
	        
	        setRedstoneOn(world, i, j, k, bReceivingRedstone);
	        
    		bStateChanged = true;
    	}
    	
    	if ( bStateChanged )
    	{
    		( (PulleyTileEntity)world.getBlockTileEntity( i, j, k ) ).notifyPulleyEntityOfBlockStateChange();
    	}
    }
    
	@Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
		if ( !isCurrentStateValid(world, i, j, k) )
		{
			// verify we have a tick already scheduled to prevent jams on chunk load
			
			if ( !world.isUpdateScheduledForBlock(i, j, k, blockID) )
			{
		        world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );        
			}
		}
    }
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
		if (!isCurrentStateValid(world, i, j, k) &&
			!world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
		{
			world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
		}
    }
    
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2; // iron or better
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, Item.stick.itemID, 2, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 2, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.gear.itemID, 1, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, Item.goldNugget.itemID, 2, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, Item.ingotIron.itemID, 1, 0, fChanceOfDrop);
		
		return true;
	}
	
    @Override
    public boolean isIncineratedInCrucible()
    {
    	return false;
    }
	
    //------------- FCIBlockMechanical -------------//
    
	@Override
    public boolean canOutputMechanicalPower()
    {
    	return false;
    }

	@Override
    public boolean canInputMechanicalPower()
    {
    	return true;
    }

	@Override
    public boolean isInputtingMechanicalPower(World world, int i, int j, int k)
    {
    	return MechPowerUtils.isBlockPoweredByAxle(world, i, j, k, this) ||
			   MechPowerUtils.isBlockPoweredByHandCrank(world, i, j, k);
    }
	
	@Override
	public boolean canInputAxlePowerToFacing(World world, int i, int j, int k, int iFacing)
	{
		return iFacing >= 2;
	}
	
	@Override
    public boolean isOutputtingMechanicalPower(World world, int i, int j, int k)
    {
    	return false;
    }
    
	@Override
	public void overpower(World world, int i, int j, int k)
	{
		breakPulley(world, i, j, k);
	}

	@Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

	@Override
    public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5)
    {
        return Container.calcRedstoneFromInventory(((IInventory) par1World.getBlockTileEntity(par2, par3, par4)));
    }
	
	//------------- Class Specific Methods ------------//
    
    public boolean isBlockOn(IBlockAccess iBlockAccess, int i, int j, int k)
    {
    	return ( iBlockAccess.getBlockMetadata( i, j, k ) & 1 ) > 0;    
	}
    
    public void setBlockOn(World world, int i, int j, int k, boolean bOn)
    {
    	int iMetaData = world.getBlockMetadata( i, j, k ); // filter out old on state
    	
    	if ( bOn )
    	{
    		iMetaData |= 1;
    	}
    	else
    	{
    		iMetaData &= ~1;
    	}
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetaData );
    }
    
    public boolean isRedstoneOn(IBlockAccess iBlockAccess, int i, int j, int k)
    {
    	return ( iBlockAccess.getBlockMetadata( i, j, k ) & 2 ) > 0;
    }
    
    public void setRedstoneOn(World world, int i, int j, int k, boolean bOn)
    {
    	int iMetaData = world.getBlockMetadata( i, j, k ) & (~2); // filter out any old on state
    	
    	if ( bOn )
    	{
    		iMetaData |= 2;
    	}
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetaData );
    }
	
    void emitPulleyParticles(World world, int i, int j, int k, Random random)
    {
        for ( int counter = 0; counter < 5; counter++ )
        {
            float smokeX = (float)i + random.nextFloat();
            float smokeY = (float)j + random.nextFloat() * 0.5F + 1.0F;
            float smokeZ = (float)k + random.nextFloat();
            
            world.spawnParticle( "smoke", smokeX, smokeY, smokeZ, 0.0D, 0.0D, 0.0D );
        }
    }
    
	public void breakPulley(World world, int i, int j, int k)
	{
		dropComponentItemsOnBadBreak(world, i, j, k, world.getBlockMetadata(i, j, k), 1F);
		
        world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );
        
		world.setBlockWithNotify( i, j, k, 0 );
	}
	
	public boolean isCurrentStateValid(World world, int i, int j, int k)
	{
    	boolean bReceivingPower = isInputtingMechanicalPower(world, i, j, k);
    	boolean bOn = isBlockOn(world, i, j, k);
    	
    	if ( bReceivingPower != bOn )
    	{
    		return false;
    	}
    	
    	boolean bRedstoneOn = isRedstoneOn(world, i, j, k);
    	boolean bReceivingRedstone = world.isBlockGettingPowered( i, j, k ) || 
    		world.isBlockGettingPowered( i, j + 1, k );
    	
    	return bRedstoneOn == bReceivingRedstone;    	
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBySideArray = new Icon[6];

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        Icon sideIcon = register.registerIcon( "fcBlockPulley_side" );
        
        blockIcon = sideIcon; // for hit effects

		iconBySideArray[0] = register.registerIcon("fcBlockPulley_bottom");
		iconBySideArray[1] = register.registerIcon("fcBlockPulley_top");

		iconBySideArray[2] = sideIcon;
		iconBySideArray[3] = sideIcon;
		iconBySideArray[4] = sideIcon;
		iconBySideArray[5] = sideIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		return iconBySideArray[iSide];
    }
	
	// FCTODO: Fix this based on whether pulley is raising or lowering
	/*
	@Override
    public void randomDisplayTick( World world, int i, int j, int k, Random random )
    {
    	if ( IsBlockOn( world, i, j, k ) )
    	{
	        EmitPulleyParticles( world, i, j, k, random );
	        
	        if ( random.nextInt( 2 ) == 0 )
	        {
		        world.playSound( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
		    		"minecart.base", 
		    		1.0F + ( random.nextFloat() * 0.1F ),		// volume 
		    		0.75F + ( random.nextFloat() * 0.1F ) );	// pitch
	        }
    	}	        
    }
    */
}