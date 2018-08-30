package net.pixelcade.customjoinmessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomJoinMessage extends JavaPlugin implements Listener {

	public void onEnable() {
		this.saveDefaultConfig();
		this.defaultJoinMessage = ChatColor.translateAlternateColorCodes('&',
				this.getConfig().getString("join_message"));
		this.defaultLeaveMessage = ChatColor.translateAlternateColorCodes('&',
				this.getConfig().getString("leave_message"));
		this.joinPrefix = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("join_prefix"));
		this.leavePrefix = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("leave_prefix"));
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	public void onDisable() {

	}

	String defaultJoinMessage;
	String defaultLeaveMessage;
	String joinPrefix;
	String leavePrefix;

	public String getJoinMessage(Player player) {
		if (player.hasPermission("cjm.join")) {
			if (this.getConfig().getString("data." + player.getUniqueId().toString() + ".join") == null) {
				String message = this.defaultJoinMessage.replace("%player%", player.getName());
				return this.joinPrefix + " " + message;
			} else {
				String message = this.getConfig().getString("data." + player.getUniqueId().toString() + ".join");
				message = message.replace("%player%", player.getName());
				return this.joinPrefix + ChatColor.translateAlternateColorCodes('&', message);
			}
		} else {
			String message = this.defaultJoinMessage.replaceAll("%player%", player.getName());
			return this.joinPrefix + " " + message;
		}
	}

	public String getLeaveMessage(Player player) {
		if (player.hasPermission("cjm.leave")) {
			if (this.getConfig().getString("data." + player.getUniqueId().toString() + ".leave") == null) {
				String message = this.defaultLeaveMessage.replace("%player%", player.getName());
				return this.leavePrefix + " " + message;
			} else {
				String message = this.getConfig().getString("data." + player.getUniqueId().toString() + ".leave");
				message = message.replace("%player%", player.getName());
				return this.leavePrefix + ChatColor.translateAlternateColorCodes('&', message);
			}
		} else {
			String message = this.defaultLeaveMessage.replaceAll("%player%", player.getName());
			return this.leavePrefix + " " + message;
		}
	}

	public void setJoinMessage(Player player, String message) {
		this.getConfig().set("data." + player.getUniqueId().toString() + ".join", message);
		this.saveConfig();
	}

	public void setLeaveMessage(Player player, String message) {
		this.getConfig().set("data." + player.getUniqueId().toString() + ".leave", message);
		this.saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player to use this command");
			return true;
		}

		Player player = (Player) sender;

		if (command.getName().equalsIgnoreCase("jm") || command.getName().equalsIgnoreCase("joinmessage")) {
			if (args.length == 0) {
				player.sendMessage(ChatColor.GREEN + "Current Join Message:");
				player.sendMessage(this.getJoinMessage(player));
				player.sendMessage("");
				player.sendMessage(
						ChatColor.GREEN + "Type /jm set [message] to change message. Use %player% for your name.");
				return true;
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("set")) {
				if (player.hasPermission("cjm.join")) {
					String message = "";
					for (int i = 1; i < args.length; i++) {
						message = message + " " + args[i];
					}
					if (message.length() > 10) {
						player.sendMessage(ChatColor.RED + "You can only use 10 characters for your join message.");
						return true;
					}
					this.setJoinMessage(player, message);
					player.sendMessage(ChatColor.GREEN + "Join message set: " + this.getJoinMessage(player));
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You do not have the permissions.");
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "Wrong usage. Type /jm or /jm set [message]");
				return true;
			}
		}

		if (command.getName().equalsIgnoreCase("lm") || command.getName().equalsIgnoreCase("leavemessage")) {
			if (args.length == 0) {
				player.sendMessage(ChatColor.GREEN + "Current Leave Message:");
				player.sendMessage(this.getLeaveMessage(player));
				player.sendMessage("");
				player.sendMessage(
						ChatColor.GREEN + "Type /lm set [message] to change message. Use %player% for your name.");
				return true;
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("set")) {
				if (player.hasPermission("cjm.leave")) {
					String message = "";
					for (int i = 1; i < args.length; i++) {
						message = message + " " + args[i];
					}
					if (message.length() > 10) {
						player.sendMessage(ChatColor.RED + "You can only use 10 characters for your leave message.");
						return true;
					}
					this.setLeaveMessage(player, message);
					player.sendMessage(ChatColor.GREEN + "Leave message set: " + this.getLeaveMessage(player));
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You do not have the permissions.");
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "Wrong usage. Type /lm or /lm set [message]");
				return true;
			}
		}

		player.sendMessage(ChatColor.RED + "To set messages type /jm or /lm.");
		return true;
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPermission("cjm.join")) {
			event.setJoinMessage(this.getJoinMessage(event.getPlayer()));
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.hasPermission("cjm.admin")) {
					player.sendMessage(ChatColor.GRAY + event.getPlayer().getName() + " joined.");
				}
			}
		} else {
			event.setJoinMessage(this.getJoinMessage(event.getPlayer()));
		}
	}

	@EventHandler
	public void playerLeave(PlayerQuitEvent event) {
		if (event.getPlayer().hasPermission("cjm.leave")) {
			event.setQuitMessage(this.getLeaveMessage(event.getPlayer()));
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.hasPermission("cjm.admin")) {
					player.sendMessage(ChatColor.GRAY + event.getPlayer().getName() + " left.");
				}
			}
		} else {
			event.setQuitMessage(this.getLeaveMessage(event.getPlayer()));
		}
	}

	@EventHandler
	public void playerKick(PlayerKickEvent event) {
		if (event.getPlayer().hasPermission("cjm.leave")) {
			event.setLeaveMessage(this.getLeaveMessage(event.getPlayer()));
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.hasPermission("cjm.admin")) {
					player.sendMessage(ChatColor.GRAY + event.getPlayer().getName() + " left.");
				}
			}
		} else {
			event.setLeaveMessage(this.getLeaveMessage(event.getPlayer()));
		}
	}

}
