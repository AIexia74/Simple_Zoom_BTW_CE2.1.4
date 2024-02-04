// FCMOD

package btw.item.blockitems;

import btw.block.BTWBlocks;
import btw.block.blocks.PlanksBlock;
import btw.block.blocks.SidingAndCornerAndDecorativeBlock;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class WoodSidingDecorativeStubBlockItem extends ItemBlock
{
	public static final int TYPE_BENCH = 0;
	public static final int TYPE_FENCE = 1;
	
    public WoodSidingDecorativeStubBlockItem(int iItemID )
    {
        super( iItemID );
        
        setHasSubtypes( true );        
    }
    
    @Override
    public int getBlockIDToPlace(int iItemDamage, int iFacing, float fClickX, float fClickY, float fClickZ)
    {
    	int iWoodType = getWoodType(iItemDamage);
    	
    	switch ( iWoodType )
    	{
    		case 0: // oak
    			
    			return BTWBlocks.oakWoodSidingAndCorner.blockID;
    			
    		case 1: // spruce
    			
    			return BTWBlocks.spruceWoodSidingAndCorner.blockID;
    			
    		case 2: // birch
    			
    			return BTWBlocks.birchWoodSidingAndCorner.blockID;
    			
    		case 3: // jungle
    			
    			return BTWBlocks.jungleWoodSidingAndCorner.blockID;
    			
    		default: // blood
    			
    			return BTWBlocks.bloodWoodSidingAndCorner.blockID;
    	}
    }
    
    @Override
    public int getMetadata( int iItemDamage )
    {
    	int iBlockType = getBlockType(iItemDamage);
    	
    	if (iBlockType == TYPE_BENCH)
    	{
    		return SidingAndCornerAndDecorativeBlock.SUBTYPE_BENCH;
    	}
    	else // fence
    	{
    		return SidingAndCornerAndDecorativeBlock.SUBTYPE_FENCE;
    	}
    }
    
    @Override
    public String getUnlocalizedName( ItemStack itemstack )
    {
    	int iWoodType = getWoodType(itemstack.getItemDamage());
    	
    	String sWoodTypeName;
    	
    	if( iWoodType == 0 )
    	{
    		sWoodTypeName = "oak";
    	}
    	else if( iWoodType == 1 )
    	{
    		sWoodTypeName = "spruce";
    	}
    	else if( iWoodType == 2 )
    	{
    		sWoodTypeName = "birch";
    	}
    	else if( iWoodType == 3 )
    	{
    		sWoodTypeName = "jungle";
    	}
    	else // 4
    	{
    		sWoodTypeName = "blood";
    	}
    	
    	int iBlockType = itemstack.getItemDamage() >> 2;
    	
    	String sBlockTypeName;
    	
    	if (iBlockType == TYPE_BENCH)
    	{
    		sBlockTypeName = "bench";
    	}
    	else // fence
    	{
    		sBlockTypeName = "fence";    		
    	}
    	
    	return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append(sWoodTypeName).append(".").append(sBlockTypeName).toString();
    }
    
    @Override
    public int getFurnaceBurnTime(int iItemDamage)
    {
		return PlanksBlock.getFurnaceBurnTimeByWoodType(getWoodType(iItemDamage)) / 2;
    }
    
    //------------- Class Specific Methods ------------//
	
    static public int getWoodType(int iItemDamage)
    {
    	int iWoodType = ( iItemDamage & 3 ) | ( ( iItemDamage >> 4 ) << 2 );
    	
    	return iWoodType;    	
    }
    
    static public int getBlockType(int iItemDamage)
    {
    	int iBlockType = ( iItemDamage >> 2 ) & 3;
    	
    	return iBlockType;
    }
    
    static public int getItemDamageForType(int iWoodType, int iBlockType)
    {
    	int iItemDamage = ( iWoodType & 3 ) | ( ( iWoodType >> 2 ) << 4 ) | ( iBlockType << 2 );
    	
    	return iItemDamage;
    }
}
