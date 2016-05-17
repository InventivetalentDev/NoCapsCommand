package org.inventivetalent.nocapscommand;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.config.ConfigValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoCapsCommand extends JavaPlugin implements Listener {

	@ConfigValue(path = "alternatives") List<String> alternatives = new ArrayList<>();
	@ConfigValue(path = "message",
				 colorChar = '&')       String       message      = "";
	@ConfigValue(path = "autoCorrect")  boolean      autoCorrect  = false;
	@ConfigValue(path = "timeout")      int          timeoutTime  = 10;

	List<UUID> timeout = new ArrayList<>();

	@Override
	public void onEnable() {
		saveDefaultConfig();
		PluginAnnotations.loadAll(this, this);
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onChat(final AsyncPlayerChatEvent e) {
		if (e.getPlayer().hasPermission("ncc.use")) {
			if (this.timeout.contains(e.getPlayer().getUniqueId())) {
				this.timeout.remove(e.getPlayer().getUniqueId());
				return;
			}
			for (String s : this.alternatives) {
				if (e.getMessage().startsWith(s)) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(String.format(this.message, new Object[] { s }));
					this.timeout.add(e.getPlayer().getUniqueId());
					if (this.autoCorrect) {
						Bukkit.dispatchCommand(e.getPlayer(), e.getMessage().substring(1));
					}
					Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						public void run() {
							timeout.remove(e.getPlayer().getUniqueId());
						}
					}, 20 * timeoutTime);
					break;
				}
			}
		}
	}

}
