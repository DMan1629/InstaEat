package me.DMan16.InstaEat;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import me.DMan16.CustomFood.CustomFoods;
import me.DMan16.Utils.Permissions;
import me.DMan16.Utils.Utils;

public class FoodListener implements Listener {
	@EventHandler
	void onPlayerFoodEvent(PlayerInteractEvent event) {
		if (!Permissions.PreventPermission(event.getPlayer())) {
			if (event.hasItem() && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
				Block block = event.getClickedBlock();
				ItemStack item = event.getItem();
				if (block != null) {
					if (Utils.isInteractable(block.getType())) return;
					Block above = block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ());
					if (item != null) {
						if ((item.getType() == Material.POTATO || item.getType() == Material.CARROT) &&
								(block.getType() == Material.FARMLAND && (above.isEmpty() || above.isLiquid()))) return;
						if (item.getType() == Material.SWEET_BERRIES &&
								((block.getType() == Material.FARMLAND && (above.isEmpty() || above.isLiquid())) ||
								block.getType() == Material.GRASS_BLOCK || block.getType() == Material.DIRT || block.getType() == Material.PODZOL
								|| block.getType() == Material.COARSE_DIRT)) return;
					}
				}
				if (item != null) {
					if (CustomFoods.isFood(item)) {
						if (CustomFoods.consumeFood(event.getPlayer(),item)) {
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	void onPlayerConsumeEvent(PlayerItemConsumeEvent event) {
		if (!Permissions.PreventPermission(event.getPlayer())) {
			if (event.getItem() != null && !event.getItem().getType().isAir()) {
				ItemStack item = event.getItem(); 
				if (CustomFoods.isFood(item)) {
					event.setCancelled(true);
				}
			}
		}
	}
}