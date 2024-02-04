//FCMOD 

package btw.item.blockitems;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticVegetationBlock;
import btw.block.blocks.PlanterBlock;
import btw.entity.mob.ZombiePigmanEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class AestheticVegetationBlockItem extends ItemBlock
{
    public AestheticVegetationBlockItem(int iItemID )
    {
        super( iItemID );
        
        setMaxDamage( 0 );
        setHasSubtypes(true);
        
        setUnlocalizedName( "fcAestheticVegetation" );
    }

    @Override
    public int getMetadata( int iItemDamage )
    {
    	if ( iItemDamage == AestheticVegetationBlock.SUBTYPE_BLOOD_LEAVES)
    	{
    		// substitute the new block type for the old
    		
    		return 4;
    	}
    	
		return iItemDamage;    	
    }
    
    @Override
    public String getUnlocalizedName( ItemStack itemstack )
    {
    	switch( itemstack.getItemDamage() )
    	{
    		case AestheticVegetationBlock.SUBTYPE_VINE_TRAP:
    		case AestheticVegetationBlock.SUBTYPE_VINE_TRAP_TRIGGERED_BY_ENTITY:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("vinetrap").toString();
    			
    		case AestheticVegetationBlock.SUBTYPE_BLOOD_WOOD_SAPLING:
    			
                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("bloodwoodsapling").toString();
                
			case AestheticVegetationBlock.SUBTYPE_BLOOD_LEAVES:

                return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("bloodleaves").toString();                
    			
			default:
				
				return super.getUnlocalizedName();
    	}
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
    	int iSubtype = itemStack.getItemDamage();
    	
    	if ( iSubtype == AestheticVegetationBlock.SUBTYPE_BLOOD_WOOD_SAPLING)
    	{
	        if ( iFacing != 1)
	        {
	            return false;
	        }
	        
	        if (  player != null )
	        {
	            if ( !player.canPlayerEdit( i, j, k, iFacing, itemStack ) || !player.canPlayerEdit( i, j + 1, k, iFacing, itemStack ) )
		        {
		            return false;
		        }
	        }	        
	            	
	        int iTargetBlockID = world.getBlockId( i, j, k );
	        
	        boolean bValidBlockForGrowth = false;
	        
	        if ( iTargetBlockID == Block.slowSand.blockID )
	        {
	        	bValidBlockForGrowth = true;
	        }
    		else if ( iTargetBlockID == BTWBlocks.planter.blockID )
    		{
    			if (((PlanterBlock) BTWBlocks.planter).getPlanterType(world, i, j, k) == PlanterBlock.TYPE_SOUL_SAND)
    			{
    	        	bValidBlockForGrowth = true;
    			}
    		}
	        
	        if ( bValidBlockForGrowth && world.isAirBlock(i, j + 1, k))
	        {
	            world.setBlockAndMetadataWithNotify( i, j + 1, k, BTWBlocks.aestheticVegetation.blockID, AestheticVegetationBlock.SUBTYPE_BLOOD_WOOD_SAPLING);
	            
	            itemStack.stackSize--;
	            
    	        // verify if we're in the nether
    	        
    	        WorldChunkManager worldchunkmanager = world.getWorldChunkManager();
    	        
    	        if ( worldchunkmanager != null )
    	        {
    	            BiomeGenBase biomegenbase = worldchunkmanager.getBiomeGenAt(i, k);
    	            
    	            if ( biomegenbase instanceof BiomeGenHell )
    	            {
    		            angerPigmen(world, player);
    	            }
    	        }
	            
	            return true;
	        } 
	        else
	        {
	            return false;
	        }
    	}
    	else
    	{
    		return super.onItemUse( itemStack, player, world, i, j, k, iFacing, fClickX, fClickY, fClickZ );
    	}
    }
    
    @Override
    public int getBlockIDToPlace(int iItemDamage, int iFacing, float fClickX, float fClickY, float fClickZ)
    {
    	if ( iItemDamage == AestheticVegetationBlock.SUBTYPE_BLOOD_LEAVES)
    	{
    		// substitute the new block type for the old
    		
    		return BTWBlocks.bloodWoodLeaves.blockID;
    	}
    	
    	return super.getBlockIDToPlace(iItemDamage, iFacing, fClickX, fClickY, fClickZ);
    }
    
    //------------- Class Specific Methods ------------//
    
    private void angerPigmen(World world, EntityPlayer entityPlayer)
    {
        List list = world.getEntitiesWithinAABB( ZombiePigmanEntity.class, entityPlayer.boundingBox.expand( 32D, 32D, 32D ) );
        
        for( int tempIndex = 0; tempIndex < list.size(); tempIndex++ )
        {
            Entity targetEntity = (Entity)list.get( tempIndex );
            
            targetEntity.attackEntityFrom( DamageSource.causePlayerDamage( entityPlayer ), 0 );
            
        }
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage( int iItemDamage )
    {
    	int iSubType = iItemDamage;
    	
    	if ( iSubType == AestheticVegetationBlock.SUBTYPE_BLOOD_WOOD_SAPLING)
    	{
    		return Block.blocksList[this.getBlockID()].getIcon( 1, iItemDamage );    		
    	}
    	else
    	{
    		return super.getIconFromDamage( iItemDamage );
    	}
    }    
}