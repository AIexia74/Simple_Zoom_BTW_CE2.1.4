// FCMOD

package btw.block.blocks;

import btw.block.tileentity.ArcaneVesselTileEntity;
import btw.client.fx.BTWEffectManager;
import btw.client.texture.ArcaneVesselXPTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class ArcaneVesselBlock extends VesselBlock
{
    public ArcaneVesselBlock(int iBlockID )
    {
        super( iBlockID, Material.iron );
        
        setHardness( 3.5F );
        setResistance( 10F );    	
    	setPicksEffectiveOn(true);
        
        setStepSound( Block.soundMetalFootstep );
        
        setCreativeTab(CreativeTabs.tabRedstone);
        
        setUnlocalizedName( "fcBlockArcaneVessel" );        
    }

	@Override
    public TileEntity createNewTileEntity(World world )
    {
        return new ArcaneVesselTileEntity();
    }
    
	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
        TileEntity tileEnt = world.getBlockTileEntity( i, j, k );
        
        if ( tileEnt != null && ( tileEnt instanceof ArcaneVesselTileEntity) )
        {
            ArcaneVesselTileEntity vesselEnt = (ArcaneVesselTileEntity)tileEnt;
            
            vesselEnt.ejectContentsOnBlockBreak();
        }

        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
    }

	@Override
    public void onEntityCollidedWithBlock( World world, int i, int j, int k, Entity entity )
    {
		// don't collect items on the client, as it's dependent on the state of the inventory
		
		if ( !world.isRemote )
		{
			if ( entity instanceof EntityXPOrb )
			{
				onEntityXPOrbCollidedWithBlock(world, i, j, k, (EntityXPOrb)entity);
				//Updates nearby comparator states
	            world.func_96440_m(i, j, k, this.blockID);
			}
		}		
    }
	
    private void onEntityXPOrbCollidedWithBlock(World world, int i, int j, int k, EntityXPOrb entityXPOrb)
    {
        if ( entityXPOrb.isDead )
        {
        	return;
        }
        
        if ( getMechanicallyPoweredFlag(world, i, j, k) )
        {
        	// tilted blocks can't collect
        	
        	return;
        }
        
        // check if item is within the collection zone
    	
        final float fVesselHeight = 1F;
        
    	AxisAlignedBB collectionZone = AxisAlignedBB.getAABBPool().getAABB( (float)i, (float)j + fVesselHeight, (float)k, 
				(float)(i + 1), (float)j + fVesselHeight + 0.05F, (float)(k + 1) );
    	
    	if ( entityXPOrb.boundingBox.intersectsWith( collectionZone ) )
    	{    	
        	boolean bSwallowed = false;
        	
        	TileEntity tileEnt = world.getBlockTileEntity( i, j, k );
        	
        	if ( tileEnt != null && ( tileEnt instanceof ArcaneVesselTileEntity) )
        	{
	            ArcaneVesselTileEntity vesselTileEnt = (ArcaneVesselTileEntity)tileEnt;
	
	        	if ( vesselTileEnt.attemptToSwallowXPOrb(world, i, j, k, entityXPOrb) )
	        	{
			        world.playAuxSFX( BTWEffectManager.ITEM_COLLECTION_POP_EFFECT_ID, i, j, k, 0 );
	        	}
        	}
    	}
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5)
    {
    	ArcaneVesselTileEntity tileEntity = (ArcaneVesselTileEntity) par1World.getBlockTileEntity(par2, par3, par4);
    	int currentXP = tileEntity.getContainedTotalExperience();
    	
    	float xpPercent = currentXP / (float) tileEntity.MAX_CONTAINED_EXPERIENCE;

        return MathHelper.floor_float(xpPercent * 14.0F) + (currentXP > 0 ? 1 : 0);
    }
    
    //------------- Class Specific Methods -------------//
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconContents;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        Icon sideIcon = register.registerIcon( "fcBlockVessel_side" );
        
        blockIcon = sideIcon; // for hit effects

        iconWideBandBySideArray[0] = iconCenterColumnBySideArray[0] = register.registerIcon("fcBlockVessel_bottom");

        iconCenterColumnBySideArray[1] = register.registerIcon("fcBlockVessel_top");
        iconWideBandBySideArray[1] = register.registerIcon("fcBlockVesselWideBand_top");

        iconWideBandBySideArray[2] = iconCenterColumnBySideArray[2] = sideIcon;
        iconWideBandBySideArray[3] = iconCenterColumnBySideArray[3] = sideIcon;
        iconWideBandBySideArray[4] = iconCenterColumnBySideArray[4] = sideIcon;
        iconWideBandBySideArray[5] = iconCenterColumnBySideArray[5] = sideIcon;

        iconInteriorBySideArray[0] = iconWideBandBySideArray[0];
        iconInteriorBySideArray[1] = iconWideBandBySideArray[0];
        iconInteriorBySideArray[2] = iconWideBandBySideArray[0];
        iconInteriorBySideArray[3] = iconWideBandBySideArray[0];
        iconInteriorBySideArray[4] = iconWideBandBySideArray[0];
        iconInteriorBySideArray[5] = iconWideBandBySideArray[0];

        iconContents = register.registerIcon("fcBlockVessel_xp", new ArcaneVesselXPTexture("fcBlockVessel_xp" ));
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
    		
    		if ( tileEntity instanceof ArcaneVesselTileEntity)
    		{
    			ArcaneVesselTileEntity vesselEntity = (ArcaneVesselTileEntity)tileEntity;
    	        
    	        int iContainedExperience = vesselEntity.getVisualExperienceLevel();
    	        
    	        if ( iContainedExperience > 0 )
    	        {
		        	float fHeightRatio = (float)iContainedExperience / (float) ArcaneVesselTileEntity.MAX_VISUAL_EXPERIENCE_LEVEL;
		        	
		            float fBottom = 3.0F / 16F;
		            
		            float fTop = fBottom + ( 1.0F / 16F ) + 
		            	( ( ( 1.0F - ( 2.0F / 16.0F ) ) - ( fBottom + ( 1.0F / 16F ) ) ) * fHeightRatio );
		
		            renderBlocks.setRenderBounds( 0.125F, fBottom, 0.125F, 
	    	    		0.875F, fTop, 0.875F );
	            
		            Tessellator tesselator = Tessellator.instance;
		            
		            tesselator.setBrightness( 240 );
		            
		            renderBlocks.renderFaceYPos(this, (double)i, (double)j, (double)k, iconContents);
    	        }
    		}
    	}
    	
    	return true;
    }
}