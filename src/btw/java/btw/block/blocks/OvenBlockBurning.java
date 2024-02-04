// FCMOD

package btw.block.blocks;

public class OvenBlockBurning extends OvenBlock
{
    public OvenBlockBurning(int iBlockID)
    {
        super( iBlockID, true );
        
    	setLightValue( 0.5F ); // 0.875 on standard furnace
    	
    	// this is necessary so changes to nearby lights will propagate past this one
    	
    	setLightOpacity( 8 ); 
    }
    
	@Override
    public boolean isOpaqueCube()
    {
    	// this is necessary so changes to nearby lights will propagate past this one
    	
        return false;
    }
}
