// FCMOD

package btw.item.blockitems.legacy;

import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class LegacySubstitutionBlockItem extends ItemBlock
{
	protected int substituteBlockID;
	
    public LegacySubstitutionBlockItem(int iItemID, int iSubstituteBlockID )
    {
    	super( iItemID );

        substituteBlockID = iSubstituteBlockID;
    }
    
    @Override
    public int getBlockIDToPlace(int iItemDamage, int iFacing, float fClickX, float fClickY, float fClickZ)
    {
    	return substituteBlockID;
    }
    
    @Override
    public String getItemDisplayName( ItemStack stack )
    {
        return "Old " + super.getItemDisplayName( stack );
    }    
}
