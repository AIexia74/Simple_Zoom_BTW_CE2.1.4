// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.World;

import java.util.Random;

public class OreChunkBlockGold extends OreChunkBlock
{
    public OreChunkBlockGold(int iBlockID)
    {
        super( iBlockID );
        
        setUnlocalizedName( "fcBlockChunkOreGold" );
    }
    
	@Override
    public int idDropped( int iMetadata, Random random, int iFortuneModifier )
    {
		return BTWItems.goldOreChunk.itemID;
    }
	
	//------ Client Only Functionality ------//

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked(World world, int x, int y, int z) {
		return BTWItems.goldOreChunk.itemID;
	}
}