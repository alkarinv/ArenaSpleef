package mc.arena.spleef.util;

import java.io.File;
import java.io.IOException;

import mc.alk.arena.util.MessageUtil;
import mc.arena.spleef.Defaults;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.DisallowedItemException;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.UnknownItemException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.patterns.Pattern;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardUtil {
	public static WorldEditPlugin wep;
	public static WorldGuardPlugin wgp;

	public static class WorldGuardException extends Exception{
		private static final long serialVersionUID = 1L;
		public WorldGuardException(String msg) {
			super(msg);
		}
	}
	public static boolean hasWorldGuard() {
		return wgp != null && wep != null;
	}


	public static boolean pasteSchematic(Player p, Location loc, String schematic){
		CommandContext cc = null;
		String args[] = {"load", schematic};
		final WorldEdit we = wep.getWorldEdit();
		final LocalSession session = wep.getSession(p);
		final BukkitPlayer lPlayer = wep.wrapPlayer(p);

		EditSession editSession = session.createEditSession(lPlayer);
		Vector pos = new Vector(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
		try {
			cc = new CommandContext(args);
			return loadAndPaste(cc, we, session, lPlayer,editSession,pos);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * This is just copied and pasted from world edit source, with small changes to also paste
	 * @param args
	 * @param we
	 * @param session
	 * @param player
	 * @param editSession
	 */
	public static boolean loadAndPaste(CommandContext args, WorldEdit we,
			LocalSession session, com.sk89q.worldedit.LocalPlayer player, EditSession editSession, Vector pos) {

		LocalConfiguration config = we.getConfiguration();

		String filename = args.getString(0);
		File dir = we.getWorkingDirectoryFile(config.saveDir);
		File f = null;
		try {
			f = we.getSafeOpenFile(player, dir, filename, "schematic",new String[] {"schematic"});
			String filePath = f.getCanonicalPath();
			String dirPath = dir.getCanonicalPath();

			if (!filePath.substring(0, dirPath.length()).equals(dirPath)) {
				player.printError("Schematic could not read or it does not exist.");
				return false;
			} 
			SchematicFormat format = SchematicFormat.getFormat(f);
			if (format == null) {
				player.printError("Unknown schematic format for file" + f);
				return false;
			}

			if (!filePath.substring(0, dirPath.length()).equals(dirPath)) {
				player.printError("Schematic could not read or it does not exist.");
			} else {
				session.setClipboard(format.load(f));
				WorldEdit.logger.info(player.getName() + " loaded " + filePath);
				player.print(filePath + " loaded");
			}
			session.getClipboard().paste(editSession, pos, false, true);
			WorldEdit.logger.info(player.getName() + " pasted schematic" + filePath +"  at " + pos);            
		} catch (DataException e) {
			player.printError("Load error: " + e.getMessage());
		} catch (IOException e) {
			player.printError("Schematic could not read or it does not exist: " + e.getMessage());
		} catch (Exception e){
			player.printError("Error : " + e.getMessage());
		}
		return true;
	}

	public static ProtectedRegion setBlocks(World w, String region, int type, int data) throws Exception{
		RegionManager rm = wgp.getRegionManager(w);
		if (rm == null)
			throw new WorldGuardException("Region manager for world " + w.getName() +" was null");
		ProtectedRegion pr = rm.getRegion(region);
		if (pr == null)
			throw new WorldGuardException("ProtectedRegion " + region +" was null");
		BlockVector min = pr.getMinimumPoint();
		BlockVector max = pr.getMaximumPoint();
		final Selection sel = new CuboidSelection(w,min,max);
		EditSession es = new EditSession(BukkitUtil.getLocalWorld(sel.getWorld()), Defaults.MAX_REGION_SIZE);
		Pattern p = new SingleBlockPattern(new BaseBlock(type,data));
		es.setBlocks(sel.getRegionSelector().getRegion(), p);
		return pr;
	}



	public static void setFlag(World w, String region, StateFlag flag, State value) throws Exception {
		RegionManager rm = wgp.getRegionManager(w);
		if (rm == null)
			throw new WorldGuardException("Region manager for world " + w.getName() +" was null");
		ProtectedRegion pr = rm.getRegion(region);
		if (pr == null)
			throw new WorldGuardException("ProtectedRegion " + region +" was null");

		pr.setFlag(flag, value);
		rm.save();
	}
	public static void setFlag(World w, StateFlag flag, State value, ProtectedRegion pr) throws Exception {
		RegionManager rm = wgp.getRegionManager(w);
		if (rm == null)
			throw new WorldGuardException("Region manager for world " + w.getName() +" was null");
		pr.setFlag(flag, value);
		rm.save();
	}

	
	public static Pattern getPattern(CommandSender sender, String type) {
		LocalPlayer lp = wep.wrapCommandSender(sender);
		try {
			return wep.getWorldEdit().getBlockPattern(lp, type);
		} catch (UnknownItemException e) {
			MessageUtil.sendMessage(sender, e.getMessage());
		} catch (DisallowedItemException e) {
			MessageUtil.sendMessage(sender, e.getMessage());
		}
		return null;
	}


	public static BaseBlock getBlock(CommandSender sender, String id) {
		try {
			return wep.getWorldEdit().getBlock(wep.wrapCommandSender(sender), id);
		} catch (UnknownItemException e) {
			MessageUtil.sendMessage(sender, e.getMessage());
		} catch (DisallowedItemException e) {
			MessageUtil.sendMessage(sender, e.getMessage());
		}
		return null;
	}
	
	public static boolean addRegion(Selection sel, String id) throws Exception {
		World w = sel.getWorld();
		RegionManager mgr = wgp.getGlobalRegionManager().get(w);
		ProtectedRegion region = mgr.getRegion(id);

		region = new ProtectedCuboidRegion(id, 
				sel.getNativeMinimumPoint().toBlockVector(), sel.getNativeMaximumPoint().toBlockVector());
		try {
			region.setPriority(11); /// some relatively high priority
			region.setFlag(DefaultFlag.PVP,State.DENY);
			region.setFlag(DefaultFlag.BUILD,State.ALLOW); /// allow them to build on the layer, we will handle blocking breaks
			wgp.getRegionManager(w).addRegion(region);
			mgr.save();
		} catch (ProtectionDatabaseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static ProtectedRegion getRegion(World w, String id) {
		return wgp.getRegionManager(w).getRegion(id);
	}
	
	public static boolean hasRegion(World world, String id){
		RegionManager mgr = wgp.getGlobalRegionManager().get(world);
		return mgr.hasRegion(id);
	}


	public static Selection getSelection(Player player) {
		return wep.getSelection(player);
	}


	public static void loadWorldGuardPlugin() {
		Plugin plugin= Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

		if (plugin == null) {
			System.out.println("[ArenaSpleef] WorldEdit not detected!");
			return;
		}        
		wep = ((WorldEditPlugin) plugin);


		plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null) {
			System.out.println("[ArenaSpleef] WorldGuard not detected!");
			return;
		}        
		wgp = ((WorldGuardPlugin) plugin);
	}





}
