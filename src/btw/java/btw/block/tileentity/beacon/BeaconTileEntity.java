// FCMOD

package btw.block.tileentity.beacon;

import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.block.blocks.AestheticOpaqueEarthBlock;
import net.minecraft.src.*;

import java.util.*;

public class BeaconTileEntity extends TileEntityBeacon {
	private static final long TICKS_UPDATES = 80L;
	
	private static ArrayList<BeaconEffectDescriptor>[] beaconEffectsByBlockID = new ArrayList[4096];
	
	private static Random rand = new Random();
	
	private long updateOffset;
	
	/**
	 * can use this for slow ticking things, like refreshing potion effects
	 */
	public boolean updatedPowerState = true;
	
	public boolean playerRespawnedAtBeacon = false;
	
	public int belowBlockID = -1;
	public int belowMetadata = -1;
	
	public static final BeaconEffect HASTE_EFFECT = new PotionBeaconEffect(Potion.digSpeed.getId(), true);
	public static final BeaconEffect FORTUNE_EFFECT = new PotionBeaconEffect(BTWMod.potionFortune.getId(), true);
	public static final BeaconEffect TRUE_SIGHT_EFFECT = new PotionBeaconEffect(BTWMod.potionTrueSight.getId(), false);
	public static final BeaconEffect NIGHT_VISION_EFFECT = new PotionBeaconEffect(Potion.nightVision.getId(), false);
	public static final BeaconEffect FIRE_RESIST_EFFECT = new PotionBeaconEffect(Potion.fireResistance.getId(), false);
	public static final BeaconEffect MAGNETIC_POLE_EFFECT = new MagneticPointBeaconEffect();
	public static final BeaconEffect SPAWN_ANCHOR_EFFECT = new SpawnAnchorBeaconEffect();
	public static final BeaconEffect DECORATIVE_EFFECT = new DecorativeBeaconEffect();
	public static final BeaconEffect NAUSEA_EFFECT = new NauseaBeaconEffect();
	public static final BeaconEffect ENDER_ANTENNA_EFFECT = new EnderAntennaBeaconEffect();
	public static final AmbientBeaconEffect LOOTING_EFFECT = new LootingBeaconEffect();
	public static final CompanionBeaconEffect COMPANION_EFFECT = new CompanionBeaconEffect();
	public static final AmbientBeaconEffect JUNGLE_SPIDER_REPELLENT = new AmbientBeaconEffect("JungleRepel");
	
	public BeaconEffect beaconEffect;
	
	public BeaconTileEntity() {
		updateOffset = rand.nextInt((int) TICKS_UPDATES);
	}
	
	
	@Override
	public void updateEntity() {
		if ((worldObj.getTotalWorldTime() + updateOffset) % TICKS_UPDATES == 0L) {
			updatePowerState();
			updatedPowerState = true;
		}
		
		if (beaconEffect != null && isOn() && getLevels() > 0) {
			beaconEffect.onUpdate(this);
		}
		
		if (updatedPowerState) {
			updatedPowerState = false;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		
		this.belowBlockID = par1NBTTagCompound.getInteger("belowBlockID");
		this.belowMetadata = par1NBTTagCompound.getInteger("belowMetadata");
		this.isBeaconActive = par1NBTTagCompound.getBoolean("isBeaconActive");

		if (isBeaconActive) {
			BeaconEffectDescriptor effectDescriptor = getEffectDescriptor(belowBlockID, belowMetadata);
			if (effectDescriptor != null) {
				if (effectDescriptor.EFFECT != null && effectDescriptor.EFFECT != beaconEffect) {
					beaconEffect = effectDescriptor.EFFECT;
				}
			}
		}
	}
	
	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setInteger("belowBlockID", this.belowBlockID);
		par1NBTTagCompound.setInteger("belowMetadata", this.belowMetadata);
		par1NBTTagCompound.setBoolean("isBeaconActive", this.isBeaconActive);


	}
	
	
	//------------- Class Specific Methods ------------//
	
	private boolean isPyramidLevelValid(int level, int blockIDToCheck, int metadataToCheck) {
		int j = yCoord - level;
		
		if (j < 0) {
			return false;
		}
		
		for (int i = xCoord - level; i <= xCoord + level; ++i) {
			for (int k = zCoord - level; k <= zCoord + level; ++k) {
				int blockID = worldObj.getBlockId(i, j, k);
				
				if (blockID != blockIDToCheck || (metadataToCheck != -1 && worldObj.getBlockMetadata(i, j, k) != metadataToCheck)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void updatePowerState() {
		// Don't update if part of the pyramid might be unloaded
		if (!worldObj.checkChunksExist(xCoord - 4, yCoord, zCoord - 4, xCoord + 4, yCoord, zCoord + 4)) {
			return;
		}
		
		updateBelowBlockType();
		
		if (!canBeaconSeeSky()) {
			setPowerState(false, 0, null);
			return;
		}
		
		int level = 0;
		
		BeaconEffectDescriptor effectDescriptor = getEffectDescriptor(belowBlockID, belowMetadata);
		
		if (effectDescriptor != null) {
			int metadataToCheck = effectDescriptor.BLOCK_METADATA;
			
			for (int i = 1; i <= 4; i++) {
				if (isPyramidLevelValid(i, belowBlockID, metadataToCheck)) {
					level = i;
				}
				else {
					break;
				}
			}
			
			if (effectDescriptor.EFFECT != null) {
				if (level > 0) {
					setPowerState(true, level, effectDescriptor.EFFECT);
				}
				else {
					setPowerState(false, 0, null);
				}
			}
		}
	}
	
	private void updateBelowBlockType() {
		belowBlockID = worldObj.getBlockId(xCoord, yCoord - 1, zCoord);
		if (belowBlockID > 0) {
			belowMetadata = worldObj.getBlockMetadata(xCoord, yCoord - 1, zCoord);
			
		}
	}
	
	public void setPowerState(boolean isOn, int newPowerLevel, BeaconEffect effectClass) {
		int oldPowerLevel = getLevels();
		
		BeaconEffect oldEffect = beaconEffect;
		beaconEffect = effectClass;
		
		if(beaconEffect != null) {
			if (newPowerLevel != oldPowerLevel) {
				effectClass.onPowerChange(newPowerLevel, oldPowerLevel, this);
			}
			
			if (oldPowerLevel <= 0 && newPowerLevel > 0) {
				effectClass.onPowerOn(this);
			}
		}
		else if(oldEffect != null){
			if (newPowerLevel != oldPowerLevel) {
				oldEffect.onPowerChange(newPowerLevel, oldPowerLevel, this);
			}
			
			if (newPowerLevel <= 0 && oldPowerLevel > 0) {
				oldEffect.onPowerOff(this);
			}
		}
		
		setIsOn(isOn);
		setLevelsServerSafe(newPowerLevel);
	}
	
	private boolean canBeaconSeeSky() {
		if (worldObj.provider.dimensionId != -1) {
			return worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord);
		}
		else {
			if (!worldObj.isAirBlock(xCoord, yCoord + 1, zCoord)) {
				// the beacon needs at least one empty space above it to activate
				
				return false;
			}
			
			// if we're in the nether, check if we can see bedrock above
			
			for (int iTempY = yCoord + 2; iTempY < 256; iTempY++) {
				if (!worldObj.isAirBlock(xCoord, iTempY, zCoord)) {
					int iBlockID = worldObj.getBlockId(xCoord, iTempY, zCoord);
					
					return iBlockID == Block.bedrock.blockID;
				}
			}
			
			return true;
		}
	}
	
	private BeaconEffectDescriptor getEffectDescriptor(int blockID, int metadata) {
		ArrayList<BeaconEffectDescriptor> descriptorArray = beaconEffectsByBlockID[blockID];
		
		for (int i = 0; i < descriptorArray.size(); i++) {
			BeaconEffectDescriptor tempDescriptor = descriptorArray.get(i);
			
			if (tempDescriptor.BLOCK_METADATA == -1 || tempDescriptor.BLOCK_METADATA == metadata) {
				return tempDescriptor;
			}
		}
		
		return null;
	}
	
	public BeaconEffect getActiveEffect() {
		return this.beaconEffect;
	}
	
	public static void addBeaconEffect(int iBlockID, BeaconEffect effectClass) {
		addBeaconEffect(iBlockID, -1, effectClass);
	}
	public static void addBeaconEffect(int iBlockID, int iBlockMetadata, BeaconEffect effectClass) {
		beaconEffectsByBlockID[iBlockID].add(new BeaconEffectDescriptor(iBlockMetadata, effectClass));
	}
	
	public static void initializeEffectsByBlockID() {
		for (int i = 0; i < 4096; i++) {
			beaconEffectsByBlockID[i] = new ArrayList<BeaconEffectDescriptor>();
		}
		
		addBeaconEffect(Block.blockGold.blockID, HASTE_EFFECT);
		addBeaconEffect(Block.blockDiamond.blockID, FORTUNE_EFFECT);
		addBeaconEffect(Block.blockEmerald.blockID, LOOTING_EFFECT);
		addBeaconEffect(Block.blockLapis.blockID, TRUE_SIGHT_EFFECT);
		addBeaconEffect(Block.glowStone.blockID, NIGHT_VISION_EFFECT);
		
		addBeaconEffect(BTWBlocks.aestheticOpaque.blockID, AestheticOpaqueBlock.SUBTYPE_HELLFIRE, FIRE_RESIST_EFFECT);
		
		addBeaconEffect(Block.blockIron.blockID, MAGNETIC_POLE_EFFECT);
		addBeaconEffect(BTWBlocks.soulforgedSteelBlock.blockID, SPAWN_ANCHOR_EFFECT);
		addBeaconEffect(Block.glass.blockID, DECORATIVE_EFFECT);
		addBeaconEffect(BTWBlocks.aestheticEarth.blockID, AestheticOpaqueEarthBlock.SUBTYPE_DUNG, NAUSEA_EFFECT);
		
		addBeaconEffect(BTWBlocks.aestheticOpaque.blockID, AestheticOpaqueBlock.SUBTYPE_ENDER_BLOCK, ENDER_ANTENNA_EFFECT);
		addBeaconEffect(BTWBlocks.companionCube.blockID, COMPANION_EFFECT);
		
		addBeaconEffect(BTWBlocks.spiderEyeBlock.blockID, JUNGLE_SPIDER_REPELLENT);
	}
}
