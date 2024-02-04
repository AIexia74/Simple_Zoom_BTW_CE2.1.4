// FCMOD

package btw.block.blocks;

import btw.block.tileentity.CauldronTileEntity;
import btw.block.tileentity.CookingVesselTileEntity;
import btw.client.render.util.RenderUtils;
import btw.inventory.BTWContainers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class CauldronBlock extends CookingVesselBlock
{
    public CauldronBlock(int iBlockID )
    {
        super( iBlockID, Material.iron );
        
        setHardness( 3.5F );
        setResistance( 10F );
        
        setStepSound( Block.soundMetalFootstep );
        
        setUnlocalizedName( "fcBlockCauldron" );        
        
        setCreativeTab( CreativeTabs.tabRedstone );
    }

	@Override
    public TileEntity createNewTileEntity(World world )
    {
        return new CauldronTileEntity();
    }
    
    //------------- FCBlockCookingVessel -------------//

	@Override
	protected void validateFireUnderState(World world, int i, int j, int k)
	{
		// FCTODO: Move this to parent class
		
		if ( !world.isRemote )
		{
			TileEntity tileEnt = world.getBlockTileEntity( i, j, k );
			
			if ( tileEnt instanceof CauldronTileEntity)
			{
				CauldronTileEntity tileEntityCauldron =
	            	(CauldronTileEntity)tileEnt;
	            
	            tileEntityCauldron.validateFireUnderType();
			}
		}
	}
	
	@Override
	protected int getContainerID()
	{
		return BTWContainers.cauldronContainerID;
	}
	
    //------------- Class Specific Methods -------------//
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconContents;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        Icon sideIcon = register.registerIcon( "fcBlockCauldron_side" );
        
        blockIcon = sideIcon; // for hit effects

        iconInteriorBySideArray[0] = iconWideBandBySideArray[0] = iconCenterColumnBySideArray[0] = register.registerIcon("fcBlockCauldron_bottom");

        iconInteriorBySideArray[1] = iconCenterColumnBySideArray[1] = register.registerIcon("fcBlockCauldron_top");
        iconWideBandBySideArray[1] = register.registerIcon("fcBlockCauldronWideBand_top");

        iconInteriorBySideArray[2] = iconWideBandBySideArray[2] = iconCenterColumnBySideArray[2] = sideIcon;
        iconInteriorBySideArray[3] = iconWideBandBySideArray[3] = iconCenterColumnBySideArray[3] = sideIcon;
        iconInteriorBySideArray[4] = iconWideBandBySideArray[4] = iconCenterColumnBySideArray[4] = sideIcon;
        iconInteriorBySideArray[5] = iconWideBandBySideArray[5] = iconCenterColumnBySideArray[5] = sideIcon;

        iconContents = register.registerIcon("fcBlockCauldron_contents");
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
    	        
	        	float fHeightRatio = (float)iItemCount / 27.0F;
	        	
	            float fBottom = 9.0F / 16F;
	            
	            float fTop = fBottom + ( 1.0F / 16F ) + 
	            	( ( ( 1.0F - ( 2.0F / 16.0F ) ) - ( fBottom + ( 1.0F / 16F ) ) ) * fHeightRatio );
	
	            renderBlocks.setRenderBounds( 0.125F, fBottom, 0.125F, 
	    	    		0.875F, fTop, 0.875F );
	            
	            RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, i, j, k, iconContents);
    		}
    	}

    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	super.renderBlockAsItem(renderBlocks, iItemDamage, fBrightness);
    	
        // render contents
		
        final float fBottom = 9.0F / 16F;
        
        final float fTop = 10.0F / 16F;

        renderBlocks.setRenderBounds( 0.125F, fBottom, 0.125F, 
	    		0.875F, fTop, 0.875F );
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, iconContents);
    }
}