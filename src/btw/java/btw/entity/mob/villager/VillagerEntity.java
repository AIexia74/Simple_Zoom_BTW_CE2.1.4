// FCMOD

package btw.entity.mob.villager;

import btw.client.fx.BTWEffectManager;
import btw.entity.mob.WitchEntity;
import btw.entity.mob.WolfEntity;
import btw.entity.mob.ZombieEntity;
import btw.entity.mob.behavior.VillagerBreedBehavior;
import btw.item.BTWItems;
import btw.util.RandomSelector;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.ToDoubleFunction;

public abstract class VillagerEntity extends EntityVillager
{
	protected static final int IN_LOVE_DATA_WATCHER_ID = 22;
	protected static final int TRADE_LEVEL_DATA_WATCHER_ID = 23;

	// data watcher 24 used by EntityCreature parent to indicate possession

	protected static final int TRADE_EXPERIENCE_DATA_WATCHER_ID = 25;
	protected static final int DIRTY_PEASANT_DATA_WATCHER_ID = 26;

	protected int aiFullTickCountdown;
	protected int updateTradesCountdown;

	public static final int PROFESSION_ID_FARMER = 0;
	public static final int PROFESSION_ID_LIBRARIAN = 1;
	public static final int PROFESSION_ID_PRIEST = 2;
	public static final int PROFESSION_ID_BLACKSMITH = 3;
	public static final int PROFESSION_ID_BUTCHER = 4;

	public static final int CASTE_ID_PEASANT = 0;
	public static final int CASTE_ID_MANUFACTURER = 1;
	public static final int CASTE_ID_ERUDITE = 2;

	public static Map<Integer, Class> professionMap = new HashMap();
	public static Map<Integer, ArrayList<Integer>> casteMap = new HashMap();

	public static Map<Integer, Set<WeightedMerchantEntry>> tradeByProfessionList = new HashMap();
	public static Map<Integer, Map<Integer, WeightedMerchantEntry>> levelUpTradeByProfessionList = new HashMap();
	public static Map<Integer, WeightedMerchantEntry> defaultTradeByProfessionList = new HashMap();

	public static Map<Integer, Map<WeightedMerchantEntry, TradeEffect>> tradeEffectRegistry = new HashMap();
	public static Map<Integer, Map<Integer, TradeEffect>> levelUpEffectRegistry = new HashMap();

	public static TradeEffect defaultLevelUpEffect;

	public static int[] xpPerLevel = {0, 5, 7, 10, 15, 20};

	static {
		professionMap.put(PROFESSION_ID_FARMER, FarmerVillagerEntity.class);
		professionMap.put(PROFESSION_ID_LIBRARIAN, LibrarianVillagerEntity.class);
		professionMap.put(PROFESSION_ID_PRIEST, PriestVillagerEntity.class);
		professionMap.put(PROFESSION_ID_BLACKSMITH, BlacksmithVillagerEntity.class);
		professionMap.put(PROFESSION_ID_BUTCHER, ButcherVillagerEntity.class);

		casteMap.put(CASTE_ID_PEASANT, new ArrayList());
		casteMap.put(CASTE_ID_MANUFACTURER, new ArrayList());
		casteMap.put(CASTE_ID_ERUDITE, new ArrayList());

		casteMap.get(CASTE_ID_PEASANT).add(PROFESSION_ID_FARMER);
		casteMap.get(CASTE_ID_MANUFACTURER).add(PROFESSION_ID_BLACKSMITH);
		casteMap.get(CASTE_ID_MANUFACTURER).add(PROFESSION_ID_BUTCHER);
		casteMap.get(CASTE_ID_ERUDITE).add(PROFESSION_ID_LIBRARIAN);
		casteMap.get(CASTE_ID_ERUDITE).add(PROFESSION_ID_PRIEST);

		defaultLevelUpEffect = new TradeEffect() {
			@Override
			public void playEffect(VillagerEntity villager) {
				villager.worldObj.playSoundAtEntity(villager, "random.levelup", 0.5F + (villager.rand.nextFloat() * 0.25F), 1.5F);
			}
		};
	}

	public VillagerEntity(World world)
	{
		this(world, 0);
	}

	public VillagerEntity(World world, int iProfession)
	{
		super(world, iProfession);

		tasks.removeAllTasksOfClass(EntityAIAvoidEntity.class);

		tasks.addTask(1, new EntityAIAvoidEntity(this, ZombieEntity.class, 8.0F, 0.3F, 0.35F));
		tasks.addTask(1, new EntityAIAvoidEntity(this, WolfEntity.class, 8.0F, 0.3F, 0.35F));

		tasks.removeAllTasksOfClass(EntityAIVillagerMate.class);

		tasks.addTask(1, new VillagerBreedBehavior(this));
		tasks.addTask(2, new EntityAITempt(this, 0.3F, Item.diamond.itemID, false));

		experienceValue = 50; // set experience when slain

		updateTradesCountdown = 0;
		aiFullTickCountdown = 0;
	}

	@Override
	protected void updateAITick() {
		aiFullTickCountdown--;

		if (aiFullTickCountdown <= 0) {
			aiFullTickCountdown = 70 + rand.nextInt(50); // schedule the next village position update

			worldObj.villageCollectionObj.addVillagerPosition(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));

			villageObj = worldObj.villageCollectionObj.findNearestVillage(
					MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ), 32);

			if (villageObj == null) {
				detachHome();
			}
			else {
				ChunkCoordinates var1 = villageObj.getCenter();

				setHomeArea(var1.posX, var1.posY, var1.posZ, (int)((float)villageObj.getVillageRadius() * 0.6F));
			}
		}

		if (!isTrading()) {
			if (this.getCurrentTradeLevel() == 0) {
				// this indicates a newly created villager or an old one that was created before I revamped the trading system

				setTradeLevel(1);

				buyingList = null;
				updateTradesCountdown = 0;

				checkForNewTrades(1);
			}
			else if (updateTradesCountdown > 0) {
				if(buyingList == null) {
					buyingList = new MerchantRecipeList();
				}
				updateTradesCountdown--;

				if (updateTradesCountdown <= 0) {
					// remove all trades which have exceeded their maximum uses

					Iterator tradeListIterator = this.buyingList.iterator();

					while (tradeListIterator.hasNext()) {
						MerchantRecipe tempRecipe = (MerchantRecipe)tradeListIterator.next();

						if (tempRecipe.func_82784_g()) { // check for toolUses >= this.maxTradeUses;
							tradeListIterator.remove();
						}
					}

					int desiredNumTrades = getCurrentMaxNumTrades();

					if (buyingList.size() < desiredNumTrades) {
						checkForNewTrades(desiredNumTrades - buyingList.size());

						worldObj.setEntityState(this, (byte)14); // triggers "happy villager" particles

						addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 0));
					}
				}
			}
			else {
				// schedule periodic checks of the trade list so it'll never jam up

				updateTradesCountdown = 600 + rand.nextInt(600); // 30 seconds to a minute
			}
		}
	}

	@Override
	public boolean interact(EntityPlayer player)
	{
		if (customInteract(player))
		{
			return true;
		}

		if (getInLove() > 0)
		{
			return entityAgeableInteract(player);
		}

		return super.interact(player);  		
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();

		dataWatcher.addObject(IN_LOVE_DATA_WATCHER_ID, new Integer(0));
		dataWatcher.addObject(TRADE_LEVEL_DATA_WATCHER_ID, new Integer(0));
		dataWatcher.addObject(TRADE_EXPERIENCE_DATA_WATCHER_ID, new Integer(0));
		dataWatcher.addObject(DIRTY_PEASANT_DATA_WATCHER_ID, new Integer(0));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag)
	{
		super.writeEntityToNBT(tag);

		tag.setInteger("FCInLove", getInLove());

		tag.setInteger("FCTradeLevel", this.getCurrentTradeLevel());
		tag.setInteger("FCTradeXP", this.getCurrentTradeXP());

		tag.setInteger("FCDirty", getDirtyPeasant());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);

		if (tag.hasKey("FCInLove")) {
			this.setInLove(tag.getInteger("FCInLove"));
		}

		if (tag.hasKey("FCTradeLevel")) {
			this.setTradeLevel(tag.getInteger("FCTradeLevel"));
		}

		if (tag.hasKey("FCTradeXP")) {
			this.setTradeExperience(tag.getInteger("FCTradeXP"));
		}

		if (tag.hasKey("FCDirty")) {
			this.setDirtyPeasant(tag.getInteger("FCDirty"));
		}

		this.checkForInvalidTrades();
	}

	@Override
	public void setRevengeTarget(EntityLiving attackingEntity)
	{
		entityLivingToAttack = attackingEntity;

		if (attackingEntity != null)
		{
			revengeTimer = 100;

			if (villageObj != null)
			{
				villageObj.addOrRenewAgressor(attackingEntity);
			}

			if (isEntityAlive())
			{
				worldObj.setEntityState(this, (byte)13);
			}
		}
		else
		{
			revengeTimer = 0;
		}
	}

	@Override
	public void useRecipe(MerchantRecipe recipe) {
		//if (!recipe.isMandatory())
		recipe.incrementToolUses();

		updateTradesCountdown = 10;

		// special trade reactions
		this.playEffectsForTrade(recipe);

		if (recipe.tradeLevel < 0) { // negative trade levels represent a level up trade
			int tradeLevel = this.getCurrentTradeLevel();

			if (tradeLevel < 5 && this.getCurrentTradeXP() == getCurrentTradeMaxXP() && this.getCurrentTradeLevel() == -(recipe.tradeLevel)) {
				tradeLevel++;

				setTradeLevel(tradeLevel);
				setTradeExperience(0);

				this.playEffectsForLevelUp(recipe);
			}
		}
		else if (recipe.tradeLevel >= this.getCurrentTradeLevel() && !recipe.isMandatory()) {
			int currentXP = this.getCurrentTradeXP() + 1;
			int maxXP = getCurrentTradeMaxXP();

			if (currentXP > maxXP) {
				currentXP = maxXP;
			}

			setTradeExperience(currentXP);
		}
	}

	@Override
	public MerchantRecipeList getRecipes(EntityPlayer player) {
		if (buyingList == null) {
			checkForNewTrades(1);
		}

		return buyingList;
	}

	@Override
	public void initCreature() {
		setProfession(this.getProfessionFromClass());
	}


	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		if (!worldObj.isRemote)
		{
			if (isEntityAlive())
			{
				checkForLooseMilk();
			}
		}
		else
		{
			updateStatusParticles();
		}
	}

	@Override
	protected void dropFewItems(boolean bKilledByPlayer, int iLootingModifier)
	{
		if (!hasHeadCrabbedSquid())
		{
			int iDropItemID = BTWItems.rawMysteryMeat.itemID;

			if (isBurning()) {
				if (worldObj.getDifficulty().shouldBurningMobsDropCookedMeat()) {
					iDropItemID = BTWItems.cookedMysteryMeat.itemID;
				}
				else {
					iDropItemID = BTWItems.burnedMeat.itemID;
				}
			}

			int iNumDropped = rand.nextInt(3) + 1 + rand.nextInt(1 + iLootingModifier);

			for (int iTempCount = 0; iTempCount < iNumDropped; ++iTempCount)
			{    	
				dropItem(iDropItemID, 1);
			}
		}
	}

	@Override
	public float getSoundPitch()
	{
		float fPitch = super.getSoundPitch();

		if (isPossessed() || (getProfession() == 2 && this.getCurrentTradeLevel() == 5))
		{
			fPitch *= 0.60F;
		}

		return fPitch;
	}

	@Override
    public boolean getCanCreatureTypeBePossessed()
	{
		return true;
	}

	@Override
	public void onFullPossession()
	{
		worldObj.playAuxSFX(BTWEffectManager.POSSESSED_VILLAGER_TRANSFORMATION_EFFECT_ID,
				MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ), 
				0);

		setDead();

		WitchEntity entityWitch = (WitchEntity) EntityList.createEntityOfType(WitchEntity.class, worldObj);

		entityWitch.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
		entityWitch.renderYawOffset = renderYawOffset;

		entityWitch.setPersistent(true);

		worldObj.spawnEntityInWorld(entityWitch);
	}

	@Override
	public boolean isValidZombieSecondaryTarget(EntityZombie zombie)
	{
		return true;
	}

	@Override
	public boolean isSecondaryTargetForSquid()
	{
		return true;
	}

	@Override
	public double getMountedYOffset()
	{
		return (double)height;
	}

	@Override
	public VillagerEntity func_90012_b(EntityAgeable otherParent)
	{
		// creates new villager when breeding

		VillagerEntity child = createVillager(this.worldObj);

		child.initCreature();

		return child;
	}

	//------------- Class Specific Methods ------------//

	public static VillagerEntity createVillager(World world) {
		return createVillagerFromProfession(world, 0);
	}

	public static VillagerEntity createVillagerFromProfession(World world, int profession) {
		Class villagerClass = professionMap.get(profession);

		try {
			villagerClass = EntityList.getRegisteredReplacement(villagerClass);
			
			VillagerEntity villager = (VillagerEntity) villagerClass.getConstructor(World.class).newInstance(world);
			villager.setProfession(profession);

			return villager;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return null;
	}

	public VillagerEntity spawnBabyVillagerWithProfession(EntityAgeable otherParent, int profession) {
		VillagerEntity child = createVillagerFromProfession(this.worldObj, profession);
		child.initCreature();

		return child;
	}

	public int getProfessionFromClass() {
		for (int id : this.professionMap.keySet()) {
			if (this.getClass().isAssignableFrom(professionMap.get(id))) {
				return id;
			}
		}

		return 0;
	}

	public static int getCasteFromProfession(int professionID) {
		for (int caste : casteMap.keySet()) {
			if (casteMap.get(caste).contains(professionID)) {
				return caste;
			}
		}

		return -1;
	}

	// ------ Trade list functionality ------ //

	protected void checkForNewTrades(int availableTrades) {
		if (availableTrades > 0) {
			if (this.getCurrentTradeMaxXP() == this.getCurrentTradeXP() && this.checkForLevelUpTrade()) {
				--availableTrades;

				if (availableTrades <= 0) {
					return;
				}
			}

			MerchantRecipeList recipeList = new MerchantRecipeList();

			availableTrades = this.checkForProfessionMandatoryTrades(recipeList, availableTrades, this.getCurrentTradeLevel());

			if (availableTrades > 0) {
				this.checkForProfessionTrades(recipeList, availableTrades);
			}

			if (recipeList.isEmpty()) {
				recipeList.add(this.getProfessionDefaultTrade());
			}
			else {
				Collections.shuffle(recipeList);
			}

			if (this.buyingList == null) {
				this.buyingList = new MerchantRecipeList();
			}

			for (int i = 0; i < recipeList.size(); ++i) {
				this.buyingList.addToListWithCheck((MerchantRecipe)recipeList.get(i));
			}
		}
	}

	protected void checkForProfessionTrades(MerchantRecipeList recipeList, int availableTrades) {
		//Copies trade list to allow for special case trades
		Set<WeightedMerchantEntry> tradeList = new HashSet();

		for (WeightedMerchantEntry entry : tradeByProfessionList.get(this.getProfessionFromClass())) {
			if (entry.level <= this.getCurrentTradeLevel() && !entry.isMandatory() && entry.canBeAdded(this)) {
				tradeList.add(entry);
			}
		}
		
		// Sanity check to prevent infinite loops
		int currentAttempts = 0;
		int maxAttempts = 50;
		
		while (availableTrades > 0 && currentAttempts < maxAttempts) {
			MerchantRecipe recipe = this.getRandomTradeFromAdjustedWeight(tradeList);
			
			if (!this.doesRecipeListAlreadyContainRecipe(recipe)) {
				recipeList.add(recipe);
				availableTrades--;
			}
			
			currentAttempts++;
		}
	}

	protected int checkForProfessionMandatoryTrades(MerchantRecipeList recipeList, int availableTrades, int level) {
		Set<WeightedMerchantEntry> entries = tradeByProfessionList.get(this.getProfessionFromClass());

		if (entries != null) {
			for (WeightedMerchantEntry entry : entries) {
				if (entry.level <= level && availableTrades > 0 && entry.isMandatory()) {
					MerchantRecipe recipe = entry.generateRecipe(this.rand);
					
					if (!this.doesRecipeListAlreadyContainRecipe(recipe)) {
						recipeList.add(recipe);
						availableTrades--;
					}
				}
			}
		}

		return availableTrades;
	}

	private boolean checkForLevelUpTrade() {
		if (this.getCurrentTradeLevel() >= 5)
			return false;

		MerchantRecipe recipe = this.getProfessionLevelUpTrade(this.getCurrentTradeLevel());

		if (recipe != null && !this.doesRecipeListAlreadyContainRecipe(recipe)) {
			this.buyingList.add(recipe);
			return true;
		}
		else {
			return false;
		}
	}

	protected MerchantRecipe getProfessionLevelUpTrade(int level) {
		WeightedMerchantEntry entry = levelUpTradeByProfessionList.get(this.getProfessionFromClass()).get(level);

		if (entry == null) {
			throw new RuntimeException("Level up entry for profession " + this.getProfessionFromClass() + " on level " + this.getCurrentTradeLevel() + " was missing!");
		}

		return entry.generateRecipe(this.rand);
	}
	
	protected boolean doesRecipeListAlreadyContainRecipe(MerchantRecipe recipe) {
		if (this.buyingList != null) {
			for (int i = 0; i < buyingList.size(); ++i) {
				MerchantRecipe recipeForCompare = (MerchantRecipe) buyingList.get(i);
				
				if (recipe.hasSameIDsAs(recipeForCompare)) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * Return the default trade to use if no other trade is found
	 * @return The default trade
	 */
	protected MerchantRecipe getProfessionDefaultTrade() {
		return defaultTradeByProfessionList.get(this.getProfessionFromClass()).generateRecipe(this.rand);
	}

	protected MerchantRecipe getRandomTradeFromAdjustedWeight(Set<WeightedMerchantEntry> tradeList) {
		final int villagerTradeLevel = this.getCurrentTradeLevel();

		ToDoubleFunction<WeightedMerchantEntry> weighter = new ToDoubleFunction<WeightedMerchantEntry>() {
			@Override
			public double applyAsDouble(WeightedMerchantEntry entry) {
				return entry.weight * entry.level / villagerTradeLevel;
			}
		};

		RandomSelector<WeightedMerchantEntry> selector = RandomSelector.weighted(tradeList, weighter);

		return selector.next(this.rand).generateRecipe(this.rand);
	}

	public int getCurrentMaxNumTrades() {
		int numMandatoryTrades = 0;
		
		for (WeightedMerchantEntry entry : tradeByProfessionList.get(this.getProfessionFromClass())) {
			if (entry.level <= this.getCurrentTradeLevel() && entry.isMandatory()) {
				numMandatoryTrades++;
			}
		}
		
		return this.getCurrentTradeLevel() + numMandatoryTrades;
	}

	private void checkForInvalidTrades() {
		MerchantRecipe trade;

		if (this.buyingList != null) {
			Iterator iterator = this.buyingList.iterator();

			while (iterator.hasNext()) {
				trade = (MerchantRecipe)iterator.next();

				if (this.isInvalidProfessionTrade(trade)) {
					iterator.remove();
				}
			}
		}
	}

	/**
	 * Used to clear any invalid trades e.g. that may be left over from previous versions
	 * @param trade The trade to check
	 * @return Whether the trade was invalid and should be removed
	 */
	protected boolean isInvalidProfessionTrade(MerchantRecipe trade) {
		try {
			//Checks if the trade is in any of the trade lists
			//If it is, check for invalid stack sizes

			//Standard trades
			for (WeightedMerchantEntry entry : tradeByProfessionList.get(this.getProfessionFromClass())) {
				if (entry.matchesMerchantRecipe(trade)) {
					if (trade.getItemToBuy().stackSize > trade.getItemToBuy().getMaxStackSize() ||
							trade.hasSecondItemToBuy() && trade.getSecondItemToBuy().stackSize > trade.getSecondItemToBuy().getMaxStackSize() ||
							trade.getItemToSell().stackSize > trade.getItemToSell().getMaxStackSize()) {
						return true;
					}

					return false;
				}
			}

			//Level up trades
			for (int i = 1; i < 5; i++) {
				WeightedMerchantEntry entry = levelUpTradeByProfessionList.get(this.getProfessionFromClass()).get(i);

				if (entry.matchesMerchantRecipe(trade)) {
					if (trade.getItemToBuy().stackSize > trade.getItemToBuy().getMaxStackSize() ||
							trade.hasSecondItemToBuy() && trade.getSecondItemToBuy().stackSize > trade.getSecondItemToBuy().getMaxStackSize() ||
							trade.getItemToSell().stackSize > trade.getItemToSell().getMaxStackSize()) {
						return true;
					}

					return false;
				}
			}

			//Trade not in any lists
			return true;
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		}
	}

	// ------ Simple buying trades ------ //

	public static WeightedMerchantEntry addTradeToBuySingleItem(int profession, int itemID, int minEmeraldCount, int maxEmeraldCount, float weight, int tradeLevel) {
		return addTradeToBuySingleItem(profession, itemID, 0, minEmeraldCount, maxEmeraldCount, weight, tradeLevel);
	}

	public static WeightedMerchantEntry addTradeToBuySingleItem(int profession, int itemID, int itemMetadata, int minEmeraldCount, int maxEmeraldCount, float weight, int tradeLevel) {
		return addTradeToBuy(profession, itemID, itemMetadata, 1, 1, minEmeraldCount, maxEmeraldCount, weight, tradeLevel);
	}

	public static WeightedMerchantEntry addTradeToBuyMultipleItems(int profession, int itemID, int minItemCount, int maxItemCount, float weight, int tradeLevel) {
		return addTradeToBuyMultipleItems(profession, itemID, 0, minItemCount, maxItemCount, weight, tradeLevel);
	}

	public static WeightedMerchantEntry addTradeToBuyMultipleItems(int profession, int itemID, int itemMetadata, int minItemCount, int maxItemCount, float weight, int tradeLevel) {
		return addTradeToBuy(profession, itemID, itemMetadata, minItemCount, maxItemCount, 1, 1, weight, tradeLevel);
	}

	public static WeightedMerchantEntry addTradeToBuy(int profession, int itemID, int itemMetadata, int minItemCount, int maxItemCount, int minEmeraldCount, int maxEmeraldCount, float weight, int tradeLevel) {
		WeightedMerchantRecipeEntry entry = new WeightedMerchantRecipeEntry(
				itemID, itemMetadata, minItemCount, maxItemCount,
				Item.emerald.itemID, 0, minEmeraldCount, maxEmeraldCount,
				weight, tradeLevel
				);

		return addCustomTrade(profession, entry);
	}

	// ------ Simple selling trades ------ //

	public static WeightedMerchantEntry addTradeToSellSingleItem(int profession, int itemID, int minEmeraldCount, int maxEmeraldCount, float weight, int tradeLevel) {
		return addTradeToSellSingleItem(profession, itemID, 0, minEmeraldCount, maxEmeraldCount, weight, tradeLevel);
	}

	public static WeightedMerchantEntry addTradeToSellSingleItem(int profession, int itemID, int itemMetadata, int minEmeraldCount, int maxEmeraldCount, float weight, int tradeLevel) {
		return addTradeToSell(profession, itemID, itemMetadata, 1, 1, minEmeraldCount, maxEmeraldCount, weight, tradeLevel);
	}

	public static WeightedMerchantEntry addTradeToSellMultipleItems(int profession, int itemID, int minItemCount, int maxItemCount, float weight, int tradeLevel) {
		return addTradeToSellMultipleItems(profession, itemID, 0, minItemCount, maxItemCount, weight, tradeLevel);
	}

	public static WeightedMerchantEntry addTradeToSellMultipleItems(int profession, int itemID, int itemMetadata, int minItemCount, int maxItemCount, float weight, int tradeLevel) {
		return addTradeToSell(profession, itemID, itemMetadata, minItemCount, maxItemCount, 1, 1, weight, tradeLevel);
	}

	public static WeightedMerchantEntry addTradeToSell(int profession, int itemID, int itemMetadata, int minItemCount, int maxItemCount, int minEmeraldCount, int maxEmeraldCount, float weight, int tradeLevel) {
		WeightedMerchantRecipeEntry entry = new WeightedMerchantRecipeEntry(
				Item.emerald.itemID, 0, minEmeraldCount, maxEmeraldCount,
				itemID, itemMetadata, minItemCount, maxItemCount,
				weight, tradeLevel
				);

		return addCustomTrade(profession, entry);
	}

	// ------ Other trades ------//

	public static WeightedMerchantEntry addArcaneScrollTrade(int profession, int enchantmentID, int minEmeraldCount, int maxEmeraldCount, float weight, int tradeLevel) {
		return addItemConversionTrade(profession, Item.paper.itemID, 0, minEmeraldCount, maxEmeraldCount, BTWItems.arcaneScroll.itemID, enchantmentID, weight, tradeLevel);
	}

	public static WeightedMerchantEntry addSkullconversionTrade(int profession, int inputSkullType, int minEmeralds, int maxEmeralds, int resultSkullType, float weight, int tradeLevel) {
		return addItemConversionTrade(profession, Item.skull.itemID, inputSkullType, minEmeralds, maxEmeralds, Item.skull.itemID, resultSkullType, weight, tradeLevel);
	}

	public static WeightedMerchantEntry addItemConversionTrade(int profession, int itemID, int minEmeralds, int maxEmeralds, int resultID, float weight, int tradeLevel) {
		return addItemConversionTrade(profession, itemID, 0, minEmeralds, maxEmeralds, resultID, 0, weight, tradeLevel);
	}

	public static WeightedMerchantEntry addItemConversionTrade(int profession, int itemID, int itemMetadata, int minEmeraldCount, int maxEmeraldCount, int resultID, int resultMetadata, float weight, int tradeLevel) {
		WeightedMerchantRecipeEntry entry = new WeightedMerchantRecipeEntry(
				itemID, itemMetadata, 1, 1,
				Item.emerald.itemID, 0, minEmeraldCount, maxEmeraldCount,
				resultID, resultMetadata, 1, 1,
				weight, tradeLevel
				);

		return addCustomTrade(profession, entry);
	}

	public static WeightedMerchantEntry addEnchantmentTrade(int profession, int itemID, int minEmeraldCount, int maxEmeraldCount, float weight, int tradeLevel) {
		WeightMerchantEnchantmentEntry entry = new WeightMerchantEnchantmentEntry(itemID, minEmeraldCount, maxEmeraldCount, weight, tradeLevel);

		return addCustomTrade(profession, entry);
	}

	public static WeightedMerchantEntry addComplexTrade(
			int profession,
			int input1ID, int input1Metadata, int input1MinCount, int input1MaxCount,
			int input2ID, int input2Metadata, int input2MinCount, int input2MaxCount,
			int resultID, int resultMetadata, int resultMinCount, int resultMaxCount,
			float weight, int tradeLevel
			) {
		WeightedMerchantRecipeEntry entry = new WeightedMerchantRecipeEntry(
				input1ID, input1Metadata, input1MinCount, input1MaxCount,
				input2ID, input2Metadata, input2MinCount, input2MaxCount,
				resultID, resultMetadata, resultMinCount, resultMaxCount,
				weight, tradeLevel
				);

		return addCustomTrade(profession, entry);
	}

	/**
	 * Add a trade with a custom-defined WeightedMerchantEntry class
	 */
	public static WeightedMerchantEntry addCustomTrade(int profession, WeightedMerchantEntry entry) {
		Map<Integer, Set<WeightedMerchantEntry>> tradeList = tradeByProfessionList;

		Set<WeightedMerchantEntry> tradeEntryList = tradeList.get(profession);

		if (tradeEntryList == null) {
			tradeEntryList = new HashSet();
			tradeList.put(profession, tradeEntryList);
		}

		tradeEntryList.add(entry);

		return entry;
	}

	// ------ Level up trades ------ //

	public static void addLevelUpTradeToBuySingleItem(int profession, int itemID, int minEmeraldCount, int maxEmeraldCount, int tradeLevel) {
		addLevelUpTradeToBuySingleItem(profession, itemID, 0, minEmeraldCount, maxEmeraldCount, tradeLevel);
	}

	public static void addLevelUpTradeToBuySingleItem(int profession, int itemID, int itemMetadata, int minEmeraldCount, int maxEmeraldCount, int tradeLevel) {
		addLevelUpTradeToBuy(profession, itemID, itemMetadata, 1, 1, minEmeraldCount, maxEmeraldCount, tradeLevel);
	}

	public static void addLevelUpTradeToBuyMultipleItems(int profession, int itemID, int minItemCount, int maxItemCount, int tradeLevel) {
		addLevelUpTradeToBuyMultipleItems(profession, itemID, 0, minItemCount, maxItemCount, tradeLevel);
	}

	public static void addLevelUpTradeToBuyMultipleItems(int profession, int itemID, int itemMetadata, int minItemCount, int maxItemCount, int tradeLevel) {
		addLevelUpTradeToBuy(profession, itemID, itemMetadata, minItemCount, maxItemCount, 1, 1, tradeLevel);
	}

	public static void addLevelUpTradeToBuy(int profession, int itemID, int itemMetadata, int minItemCount, int maxItemCount, int minEmeraldCount, int maxEmeraldCount, int tradeLevel) {
		WeightedMerchantRecipeEntry entry = new WeightedMerchantRecipeEntry(
				itemID, itemMetadata, minItemCount, maxItemCount,
				Item.emerald.itemID, 0, minEmeraldCount, maxEmeraldCount,
				1, tradeLevel
				);

		addCustomLevelUpTrade(profession, entry);
	}

	public static void addCustomLevelUpTrade(int profession, WeightedMerchantEntry entry) {
		Map<Integer, WeightedMerchantEntry> tradeEntryList = levelUpTradeByProfessionList.get(profession);

		if (tradeEntryList == null) {
			tradeEntryList = new HashMap();
			levelUpTradeByProfessionList.put(profession, tradeEntryList);
		}

		int tradeLevel = entry.level;
		entry.level *= -1; //Negative level indicates level up

		if (tradeEntryList.containsKey(tradeLevel)) {
			throw new RuntimeException("Profession id " + profession + "already has a level up trade assigned for level " + tradeLevel);
		}
		else {
			tradeEntryList.put(tradeLevel, entry);
		}
	}

	public static boolean removeLevelUpTrade(int profession, int level) {
		Map<Integer, WeightedMerchantEntry> tradeEntryList = levelUpTradeByProfessionList.get(profession);

		if (tradeEntryList != null) {
			WeightedMerchantEntry entry = tradeEntryList.get(level);

			if (entry != null) {
				tradeEntryList.remove(level);
			}
		}

		return false;
	}

	// ------ Trade removal ------ //
	public static boolean removeTradeToBuy(int profession, int itemID, int itemMetadata) {
		return removeComplexTrade(profession, itemID, itemMetadata, 0, 0, Item.emerald.itemID, 0);
	}

	public static boolean removeTradeToSell(int profession, int itemID, int itemMetadata) {
		return removeComplexTrade(profession, Item.emerald.itemID, 0, 0, 0, itemID, itemMetadata);
	}

	public static boolean removeComplexTrade(int profession, int item1ID, int item1Metadata, int item2ID, int item2Metadata, int resultID, int resultMetadata) {
		WeightedMerchantRecipeEntry entryToRemove = new WeightedMerchantRecipeEntry(item1ID, item1Metadata, 1, 1, item2ID, item2Metadata, 1, 1, resultID, resultMetadata, 1, 1, 1, 1);

		return removeCustomTrade(profession, entryToRemove);
	}

	public static boolean removeEnchantmentTrade(int profession, int itemID) {
		WeightMerchantEnchantmentEntry entryToRemove = new WeightMerchantEnchantmentEntry(itemID, 1, 1, 1, 1);

		return removeCustomTrade(profession, entryToRemove);
	}

	public static boolean removeCustomTrade(int profession, WeightedMerchantEntry entryToRemove) {
		//Standard trades
		for (WeightedMerchantEntry entry : tradeByProfessionList.get(profession)) {
			if (entry.equals(entryToRemove)) {
				tradeByProfessionList.get(profession).remove(entry);
				return true;
			}
		}

		//Level up trades
		for (int i = 1; i < 5; i++) {
			WeightedMerchantEntry entry = levelUpTradeByProfessionList.get(profession).get(i);

			if (entry.equals(entryToRemove)) {
				levelUpTradeByProfessionList.get(profession).put(i, null);
				return true;
			}
		}

		//Trade not in any lists
		return false;
	}

	// ------ Trade Effects ------ //

	public void playEffectsForTrade(MerchantRecipe trade) {
		Map<WeightedMerchantEntry, TradeEffect> effectRegistry = tradeEffectRegistry.get(this.getProfessionFromClass());

		if (effectRegistry != null) {
			for (WeightedMerchantEntry entry : effectRegistry.keySet()) {
				if (entry.matchesMerchantRecipe(trade)) {
					effectRegistry.get(entry).playEffect(this);
				}
			}
		}
	}

	public static void registerEffectForTrade(int profession, WeightedMerchantEntry entry, TradeEffect effect) {
		Map<WeightedMerchantEntry, TradeEffect> effectRegistry = tradeEffectRegistry.get(profession);

		if (effectRegistry == null) {
			effectRegistry = new HashMap();
			tradeEffectRegistry.put(profession, effectRegistry);
		}

		effectRegistry.put(entry, effect);
	}

	public static boolean removeEffectForTrade(int profession, WeightedMerchantEntry entry, TradeEffect effect) {
		Map<WeightedMerchantEntry, TradeEffect> effectRegistry = tradeEffectRegistry.get(profession);

		if (effectRegistry != null && effectRegistry.containsKey(entry)) {
			effectRegistry.remove(entry);
			return true;
		}
		else {
			return false;
		}
	}

	public void playEffectsForLevelUp(MerchantRecipe trade) {
		Map<Integer, TradeEffect> effectRegistry = levelUpEffectRegistry.get(this.getProfessionFromClass());

		if (effectRegistry != null) {
			TradeEffect effect = effectRegistry.get(-trade.tradeLevel);

			if (effect != null) {
				effect.playEffect(this);
				return;
			}
		}

		defaultLevelUpEffect.playEffect(this);
	}

	public static void registerEffectForLevelUp(int profession, int level, TradeEffect effect) {
		Map<Integer, TradeEffect> effectRegistry = levelUpEffectRegistry.get(profession);

		if (effectRegistry == null) {
			effectRegistry = new HashMap();
			levelUpEffectRegistry.put(profession, effectRegistry);
		}

		effectRegistry.put(level, effect);
	}

	public static boolean removeEffectForLevelUp(int profession, int level) {
		Map<Integer, TradeEffect> effectRegistry = levelUpEffectRegistry.get(profession);

		if (effectRegistry != null && effectRegistry.containsKey(level)) {
			effectRegistry.remove(level);
			return true;
		}
		else {
			return false;
		}
	}

	// ------ Misc entity functionality ------ //

	protected boolean customInteract(EntityPlayer player) {
		ItemStack heldStack = player.inventory.getCurrentItem();

		if (heldStack != null && heldStack.getItem().itemID == Item.diamond.itemID && getGrowingAge() == 0 && getInLove() == 0 && !isPossessed()) {
			if (!player.capabilities.isCreativeMode) {
				heldStack.stackSize--;

				if (heldStack.stackSize <= 0) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
				}
			}

			worldObj.playSoundAtEntity(this, "random.classic_hurt", 1.0F, getSoundPitch() * 2.0F);

			setInLove(1);

			entityToAttack = null;

			return true;
		}
		else if (heldStack != null && heldStack.itemID == BTWItems.nameTag.itemID) {
			BTWItems.nameTag.itemInteractionForEntity(heldStack, this);
		}

		return false;
	}

	protected void updateStatusParticles() {
		spawnCustomParticles();

		if (getInLove() > 0) {
			generateRandomParticles("heart");
		}
	}

	protected void spawnCustomParticles() {}

	protected void generateRandomParticles(String sParticle) {
		for (int iTempCount = 0; iTempCount < 5; ++iTempCount) {
			double dVelX = rand.nextGaussian() * 0.02D;
			double dVelY = rand.nextGaussian() * 0.02D;
			double dVelZ = rand.nextGaussian() * 0.02D;

			worldObj.spawnParticle(sParticle, 
					posX + (rand.nextFloat() * width * 2F) - width, 
					posY + 1D + (rand.nextFloat() * height), 
					posZ + (rand.nextFloat() * width * 2F) - width, 
					dVelX, dVelY, dVelZ);
		}
	}

	public void checkForLooseMilk() {
		List collisionList = worldObj.getEntitiesWithinAABB(EntityItem.class, 
				AxisAlignedBB.getAABBPool().getAABB(
						posX - 1.0f, posY - 1.0f, posZ - 1.0f, 
						posX + 1.0f, posY + 1.0f, posZ + 1.0f));

		if (!collisionList.isEmpty()) {
			for(int listIndex = 0; listIndex < collisionList.size(); listIndex++) {
				EntityItem entityItem = (EntityItem)collisionList.get(listIndex);

				if (entityItem.delayBeforeCanPickup <= 0 && !(entityItem.isDead)) {
					int iTempItemID = entityItem.getEntityItem().itemID;

					Item tempItem = Item.itemsList[iTempItemID];

					if (tempItem.itemID == Item.bucketMilk.itemID) {
						// toss the milk

						entityItem.setDead();

						entityItem = (EntityItem) EntityList.createEntityOfType(EntityItem.class, worldObj, posX, posY - 0.30000001192092896D + (double)getEyeHeight(), posZ, 
								new ItemStack(Item.bucketMilk, 1, 0));

						float f1 = 0.2F;

						entityItem.motionX = -MathHelper.sin((rotationYaw / 180F) * 3.141593F) * MathHelper.cos((rotationPitch / 180F) * 3.141593F) * f1;
						entityItem.motionZ = MathHelper.cos((rotationYaw / 180F) * 3.141593F) * MathHelper.cos((rotationPitch / 180F) * 3.141593F) * f1;

						entityItem.motionY = -MathHelper.sin((rotationPitch / 180F) * 3.141593F) * f1 + 0.2F;

						f1 = 0.02F;
						float f3 = rand.nextFloat() * 3.141593F * 2.0F;
						f1 *= rand.nextFloat();
						entityItem.motionX += Math.cos(f3) * (double)f1;
						entityItem.motionY += 0.25F;
						entityItem.motionZ += Math.sin(f3) * (double)f1;

						entityItem.delayBeforeCanPickup = 10;

						worldObj.spawnEntityInWorld(entityItem);

						int iFXI = MathHelper.floor_double(entityItem.posX);
						int iFXJ = MathHelper.floor_double(entityItem.posY);
						int iFXK = MathHelper.floor_double(entityItem.posZ);

						int iExtraData = 0;

						if (isPossessed() || (getProfession() == 2 && this.getCurrentTradeLevel() == 5)) {
							iExtraData = 1;
						}

						worldObj.playAuxSFX(BTWEffectManager.TOSS_THE_MILK_EFFECT_ID, iFXI, iFXJ, iFXK, iExtraData);
					}
				}
			}
		}
	}  

	public int getInLove() {
		return dataWatcher.getWatchableObjectInt(IN_LOVE_DATA_WATCHER_ID);
	}

	public void setInLove(int iInLove) {
		dataWatcher.updateObject(IN_LOVE_DATA_WATCHER_ID, iInLove);
	}

	public int getCurrentTradeLevel() {
		return dataWatcher.getWatchableObjectInt(TRADE_LEVEL_DATA_WATCHER_ID);
	}

	public void setTradeLevel(int iTradeLevel) {
		dataWatcher.updateObject(TRADE_LEVEL_DATA_WATCHER_ID, iTradeLevel);
	}

	public int getCurrentTradeXP() {
		return dataWatcher.getWatchableObjectInt(TRADE_EXPERIENCE_DATA_WATCHER_ID);
	}

	public void setTradeExperience(int iTradeExperience) {
		dataWatcher.updateObject(TRADE_EXPERIENCE_DATA_WATCHER_ID, iTradeExperience);
	}

	public int getDirtyPeasant() {
		return 0;
	}

	public int getCurrentTradeMaxXP() {
		return this.xpPerLevel[this.getCurrentTradeLevel()];
	}

	public void setDirtyPeasant(int iDirtyPeasant) {}

	protected void scheduleImmediateTradelistRefresh() {
		updateTradesCountdown = 1;
	}

	// ------ Helper Classes ------ //

	public static abstract class WeightedMerchantEntry {
		public final float weight;
		public int level; //Not final to allow inversion for level up trade

		private boolean isMandatory;

		private TradeConditional conditional;

		public WeightedMerchantEntry(float weight, int level) {
			this.weight = weight;
			this.level = level;
			this.isMandatory = false;
		}

		public abstract boolean equals(WeightedMerchantEntry entry);

		public abstract MerchantRecipe generateRecipe(Random rand);

		public abstract boolean matchesMerchantRecipe(MerchantRecipe trade);

		public void setDefault(int profession) {
			VillagerEntity.defaultTradeByProfessionList.put(profession, this);
		}

		public WeightedMerchantEntry registerEffectForTrade(int profession, TradeEffect effect) {
			VillagerEntity.registerEffectForTrade(profession, this, effect);
			return this;
		}

		public boolean isMandatory() {
			return this.isMandatory;
		}

		public WeightedMerchantEntry setMandatory() {
			this.isMandatory = true;
			return this;
		}

		public WeightedMerchantEntry setConditional(TradeConditional conditional) {
			this.conditional = conditional;
			return this;
		}

		public boolean canBeAdded(VillagerEntity villager) {
			return this.conditional == null || this.conditional.shouldAddTrade(villager);
		}
	}

	public static class WeightMerchantEnchantmentEntry extends WeightedMerchantEntry {
		public final int itemID;
		public final int minEmeraldCount;
		public final int maxEmeraldCount;

		public WeightMerchantEnchantmentEntry(int itemID, int minEmeraldCount, int maxEmeraldCount, float weight, int level) {
			super(weight, level);

			this.itemID = itemID;
			this.minEmeraldCount = minEmeraldCount;
			this.maxEmeraldCount = maxEmeraldCount;
		}

		public boolean equals(WeightedMerchantEntry entry) {
			return entry instanceof WeightMerchantEnchantmentEntry && 
					this.itemID == ((WeightMerchantEnchantmentEntry) entry).itemID;
		}

		@Override
		public MerchantRecipe generateRecipe(Random rand) {
			int cost = MathHelper.getRandomIntegerInRange(rand, minEmeraldCount, maxEmeraldCount);

			ItemStack input = new ItemStack(Item.itemsList[this.itemID]);
			ItemStack emeralds = new ItemStack(Item.emerald, cost);
			ItemStack result = EnchantmentHelper.addRandomEnchantment(rand, input.copy(), 5 + rand.nextInt(15));

			MerchantRecipe trade = new MerchantRecipe(input, emeralds, result, level);

			if (this.isMandatory())
				trade.setMandatory();

			return trade;
		}

		@Override
		public boolean matchesMerchantRecipe(MerchantRecipe trade) {
			return trade.getItemToBuy().itemID == this.itemID && 
					(!trade.hasSecondItemToBuy() || trade.getSecondItemToBuy().itemID == Item.emerald.itemID) && 
					trade.getItemToSell().itemID == this.itemID; 
		}
	}

	public static class WeightedMerchantRecipeEntry extends WeightedMerchantEntry {
		public final int input1ID;
		public final int input1Metadata;
		public final int input1MinCount;
		public final int input1MaxCount;

		public final int input2ID;
		public final int input2Metadata;
		public final int input2MinCount;
		public final int input2MaxCount;

		public final int resultID;
		public final int resultMetadata;
		public final int resultMinCount;
		public final int resultMaxCount;

		private boolean randomizeMeta1 = false;
		private int[] randomMetas1;
		private boolean randomizeMeta2 = false;
		private int[] randomMetas2;
		private boolean randomizeMetaResult = false;
		private int[] randomMetasResult;

		public WeightedMerchantRecipeEntry(int input1ID, int input1Metadata, int input1MinCount, int input1MaxCount,
				int input2ID, int input2Metadata, int input2MinCount, int input2MaxCount,
				int resultID, int resultMetadata, int resultMinCount, int resultMaxCount,
				float weight, int level) {
			super(weight, level);

			this.input1ID = input1ID;
			this.input1Metadata = input1Metadata;
			this.input1MinCount = input1MinCount;
			this.input1MaxCount = input1MaxCount;

			this.input2ID = input2ID;
			this.input2Metadata = input2Metadata;
			this.input2MinCount = input2MinCount;
			this.input2MaxCount = input2MaxCount;

			this.resultID = resultID;
			this.resultMetadata = resultMetadata;
			this.resultMinCount = resultMinCount;
			this.resultMaxCount = resultMaxCount;
		}

		public WeightedMerchantRecipeEntry(int inputID, int inputMetadata, int inputMinCount, int inputMaxCount,
				int resultID, int resultMetadata, int resultMinCount, int resultMaxCount,
				float weight, int level) {
			super(weight, level);

			this.input1ID = inputID;
			this.input1Metadata = inputMetadata;
			this.input1MinCount = inputMinCount;
			this.input1MaxCount = inputMaxCount;

			this.input2ID = 0;
			this.input2Metadata = 0;
			this.input2MinCount = 0;
			this.input2MaxCount = 0;

			this.resultID = resultID;
			this.resultMetadata = resultMetadata;
			this.resultMinCount = resultMinCount;
			this.resultMaxCount = resultMaxCount;
		}

		@Override
		public boolean equals(WeightedMerchantEntry entry) {
			if (entry instanceof WeightedMerchantRecipeEntry) {
				WeightedMerchantRecipeEntry recipeEntry = (WeightedMerchantRecipeEntry) entry;

				return this.input1ID == recipeEntry.input1ID &&
						this.input1Metadata == recipeEntry.input1Metadata &&
						this.input2ID == recipeEntry.input2ID &&
						this.input2Metadata == recipeEntry.input2Metadata &&
						this.resultID == recipeEntry.resultID &&
						this.resultMetadata == recipeEntry.resultMetadata;
			}
			else {
				return false;
			}
		}

		@Override
		public MerchantRecipe generateRecipe(Random rand) {
			int count1 = MathHelper.getRandomIntegerInRange(rand, input1MinCount, input1MaxCount);
			int count2 = MathHelper.getRandomIntegerInRange(rand, input2MinCount, input2MaxCount);
			int countResult = MathHelper.getRandomIntegerInRange(rand, resultMinCount, resultMaxCount);

			int meta1 = input1Metadata;
			int meta2 = input2Metadata;
			int metaResult = resultMetadata;

			if (randomizeMeta1) {
				meta1 = randomMetas1[rand.nextInt(randomMetas1.length)];
			}
			if (randomizeMeta2) {
				meta2 = randomMetas2[rand.nextInt(randomMetas2.length)];
			}
			if (randomizeMetaResult) {
				metaResult = randomMetasResult[rand.nextInt(randomMetasResult.length)];
			}

			ItemStack input1 = new ItemStack(Item.itemsList[input1ID], count1, meta1);

			ItemStack input2 = null;
			if (input2ID != 0)
				input2 = new ItemStack(Item.itemsList[input2ID], count2, meta2);

			ItemStack result = new ItemStack(Item.itemsList[resultID], countResult, metaResult);

			MerchantRecipe trade = new MerchantRecipe(input1, input2, result, level);

			if (this.isMandatory())
				trade.setMandatory();

			return trade;
		}

		public WeightedMerchantEntry setRandomMetas(int[] randomMetas, int slot) {
			switch (slot) {
			case 0:
				this.randomizeMeta1 = true;
				this.randomMetas1 = randomMetas;
				break;
			case 1:
				this.randomizeMeta2 = true;
				this.randomMetas2 = randomMetas;
				break;
			case 2:
				this.randomizeMetaResult = true;
				this.randomMetasResult = randomMetas;
				break;
			}

			return this;
		}

		@Override
		public boolean matchesMerchantRecipe(MerchantRecipe trade) {
			return trade.getItemToBuy().itemID == this.input1ID && 
					(trade.getItemToBuy().getItemDamage() == this.input1Metadata || this.randomizeMeta1) &&
					(!trade.hasSecondItemToBuy() || 
							(trade.getSecondItemToBuy().itemID == this.input2ID && 
							(trade.getSecondItemToBuy().getItemDamage() == this.input2Metadata || this.randomizeMeta2))) && 
					trade.getItemToSell().itemID == this.resultID && 
					(trade.getItemToSell().getItemDamage() == this.resultMetadata || this.randomizeMetaResult);
		}
	}

	public static abstract interface TradeEffect {
		public abstract void playEffect(VillagerEntity villager);
	}

	public static abstract interface TradeConditional {
		public abstract boolean shouldAddTrade(VillagerEntity villager);
	}

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void handleHealthUpdate(byte bUpdateType) {
		super.handleHealthUpdate(bUpdateType);

		if (bUpdateType == 14) {
			// item collect sound on villager restock
			worldObj.playSound(posX, posY, posZ, "random.pop", 
					0.25F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1F) * 2F);
		}
	}
}