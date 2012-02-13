package com.sgcraft.SGBroadcast;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class SGBroadcast extends JavaPlugin {
	
	public static SGBroadcast plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public static int currentLine = 0;
	public static int tid = 0;
	public static boolean running = true;
	public static long interval = 60;
	public static int messageCount = 0;
	public static FileConfiguration config;
	
	@Override
	public void onDisable() {
		saveConfig();
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info("[" + pdfFile.getName() + "] is now disabled!");
		
	}
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		config = getConfig();
        config.options().copyDefaults(true);
		saveConfig();
		interval = config.getInt("config.interval");
		running = config.getBoolean("config.run-on-startup");
		loadCommands();
		if (running == true)
			startBroadcasts();
		logger.info("[" + pdfFile.getName() + "] v" + pdfFile.getVersion() + " is now enabled!");
	}
	
	public void startBroadcasts() {
		tid = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				broadcastMessage();
			}
		}, 0, interval * 20);
	}
	
	public void broadcastMessage() {
		List<String> messages = config.getStringList("messages");
		messageCount = messages.size();
		messageCount = messageCount--;
		if (currentLine >= messageCount)
			currentLine = 0;
		String message = messages.get(currentLine);
		message = replaceColors(message);
		currentLine++;
		
		Bukkit.getServer().broadcastMessage(replaceColors(config.getString("config.title")) + ChatColor.WHITE + " " + message);
		
	}
	
	public static String replaceColors(String line) {
		line = line.replaceAll("&f", ChatColor.WHITE + "");
		line = line.replaceAll("&e", ChatColor.YELLOW + "");
		line = line.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
		line = line.replaceAll("&a", ChatColor.GREEN + "");
		line = line.replaceAll("&b", ChatColor.AQUA + "");
		line = line.replaceAll("&c", ChatColor.RED + "");
		line = line.replaceAll("&0", ChatColor.BLACK + "");
		line = line.replaceAll("&1", ChatColor.DARK_BLUE + "");
		line = line.replaceAll("&2", ChatColor.DARK_AQUA + "");
		line = line.replaceAll("&3", ChatColor.DARK_GREEN + "");
		line = line.replaceAll("&4", ChatColor.DARK_RED + "");
		line = line.replaceAll("&5", ChatColor.DARK_PURPLE + "");
		line = line.replaceAll("&6", ChatColor.GOLD + "");
		line = line.replaceAll("&7", ChatColor.GRAY + "");
		line = line.replaceAll("&8", ChatColor.DARK_GRAY + "");
		line = line.replaceAll("&9", ChatColor.BLUE + "");
		
		return line;
	}
	
	void loadCommands() {
		getCommand("sgbc").setExecutor(new CommandExecutor() {

			public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You must be a player!");
				}
				
				Player player = (Player) sender;
				
				if (!player.isOp()) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
					return false;
				}
				
				if (args[0].equalsIgnoreCase("stop")) {
					if (running == true) {
						Bukkit.getServer().getScheduler().cancelTask(tid);
						player.sendMessage("Cancelled broadcasts.");
						running = false;
					} else {
						player.sendMessage("Broadcasts are already disabled!");
					}
				} else if (args[0].equalsIgnoreCase("start")) {
					if (running == true) {
						player.sendMessage("Broadcasts are already running!");
					} else {
						startBroadcasts();
						running = true;
					}
				} else if (args[0].equalsIgnoreCase("reload")) {
					reloadConfig();
					player.sendMessage("[SGBroadcast] Config reloaded!");
					config = getConfig();
					interval = config.getInt("config.interval");
				} else {
					player.sendMessage("Unrecognized broadcast command");
				}
				return false;
			}
			
		});
	}

}
