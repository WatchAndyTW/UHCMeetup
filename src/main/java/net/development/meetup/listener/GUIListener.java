package net.development.meetup.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.development.meetup.Main;
import net.development.meetup.UHCPlayer;
import net.development.meetup.UHCTeam;
import net.development.meetup.options.TeamGUI;

public class GUIListener implements Listener{
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Inventory inv = e.getClickedInventory();
		if(inv != null && inv.getTitle().equals("§e選擇隊伍!") && 
				item != null && !item.getType().equals(Material.AIR) && item.getItemMeta().hasDisplayName()) {
			String[] name = item.getItemMeta().getDisplayName().split(" ");
			int team = Integer.valueOf(name[1])-1;
			UHCTeam uteam = TeamGUI.getInstance().teams.get(team);
			Player p = (Player) e.getWhoClicked();
			UHCPlayer up = Main.getGM().getData.get(p.getUniqueId());
			e.setCancelled(true);
			if(up.isInTeam()) {
				UHCTeam teamBefore = up.getTeam();
				if(teamBefore != null) {
					if(teamBefore.p1 == p) {
						teamBefore.p1 = null;
					}else if(teamBefore.p2 == p) {
						teamBefore.p2 = null;
					}TeamGUI.getInstance().updateGUI(teamBefore.id);
				}
			}
			if(uteam.p1 == null) {
				uteam.p1 = p;
			}else if(uteam.p2 == null) {
				uteam.p2 = p;
			}else {
				p.sendMessage("§c該隊伍已滿");
				return;
			}TeamGUI.getInstance().updateGUI(team);
			up.setTeam(uteam, 2);
			p.sendMessage("§a你加入了隊伍 "+(team+1)+" !");
		}
	}

}