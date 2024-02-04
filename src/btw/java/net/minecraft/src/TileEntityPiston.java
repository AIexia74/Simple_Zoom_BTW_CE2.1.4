package net.minecraft.src;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.crafting.manager.PistonPackingCraftingManager;
import btw.crafting.recipe.types.PistonPackingRecipe;
import btw.item.util.ItemUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileEntityPiston extends TileEntity
{
	private int storedBlockID;
	private int storedMetadata;
	
	public NBTTagCompound storedTileEntityData;
	
	public TileEntity cachedTileEntity;
	
	/** the side the front of the piston is on */
	private int storedOrientation;
	
	/** if this piston is extending or not */
	private boolean extending;
	private boolean shouldHeadBeRendered;
	private float progress;
	
	/** the progress in (de)extending */
	private float lastProgress;
	private List pushedObjects = new ArrayList();
	
	// FCMOD: Added
	private boolean shoveledBlock = false;
	
	public TileEntityPiston( int iBlockID, int iMetadata, int iFacing, boolean bExtending, boolean bShouldHeadBeRendered, boolean bShoveledBlock )
	{
		this( iBlockID, iMetadata, iFacing, bExtending, bShouldHeadBeRendered );
		
		shoveledBlock = true;
	}
	// END FCMOD
	
	public TileEntityPiston() {}
	
	public TileEntityPiston(int par1, int par2, int par3, boolean par4, boolean par5)
	{
		this.storedBlockID = par1;
		this.storedMetadata = par2;
		this.storedOrientation = par3;
		this.extending = par4;
		this.shouldHeadBeRendered = par5;
	}
	public void storeTileEntity(NBTTagCompound tileEntityData) {
		this.storedTileEntityData = tileEntityData;
		if (storedTileEntityData != null) {
			storeCachedTileEntity();
		}
	}
	
	public void storeCachedTileEntity() {
		cachedTileEntity = TileEntity.createAndLoadEntity(storedTileEntityData);
		cachedTileEntity.setWorldObj(this.worldObj);
		cachedTileEntity.blockMetadata = this.storedMetadata;
		cachedTileEntity.blockType = Block.blocksList[this.getStoredBlockID()];
		
		
	}
	
	public int getStoredBlockID()
	{
		return this.storedBlockID;
	}
	
	/**
	 * Returns block data at the location of this entity (client-only).
	 */
	public int getBlockMetadata()
	{
		return this.storedMetadata;
	}
	
	/**
	 * Returns true if a piston is extending
	 */
	public boolean isExtending()
	{
		return this.extending;
	}
	
	/**
	 * Returns the orientation of the piston as an int
	 */
	public int getPistonOrientation()
	{
		return this.storedOrientation;
	}
	
	@Environment(EnvType.CLIENT)
	public boolean shouldRenderHead()
	{
		return this.shouldHeadBeRendered;
	}
	
	/**
	 * Get interpolated progress value (between lastProgress and progress) given the fractional time between ticks as an
	 * argument.
	 */
	public float getProgress(float par1)
	{
		if (par1 > 1.0F)
		{
			par1 = 1.0F;
		}
		
		return this.lastProgress + (this.progress - this.lastProgress) * par1;
	}
	
	@Environment(EnvType.CLIENT)
	public float getOffsetX(float par1)
	{
		return this.extending ? (this.getProgress(par1) - 1.0F) * (float)Facing.offsetsXForSide[this.storedOrientation] : (1.0F - this.getProgress(par1)) * (float)Facing.offsetsXForSide[this.storedOrientation];
	}
	
	@Environment(EnvType.CLIENT)
	public float getOffsetY(float par1)
	{
		return this.extending ? (this.getProgress(par1) - 1.0F) * (float)Facing.offsetsYForSide[this.storedOrientation] : (1.0F - this.getProgress(par1)) * (float)Facing.offsetsYForSide[this.storedOrientation];
	}
	
	@Environment(EnvType.CLIENT)
	public float getOffsetZ(float par1)
	{
		return this.extending ? (this.getProgress(par1) - 1.0F) * (float)Facing.offsetsZForSide[this.storedOrientation] : (1.0F - this.getProgress(par1)) * (float)Facing.offsetsZForSide[this.storedOrientation];
	}
	
	private void updatePushedObjects(float par1, float par2)
	{
		if (this.extending)
		{
			par1 = 1.0F - par1;
		}
		else
		{
			--par1;
		}
		
		AxisAlignedBB var3 = Block.pistonMoving.getAxisAlignedBB(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.storedBlockID, par1, this.storedOrientation);
		
		if (var3 != null)
		{
			List var4 = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, var3);
			
			if (!var4.isEmpty())
			{
				this.pushedObjects.addAll(var4);
				Iterator var5 = this.pushedObjects.iterator();
				
				while (var5.hasNext())
				{
					Entity var6 = (Entity)var5.next();
					var6.moveEntity((double)(par2 * (float)Facing.offsetsXForSide[this.storedOrientation]), (double)(par2 * (float)Facing.offsetsYForSide[this.storedOrientation]), (double)(par2 * (float)Facing.offsetsZForSide[this.storedOrientation]));
				}
				
				this.pushedObjects.clear();
			}
		}
	}
	
	/**
	 * removes a pistons tile entity (and if the piston is moving, stops it)
	 */
	public void clearPistonTileEntity()
	{
		if (this.lastProgress < 1.0F && this.worldObj != null)
		{
			
			this.lastProgress = this.progress = 1.0F;
			this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
			this.invalidate();
			
			if (this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord) == Block.pistonMoving.blockID)
			{
				// FCMOD: Added
				if ( destroyAndDropIfShoveled() )
				{
					return;
				}
				
				preBlockPlaced();
				// END FCMOD
				
				this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, this.storedBlockID, this.storedMetadata, 3);
				
				if (this.storedTileEntityData != null) {
					restoreBlockTileEntity();
				}
				
				this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.storedBlockID);
			}
		}
	}
	
	public void restoreBlockTileEntity() {
		TileEntity tileEntity = TileEntity.createAndLoadEntity(storedTileEntityData);
		tileEntity.xCoord = xCoord;
		tileEntity.yCoord = yCoord;
		tileEntity.zCoord = zCoord;
		cachedTileEntity = null;
		worldObj.setBlockTileEntity(xCoord, yCoord, zCoord, tileEntity);
	}
	
	/**
	 * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
	 * ticks and creates a new spawn inside its implementation.
	 */
	public void updateEntity()
	{
		this.lastProgress = this.progress;
		
		if (this.lastProgress >= 1.0F)
		{
			this.updatePushedObjects(1.0F, 0.25F);
			
			// FCMOD: Added
			attemptToPackItems();
			// END FCMOD
			
			this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
			this.invalidate();
			
			if (this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord) == Block.pistonMoving.blockID) {
				// FCMOD: Added
				if (destroyAndDropIfShoveled()) {
					return;
				}
				
				preBlockPlaced();
				// END FCMOD
				
				// bandaid for chests until they get reworked to be able to sit next to eachother
				if( storedBlockID == BTWBlocks.chest.blockID && !Block.blocksList[storedBlockID].canPlaceBlockAt(worldObj, this.xCoord, this.yCoord, this.zCoord)) {
					restoreStoredBlock();
					
					worldObj.destroyBlock(xCoord, yCoord, zCoord, true);
				}
				else {
					restoreStoredBlock();
				}
			}
		}
		else
		{
			this.progress += 0.5F;
			
			if (this.progress >= 1.0F)
			{
				this.progress = 1.0F;
			}
			
			if (this.extending)
			{
				this.updatePushedObjects(this.progress, this.progress - this.lastProgress + 0.0625F);
			}
		}
	}
	
	public void restoreStoredBlock() {
		this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, this.storedBlockID, this.storedMetadata, 3);
		
		if (this.storedTileEntityData != null) {
			restoreBlockTileEntity();
		}
		
		this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.storedBlockID);
	}
	
	/**
	 * Reads a tile entity from NBT.
	 */
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		this.storedBlockID = par1NBTTagCompound.getInteger("blockId");
		this.storedMetadata = par1NBTTagCompound.getInteger("blockData");
		this.storedOrientation = par1NBTTagCompound.getInteger("facing");
		this.lastProgress = this.progress = par1NBTTagCompound.getFloat("progress");
		this.extending = par1NBTTagCompound.getBoolean("extending");
		
		// FCMOD: Added
		if ( par1NBTTagCompound.hasKey( "fcShovel" ) )
		{
			shoveledBlock = par1NBTTagCompound.getBoolean("fcShovel");
		}
		if ( par1NBTTagCompound.hasKey( "fcTileEntityData" ) )
		{
			storedTileEntityData = par1NBTTagCompound.getCompoundTag("fcTileEntityData");
		}
		if ( par1NBTTagCompound.hasKey( "fcCachedTileEntity" ) )
		{
			storedTileEntityData = par1NBTTagCompound.getCompoundTag("fcCachedTileEntity");
			if(storedTileEntityData != null) {
				storeCachedTileEntity();
			}
		}
		// END FCMOD
	}
	
	/**
	 * Writes a tile entity to NBT.
	 */
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("blockId", this.storedBlockID);
		par1NBTTagCompound.setInteger("blockData", this.storedMetadata);
		par1NBTTagCompound.setInteger("facing", this.storedOrientation);
		par1NBTTagCompound.setFloat("progress", this.lastProgress);
		par1NBTTagCompound.setBoolean("extending", this.extending);
		
		// FCMOD: Added
		par1NBTTagCompound.setBoolean("fcShovel", shoveledBlock);
		if (storedTileEntityData != null) {
			par1NBTTagCompound.setCompoundTag("fcTileEntityData", storedTileEntityData);
		}
		// END FCMOD
	}
	
	// FCMOD: Added
    /*private void AttemptToPackItems()
    {
    	if ( !worldObj.isRemote && isExtending() && ( storedBlockID == Block.pistonExtension.blockID || Block.isNormalCube( storedBlockID ) || storedBlockID == Block.glass.blockID ) )
    	{
	    	FCUtilsBlockPos targetPos = new FCUtilsBlockPos( xCoord, yCoord, zCoord, storedOrientation );
	    	
	    	if ( IsLocationSuitableForPacking( targetPos.i, targetPos.j, targetPos.k, Block.GetOppositeFacing( storedOrientation ) ) )
			{
	    		AxisAlignedBB targetBox = AxisAlignedBB.getAABBPool().getAABB((double)targetPos.i, (double)targetPos.j, (double)targetPos.k, 
	    			(double)targetPos.i + 1D, (double)targetPos.j + 1D, (double)targetPos.k + 1D );
	    		
	    		List itemsWithinBox = worldObj.getEntitiesWithinAABB( EntityItem.class, targetBox );
	    		
	    		if ( !itemsWithinBox.isEmpty() )
	    		{
	                Iterator itemIterator = itemsWithinBox.iterator();
	
	                while ( itemIterator.hasNext() )
	                {
	        			EntityItem tempItem = (EntityItem)itemIterator.next();
	        			
	        			if ( !tempItem.isDead )
	        			{
		        			ItemStack tempStack = tempItem.getEntityItem();
		        			
		        			if ( IsPackableItem( tempStack ) )
		        			{
		        				int iRequiredCount = GetItemCountToPack( tempStack );
		        				int iCountOfItems = CountItemsOfTypeInList( tempStack, itemsWithinBox );
		        				
		        				if ( iCountOfItems >= iRequiredCount )
		        				{
		        					RemoveItemsOfTypeFromList( tempStack, iRequiredCount, itemsWithinBox );
		        					
		        					CreatePackedBlockOfTypeAtLocation( tempStack, targetPos.i, targetPos.j, targetPos.k );
		        					
		        					break;
		        				}
		        			}
	        			}
	                }
	    		}
			}
    	}
    }*/
	
	private void attemptToPackItems() {
		if (!worldObj.isRemote && isExtending() && (storedBlockID == Block.pistonExtension.blockID || Block.isNormalCube(storedBlockID) || storedBlockID == Block.glass.blockID)) {
			BlockPos pos = new BlockPos(xCoord, yCoord, zCoord, storedOrientation);
			
			if (isLocationSuitableForPacking(pos.x, pos.y, pos.z, Block.getOppositeFacing(storedOrientation))) {
				AxisAlignedBB targetBox = AxisAlignedBB.getAABBPool().getAABB(
						pos.x, pos.y, pos.z,
						pos.x + 1D, pos.y + 1D, pos.z + 1D
				);
				
				List<EntityItem> itemsWithinBox = worldObj.getEntitiesWithinAABB(EntityItem.class, targetBox);
				
				if (!itemsWithinBox.isEmpty()) {
					PistonPackingRecipe recipe = PistonPackingCraftingManager.instance.getValidRecipeFromItemList(itemsWithinBox);
					
					if (recipe != null) {
						for (ItemStack stack : recipe.getInput()) {
							removeItemsOfTypeFromList(stack, stack.stackSize, itemsWithinBox);
						}
						
						createPackedBlockOfTypeAtLocation(recipe.getOutput().blockID, recipe.getOutputMetadata(), pos.x, pos.y, pos.z);
					}
				}
			}
		}
	}
	
	private boolean isLocationSuitableForPacking(int i, int j, int k, int iPistonDirection)
	{
		if ( worldObj.isAirBlock( i, j, k ) )
		{
			for ( int iTempFacing = 0; iTempFacing < 6; iTempFacing++ )
			{
				if ( iTempFacing != iPistonDirection )
				{
					BlockPos tempPos = new BlockPos( i, j, k, iTempFacing );
					
					if ( !isBlockSuitableForPackingToFacing(tempPos.x, tempPos.y, tempPos.z, Block.getOppositeFacing(iTempFacing)) )
					{
						return false;
					}
					
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	private boolean isBlockSuitableForPackingToFacing(int i, int j, int k, int iFacing)
	{
		Block block = Block.blocksList[worldObj.getBlockId( i, j, k )];
		
		if ( block != null )
		{
			return block.canContainPistonPackingToFacing(worldObj, i, j, k, iFacing);
		}
		
		return false;
	}
	
	private void createPackedBlockOfTypeAtLocation(int blockID, int metadata, int x, int y, int z) {
		worldObj.setBlockAndMetadataWithNotify(x, y, z, blockID, metadata);
		
		worldObj.playAuxSFX(BTWEffectManager.BLOCK_PLACE_EFFECT_ID, x, y, z, blockID);
	}
	
	private int countItemsOfTypeInList(ItemStack stack, List list)
	{
		Iterator itemIterator = list.iterator();
		int iCount = 0;
		
		while ( itemIterator.hasNext() )
		{
			EntityItem tempItem = (EntityItem)itemIterator.next();
			
			if ( !tempItem.isDead )
			{
				ItemStack tempStack = tempItem.getEntityItem();
				
				if ( tempStack.itemID == stack.itemID )
				{
					iCount += tempStack.stackSize;
				}
			}
		}
		
		return iCount;
	}
	
	private void removeItemsOfTypeFromList(ItemStack stack, int iCount, List list)
	{
		Iterator itemIterator = list.iterator();
		
		while ( itemIterator.hasNext() )
		{
			EntityItem tempItem = (EntityItem)itemIterator.next();
			
			if ( !tempItem.isDead )
			{
				ItemStack tempStack = tempItem.getEntityItem();
				
				if ( tempStack.itemID == stack.itemID )
				{
					if ( tempStack.stackSize > iCount )
					{
						tempStack.stackSize -= iCount;
						
						break;
					}
					else
					{
						iCount -= tempStack.stackSize;
						
						tempStack.stackSize = 0;
						
						tempItem.setDead();
						
						if ( iCount <= 0 )
						{
							break;
						}
					}
				}
			}
		}
		
	}
	
	private boolean destroyAndDropIfShoveled() {
		if (shoveledBlock) {
			Block tempBlock = Block.blocksList[storedBlockID];
			
			if (tempBlock != null && !worldObj.isRemote) {
				ItemStack tempStack = null;
				
				if (tempBlock.canSilkHarvest(storedMetadata)) {
					tempStack = tempBlock.createStackedBlock(storedMetadata);
				}
				else {
					int idDropped = tempBlock.idDropped(storedMetadata, worldObj.rand, 0);
					
					if (idDropped != 0) {
						tempStack = new ItemStack(idDropped, tempBlock.quantityDropped(worldObj.rand), tempBlock.damageDropped(storedMetadata));
					}
				}
				
				if (tempStack != null) {
					ejectStackOnShoveled(tempStack);
				}
			}
			
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			worldObj.notifyBlockOfNeighborChange(xCoord, yCoord, zCoord, storedBlockID);
			
			return true;
		}
		
		return false;
	}
	
	private void ejectStackOnShoveled(ItemStack stack)
	{
		BlockPos sourcePos = new BlockPos( xCoord, yCoord, zCoord, Block.getOppositeFacing(storedOrientation) );
		
		ItemUtils.ejectStackFromBlockTowardsFacing(worldObj, sourcePos.x, sourcePos.y, sourcePos.z, stack, storedOrientation);
	}
	
	private void preBlockPlaced()
	{
		Block tempBlock = Block.blocksList[storedBlockID];
		
		if ( tempBlock != null && !worldObj.isRemote )
		{
			storedMetadata = tempBlock.onPreBlockPlacedByPiston(worldObj, xCoord, yCoord, zCoord,
					storedMetadata, getDirectionMoving());
		}
	}
	
	private int getDirectionMoving()
	{
		if ( !extending )
		{
			return Block.getOppositeFacing(storedOrientation);
		}
		
		return storedOrientation;
	}
	// END FCMOD
}
