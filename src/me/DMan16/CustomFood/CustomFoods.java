package me.DMan16.CustomFood;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mojang.datafixers.util.Pair;

import me.DMan16.Config.FoodsConfigManager;
import me.DMan16.Config.MCFoodsDefault;
import me.DMan16.Utils.Permissions;
import me.DMan16.Utils.Utils;

public class CustomFoods {
	final static String customFoodName = "customfood";
	final static String customFoodEffectName = "customfoodeffect";
	final static NamespacedKey customFoodNamespacedKey = Utils.namespacedKey(customFoodName);
	final static List<String> customFood = Arrays.asList("get","set","remove");
	final static List<String> customFoodEffect = Arrays.asList("get","add","remove","remove_all");
	final static List<String> customFoodEffectAdd = Arrays.asList("give","clear");
	final static List<Material> equipSound = Arrays.asList(Material.BEETROOT_SOUP,Material.MUSHROOM_STEW,Material.RABBIT_STEW,Material.SUSPICIOUS_STEW);
	final static int maxHunger = 20;
	final static int maxSaturation = 20;
	private static Advancement food1 = null;
	private static Advancement food2 = null;
	
	static void RootCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (Permissions.CommandPermission(player)) {
				String usage = "<" + customFoodName + "/" + customFoodEffectName + ">";
				Utils.chatColorsUsage(sender,usage);
			}
		} else {
			Permissions.NoPermission(sender);
		}
	}
	
	static void CustomFoodCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (Permissions.CommandPermission(player)) {
				if (player.getInventory().getItemInMainHand() != null) {
					String usage = customFoodName + " <" + String.join("/",customFood) + ">";
					String usageSet = customFoodName + " " + customFood.get(1) + " <hunger> <saturation> <chance> (hunger - Integer <= " +
							Integer.toString(maxHunger) + ", saturation <=  " + Integer.toString(maxSaturation) + " [precision: 0.1])";
					if (args.length != 2 && args.length != 5) {
						Utils.chatColorsUsage(sender,usage);
						return;
					}
					if (!customFood.contains(args[1])) {
						Utils.chatColorsUsage(sender,usage);
						return;
					}
		        	if ((args[1].equalsIgnoreCase(customFood.get(0)) || args[1].equalsIgnoreCase(customFood.get(2))) && args.length != 2) { 
		        		Utils.chatColorsUsage(sender,customFoodName + args[1].toLowerCase());
						return;
		        	}
		        	if (args[1].equalsIgnoreCase(customFood.get(1))) {
		        		try {
			        		if (Integer.parseInt(args[2]) > maxHunger || Integer.parseInt(args[2]) < 0 ||
			        				Float.parseFloat(args[3]) > maxSaturation || Float.parseFloat(args[3]) < 0 ||
			        				Integer.parseInt(args[4]) > 100 || Integer.parseInt(args[4]) < 0) throw new Exception();
			        		else if (((float) ((int) (Float.parseFloat(args[3]) * 10))) != Float.parseFloat(args[3]) * 10) throw new Exception();
		        		} catch (Exception e) {
			        		Utils.chatColorsUsage(sender,usageSet);
							return;
		        		}
		        	}
			        ItemStack item = player.getInventory().getItemInMainHand();
			        ItemMeta itemMeta = item.getItemMeta();
			        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
			        
			        if (isFood(item)) {
			        	if (args[1].equalsIgnoreCase(customFood.get(0))) {
			        		if (container.has(customFoodNamespacedKey,PersistentDataType.STRING)) {
			        			int itemHunger = getCustomFoodHunger(container);
			        			float itemSaturation = getCustomFoodSaturation(container);
			        			int itemChance = getCustomFoodChance(container);
			        			if (itemHunger != -1 && itemSaturation != -1.0F && itemChance != -1) {
					        		PersistentDataContainer newContainer = customFoodFix(container);
					        		if (!container.equals(newContainer)) {
					        			container = newContainer;
					        			item.setItemMeta(itemMeta);
					        		}
					        		Utils.chatColorsPlugin(sender,"&aCustom food info: " +
					        		Integer.toString(itemHunger) + " hunger, " + Float.toString(itemSaturation)
					        		+ " saturation, " + Integer.toString(itemChance) + "% effects chance.");
			                        return;
			        			} else {
			        				removeCustomFood(item);
					        		Utils.chatColorsPlugin(sender,"&cCustom food not set.");
			                        return;
			        			}
			        		} else {
				        		Utils.chatColorsPlugin(sender,"&cCustom food not set.");
		                        return;
			        		}
			        	}
			        	if (args[1].equalsIgnoreCase(customFood.get(2))) {
			        		if (container.has(customFoodNamespacedKey,PersistentDataType.STRING)) {
				        		removeCustomFood(item);
				        		container = removeAllCustomFoodEffects(container);
				        		item.setItemMeta(itemMeta);
		        				player.updateInventory();
				        		Utils.chatColorsPlugin(sender,"&aCustom food removed.");
				        		return;
			        		} else {
				        		Utils.chatColorsPlugin(sender,"&cCustom food not set.");
		                        return;
			        		}
			        	}
			        	if (args[1].equalsIgnoreCase(customFood.get(1))) {
			        		addCustomFood(item,Integer.parseInt(args[2]),Float.parseFloat(args[3]),Integer.parseInt(args[4]));
	        				player.updateInventory();
	        				container = item.getItemMeta().getPersistentDataContainer();
			        		Utils.chatColorsPlugin(sender,"&aCustom food set: " + getCustomFoodHunger(container)
			        		+ " hunger, " + getCustomFoodSaturation(container) + " saturation, " +
			        				getCustomFoodChance(container) + "% effects chance.");
			        		return;
			        	}
			        } else {
			        	Utils.chatColorsPlugin(sender,"&cNot CustomFood material.");
			        }
				} else {
					Utils.chatColorsPlugin(sender,"&cFirst have an item selected.");
				}
			} else {
				Permissions.NoPermission(sender);
			}
		}
	}
	
	static void CustomFoodEffectCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (Permissions.CommandPermission(player)) {
				if (player.getInventory().getItemInMainHand() != null) {
					String usage = customFoodEffectName + " <" + String.join("/",customFoodEffect) + ">";
					String usageRemove = customFoodEffectName + " " + customFoodEffect.get(2) + " <type>";
					String usageAdd = customFoodEffectName + " " + customFoodEffect.get(1) + " <" + String.join("/",customFoodEffectAdd) + ">";
					String usageClear = customFoodEffectName + " " + customFoodEffect.get(1) + " " + customFoodEffectAdd.get(1) + "<type>";
					String usageGive = customFoodEffectName + customFoodEffect.get(1) + " " + customFoodEffectAdd.get(0) + 
							" <type> <duration> <amplifier> (duration - seconds, Integer: 1-1M , amplifier - integer: 1-256)";
					if (args.length != 2 && args.length != 3 && args.length != 4 && args.length != 6) {
						Utils.chatColorsUsage(sender,usage);
						return;
					}
					if (!customFoodEffect.contains(args[1])) {
						Utils.chatColorsUsage(sender,usage);
						return;
					}
		        	if ((args[1].equalsIgnoreCase(customFoodEffect.get(0)) || args[1].equalsIgnoreCase(customFoodEffect.get(3))) && args.length != 2) {
		        		Utils.chatColorsUsage(sender,customFoodEffectName + " " + args[1].toLowerCase());
						return;
		        	}
		        	if (args[1].equalsIgnoreCase(customFoodEffect.get(2)) && args.length != 3) {
		        		Utils.chatColorsUsage(sender,usageRemove);
						return;
		        	}
		        	if (args[1].equalsIgnoreCase(customFoodEffect.get(1))) {
		        		if (args.length < 4) {
		        			Utils.chatColorsUsage(sender,usageAdd);
							return;
		        		}
	        			if (args[2].equalsIgnoreCase(customFoodEffectAdd.get(1))) {
		        			if (args.length != 4) {
		        				Utils.chatColorsUsage(sender,usageClear);
								return;
		        			}
	        			} else if (args[2].equalsIgnoreCase(customFoodEffectAdd.get(0))) {
	        				if (args.length != 6 || Integer.parseInt(args[4]) < 1 || Integer.parseInt(args[4]) > 1000000 ||
			        				Integer.parseInt(args[5]) < 1 || Integer.parseInt(args[5]) > 256) {
	        					Utils.chatColorsUsage(sender,usageGive);
	    						return;
	        				}
	        			} else {
	        				Utils.chatColorsUsage(sender,usageAdd);
							return;
	        			}
		        	}
			        ItemStack item = player.getInventory().getItemInMainHand();
			        if (isFood(item)) {
				        ItemMeta itemMeta = item.getItemMeta();
				        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
						if (container.has(customFoodNamespacedKey,PersistentDataType.STRING)) {
							int itemHunger = CustomFoods.getCustomFoodHunger(container);
							float itemSaturation = CustomFoods.getCustomFoodSaturation(container);
							int itemChance = CustomFoods.getCustomFoodChance(container);
							if (itemHunger != -1 && itemSaturation != -1.0F && itemChance != -1) {
								if (args[1].equalsIgnoreCase(customFoodEffect.get(0))) {
									List<PotionEffect> effects = getCustomFoodEffects(container);
									List<String> effectsList = new ArrayList<String>();
									if (effects != null) {
										for (int i = 0; i < effects.size(); i++) {
											effectsList.add(effects.get(i).getType().getName() + ": " + (effects.get(i).getAmplifier() > -1 ?
													"give " + Integer.toString(effects.get(i).getDuration()) +
													", " + Integer.toString(effects.get(i).getAmplifier()) : customFoodEffectAdd.get(1)));
										}
										Utils.chatColorsPlugin(sender,String.join(". ",effectsList));
									} else {
										Utils.chatColorsPlugin(sender,"&cNo custom effects on the item.");
						        		return;
									}
								}
								if (args[1].equalsIgnoreCase(customFoodEffect.get(2))) {
									if (container.has(Utils.namespacedKey(customFoodEffectName + "_" + args[2].toLowerCase()),PersistentDataType.STRING)) {
										container = removeCustomFoodEffect(container,args[2]);
										item.setItemMeta(itemMeta);
				        				player.updateInventory();
										Utils.chatColorsPlugin(sender,"&a" + args[2] + " effect removed.");
						        		return;
									} else {
										Utils.chatColorsPlugin(sender,"&cNo " + args[2] + " effect on the item.");
						        		return;
									}
								}
								if (args[1].equalsIgnoreCase(customFoodEffect.get(3))) {
									container = removeAllCustomFoodEffects(container);
									item.setItemMeta(itemMeta);
									Utils.chatColorsPlugin(sender,"&aAll effects removed.");
					        		return;
								}
								if (args[1].equalsIgnoreCase(customFoodEffect.get(1))) {
									if (!container.has(Utils.namespacedKey(customFoodEffectName + "_" + args[3].toLowerCase()),PersistentDataType.STRING)) {
										if (itemChance > 0) {
											if (args[2].equalsIgnoreCase(customFoodEffectAdd.get(1))) {
												addCustomFoodEffect(item,args[3],-1,-1);
						        				player.updateInventory();
												Utils.chatColorsPlugin(sender,"&aCustom effect added: " + args[3].toUpperCase() + " - clear");
												return;
											} else {
												addCustomFoodEffect(item,args[3],Integer.parseInt(args[4]),Integer.parseInt(args[5]));
						        				player.updateInventory();
												Utils.chatColorsPlugin(sender,"&aCustom effect added: " + args[3].toUpperCase() + " - give " + args[4] +
														", " + args[5]);
												return;
											}
										} else {
							        		Utils.chatColorsPlugin(sender,"&cCustomFood chance is 0 - not adding effect.");
										}
									} else {
						        		Utils.chatColorsPlugin(sender,"&cCustomFood already has this effect - not adding effect.");
									}
								}
							} else {
				        		Utils.chatColorsPlugin(sender,"&cCustom food not set.");
							}
						} else {
			        		Utils.chatColorsPlugin(sender,"&cCustom food not set.");
						}
			        } else {
			        	Utils.chatColorsPlugin(sender,"&cNot CustomFood material.");
			        }
		        } else {
					Utils.chatColorsPlugin(sender,"&cFirst have an item selected.");
		        }
			} else {
				Permissions.NoPermission(sender);
			}
		}
	}
	
	public static boolean isFood(ItemStack item) {
		return MCFoodsDefault.isFood(item.getType());
	}
	
	static int getCustomFoodHunger(PersistentDataContainer container) {
		List<NamespacedKey> keys = new ArrayList<NamespacedKey>(container.getKeys());
		if (keys != null) {
			for (int i = 0; i < keys.size(); i++) {
				if (keys.get(i).getKey().toLowerCase().startsWith(customFoodName + "_hunger_")) {
					String[] splitKey = keys.get(i).getKey().toLowerCase().split(customFoodName + "_hunger_");
					if (splitKey.length == 2) {
						try {
							if (Integer.parseInt(splitKey[1]) >= 0 && Integer.parseInt(splitKey[1]) <= maxHunger) {
								return Integer.parseInt(splitKey[1]);
							}
						} catch (Exception e) {
						}
					}
				}
			}
		}
		return -1;
	}
	
	static float getCustomFoodSaturation(PersistentDataContainer container) {
		List<NamespacedKey> keys = new ArrayList<NamespacedKey>(container.getKeys());
		if (keys != null) {
			for (int i = 0; i < keys.size(); i++) {
				if (keys.get(i).getKey().toLowerCase().startsWith(customFoodName + "_saturation_")) {
					String[] splitKey = keys.get(i).getKey().toLowerCase().split(customFoodName + "_saturation_");
					if (splitKey.length == 2) {
						try {
							float var = Float.parseFloat(splitKey[1]);
							if (var >=0 && var <= maxSaturation && (((float) ((int) (var * 10))) == var * 10)) {
								return var;
							}
						} catch (Exception e) {
						}
					}
				}
			}
		}
		return -1.0F;
	}
	
	static int getCustomFoodChance(PersistentDataContainer container) {
		List<NamespacedKey> keys = new ArrayList<NamespacedKey>(container.getKeys());
		if (keys != null) {
			for (int i = 0; i < keys.size(); i++) {
				if (keys.get(i).getKey().toLowerCase().startsWith(customFoodName + "_chance_")) {
					String[] splitKey = keys.get(i).getKey().toLowerCase().split(customFoodName + "_chance_");
					if (splitKey.length == 2) {
						try {
							if (Integer.parseInt(splitKey[1]) >= 0 && Integer.parseInt(splitKey[1]) <= 100) {
								return Integer.parseInt(splitKey[1]);
							}
						} catch (Exception e) {
						}
					}
				}
			}
		}
		return -1;
	}
	
	private static PersistentDataContainer customFoodFix(PersistentDataContainer container) {
		PersistentDataContainer newContainer = container;
		int firstHunger = getCustomFoodHunger(newContainer);
		float firstSaturation = getCustomFoodSaturation(newContainer);
		int firstChance = getCustomFoodChance(newContainer);
		List<NamespacedKey> keys = new ArrayList<NamespacedKey>(container.getKeys());
		if (keys != null) {
			for (int i = 0; i < keys.size(); i++) {
				if (keys.get(i).getKey().toLowerCase().startsWith(customFoodName + "_hunger") ||
						keys.get(i).getKey().toLowerCase().startsWith(customFoodName + "_saturation") ||
						keys.get(i).getKey().toLowerCase().startsWith(customFoodName + "_chance")) {
					newContainer.remove(keys.get(i));
				}
			}
		}
		if (firstHunger != -1 && firstSaturation != -1.0F && firstChance != -1) {
			newContainer.set(Utils.namespacedKey(customFoodName + "_hunger_" + Integer.toString(firstHunger)),PersistentDataType.STRING,"protected");
			newContainer.set(Utils.namespacedKey(customFoodName + "_saturation_" + Float.toString(firstSaturation)),PersistentDataType.STRING,"protected");
			newContainer.set(Utils.namespacedKey(customFoodName + "_chance_" + Integer.toString(firstChance)),PersistentDataType.STRING,"protected");
		}
		return newContainer;
	}

	static void removeCustomFood(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
		container = customFoodFix(container);
		int itemHunger = getCustomFoodHunger(container);
		float itemSaturation = getCustomFoodSaturation(container);
		int itemChance = getCustomFoodChance(container);
		if (itemHunger != -1) {
			container.remove(Utils.namespacedKey(customFoodName + "_hunger_" + Integer.toString(itemHunger)));
		}
		if (itemSaturation != -1.0F) {
			container.remove(Utils.namespacedKey(customFoodName + "_saturation_" + Float.toString(itemSaturation)));
		}
		if (itemChance != -1) {
			container.remove(Utils.namespacedKey(customFoodName + "_chance_" + Integer.toString(itemChance)));
		}
		if (container.has(customFoodNamespacedKey,PersistentDataType.STRING)) {
			container.remove(customFoodNamespacedKey);
		}
		container = removeAllCustomFoodEffects(container);
		List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : null;
		if (lore != null && !lore.isEmpty()) {
			for (int i = lore.size() - 1; i >= 0; i--) {
				if (lore.get(i).contains("Hunger:") || lore.get(i).contains("Saturation:") || lore.get(i).contains("Chance:")) {
					lore.remove(i);
				}
			}
		}
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
	}

	static void addCustomFood(ItemStack item, int hunger, float saturation, int chance) {
		removeCustomFood(item);
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
		container.set(customFoodNamespacedKey,PersistentDataType.STRING,"protected");
		container.set(Utils.namespacedKey(customFoodName + "_hunger_" + Integer.toString(hunger)),PersistentDataType.STRING,"protected");
		container.set(Utils.namespacedKey(customFoodName + "_saturation_" + Float.toString(saturation)),PersistentDataType.STRING,"protected");
		container.set(Utils.namespacedKey(customFoodName + "_chance_" + Integer.toString(chance)),PersistentDataType.STRING,"protected");
		itemMeta = addCustomFoodLore(itemMeta,hunger,saturation);
		item.setItemMeta(itemMeta);
	}
	
	private static ItemMeta addCustomFoodLore(ItemMeta itemMeta, int hunger, float saturation) {
		List<String> lore = new ArrayList<String>();
		lore.add(Utils.chatColors("&7Hunger: &c" + Integer.toString(hunger)));
		lore.add(Utils.chatColors("&7Saturation: &a" + Float.toString(saturation)));
		itemMeta.setLore(lore);
		return itemMeta;
	}
	
	private static PotionEffect getCustomFoodEffectFromKey(PersistentDataContainer container, String checkKey, PotionEffectType effect) {
		if (checkKey.toLowerCase().startsWith(effect.getName().toLowerCase())) {
			String[] splitEffect = checkKey.toLowerCase().split(effect.getName().toLowerCase() + "_");
			if (splitEffect.length == 2) {
				String[] splitEffectInfo = splitEffect[1].split("_");
				if (splitEffectInfo.length == 2) {
					try {
						int dur = Integer.parseInt(splitEffectInfo[0]);
						int amp = Integer.parseInt(splitEffectInfo[1]) - 1;
						if (dur >= -1 && dur <= 1000000 && amp >= -1 && amp <= 255) {
							return new PotionEffect(effect,dur,amp);
						}
					} catch (Exception e) {
					}
				}
			}
		}
		return null;
	}
	
	static List<PotionEffect> getCustomFoodEffects(PersistentDataContainer container) {
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		List<NamespacedKey> keys = new ArrayList<NamespacedKey>(container.getKeys());
		if (keys != null) {
			for (int i = 0; i < keys.size(); i++) {
				if (keys.get(i).getKey().toLowerCase().startsWith(customFoodEffectName + "_")) {
					String[] splitKey = keys.get(i).getKey().toLowerCase().split(customFoodEffectName + "_");
					if (splitKey.length == 2) {
						for (PotionEffectType effect : PotionEffectType.values()) {
							PotionEffect effectFromKey = getCustomFoodEffectFromKey(container,splitKey[1],effect);
							if (effectFromKey != null) {
								effects.add(effectFromKey);
							}
						}
					}
				}
			}
		}
		if (effects != null && !effects.isEmpty()) return effects;
		return null;
	}
	
	static void addCustomFoodEffect(ItemStack item, String type, int duration, int amplifier) {
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
		container.set(Utils.namespacedKey(customFoodEffectName + "_" + type.toLowerCase()),PersistentDataType.STRING,"protected");
		container.set(Utils.namespacedKey(customFoodEffectName + "_" + type.toLowerCase() + "_" + Integer.toString(duration) + "_" +
		Integer.toString(amplifier)),PersistentDataType.STRING,"protected");
		item.setItemMeta(itemMeta);
	}

	static void addCustomFoodEffect(ItemStack item, PotionEffect effect) {
		addCustomFoodEffect(item,effect.getType().getName(),effect.getDuration(),effect.getAmplifier() + 1);
	}

	static PersistentDataContainer removeCustomFoodEffect(PersistentDataContainer container, String effectType) {
		PersistentDataContainer newContainer = container;
		List<NamespacedKey> keys = new ArrayList<NamespacedKey>(container.getKeys());
		if (keys != null && newContainer.has(Utils.namespacedKey(customFoodEffectName + "_" + effectType.toLowerCase()),PersistentDataType.STRING)) {
			for (int i = 0; i < keys.size(); i++) {
				if (keys.get(i).getKey().toLowerCase().startsWith(customFoodEffectName + "_" + effectType.toLowerCase())) {
					newContainer.remove(keys.get(i));
				}
			}
		}
		return newContainer;
	}

	static PersistentDataContainer removeAllCustomFoodEffects(PersistentDataContainer container) {
		PersistentDataContainer newContainer = container;
		List<PotionEffect> effects = getCustomFoodEffects(newContainer);
		if (effects != null && effects.size() != 0) {
			for (int i = 0; i < effects.size(); i++) {
				newContainer = removeCustomFoodEffect(newContainer,effects.get(i).getType().getName());
			}
		}
		return newContainer;
	}

	public static boolean consumeFood(Player player, ItemStack item) {
		CustomFood food = null;
		boolean regular = true;
		PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
		if (container.has(customFoodNamespacedKey,PersistentDataType.STRING)) {
			int itemHunger = CustomFoods.getCustomFoodHunger(container);
			float itemSaturation = CustomFoods.getCustomFoodSaturation(container);
			int itemChance = CustomFoods.getCustomFoodChance(container);
			if (itemHunger != -1 && itemSaturation != -1.0F && itemChance != -1) {
				food = new CustomFood(item.getType(),itemHunger,itemSaturation,CustomFoods.getCustomFoodEffects(container), itemChance);
				regular = false;
			} else {
				CustomFoods.removeCustomFood(item);
			}
		}
		if (regular) {
			food = FoodsConfigManager.get(item.getType());
		}
		if (food != null) {
			if (consumeFood(player,food)) {
				Pair<Advancement,Advancement> foods = getFoods();
				if (player.getAdvancementProgress(foods.getFirst()) != null && !player.getAdvancementProgress(foods.getFirst()).isDone()) {
					player.getAdvancementProgress(foods.getFirst()).awardCriteria(
							(new ArrayList<String>(player.getAdvancementProgress(foods.getFirst()).getRemainingCriteria())).get(0));
				}
				ItemStack tempItem = item.clone();
				tempItem.setAmount(1);
				if (tempItem.equals(new ItemStack(tempItem.getType()))) {
					if (!player.getAdvancementProgress(foods.getSecond()).getRemainingCriteria().isEmpty() &&
							player.getAdvancementProgress(foods.getSecond()).getRemainingCriteria().contains(item.getType().name().toLowerCase())) {
						player.getAdvancementProgress(foods.getSecond()).awardCriteria(item.getType().name().toLowerCase());
					}
				}
				if (player.getGameMode() != GameMode.CREATIVE) {
					ItemStack newItem = null;
					if (item.getType() == Material.HONEY_BOTTLE) {
						newItem = new ItemStack(Material.GLASS_BOTTLE);
					} else if (equipSound.contains(item.getType())) {
						newItem = new ItemStack(Material.BOWL);
					}
					if (item.getAmount() == 1) {
						player.getInventory().setItemInMainHand(newItem);
					} else {
						item.setAmount(item.getAmount() - 1);
						if (newItem != null) {
							player.getInventory().addItem(newItem);
						}
					}
				}
				return true;
			} else if (item.getType() == Material.PLAYER_HEAD && container.has(customFoodNamespacedKey,PersistentDataType.STRING)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean consumeFood(Player player, CustomFood food) {
		if (player.getFoodLevel() >= CustomFoods.maxHunger && player.getSaturation() >= CustomFoods.maxSaturation && food.chance < 100 &&
				player.getGameMode() != GameMode.CREATIVE) {
			return false;
		}
		int hunger = player.getFoodLevel() + food.hunger;
		Float saturation = player.getSaturation() + food.saturation;
		player.setFoodLevel(hunger > CustomFoods.maxHunger ? CustomFoods.maxHunger : hunger);
		player.setSaturation(saturation > CustomFoods.maxSaturation ? CustomFoods.maxSaturation : saturation);
		if (food.effects != null && food.chance > 0) {
			if (food.chance >= 100) {
				applyEffects(player,food.effects);
			} else if (food.chance > (new Random().nextInt(100))) {
				applyEffects(player,food.effects);
			}
		}
		if (food.material == Material.HONEY_BOTTLE) {
			player.playSound(player.getLocation(),Sound.ITEM_HONEY_BOTTLE_DRINK,1F,1F);
			player.playSound(player.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1F,1F);
		} else {
			player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_BURP,1F,1F);
			player.playSound(player.getLocation(),Sound.ENTITY_GENERIC_EAT,1F,1F);
			if (equipSound.contains(food.material)) {
				player.playSound(player.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1F,1F);
			}
		}
		return true;
	}
	
	private static void applyEffects(Player player, List<PotionEffect> effects) {
		effects.forEach(effect -> {
			if (effect.getAmplifier() == -1) {
				player.removePotionEffect(effect.getType());
			} else {
				player.addPotionEffect(new PotionEffect(effect.getType(),effect.getDuration() * 20,effect.getAmplifier()));
			}
		});
	}
	
	private static Pair<Advancement,Advancement> getFoods() {
		if (food1 == null || food2 == null) {
			Iterator<Advancement> advancements = Bukkit.advancementIterator();
			while (advancements.hasNext()) {
				Advancement advancement = advancements.next();
				if (advancement.getKey().getKey().toLowerCase().contains("husbandry")) {
					if (advancement.getKey().getKey().toLowerCase().contains("root")) {
						food1 = advancement;
					} else if (advancement.getKey().getKey().toLowerCase().contains("balanced_diet")) {
						food2 = advancement;
					}
				}
			}
		}
		return new Pair<Advancement,Advancement>(food1,food2);
	}
}