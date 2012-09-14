package mc.arena.spleef;

import mc.alk.arena.BattleArena;
import mc.alk.arena.util.Log;
import mc.arena.spleef.util.WorldGuardUtil;

import org.bukkit.plugin.java.JavaPlugin;

public class ArenaSpleef extends JavaPlugin {
	static ArenaSpleef plugin;
	
	@Override
	public void onEnable(){	
		plugin = this;
		BattleArena.registerMatchType(this, "Spleef", "spleef", SpleefArena.class, new SpleefExecutor());
		
		WorldGuardUtil.loadWorldGuardPlugin();
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
