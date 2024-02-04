// FCMOD

package btw.block.blocks;

import btw.client.fx.BTWEffectManager;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class BuddyBlock extends Block
{
    private final static int TICK_RATE = 5;
    
    public BuddyBlock(int iBlockID )
    {
        super( iBlockID, Material.rock );
        
        setHardness( 3.5F );
        
        setTickRandomly( true );        
		
        setStepSound(soundStoneFootstep);
        
        setUnlocalizedName( "fcBlockBuddyBlock" );        
        
		setCreativeTab( CreativeTabs.tabRedstone );
    }
    
	@Override
    public int tickRate( World world )
    {
        return TICK_RATE;
    }    

	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        super.onBlockAdded( world, i, j, k );

        // minimal delay same as when the buddy block is changed by a neigbor notification to handle
        // state changes due to being pushed around by a piston
    	world.scheduleBlockUpdate( i, j, k, blockID, 1 ); 
    }

	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
        return setFacing(iMetadata, Block.getOppositeFacing(iFacing));
    }
    
	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityLiving, ItemStack stack )
	{
		int iFacing = MiscUtils.convertPlacingEntityOrientationToBlockFacingReversed(entityLiving);
		
		setFacing(world, i, j, k, iFacing);
	}
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
    	if ( !isRedstoneOn(world, i, j, k) )
    	{
    		Block neighborBlock = blocksList[iNeighborBlockID];
        	
	    	if ( neighborBlock != null && neighborBlock.triggersBuddy() &&
				 !world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
	    	{
	    		// minimal delay when triggered to avoid notfying neighbors of change in same tick
	    		// that they are notifying of the original change. Not doing so causes problems 
	    		// with some blocks (like ladders) that haven't finished initializing their state 
	    		// on placement when they send out the notification
	    		
	        	world.scheduleBlockUpdate( i, j, k, blockID, 1 ); 
	    	}
    	}
    }
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
    	if ( isRedstoneOn(world, i, j, k) )
    	{
    		setBlockRedstoneOn(world, i, j, k, false);
    	}
    	else
    	{
    		setBlockRedstoneOn(world, i, j, k, true);

    		// schedule another update to turn the block off
        	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) ); 
    	}
    }
    
	@Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
		if ( isRedstoneOn(world, i, j, k) )
		{
			// verify we have a tick already scheduled to prevent jams on chunk load
			
			if ( !world.isUpdateScheduledForBlock(i, j, k, blockID) )
			{
		        world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );        
			}
		}
    }
	
	@Override
    public int isProvidingWeakPower(IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
		return getPowerProvided(blockAccess, i, j, k, iSide);
    }
    
	@Override
    public int isProvidingStrongPower( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
		return getPowerProvided(blockAccess, i, j, k, iSide);
    }
    
	@Override
    public boolean canProvidePower()
    {
        return true;
    }
    
	@Override
	public int getFacing(int iMetadata)
	{
    	return ( iMetadata & (~1) ) >> 1;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
    	iMetadata = ( iMetadata & 1 ) | ( iFacing << 1 );    	
    	
		return iMetadata;
	}
	
	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{		
		int iFacing = getFacing(world, i, j, k);
		
		iFacing = Block.cycleFacing(iFacing, bReverse);

		setFacing(world, i, j, k, iFacing);
		
        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        
        return true;
	}
	
	@Override
	public boolean triggersBuddy()
	{
		return false;
	}
	
	@Override
    public int onPreBlockPlacedByPiston(World world, int i, int j, int k, int iMetadata, int iDirectionMoved)
	{
		// notify the neigbours of the block we originated at
		
        BlockPos originPos = new BlockPos(i, j, k, getOppositeFacing(iDirectionMoved) );
        
        notifyNeigborsToFacingOfPowerChange(world, originPos.x, originPos.y, originPos.z, getFacing(iMetadata));
		
		return iMetadata;
    }
	
    //------------- Class Specific Methods ------------//
    
    public int getPowerProvided(IBlockAccess blockAccess, int i, int j, int k, int iSide)
    {
    	int iFacing = getFacing(blockAccess, i, j, k);
    	
    	if (Block.getOppositeFacing(iSide) == iFacing && isRedstoneOn(blockAccess, i, j, k) )
    	{    		
    		return 15;
    	}
    	
		return 0;
    }
	
    public boolean isRedstoneOn(IBlockAccess iblockaccess, int i, int j, int k)
    {
    	return ( iblockaccess.getBlockMetadata(i, j, k) & 1 ) > 0;
    }
    
    public void setBlockRedstoneOn(World world, int i, int j, int k, boolean bOn)
    {
    	if (bOn != isRedstoneOn(world, i, j, k) )
    	{
	    	int iMetaData = world.getBlockMetadata(i, j, k);
	    	
	    	if ( bOn )
	    	{
	    		iMetaData = iMetaData | 1;
	    		
		        world.playAuxSFX( BTWEffectManager.REDSTONE_CLICK_EFFECT_ID, i, j, k, 0 );
	    	}
	    	else
	    	{
	    		iMetaData = iMetaData & (~1);
	    	}
	    	
	        world.setBlockMetadataWithClient( i, j, k, iMetaData );
	        
	        // only notify on the output side to prevent weird shit like doors auto-closing when the block
	        // goes off
	        
	        int iFacing = this.getFacing(world, i, j, k);
	        
	        notifyNeigborsToFacingOfPowerChange(world, i, j, k, iFacing);
	        
	        // the following forces a re-render (for texture change)
	        
	        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );    	
        }
    }
    
    public void notifyNeigborsToFacingOfPowerChange(World world, int i, int j, int k, int iFacing)
    {
        BlockPos outputPos = new BlockPos( i, j, k );
        
        outputPos.addFacingAsOffset(iFacing);
        
        Block outputBlock = Block.blocksList[world.getBlockId(outputPos.x, outputPos.y, outputPos.z)];
        
        if ( outputBlock != null)
        {
            outputBlock.onNeighborBlockChange(world, outputPos.x, outputPos.y, outputPos.z, blockID);
        }
        
        // we have to notify in a radius as some redstone blocks get their power state from up to two blocks away
        
        world.notifyBlocksOfNeighborChange(outputPos.x, outputPos.y, outputPos.z, blockID);
        
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconOn;
    @Environment(EnvType.CLIENT)
    private Icon iconFront;
    @Environment(EnvType.CLIENT)
    private Icon iconFrontOn;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconOn = register.registerIcon("fcBlockBuddyBlock_on");
		iconFront = register.registerIcon("fcBlockBuddyBlock_front");
		iconFrontOn = register.registerIcon("fcBlockBuddyBlock_front_on");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		// item render
		
		if ( iSide == 3 )
		{
			return iconFront;
		}
		
		return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
    	int iFacing = getFacing(blockAccess, i, j, k);
    	
    	if ( iFacing == iSide )
    	{
	    	if ( isRedstoneOn(blockAccess, i, j, k) )
	    	{
	    		return iconFrontOn;
			}
	    	else
	    	{
	    		return iconFront;
	    	}
    	}
    	else if ( isRedstoneOn(blockAccess, i, j, k) )
    	{
    		return iconOn;
		}
    	
    	return blockIcon;
    }   
}