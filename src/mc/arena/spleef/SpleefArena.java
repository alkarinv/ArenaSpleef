package mc.arena.spleef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.events.MatchEventHandler;
import mc.alk.arena.serializers.Persist;
import mc.alk.arena.util.Log;
import mc.arena.spleef.util.WorldGuardUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class SpleefArena extends Arena{
	public static boolean superpick = false;
	public static int superpick_item = 284;

	@Persist
	String worldName; /// What world this spleef is in

	@Persist
	List<String> layerNames; /// What are our layer names

	@Persist
	Map<String, Integer> regenTimes;

	Map<String, Integer> regenTimers; /// list of regen timers

	List<ProtectedRegion> regions; /// Our protected region layers

	World world; /// what world our spleef is in

	@Override
	public void onOpen(){
		cancelTimers();
		world = Bukkit.getWorld(worldName);

		if (world == null){
			Log.err("[ArenaSpleef] worldName was null in arena " + getName());
			getMatch().cancelMatch();
			return;
		}
		initLayers();
	}

	@Override
	public void onStart(){
		initLayers();
	}

	public void initLayers(){
		if (layerNames == null || layerNames.isEmpty())
			return;
		regions = new CopyOnWriteArrayList<ProtectedRegion>();
		for (String layerName: layerNames){
			ProtectedRegion pr = WorldGuardUtil.getRegion(world, layerName);
			if (regenTimes != null && regenTimes.containsKey(layerName)){
				startRegenTimer(pr, regenTimes.get(layerName));}

			regions.add(pr);
		}
		regenLayers();
	}

	private void startRegenTimer(final ProtectedRegion pr, final Integer time) {
		if (regenTimers == null)
			regenTimers = new ConcurrentHashMap<String, Integer>();
		Integer timerid = regenTimers.remove(pr.getId());
		if (timerid != null){
			Bukkit.getScheduler().cancelTask(timerid);}
		// Set the timer to regenLayerTimes the blocks if someone has walled themselves off, or people cant reach each other
		timerid = Bukkit.getScheduler().scheduleSyncRepeatingTask(ArenaSpleef.getSelf(), new Runnable(){
			public void run() {
				regenLayer(pr);
			}
		}, (long) (time*20L*mc.alk.arena.Defaults.TICK_MULT),
		(long) (time*20L*mc.alk.arena.Defaults.TICK_MULT));
		regenTimers.put(pr.getId(), timerid);
	}

	public void regenLayers(){
		for (ProtectedRegion pr: regions){
			regenLayer(pr);}	
	}

	void regenLayer(ProtectedRegion pr){
		try {
			//WorldGuardUtil.setBlocks(w, layerName, type, data);
			WorldGuardUtil.pasteSchematic(Bukkit.getConsoleSender(), pr, pr.getId(),world);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFinish(){
		regenLayers();
		cancelTimers();
	}

	@MatchEventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Location l = event.getBlock().getLocation();
		for (ProtectedRegion pr: regions){
			if (pr.contains(l.getBlockX(), l.getBlockY(),l.getBlockZ())){
				event.setCancelled(false);}			
		}
	}

	@MatchEventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if (!superpick || event.getAction() != Action.LEFT_CLICK_BLOCK || 
				!superPickItem(event.getPlayer().getItemInHand()))
			return;
		Location l = event.getClickedBlock().getLocation();
		for (ProtectedRegion pr: regions){
			if (pr.contains(l.getBlockX(), l.getBlockY(),l.getBlockZ())){
				event.setCancelled(true);
				event.getClickedBlock().setType(Material.AIR);
			}			
		}
	}

	private boolean superPickItem(ItemStack item) {
		if (item == null)
			return false;
		return item.getTypeId() == superpick_item;
	}

	@Override
	public boolean valid(){
		boolean success = super.valid() && layerNames != null && !layerNames.isEmpty() 
				&& worldName != null && Bukkit.getWorld(worldName)!=null;
		if (!success)
			return false;
		for (int i=0;i<layerNames.size();i++){
			if (!WorldGuardUtil.hasRegion(Bukkit.getWorld(worldName), getRegionName(i)))
				return false;
		}
		return true;
	}

	@Override
	public List<String> getInvalidReasons(){
		List<String> reasons = new ArrayList<String>();
		if (layerNames == null || layerNames.isEmpty()){
			reasons.add("ArenaSpleef arena needs a layer region, and none is defined!");
		} else {
			for (int i=0;i<layerNames.size();i++){
				if (!WorldGuardUtil.hasRegion(Bukkit.getWorld(worldName), getRegionName(i)))
					reasons.add("ArenaSpleef lost layer "+i+", please reselect it");
			}			
		}
		if (worldName == null || worldName.isEmpty() || Bukkit.getWorld(worldName)==null){
			reasons.add("ArenaSpleef lost its region, please reselect it");
		}
		reasons.addAll(super.getInvalidReasons());
		return reasons;
	}


	private void cancelTimers() {
		if (regenTimers != null){
			for (Integer timerid: regenTimers.values()){
				Bukkit.getScheduler().cancelTask(timerid);				
			}
		}
	}

	private String getRegionName(int layer) {
		return "ba-spleef-"+getName() +"-"+layer;
	}

	public void setRegion(Player p, Selection sel, int layer, Integer regenTime) throws Exception {
		final String layerName = getRegionName(layer);
		if (regenTimes == null && regenTime != null && regenTime > 0)
			regenTimes = new ConcurrentHashMap<String,Integer>();
		if (layerNames == null)
			layerNames = new CopyOnWriteArrayList<String>();
		if (layer < layerNames.size()){
			layerNames.set(layer, layerName);
		} else { 
			layerNames.add(layerName);
		}
		if (regenTime != null && regenTime > 0){
			regenTimes.put(layerName, regenTime);}

		worldName = sel.getWorld().getName();
		WorldGuardUtil.addRegion(sel, layerName);
		WorldGuardUtil.saveSchematic(p, layerName);
	}
	
	@Override
	protected void delete(){
		for (String layerName: layerNames){
			WorldGuardUtil.deleteRegion(world, layerName);			
		}		
	}
}