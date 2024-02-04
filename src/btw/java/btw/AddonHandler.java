// FCMOD

package btw;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import btw.client.fx.EffectHandler;
import btw.network.packet.handler.CustomPacketHandler;
import btw.world.biome.BiomeDecoratorBase;
import btw.world.util.WorldData;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft; // client only
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class AddonHandler
{
	public static Map<Class<? extends BTWAddon>, BTWAddon> modList = new LinkedHashMap<>();
	public static boolean modsInitialized = false;
	private static boolean loggerInitialized = false;

	public static final Logger logger;

	static {
		logger = Logger.getLogger("BetterThanWolves");
		logFileHandler = null;
	}

	private static File logFile;

	private static FileHandler logFileHandler;

	private static NetServerHandler netServerHandler;
	private static ArrayList<String> ackCheckFails = new ArrayList<String>();

	private static Map<String, CustomPacketHandler> packetHandlers = new HashMap<>();

	public static Map<Class<? extends BTWAddon>, WorldData> addonWorldDataMap;

	private static ArrayList<BTWAddon> loadOrder = new ArrayList<>();

	private static Class<?> classDomain = AddonHandler.class;
	
	public static ArrayList<ICommand> commandList = new ArrayList<>();

	public static void addMod(BTWAddon mod)
	{
		modList.put(mod.getClass(), mod);
	}

	public static URL getResource(URL url) {
		try {
			if (url.getPath().endsWith(".jar") || url.getPath().endsWith("zip")) {
				return new URL("jar:" + url + "!/");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

	public static boolean isFabricEnvironment() {
		return classDomain.getName().startsWith("net.fabricmc") || classDomain.getClassLoader().toString().contains("net.fabricmc");
	}

	public static void loadModClasses() {
		final ClassLoader classLoader = classDomain.getClassLoader();
		try {
			final File[] files = new File(logFile.getParentFile().getCanonicalPath() + "/mods").listFiles();
			final List<URL> mods = new ArrayList<URL>();
			if (files != null) {
				for (final File file : files) {
					if (file.isDirectory())
						mods.add(file.toURI().toURL());
					else {
						if (file.getPath().endsWith(".jar") || file.getPath().endsWith(".zip"))
							mods.add(getResource(file.toURI().toURL()));
					}
				}
			}

			List<String> classes = AddonFinder.getClasses(Collections.enumeration(mods));
			if (classes.size() > 0) {
				loadClasses(classes, mods.toArray(new URL[mods.size()]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadJarClasses() {
		final URL baseURL = getResource(classDomain.getProtectionDomain().getCodeSource().getLocation());
		logMessage("Minecraft jar found: " + baseURL.toString());

		try {
			List<String> classes = AddonFinder.getClasses(Collections.enumeration(Collections.singleton(baseURL)));
			loadClasses(classes, new URL[] { baseURL });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadClasses(List<String> classes, URL[] resources) {
		ClassLoader loader = classDomain.getClassLoader();

		for (String name : classes) {
			Class<?> current = null;

			try {
				current = loader.loadClass(name);
			} catch(Throwable e) {
				continue;
			}

			try {
				if (BTWAddon.class.isAssignableFrom(current)) {
					Class<? extends BTWAddon> addonClass = (Class<? extends BTWAddon>) Class.forName(name, true, loader);
					logMessage("Found Add-on: " + addonClass.getSimpleName());

					if (!modList.containsKey(addonClass)) {
						Constructor<? extends BTWAddon> constructor = addonClass.getDeclaredConstructor();
						constructor.setAccessible(true);
						constructor.newInstance();
					}
				}
			} catch(Throwable e) {}
		}
	}

	public static void loadAddOns() {
		final ClassLoader knot = classDomain.getClassLoader();
		logMessage("Classloader: " + knot.getClass().getName());
		
		// Should be impossible to get this far without Fabric anyway
		if (!isFabricEnvironment()) {
			throw new RuntimeException("Something went wrong, Fabric not found!");
		}
		
		loadJarClasses();
		loadModClasses();
	}

	public static void initializeMods()
	{
		if (!modsInitialized)
		{
			logMessage("...Add-On Handler Initializing...");

			loadAddOns();

			addonWorldDataMap = initWorldDataForAddon();

			preInitializeMods();

			loadModConfigs();
			
			BTWMod.instance.initialize();
			
			for (BTWAddon addon : modList.values()) {
				if (addon == BTWMod.instance) {
					continue;
				}
				
				addon.initialize();
			}

			postInitializeMods();

			modsInitialized = true;

			onLanguageLoaded(StringTranslate.getInstance());

			logMessage("...Add-On Handler Initialization Complete...");
		}
	}

	public static void initializeLogger()
	{
		if (loggerInitialized) {
			return;
		}
		try
		{
			if (MinecraftServer.getIsServer()) {
				logFile = new File(new File("."), "BTWLog.txt");
			}
			else {
				logFile = new File(Minecraft.getMinecraftDir(), "BTWLog.txt");
			}
			if ((logFile.exists() || logFile.createNewFile()) && logFile.canWrite())
			{
				logFileHandler = new FileHandler(logFile.getPath());
				logFileHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(logFileHandler);

				logger.setLevel(Level.FINE);
				loggerInitialized = true;
			}
		}
		catch (Throwable error)
		{
			throw new RuntimeException(error);
		}
	}

	public static void logMessage(String string)
	{
		System.out.println(string);

		if (net.minecraft.server.MinecraftServer.getServer() != null)
		{
			// client
			net.minecraft.server.MinecraftServer.getServer().getLogAgent().logInfo(string);
			// server
			//net.minecraft.server.MinecraftServer.getServer().getLogAgent().func_98233_a(string);
		}

		logger.fine(string);
	}

	public static void logWarning(String string)
	{
		System.out.println(string);

		if (net.minecraft.server.MinecraftServer.getServer() != null)
		{
			// client
			net.minecraft.server.MinecraftServer.getServer().getLogAgent().logWarning(string);
			// server
			//net.minecraft.server.MinecraftServer.getServer().getLogAgent().func_98236_b(string);
		}

		logger.fine(string);
	}

	public static void preInitializeMods() {
		BTWMod.instance.preInitialize();
		
		for (BTWAddon addon : modList.values()) {
			if (addon == BTWMod.instance) {
				continue;
			}
			
			addon.preInitialize();
		}
	}

	public static void postInitializeMods()
	{
		BTWMod.instance.postInitialize();
		
		for (BTWAddon addon : modList.values()) {
			if (addon == BTWMod.instance) {
				continue;
			}
			
			addon.postInitialize();
		}
	}

	public static void onLanguageLoaded(StringTranslate translator) {
		// only call on language loaded after mods have been initialized to prevent funkiness due to static instance variable of
		// StringTranslate creating ambiguous initialization order.
		if (modsInitialized) {
			BTWMod.instance.onLanguageLoaded(translator);
			String prefix = BTWMod.instance.getLanguageFilePrefix();
			
			if (prefix != null) {
				logMessage("...Add-On Handler Loading Custom Language File With Prefix: " + prefix + "..." );
				translator.loadAddonLanguageExtension(prefix);
			}
			
			for (BTWAddon addon : modList.values()) {
				if (addon == BTWMod.instance) {
					continue;
				}
				
				addon.onLanguageLoaded(translator);
				prefix = addon.getLanguageFilePrefix();
				
				if (prefix != null) {
					logMessage("...Add-On Handler Loading Custom Language File With Prefix: " + prefix + "..." );
					translator.loadAddonLanguageExtension(prefix);
				}
			}
		}

		if (modsInitialized) {
			Iterator<BTWAddon> modIterator = modList.values().iterator();

			while (modIterator.hasNext()) {
				BTWAddon mod = modIterator.next();

				mod.onLanguageLoaded(translator);

				String prefix = mod.getLanguageFilePrefix();

				if (prefix != null) {
					logMessage("...Add-On Handler Loading Custom Language File With Prefix: " + prefix + "..." );

					translator.loadAddonLanguageExtension(prefix);
				}
			}
		}
	}

	public static void serverCustomPacketReceived(NetServerHandler handler, Packet250CustomPayload packet)
	{
		if (packetHandlers.get(packet.channel) != null) {
			try {
				packetHandlers.get(packet.channel).handleCustomPacket(packet);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			Iterator<BTWAddon> modIterator = modList.values().iterator();

			while (modIterator.hasNext()) {
				BTWAddon mod = modIterator.next();

				if (mod.serverCustomPacketReceived(handler, packet)) {
					return;
				}
			}
		}
	}


	public static void serverPlayerConnectionInitialized(NetServerHandler serverHandler, EntityPlayerMP playerMP) {
		netServerHandler = serverHandler;

		BTWMod.instance.serverPlayerConnectionInitialized(serverHandler, playerMP);
		BTWMod.instance.sendVersionCheckToClient(serverHandler, playerMP);
		
		for (BTWAddon addon : modList.values()) {
			if (addon == BTWMod.instance) {
				continue;
			}
			
			addon.serverPlayerConnectionInitialized(serverHandler, playerMP);

			if (addon.shouldVersionCheck) {
				addon.sendVersionCheckToClient(serverHandler, playerMP);
			}
		}
		
		BTWMod.instance.sendDifficultyToClient(serverHandler, playerMP);
	}

	public static boolean getAwaitingLoginAck() {
		for (Object mod : AddonHandler.modList.values()) {
			if (((BTWAddon) mod).getAwaitingLoginAck()) {
				return true;
			}
		}

		return false;
	}

	public static void incrementTicksSinceAckRequested() {
		for (Object mod : AddonHandler.modList.values()) {
			((BTWAddon) mod).incrementTicksSinceAckRequested();
		}
	}

	public static void handleAckCheck() {
		for (Object mod : AddonHandler.modList.values()) {
			if (!((BTWAddon) mod).handleAckCheck()) {
				ackCheckFails.add(((BTWAddon) mod).getName());
			}
		}

		if (!ackCheckFails.isEmpty()) {
			String message = "WARNING: Client missing the following addons, or very high latency connection: ";

			for (int i = 0; i < ackCheckFails.size(); i++) {
				if (i > 0)
					message += ", ";
				message += ackCheckFails.get(i);
			}

			WorldUtils.sendPacketToPlayer(netServerHandler, new Packet3Chat(message));
		}
	}

	public static Map<Class<? extends BTWAddon>, WorldData> initWorldDataForAddon() {
		Map<Class<? extends BTWAddon>, WorldData> worldDataMap = new HashMap<>();

		for (Object mod : AddonHandler.modList.values()) {
			WorldData worldData = ((BTWAddon) mod).createWorldData();

			if (worldData != null) {
				worldDataMap.put(((BTWAddon) mod).getClass(), worldData);
			}
		}

		return worldDataMap;
	}

	public static void loadModConfigs() {
		for (Object mod : AddonHandler.modList.values()) {
			BTWAddon addon = (BTWAddon) mod;

			Map<String, String> addonConfigProperties = addon.loadConfigProperties();

			if (addonConfigProperties != null) {
				addon.repopulateConfigFile(addonConfigProperties);
				addon.handleConfigProperties(addonConfigProperties);
			}
		}
	}

	public static void registerPacketHandler(String channel, CustomPacketHandler handler) {
		packetHandlers.put(channel, handler);
	}
	
	public static void registerCommand(ICommand command, boolean clientOnly) {
		if (MinecraftServer.getIsServer()) {
			if (!clientOnly) {
				MinecraftServer.getServer().commandManager.registerCommand(command);
			}
		}
		else {
			commandList.add(command);
		}
	}

	public static WorldData getWorldDataForMod(Class<? extends BTWAddon> mod) {
		if (AddonHandler.addonWorldDataMap.containsKey(mod))
			return AddonHandler.addonWorldDataMap.get(mod);
		else
			return null;
	}

	public static void setWorldDataForMod(Class<? extends BTWAddon> mod, WorldData data) {
		AddonHandler.addonWorldDataMap.put(mod, data);
	}

	public static void decorateWorld(BiomeDecoratorBase decorator, World world, Random rand, int x, int y, BiomeGenBase biome) {
		for (Object mod : AddonHandler.modList.values()) {
			BTWAddon addon = (BTWAddon) mod;

			addon.decorateWorld(decorator, world, rand, x, y, biome);
		}
	}

	public static boolean isModInstalled(String name) {
		for (BTWAddon mod : modList.values()) {
			if (mod.getName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	public static BTWAddon getModByName(String name) {
		for (BTWAddon mod : modList.values()) {
			if (mod.getName().equals(name)) {
				return mod;
			}
		}

		return null;
	}

	public static enum DependencyType {
		LOAD_BEFORE,
		LOAD_AFTER,
		INCOMPATIBLE,
		NO_DEPENDENCY
	}

	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    public static void clientCustomPacketReceived(Minecraft mcInstance, Packet250CustomPayload packet)
	{
		if (packetHandlers.get(packet.channel) != null) {
			try {
				packetHandlers.get(packet.channel).handleCustomPacket(packet);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			Iterator<BTWAddon> modIterator = modList.values().iterator();

			while (modIterator.hasNext()) {
				BTWAddon mod = modIterator.next();

				if (packet.channel == mod.addonCustomPacketChannelVersionCheck && mod.shouldVersionCheck && mod.versionCheckPacketReceived(mcInstance, packet)) {
					return;
				} else if (mod.clientCustomPacketReceived(mcInstance, packet)) {
					return;
				}
			}
		}
	}

    @Environment(EnvType.CLIENT)
    public static void clientPlayCustomAuxFX(Minecraft mcInstance, World world, EntityPlayer player, int iFXID, int i, int j, int k, int iFXSpecificData)
	{
		if (!EffectHandler.playEffect(iFXID, mcInstance, world, player, i, j, k, iFXSpecificData)) {
			Iterator<BTWAddon> modIterator = modList.values().iterator();

			while (modIterator.hasNext()) {
				BTWAddon mod = modIterator.next();

				if (mod.clientPlayCustomAuxFX(mcInstance, world, player, iFXID, i, j, k, iFXSpecificData)) {
					return;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static boolean interceptCustomClientPacket(Minecraft mc, Packet250CustomPayload packet) {
		for (Object mod : AddonHandler.modList.values()) {
			if (((BTWAddon) mod).interceptCustomClientPacket(mc, packet)) {
				return true;
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	public static EntityFX spawnCustomParticle(World world, String particleType, double x, double y, double z, double velX, double velY, double velZ) {
		for (Object mod : AddonHandler.modList.values()) {
			EntityFX particle = ((BTWAddon) mod).spawnCustomParticle(world, particleType, x, y, z, velX, velY, velZ);
			if (particle != null)
				return particle;
		}

		return null;
	}
}
