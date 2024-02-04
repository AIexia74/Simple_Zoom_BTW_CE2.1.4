// FCMOD

package btw.block.blocks;

import net.minecraft.src.Item;
import net.minecraft.src.World;

import java.util.Random;

public class RedstoneClutchBlock extends GearBoxBlock
{
	public RedstoneClutchBlock(int iBlockID)
	{
        super( iBlockID );
        
        setUnlocalizedName( "fcBlockRedstoneClutch" );        
	}
	
	@Override
    public void updateTick(World world, int i, int j, int k, Random rand )
    {
    	boolean bMechPowered = isInputtingMechanicalPower(world, i, j, k);
    	
    	if ( bMechPowered )
    	{
    		// a Redstone powered gearbox outputs no mechanical power
    		
        	if ( world.isBlockGettingPowered( i, j, k ) || 
    			world.isBlockGettingPowered( i, j + 1, k ) )
        	{    	
        		bMechPowered = false;
        	}
    	}
    	
    	updateMechPoweredState(world, i, j, k, bMechPowered);
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		super.dropComponentItemsOnBadBreak(world, i, j, k, iMetadata, fChanceOfDrop);
		
		dropItemsIndividually(world, i, j, k, Item.goldNugget.itemID, 2, 0, fChanceOfDrop);
		
		return true;
	}
	
    @Override
    public boolean isIncineratedInCrucible()
    {
    	return false;
    }
	
	//------------- Class Specific Methods ------------//
	
	protected boolean isCurrentStateValid(World world, int i, int j, int k)
	{
    	boolean bMechPowered = isInputtingMechanicalPower(world, i, j, k);
    	
    	if ( bMechPowered )
    	{
    		// a Redstone powered gearbox outputs no mechanical power
    		
        	if ( world.isBlockGettingPowered( i, j, k ) || 
        		world.isBlockGettingPowered( i, j + 1, k ) )
        	{    	
        		bMechPowered = false;
        	}
    	}
    	
    	return isGearBoxOn(world, i, j, k) == bMechPowered;
	}
	
	//----------- Client Side Functionality -----------//
}
