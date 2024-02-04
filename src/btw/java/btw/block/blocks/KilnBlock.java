// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class KilnBlock extends Block
{
	private static final int MIN_FIRE_FACTOR_BASE_TICK_RATE = 40;
	private static final int MAX_FIRE_FACTOR_BASE_TICK_RATE = 160;
	
    public KilnBlock(int iBlockID )
    {
        super( iBlockID, Material.rock );
        
        setHardness( 2F );
        setResistance( 10F );
        setStepSound( soundStoneFootstep );
        
        setTickRandomly( true );
        
        setUnlocalizedName( "fcBlockKiln" );        
    }
    
	@Override
    public void onBlockAdded(World world, int i, int j, int k )
    {
        super.onBlockAdded( world, i, j, k );
        
    	if ( canBlockBeCooked(world, i, j + 1, k) )
    	{
    		scheduleUpdateBasedOnCookState(world, i, j, k);
    	}
    }
	
	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
    	int iOldCookCounter = getCookCounter(world, i, j, k);
    	int iNewCookCounter = 0;
    	
    	if ( canBlockBeCooked(world, i, j + 1, k) )
    	{
    		if ( checkKiLnIntegrity(world, i, j, k) )
    		{
	    		if ( iOldCookCounter >= 15 )
	    		{
	    			cookBlock(world, i, j + 1, k);
	    		}
	    		else
	    		{    		
		    		iNewCookCounter = iOldCookCounter + 1;
		    		
	    			scheduleUpdateBasedOnCookState(world, i, j, k);
	    		}
    		}
    		else
    		{
        		// if we have a valid cook block above, we have to reschedule another tick
        		// regardless of other factors, because the shape of the kiln can change without
        		// an immediate neighbor changing, causing the cook process to restart
        		
    			scheduleUpdateBasedOnCookState(world, i, j, k);
    		}
    	}
    	
    	if ( iOldCookCounter != iNewCookCounter )
    	{    		
			setCookCounter(world, i, j, k, iNewCookCounter);
    	}
    }
    
	@Override
    public int idDropped( int iMetaData, Random random, int iFortuneModifier )
    {
        return Block.brick.blockID;
    }
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
    	if ( world.getBlockId( i, j - 1, k ) != BTWBlocks.stokedFire.blockID )
    	{
    		// we don't have a stoked fire beneath us, so revert to regular brick
    		
    		world.setBlockWithNotify( i, j, k, Block.brick.blockID );
    	}
    	else if ( canBlockBeCooked(world, i, j + 1, k) )
    	{
        	if (!world.isUpdateScheduledForBlock(i, j, k, blockID) &&
				!world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
        	{			
        		scheduleUpdateBasedOnCookState(world, i, j, k);
        	}
    	}
    	else if (getCookCounter(world, i, j, k) > 0 )
    	{        	
    		// reset the cook counter so it doesn't get passed to another block on piston push
    		
    		setCookCounterNoNotify(world, i, j, k, 0);
    	}
    }
    
	@Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
		// verify we have a tick already scheduled to prevent jams on chunk load
		
    	if (!world.isUpdateScheduledForBlock(i, j, k, blockID) &&
			canBlockBeCooked(world, i, j + 1, k) )
    	{			
    		scheduleUpdateBasedOnCookState(world, i, j, k);
    	}
    }
	
	@Override
	public boolean hasMortar(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
    //------------- Class Specific Methods ------------//
    
	public int getCookCounter(int iMetadata)
	{
		return iMetadata;
	}
	
    public int getCookCounter(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getCookCounter(blockAccess.getBlockMetadata(i, j, k));
	}
    
    public int setCookCounter(int iMetadata, int iCounter)
    {
    	return iCounter;
    }
    
    public void setCookCounter(World world, int i, int j, int k, int iCounter)
    {
    	int iMetadata = setCookCounter(world.getBlockMetadata(i, j, k), iCounter);
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }
    
    public void setCookCounterNoNotify(World world, int i, int j, int k, int iCounter)
    {
    	int iMetadata = setCookCounter(world.getBlockMetadata(i, j, k), iCounter);
    	
        world.setBlockMetadataWithClient( i, j, k, iMetadata );
    }
    
	protected void scheduleUpdateBasedOnCookState(World world, int i, int j, int k)
	{
		int iTickRate = computeTickRateBasedOnFireFactor(world, i, j, k);
		
		iTickRate *= getBlockCookTimeMultiplier(world, i, j + 1, k);
		
    	world.scheduleBlockUpdate( i, j, k, blockID, iTickRate );
	}

    private boolean canBlockBeCooked(IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iBlockID = blockAccess.getBlockId( i, j, k );
    	Block block = Block.blocksList[iBlockID];
    	
    	if ( block != null )
    	{
	    	return block.getCanBeCookedByKiLn(blockAccess, i, j, k);
    	}
    	
    	return false;
    }
    
    private void cookBlock(World world, int i, int j, int k)
    {
    	int iBlockID = world.getBlockId( i, j, k );
    	Block block = Block.blocksList[iBlockID];
    	
    	if ( block != null )
    	{
    		if ( block.getCanBeCookedByKiLn(world, i, j, k) )
			{
    			block.onCookedByKiLn(world, i, j, k);
			}
    	}
    }
    
    private boolean checkKiLnIntegrity(IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iBrickCount = 0;
    	
    	for ( int iTempFacing = 1; iTempFacing <= 5; iTempFacing++ )
    	{
    		BlockPos tempPos = new BlockPos( i, j + 1, k );
    		
    		tempPos.addFacingAsOffset(iTempFacing);
    		
    		int iTempBlockID = blockAccess.getBlockId(tempPos.x, tempPos.y, tempPos.z);
    		
    		if ( iTempBlockID == Block.brick.blockID || 
				iTempBlockID == BTWBlocks.kiln.blockID )
    		{
    			iBrickCount++;
    			
    	    	if ( iBrickCount >= 3 )
    	    	{
    	    		return true;
    	    	}
    		}
    	}
    	
		return false;
    }
    
    private int computeTickRateBasedOnFireFactor(IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iSecondaryFireFactor = 0;
    	
    	for ( int iOffset = -1; iOffset <= 1; iOffset++ )
    	{
        	for ( int kOffset = -1; kOffset <= 1; kOffset++ )
        	{
        		if ( iOffset != 0 || kOffset != 0 )
        		{
        			if ( blockAccess.getBlockId( i + iOffset, j - 1, k + kOffset ) == 
        				BTWBlocks.stokedFire.blockID )
					{
        				iSecondaryFireFactor++;
					}        				
        		}
        	}
    	}
    	
    	int iTickRate = ((MAX_FIRE_FACTOR_BASE_TICK_RATE - MIN_FIRE_FACTOR_BASE_TICK_RATE) *
						 ( 8 - iSecondaryFireFactor ) / 8 ) + MIN_FIRE_FACTOR_BASE_TICK_RATE;
    	
    	return iTickRate;
    }
    
    private int getBlockCookTimeMultiplier(IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iBlockID = blockAccess.getBlockId( i, j, k );
    	Block block = Block.blocksList[iBlockID];
    	
    	if ( block != null )
    	{
	    	return block.getCookTimeMultiplierInKiLn(blockAccess, i, j, k);
    	}
    	
    	return 1;
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] cookIcons;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "brick" );

		cookIcons = new Icon[7];

        for ( int iTempIndex = 0; iTempIndex < 7; iTempIndex++ )
        {
			cookIcons[iTempIndex] = register.registerIcon("fcOverlayCook_" + (iTempIndex + 1 ));
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return idDropped( world.getBlockMetadata( i, j, k ), world.rand, 0 );
    }

    @Environment(EnvType.CLIENT)
    public Icon getCookTextureForCurrentState(IBlockAccess blockAccess, int i, int j, int k)
	{
		int iTextureIndex = (getCookCounter(blockAccess, i, j, k) / 2 ) - 1;
		
		if ( iTextureIndex >= 0 && iTextureIndex <= 6 )
		{
			return cookIcons[iTextureIndex];
		}
		
		return null;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random rand )
    {
		Block blockAbove = Block.blocksList[world.getBlockId( i, j + 1, k )];
		
		if ( blockAbove != null && blockAbove.getCanBeCookedByKiLn(world, i, j + 1, k) &&
             checkKiLnIntegrity(world, i, j, k) )
		{
			if ( !blockAbove.renderAsNormalBlock() )
			{
	            for ( int iTempCount = 0; iTempCount < 2; iTempCount++ )
	            {
	                double xPos = i + rand.nextDouble();
	                double yPos = j + 1D + ( rand.nextDouble() * 0.75D );
	                double zPos = k + rand.nextDouble();
	                
	                world.spawnParticle( "fcwhitesmoke", xPos, yPos, zPos, 0D, 0D, 0D );
	            }
			}
			else
			{
	            for ( int iTempFacing = 2; iTempFacing < 6; iTempFacing++ )
	            {
	                double xPos = i + 0.5D;
	                double yPos = j + 1D + ( rand.nextDouble() * 0.75D );
	                double zPos = k + 0.5D;
	                
	                double dFacingOffset = 0.75D;
	                double dHorizontalOffset = -0.75D + ( rand.nextDouble() * 1.5D );
	                	                
 	                if ( iTempFacing == 2 ) // negative k
 	                {
 	                	xPos += dHorizontalOffset;
 	                	zPos -= dFacingOffset;
 	                }
 	                else if ( iTempFacing == 3 ) // positive k
 	                {
 	                	xPos += dHorizontalOffset;
 	                	zPos += dFacingOffset;
 	                }
 	                else if ( iTempFacing == 4 ) // negative i
 	                {
 	                	xPos -= dFacingOffset;
 	                	zPos += dHorizontalOffset;
 	                }
 	                else if ( iTempFacing == 5 ) // positive i
 	                {
 	                	xPos += dFacingOffset;
 	                	zPos += dHorizontalOffset;
 	                }
	                
	                world.spawnParticle( "fcwhitesmoke", xPos, yPos, zPos, 0D, 0D, 0D );
	            }
			}
		}
    }
}