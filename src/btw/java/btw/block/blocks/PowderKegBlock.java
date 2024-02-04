// FCMOD

package btw.block.blocks;

import btw.block.util.Flammability;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class PowderKegBlock extends BlockTNT
{
	public PowderKegBlock(int iBlockID )
	{
		super( iBlockID );
		
		setHardness( 0F );
		
		setBuoyant();
		
		setFireProperties(Flammability.EXPLOSIVES);
		
		setStepSound( soundGrassFootstep );
		
		setUnlocalizedName( "tnt" );
	}
	
	@Override 
    public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion)
    {
		// override so that explosion param can be null
        if (!par1World.isRemote)
        {
        	EntityLiving explosionOwner = null;
        	
        	if ( par5Explosion != null )
        	{
        		explosionOwner = par5Explosion.func_94613_c();
        	}
        	
        	EntityTNTPrimed var6;
        	
        	if (explosionOwner == null) {
        		var6 = (EntityTNTPrimed) EntityList.createEntityOfType(EntityTNTPrimed.class, par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F) );
        	}
        	else {
        		var6 = (EntityTNTPrimed) EntityList.createEntityOfType(EntityTNTPrimed.class, par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), explosionOwner );
        	}
            
            var6.fuse = par1World.rand.nextInt(var6.fuse / 4) + var6.fuse / 8;
            par1World.spawnEntityInWorld(var6);
        }
    }
    
	@Override 
    public void onDestroyedByFire(World world, int i, int j, int k, int iFireAge, boolean bForcedFireSpread)
    {
		super.onDestroyedByFire(world, i, j, k, iFireAge, bForcedFireSpread);
		
        onBlockDestroyedByPlayer( world, i, j, k, 1 );
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBySideArray = new Icon[6];

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		Icon bottomIcon = register.registerIcon( "fcBlockPowderKeg_bottom" );
		
        blockIcon = bottomIcon; // for hit effects

        iconBySideArray[0] = bottomIcon;
        iconBySideArray[1] = register.registerIcon("fcBlockPowderKeg_top");
        
        Icon sideIcon = register.registerIcon( "fcBlockPowderKeg_side" );

        iconBySideArray[2] = sideIcon;
        iconBySideArray[3] = sideIcon;
        iconBySideArray[4] = sideIcon;
        iconBySideArray[5] = sideIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		return iconBySideArray[iSide];
    }	
}
