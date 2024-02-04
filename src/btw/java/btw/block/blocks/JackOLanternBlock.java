// FCMOD

package btw.block.blocks;

import btw.client.fx.BTWEffectManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class JackOLanternBlock extends Block
{
    public JackOLanternBlock(int iBlockID)
    {
    	super( iBlockID, Material.pumpkin );
    	
        setTickRandomly(true);
        
        setHardness(1.0F);
        setBuoyant();
        
        setStepSound(soundWoodFootstep);
        setLightValue(1.0F);
        setUnlocalizedName("litpumpkin");
        
        setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving placingEntity, ItemStack itemStack )
    {
        int iFacing = MathHelper.floor_double( ( (double)placingEntity.rotationYaw * 4D / 360D ) + 2.5D ) & 3;
        
        int iMetadata = ( world.getBlockMetadata( i, j, k ) ) & (~3);
        
        iMetadata |= iFacing;
        
        world.SetBlockMetadataWithNotify( i, j, k, iMetadata, 2 );
    }

	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        super.onBlockAdded( world, i, j, k );

        int iMetadata = ( world.getBlockMetadata( i, j, k ) ) | 8; // set 4th bit to indicate block is player placed
        
        world.SetBlockMetadataWithNotify( i, j, k, iMetadata, 2 );
        
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    }
	
	@Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
		checkForExtinguish(world, i, j, k);
    }
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
		checkForExtinguish(world, i, j, k);
    }	
	
    @Override
    public boolean getCanBlockLightItemOnFire(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		int iDirection = iMetadata & 3;
		
		if ( bReverse )
		{
			iDirection++;
			
			if ( iDirection > 3 )
			{
				iDirection = 0;
			}
		}
		else
		{
			iDirection--;
			
			if ( iDirection < 0 )
			{
				iDirection = 3;
			}
		}		
		
		return ( iMetadata & (~3) ) | iDirection;
	}
    
    @Override
    public boolean canBeGrazedOn(IBlockAccess blockAccess, int i, int j, int k,
								 EntityAnimal animal)
    {
		return animal.canGrazeOnRoughVegetation();
    }

    //------------- Class Specific Methods ------------//
    
	private void checkForExtinguish(World world, int i, int j, int k)
	{
		int iMetadata = world.getBlockMetadata( i, j, k );
		
		if ( ( iMetadata & 8 ) != 0 )
		{
			if ( hasWaterToSidesOrTop(world, i, j, k) )
			{
				extinguishLantern(world, i, j, k);
			}
		}
	}
	
	private void extinguishLantern(World world, int i, int j, int k)
	{
		int iMetadata = world.getBlockMetadata( i, j, k );
		
		world.setBlockAndMetadataWithNotify( i, j, k, Block.pumpkin.blockID, iMetadata & 3 );
		
        world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconTop;
    @Environment(EnvType.CLIENT)
    private Icon iconFront;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
		iconFront = par1IconRegister.registerIcon("pumpkin_jack");
		iconTop = par1IconRegister.registerIcon("pumpkin_top");
        blockIcon = par1IconRegister.registerIcon( "pumpkin_side" );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	if ( iSide == 1 || iSide == 0 )
    	{
    		return iconTop;
    	}
    	else
    	{
        	int iFacing = iMetadata & 3;
        	
    		if ( ( iFacing == 2 && iSide == 2 ) || 
    			( iFacing == 3 && iSide == 5 ) || 
    			( iFacing == 0 && iSide == 3 ) || 
    			( iFacing == 1 && iSide == 4 ) )
    		{
    			return iconFront;
    		}
    	}
    	
    	return blockIcon;
    	
    }
}
