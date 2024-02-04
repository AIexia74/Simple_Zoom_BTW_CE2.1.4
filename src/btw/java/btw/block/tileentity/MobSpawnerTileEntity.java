// FCMOD

package btw.block.tileentity;

import net.minecraft.src.Block;
import net.minecraft.src.TileEntityMobSpawner;

public class MobSpawnerTileEntity extends TileEntityMobSpawner
{
    public MobSpawnerTileEntity()
    {
        super();
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
			// check for generation of mossy cobble
			
	    	if (worldObj.rand.nextInt(1200) == 0) { // about once a minute
	            if (worldObj.checkChunksExist(xCoord - 4, yCoord - 1, zCoord - 4, xCoord + 4, yCoord + 4, zCoord + 4)) {
		    		// generate a random coordinate around the spawner
		    		int xOffset = worldObj.rand.nextInt(9);
		    		int yOffset = worldObj.rand.nextInt(6);
		    		int zOffset = worldObj.rand.nextInt(9);
		    		
		    		int x = xCoord - 4 + xOffset;
		    		int y = yCoord - 1 + yOffset;
		    		int z = zCoord - 4 + zOffset;
		    		
		    		int targetBlockID = worldObj.getBlockId(x, y, z);
		    		Block targetBlock = Block.blocksList[targetBlockID];
		    		
		    		if (targetBlock != null && targetBlock.canBeConvertedByMobSpawner(worldObj, x, y, z)) {
		    			targetBlock.convertBlockFromMobSpawner(worldObj, x, y, z);
		    		}
	            }
	    	}
        }
        
    	super.updateEntity();
    }    
}