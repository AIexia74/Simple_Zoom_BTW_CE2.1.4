// FCMOD

package btw.block.blocks;

import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class SilverfishBlock extends BlockSilverfish
{

	private static final int GROW_FREQUENCY = 2;
	private static final int REPRODUCE_FREQUENCY = 10;
	

    protected final Block referenceBlock;
    protected final int referenceBlockMetadata;
    
	/**
	 * 
	 * @param iBlockID
	 * @param refBlock Block
	 * @param refBlockMetadata Block Metadata
	 */
    public SilverfishBlock(int iBlockID, Block refBlock, int refBlockMetadata )
    {
    	super( iBlockID );
    	
    	setHardness( 1.5F );
        referenceBlock = refBlock;
        referenceBlockMetadata = refBlockMetadata;

    	
    	setPicksEffectiveOn();
        setChiselsEffectiveOn();
    	
        setTickRandomly( true );
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {

    	if ( world.provider.dimensionId == 1 )
    	{

    		int metadata = world.getBlockMetadata( x, y, z );
    		if (metadata<6)
    		{
    			if(rand.nextInt(GROW_FREQUENCY) == 0)
    			{
    				metadata++; 
    				world.setBlockMetadataWithNotify(x, y, z, metadata, 3);
    			}
    		}

    		else
    		{
    			int fishInside = 1 +(metadata % 2);
    			for (int cycle=0; cycle<fishInside; cycle++)
    			{
    				if (rand.nextInt(REPRODUCE_FREQUENCY) == 0 )
    				{
    					//create fish 
    					EntitySilverfish silverfish = (EntitySilverfish) EntityList.createEntityOfType(EntitySilverfish.class, world);

    					//set target
    					int offset = rand.nextInt(6);
    					int targetX = x + Facing.offsetsXForSide[offset];
    					int targetY = y + Facing.offsetsYForSide[offset];
    					int targetZ = z + Facing.offsetsZForSide[offset];

    					//define target
    					int neighborblockid = world.getBlockId(targetX, targetY, targetZ);
    					int neighborblockmetadata = world.getBlockMetadata(targetX, targetY, targetZ);

    					//if can spawn a fish there
    					if ( neighborblockid == 0 || Block.blocksList[neighborblockid].isAirBlock() || Block.blocksList[neighborblockid].isGroundCover())
    					{
    						silverfish.setLocationAndAngles( (double)targetX + 0.5D, (double)targetY, (double)targetZ + 0.5D, 0.0F, 0.0F);
    						world.spawnEntityInWorld( silverfish );	
    						fishInside--;
    					}

    					// if can infest block
    					else if (Block.blocksList[neighborblockid].isBlockInfestable(silverfish, neighborblockmetadata))
    					{
    						Block.blocksList[neighborblockid].onInfested(world, silverfish,targetX, targetY, targetZ, neighborblockmetadata);
    						fishInside--;
    					}

    					//handle block eruption
    					if (metadata>13 || fishInside>1)
    					{
    						// block destroy FX
    						world.playAuxSFX( BTWEffectManager.DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID, x, y, z, blockID + ( metadata << 12 ) );
    						world.setBlockWithNotify( x, y, z, 0 );

    						//spawn fishes for eruption
    						int numsilverfish = 2;

    						if ( metadata%2 == 1 ) // add clogged one
    						{
    							numsilverfish++;
    						}

    						for ( int tempCount = 0; tempCount < numsilverfish; tempCount++ )
    						{
    							EntitySilverfish eruptionSilverfish = (EntitySilverfish) EntityList.createEntityOfType(EntitySilverfish.class, world);

    							eruptionSilverfish.setLocationAndAngles( (double)x + 0.5D, (double)y, (double)z + 0.5D, 0.0F, 0.0F);

    							world.spawnEntityInWorld( eruptionSilverfish );
    						}
    						dropBlockAsItemWithChance(world, x, y, z, metadata, 1, 0);

    						break;
    					}
    					int progress = 2;
    					if(metadata%2 == 1 )
    					{
    						progress = 1;
    					}
    					else if (fishInside>0)
    					{
    						progress = 3;
    					}
    					world.setBlockMetadataWithNotify(x, y, z, metadata+progress, 3);

    				}	
    			}
    		}
    	}
    }

    @Override
    protected ItemStack createStackedBlock( int iMetadata )
    {
        return new ItemStack( referenceBlock, 1, referenceBlockMetadata );
    }
    @Override
    public void dropBlockAsItemWithChance( World world, int x, int y, int z, int iMetadata, float fChance, int iFortuneModifier )
    {
		for (int rubbleCount = 0; rubbleCount <8; rubbleCount++)
		{
			// for each 2 metadata receive one clay ball instead of gravel
			if (rubbleCount*2 <= iMetadata) 
			{
				ItemUtils.dropSingleItemAsIfBlockHarvested(world, x, y, z, Item.clay.itemID, 0);
			}

			else 
			{
				ItemUtils.dropSingleItemAsIfBlockHarvested(world, x, y, z, BTWItems.gravelPile.itemID, 0);
			}
		}
		//one standard gravel pile
		ItemUtils.dropSingleItemAsIfBlockHarvested(world, x, y, z, BTWItems.gravelPile.itemID, 0);
    }
    @Override
	public boolean isBlockInfestedBy(EntityLiving entity)
	{
		return (entity instanceof EntitySilverfish);
	}
	
	//----------- Client Side Functionality -----------//
    @Environment(EnvType.CLIENT)
    public Icon[] crackIcons;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {

        crackIcons = new Icon[7];

        for ( int iTempIndex = 0; iTempIndex < 7; iTempIndex++ )
        {
        	crackIcons[iTempIndex] = register.registerIcon( "fcOverlayStoneRough_" + ( iTempIndex + 1 ) );
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		return referenceBlock.getIcon( iSide, referenceBlockMetadata );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random rand )
    {
        if ( rand.nextInt( 32 ) == 0 )
        {
            world.playSound( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, "mob.silverfish.step", 
            	rand.nextFloat() * 0.05F + 0.2F, rand.nextFloat() * 1.0F + 0.5F, false );
        }
    }
	
	//override regular silverfish to prevent triple creative inventory blocks
    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
		par3List.add(new ItemStack(par1, 1, 0));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult)
    {
    	if ( bFirstPassResult )
    	{
	    	IBlockAccess blockAccess = renderBlocks.blockAccess;
	    	//metadata-2 to stop cracks showing in first stages
	    	int progress = blockAccess.getBlockMetadata( i, j, k )-2;
	    	
	    	
	    	if ( progress > 0 )
	    	{
	    		
	    		int texindex = Math.floorDiv(progress, 2);
    			Icon overlayTexture = crackIcons[texindex];
    			
            	if ( overlayTexture != null )
            	{
            		renderBlockWithTexture(renderBlocks, i, j, k, overlayTexture);
            	}
	    	}
    	}
    }    

}
