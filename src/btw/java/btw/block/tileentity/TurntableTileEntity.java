// FCMOD

package btw.block.tileentity;

import btw.block.BTWBlocks;
import btw.block.blocks.TurntableBlock;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

public class TurntableTileEntity extends TileEntity {
	private final int maxHeightOfBlocksRotated = 2;
	
	private int rotationTickCount;
	public int craftingRotationCount;
	
    private static int ticksToRotate[];
    
	private int switchOverride; // legacy support for old data value
	
    static {
    	ticksToRotate = (new int[] {
			10, 
			20, 40, 80, 200, 600,
			1200, 2400, 6000, 12000, 24000
        });
    }
    
    public TurntableTileEntity() {
    	rotationTickCount = 0;
		craftingRotationCount = 0;
    	
    	switchOverride = -1;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        
    	rotationTickCount = nbttagcompound.getInteger("m_iRotationCount"); // legacy name
    	
        if (nbttagcompound.hasKey("m_iSwitchSetting")) {
        	// legacy data format support
        	switchOverride = nbttagcompound.getInteger("m_iSwitchSetting");
	    	
	    	if (switchOverride > 3) {
	    		switchOverride = 3;
	    	}
        }
    	
        if(nbttagcompound.hasKey("m_iPotteryRotationCount")) { // legacy name
			craftingRotationCount = nbttagcompound.getInteger("m_iPotteryRotationCount"); // legacy name
        }
    }
    
    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        
        nbttagcompound.setInteger("m_iRotationCount", rotationTickCount); // legacy name        
        nbttagcompound.setInteger("m_iPotteryRotationCount", craftingRotationCount); // legacy name
    }
        
    @Override
    public void updateEntity() {
    	// Remove the following block for the server
    	if (worldObj.isRemote) {
        	if (((TurntableBlock) BTWBlocks.turntable).isBlockMechanicalOn(worldObj, xCoord, yCoord, zCoord))
        	{
        		rotationTickCount++;
        		
        		if (rotationTickCount >= getTicksToRotate()) {
                    worldObj.playSound((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D, "random.click", 0.05F, 1.0F);
        			
            		rotationTickCount = 0;    	
        		}
        	}
        	else {
        		rotationTickCount = 0;    	
    		}
        	
    		return;
    	}
    	
    	if (switchOverride >= 0) {
    		// support for legacy data format
    		((TurntableBlock) BTWBlocks.turntable).setSwitchSetting(worldObj, xCoord, yCoord, zCoord, switchOverride);
				
			switchOverride = -1;
    	}
    	
        byte updateOffset = 9; // standard block update range + 1 to take into account stuff that may be attached

        if (!worldObj.checkChunksExist(xCoord - updateOffset, yCoord - updateOffset, zCoord - updateOffset, 
        		xCoord + updateOffset, yCoord + updateOffset, zCoord + updateOffset))
        {
        	return;
    	}

    	if (((TurntableBlock) BTWBlocks.turntable).isBlockMechanicalOn(worldObj, xCoord, yCoord, zCoord))
    	{
    		rotationTickCount++;
    		
    		if (rotationTickCount >= getTicksToRotate()) {
    			rotateTurntable();
        		rotationTickCount = 0;    	
    		}
    	}
    	else {
    		rotationTickCount = 0;    	
		}
    }
    
	//------------- Class Specific Methods ------------//
    
    private int getTicksToRotate() {
    	return ticksToRotate[((TurntableBlock) BTWBlocks.turntable).getSwitchSetting(worldObj, xCoord, yCoord, zCoord)];
    }
    
    private void rotateTurntable() {
    	boolean reverseDirection = ((TurntableBlock) BTWBlocks.turntable).isBlockRedstoneOn(worldObj, xCoord, yCoord, zCoord);
    	
    	int craftingCounter = craftingRotationCount;
    	
    	for (int j = yCoord + 1; j <= yCoord + maxHeightOfBlocksRotated; j++) {
        	Block targetBlock = Block.blocksList[worldObj.getBlockId(xCoord, j, zCoord)];

        	if (targetBlock != null && targetBlock.canRotateOnTurntable(worldObj, xCoord, j, zCoord)) {
	    		// have to store the transmission capacity as rotation may affect it (like with crafter blocks)
	    		boolean canTransmitHorizontally = targetBlock.canTransmitRotationHorizontallyOnTurntable(worldObj, xCoord, j, zCoord);
	    		
	    		boolean canTransmitVertically = targetBlock.canTransmitRotationVerticallyOnTurntable(worldObj, xCoord, j, zCoord);
	    		
	    		craftingCounter = targetBlock.rotateOnTurntable(worldObj, xCoord, j, zCoord, reverseDirection, craftingCounter);
				
		    	if (canTransmitHorizontally) {
			    	rotateBlocksAttachedToBlock(xCoord, j, zCoord, reverseDirection);
		    	}
		    	
		    	if (!canTransmitVertically){
		    		break;
		    	}
        	}
        	else {
        		break;
        	}
    	}
    	
    	if (craftingCounter > craftingRotationCount) {
			craftingRotationCount = craftingCounter;
    	}
    	else {
			craftingRotationCount = 0;
    	}
    	
    	// notify the neighbours so Buddy can pick up on this change
    	
    	worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, BTWBlocks.turntable.blockID);
    }   
    
	private void rotateBlocksAttachedToBlock(int x, int y, int z, boolean reverseDirection) {
		int newBlockIDs[] = new int[4];
		int newMetadataArray[] = new int[4];
		
		for (int i = 0; i < 4; i++) {
			newBlockIDs[i] = 0;
			newMetadataArray[i] = 0;
		}
		
		for (int i = 2; i <=5; i++) {
			BlockPos pos = new BlockPos(x, y, z, i);
			
			int blockID = worldObj.getBlockId(pos.x, pos.y, pos.z);
			Block block = Block.blocksList[blockID];
			
			if (block != null) {
				int oppositeFacing = Block.getOppositeFacing(i);
				
				if (block.canRotateAroundBlockOnTurntableToFacing(worldObj, pos.x, pos.y, pos.z, oppositeFacing)) {
					if (block.onRotatedAroundBlockOnTurntableToFacing(worldObj, pos.x, pos.y, pos.z, oppositeFacing)) {
						int destinationFacing = Block.rotateFacingAroundY(i, reverseDirection);
						
						newBlockIDs[destinationFacing - 2] = blockID;
						
						newMetadataArray[destinationFacing - 2] = block.getNewMetadataRotatedAroundBlockOnTurntableToFacing(
								worldObj, pos.x, pos.y, pos.z,
								oppositeFacing, Block.getOppositeFacing(destinationFacing));
						
						worldObj.setBlockWithNotify(pos.x, pos.y, pos.z, 0);
					}
				}
			}
		}
		
		for (int i = 0; i < 4; i++) {
			int blockID = newBlockIDs[i];
			
			if (blockID != 0) {
				int facing = i + 2;
				int metadata = newMetadataArray[i];
				
				BlockPos pos = new BlockPos(x, y, z);
				pos.addFacingAsOffset(facing);
				
				if (WorldUtils.isReplaceableBlock(worldObj, pos.x, pos.y, pos.z)) {
					worldObj.setBlockAndMetadataWithNotify(pos.x, pos.y, pos.z, blockID, metadata);
				}
				else {
					// target block is occupied.  Eject rotated block as item at old location
					Block block = Block.blocksList[blockID];
					
					int oldFacing = Block.rotateFacingAroundY(facing, !reverseDirection);
					BlockPos oldPos = new BlockPos(x, y, z, oldFacing);
					
					block.dropBlockAsItem(worldObj, oldPos.x, oldPos.y, oldPos.z, blockID, 0);
				}
			}
		}
	}	
}