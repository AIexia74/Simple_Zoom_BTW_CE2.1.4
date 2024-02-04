// FCMOD

package btw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import btw.network.packet.handler.CustomPacketHandler;
import btw.world.biome.BiomeDecoratorBase;
import btw.world.util.WorldData;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public abstract class BTWAddon {
	protected String addonName;
	protected String versionString;
	protected String prefix;

	protected boolean isRequiredClientAndServer = false;
	protected boolean shouldVersionCheck = false;

	private boolean awaitingLoginAck = false;
	private int ticksSinceAckRequested = 0;
	private static final int MAX_TICKS_FOR_ACK_WAIT = 50;

	public String addonCustomPacketChannelVersionCheck;
	public String addonCustomPacketChannelVersionCheckAck;

	private ArrayList<String> configProperties = new ArrayList<>();
	private Map<String, String> configPropertyDefaults = new HashMap<>();
	private Map<String, String> configPropertyComments = new HashMap<>();
	
	private Map<BTWAddon, AddonHandler.DependencyType> dependencyList = new HashMap<>();
	
	//Used for dependency mapping, should not be otherwise touched
	public final Set<BTWAddon> dependants = new HashSet<>();
	public boolean hasBeenInitialized = false;

	protected BTWAddon() {
		AddonHandler.addMod(this);
	}

	/**
	 * @param addonName Used for display in version checking
	 * @param versionString Used for version checking
	 * @param prefix Used for translations and packet channels
	 */
	public BTWAddon(String addonName, String versionString, String prefix) {
		this();
		this.addonName = addonName;
		this.versionString = versionString;
		this.prefix = prefix;
		this.addonCustomPacketChannelVersionCheck = prefix + "|VC";
		this.addonCustomPacketChannelVersionCheckAck = prefix + "|VC_Ack";
		this.shouldVersionCheck = true;
		this.isRequiredClientAndServer = true;
	}

	//------ Override Methods ------//
	/*
	 * These are methods which should be overridden 
	 * to add functionality to your addon
	 */
	
	public void preInitialize() {}

	public abstract void initialize();

	public void postInitialize() {}

	public void onLanguageLoaded(StringTranslate translator) {}

	/**
	 * Where the addon should handle the values read from config files
	 * This is called after Initialize() is called
	 * @param propertyValues Key-value pair for each property which has been registered
	 */
	public void handleConfigProperties(Map<String, String> propertyValues) {}
	
	/**
	 * Called when a player joins the world
	 */
	public void serverPlayerConnectionInitialized(NetServerHandler serverHandler, EntityPlayerMP playerMP) {}

	/**
	 * Deprecated - please register custom packet handlers instead
	 * @return true if the packet has been processed, false otherwise
	 */
	@Deprecated
	public boolean serverCustomPacketReceived(NetServerHandler handler, Packet250CustomPayload packet) {
		return false;
	}

	/**
	 * Used for storing custom information in the save file
	 * Has hooks to save information per dimension or globally
	 * @return
	 */
	public WorldData createWorldData() {
		return null;
	}
	
	/**
	 * Called when decorating a chunk (adding trees, ores, etc) to allow addons to add their own generators
	 * Look in BiomeDecorator for guidance in how to create and use a generator for those things
	 * @param decorator The decorator instance
	 * @param world The current world
	 * @param rand The random number generator. Always use this generator for deterministic generation.
	 * @param x 
	 * @param y
	 * @param biome The biome being decorated. Biomes during decoration are lower resolution, only being caluclated per chunk not per block
	 */
	public void decorateWorld(BiomeDecoratorBase decorator, World world, Random rand, int x, int y, BiomeGenBase biome) {}
	
	//------ API Methods ------//
	/*
	 * These are methods which should be called from
	 * your addon to define behavior
	 */

	/**
	 * Registers a property to load via the config
	 * This should be done either PreInitialize() as properties are read between pre-initialization and initialization
	 * @param propertyName The name for the property
	 * @param defaultValue The default value. If the config file cannot be found, one will be generated and populated with the provided default
	 * @param comment Comment to show for the property (optional through overload)
	 */
	protected void registerProperty(String propertyName, String defaultValue, String comment) {
		if (!configPropertyDefaults.containsKey(propertyName)) {
			configProperties.add(propertyName);
			configPropertyDefaults.put(propertyName, defaultValue);
			configPropertyComments.put(propertyName, comment);
		}
		else {
			AddonHandler.logWarning("Cannot add config property \"" + propertyName + "\" for " + addonName + " because a property with that name has already been registered for this addon");
		}
	}
	
	protected void registerProperty(String propertyName, String defaultValue) {
		this.registerProperty(propertyName, defaultValue, "");
	}

	/**
	 * Add a custom handler for packets, assigned to a specific channel
	 * @param channel Channel must be of the format "<addon prefix>|<channel string>"
	 * @param handler
	 */
	public void registerPacketHandler(String channel, CustomPacketHandler handler) {
		if (!channel.startsWith(this.prefix + "|")) {
			throw new IllegalArgumentException("Channel must be of the format \"<addon prefix>|<channel string>\"");
		}

		AddonHandler.registerPacketHandler(channel, handler);
	}

	/**
	 * Register a command for both client and server
	 * @param command
	 */
	public void registerAddonCommand(ICommand command) {
		AddonHandler.registerCommand(command, false);
	}

	public String getName() {
		return this.addonName;
	}

	public String getVersionString() {
		return this.versionString;
	}

	//------ Internal Methods ------//
	/*
	 * These methods are used for internal processing and should
	 * not be touched unless you know what you're doing
	 */
	
	/**
	 * Loads the registered properties
	 * Addons should not need to override this but may if they would like to implement custom property handling
	 * @return A map of the property names paired with the value read from them. Returns null if no properties were registered or if there was a problem loading the file
	 */
	public Map<String, String> loadConfigProperties() {
		if (this.configPropertyDefaults.size() == 0) {
			return null;
		}
		
		String filename = this.prefix + ".properties";
		BufferedReader fileIn = null;

		try {
			fileIn = new BufferedReader(new FileReader("config/" + filename));
		} catch (FileNotFoundException e) {
			//Generate file if it does not exist
			try {
				Files.createDirectories(Paths.get("config"));

				File config = new File("config/" + filename);
				config.createNewFile();
				fileIn = new BufferedReader(new FileReader(config));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if (fileIn != null) {
			Map<String, String> propertyValues = new HashMap<>();

			String line;
			
			//Reads properties from the config file
			try {
				while ((line = fileIn.readLine()) != null) {
					if (line.startsWith("#"))
						continue;
					
					String[] lineSplit = line.split("=");
					
					if (configPropertyDefaults.containsKey(lineSplit[0])) {
						propertyValues.put(lineSplit[0], lineSplit[1]);
					}
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
			for (String propertyName : configProperties) {
				if (!propertyValues.containsKey(propertyName)) {
					propertyValues.put(propertyName, configPropertyDefaults.get(propertyName));
				}
			}

			try {
				fileIn.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
			return propertyValues;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Rewrites the existing config file to account for missing options
	 * Does a complete rewrite instead of appending in order to maintain option order
	 */
	public void repopulateConfigFile(Map<String, String> propertyValues) {
		String filename = this.prefix + ".properties";
		File config = new File("config/" + filename);
		
		try {
			BufferedWriter writer = Files.newBufferedWriter(config.toPath(), StandardOpenOption.TRUNCATE_EXISTING);
			
			for (String propertyName : this.configProperties) {
				String comment = configPropertyComments.get(propertyName);
				
				if (!comment.equals("")) {
					writer.write("\n# " + comment + "\n");
				}
				
				String propertyValue;
				
				if (propertyValues.containsKey(propertyName)) {
					propertyValue = propertyValues.get(propertyName); 
				}
				else {
					propertyValue = configPropertyDefaults.get(propertyName);
				}
				
				writer.write(propertyName + "=" + propertyValue + "\n");
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles version checking
	 */
	public void sendVersionCheckToClient(NetServerHandler serverHandler, EntityPlayerMP playerMP) {
		if (!MinecraftServer.getServer().isSinglePlayer()) {
			WorldUtils.sendPacketToPlayer(serverHandler, new Packet3Chat("\u00a7f" + addonName + " V" + versionString));

			if (shouldVersionCheck) {
				ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
				DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);

				try {
					dataOutput.writeUTF(versionString);
				}
				catch (Exception var9) {
					var9.printStackTrace();
				}

				Packet250CustomPayload var4 = new Packet250CustomPayload(addonCustomPacketChannelVersionCheck, byteArrayOutput.toByteArray());
				WorldUtils.sendPacketToPlayer(serverHandler, var4);
				awaitingLoginAck = true;
			}
		}
		else {
			WorldUtils.sendPacketToPlayer(serverHandler, new Packet3Chat("\u00a7f" + addonName + " V" + versionString));
		}
	}
	
	/**
	 * Handles new difficulty system
	 */
	public void sendDifficultyToClient(NetServerHandler serverHandler, EntityPlayerMP playerMP) {
		WorldUtils.sendPacketToPlayer(serverHandler, Difficulties.createDifficultyPacket(serverHandler.mcServer));
	}

	/**
	 * Called when client ack packet is received by the NetServerHandler
	 * If overriding, make sure to make a call to the super method if you want to maintain version checking
	 * @return true if packet was handled, false otherwise
	 */
	public boolean serverAckPacketReceived(NetServerHandler serverHandler, Packet250CustomPayload packet) {
		if (addonCustomPacketChannelVersionCheckAck.equals(packet.channel)) {
			WorldUtils.sendPacketToPlayer(serverHandler, new Packet3Chat("\u00a7f" + addonName + " version check successful."));
			awaitingLoginAck = false;
			ticksSinceAckRequested = 0;
		}

		return false;
	}

	/**
	 * @return whether the server is awaiting the client's response to the version check
	 */
	public boolean getAwaitingLoginAck() {
		return awaitingLoginAck;
	}

	public void incrementTicksSinceAckRequested() {
		ticksSinceAckRequested++;
	}

	public boolean handleAckCheck() {
		if (ticksSinceAckRequested > MAX_TICKS_FOR_ACK_WAIT) {
			awaitingLoginAck = false;
			ticksSinceAckRequested = 0;
			return false;
		}

		return true;
	}

	public String getLanguageFilePrefix() {
		return this.prefix;
	}

	//----------- Client Side Functionality -----------//

	//------ Override Methods ------//
	/*
	 * These are methods which should be overridden 
	 * to add functionality to your addon
	 */

	/**
	 * Deprecated - please register custom packet handlers instead
	 * @return true if the packet has been processed, false otherwise
	 */
    @Environment(EnvType.CLIENT)
	@Deprecated
    public boolean clientCustomPacketReceived(Minecraft mcInstance, Packet250CustomPayload packet) {
		return false;
	}

	/**
	 * Used to modify existing client side packet250 behavior (For modifying BTW behavior)
	 * @return true if packet was handled, false otherwise
	 */
    @Environment(EnvType.CLIENT)
    public boolean interceptCustomClientPacket(Minecraft mc, Packet250CustomPayload packet) {
		return false;
	}

	/**
	 * Deprecated - please register custom effects using EffectHandler instead
	 * @return true if the packet has been processed, false otherwise
	 */
    @Environment(EnvType.CLIENT)
	@Deprecated
    public boolean clientPlayCustomAuxFX(Minecraft mcInstance, World world, EntityPlayer player, int iFXID, int i, int j, int k, int iFXSpecificData) {
		return false;
	}

	/**
	 * Spawns a custom particle based on a string specifying the type
	 * @return the spawned particle, or null if type is not handled
	 */
    @Environment(EnvType.CLIENT)
    public EntityFX spawnCustomParticle(World world, String particleType, double x, double y, double z, double velX, double velY, double velZ) {
		return null;
	}

	//------ API Methods ------//
	/*
	 * These are methods which should be called from
	 * your addon to define behavior
	 */

	/**
	 * Register a client only command
	 * @param command
	 */
	@Environment(EnvType.CLIENT)
	public void registerAddonCommandClientOnly(ICommand command) {
		AddonHandler.registerCommand(command, false);
	}

	//------ Internal Methods ------//
	/*
	 * These methods are used for internal processing and should
	 * not be touched unless you know what you're doing
	 */

	/**
	 * Called when the version check packet is received by the NetClientHandler
	 * If overriding, make sure to make a call to the super method
	 * @return true if packet was handled, false otherwise
	 */
    @Environment(EnvType.CLIENT)
    public boolean versionCheckPacketReceived(Minecraft mc, Packet250CustomPayload packet) {
		try {
			WorldClient world = mc.theWorld;
			DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));

			String var33 = dataStream.readUTF();

			if (!var33.equals(versionString)) {
				mc.thePlayer.addChatMessage("\u00a74" + "WARNING: " + this.getName() + " version mismatch detected! Local Version: " + this.versionString + " Server Version: " + var33);
			}

			ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
			DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);

			try {
				dataOutput.writeUTF(versionString);
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			// TODO: Re-enable ack packet checking
			//Packet250CustomPayload ackPacket = new Packet250CustomPayload(addonCustomPacketChannelVersionCheckAck, byteArrayOutput.toByteArray());
			//mc.getNetHandler().addToSendQueue(ackPacket);

			mc.thePlayer.addChatMessage("\u00a7f" + addonName + " version check successful.");

			return true;
		}
		catch (IOException var23) {
			var23.printStackTrace();
		}

		return false;
	}

	//----------- Server Side Functionality -----------//

	//------ API Methods ------//
	/*
	 * These are methods which should be called from
	 * your addon to define behavior
	 */

	/**
	 * Register a server only command
	 * @param command
	 */
	@Environment(EnvType.SERVER)
	public void registerAddonCommandServerOnly(ICommand command) {
		if (MinecraftServer.getIsServer()) {
			MinecraftServer.getServer().commandManager.registerCommand(command);
		}
	}
}
