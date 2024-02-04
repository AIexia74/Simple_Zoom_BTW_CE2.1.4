// FCMOD

package btw.item.items;

import btw.entity.mob.CowEntity;
import btw.entity.mob.PigEntity;
import btw.entity.mob.SheepEntity;
import net.minecraft.src.*;

public class BreedingHarnessItem extends Item {
    public BreedingHarnessItem(int itemID) {
    	super(itemID);
    	
    	setBuoyant();
    	
    	setUnlocalizedName("fcItemHarnessBreeding");
    	
    	setCreativeTab(CreativeTabs.tabMaterials);
    }
    
    @Override
    public boolean itemInteractionForEntity(ItemStack itemStack, EntityLiving targetEntity) {
    	if (targetEntity instanceof EntityAnimal) {
	    	EntityAnimal animal = (EntityAnimal)targetEntity;
	
        	if (!animal.isChild() && !animal.getWearingBreedingHarness()) {
		        if (targetEntity instanceof SheepEntity) {
		            if (!animal.worldObj.isRemote) {
		            	SheepEntity sheep = (SheepEntity)animal;
		            	
		            	sheep.setSheared(true);
		            }
		        }
		        else if (!(targetEntity instanceof PigEntity || targetEntity instanceof CowEntity)) {
		        	return false;
		        }
		        
	            itemStack.stackSize--;

	            if (!animal.worldObj.isRemote) {
	            	animal.setWearingBreedingHarness(true);
	            }
	        	
	            return true;
        	}
    	}
        
        return false;
    }
}