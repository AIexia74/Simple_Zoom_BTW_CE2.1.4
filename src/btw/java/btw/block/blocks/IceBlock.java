// FCMOD

package btw.block.blocks;

import btw.util.MiscUtils;
import net.minecraft.src.*;

import java.util.Random;

public class IceBlock extends BlockIce
{
    public IceBlock(int iBlockID )
    {
    	super ( iBlockID );
    	
    	setPicksEffectiveOn();
    }
    
    @Override
    public void harvestBlock(World world, EntityPlayer player, int i, int j, int k, int iMetadata )
    {
        if ( !world.provider.isHellWorld )
        {
	        if (isNonSourceIceFromMetadata(iMetadata) && (!canSilkHarvest() || !EnchantmentHelper.getSilkTouchModifier(player) ) )
	        {
	        	// HCB ice turns into non persistant water on harvest
	        	
	            player.addExhaustion( 0.025F );		            
	            player.addStat( StatList.mineBlockStatArray[blockID], 1 );

				MiscUtils.placeNonPersistentWaterMinorSpread(world, i, j, k);
	        	
	        	return;
	        }
        }
        
        super.harvestBlock( world, player, i, j, k, iMetadata );
    }
    
    @Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
        if (world.getSavedLightValue(EnumSkyBlock.Block, i, j, k) > 11 - Block.lightOpacity[blockID])
        {
        	melt(world, i, j, k);
        }
    }
    
    @Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        if ( world.provider.isHellWorld )
        {
        	// melt ice instantly in the nether
        	
        	world.setBlockWithNotify( i, j, k, 0 );
        	
            world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
            	"random.fizz", 0.5F, 2.6F + ( world.rand.nextFloat() - world.rand.nextFloat() ) * 0.8F );
            
            for ( int l = 0; l < 8; l++ )
            {
                world.spawnParticle( "largesmoke", (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D );
            }
        }
        else 
        {            
        	int iBlockAboveID = world.getBlockId( i, j + 1, k );
        	
            if (!MiscUtils.isIKInColdBiome(world, i, k) ||
                ( iBlockAboveID != blockID && !world.canBlockSeeTheSky( i, j + 1, k ) ) )
            {            	
            	world.setBlockWithNotify( i, j, k, 0 );
            	
    			MiscUtils.placeNonPersistentWaterMinorSpread(world, i, j, k);
            }            
        }
    }
    
    @Override
    public float getMovementModifier(World world, int i, int j, int k)
    {
    	return 1.0F;
    }
    
    @Override
    public int adjustMetadataForPistonMove(int iMetadata)
    {
    	// flag pushed ice as non-source
    	
    	return iMetadata |= 8;
    }
    
    @Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return bIgnoreTransparency;
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
    
    //------------- Class Specific Methods ------------//    
    
    public boolean isNonSourceIce(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return isNonSourceIceFromMetadata(blockAccess.getBlockMetadata(i, j, k));
	}
    
    public void setIsNonSourceIce(World world, int i, int j, int k, boolean bNonSource)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k ) & (~8); // filter out old state
    	
    	if ( bNonSource )
    	{
            iMetadata |= 8;
    	}
    	
		world.setBlockMetadata( i, j, k, iMetadata );
    }
    
    public boolean isNonSourceIceFromMetadata(int iMetadata)
    {
    	return ( iMetadata & 8 ) > 0;    
    }
    
	private void melt(World world, int i, int j, int k)
	{
    	if ( isNonSourceIce(world, i, j, k) )
    	{
    		MiscUtils.placeNonPersistentWaterMinorSpread(world, i, j, k);
    	}
    	else
    	{
            world.setBlockWithNotify( i, j, k, Block.waterMoving.blockID );
    	}
	}
	
	//----------- Client Side Functionality -----------//
}
