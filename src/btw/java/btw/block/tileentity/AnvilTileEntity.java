// FCMOD

package btw.block.tileentity;

import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class AnvilTileEntity extends TileEntity
{
    private int mouldContentsBitField;
    
	public AnvilTileEntity()
	{
        mouldContentsBitField = 0;
	}
	
    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        
        nbttagcompound.setInteger("m_iMouldContentsBitField", mouldContentsBitField);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        
        if( nbttagcompound.hasKey( "m_iMouldContentsBitField" ) )
        {
            mouldContentsBitField = nbttagcompound.getInteger("m_iMouldContentsBitField");
        }
        else
        {
            mouldContentsBitField = 0;
        }        
    }    
    
    //************* Class Specific Methods ************//
    
    public void clearMouldContents()
    {
        mouldContentsBitField = 0;
    }
    
    public boolean doesSlotContainMould(int iSlotX, int iSlotY)
    {
    	int iSlotNum = ( iSlotX + iSlotY * 4 );
    	
    	return doesSlotContainMould(iSlotNum);
    }
    
    public boolean doesSlotContainMould(int iSlotNum)
    {
    	int iBitMask = 1 << iSlotNum;
    	
    	return (mouldContentsBitField & iBitMask ) > 0;
    }
    
    public void setSlotContainsMould(int iSlotNum)
    {
    	int iBitMask = 1 << iSlotNum;

        mouldContentsBitField |= iBitMask;
    }
    
    public void setSlotContainsMould(int iSlotX, int iSlotY)
    {
    	int iBitMask = 1 << ( iSlotX + iSlotY * 4 );

        mouldContentsBitField |= iBitMask;
    }
    
    public void ejectMoulds()
    {
    	int iMouldCount = 0;
    	
    	for ( int iTemp = 0; iTemp < 16; iTemp++ )
    	{
    		if ( doesSlotContainMould(iTemp) )
    		{
    			ItemUtils.ejectSingleItemWithRandomOffset(worldObj, xCoord, yCoord, zCoord, BTWItems.mould.itemID, 0);
    		}    	
    	}
    	
    	clearMouldContents();
    }
}