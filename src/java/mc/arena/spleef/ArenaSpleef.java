package mc.arena.spleef;

import mc.alk.arena.BattleArena;
import mc.alk.arena.util.Log;
import mc.alk.arena.util.WorldGuardUtil;
import mc.arena.spleef.executors.SpleefExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenaSpleef extends JavaPlugin {
	static ArenaSpleef plugin;  /// our plugin
    static final int bukkitID = 44785; /// https://api.curseforge.com/servermods/projects?search=arenaspleef

    @Override
	public void onEnable(){
		plugin = this;
		/// We need worldguard/worldedit, load them in
		if (!WorldGuardUtil.hasWorldGuard){
			Log.err("Arena Spleef needs WorldEdit and WorldGuard to function!");
			setEnabled(false);
			return;
		}

		/// Register our spleef type
		BattleArena.registerCompetition(this, "Spleef", "spleef", SpleefArena.class, new SpleefExecutor());

		/// Load the Config
		loadConfig();
//        try{
//            BattleArena.update(this, bukkitID, getFile(),
//                    UpdateOption.fromString(getConfig().getString("autoUpdate")),
//                    AnnounceUpdateOption.NONE);
//        } catch(Throwable e){/* do nothing */}
		Log.info("[" + getName()+ "] v" + getDescription().getVersion()+ " enabled!");
	}

	@Override
	public void onDisable(){
		Log.info("[" + getName()+ "] v" + getDescription().getVersion()+ " stopping!");
	}

	@Override
	public void reloadConfig(){
		super.reloadConfig();
		loadConfig();
	}

	public static ArenaSpleef getSelf() {
		return plugin;
	}

	public void loadConfig(){
		/// create our default config if it doesn't exist
		saveDefaultConfig();

		/// Read in our default spleef options
		FileConfiguration config = this.getConfig();
		Defaults.SUPERPICK = config.getBoolean("superpick", Defaults.SUPERPICK);
		Defaults.SUPERPICK_ITEM = config.getInt("superpick_item", Defaults.SUPERPICK_ITEM);
		Defaults.MAX_LAYERS = config.getInt("maxLayers", Defaults.MAX_LAYERS);
		Defaults.MAX_REGION_SIZE = config.getInt("maxRegionSize", Defaults.MAX_REGION_SIZE);
		Defaults.STOP_BLOCKBREAK_DROPS = config.getBoolean("noBlockDrops", Defaults.STOP_BLOCKBREAK_DROPS);
		Defaults.HEIGHT_LOSS = config.getBoolean("heightLoss", Defaults.HEIGHT_LOSS);

        if (config.getBoolean("islanding.enable", true)){
            SpleefArena.ISLAND_FAILS = config.getInt("islanding.fails", SpleefArena.ISLAND_FAILS);
            SpleefArena.ISLAND_RADIUS = config.getInt("islanding.radius", SpleefArena.ISLAND_RADIUS);
        } else {
            SpleefArena.ISLAND_FAILS = -1;
        }


	}
}
