package mc.arena.spleef;

import mc.alk.arena.executors.BAExecutor;
import mc.alk.arena.executors.MCCommand;
import mc.alk.arena.objects.arenas.Arena;
import mc.arena.spleef.util.WorldGuardUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class SpleefExecutor extends BAExecutor{

	@MCCommand(cmds={"setType"}, admin=true)
	public boolean addBlock(CommandSender sender, Arena arena, String type) {
		if (!(arena instanceof SpleefArena)){
			return sendMessage(sender,"&eArena " + arena.getName() +" is not a spleef arena!");
		}
		SpleefArena sa = (SpleefArena) arena;

		BaseBlock fill = WorldGuardUtil.getBlock(sender,type);
		if (fill == null)
			return true;
		sa.setRegionType(fill);
		return sendMessage(sender,"&2Layer has been set to " + type);
	}

	@MCCommand(cmds={"setLayer"}, inGame=true, admin=true)
	public boolean clearBlocks(Player sender, Arena arena, String type) {
		if (!(arena instanceof SpleefArena)){
			return sendMessage(sender,"&eArena " + arena.getName() +" is not a spleef arena!");
		}
		SpleefArena sa = (SpleefArena) arena;
		BaseBlock block = WorldGuardUtil.getBlock(sender,type);
		if (block == null)
			return true;
		Selection sel = WorldGuardUtil.getSelection(sender);
		if (sel == null)
			return sendMessage(sender, ChatColor.RED + "Please select an area first using WorldEdit.");

		try {
			sa.setRegion(sel);
		} catch (Exception e) {
			e.printStackTrace();
			return sendMessage(sender, ChatColor.RED + "Error creating region. " + e.getMessage());
		}
		sa.setRegionType(block);
		
		return sendMessage(sender,"&2Region has been set to " + type);
	}
}
