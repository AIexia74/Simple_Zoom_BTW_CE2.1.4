package btw.item.blockitems;

public class SlabWithMetadataBlockItem extends SlabBlockItem {
	
	    public SlabWithMetadataBlockItem(int iItemID )
	    {
	        super( iItemID );        
	    }
	    
	    @Override
	    public int getMetadata(int par1)
	    {
	        return par1;
	    }
}
