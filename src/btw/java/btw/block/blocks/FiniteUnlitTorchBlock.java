// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class FiniteUnlitTorchBlock extends TorchBlockUnlitBase
{
    public FiniteUnlitTorchBlock(int iBlockID)
    {
    	super( iBlockID );    	
    	
    	setUnlocalizedName( "fcBlockTorchFiniteIdle" );
    }
    
	@Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
		if ( getIsBurnedOut(iMetadata) )
		{
			return 0;
		}
		
    	return super.idDropped( iMetadata, rand, iFortuneModifier );
    }
	
	@Override
    public boolean getCanBeSetOnFireDirectly(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return !getIsBurnedOut(blockAccess, i, j, k);
    }
    
	@Override
    public boolean setOnFireDirectly(World world, int i, int j, int k)
    {
		if ( !getIsBurnedOut(world, i, j, k) )
		{
			if ( isRainingOnTorch(world, i, j, k) )
			{
	            world.playAuxSFX(BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );
	            
	            return true;
			}
			
			return super.setOnFireDirectly(world, i, j, k);
		}
		
		return false;
    }
	
	@Override
    public int getChanceOfFireSpreadingDirectlyTo(IBlockAccess blockAccess, int i, int j, int k)
    {
		if ( getIsBurnedOut(blockAccess, i, j, k) )
		{
			return 0; // same chance as leaves and other highly flammable objects
		}

		return super.getChanceOfFireSpreadingDirectlyTo(blockAccess, i, j, k);
    }
    
	@Override
	protected int getLitBlockID()
	{
		return BTWBlocks.finiteBurningTorch.blockID;
	}
	
    //------------- Class Specific Methods ------------//    
    
	public void setIsBurnedOut(World world, int i, int j, int k, boolean bBurnedOut)
	{
		int iMetadata = setIsBurnedOut(world.getBlockMetadata(i, j, k), bBurnedOut);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	static public int setIsBurnedOut(int iMetadata, boolean bIsBurnedOut)
	{
		if ( bIsBurnedOut )
		{
			iMetadata |= 8;
		}
		else
		{
			iMetadata &= (~8);
		}
		
		return iMetadata;
	}
    
	public boolean getIsBurnedOut(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getIsBurnedOut(blockAccess.getBlockMetadata(i, j, k));
	}
	
	static public boolean getIsBurnedOut(int iMetadata)
	{
		return ( iMetadata & 8 ) != 0;
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon burnedIcon;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		burnedIcon = register.registerIcon("fcBlockTorchFiniteIdle_burned");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if ( getIsBurnedOut(iMetadata) )
		{
			return burnedIcon;
		}
		
		return super.getIcon( iSide, iMetadata );
    }
}
    
