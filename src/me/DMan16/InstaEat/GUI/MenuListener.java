package me.DMan16.InstaEat.GUI;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import me.DMan16.InstaEat.InstaEat.InstaEat;

public class MenuListener implements Listener {
	@EventHandler(priority = EventPriority.LOWEST)
	void onCloseInventory(InventoryCloseEvent event) {
		if (InstaEat.MenuManager.containsKey(event.getPlayer())) {
			Menu menu = InstaEat.MenuManager.get(event.getPlayer());
			if (menu instanceof EditMenu) {
				EditMenu editMenu = (EditMenu) menu;
				if (((EditMenu) menu).editInput) {
					ItemStack item = editMenu.getItem();
					ItemStack item2 = editMenu.getItem2();
					ItemStack output = editMenu.getOutput();
					if (item != null) {
						event.getPlayer().getInventory().addItem(item);
					}
					if (item2 != null) {
						event.getPlayer().getInventory().addItem(item2);
					}
					if (output != null) {
						event.getPlayer().getInventory().addItem(output);
					}
				}
			}
			if (event.getInventory().equals(event.getPlayer().getOpenInventory().getTopInventory())) {
				InstaEat.MenuManager.remove(event.getPlayer());
			}
		}
	}

	@EventHandler
	void onInventoryDrag(InventoryDragEvent event) {
		if (InstaEat.MenuManager.containsKey(event.getWhoClicked())) {
			if (InstaEat.MenuManager.get(event.getWhoClicked()) instanceof EditMenu) {
				event.getRawSlots().forEach(slot -> {
					if (slot < InstaEat.MenuManager.get(event.getWhoClicked()).inv.getSize()) {
						event.setCancelled(true);
						return;
					}
				});
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onInventoryOpen(InventoryOpenEvent event) {
		if (InstaEat.MenuManager.containsKey(event.getPlayer())) {
			if (InstaEat.MenuManager.get(event.getPlayer()) instanceof EditMenu) {
				((EditMenu) InstaEat.MenuManager.get(event.getPlayer())).update();
			} else if (InstaEat.MenuManager.get(event.getPlayer()) instanceof ContentMenu) {
				((ContentMenu) InstaEat.MenuManager.get(event.getPlayer())).updateContent();
			}
		}
	}

	@EventHandler
	void onInventoryClick(InventoryClickEvent event) {
		if (InstaEat.MenuManager.containsKey(event.getWhoClicked())) {
			boolean cancel = true;
			Menu menu = InstaEat.MenuManager.get(event.getWhoClicked());
			if (menu instanceof EditMenu) {
				EditMenu editMenu = (EditMenu) menu;
				if (editMenu.input.equals(editMenu.loc(event.getRawSlot()))) {
					cancel = !editMenu.editInput;
				} else if (editMenu.input2.equals(editMenu.loc(event.getRawSlot()))) {
					cancel = editMenu.type != MenuType.COPY;
				} else if (editMenu.output.equals(editMenu.loc(event.getRawSlot()))) {
					if (editMenu.invMap.get(event.getRawSlot()) != null) {
						if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
							cancel = event.getCursor() != null;
						} else if (event.getAction() == InventoryAction.HOTBAR_SWAP) {
							cancel = event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) != null;
						}
					}
				} else if (event.getRawSlot() < menu.inv.getSize()) {
					cancel = true;
				} else if (event.isShiftClick() && event.getCurrentItem() != null) {
					if (editMenu.currentFood == null) {
						cancel = false;
					} else {
						ItemStack item = event.getCurrentItem() == null ? null : event.getCurrentItem().clone();
						if (editMenu.sameItem(editMenu.getItem(),item) && editMenu.getItem().getAmount() < editMenu.getItem().getMaxStackSize()) {
							int stack = editMenu.getItem().getAmount() + item.getAmount();
							if (stack <= item.getMaxStackSize()) {
								editMenu.setItemAmount(stack);
								item = null;
							} else {
								editMenu.setItemAmount(item.getMaxStackSize());
								item.setAmount(stack - item.getMaxStackSize());
							}
						}
						if (item == null || editMenu.type != MenuType.COPY) {
							event.setCurrentItem(item);
						} else {
							ItemStack item2 = editMenu.getItem2();
							if (item2 == null) {
								event.setCurrentItem(null);
								editMenu.setItem2(item);
							} else if (editMenu.sameItem(item2,item) && item2.getAmount() < item2.getMaxStackSize()) {
								int stack = item2.getAmount() + item.getAmount();
								if (stack <= item.getMaxStackSize()) {
									item2.setAmount(stack);
									item = null;
									event.setCurrentItem(item);
									editMenu.setItem2(item2);
								} else {
									item2.setAmount(item.getMaxStackSize());
									item.setAmount(stack - item.getMaxStackSize());
									event.setCurrentItem(item);
									editMenu.setItem2(item2);
								}
							}
						}
					}
				} else {
					cancel = false;
				}
			}
			event.setCancelled(cancel);
			if (cancel && event.getClick() != ClickType.RIGHT && event.getClick() != ClickType.LEFT) return;
			Button button = menu.invMap.get(event.getRawSlot());
			if (button != null) button.run(menu);
			if (menu instanceof EditMenu && menu.type != MenuType.EDITVANILLA) {
				((EditMenu) menu).update();
			}
		}
	}
}