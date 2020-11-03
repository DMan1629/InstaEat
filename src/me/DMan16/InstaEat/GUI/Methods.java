package me.DMan16.InstaEat.GUI;

import java.util.Arrays;
import java.util.function.Consumer;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mojang.datafixers.util.Pair;

import me.DMan16.InstaEat.InstaEat.CustomFood;
import me.DMan16.InstaEat.InstaEat.InstaEat;
import me.DMan16.InstaEat.Utils.Utils;

public class Methods {
	public static final Consumer<Pair<Menu,Object>> updateEditMethod = (info) -> {((EditMenu) info.getFirst()).update();};
	
	public static final Consumer<Pair<Menu,Object>> updateContentMethod = (info) -> {((ContentMenu) info.getFirst()).updateContent();};
	
	public static final Consumer<Pair<Menu,Object>> setModeMethod = (info) -> {((EditMenu) info.getFirst()).setMode((int) info.getSecond());};
	
	public static final Consumer<Pair<Menu,Object>> unsetFoodMethod = (info) -> {
		Consumer<Pair<Menu,Object>> unsetMethod = (info2) -> {
			EditMenu menu = (EditMenu) info2.getFirst();
			menu.currentFood.unset();
			if (menu.type == MenuType.EDITVANILLA) InstaEat.FoodsManager.unset(menu.getItem());
			else menu.setItem();
		};
		EditMenu menu = (EditMenu) info.getFirst();
		menu.setIncrementButtons(null,false,0,0,0,Buttons.unset(unsetMethod,null,null));
	};
	
	private static final Consumer<Pair<Menu,Object>> unsetEffectMethod = (info) -> {
		EditMenu menu = (EditMenu) info.getFirst();
		PotionEffectType type = (PotionEffectType) info.getSecond();
		PotionEffect effect = menu.currentFood.getEffect(type);
		Consumer<Pair<Menu,Object>> unsetMethod = (info2) -> {
			((EditMenu) info2.getFirst()).currentFood.removeEffect(effect);
		};
		menu.setIncrementButtons(null,false,0,0,0,Buttons.unset(unsetMethod,Arrays.asList("&a" + Utils.splitCapitalize(type.getName(),"_")),type));
	};
	
	private static Consumer<Pair<Menu,Object>> amplifierIncrementMethod(PotionEffectType type) {
		Consumer<Pair<Menu,Object>> incrementMethod = (info) -> {
			EditMenu menu = (EditMenu) info.getFirst();
			int amp = (int) ((double) info.getSecond());
			PotionEffect effect = menu.currentFood.getEffect(type);
			if (effect == null) {
				effect = new PotionEffect(type,1,amp);
			} else {
				int dur = (effect.getAmplifier() + amp == -1) ? 1 : effect.getDuration();
				effect = new PotionEffect(type,dur,effect.getAmplifier() + amp);
			}
			menu.currentFood.addOrChangeEffect(effect);
			menu.setIncrementButtons(amplifierIncrementMethod(type),false,effect.getAmplifier(),255,-1,
					Buttons.info(Arrays.asList("&b&o" + Utils.splitCapitalize(type.getName(),"_") + " &aLevel: &f" +
					(effect == null ? "&cNot set" : (effect.getAmplifier() == -1 ? "Remove" : Integer.toString(effect.getAmplifier()))))));
		};
		return incrementMethod;
	}
	
	private static final Consumer<Pair<Menu,Object>> amplifierMethod = (info) -> {
		EditMenu menu = (EditMenu) info.getFirst();
		PotionEffectType type = (PotionEffectType) info.getSecond();
		PotionEffect effect = menu.currentFood.getEffect(type);
		Integer current = effect == null ? null : effect.getAmplifier();
		menu.setIncrementButtons(amplifierIncrementMethod(type),false,current,255,-1,
				Buttons.info(Arrays.asList("&b&o" + Utils.splitCapitalize(type.getName(),"_") + " &aLevel: &f" +
				(current == null ? "&cNot set" : (current == -1 ? "Remove" : Integer.toString(current + 1))))));
	};
	
	private static Consumer<Pair<Menu,Object>> durationIncrementMethod(PotionEffectType type) {
		Consumer<Pair<Menu,Object>> incrementMethod = (info) -> {
			EditMenu menu = (EditMenu) info.getFirst();
			int dur = (int) ((double) info.getSecond());
			PotionEffect effect = menu.currentFood.getEffect(type);
			if (effect == null) {
				effect = new PotionEffect(type,dur,0);
			} else {
				effect = new PotionEffect(type,effect.getDuration() + dur,effect.getAmplifier());
			}
			menu.currentFood.addOrChangeEffect(effect);
			int max = (effect == null || effect.getAmplifier() != -1) ? 1000000 : 1;
			menu.setIncrementButtons(durationIncrementMethod(type),false,effect.getDuration(),max,1,
					Buttons.info(Arrays.asList("&b&o" + Utils.splitCapitalize(type.getName(),"_") + " &aDuration: &f" + 
					(effect.getDuration() == -1 ? "&cNot set" : Integer.toString(effect.getDuration())))));
		};
		return incrementMethod;
	}
	
	private static final Consumer<Pair<Menu,Object>> durationMethod = (info) -> {
		EditMenu menu = (EditMenu) info.getFirst();
		PotionEffectType type = (PotionEffectType) info.getSecond();
		PotionEffect effect = menu.currentFood.getEffect(type);
		int current = effect == null ? -1 : effect.getDuration();
		int max = (effect == null || effect.getAmplifier() != -1) ? 1000000 : 1;
		menu.setIncrementButtons(durationIncrementMethod(type),false,current,max,1,
				Buttons.info(Arrays.asList("&b&o" + Utils.splitCapitalize(type.getName(),"_") + " &aDuration: &f" + 
				(current == -1 ? "&cNot set" : Integer.toString(current)))));
	};
	
	public static final Consumer<Pair<Menu,Object>> effectMethod = (info) -> {
		EditMenu menu = (EditMenu) info.getFirst();
		PotionEffectType type = (PotionEffectType) info.getSecond();
		Button old = menu.invMap.get(menu.loc(menu.incrementCenter.add(-1,0)));
		if (old != null) {
			if (!((PotionEffectType) old.info).equals(type)) {
				menu.setIncrementButtons(null,false,0,0,0,null);
			}
		}
		menu.button(menu.incrementCenter.add(-1,-1),Buttons.unset(unsetEffectMethod,Arrays.asList("&b&o" + Utils.splitCapitalize(type.getName(),"_")),type));
		menu.button(menu.incrementCenter.add(-1,0),Buttons.amplifier(amplifierMethod,type));
		menu.button(menu.incrementCenter.add(-1,1),Buttons.duration(durationMethod,type));
	};
	
	private static Consumer<Pair<Menu,Object>> hungerIncrementMethod() {
		Consumer<Pair<Menu,Object>> incrementMethod = (info) -> {
			EditMenu menu = (EditMenu) info.getFirst();
			int hunger = (int) ((double) info.getSecond());
			menu.currentFood.addHunger(hunger);
			Integer newHunger = menu.currentFood.hunger();
			menu.setIncrementButtons(hungerIncrementMethod(),false,newHunger,CustomFood.maxHunger,-CustomFood.maxHunger,
					Buttons.info(Arrays.asList("&aHunger: &f" + (newHunger == null ? "&cNot set" : Integer.toString(newHunger)))));
		};
		return incrementMethod;
	}
	
	public static final Consumer<Pair<Menu,Object>> hungerMethod = (info) -> {
		EditMenu menu = (EditMenu) info.getFirst();
		Integer current = menu.currentFood.hunger();
		menu.setIncrementButtons(hungerIncrementMethod(),false,current,CustomFood.maxHunger,-CustomFood.maxHunger,
				Buttons.info(Arrays.asList("&aHunger: &f" + (current == null ? "&cNot set" : Integer.toString(current)))));
	};
	
	private static Consumer<Pair<Menu,Object>> saturationIncrementMethod() {
		Consumer<Pair<Menu,Object>> incrementMethod = (info) -> {
			EditMenu menu = (EditMenu) info.getFirst();
			float saturation = (float) ((double) info.getSecond());
			menu.currentFood.addSaturation(saturation);
			Float newSaturation = menu.currentFood.saturation();
			menu.setIncrementButtons(saturationIncrementMethod(),true,newSaturation,CustomFood.maxSaturation,-CustomFood.maxSaturation,
					Buttons.info(Arrays.asList("&aSaturation: &f" + (newSaturation == null ? "&cNot set" : Float.toString(newSaturation)))));
		};
		return incrementMethod;
	}
	
	public static final Consumer<Pair<Menu,Object>> saturationMethod = (info) -> {
		EditMenu menu = (EditMenu) info.getFirst();
		Float current = menu.currentFood.saturation();
		menu.setIncrementButtons(saturationIncrementMethod(),true,current,CustomFood.maxSaturation,-CustomFood.maxSaturation,
				Buttons.info(Arrays.asList("&aSaturation: &f" + (current == null ? "&cNot set" : Float.toString(current)))));
	};
	
	private static Consumer<Pair<Menu,Object>> chanceIncrementMethod() {
		Consumer<Pair<Menu,Object>> incrementMethod = (info) -> {
			EditMenu menu = (EditMenu) info.getFirst();
			int chance = (int) ((double) info.getSecond());
			menu.currentFood.addChance(chance);
			Integer newChance = menu.currentFood.chance();
			menu.setIncrementButtons(chanceIncrementMethod(),false,newChance,100,0,Buttons.info(Arrays.asList("&aChance: &f" + 
					(newChance == null ? "&cNot set" : Integer.toString(newChance)))));
		};
		return incrementMethod;
	}
	
	public static final Consumer<Pair<Menu,Object>> chanceMethod = (info) -> {
		EditMenu menu = (EditMenu) info.getFirst();
		Integer current = menu.currentFood.chance();
		menu.setIncrementButtons(chanceIncrementMethod(),false,current,100,0,Buttons.info(Arrays.asList("&aChance: &f" + 
				(current == null ? "&cNot set" : Integer.toString(current)))));
	};
}