package net.minecraft.src;

import btw.block.blocks.MyceliumBlock;
import btw.client.fx.BTWEffectManager;
import btw.entity.mob.CowEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class EntityMooshroom extends CowEntity
{
    public EntityMooshroom(World par1World)
    {
        super(par1World);
        this.texture = "/mob/redcow.png";
        this.setSize(0.9F, 1.3F);
    }

    public EntityMooshroom func_94900_c(EntityAgeable par1EntityAgeable)
    {
        return (EntityMooshroom) EntityList.createEntityOfType(EntityMooshroom.class, this.worldObj);
    }

    /**
     * This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.
     */
    public CowEntity spawnBabyAnimal(EntityAgeable par1EntityAgeable)
    {
        return this.func_94900_c(par1EntityAgeable);
    }

    public EntityAgeable createChild(EntityAgeable par1EntityAgeable)
    {
        return this.func_94900_c(par1EntityAgeable);
    }

    // FCMOD: Added New
    @Override
    public void checkForGrazeSideEffects(int i, int j, int k)
    {
    	// override to get rid of mooshroom conversion on eat mycellium
    }
    
    @Override
    public void convertToMooshroom()
    {
    }
    
    @Override
    public void onLivingUpdate()
    {
        if ( !worldObj.isRemote )
        {
            checkForMyceliumSpread();
        }

        super.onLivingUpdate();
    }
    
    @Override
    public boolean interact( EntityPlayer player )
    {
        ItemStack heldStack = player.inventory.getCurrentItem();

        if ( heldStack != null && heldStack.itemID == Item.bowlEmpty.itemID && gotMilk() )
        {
            attackEntityFrom( DamageSource.generic, 0 );
            
            if ( !worldObj.isRemote )
        	{
            	setGotMilk(false);
            	
		        worldObj.playAuxSFX( BTWEffectManager.COW_MILKING_EFFECT_ID,
	                MathHelper.floor_double( posX ), (int)posY,
	                MathHelper.floor_double( posZ ), 0 );
            }
            
            if ( heldStack.stackSize == 1 )
            {
                player.inventory.setInventorySlotContents( player.inventory.currentItem, 
                	new ItemStack( Item.bowlSoup ) );
            }
            else if ( player.inventory.addItemStackToInventory( new ItemStack( Item.bowlSoup ) ) ) 
            {
                player.inventory.decrStackSize(player.inventory.currentItem, 1);
            }
            
            return true;
        }
        
        // skip over parent to avoid vanilla milking
        
        return entityAnimalInteract(player);
    }
    
	//------------- Class Specific Methods ------------//
    
    private void checkForMyceliumSpread()
    {
    	if ( worldObj.provider.dimensionId != 1 && rand.nextInt( 1000 ) == 0 )
    	{
    		MyceliumBlock.checkForMyceliumSpreadFromLocation(worldObj,
    			MathHelper.floor_double( posX ), (int)posY - 1, 
    			MathHelper.floor_double( posZ ) );
    	}
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public String getTexture()
    {
    	if ( getWearingBreedingHarness() )
    	{
			return "/btwmodtex/fc_mr_redcow.png";
    	}
    	
    	int iHungerLevel = getHungerLevel();
    	
    	if ( iHungerLevel == 1 )
    	{
			return "/btwmodtex/fcMooshroomFamished.png";
    	}
    	else if ( iHungerLevel == 2 )
    	{
			return "/btwmodtex/fcMooshroomStarving.png";
    	}
    	
        return texture;
    }
    // END FCMOD    
}
