package net.minecraft.src;

import java.util.List;

import btw.world.util.difficulty.Difficulties;
import btw.world.util.difficulty.Difficulty;
import net.minecraft.server.MinecraftServer;

public class CommandDifficulty extends CommandBase {
	@Override
	public String getCommandName() {
		return "difficulty";
	}
	
	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return commandSender.translateString("commands.difficulty.usage", new Object[0]);
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender commandSender) {
		return MinecraftServer.getServer().isSinglePlayer() || super.canCommandSenderUseCommand(commandSender);
	}
	
	@Override
	public void processCommand(ICommandSender commandSender, String arguments[]) {
		if (arguments.length == 0) {
			commandSender.sendChatToPlayer("\247e" + // yellow text
					StringTranslate.getInstance().translateKey("commands.difficulty.current") + " " +
					MinecraftServer.getServer().worldServers[0].getDifficulty().getLocalizedName());
		}
		else if (arguments.length == 1) {
			Difficulty newDifficulty = Difficulties.getDifficultyFromName(arguments[0]);
			
			if (newDifficulty == null) {
				throw new WrongUsageException("commands.difficulty.unknown");
			}
			else {
				MinecraftServer.getServer().setDifficultyForAllWorlds(newDifficulty);
				commandSender.sendChatToPlayer("\247e" + // yellow text
						StringTranslate.getInstance().translateKey("commands.difficulty.set") + " " +
						MinecraftServer.getServer().worldServers[0].getDifficulty().getLocalizedName());
				
				MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(Difficulties.createDifficultyPacket(MinecraftServer.getServer()));
			}
		}
		else {
			throw new WrongUsageException("commands.difficulty.usage");
		}
	}
	
	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
		return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, Difficulties.getAllDifficultyNames()) : null;
	}
}
