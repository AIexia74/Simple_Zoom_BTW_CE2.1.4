package btw.inventory;

import btw.block.tileentity.*;
import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import btw.client.gui.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.GuiFurnace;
import net.minecraft.src.InventoryBasic;

public class BTWContainers {
	// custom Container IDs
	// vanilla IDs can be found in NetClientHandler.handleOpenWindow()
	public static int cauldronContainerID = 223;
	public static int hopperContainerID = 224;
	public static int crucibleContainerID = 225;
	public static int soulforgeContainerID = 226;
	public static int blockDispenserContainerID = 227;
	public static int pulleyContainerID = 228;
	public static int infernalEnchanterContainerID = 229;
	public static int furnaceBrickContainerID = 230;
	public static int hamperContainerID = 231;
	public static int anvilContainerID = 232;
	public static int loomContainerID = 233;
	
	@Environment(EnvType.CLIENT)
	public static GuiContainer getAssociatedGui(EntityClientPlayerMP entityclientplayermp, int containerID) {
		if (containerID == cauldronContainerID) {
			CauldronTileEntity cauldronEntity = new CauldronTileEntity();
			return new CookingVesselGui(entityclientplayermp.inventory, cauldronEntity, containerID);
		}
		else if (containerID == hopperContainerID) {
			HopperTileEntity hopperEntity = new HopperTileEntity();
			return new HopperGui(entityclientplayermp.inventory, hopperEntity);
		}
		else if (containerID == crucibleContainerID) {
			CrucibleTileEntity crucibleEntity = new CrucibleTileEntity();
			return new CookingVesselGui(entityclientplayermp.inventory, crucibleEntity, containerID);
		}
		else if (containerID == soulforgeContainerID) {
			return new CraftingGuiSoulforge(entityclientplayermp.inventory, entityclientplayermp.worldObj, 0, 0, 0);
		}
		else if (containerID == blockDispenserContainerID) {
			BlockDispenserTileEntity dispenserEntity = new BlockDispenserTileEntity();
			return new BlockDispenserGui(entityclientplayermp.inventory, dispenserEntity);
		}
		else if (containerID == pulleyContainerID) {
			PulleyTileEntity pulleyEntity = new PulleyTileEntity();
			return new PulleyGui(entityclientplayermp.inventory, pulleyEntity);
		}
		else if (containerID == infernalEnchanterContainerID) {
			return new InfernalEnchanterGui(entityclientplayermp.inventory, entityclientplayermp.worldObj, 0, 0, 0);
		}
		else if (containerID == furnaceBrickContainerID) {
			OvenTileEntity brickFurnaceEntity = new OvenTileEntity();
			return new GuiFurnace(entityclientplayermp.inventory, brickFurnaceEntity);
		}
		else if (containerID == hamperContainerID) {
			// Hamper uses a dummy inventory for the client to avoid calls on openChest() and closeChest()
			// which would crash if not initialized.  This is the same way the vanilla chest handles it.
			InventoryBasic hamperInventory = new InventoryBasic(HamperTileEntity.FC_HAMPER, false, HamperTileEntity.INVENTORY_SIZE);
			return new HamperGui(entityclientplayermp.inventory, hamperInventory);
		}
		else if (containerID == anvilContainerID) {
			return new CraftingGuiAnvil(entityclientplayermp.inventory, entityclientplayermp.worldObj, 0, 0, 0);
		}
		else if (containerID == loomContainerID) {
			LoomTileEntity loomEntity = new LoomTileEntity();
			return new LoomGui(entityclientplayermp.inventory, loomEntity);
		}
		
		return null;
	}
}
