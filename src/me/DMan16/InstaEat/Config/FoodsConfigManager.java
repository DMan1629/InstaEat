package me.DMan16.InstaEat.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;

import me.DMan16.InstaEat.InstaEat.CustomFood;
import me.DMan16.InstaEat.Utils.Utils;

@SuppressWarnings("unchecked")
public class FoodsConfigManager {
	private HashMap<Material,CustomFood> foods;
	private JSONObject foodsArray;
	private String fileName;
	private JsonLoader loader;
	private JSONObject defaultFoods;
	private List<String> usingDefault;
	private List<String> fieldNames = CustomFood.fieldNames;
	private List<String> effectsFieldNames = CustomFood.effectsFieldNames;
	public boolean stop;
	
	public FoodsConfigManager() {
		foods = new HashMap<Material,CustomFood>();
		fileName = "foods.json";
		usingDefault = new ArrayList<String>();
		stop = false;
		try {
			loader = new JsonLoader();
			setDefault();
			this.foodsArray = loader.read(fileName,defaultFoods);
			setFoods();
			write();
			if (!usingDefault.isEmpty()) {
				Utils.chatColorsLog("&aFollowing foods were not found - using default values: " + String .join(", ",usingDefault));
			}
		} catch (IOException e) {
			Utils.chatColorsLog("&cError reading config file.");
			stop = true;
		}
	}
	
	public boolean isSet(Material material) {
		if (material == null) return false;
		return foods.containsKey(material);
	}
	
	public boolean isSet(ItemStack item) {
		return isSet(item == null ? null : item.getType());
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void unset(Material material) {
		if (!isSet(material)) return;
		foods.remove(material);
		write();
	}
	
	public void unset(ItemStack item) {
		unset(item == null ? null : item.getType());
	}
	
	public void set(CustomFood food) {
		if (food == null) return;
		if (food.item() == null) return;
		if (MCFoodsDefault.isFood(food.item().getType()) || MCFoodsDefault.isDefaultConfigFood(food.item().getType())) {
			if (food.fullySet()) {
				unset(food.item().getType());
				foods.put(food.item().getType(),food);
				write();
			} else if (food.notSet()) {
				unset(food.item().getType());
			}
		}
	}
	
	public CustomFood get(Material material) {
		if (material == null || !(MCFoodsDefault.isFood(material) || MCFoodsDefault.isDefaultConfigFood(material))) return null;
		if (foods.containsKey(material)) return new CustomFood(foods.get(material));
		else return new CustomFood(new ItemStack(material),null,null,null,null);
	}
	
	public void setFoods() {
		HashMap<String,String> foodsMap = (HashMap<String,String>) foodsArray;
		foodsMap.keySet().forEach(food -> {
			try {
				Material material = Material.getMaterial(food.trim().replaceAll(" +","_").toUpperCase());
				CustomFood defaultFood = MCFoodsDefault.getFood(material).asFood();
				JSONObject info = (JSONObject) foodsArray.get(food);
				int hunger, chance;
				float saturation;
				List<PotionEffect> effects = new ArrayList<PotionEffect>();
				try {
					hunger = Integer.parseInt(info.get(fieldNames.get(0)).toString());
				} catch (Exception e) {
					hunger = defaultFood.hunger();
				}
				try {
					saturation = Float.parseFloat(info.get(fieldNames.get(1)).toString());
				} catch (Exception e) {
					saturation = defaultFood.saturation();
				}
				try {
					chance = Integer.parseInt(info.get(fieldNames.get(2)).toString());
				} catch (Exception e) {
					chance = defaultFood.chance();
				}
				try {
					HashMap<String,String> effectsMap = (HashMap<String,String>) info.get(fieldNames.get(3));
					for (String effect : effectsMap.keySet()) {
						try {
							PotionEffectType type = PotionEffectType.getByName(effect.replace(" ","_").toUpperCase());
							JSONObject effectInfo = (JSONObject) ((JSONObject) info.get(fieldNames.get(3))).get(effect);
							int amp = ((Long) effectInfo.get(effectsFieldNames.get(0))).intValue();
							int dur = ((Long) effectInfo.get(effectsFieldNames.get(1))).intValue();
							effects.add(new PotionEffect(type,dur,amp));
						} catch (Exception e) {}
					}
				} catch (Exception e) {
					effects = defaultFood.effects();
				}
				foods.put(material, new CustomFood(new ItemStack(material),hunger,saturation,chance,effects));
			} catch (Exception e) {}
		});
	}
	
	private void write() {
		JSONObject foodsJSON = new JSONObject();
		for (Material food : foods.keySet()) {
			foodsJSON.put(Utils.JString(food.name()),foods.get(food).toJSONObject());
		}
		loader.write(fileName,foodsJSON);
	}
	
	public boolean isEmpty() {
		return foods == null || foods.isEmpty();
	}
	
	private void setDefault() {
		defaultFoods = new JSONObject();
		for (Material material : Material.values()) {
			if (MCFoodsDefault.isDefaultConfigFood(material)) {
				defaultFoods.put(Utils.JString(material.name()),MCFoodsDefault.getFood(material).asFood().toJSONObject());
			}
		}
	}
}