// FCMOD

package btw.block.blocks;

import btw.block.MechanicalBlock;
import btw.block.tileentity.TurntableTileEntity;
import btw.client.fx.BTWEffectManager;
import btw.client.render.util.RenderUtils;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import btw.block.util.MechPowerUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class TurntableBlock extends BlockContainer implements MechanicalBlock {
	private static final int TURNTABLE_TICK_RATE = 10;
	
	public TurntableBlock(int blockID) {
        super(blockID, Material.rock);

        setHardness(2F);
        setStepSound(soundStoneFootstep);
        
        setUnlocalizedName("fcBlockTurntable");
        
        setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	@Override
    public int tickRate(World world) {
    	return TURNTABLE_TICK_RATE;
    }
    
	@Override
    public TileEntity createNewTileEntity(World world) {
        return new TurntableTileEntity();
    }

	@Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        
    	world.scheduleBlockUpdate(x, y, z, blockID, tickRate(world));
    }

	@Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
		if (!world.isUpdatePendingThisTickForBlock(x, y, z, this.blockID)) {
			world.scheduleBlockUpdate(x, y, z, this.blockID, tickRate(world));
		}
    }
    
	@Override
    public void updateTick(World world, int x, int y, int z, Random random) {
    	boolean receivingMechanicalPower = isInputtingMechanicalPower(world, x, y, z);
    	boolean isMechanicalOn = isBlockMechanicalOn(world, x, y, z);
    	
    	if (isMechanicalOn != receivingMechanicalPower) {
	        emitTurntableParticles(world, x, y, z, random);
	        
    		setBlockMechanicalOn(world, x, y, z, receivingMechanicalPower);
    		
	        world.markBlockForUpdate(x, y, z);
    	}
    	
    	boolean receivingRedstonePower = world.isBlockGettingPowered(x, y, z);
    	boolean isRedstoneOn = isBlockRedstoneOn(world, x, y, z);
    	
    	if (isRedstoneOn != receivingRedstonePower) {
    		setBlockRedstoneOn(world, x, y, z, receivingRedstonePower);
    	}
    }
    
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float hitX, float hitY, float hitZ) {
    	ItemStack playerEquippedItem = player.getCurrentEquippedItem();
    	
    	if (playerEquippedItem != null) {
    		return false;
    	}
    	
        if (!world.isRemote) {
        	int switchSetting = getSwitchSetting(world, x, y, z);
        	switchSetting++;
        	
        	if (switchSetting > 3) {
        		switchSetting = 0;
        	}
        	
        	setSwitchSetting(world, x, y, z, switchSetting);
        
	        world.markBlockForUpdate(x, y, z);
	        world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);

	        // click sound
            world.playAuxSFX(1001, x, y, z, 0);
        }
        
        return true;
    }

	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
		return false;
	}
	
	@Override
	public boolean canTransmitRotationHorizontallyOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
		return false;
	}
	
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
		return false;
	}
	
    //------------- FCIBlockMechanical -------------//
    
	@Override
    public boolean canOutputMechanicalPower() {
    	return false;
    }

	@Override
    public boolean canInputMechanicalPower() {
    	return true;
    }

	@Override
    public boolean isInputtingMechanicalPower(World world, int x, int y, int z) {
    	// check for powered axles below
    	return MechPowerUtils.isBlockPoweredByAxleToSide(world, x, y, z, 0);
    }    

	@Override
    public boolean isOutputtingMechanicalPower(World world, int i, int j, int k) {
    	return false;
    }
    
	@Override
	public boolean canInputAxlePowerToFacing(World world, int x, int y, int z, int facing) {
		return facing == 0;
	}
	
	@Override
	public void overpower(World world, int x, int y, int z) {
		breakTurntable(world, x, y, z);
	}
	
    //------------- Class Specific Methods ------------//
    
    public boolean isBlockMechanicalOn(IBlockAccess blockAccess, int x, int y, int z)
    {
    	return (blockAccess.getBlockMetadata(x, y, z) & 1) > 0;    
	}
    
    public void setBlockMechanicalOn(World world, int x, int y, int z, boolean isOn) {
    	int metaData = world.getBlockMetadata(x, y, z) & (~1); // filter out old on state
    	
    	if (isOn) {
    		metaData |= 1;
    	}
    	
        world.setBlockMetadataWithNotify(x, y, z, metaData);
    }
    
    public boolean isBlockRedstoneOn(IBlockAccess blockAccess, int x, int y, int z) {
    	return (blockAccess.getBlockMetadata(x, y, z) & 2) > 0;    
	}
    
    public void setBlockRedstoneOn(World world, int x, int y, int z, boolean isOn) {
    	int metaData = world.getBlockMetadata(x, y, z) & (~2); // filter out old on state
    	
    	if (isOn) {
    		metaData |= 2;
    	}
    	
        world.setBlockMetadataWithNotify(x, y, z, metaData);
    }
    
    public int getSwitchSetting(IBlockAccess iBlockAccess, int x, int y, int z) {
    	return (iBlockAccess.getBlockMetadata(x, y, z) & 12) >> 2;    
    }
    
    public void setSwitchSetting(World world, int x, int y, int z, int setting) {
    	if (setting >= 4 || setting < 0) {
    		setting = 0;
    	}
    	
    	int metadata = world.getBlockMetadata(x, y, z) & (~12); // filter out old on state
    	
    	metadata |= (setting << 2);
    	
        world.setBlockMetadataWithNotify(x, y, z, metadata);
    }
    
    public void emitTurntableParticles(World world, int x, int y, int z, Random random) {
        for (int i = 0; i < 5; i++) {
            float smokeX = (float) x + random.nextFloat();
            float smokeY = (float) y + random.nextFloat() * 0.5F + 1.0F;
            float smokeZ = (float) z + random.nextFloat();
            
            world.spawnParticle("smoke", smokeX, smokeY, smokeZ, 0.0D, 0.0D, 0.0D);
        }
    }
    
	private void breakTurntable(World world, int x, int y, int z) {
		ItemUtils.ejectSingleItemWithRandomOffset(world, x, y, z, Item.redstone.itemID, 0);
		
    	dropItemsIndividually(world, x, y, z, BTWItems.stone.itemID, 16, 0, 0.75F);
		
		// drop wood siding
		for (int i = 0; i < 2; i++) {
			ItemUtils.ejectSingleItemWithRandomOffset(world, x, y, z, BTWItems.woodSidingStubID, 0);
		}
		
        world.playAuxSFX(BTWEffectManager.MECHANICAL_DEVICE_EXPLODE_EFFECT_ID, x, y, z, 0);
        
		world.setBlockWithNotify(x, y, z, 0);
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBySideArray = new Icon[6];

    @Environment(EnvType.CLIENT)
    private Icon iconSwitch;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon("stone"); // for hit effects
        
        iconBySideArray[0] = register.registerIcon("fcBlockTurntable_bottom");
        iconBySideArray[1] = register.registerIcon("fcBlockTurntable_top");
        
        Icon sideIcon = register.registerIcon("fcBlockTurntable_side");
        
        iconBySideArray[2] = sideIcon;
        iconBySideArray[3] = sideIcon;
        iconBySideArray[4] = sideIcon;
        iconBySideArray[5] = sideIcon;
        
        iconSwitch = register.registerIcon("fcBlockTurntable_switch");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int iSide, int iMetadata) {
		return iconBySideArray[iSide];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
    	if (isBlockMechanicalOn(world, x, y, z)) {
    		emitTurntableParticles(world, x, y, z, random);
    	}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int neighborX, int neighborY, int neighborZ, int side) {
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int x, int y, int z) {
    	super.renderBlock(renderBlocks, x, y, z);
    	
    	// render the switches
    	int switchSetting = getSwitchSetting(renderBlocks.blockAccess, x, y, z);
    	
    	float switchOffset = (4.0F / 16.0F) + ((float)switchSetting * (2.0F / 16.0F)); 
        renderBlocks.setRenderBounds(switchOffset, (5.0F / 16.0F), (1.0F / 16.0F), 
        		switchOffset + (2.0F / 16.0F), (7.0F / 16.0F), 1.0F + (1.0F / 16.0F));
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, x, y, z, iconSwitch);
        
        renderBlocks.setRenderBounds(1.0F - (switchOffset + (2.0F / 16.0F)), (5.0F / 16.0F), 0.0F - (1.0F / 16.0F), 
        		1.0F - switchOffset, (7.0F / 16.0F), 1.0F - (1.0F / 16.0F));
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, x, y, z, iconSwitch);
        
        renderBlocks.setRenderBounds((1.0F / 16.0F), (5.0F / 16.0F), 1.0F - (switchOffset + (2.0F / 16.0F)), 
        		1.0F + (1.0F / 16.0F), (7.0F / 16.0F), 1.0F - switchOffset);
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, x, y, z, iconSwitch);
        
        renderBlocks.setRenderBounds(0.0F - (1.0F / 16.0F), (5.0F / 16.0F), switchOffset, 
        		1.0F - (1.0F / 16.0F), (7.0F / 16.0F), switchOffset + (2.0F / 16.0F));
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, x, y, z, iconSwitch);
        
        return true;
    }    
}