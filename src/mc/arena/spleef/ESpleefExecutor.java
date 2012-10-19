package mc.arena.spleef;

import mc.alk.arena.BattleArena;
import mc.alk.arena.executors.MCCommand;
import mc.alk.arena.executors.ReservedArenaEventExecutor;
import mc.alk.arena.objects.arenas.Arena;
import mc.arena.spleef.util.WorldGuardUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.Selection;

public class ESpleefExecutor extends ReservedArenaEventExecutor{

	@MCCommand(cmds={"setLayer"}, inGame=true, admin=true)
	public boolean setLayer(Player sender, Arena arena) {
		return setLayer(sender,arena,1, null);
	}
	
	@MCCommand(cmds={"setLayer"}, inGame=true, admin=true)
	public boolean setLayer(Player sender, Arena arena, Integer layerIndex) {
		return setLayer(sender,arena,layerIndex, null);
	}

	@MCCommand(cmds={"setLayer"}, inGame=true, admin=true)
	public boolean setLayer(Player sender, Arena arena, Integer layerIndex, Integer regenTime) {
		if (!(arena instanceof SpleefArena)){
			return sendMessage(sender,"&cArena &6" + arena.getName() +"&c is not a spleef arena!");
		}
		if (layerIndex <1 || layerIndex > 10){
			return sendMessage(sender,"&cBad layer index, 1-10");}
		SpleefArena sa = (SpleefArena) arena;
		Selection sel = WorldGuardUtil.getSelection(sender);
		if (sel == null)
			return sendMessage(sender, ChatColor.RED + "Please select an area first using WorldEdit.");

		try {
			sa.setRegion(sender, sel, layerIndex-1, regenTime);
			BattleArena.saveArenas(ArenaSpleef.getSelf());
		} catch (Exception e) {
			e.printStackTrace();
			return sendMessage(sender, ChatColor.RED + "Error creating region. " + e.getMessage());
		}
		
		return sendMessage(sender,"&2Spleef Layer "+layerIndex+" has been created");
	}

}
