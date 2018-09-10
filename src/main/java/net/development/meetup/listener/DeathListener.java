package net.development.meetup.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.development.meetup.Main;
import net.development.meetup.UHCPlayer;
import net.development.meetup.manager.KitManager;
import net.development.meetup.manager.PlayerManager;
import net.development.meetup.options.checkWin;
import net.development.meetup.scenarios.ScenariosEnable;

public class DeathListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPPDeath(PlayerDeathEvent e) {
		Player p = (Player) e.getEntity();
		p.setHealth(20.0);
		Main.getGM().players.remove(p.getUniqueId());
		if (!ScenariosEnable.TimebombE) {
			p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.EXP_BOTTLE, 24));
			p.getWorld().dropItemNaturally(p.getLocation(), KitManager.goldenHead(1));
		}
		p.getWorld().strikeLightningEffect(p.getLocation());
		e.setDeathMessage(null);
		executeDeathEvent(e);
		Main.getGM().getData.get(p.getUniqueId()).setAlive(false);
		new BukkitRunnable() {
			@Override
			public void run() {
				PlayerManager.setPlayerSpec(p);
			}
		}.runTaskLater(Main.get(), 2L);
		checkWin.checkWins();
		if (Main.getGM().players.size() <= 5 && !(Main.getGM().players.size() <= 1)) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
				for (String a : Main.get().getLang().translateArrays(p, "ppl5BroadCast")) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', a));
				}
			}
		}
		if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) p.getLastDamageCause();
			if (!(ev.getDamager() instanceof Player) && !(ev.getDamager() instanceof Arrow)) {
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c" + p.getName() + "&7 死亡了!"));
				return;
			}
			Player p1 = null;
			if (ev.getDamager() instanceof Player) {
				p1 = (Player) ev.getDamager();
			} else if (((Arrow) ev.getDamager()).getShooter() instanceof Player) {
				p1 = (Player) ((Arrow) ev.getDamager()).getShooter();
			} else {
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c" + p.getName() + "&8[&f"
						+ Main.getGM().getData.get(p.getUniqueId()).getKills() + "&8]&7 死亡了!"));
				return;
			}
			Main.getGM().getData.get(p1.getUniqueId()).addKills();
			if(Main.TeamMode)Main.getGM().getData.get(p1.getUniqueId()).getTeam().kills++;
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
					"&c" + p.getName() + "&8[&f" + Main.getGM().getData.get(p.getUniqueId()).getKills() + "&8]&7 被 &a"
							+ p1.getName() + "&8[&f" + Main.getGM().getData.get(p1.getUniqueId()).getKills()
							+ "&8] &7殺死了!"));

		} else {
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c" + p.getName() + "&8[&f"
					+ Main.getGM().getData.get(p.getUniqueId()).getKills() + "&8]&7 死亡了!"));
		}

	}

	private void executeDeathEvent(PlayerDeathEvent e) {
		if (ScenariosEnable.TimebombE) {
			Player player = e.getEntity();
			Location loc = e.getEntity().getLocation();
			player.getLocation().getBlock().setType(Material.CHEST);
			player.getLocation().add(1.0, 0.0, 0.0).getBlock().setType(Material.CHEST);
			player.getLocation().add(0.0, 1.0, 0.0).getBlock().setType(Material.AIR);
			player.getLocation().add(1.0, 1.0, 0.0).getBlock().setType(Material.AIR);
			Chest chest = (Chest) player.getLocation().getBlock().getState();
			chest.getBlockInventory().setItem(3, player.getInventory().getBoots());
			for (ItemStack item : e.getDrops()) {
				if (item.getType() == null || item.getType().equals(Material.AIR)) {
					continue;
				}
				chest.getInventory().addItem(item);
			}
			chest.getInventory().addItem(new ItemStack(Material.EXP_BOTTLE, 24));
			chest.getInventory().addItem(KitManager.goldenHead(1));
			e.getDrops().clear();
			new BukkitRunnable() {
				Integer it = 30;

				@Override
				public void run() {

					if (it >= 1 && it <= 5) {
						loc.getWorld().playSound(loc, Sound.LEVEL_UP, 2.0f, 10.0f);
					}
					if (it <= 0) {
						loc.getBlock().setType(Material.AIR);
						loc.add(1.0, 0.0, 0.0).getBlock().setType(Material.AIR);
						loc.getWorld().createExplosion((double) loc.getBlockX() + 0.5, (double) loc.getBlockY() + 0.5,
								(double) loc.getBlockZ() + 0.5, 3.5f, false, true);
						loc.getWorld().strikeLightning(loc);
						cancel();
						return;
					}
					--it;
				}
			}.runTaskTimer(Main.get(), 20, 20);

		}
		if (ScenariosEnable.NoCleanE) {
			if (e.getEntity().getKiller() != null) {
				Player killer = e.getEntity().getKiller();
				killer.sendMessage(Main.get().getLang().translate(killer, "noCleanStart"));
				UHCPlayer uk = Main.getGM().getData.get(killer.getUniqueId());
				if(uk != null) {
					if (uk.isNoClean())
						return;
					uk.setNoCleanTimer(20);
					uk.setNoClean(true);
					uk.countDown();
				}
			}
		}

	}

}