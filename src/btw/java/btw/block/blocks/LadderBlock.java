// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class LadderBlock extends LadderBlockBase
{
    public LadderBlock(int iBlockID)
    {
        super( iBlockID );
        
        setUnlocalizedName( "fcBlockLadder" );
        
        setCreativeTab( CreativeTabs.tabDecorations );
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
    	int iNewMetadata = BTWBlocks.flamingLadder.setFacing(0, getFacing(world, i, j, k));
		
    	world.setBlockAndMetadataWithNotify( i, j, k, BTWBlocks.flamingLadder.blockID, iNewMetadata );
		
    	return true;
    }
    
    @Override
    public int getChanceOfFireSpreadingDirectlyTo(IBlockAccess blockAccess, int i, int j, int k)
    {
		return 60; // same chance as leaves and other highly flammable objects
    }
    
    @Override
    public boolean canBeCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
    	return true;
    }
    
    @Override
    public void onCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
    	if ( !world.isRemote )
    	{
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );    		
    	}
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
}
