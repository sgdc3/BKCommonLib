package com.bergerkiller.bukkit.common;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Common extends PluginBase {
	
	public static boolean isShowcaseEnabled = false;
	public static boolean showCaseUseOldMode = false;
	public static boolean isSCSEnabled = false;
	public static Plugin bleedingMobsInstance = null;
	public static Common plugin;
	
	public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
		if (pluginName.equals("Showcase")) {
			isShowcaseEnabled = enabled;
			log(Level.INFO, "Showcase detected: Showcased items will be ignored");
		} else if (pluginName.equals("ShowCaseStandalone")) {
			isSCSEnabled = enabled;
			log(Level.INFO, "Showcase Standalone detected: Showcased items will be ignored");
		} else if (pluginName.equals("BleedingMobs")) {
			bleedingMobsInstance = enabled ? plugin : null;
			log(Level.INFO, "Bleeding Mobs detected: Particle items will be ignored");
		}
	}
		
	public void setDisableMessage(String message) {};
	
	public void disable() {}
	public void enable() {
		plugin = this;
		this.register(new Listener() {
			@SuppressWarnings("unused")
			@EventHandler(priority = EventPriority.MONITOR)
			private void onPluginEnable(final PluginEnableEvent event) {
				for (PluginBase pb : PluginBase.plugins) {
					pb.updateDependency(event.getPlugin(), true);
				}
			}
			@SuppressWarnings("unused")
			@EventHandler(priority = EventPriority.MONITOR)
			private void onPluginDisable(PluginDisableEvent event) {
				for (PluginBase pb : PluginBase.plugins) {
					pb.updateDependency(event.getPlugin(), false);
				}
			}
		});
	}

	@Override
	public boolean command(CommandSender sender, String command, String[] args) {
		return true;
	}

	@Override
	public void permissions() {
		
	}

}
