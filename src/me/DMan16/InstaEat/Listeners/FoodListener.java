package me.DMan16.InstaEat.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Cake;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.DMan16.InstaEat.InstaEat.CustomFood;
import me.DMan16.InstaEat.InstaEat.InstaEat;
import me.DMan16.InstaEat.Utils.Permissions;
import me.DMan16.InstaEat.Utils.Utils;

public class FoodListener implements Listener {
	private List<Player> cake = new ArrayList<Player>();
	
	@EventHandler(priority = EventPriority.LOWEST)
	void onPlayerFoodEvent(PlayerInteractEvent event) {
		if (allow(event.getPlayer())) {
			if (event.hasItem() && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
				Block block = event.getClickedBlock();
				ItemStack item = event.getItem();
				if (block != null) {
					if (Utils.isInteractable(block)) return;
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
					if (Utils.isFood(item)) {
						CustomFood food = new CustomFood(item);
						if (!food.fullySet()) {
							food = InstaEat.FoodsManager.get(item.getType());
							food.setItemAmount(item.getAmount());
						}
						ItemStack newItem = food.consume(event.getPlayer(),event.getHand() == EquipmentSlot.HAND);
						if (food.item() == null || food.item().getAmount() != item.getAmount()) {
							event.setCancelled(true);
							if (food.item() == null) item = null;
							else item.setAmount(food.item().getAmount());
							if (event.getHand() == EquipmentSlot.HAND) event.getPlayer().getInventory().setItemInMainHand(item);
							else event.getPlayer().getInventory().setItemInOffHand(item);
							if (newItem != null) {
								if (item == null) {
									if (event.getHand() == EquipmentSlot.HAND) event.getPlayer().getInventory().setItemInMainHand(newItem);
									else event.getPlayer().getInventory().setItemInOffHand(newItem);
								} else {
									event.getPlayer().getInventory().addItem(newItem);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	void onPlayerConsumeEvent(PlayerItemConsumeEvent event) {
		if (event.getItem() != null && !event.getItem().getType().isAir()) {
			ItemStack item = event.getItem(); 
			if (Utils.isFood(item) && (allow(event.getPlayer()) || !InstaEat.FoodsManager.isSet(item))) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	void onPlayerCakeEvent(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasBlock()) {
			if (event.getClickedBlock().getBlockData() instanceof Cake) {
				cake.add(event.getPlayer());
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(InstaEat.getMain(), new Runnable() {
					@Override
					public void run() {
						cake.remove(event.getPlayer());
						InstaEat.FoodsManager.get(Material.CAKE).consume(event.getPlayer(),true);
					}
				},1);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	void onPlayerFoodLevelChangeEvent(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		if (event.getItem() == null) {
			if (cake.contains(event.getEntity())) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
			ItemStack item = event.getItem();
			if (Utils.isFood(item)) {
				CustomFood food = new CustomFood(item);
				if (!food.fullySet()) {
					food = InstaEat.FoodsManager.get(item.getType());
				}
				food.consume((Player) event.getEntity(),true);
			}
		}
	}
	
	private boolean allow(Player player) {
		short allow = 0;
		if (InstaEat.getConfigLoader().getInstaEat()) allow++;
		if (Permissions.PreventPermission(player)) allow--;
		if (Permissions.AllowPermission(player)) allow++;
		return allow > 0;
	}
}