// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class TorchBlockBurningBase extends TorchBlockBase
{
    protected TorchBlockBurningBase(int iBlockID )
    {
    	super( iBlockID );
    }
    
    @Override
    public boolean getCanBlockLightItemOnFire(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return true;
    }    
    
    @Override
	public void onFluidFlowIntoBlock(World world, int i, int j, int k, BlockFluid newBlock)
	{
    	if ( newBlock.blockMaterial == Material.water )
    	{
	        world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );
	        
	        dropBlockAsItem_do( world, i, j, k, new ItemStack( BTWBlocks.infiniteUnlitTorch.blockID, 1, 0 ) );
    	}
    	else
    	{
    		super.onFluidFlowIntoBlock(world, i, j, k, newBlock);
    	}
	}

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random rand )
    {
    	Vec3 pos = getParticalPos(world, i, j, k);
    	
        world.spawnParticle( "smoke", pos.xCoord, pos.yCoord, pos.zCoord, 0D, 0D, 0D );
        world.spawnParticle( "flame", pos.xCoord, pos.yCoord, pos.zCoord, 0D, 0D, 0D );
    }

    @Environment(EnvType.CLIENT)
    protected Vec3 getParticalPos(World world, int i, int j, int k)
    {
    	Vec3 pos = Vec3.createVectorHelper( i + 0.5D, j + 0.92D, k + 0.5D );
    	
        int iOrientation = getOrientation(world, i, j, k);
        
        double dHorizontalOffset = 0.27D;

        if ( iOrientation == 1 )
        {
        	pos.xCoord -= dHorizontalOffset;
        }
        else if ( iOrientation == 2 )
        {
        	pos.xCoord += dHorizontalOffset;
        }
        else if ( iOrientation == 3 )
        {
        	pos.zCoord -= dHorizontalOffset;        	
        }
        else if ( iOrientation == 4 )
        {
        	pos.zCoord += dHorizontalOffset;
        }
        else
        {
        	pos.yCoord -= 0.22D;
        }
        
    	return 	pos;
    }
}
