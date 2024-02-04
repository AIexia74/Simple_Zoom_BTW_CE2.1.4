// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class StairsBlock extends StairsBlockBase
{
    protected final Block referenceBlock;
    protected final int referenceBlockMetadata;
    
    public StairsBlock(int iBlockID, Block referenceBlock, int iReferenceBlockMetadata)
    {
        super( iBlockID, referenceBlock.blockMaterial);

        this.referenceBlock = referenceBlock;
        referenceBlockMetadata = iReferenceBlockMetadata;
        
        setHardness( referenceBlock.blockHardness );
        setResistance( referenceBlock.blockResistance / 3.0F );
        
        setStepSound( referenceBlock.stepSound );        
    }

    @Override
    public void onBlockClicked(World world, int i, int j, int k, EntityPlayer player )
    {
        referenceBlock.onBlockClicked(world, i, j, k, player);
    }

    @Override
    public void onBlockDestroyedByPlayer( World world, int i, int j, int k, int iMetadata )
    {
        referenceBlock.onBlockDestroyedByPlayer(world, i, j, k, iMetadata);
    }

    @Override
    public float getExplosionResistance( Entity entity )
    {
        return referenceBlock.getExplosionResistance(entity);
    }

    @Override
    public int tickRate( World world )
    {
        return referenceBlock.tickRate(world);
    }

    @Override
    public void velocityToAddToEntity( World world, int i, int j, int k, Entity entity, Vec3 velocityVec )
    {
        referenceBlock.velocityToAddToEntity(world, i, j, k, entity, velocityVec);
    }

    @Override
    public boolean isCollidable()
    {
        return referenceBlock.isCollidable();
    }

    @Override
    public boolean canCollideCheck( int iMetadata, boolean flag )
    {
        return referenceBlock.canCollideCheck(iMetadata, flag);
    }

    @Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
        return this.referenceBlock.canPlaceBlockAt(world, i, j, k);
    }

    @Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
    	super.onBlockAdded( world, i, j, k );
        
        referenceBlock.onBlockAdded(world, i, j, k);
    }

    @Override
    public void breakBlock(World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
    	super.breakBlock( world, i, j, k, iBlockID, iMetadata );
    	
        referenceBlock.breakBlock(world, i, j, k, iBlockID, iMetadata);
    }

    @Override
    public void onEntityWalking( World world, int i, int j, int k, Entity entity )
    {
        referenceBlock.onEntityWalking(world, i, j, k, entity);
    }

    @Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
        referenceBlock.updateTick(world, i, j, k, rand);
    }

    @Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
        return referenceBlock.onBlockActivated(world, i, j, k, player, 0, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void onBlockDestroyedByExplosion( World world, int i, int j, int k, Explosion explosion )
    {
        referenceBlock.onBlockDestroyedByExplosion(world, i, j, k, explosion);
    }

    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random rand )
    {
        referenceBlock.randomDisplayTick(world, i, j, k, rand);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getMixedBrightnessForBlock( IBlockAccess blockAccess, int i, int j, int k )
    {
        return referenceBlock.getMixedBrightnessForBlock(blockAccess, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float getBlockBrightness( IBlockAccess blockAccess, int i, int j, int k )
    {
        return referenceBlock.getBlockBrightness(blockAccess, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getRenderBlockPass()
    {
        return referenceBlock.getRenderBlockPass();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
        return referenceBlock.getIcon(iSide, this.referenceBlockMetadata);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, int i, int j, int k )
    {
        return referenceBlock.getSelectedBoundingBoxFromPool(world, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register ) 
    {    	
    }
}