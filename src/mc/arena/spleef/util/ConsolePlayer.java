package mc.arena.spleef.util;

import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.ServerInterface;
import com.sk89q.worldedit.bukkit.BukkitCommandSender;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class ConsolePlayer extends BukkitCommandSender {
	LocalWorld world;
	public ConsolePlayer(WorldEditPlugin plugin, ServerInterface server,CommandSender sender, World w) {
		super(plugin, server, sender);
		world = BukkitUtil.getLocalWorld(w);
	}

    @Override
    public boolean isPlayer() {
        return true;
    }
    @Override
    public LocalWorld getWorld() {
        return world;
    }

}
