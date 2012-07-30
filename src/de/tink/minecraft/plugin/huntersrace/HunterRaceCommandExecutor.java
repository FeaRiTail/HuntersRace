package de.tink.minecraft.plugin.huntersrace;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;

public class HunterRaceCommandExecutor implements org.bukkit.command.CommandExecutor {
	private HuntersRacePlugin plugin;
	private Configuration pluginConfig;
	private Configuration playerConfig;
	
	public HunterRaceCommandExecutor(HuntersRacePlugin plugin) {
		this.plugin = plugin;
		this.pluginConfig = plugin.getConfig();
		this.playerConfig = plugin.getPlayerConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!command.getName().equalsIgnoreCase("huntersrace")) {
			return false;
		}
		
		
		pluginConfig = plugin.getConfig();
		playerConfig = plugin.getPlayerConfig();
		boolean raceIsRunning = plugin.raceIsRunning();

		// Start Hunting race
		if (args != null && args.length == 1 && "start".equals(args[0]) && !raceIsRunning ) {
			plugin.startCountdown();
			return true;
		} else if (args != null && args.length == 1 && "start".equals(args[0]) && raceIsRunning ) {
			sender.sendMessage("A race is already running!");
			return true;
		}
		
		// Stop Hunting race
		if (args != null && args.length == 1 && "stop".equals(args[0]) && raceIsRunning ) {
			plugin.stopRace();
			return true;
		} else if (args != null && args.length == 1 && "start".equals(args[0]) && !raceIsRunning ) {
			sender.sendMessage("There is no race to stop!");
			return true;
		} 
		
		return true;
	}
}
