package de.tink.minecraft.plugin.huntersrace;

/*
Copyright (C) 2012 Thomas Starl

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HuntersRacePlugin extends JavaPlugin {
	private HunterRaceCommandExecutor hunterRaceCommmandExecutor;
	private HunterRaceEventListener hunterRaceEventListener;
	private static String playerConfigFileName = "playerData.yml";
	private FileConfiguration playerConfig = null;
	private File playerConfigFile = null;
	private int stopper;
	private int initWarmup = 10;
	private int warmUp = initWarmup;
	private String HUNTERSRACE_START_RACE_COUNTDOWN = "A new hunting-race will start! In...";
	private String HUNTERSRACE_START_RACE = "Hunting-race has begun!";
	
	@Override
	public void onDisable() {
		getLogger().info("Disabling: "+this.toString());
	}

	@Override
	public void onEnable() {
        saveDefaultConfig();
        getPlayerConfig();
        savePlayerConfig();
        getLogger().info(this.toString() + " has been enabled.");
        hunterRaceCommmandExecutor = new HunterRaceCommandExecutor(this);
        hunterRaceEventListener = new HunterRaceEventListener(this);
		getCommand("huntersrace").setExecutor(hunterRaceCommmandExecutor);
		getServer().getPluginManager().registerEvents(hunterRaceEventListener, this);
	}

	public void startCountdown() {
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN + HUNTERSRACE_START_RACE_COUNTDOWN);
		stopper = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		public void run() {
			if (warmUp > 0) {
				Bukkit.getServer().broadcastMessage(ChatColor.GREEN + (Integer.valueOf(warmUp)).toString());
				warmUp--;
			} else {
				Bukkit.getServer().broadcastMessage(ChatColor.GREEN + HUNTERSRACE_START_RACE);
				startRace();
			}
		}
		}, 0L, 20L);
	}

	public void startRace() {
		this.getServer().getScheduler().cancelTask(this.stopper);
		warmUp = initWarmup;
		this.setRunning("true");
	}
	
	public void stopRace() {
		List<Player> topPerformer = this.getTopPerformerForRace();
		// top 3 players
		for ( int i = 1; i <= 3; i++ ) {
			if ( topPerformer.get(i) != null ) {
				this.announce("#"+i+": "+topPerformer.get(i));
			}
		}
		this.setRunning("false");
	}
	
	public void setRunning(String runState) {
		Configuration config = getConfig();
		config.set("race_is_running",runState);
		saveConfig();
		reloadConfig();
	}
	
	public boolean raceIsRunning() {
		Configuration config = getConfig();
		String isRunning = config.getString("race_is_running");
		if ( isRunning == null || "false".equals(isRunning) ) {
			return false;
		}
		return true;
	}

	public void announce(String message) {
		this.getServer().broadcastMessage(message);
	}

	
	// Needs to return a sorted array.. best players first
	private List<Player> getTopPerformerForRace() {
		HashMap<String,Integer> performer = new HashMap<String,Integer>();
		Set<String> players = getConfig().getConfigurationSection("hunters").getKeys(false);
		for ( String playerName : players ) {
			Integer playerKills = getConfig().getInt("hunters."+playerName+".total_kills");
			performer.put(playerName, playerKills);
		}
		List<Player> topPerformer = new ArrayList<Player>();
		return topPerformer;		
	}	
	
	public void reloadPlayerConfig() {
        if (playerConfigFile == null) {
        	playerConfigFile = new File(getDataFolder(), playerConfigFileName);
        }
        playerConfig = YamlConfiguration.loadConfiguration(playerConfigFile);
        // Look for defaults in the jar
        InputStream playerConfigStream = this.getResource(playerConfigFileName);
        if (playerConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(playerConfigStream);
            playerConfig.setDefaults(defConfig);
        }
    }
	
	public FileConfiguration getPlayerConfig() {
        if (playerConfig == null) {
            this.reloadPlayerConfig();
        }
        return playerConfig;
    }
	
	
	public void savePlayerConfig() {
        if (playerConfig == null || playerConfigFile == null) {
        return;
        }
        try {
        	getPlayerConfig().save(playerConfigFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + playerConfigFile, ex);
        }
    }

}
