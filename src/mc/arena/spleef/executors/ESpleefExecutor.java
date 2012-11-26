package mc.arena.spleef.executors;

import mc.alk.arena.executors.MCCommand;
import mc.alk.arena.executors.ReservedArenaEventExecutor;
import mc.alk.arena.objects.arenas.Arena;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ESpleefExecutor extends ReservedArenaEventExecutor{
	@MCCommand(cmds={"setLayer"}, inGame=true, admin=true)
	public boolean setLayer(Player sender, Arena arena) {
		return SpleefsExecutor.setLayer(sender,arena,1);
	}

	@MCCommand(cmds={"setLayer"}, inGame=true, admin=true, order=1)
	public boolean setLayer(Player sender, Arena arena, Integer layerIndex) {
		return SpleefsExecutor.setLayer(sender,arena,layerIndex);
	}

	@MCCommand(cmds={"setRegen"}, admin=true)
	public boolean setRegen(CommandSender sender, Arena arena, Integer regenTime) {
		return SpleefsExecutor.setRegen(sender,arena,1, regenTime);
	}

	@MCCommand(cmds={"setRegen"}, admin=true, order=1)
	public boolean setRegen(CommandSender sender, Arena arena, Integer layerIndex, Integer regenTime) {
		return SpleefsExecutor.setRegen(sender,arena,layerIndex, regenTime);
	}

	@MCCommand(cmds={"deleteRegen"}, admin=true)
	public boolean deleteRegen(CommandSender sender, Arena arena) {
		return SpleefsExecutor.deleteRegen(sender,arena,1);
	}

	@MCCommand(cmds={"deleteRegen"}, admin=true, order=1)
	public boolean deleteRegen(CommandSender sender, Arena arena, Integer layerIndex) {
		return SpleefsExecutor.deleteRegen(sender,arena,layerIndex);
	}
}
