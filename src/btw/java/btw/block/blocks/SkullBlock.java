// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class SkullBlock extends BlockSkull
{
    public SkullBlock(int iBlockID)
    {
        super( iBlockID );
        
        setAxesEffectiveOn(true);
        
        setHardness( 1F );
        
        initBlockBounds(0.25D, 0D, 0.25D, 0.75D, 0.5D, 0.75D);
        
        setStepSound( soundStoneFootstep );
        
        setUnlocalizedName( "skull" );
    }
    
	@Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int i, int j, int k )
    {
    	// override to deprecate parent
    }
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
        int iFacing = blockAccess.getBlockMetadata(i, j, k) & 7;

        switch ( iFacing )
        {
            case 2:
            	
            	return AxisAlignedBB.getAABBPool().getAABB(         	
            		0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F );

            case 3:
            	
            	return AxisAlignedBB.getAABBPool().getAABB(         	
            		0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);

            case 4:
            	
            	return AxisAlignedBB.getAABBPool().getAABB(         	
            		0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);

            case 5:
            	
            	return AxisAlignedBB.getAABBPool().getAABB(         	
            		0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
                
            default:
            	
            	return AxisAlignedBB.getAABBPool().getAABB(         	
            		0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
        }
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k )
    {
    	return getBlockBoundsFromPoolBasedOnState(world, i, j, k).offset(i, j, k);
    }
    
    @Override
    public void makeWither( World world, int i, int j, int k, TileEntitySkull tileEntity )
    {
    	// wither is summoned through soul urns now, override to prevent vanilla method
    }
    
    public boolean isBlockSummoningBase(World world, int i, int j, int k)
    {
    	if ( world.getBlockId( i, j, k ) == BTWBlocks.aestheticOpaque.blockID )
    	{
    		int iSubtype = world.getBlockMetadata( i, j, k );
    		
    		if ( iSubtype == AestheticOpaqueBlock.SUBTYPE_BONE)
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    @Override
	public boolean isBlockRestingOnThatBelow(IBlockAccess blockAccess, int i, int j, int k)
	{
        int iMetadata = blockAccess.getBlockMetadata( i, j, k );
        
        return iMetadata == 1;
	}
	
    @Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
    @Override
	public boolean rotateAroundJAxis(World world, int i, int j, int k, boolean bReverse)
	{
        TileEntity tileEnt = world.getBlockTileEntity( i, j, k );
    	
        if ( tileEnt != null && tileEnt instanceof TileEntitySkull)
        {        	
        	TileEntitySkull skullEnt = (TileEntitySkull)tileEnt;
        	
        	int iSkullFacing = skullEnt.getSkullRotationServerSafe();
        	
        	if ( bReverse )
        	{
        		iSkullFacing += 4;
        		
        		if ( iSkullFacing > 15 )
        		{
        			iSkullFacing -= 16;
        		}
        	}
        	else
        	{
        		iSkullFacing -= 4;
        		
        		if ( iSkullFacing < 0 )
        		{
        			iSkullFacing += 16;
        		}
        	}
        	
        	skullEnt.setSkullRotation( iSkullFacing );
        	
	        world.markBlockForUpdate( i, j, k );
	        
	        return true;
        }
        
        return false;
	}
	
    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k)
    {
		// don't allow skulls to be retrieved because their code is a mess
		
    	return null;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	return false;
    }    
}
