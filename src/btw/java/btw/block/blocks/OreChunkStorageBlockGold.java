// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.minecraft.src.World;

public class OreChunkStorageBlockGold extends OreChunkStorageBlock
{
    public OreChunkStorageBlockGold(int iBlockID)
    {
        super( iBlockID );
        
        setUnlocalizedName( "fcBlockChunkOreStorageGold" );
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.goldOreChunk.itemID,
							  9, 0, fChanceOfDrop);
		
		return true;
	}
    
	//------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}