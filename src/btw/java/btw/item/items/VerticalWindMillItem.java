//FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import btw.block.blocks.AxleBlock;
import btw.entity.mechanical.source.VerticalWindMillEntity;
import net.minecraft.src.*;

public class VerticalWindMillItem extends Item
{
    public VerticalWindMillItem(int iItemID )
    {
        super( iItemID );
        
        setBuoyant();
        
        maxStackSize = 1;
        
        setUnlocalizedName( "fcItemWindMillVertical" );
    	
        setCreativeTab( CreativeTabs.tabRedstone );
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
        int iTargetBlockID = world.getBlockId( i, j, k );
        
        if ( iTargetBlockID == BTWBlocks.axle.blockID )
        {
        	int iAxisAlignment = ((AxleBlock)(BTWBlocks.axle)).getAxisAlignment(world, i, j, k);
        	
        	if ( iAxisAlignment == 0 )
        	{
        		// offset the center of the Wind Mill in the direction the player is looking
        		
        		if ( player.rotationPitch <= 0F )
        		{
        			j += 3; // looking upwards
        		}
        		else
        		{
        			j -= 3;
        		}
        		
        		if ( !checkForSupportingAxles(world, i, j, k) )
        		{
                    if( world.isRemote )
                    {
                    	player.addChatMessage( this.getUnlocalizedName() + ".tooFewAxles" );
                    }
        		}
        		else
        		{
        			VerticalWindMillEntity windMill = (VerticalWindMillEntity) EntityList.createEntityOfType(VerticalWindMillEntity.class, world,
                		(float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F );
    			
	        		if ( windMill.validateAreaAroundDevice() )
	        		{
	        			if ( windMill.isClearOfBlockingEntities() )
	        			{
		                    if ( !world.isRemote )
		                    {
		            			windMill.setRotationSpeed( windMill.computeRotation());
		                        
				                world.spawnEntityInWorld( windMill );
		                    }
			                
			                itemStack.stackSize--;
	        			}
	        			else
	        			{
	                        if( world.isRemote )
	                        {
	                        	player.addChatMessage( "message.windMill.placementObstructed" );
	                        }
	        			}	        			
	        		}
	        		else
	        		{
	                    if( world.isRemote )
	                    {
	                    	player.addChatMessage( "message.windMill.notEnoughRoom" );
	                    }
	        		}
        		}
            }
            
            return true;
        } 
        
        return false;
    }
    
    //------------- Class Specific Methods ------------//
    
    private boolean checkForSupportingAxles(World world, int i, int j, int k)
    {
    	for ( int iTempJ = j - 3; iTempJ <= j + 3; iTempJ++ )
    	{    		
            int iTargetBlockID = world.getBlockId( i, iTempJ, k );
            
            if ( iTargetBlockID == BTWBlocks.axle.blockID )
            {
            	int iAxisAlignment = ((AxleBlock)(BTWBlocks.axle)).getAxisAlignment(world, i, iTempJ, k);
            	
            	if ( iAxisAlignment != 0 )
            	{
            		return false;
            	}
            }
            else
            {
            	return false;
            }
    	}
    	
    	return true;
    }
}