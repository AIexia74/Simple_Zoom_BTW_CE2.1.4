// FCMOD

package btw.block.blocks;

import btw.crafting.util.FurnaceBurnTime;
import net.minecraft.src.*;

public class FlowerBlock extends BlockFlower
{
    public FlowerBlock(int iBlockID)
    {
        super( iBlockID );
        
        setHardness( 0F );
        
        setBuoyant();
    	setFurnaceBurnTime(FurnaceBurnTime.DAMP_VEGETATION);
    	setFilterableProperties(Item.FILTERABLE_SMALL);
        
        setStepSound( Block.soundGrassFootstep );
    }
    
    @Override
    public boolean canBeGrazedOn(IBlockAccess access, int i, int j, int k, EntityAnimal animal)
    {
		return true;
    }
    
    @Override
    public boolean canBeCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
    	return true;
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
}
