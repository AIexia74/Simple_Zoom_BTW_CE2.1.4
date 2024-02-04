// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class HibachiBlock extends Block
{
    private final static int TICK_RATE = 4;
    
    public HibachiBlock(int iBlockID )
    {
        super( iBlockID, Material.rock );
        
        setHardness( 3.5F );
        setStepSound( Block.soundStoneFootstep );
        
        setUnlocalizedName( "fcBlockHibachi" );

        setTickRandomly( true );
        
        setCreativeTab( CreativeTabs.tabRedstone );
    }

	@Override
    public int tickRate( World world )
    {
        return TICK_RATE;
    }    

	@Override
    public void onBlockAdded(World world, int i, int j, int k)
    {
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    }    
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
        boolean bPowered = isGettingPowered(world, i, j, k);
        
        if ( bPowered )
        {
        	if ( !isLit(world, i, j, k) )
        	{
        		ignite(world, i, j, k);
        	}
        	else
            {
                // make sure the fire hasn't gone out from other sources, or that the block above hasn't become vacated,
                // if we're supposed to be lit
        		
        		int iBlockAboveID = world.getBlockId(i, j + 1, k);

            	if ( iBlockAboveID != Block.fire.blockID &&
        			iBlockAboveID != BTWBlocks.stokedFire.blockID )
            	{
            		if ( shouldIgniteAbove(world, i, j, k) )
            		{
        	            world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, "mob.ghast.fireball", 1F, world.rand.nextFloat() * 0.4F + 0.8F );

                        world.setBlockWithNotify( i, j + 1, k, fire.blockID );
            		}
            	}
            }
        }
    	else
    	{
    		if ( isLit(world, i, j, k) )
    		{
    			extinguish(world, i, j, k);
    		}
    		else
    		{
        		int iBlockAboveID = world.getBlockId( i, j + 1, k );

            	if ( iBlockAboveID == Block.fire.blockID ||
        			iBlockAboveID == BTWBlocks.stokedFire.blockID )
            	{
            		// we've got a fire burning above an unlit, unpowered BBQ.  
            		// It has probably been lit by the player, or spread from elsewhere, so
            		// put it out.

                    world.setBlockWithNotify( i, j + 1, k, 0 );            		
            	}
    		}
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
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {        
    	if (!isCurrentStateValid(world, i, j, k) &&
            !world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
		{
        	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
		}
    }    

	@Override
    public boolean doesExtinguishFireAbove(World world, int i, int j, int k)
    {
    	return !isLit(world, i, j, k);
    }
    
	@Override
    public boolean doesInfiniteBurnToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
    {
		if ( iFacing == 1 )
		{
	    	return isLit(blockAccess, i, j, k);
		}
		
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
    
    public boolean isLit(IBlockAccess blockAccess, int i, int j, int k)
    {
        int iMetaData = blockAccess.getBlockMetadata(i, j, k);
        
    	return ( iMetaData & 4 ) > 0;
    }
    
    private void setLitFlag(World world, int i, int j, int k)
    {
        int iMetaData = world.getBlockMetadata(i, j, k);
    	
        world.setBlockMetadataWithNotify( i, j, k, ( iMetaData | 4 ) );
    }

    private void clearLitFlag(World world, int i, int j, int k)
    {    	
        int iMetaData = world.getBlockMetadata(i, j, k);
    	
        world.setBlockMetadataWithNotify( i, j, k, ( iMetaData & (~4) ) );
    }
    
    private boolean isGettingPowered(World world, int i, int j, int k)
    {
        boolean bPowered = world.isBlockGettingPowered(i, j, k) || world.isBlockGettingPowered(i, j + 1, k);
        
        return bPowered;
    }
    
    private boolean shouldIgniteAbove(World world, int i, int j, int k)
    {
    	return world.isAirBlock( i, j + 1, k ) || canIncinerateBlock(world, i, j + 1, k);
    }
    
    private boolean canIncinerateBlock(World world, int i, int j, int k)
    {
		Block targetBlock = Block.blocksList[world.getBlockId( i, j, k )];
		
		return targetBlock == null || targetBlock.getCanBlockBeIncinerated(world, i, j, k);
    }
    
    private void ignite(World world, int i, int j, int k)
    {
        setLitFlag(world, i, j, k);
        
        world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, "mob.ghast.fireball", 1F, world.rand.nextFloat() * 0.4F + 1F );

        if ( shouldIgniteAbove(world, i, j, k) )
        {
    		world.setBlockWithNotify( i, j + 1, k, fire.blockID );
        }        
    }
    
    private void extinguish(World world, int i, int j, int k)
    {
        clearLitFlag(world, i, j, k);
        
        world.playSoundEffect( (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F );
        
        // check if there is an actual fire block above the BBQ
        
        boolean isFireAbove = world.getBlockId( i, j + 1, k ) == fire.blockID ||
        	world.getBlockId(i, j + 1, k) == BTWBlocks.stokedFire.blockID;
        
        if ( isFireAbove )
        {
        	// notify the fire to extinguish
        	
            world.setBlockWithNotify( i, j + 1, k, 0 );
        }        
    }
    
	public boolean isCurrentStateValid(World world, int i, int j, int k)
	{
        boolean bPowered = isGettingPowered(world, i, j, k);
        
    	if (isLit(world, i, j, k) != bPowered )
    	{
			return false;
    	}
    	else if ( isLit(world, i, j, k) )
    	{
    		int iBlockAboveID = world.getBlockId(i, j + 1, k);

        	if ( iBlockAboveID != Block.fire.blockID &&
    			iBlockAboveID != BTWBlocks.stokedFire.blockID )
    		{
    			if ( shouldIgniteAbove(world, i, j, k) )
    			{
    				// the bbq is lit, there is no fire above, and the block above is flammable (or air)
    				// this requires a state update
    				
    				return false;
    			}
			}
    	}
    	
    	return true;
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBySideArray = new Icon[6];

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "stone" ); // for hit effects

        iconBySideArray[0] = register.registerIcon("fcBlockHibachi_bottom");
        iconBySideArray[1] = register.registerIcon("fcBlockHibachi_top");
        
        Icon sideIcon = register.registerIcon( "fcBlockHibachi_side" );

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
}