package me.DMan16.CustomFood;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

public class CustomFood {
	public final Material material;
	public final int hunger;
	public final float saturation;
	public final List<PotionEffect> effects;
	public final int chance;
	
	public CustomFood(Material material, int hunger, float saturation, List<PotionEffect> effects, int chance) {
		this.material = material;
		this.hunger = hunger < 0 ? 0 : (hunger > CustomFoods.maxHunger ? CustomFoods.maxHunger : hunger);
		this.saturation = saturation < 0 ? 0 : (saturation > CustomFoods.maxSaturation ? CustomFoods.maxSaturation : saturation);
		this.effects = effects;
		this.chance = chance < 0 ? 0 : chance;
	}
}