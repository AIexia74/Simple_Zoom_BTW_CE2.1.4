// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.tileentity.CookingVesselTileEntity;
import btw.block.tileentity.CrucibleTileEntity;
import btw.client.render.util.RenderUtils;
import btw.inventory.BTWContainers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class CrucibleBlock extends CookingVesselBlock
{
    public CrucibleBlock(int iBlockID )
    {
        super( iBlockID, Material.glass );
        
        setHardness( 0.6F );
        setResistance( 3F );
        setPicksEffectiveOn(true);
        
        setStepSound( Block.soundGlassFootstep );
        
        setUnlocalizedName( "fcBlockCrucible" );
        
        setCreativeTab( CreativeTabs.tabRedstone );
    }
    
	@Override
    public TileEntity createNewTileEntity(World world )
    {
        return new CrucibleTileEntity();
    }

    //------------- FCBlockCookingVessel -------------//

	@Override
	protected void validateFireUnderState(World world, int i, int j, int k)
	{
		// FCTODO: Move this to parent class
		
		if ( !world.isRemote )
		{
			TileEntity tileEnt = world.getBlockTileEntity( i, j, k );
			
			if ( tileEnt instanceof CrucibleTileEntity)
			{
				CrucibleTileEntity tileEntityCrucible =
	            	(CrucibleTileEntity)tileEnt;
	            
	            tileEntityCrucible.validateFireUnderType();
			}
		}
	}
	
	@Override
	protected int getContainerID()
	{
		return BTWContainers.crucibleContainerID;
	}
	
    //------------- Class Specific Methods -------------//
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconContents;
    @Environment(EnvType.CLIENT)
    private Icon iconContentsHeated;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {	
        Icon sideIcon = register.registerIcon( "fcBlockCrucible_side" );
        
        blockIcon = sideIcon; // for hit effects

		iconInteriorBySideArray[0] = iconWideBandBySideArray[0] = iconCenterColumnBySideArray[0] = register.registerIcon("fcBlockCrucible_bottom");

		iconInteriorBySideArray[1] = iconCenterColumnBySideArray[1] = register.registerIcon("fcBlockCrucible_top");
		iconWideBandBySideArray[1] = register.registerIcon("fcBlockCrucibleWideBand_top");

		iconInteriorBySideArray[2] = iconWideBandBySideArray[2] = iconCenterColumnBySideArray[2] = sideIcon;
		iconInteriorBySideArray[3] = iconWideBandBySideArray[3] = iconCenterColumnBySideArray[3] = sideIcon;
		iconInteriorBySideArray[4] = iconWideBandBySideArray[4] = iconCenterColumnBySideArray[4] = sideIcon;
		iconInteriorBySideArray[5] = iconWideBandBySideArray[5] = iconCenterColumnBySideArray[5] = sideIcon;

		iconContents = register.registerIcon("fcBlockCrucible_contents");
		iconContentsHeated = register.registerIcon("lava");
    }
	
	//------------- Custom Renderer ------------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	super.renderBlock(renderBlocks, i, j, k);
    	
    	IBlockAccess blockAccess = renderBlocks.blockAccess;
    	
    	if (getFacing(blockAccess, i, j, k) == 1 )
    	{
	        // render contents if upright
	        
    		TileEntity tileEntity = blockAccess.getBlockTileEntity( i, j, k );
    		
    		if ( tileEntity instanceof CookingVesselTileEntity)
    		{    		
    	        CookingVesselTileEntity vesselEntity = (CookingVesselTileEntity)blockAccess.getBlockTileEntity( i, j, k );
    	        
    	        short iItemCount = vesselEntity.storageSlotsOccupied;
		        
		        if ( iItemCount > 0 )
		        {        
		        	float fHeightRatio = (float)iItemCount / 27.0F;
		        	
		            float fBottom = 3.0F / 16F;
		            
		            float fTop = fBottom + ( 1.0F / 16F ) + 
		            	( ( ( 1.0F - ( 2.0F / 16.0F ) ) - ( fBottom + ( 1.0F / 16F ) ) ) * fHeightRatio );
		
		            renderBlocks.setRenderBounds( 0.125F, fBottom, 0.125F, 
		    	    		0.875F, fTop, 0.875F );
		            
		            if ( blockAccess.getBlockId( i, j - 1, k ) == BTWBlocks.stokedFire.blockID )
		            {
		                RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, i, j, k, iconContentsHeated); // lava texture
		            }
		            else
		            {
		                RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, i, j, k, iconContents);
		            }            
		        }
    		}
    	}

    	return true;
    }
}