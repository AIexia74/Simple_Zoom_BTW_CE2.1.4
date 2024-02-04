// FCMOD

package btw.item.blockitems;

import btw.block.BTWBlocks;
import btw.block.blocks.PlanksBlock;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class WoodMouldingStubBlockItem extends ItemBlock
{
    public WoodMouldingStubBlockItem(int iItemID )
    {
        super( iItemID );
        
        setHasSubtypes( true );        
    }
    
    @Override
    public int getBlockIDToPlace(int iItemDamage, int iFacing, float fClickX, float fClickY, float fClickZ)
    {
    	switch ( iItemDamage )
    	{
    		case 0: // oak
    			
    			return BTWBlocks.oakWoodMouldingAndDecorative.blockID;
    			
    		case 1: // spruce
    			
    			return BTWBlocks.spruceWoodMouldingAndDecorative.blockID;
    			
    		case 2: // birch
    			
    			return BTWBlocks.birchWoodMouldingAndDecorative.blockID;
    			
    		case 3: // jungle
    			
    			return BTWBlocks.jungleWoodMouldingAndDecorative.blockID;
    			
    		default: // blood
    			
    			return BTWBlocks.bloodWoodMouldingAndDecorative.blockID;
    	}
    }
    
    @Override
    public String getUnlocalizedName( ItemStack itemstack )
    {
    	if( itemstack.getItemDamage() == 0 )
    	{
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("oak").toString();
    	}
    	else if( itemstack.getItemDamage() == 1 )
    	{
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("spruce").toString();
    	}
    	else if( itemstack.getItemDamage() == 2 )
    	{
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("birch").toString();
    	}
    	else if( itemstack.getItemDamage() == 3 )
    	{
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("jungle").toString();
    	}
    	else // 4
    	{
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("blood").toString();
    	}
    }
    
    @Override
    public int getFurnaceBurnTime(int iItemDamage)
    {
		return PlanksBlock.getFurnaceBurnTimeByWoodType(iItemDamage) / 4;
    }
}