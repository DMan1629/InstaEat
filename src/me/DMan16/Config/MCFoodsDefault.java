package me.DMan16.Config;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.DMan16.CustomFood.CustomFood;

public enum MCFoodsDefault {
	APPLE(Material.APPLE,4,2.4F,null,0),
	BAKED_POTATO(Material.BAKED_POTATO,5,6.0F,null,0),
	BEETROOT(Material.BEETROOT,1,1.2F,null,0),
	BEETROOT_SOUP(Material.BEETROOT_SOUP,6,7.2F,null,0),
	BREAD(Material.BREAD,5,6.0F,null,0),
	CARROT(Material.CARROT,3,3.6F,null,0),
	COOKED_CHICKEN(Material.COOKED_CHICKEN,6,7.2F,null,0),
	COOKED_COD(Material.COOKED_COD,5,6.0F,null,0),
	COOKED_MUTTON(Material.COOKED_MUTTON,6,9.6F,null,0),
	COOKED_PORKCHOP(Material.COOKED_PORKCHOP,8,12.8F,null,0),
	COOKED_RABBIT(Material.COOKED_RABBIT,5,6.0F,null,0),
	COOKED_SALMON(Material.COOKED_SALMON,6,9.6F,null,0),
	COOKIE(Material.COOKIE,2,0.4F,null,0),
	DRIED_KELP(Material.DRIED_KELP,1,0.6F,null,0),
	ENCHANTED_GOLDEN_APPLE(Material.ENCHANTED_GOLDEN_APPLE,4,9.6F,Arrays.asList(
			new PotionEffect(PotionEffectType.REGENERATION,20,1),
			new PotionEffect(PotionEffectType.ABSORPTION,120,3),
			new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,300,0),
			new PotionEffect(PotionEffectType.FIRE_RESISTANCE,300,0)),100),
	GOLDEN_APPLE(Material.GOLDEN_APPLE,4,9.6F,Arrays.asList(
			new PotionEffect(PotionEffectType.REGENERATION,5,1),
			new PotionEffect(PotionEffectType.ABSORPTION,120,0)),100),
	GOLDEN_CARROT(Material.GOLDEN_CARROT,6,14.4F,null,0),
	HONEY_BOTTLE(Material.HONEY_BOTTLE,6,1.2F,Arrays.asList(
			new PotionEffect(PotionEffectType.POISON,-1,-1)),100),
	MELON_SLICE(Material.MELON_SLICE,2,1.2F,null,0),
	MUSHROOM_STEW(Material.MUSHROOM_STEW,6,7.2F,null,0),
	POISONOUS_POTATO(Material.POISONOUS_POTATO,2,1.2F,Arrays.asList(
			new PotionEffect(PotionEffectType.POISON,4,0)),60),
	POTATO(Material.POTATO,1,0.6F,null,0),
	PUFFERFISH(Material.PUFFERFISH,1,0.2F,Arrays.asList(
			new PotionEffect(PotionEffectType.HUNGER,15,2),
			new PotionEffect(PotionEffectType.CONFUSION,15,1),
			new PotionEffect(PotionEffectType.POISON,60,3)),100),
	PUMPKIN_PIE(Material.PUMPKIN_PIE,8,4.8F,null,0),
	RABBIT_STEW(Material.RABBIT_STEW,10,12.0F,null,0),
	BEEF(Material.BEEF,3,1.8F,null,0),
	CHICKEN(Material.CHICKEN,2,1.2F,Arrays.asList(
			new PotionEffect(PotionEffectType.HUNGER,30,0)),30),
	COD(Material.COD,2,0.4F,null,0),
	MUTTON(Material.MUTTON,2,1.2F,null,0),
	PORKCHOP(Material.PORKCHOP,3,1.8F,null,0),
	RABBIT(Material.RABBIT,3,1.8F,null,0),
	SALMON(Material.SALMON,2,0.4F,null,0),
	ROTTEN_FLESH(Material.ROTTEN_FLESH,4,0.8F,Arrays.asList(
			new PotionEffect(PotionEffectType.HUNGER,30,0)),80),
	SPIDER_EYE(Material.SPIDER_EYE,3,3.2F,Arrays.asList(
			new PotionEffect(PotionEffectType.POISON,4,0)),100),
	COOKED_BEEF(Material.COOKED_BEEF,8,12.8F,null,0),
	SUSPICIOUS_STEW(Material.SUSPICIOUS_STEW,6,7.2F,null,0),
	SWEET_BERRIES(Material.SWEET_BERRIES,2,0.4F,null,0),
	TROPICAL_FISH(Material.TROPICAL_FISH,1,0.2F,null,0);
	
	public final Material material;
	public final int hunger;
	public final float saturation;
	public final List<PotionEffect> effects;
	public final int chance;
	
	private MCFoodsDefault(Material material, int hunger, float saturation, List<PotionEffect> effects, int chance) {
		this.material = material;
		this.hunger = hunger;
		this.saturation = saturation;
		this.effects = effects;
		this.chance = chance;
	}
	
	public static MCFoodsDefault getFood(Material material) {
		for (MCFoodsDefault food : MCFoodsDefault.values()) {
			if (food.material.equals(material)) {
				return food;
			}
		}
		return null;
	}
	
	public static boolean isFood(Material material) {
		return (material.isEdible() && material != Material.CHORUS_FRUIT);
	}
	
	public CustomFood asFood() {
		return new CustomFood(material, hunger, saturation, effects, chance);
	}
}