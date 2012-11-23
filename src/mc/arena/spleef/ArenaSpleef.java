package mc.arena.spleef;

import mc.alk.arena.BattleArena;
import mc.alk.arena.util.Log;
import mc.arena.spleef.executors.ESpleefExecutor;
import mc.arena.spleef.executors.SpleefExecutor;
import mc.arena.spleef.util.WorldGuardUtil;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenaSpleef extends JavaPlugin {
	static ArenaSpleef plugin;  /// our plugin

	@Override
	public void onEnable(){
		plugin = this;
		/// We need worldguard/worldedit, load them in
		WorldGuardUtil.loadWorldGuardPlugin();

		/// Register our spleef match type
		BattleArena.registerMatchType(this, "Spleef", "spleef", SpleefArena.class, new SpleefExecutor());

		/// Register our spleef event type
		BattleArena.registerEventType(this, "ESpleef", "espleef", SpleefArena.class, new ESpleefExecutor());

		/// create our default config if it doesn't exist
		saveDefaultConfig();

		/// Read in our default spleef options
		FileConfiguration config = this.getConfig();
		Defaults.SUPERPICK = config.getBoolean("superpick", Defaults.SUPERPICK);
		Defaults.SUPERPICK_ITEM = config.getInt("superpick_item", Defaults.SUPERPICK_ITEM);
		Defaults.MAX_LAYERS = config.getInt("maxLayers", Defaults.MAX_LAYERS);
		Defaults.MAX_REGION_SIZE = config.getInt("maxRegionSize", Defaults.MAX_REGION_SIZE);

		Log.info("[" + getName()+ "] v" + getDescription().getVersion()+ " enabled!");
	}

	@Override
	public void onDisable(){
		Log.info("[" + getName()+ "] v" + getDescription().getVersion()+ " stopping!");
	}

	public static ArenaSpleef getSelf() {
		return plugin;
	}
}
