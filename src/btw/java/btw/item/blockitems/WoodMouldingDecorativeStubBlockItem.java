// FCMOD

package btw.item.blockitems;

import btw.block.BTWBlocks;
import btw.block.blocks.MouldingAndDecorativeBlock;
import btw.block.blocks.PlanksBlock;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class WoodMouldingDecorativeStubBlockItem extends ItemBlock
{
	public static final int TYPE_COLUMN = 0;
	public static final int TYPE_PEDESTAL = 1;
	public static final int TYPE_TABLE = 2;
	
    public WoodMouldingDecorativeStubBlockItem(int iItemID )
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
    public int getMetadata( int iItemDamage )
    {
    	int iBlockType = getBlockType(iItemDamage);
    	
    	if (iBlockType == TYPE_COLUMN)
    	{
    		return MouldingAndDecorativeBlock.SUBTYPE_COLUMN;
    	}
    	else if (iBlockType == TYPE_PEDESTAL)
    	{
    		return MouldingAndDecorativeBlock.SUBTYPE_PEDESTAL_UP;
    	}
    	else // table
    	{
    		return MouldingAndDecorativeBlock.SUBTYPE_TABLE;
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
    	
    	int iBlockType = getBlockType(itemstack.getItemDamage());
    	
    	String sBlockTypeName;
    	
    	if (iBlockType == TYPE_COLUMN)
    	{
    		sBlockTypeName = "column";
    	}
    	else if (iBlockType == TYPE_PEDESTAL)
    	{
    		sBlockTypeName = "pedestal";
    	}
    	else // table
    	{
    		sBlockTypeName = "table";    		
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
