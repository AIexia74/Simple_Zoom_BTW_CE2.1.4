// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.BlockEnchantmentTable;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

import java.util.Random;

public class EnchantingTableBlock extends BlockEnchantmentTable
{
    public EnchantingTableBlock(int iBlockID)
    {
        super( iBlockID );
        
        initBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
    }
    
	//------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess,
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {	
    	if ( iSide != 1 )
    	{
    		return super.shouldSideBeRendered( blockAccess, iNeighborI, iNeighborJ, iNeighborK, 
    			iSide );
    	}
    	
    	return true;
    }
    
    @Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		super.randomDisplayTick(world, x, y, z, rand);
	
		for (int i = x - 2; i <= x + 2; i++) {
			for (int k = z - 2; k <= z + 2; k++) {
				if (i > x - 2 && i < x + 2 && k == z - 1) {
					k = z + 2;
				}
			
				if (rand.nextInt(16) == 0) {
					for (int j = y - 1; j <= y + 2; j++) {
						if (world.getBlockId(i, j, k) == Block.bookShelf.blockID) {
							if (!world.isAirBlock((i - x) / 2 + x, j, (k - z) / 2 + z)) {
								break;
							}
						
							world.spawnParticle("enchantmenttable",
									x + 0.5D, y + 2.0D, z + 0.5D,
									(i - x) + rand.nextFloat() - 0.5D, (j - y) - rand.nextFloat() - 1.0F, (k - z) + rand.nextFloat() - 0.5D);
						}
					}
				}
			}
		}
	}
}
