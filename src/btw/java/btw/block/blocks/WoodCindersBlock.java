// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

import java.util.Random;

public class WoodCindersBlock extends FallingBlock
{
	private static final int CHANCE_OF_DISSOLVING_IN_RAIN = 5;
	
    public WoodCindersBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.logMaterial);
        
        setHardness( 0.25F );
        
		setAxesEffectiveOn();
		setChiselsEffectiveOn();
		
        setBuoyant();
        
        setTickRandomly( true );
		
        setStepSound( soundGravelFootstep );
        
        setUnlocalizedName( "fcBlockWoodCinders" );        
    }
    
    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return 0;
    }
    
    @Override
    public boolean onFinishedFalling(EntityFallingSand entity, float fFallDistance)
    {
    	World world = entity.worldObj;
    	
        if ( !world.isRemote )
        {
	        int i = MathHelper.floor_double( entity.posX );
	        int j = MathHelper.floor_double( entity.posY );
	        int k = MathHelper.floor_double( entity.posZ );
	        
	        if (!AshGroundCoverBlock.attemptToPlaceAshAt(world, i, j, k) &&
                !AshGroundCoverBlock.attemptToPlaceAshAt(world, i, j + 1, k) )
	        {
	        	for ( int iTempCount = 0; iTempCount < 16; iTempCount++ )
	        	{
	        		int iTempI = i + world.rand.nextInt( 7 ) - 3 ;
	        		int iTempJ = j + world.rand.nextInt( 5 ) - 2 ;
	        		int iTempK = k + world.rand.nextInt( 7 ) - 3 ;
	        		
	        		if ( AshGroundCoverBlock.attemptToPlaceAshAt(world, iTempI, iTempJ, iTempK) )
	        		{
	        			break;
	        		}
	        	}
	        }
        }
    	
    	// always destroyed on landing
        
    	return false;
    }
    
    @Override
    public void updateTick( World world, int i, int j, int k, Random rand ) 
    {
    	// prevent falling behavior for stumps
    	
    	if ( !getIsStump(world, i, j, k) )
    	{
    		super.updateTick( world, i, j, k, rand );
    	}
    }
    
    @Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
    	if (rand.nextInt(CHANCE_OF_DISSOLVING_IN_RAIN) == 0 )
    	{
        	if ( !getIsStump(world, i, j, k) )
        	{
		        if ( world.isRainingAtPos(i, j + 1, k) )
		        {
		        	world.setBlockWithNotify( i, j, k, 0 );
		        }
        	}
    	}
    }
    
    @Override
    public boolean canConvertBlock(ItemStack stack, World world, int i, int j, int k)
    {
    	return getIsStump(world, i, j, k);
    }
    
    @Override
    public boolean convertBlock(ItemStack stack, World world, int i, int j, int k, int iFromSide)
    {
    	if ( getIsStump(world.getBlockMetadata(i, j, k)) )
    	{
	    	world.setBlockWithNotify( i, j, k, BTWBlocks.charredStump.blockID );
	    	
	    	return true;
    	}   
    	
    	return false;
    }
    
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	if ( getIsStump(blockAccess.getBlockMetadata(i, j, k)) )
    	{
    		return 1000; // always convert, never harvest
    	}
    	
    	return super.getHarvestToolLevel(blockAccess, i, j, k);
    }
    
    @Override
    public boolean getDoesStumpRemoverWorkOnBlock(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getIsStump(blockAccess, i, j, k);
    }
    
    //------------- Class Specific Methods ------------//
    
    public boolean getIsStump(IBlockAccess blockAccess, int i, int j, int k)
    {
		int iMetadata = blockAccess.getBlockMetadata( i, j, k );
		
		return getIsStump(iMetadata);
    }
    
    public boolean getIsStump(int iMetadata)
    {
    	return ( iMetadata & 8 ) != 0;
    }
    
    public int setIsStump(int iMetadata, boolean bStump)
    {
    	if ( bStump )
    	{
    		iMetadata |= 8;
    	}
    	else
    	{
        	iMetadata &= ~8;
    	}
    	
    	return iMetadata;
    }
    
	//----------- Client Side Functionality -----------//    
}
