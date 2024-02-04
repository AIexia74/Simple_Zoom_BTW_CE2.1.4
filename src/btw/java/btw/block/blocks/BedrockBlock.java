// FCMOD

package btw.block.blocks;

import net.minecraft.src.*;

public class BedrockBlock extends FullBlock
{
	public BedrockBlock(int iBlockID )
	{
		super( iBlockID, Material.rock );
		
		setBlockUnbreakable();
		setResistance( 6000000F );
		
		setStepSound( Block.soundStoneFootstep );
		
		setUnlocalizedName( "bedrock" );
		
		disableStats();
		
		setCreativeTab( CreativeTabs.tabBlock );
	}

	@Override
    public int getMobilityFlag()
    {
        return 2; // cannot be pushed
    }
	
	@Override
    public boolean canMobsSpawnOn(World world, int i, int j, int k)
    {
    	return false;
    }
	
    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k)
    {	 
    	return null; // can't be picked up
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
