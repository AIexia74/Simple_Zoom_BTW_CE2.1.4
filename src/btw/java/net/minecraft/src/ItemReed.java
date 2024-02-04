package net.minecraft.src;

import btw.item.items.PlaceAsBlockItem;

public class ItemReed extends PlaceAsBlockItem
{
    public ItemReed(int par1, Block par2Block)
    {
    	super( par1, par2Block.blockID );
    }

    public ItemReed( int iItemID, int iBlockID )
    {
    	super( iItemID, iBlockID );
    }

}
