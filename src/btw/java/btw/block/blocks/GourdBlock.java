// FCMOD

package btw.block.blocks;

import btw.client.fx.BTWEffectManager;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public abstract class GourdBlock extends FallingBlock
{
	private static final double ARROW_SPEED_SQUARED_TO_EXPLODE = 1.10D;
	
    protected GourdBlock(int iBlockID )
    {
        super( iBlockID, Material.pumpkin );
        
        setAxesEffectiveOn(true);
        setBuoyant();
        
        setTickRandomly( true );        
        
        setCreativeTab( CreativeTabs.tabBlock );
    }
    
    @Override
    public void updateTick(World world, int i, int j, int k, Random rand )
    {
    	super.updateTick( world, i, j, k, rand );
    	
    	// necessary to check blockID because super.updateTick may cause it to fall
    	
        if ( world.getBlockId( i, j, k ) == blockID )
        {
        	validateConnectionState(world, i, j, k);
        }
    }
    
    @Override
    public int getMobilityFlag()
    {
    	// allow gourds to be pushed by pistons
    	
    	return 0;
    }
    
    @Override
    public void onArrowImpact(World world, int i, int j, int k, EntityArrow arrow)
    {
    	if ( !world.isRemote )
    	{
    		double dArrowSpeedSq = arrow.motionX * arrow.motionX + arrow.motionY * arrow.motionY + arrow.motionZ * arrow.motionZ;
    		
    		if (dArrowSpeedSq >= ARROW_SPEED_SQUARED_TO_EXPLODE)
    		{
	    		world.setBlockWithNotify( i, j, k, 0 );
	    		
	    		explode(world, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D);
    		}
    		else
    		{    		
    			world.playAuxSFX( BTWEffectManager.GOURD_IMPACT_SOUND_EFFECT_ID, i, j, k, 0 );
    		}
    	}
    }
    
    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
    {
    	// there's no improper tool to harvest gourds, but this also happens if the block is deleted after falling due to sitting on an
    	// improper block
    	
        world.playAuxSFX(auxFXIDOnExplode(), i, j, k, 0);
    }
    
    @Override
    public boolean onFinishedFalling(EntityFallingSand entity, float fFallDistance)
    {
    	entity.metadata = 0; // reset stem connection
    	
    	if ( !entity.worldObj.isRemote )
    	{
	        int i = MathHelper.floor_double( entity.posX );
	        int j = MathHelper.floor_double( entity.posY );
	        int k = MathHelper.floor_double( entity.posZ );
	        
	        int iFallDistance = MathHelper.ceiling_float_int( entity.fallDistance - 5.0F );
	        
	    	if ( iFallDistance >= 0 )
	    	{	    		
	    		damageCollidingEntitiesOnFall(entity, fFallDistance);
	    		
	    		if ( !Material.water.equals( entity.worldObj.getBlockMaterial( i, j, k ) ) )
	    		{	    			
		    		if ( entity.rand.nextInt( 10 ) < iFallDistance )
		    		{
		    			explode(entity.worldObj, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D);
		    			
		    			return false;
		    		}
	    		}
	    	}
	    	
			entity.worldObj.playAuxSFX( BTWEffectManager.GOURD_IMPACT_SOUND_EFFECT_ID, i, j, k, 0 );
    	}
        
    	return true;
    }    
    
    @Override
    public int adjustMetadataForPistonMove(int iMetadata)
    {
    	// flag pushed pumpkins as not attached to a stem
    	
    	return iMetadata = 0;
    }
    
    @Override
	public boolean isBlockAttachedToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
	{
        int iMetadata = blockAccess.getBlockMetadata( i, j, k );
        
        return iMetadata >= 2 && iFacing == iMetadata;
	}
	
    @Override
	public void attachToFacing(World world, int i, int j, int k, int iFacing)
	{
    	if ( iFacing >= 2 && iFacing <= 5 )
    	{
    		world.setBlockMetadataWithClient( i, j, k, iFacing );
    	}
	}	
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
		super.onNeighborBlockChange( world, i, j, k, iBlockID );
		
		validateConnectionState(world, i, j, k);
    }
	
    @Override
    public boolean canBeGrazedOn(IBlockAccess access, int i, int j, int k, EntityAnimal animal)
    {
		return animal.canGrazeOnRoughVegetation();
    }

    //------------- Class Specific Methods ------------//
    
	abstract protected Item itemToDropOnExplode();
	
	abstract protected int itemCountToDropOnExplode();
	
	abstract protected int auxFXIDOnExplode();
	
	abstract protected DamageSource getFallDamageSource();
	
    private void explode(World world, double posX, double posY, double posZ)
    {
    	Item itemToDrop = itemToDropOnExplode();
    	
    	if ( itemToDrop != null )
    	{
	        for (int iTempCount = 0; iTempCount < itemCountToDropOnExplode(); iTempCount++)
	        {
	    		ItemStack itemStack = new ItemStack( itemToDrop, 1, 0 );
	
	            EntityItem entityItem = (EntityItem) EntityList.createEntityOfType(EntityItem.class, world, posX, posY+0.5, posZ, itemStack );
	            
	            entityItem.motionX = ( world.rand.nextDouble() - 0.5D ) * 0.5D;
	            entityItem.motionY = 0.2D + world.rand.nextDouble() * 0.3D;
	            entityItem.motionZ = ( world.rand.nextDouble() - 0.5D ) * 0.5D;
	            
	            entityItem.delayBeforeCanPickup = 10;
	            
	            world.spawnEntityInWorld( entityItem );
	        }
    	}
    	
    	notifyNearbyAnimalsFinishedFalling(world, MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));
        
        world.playAuxSFX(auxFXIDOnExplode(),
						 MathHelper.floor_double( posX ), MathHelper.floor_double( posY ), MathHelper.floor_double( posZ ),
						 0 );
    }
    
    private void damageCollidingEntitiesOnFall(EntityFallingSand entity, float fFallDistance)
    {
        int var2 = MathHelper.ceiling_float_int( fFallDistance - 1.0F );

        if (var2 > 0)
        {
            ArrayList collisionList = new ArrayList( entity.worldObj.getEntitiesWithinAABBExcludingEntity( entity, entity.boundingBox ) );
            
            DamageSource source = getFallDamageSource();
            
            Iterator iterator = collisionList.iterator();

            while ( iterator.hasNext() )
            {
                Entity tempEntity = (Entity)iterator.next();
                
                tempEntity.attackEntityFrom( source, 1 );
            }

        }
    }
    
    protected void validateConnectionState(World world, int i, int j, int k)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k );
    	
    	if ( iMetadata > 0 )
    	{
            BlockPos targetPos = new BlockPos( i, j, k );

            if ( iMetadata >= 2 && iMetadata <= 5 )
            {
	            targetPos.addFacingAsOffset(iMetadata);
	            
	            int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
	            
	            // FCTODO: Hacky
	            if ( Block.blocksList[iTargetBlockID] == null || 
	            	!( Block.blocksList[iTargetBlockID] instanceof StemBlock) ||
					 world.getBlockMetadata(targetPos.x, targetPos.y, targetPos.z) != 15 )
	            {            	
	                world.setBlockMetadata( i, j, k, 0 ); // no notify                
	            }
            }
            else
            {
                // There may be old gourds laying about that have invalid metadata
                
                world.setBlockMetadata( i, j, k, 0 ); // no notify                
            }
    	}
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    protected Icon iconTop;

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	if ( iSide == 1 || iSide == 0 )
    	{
    		return iconTop;
    	}
    	
    	return blockIcon;
    }
}