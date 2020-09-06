package me.DMan16.CustomFood;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

public class CustomFood {
	final Material material;
	final int hunger;
	final float saturation;
	final List<PotionEffect> effects;
	final int chance;
	
	public CustomFood(Material material, int hunger, float saturation, List<PotionEffect> effects, int chance) {
		this.material = material;
		this.hunger = hunger;
		this.saturation = saturation;
		this.effects = effects;
		this.chance = chance;
	}
}