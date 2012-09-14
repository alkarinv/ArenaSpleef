package mc.arena.spleef;

import java.util.ArrayList;
import java.util.List;

import mc.alk.arena.Defaults;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.events.MatchEventHandler;
import mc.alk.arena.serializers.Persist;
import mc.alk.arena.util.Log;
import mc.arena.spleef.util.WorldGuardUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.block.BlockBreakEvent;

import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class SpleefArena extends Arena{
	@Persist
	String worldName; /// What world this spleef is in

	@Persist
	String layerName; /// What is our layer name

	@Persist
	int type; /// what type to change the blocks into

	@Persist
	int data; /// what data value to change the blocks into

	Integer timerid = null;  /// our local bukkit timer, for regening the spleef layer

	ProtectedRegion pr; /// Our protected region layer

	@Override
	public void onOpen(){
		cancelTimer();
		initLayers();
	}

	@Override
	public void onStart(){
		final World w = Bukkit.getWorld(worldName);
		/// Set the timer to regen the blocks if someone has walled themselves off, or people cant reach each other
		timerid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(ArenaSpleef.getSelf(), new Runnable(){
			public void run() {
				try { 
					WorldGuardUtil.setBlocks(w, layerName, type, data);
				} catch (Exception e) {e.printStackTrace();}
			}
		}, (long) (mc.arena.spleef.Defaults.SECONDS_TO_REGEN*20L*Defaults.TICK_MULT),
		(long) (mc.arena.spleef.Defaults.SECONDS_TO_REGEN*20L*Defaults.TICK_MULT));
	}

	public void initLayers(){
		if (layerName == null)
			return;
		World w = Bukkit.getWorld(worldName);
		if (w == null){
			Log.err("[ArenaSpleef] worldName was null in arena " + getName());
			return;
		}
		try {
			pr = WorldGuardUtil.setBlocks(w,layerName,type,data);
		} catch (Exception e) {
			Log.err(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onFinish(){
		World w = Bukkit.getWorld(worldName);
		try {
			WorldGuardUtil.setBlocks(w, layerName, type, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cancelTimer();
	}

	@MatchEventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Location l = event.getBlock().getLocation();
		if (pr.contains(l.getBlockX(), l.getBlockY(),l.getBlockZ())){
			event.setCancelled(false);
		}
	}

	@Override
	public boolean valid(){
		return super.valid() && layerName != null && worldName != null && 
				Bukkit.getWorld(worldName)!=null && WorldGuardUtil.hasRegion(Bukkit.getWorld(worldName), getRegionName());
	}

	@Override
	public List<String> getInvalidReasons(){
		List<String> reasons = new ArrayList<String>();
		if (layerName == null || layerName.isEmpty()){
			reasons.add("ArenaSpleef arena needs a floor region, and none is defined!");
		}
		if (worldName == null || worldName.isEmpty() || Bukkit.getWorld(worldName)==null){
			reasons.add("ArenaSpleef lost its region, please reselect it");
		}
		if (!WorldGuardUtil.hasRegion(Bukkit.getWorld(worldName), getRegionName())){
			reasons.add("ArenaSpleef lost its region, please reselect it");
		}
		reasons.addAll(super.getInvalidReasons());
		return reasons;
	}


	private void cancelTimer() {
		if (timerid != null){
			Bukkit.getScheduler().cancelTask(timerid);
			timerid = null;
		}
	}

	public void setRegionType(BaseBlock block) {
		type = block.getType();
		data = block.getData();
	}

	private String getRegionName() {
		return "ba-spleef-"+getName();
	}

	public void setRegion(Selection sel) throws Exception {
		WorldGuardUtil.addRegion(sel, getRegionName());
		layerName = getRegionName();
		worldName = sel.getWorld().getName();
	}
}