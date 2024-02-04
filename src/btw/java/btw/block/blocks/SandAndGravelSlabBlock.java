// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class SandAndGravelSlabBlock extends FallingSlabBlock
{
	public static final int SUBTYPE_GRAVEL = 0;
	public static final int SUBTYPE_SAND = 1;
	
    public SandAndGravelSlabBlock(int iBlockID )
    {
        super( iBlockID, Material.sand );
        
        setHardness( 0.5F );
        setShovelsEffectiveOn(true);
        
        setStepSound( soundGravelFootstep );
        
        setUnlocalizedName( "fcBlockSlabFalling" );
        
		setCreativeTab( CreativeTabs.tabBlock );
    }

	@Override
	public int damageDropped( int iMetadata )
    {
		int iSubtype = this.getSubtypeFromMetadata(iMetadata);
		
		return iSubtype;		            		
    }
	
    @Override
    public float getMovementModifier(World world, int i, int j, int k)
    {
    	float fModifier = 1.0F;
        	
    	int iSubtype = getSubtype(world, i, j, k);
    	
		if (iSubtype == SUBTYPE_GRAVEL)
		{
			fModifier = 1.2F;
		}
		else if (iSubtype == SUBTYPE_SAND)
		{
			fModifier = 0.8F;
		}
    	
    	return fModifier;
    }
    
    @Override
    public StepSound getStepSound(World world, int i, int j, int k)
    {
    	int iSubtype = getSubtype(world, i, j, k);
    	
    	if (iSubtype == SUBTYPE_SAND)
    	{
    		return soundSandFootstep;
    	}
    	
    	return stepSound;
    }
    
	@Override
    public boolean attemptToCombineWithFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
	{
		if ( entity.blockID == blockID )
		{
			int iMetadata = world.getBlockMetadata( i, j, k );
			
			if ( iMetadata == entity.metadata )
			{
				world.setBlockWithNotify(i, j, k, getCombinedBlockID(iMetadata));
					
				return true;
			}			
		}
		
		return false;
	}
	
	@Override
	public int getCombinedBlockID(int iMetadata)
	{
		if (iMetadata == SUBTYPE_SAND)
		{
			return Block.sand.blockID;
		}
		else
		{
			return Block.gravel.blockID;
		}
	}

	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
    	int iIDToDrop = BTWItems.gravelPile.itemID;
    	
    	if (getSubtypeFromMetadata(iMetadata) == SUBTYPE_SAND)
    	{
    		iIDToDrop = BTWItems.sandPile.itemID;
    	}
    	
		dropItemsIndividually(world, i, j, k, iIDToDrop, 3, 0, fChanceOfDrop);
		
		return true;
	}
	
	@Override
    public boolean getIsUpsideDown(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return false;
    }
    
	@Override
    public boolean getIsUpsideDown(int iMetadata)
    {
    	return false;
    }
    
	@Override
    public void setIsUpsideDown(World world, int i, int j, int k, boolean bUpsideDown)
    {
    }
    
	@Override
    public int setIsUpsideDown(int iMetadata, boolean bUpsideDown)
	{
    	return iMetadata;    	
	}
    
    @Override
    public boolean canBePistonShoveled(World world, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public int getFilterableProperties(ItemStack stack)
    {
    	if (stack.getItemDamage() == SUBTYPE_GRAVEL)
    	{
    		return Item.FILTERABLE_SMALL;
    	}
    	
		return Item.FILTERABLE_FINE;
    }
    
    //------------- Class Specific Methods ------------//    
    
    public int getSubtype(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getSubtypeFromMetadata(blockAccess.getBlockMetadata(i, j, k));
    }
    
    public int getSubtypeFromMetadata(int iMetadata)
    {
    	return iMetadata;
    }
    
    public void setSubtype(World world, int i, int j, int k, int iSubtype)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k ) & 0; // clear old value
    	
		iMetadata |= iSubtype;
    	
    	world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconSand;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "gravel" );

		iconSand = register.registerIcon("sand");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		int iSubtype = getSubtypeFromMetadata(iMetadata);
		
		if (iSubtype == SUBTYPE_SAND)
		{
			return iconSand;
		}
		
		return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list )
    {
        list.add( new ItemStack(iBlockID, 1, SUBTYPE_GRAVEL));
        list.add( new ItemStack(iBlockID, 1, SUBTYPE_SAND));
    }
}
