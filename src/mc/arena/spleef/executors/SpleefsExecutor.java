package mc.arena.spleef.executors;

import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.util.MessageUtil;
import mc.arena.spleef.SpleefArenaEditor;
import mc.arena.spleef.SpleefException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpleefsExecutor {

	public static boolean setLayer(Player sender, Arena arena, Integer layerIndex) {
		try{
			SpleefArenaEditor sae = new SpleefArenaEditor(arena);
			sae.setLayer(sender, layerIndex);
			return sendMessage(sender,"&2Spleef Layer "+layerIndex+" has been created");
		} catch (SpleefException e) {
			return sendMessage(sender, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return sendMessage(sender, ChatColor.RED + "Error creating region. " + e.getMessage());
		}
	}

	public static boolean setRegen(CommandSender sender, Arena arena, Integer layerIndex, Integer regenTime) {
		try{
			SpleefArenaEditor sae = new SpleefArenaEditor(arena);
			sae.setRegen(layerIndex, regenTime);
			return sendMessage(sender,"&2Spleef Layer "+layerIndex+" now regens every " + regenTime +" seconds");
		} catch (SpleefException e) {
			return sendMessage(sender, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return sendMessage(sender, ChatColor.RED + "Error setting regen. " + e.getMessage());
		}
	}

	public static boolean sendMessage(CommandSender sender, String msg){
		return MessageUtil.sendMessage(sender, msg);
	}

	public static boolean deleteRegen(CommandSender sender, Arena arena,Integer layerIndex) {
		try{
			SpleefArenaEditor sae = new SpleefArenaEditor(arena);
			sae.deleteRegen(layerIndex);
			return sendMessage(sender,"&2Spleef Layer "+layerIndex+" now no longer regens during the match");
		} catch (SpleefException e) {
			return sendMessage(sender, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return sendMessage(sender, ChatColor.RED + "Error deleting regen. " + e.getMessage());
		}

	}
}
