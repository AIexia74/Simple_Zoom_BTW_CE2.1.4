// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.EntityFallingSand;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

import java.util.Random;

public class MilkBlock extends FallingBlock
{
	public static final double HEIGHT = (1D / 16D );

	public static final int DECAY_TICK_RATE = 10;
	
    public MilkBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.milkMaterial);
        
        initBlockBounds(0D, 0D, 0D, 1D, HEIGHT, 1D);
        
        setHardness( 0F );
        setResistance( 0F );
        
        setStepSound( BTWBlocks.stepSoundSquish);
        
        setUnlocalizedName( "fcBlockMilk" );
    }
    
    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return 0;
    }
	
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
	
    @Override
    public void updateTick(World world, int i, int j, int k, Random rand )
    {
    	if ( !checkForFall(world, i, j, k) )
    	{    	
    		int iDecayLevel = getDecayLevel(world, i, j, k);
    		
    		if ( iDecayLevel < 1 )
    		{
    			iDecayLevel++;
    			
    			setDecayLevel(world, i, j, k, iDecayLevel);
    			
    	        world.scheduleBlockUpdate(i, j, k, blockID, DECAY_TICK_RATE);
    		}
    		else
    		{
    			world.setBlockToAir( i, j, k );
    		}
    	}
    }
    
	@Override
    protected void onStartFalling( EntityFallingSand entity )
    {
		entity.metadata = setDecayLevel(entity.metadata, 0);
    }
	
	//------------- Class Specific Methods ------------//
	
	public int getDecayLevel(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getDecayLevel(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public int getDecayLevel(int iMetadata)
	{
		return iMetadata & 1; 
	}
	
	public void setDecayLevel(World world, int i, int j, int k, int iLevel)
	{
		int iMetadata = setDecayLevel(world.getBlockMetadata(i, j, k), iLevel);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	public int setDecayLevel(int iMetadata, int iLevel)
	{
		iMetadata &= ~1;
		
		return iMetadata | iLevel;
	}
    
	//----------- Client Side Functionality -----------//
}
