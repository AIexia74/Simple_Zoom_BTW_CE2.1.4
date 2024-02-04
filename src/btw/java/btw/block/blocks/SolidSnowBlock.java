// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.util.MiscUtils;
import net.minecraft.src.*;

import java.util.Random;

public class SolidSnowBlock extends Block
{
	public static final float HARDNESS = 0.5F;
	
	public static final int CHANCE_OF_MELTING = 1;
	
    public SolidSnowBlock(int iBlockID )
    {
        super( iBlockID, Material.craftedSnow );
        
    	setHardness(HARDNESS);
        setShovelsEffectiveOn();
        
    	setBuoyant();
    	
    	setStepSound( soundSnowFootstep );
    	
        setUnlocalizedName( "fcBlockSnowSolid" );
        
        setTickRandomly( true );
		
        setLightOpacity( 4 );
        Block.useNeighborBrightness[iBlockID] = true;
        
        setCreativeTab(CreativeTabs.tabBlock);
    }
	
	@Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return Item.snowball.itemID;
    }

    public int quantityDropped( Random rand )
    {
        return 8;
    }

    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
    {
        dropBlockAsItem( world, i, j, k, iMetadata, 0 );
    }
    
    @Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        if ( world.provider.isHellWorld )
        {
        	// melt instantly in the nether
        	
        	world.setBlockToAir( i, j, k );
        	
            world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
            	"random.fizz", 0.5F, 2.6F + ( world.rand.nextFloat() - world.rand.nextFloat() ) * 0.8F );
            
            for ( int iTempCount = 0; iTempCount < 8; iTempCount++ )
            {
                world.spawnParticle( "largesmoke", (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 
                	0D, 0D, 0D );
            }
        }
    }
    
	@Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
		if ( !MiscUtils.isIKInColdBiome(world, i, k) )
		{
			if (rand.nextInt(CHANCE_OF_MELTING) == 0 )
			{
				convertToLooseSnow(world, i, j, k);
			}
		}
    }
	
    @Override
	public boolean isStickyToSnow(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
    @Override
    public float getMovementModifier(World world, int i, int j, int k)
    {
    	return 1F;
    }
    
    @Override
    public boolean getCanBeSetOnFireDirectly(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public boolean getCanBeSetOnFireDirectlyByItem(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return false;
    }
    
    @Override
    public boolean setOnFireDirectly(World world, int i, int j, int k)
    {
		melt(world, i, j, k);
		
    	return true;
    }
    
    @Override
    public int getChanceOfFireSpreadingDirectlyTo(IBlockAccess blockAccess, int i, int j, int k)
    {
		return 60; // same chance as leaves and other highly flammable objects
    }
    
    @Override
    public boolean canBePistonShoveled(World world, int i, int j, int k)
    {
    	return true;
    }
    
    //------------- Class Specific Methods ------------//    
    
	private void convertToLooseSnow(World world, int i, int j, int k)
	{
		int iNewMetadata = BTWBlocks.looseSnow.setHardeningLevel(0, 7);
		
		world.setBlockAndMetadataWithNotify( i, j, k, BTWBlocks.looseSnow.blockID, iNewMetadata );
		
		BTWBlocks.looseSnow.scheduleCheckForFall(world, i, j, k);
	}
	
	private void melt(World world, int i, int j, int k)
	{
		MiscUtils.placeNonPersistentWaterMinorSpread(world, i, j, k);
	}
	
	//----------- Client Side Functionality -----------//
}