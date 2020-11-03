package me.DMan16.InstaEat.InstaEat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;

import me.DMan16.InstaEat.Config.MCFoodsDefault;
import me.DMan16.InstaEat.Utils.Utils;

public class CustomFood {
	public static final int maxHunger = 20;
	public static final int maxSaturation = 20;
	public static final List<String> fieldNames = Arrays.asList("hunger","saturation","chance","effects");
	public static final List<String> effectsFieldNames = Arrays.asList("amplifier","duration");
	
	private final String customFoodName = "customfood";
	private final String customFoodEffectName = "customfoodeffect";
	private final List<Material> equipSound = Arrays.asList(Material.BEETROOT_SOUP,Material.MUSHROOM_STEW,Material.RABBIT_STEW,
			Material.SUSPICIOUS_STEW,Material.HONEY_BOTTLE);
	private final String seconds = "seconds";
	private final String[] defaultPrefixes = {"&a","&l","&f","&b&o","&2"};
	
	private ItemStack item;
	private Integer hunger;
	private Float saturation;
	private Integer chance;
	private List<PotionEffect> effects;
	
	private void Set(ItemStack item, Integer hunger, Float saturation, Integer chance, List<PotionEffect> effects) {
		this.item = item == null ? null : item.clone();
		this.hunger = hunger;
		this.saturation = saturation;
		this.chance = chance;
		this.effects = new ArrayList<PotionEffect>();
		if (hunger != null && saturation != null && chance != null) {
			hunger(hunger);
			saturation(saturation);
			chance(chance);
			effects(effects);
		}
	}
	
	public CustomFood(ItemStack item, Integer hunger, Float saturation, Integer chance, List<PotionEffect> effects) {
		Set(item,hunger,saturation,chance,effects);
	}
	
	public CustomFood(CustomFood food) {
		Set(food.item,food.hunger,food.saturation,food.chance,food.effects);
	}
	
	public CustomFood(ItemStack item) {
		Integer hunger = null;
		Float saturation = null;
		Integer chance = null;
		List<PotionEffect> effects = null;
		if (item == null) {
			Set(null,hunger,saturation,chance,effects);
		} else {
			ItemStack clone = item.clone();
			ItemMeta meta = clone.getItemMeta();
			PersistentDataContainer container = meta.getPersistentDataContainer();
			if (container.getKeys() != null) {
				for (NamespacedKey key : container.getKeys()) {
					if (key.getKey().toLowerCase().startsWith(customFoodName + "_")) {
						for (int i = 0; i < fieldNames.size(); i++) {
							if (key.getKey().toLowerCase().startsWith(customFoodName + "_" + fieldNames.get(i) + "_")) {
								String[] splitKey = key.getKey().toLowerCase().split(customFoodName + "_" + fieldNames.get(i) + "_");
								if (splitKey.length == 2) {
									try {
										if (i == 0) {
											if (hunger == null) hunger = Integer.parseInt(splitKey[1]);
										} else if (i == 1) {
											if (saturation == null) saturation = Float.parseFloat(splitKey[1]);
										} else if (i == 2) {
											if (chance == null) chance = Integer.parseInt(splitKey[1]);
										}
									} catch (Exception e) {
									}
								}
								break;
							}
						}
					} else if (key.getKey().toLowerCase().startsWith(customFoodEffectName + "_")) {
						String[] splitKey = key.getKey().toLowerCase().split(customFoodEffectName + "_");
						if (splitKey.length == 2) {
							for (PotionEffectType effect : PotionEffectType.values()) {
								if (splitKey[1].toLowerCase().startsWith(effect.getName().toLowerCase())) {
									String[] splitEffect = splitKey[1].toLowerCase().split(effect.getName().toLowerCase() + "_");
									if (splitEffect.length == 2) {
										String[] splitEffectInfo = splitEffect[1].split("_");
										if (splitEffectInfo.length == 2) {
											try {
												int dur = Integer.parseInt(splitEffectInfo[0]);
												int amp = Integer.parseInt(splitEffectInfo[1]);
												if (effects == null) {
													effects = new ArrayList<PotionEffect>();
												}
												effects.add(new PotionEffect(effect,dur,amp));
											} catch (Exception e) {
											}
										}
									}
									break;
								}
							}
						}
					}
				}
			}
			clone.setItemMeta(meta);
			if (chance == null) chance = 0;
			Set(clone,hunger,saturation,chance,effects);
		}
	}

	private void removeKey(String key) {
		removeKey(Utils.namespacedKey(key));
	}
	
	private void removeKey(NamespacedKey key) {
		if (item == null) return;
		ItemMeta meta = item.getItemMeta();
		try {
			meta.getPersistentDataContainer().remove(key);
		} catch (Exception e) {
		}
		item.setItemMeta(meta);
	}
	
	private void setKey(String key) {
		setKey(Utils.namespacedKey(key));
	}
	
	private void setKey(NamespacedKey key) {
		if (item == null) return;
		ItemMeta meta = item.getItemMeta();
		try {
			meta.getPersistentDataContainer().set(key,PersistentDataType.STRING,"protected");
		} catch (Exception e) {
		}
		item.setItemMeta(meta);
	}
	
	public ItemStack item() {
		return item == null ? null : item.clone();
	}
	
	public void setItemAmount(int amount) {
		if (item != null) item.setAmount(amount);
	}
	
	public Material material() {
		return item == null ? null : item.getType();
	}
	
	public void material(Material material) {
		item.setType(material);
	}
	
	public Integer hunger() {
		return hunger;
	}
	
	public void addHunger(int hunger) {
		hunger(hunger + (this.hunger == null ? 0 : this.hunger));
	}
	
	public void hunger(int hunger) {
		if (this.hunger != null) removeKey(customFoodName + "_" + fieldNames.get(0) + "_" + Integer.toString(this.hunger));
		this.hunger = fixHunger(hunger);
		setKey(customFoodName + "_" + fieldNames.get(0) + "_" + Integer.toString(this.hunger));
		updateLore();
	}
	
	private int fixHunger(int hunger) {
		return Math.abs(hunger) > maxHunger ? (hunger / Math.abs(hunger)) * maxHunger : hunger;
	}
	
	public void addSaturation(float saturation) {
		saturation(saturation + (this.saturation == null ? 0 : this.saturation));
	}
	
	public Float saturation() {
		return saturation;
	}
	
	public void saturation(float saturation) {
		if (this.saturation != null) removeKey(customFoodName + "_" + fieldNames.get(1) + "_" + Float.toString(this.saturation));
		this.saturation = fixSaturation(saturation);
		setKey(customFoodName + "_" + fieldNames.get(1) + "_" + Float.toString(this.saturation));
		updateLore();
	}
	
	private float fixSaturation(float saturation) {
		saturation = Float.parseFloat((new DecimalFormat("0.0")).format(saturation));
		return Math.abs(saturation) > maxSaturation ? (saturation / Math.abs(saturation)) * maxSaturation : saturation;
	}
	
	public void addChance(int chance) {
		chance(chance + (this.chance == null ? 0 : this.chance));
	}
	
	public Integer chance() {
		return chance;
	}
	
	public void chance(int chance) {
		if (this.chance != null) removeKey(customFoodName + "_" + fieldNames.get(2) + "_" + Integer.toString(this.chance));
		this.chance = chance < 0 ? 0 : chance;
		setKey(customFoodName + "_" + fieldNames.get(2) + "_" + Integer.toString(this.chance));
		updateLore();
	}
	
	public List<PotionEffect> effects() {
		return effects;
	}
	
	public void effects(List<PotionEffect> effects) {
		if (effects == null) return;
		if (this.effects != null) {
			for (PotionEffect effect : this.effects) {
				removeEffect(effect);
			}
			this.effects = null;
		}
		for (PotionEffect effect : effects) {
			addOrChangeEffect(effect);
		}
	}
	
	public boolean hasEffect(PotionEffectType type) {
		return getEffect(type) != null;
	}
	
	public PotionEffect getEffect(PotionEffectType type) {
		if (effects != null) {
			for (PotionEffect effect : effects) {
				if (effect.getType().equals(type)) return effect;
			}
		}
		return null;
	}
	
	public void addOrChangeEffect(PotionEffect effect) {
		if (item == null) return;
		if (effect == null) return;
		int dur = effect.getDuration();
		int amp = effect.getAmplifier();
		dur = dur > 1000000 ? 1000000 : (dur < 0 ? 0 : dur);
		amp = amp > 255 ? 255 : (amp < -1 ? -1 : amp);
		effect = new PotionEffect(effect.getType(),dur,amp);
		removeEffect(effect.getType());
		if (effects == null) {
			effects = new ArrayList<PotionEffect>();
		}
		effects.add(effect);
		setKey(customFoodEffectName + "_" + effect.getType().getName().toLowerCase() + "_" + Integer.toString(effect.getDuration()) + "_" +
				Integer.toString(effect.getAmplifier()));
		updateLore();
	}
	
	public void removeEffect(PotionEffectType type) {
		if (type == null) return;
		PotionEffect effect = getEffect(type);
		removeEffect(effect);
	}
	
	public void removeEffect(PotionEffect effect) {
		if (item == null) return;
		if (effect == null) return;
		if (effects == null) return;
		if (!effects.contains(effect)) return;
		effects.remove(effect);
		removeKey(customFoodEffectName + "_" + effect.getType().getName().toLowerCase() + "_" + Integer.toString(effect.getDuration()) + "_" +
				Integer.toString(effect.getAmplifier()));
		if (effects.isEmpty()) effects = null;
		updateLore();
	}
	
	public void removeAllEffects() {
		if (effects == null) return;
		for (PotionEffect effect : effects) {
			removeEffect(effect);
		}
		effects = null;
	}
	
	private void removeLore() {
		if (item == null) return;
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : null;
		if (lore == null || lore.isEmpty()) return;
		for (int i = lore.size() - 1; i >= 0; i--) {
			if (lore == null) break;
			for (String field : fieldNames) {
				if (lore.get(i).contains(Utils.splitCapitalize(field,null) + ":")) {
					lore.remove(i);
					if (lore.isEmpty()) {
						lore = null;
					}
					break;
				}
			}
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
	
	private void updateLore(String ... prefixes) {
		if (!fullySet()) return;
		removeLore();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
		lore.add(infoHunger(prefixes));
		lore.add(infoSaturation(prefixes));
		meta.setLore(Utils.chatColors(lore));
		item.setItemMeta(meta);
	}
	
	public boolean fullySet() {
		return hunger != null && saturation != null && chance != null && item != null;
	}
	
	public boolean notSet() {
		return hunger == null && saturation == null && chance == null && item != null;
	}
	
	public void unset() {
		if (hunger != null) removeKey(customFoodName + "_" + fieldNames.get(0) + "_" + Integer.toString(hunger));
		if (saturation != null) removeKey(customFoodName + "_" + fieldNames.get(1) + "_" + Float.toString(saturation));
		if (chance != null) 
			try {
				removeKey(customFoodName + "_" + fieldNames.get(2) + "_" + Integer.toString(chance));
			} catch (Exception e) {
			}
		hunger = null;
		saturation = null;
		chance = 0;
		effects(null);
		removeLore();
	}
	
	public ItemStack consume(Player player, boolean mainHand) {
		ItemStack newItem = null;
		if (!fullySet()) return newItem;
		boolean hungerFlag = (player.getFoodLevel() >= maxHunger && !InstaEat.getConfigLoader().getFullHungerEat()) || hunger == null;
		boolean saturationFlag = (player.getSaturation() >= maxSaturation && !InstaEat.getConfigLoader().getFullSaturationEat()) || saturation == null;
		boolean chanceFlag = chance() < 100;
		boolean creative = player.getGameMode() == GameMode.CREATIVE;
		if ((hungerFlag || saturationFlag) && chanceFlag && !creative) return newItem;
		int hunger = player.getFoodLevel() + hunger();
		Float saturation = player.getSaturation() + saturation();
		player.setFoodLevel(Math.max(fixHunger(hunger),0));
		player.setSaturation(Math.max(fixSaturation(saturation),0));
		if (chance() > 0) {
			if (chance() >= 100) {
				applyEffects(player);
			} else if (chance() > (new Random().nextInt(100))) {
				applyEffects(player);
			}
		}
		if (!MCFoodsDefault.isPlaceableFood(material())) {
			if (material() == Material.HONEY_BOTTLE) {
				player.playSound(player.getLocation(),Sound.ITEM_HONEY_BOTTLE_DRINK,1F,1F);
			} else {
				player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_BURP,1F,1F);
				player.playSound(player.getLocation(),Sound.ENTITY_GENERIC_EAT,1F,1F);
			}
			if (equipSound.contains(material())) {
				player.playSound(player.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1F,1F);
			}
		}
		if (player.getAdvancementProgress(InstaEat.food1) != null && !player.getAdvancementProgress(InstaEat.food1).isDone()) {
			player.getAdvancementProgress(InstaEat.food1).awardCriteria(
					(new ArrayList<String>(player.getAdvancementProgress(InstaEat.food1).getRemainingCriteria())).get(0));
		}
		ItemStack tempItem = item.clone();
		tempItem.setAmount(1);
		if (tempItem.equals(new ItemStack(tempItem.getType()))) {
			if (!player.getAdvancementProgress(InstaEat.food2).getRemainingCriteria().isEmpty() &&
					player.getAdvancementProgress(InstaEat.food2).getRemainingCriteria().contains(item.getType().name().toLowerCase())) {
				player.getAdvancementProgress(InstaEat.food2).awardCriteria(item.getType().name().toLowerCase());
			}
		}
		if (!creative) {
			if (item.getType() == Material.HONEY_BOTTLE) {
				newItem = new ItemStack(Material.GLASS_BOTTLE);
			} else if (equipSound.contains(item.getType())) {
				newItem = new ItemStack(Material.BOWL);
			}
			if (item.getAmount() == 1) {
				item = null;
				unset();
			} else {
				item.setAmount(item.getAmount() - 1);
			}
		}
		return newItem;
	}
	
	private void applyEffects(Player player) {
		if (effects == null) return;
		effects.forEach(effect -> {
			if (effect.getAmplifier() == -1) {
				player.removePotionEffect(effect.getType());
			} else {
				player.addPotionEffect(new PotionEffect(effect.getType(),effect.getDuration() * 20,effect.getAmplifier()));
			}
		});
	}
	
	public List<String> info() {
		return info(defaultPrefixes);
	}
	
	public List<String> info(String ... prefixes) {
		List<String> info = new ArrayList<String>();
		if (!fullySet()) info.add("&cNot Set");
		else {
			info.add(infoHunger(prefixes));
			info.add(infoSaturation(prefixes));
			info.add(infoChance(prefixes));
			List<String> infoEffects = infoEffects(prefixes);
			if (infoEffects != null) info.addAll(infoEffects(prefixes));
		}
		return info;
	}
	
	private String[] prefixes(String ... prefixes) {
		String[] prefix = new String[5];
		for (int i = 0; i < 5; i++) {
			try {
				prefix[i] = prefixes[i];
			} catch (Exception e) {
				prefix[i] = defaultPrefixes[i];
			}
		}
		return prefix;
	}

	public String infoHunger(String ... prefixes) {
		String[] prefix = prefixes(prefixes);
		return prefix[0] + prefix[1] + "Hunger: " + prefix[2] + Integer.toString(hunger);
	}

	public String infoSaturation(String ... prefixes) {
		String[] prefix = prefixes(prefixes);
		return prefix[0] + prefix[1] + "Saturation: " + prefix[2] + Float.toString(saturation);
	}

	public String infoChance(String ... prefixes) {
		String[] prefix = prefixes(prefixes);
		return prefix[0] + prefix[1] + "Chance to activate effects: " + prefix[2] + Integer.toString(chance) + "%";
	}

	public List<String> infoEffects(String ... prefixes) {
		if (effects == null || effects.isEmpty()) return null;
		List<String> info = new ArrayList<String>();
		String[] prefix = prefixes(prefixes);
		info.add(prefix[0] + prefix[1] + "Effects: " + prefix[2] + ((effects == null || effects.isEmpty()) ? "None" : ""));
		for (PotionEffect effect : effects) {
			info.add("   " + infoEffect(effect));
		}
		return info;
	}

	public String infoEffect(PotionEffectType type, String ... prefixes) {
		PotionEffect effect = getEffect(type);
		return infoEffect(effect,prefixes);
	}
	
	public String infoEffect(PotionEffect effect, String ... prefixes) {
		if (effect == null) return null;
		String[] prefix = prefixes(prefixes);
		String line = prefix[0] + prefix[3] + Utils.splitCapitalize(effect.getType().getName(),"_") + " ";
		if (effect.getAmplifier() == -1) {
			line += ": " + prefix[2] + "Remove";
		} else {
			line += (effect.getAmplifier() + 1) + ": " + prefix[2] + effect.getDuration() + prefix[0] + prefix[3] + prefix[4] + " " + seconds;
		}
		return line;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put(Utils.JString(fieldNames.get(0)),hunger);
		obj.put(Utils.JString(fieldNames.get(1)),saturation);
		obj.put(Utils.JString(fieldNames.get(2)),chance);
		JSONObject effectsObject = new JSONObject();
		if (effects != null && !effects.isEmpty()) {
			for (PotionEffect effect : effects) {
				JSONObject effectInfo = new JSONObject();
				effectInfo.put(Utils.JString(effectsFieldNames.get(0)),effect.getAmplifier());
				effectInfo.put(Utils.JString(effectsFieldNames.get(1)),effect.getDuration());
				effectsObject.put(Utils.JString(effect.getType().getName()),effectInfo);
			}
		}
		obj.put(Utils.JString(fieldNames.get(3)),effectsObject);
		return obj;
	}
}