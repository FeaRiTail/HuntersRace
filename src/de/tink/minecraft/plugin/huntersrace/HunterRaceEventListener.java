package de.tink.minecraft.plugin.huntersrace;

import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;


public class HunterRaceEventListener  implements Listener {
	private HuntersRacePlugin plugin;
	private Configuration pluginConfig;
	private Configuration playerConfig;
	
	public HunterRaceEventListener(HuntersRacePlugin plugin) {
		this.plugin = plugin;
		this.pluginConfig = plugin.getConfig();
		this.playerConfig = plugin.getPlayerConfig();
	}
	
	@EventHandler
	public void onMobKill(EntityDeathEvent deathEvent) {
		LivingEntity killedMob = deathEvent.getEntity();
		EntityType killedMobType = deathEvent.getEntityType();
		String killedMobTypeName = killedMobType.getName();
		Player player = killedMob.getKiller();
		if ( player == null || !plugin.raceIsRunning()) {
			return;
		}
		
		Integer mobsKilledByPlayer = playerConfig.getInt("hunters."+player.getName()+".mobs."+killedMobTypeName);
		Integer mobsKilledByPlayerOverall = playerConfig.getInt("hunters."+player.getName()+".mobs."+killedMobTypeName);
		if (mobsKilledByPlayer == null ) {
			mobsKilledByPlayer = 0;
		}
		if (mobsKilledByPlayerOverall == null ) {
			mobsKilledByPlayerOverall = 0;
		}
		mobsKilledByPlayer++;
		mobsKilledByPlayerOverall++;
		playerConfig.set("hunters."+player.getName()+".mobs."+killedMobTypeName,mobsKilledByPlayer);
		playerConfig.set("hunters."+player.getName()+".total_kills",mobsKilledByPlayerOverall);
		plugin.savePlayerConfig();
		plugin.reloadPlayerConfig();
	}

}
