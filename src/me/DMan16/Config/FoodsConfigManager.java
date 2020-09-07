package me.DMan16.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.json.simple.JSONObject;

import me.DMan16.CustomFood.CustomFood;
import me.DMan16.Utils.Utils;

@SuppressWarnings("unchecked")
public class FoodsConfigManager {
	private static HashMap<Material,CustomFood> foods;
	private JSONObject foodsArray;
	private String fileName;
	private JsonLoader loader;
	private JSONObject defaultFoods;
	public static boolean errors;
	private List<String> usingDefault;
	private List<String> fieldNames;
	public boolean stop;
	
	public FoodsConfigManager() {
		foods = new HashMap<Material,CustomFood>();
		fileName = "foods.json";
		fieldNames = Arrays.asList("hunger","saturation","chance");
		usingDefault = new ArrayList<String>();
		errors = false;
		stop = false;
		try {
			loader = new JsonLoader();
			setDefault();
			this.foodsArray = loader.read(fileName,foods.getClass(),defaultFoods);
			setFoods();
			if (!usingDefault.isEmpty()) {
				Utils.chatColorsLog("&aFollowing materials were not found - using default values: " + String .join(", ",usingDefault));
			}
			if (errors) {
				Utils.chatColorsLog("&bIllegal items found in the config file - please fix.");
			}
		} catch (IOException e) {
			Utils.chatColorsLog("&cError reading config file.");
			stop = true;
		}
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public static CustomFood get(Material material) {
		if (material == null || !foods.containsKey(material)) return null;
		return foods.get(material);
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
				try {
					hunger = Integer.parseInt(info.get(fieldNames.get(0)).toString());
				} catch (Exception e) {
					hunger = defaultFood.hunger;
				}
				try {
					saturation = Float.parseFloat(info.get(fieldNames.get(1)).toString());
				} catch (Exception e) {
					saturation = defaultFood.saturation;
				}
				try {
					chance = Integer.parseInt(info.get(fieldNames.get(2)).toString());
				} catch (Exception e) {
					chance = defaultFood.chance;
				}
				foods.put(material, new CustomFood(material,hunger,saturation,defaultFood.effects,chance));
			} catch (Exception e) {
				errors = true;
			}
		});
		for (Material material : Material.values()) {
			if (MCFoodsDefault.isFood(material)) {
				if (!foods.keySet().contains(material)) {
					usingDefault.add(WordUtils.capitalizeFully(material.name().replace("_"," ")));
					foods.put(material,MCFoodsDefault.getFood(material).asFood());
				}
			}
		}
	}
	
	public void setFood(JSONObject foodsObject) {
	}
	
	public boolean isEmpty() {
		return foods == null || foods.isEmpty();
	}
	
	private void setDefault() {
		defaultFoods = new JSONObject();
		for (Material material : Material.values()) {
			if (MCFoodsDefault.isFood(material)) {
				CustomFood food = MCFoodsDefault.getFood(material).asFood();
				JSONObject info = new JSONObject();
				info.put(JString(fieldNames.get(0)),food.hunger);
				info.put(JString(fieldNames.get(1)),food.saturation);
				info.put(JString(fieldNames.get(2)),food.chance);
				defaultFoods.put(JString(material.name()),info);
			}
		}
	}
	
	
	
	private JString JString(String str) {
		return new JString(str);
	}



	public class JString implements java.io.Serializable{
		private static final long serialVersionUID = 1L;
		String value;
		public JString(String value) {
			super();
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
		@Override
		public String toString(){
			return this.value;
		}
	}
}