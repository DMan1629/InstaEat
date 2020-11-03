package me.DMan16.InstaEat.GUI;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;

import me.DMan16.InstaEat.Config.MCFoodsDefault;
import me.DMan16.InstaEat.InstaEat.CustomFood;
import me.DMan16.InstaEat.Utils.Permissions;
import me.DMan16.InstaEat.Utils.Utils;

public class Buttons {
	private final static Material head = Material.PLAYER_HEAD;
	
	public static Button next() {
		Consumer<Pair<Menu,Object>> method = (info) -> {((ContentMenu) info.getFirst()).next();};
		return createButton(head,"&6Next Page",null,heads.NEXT,method);
	}
	
	public static Button previous() {
		Consumer<Pair<Menu,Object>> method = (info) -> {((ContentMenu) info.getFirst()).previous();};
		return createButton(head,"&6Previous Page",null,heads.PREVIOUS,method);
	}
	
	public static Button back() {
		Consumer<Pair<Menu,Object>> method = (info) -> {info.getFirst().back();};
		return createButton(head,"&dBack",null,heads.BACK,method);
	}
	
	public static Button close() {
		Consumer<Pair<Menu,Object>> method = (info) -> {info.getFirst().player.closeInventory();};
		return createButton(head,"&cClose",null,heads.CLOSE,method);
	}
	
	public static Button empty() {
		return createButton(Material.BLACK_STAINED_GLASS_PANE," ",null,null,null);
	}
	
	public static Button edge() {
		return createButton(Material.WHITE_STAINED_GLASS_PANE," ",null,null,null);
	}
	
	public static Button V() {
		return createButton(Material.GREEN_STAINED_GLASS_PANE,"&a&o&l✓",null,null,null);
	}
	
	public static Button X() {
		return createButton(Material.RED_STAINED_GLASS_PANE,"&c&o&l✗",null,null,null);
	}
	
	public static Button output() {
		return createButton(Material.LIGHT_BLUE_STAINED_GLASS_PANE," ",null,null,null);
	}
	
	public static Button amplifier(Consumer<Pair<Menu,Object>> method, PotionEffectType type) {
		return createButton(Material.GLOWSTONE_DUST,"&aAmplifier",Arrays.asList("&b&o" + Utils.splitCapitalize(type.getName(),"_")),null,method,type);
	}
	
	public static Button duration(Consumer<Pair<Menu,Object>> method, PotionEffectType type) {
		return createButton(Material.REDSTONE,"&aDuration",Arrays.asList("&b&o" + Utils.splitCapitalize(type.getName(),"_")),null,method,type);
	}
	
	public static Button vanilla() {
		List<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add("&aGet and edit");
		lore.add("&avanilla food info.");
		Consumer<Pair<Menu,Object>> method = (info) -> {
			if (Permissions.VanillaFoodPermission(info.getFirst().player)) {
				(new ContentMenu(MenuType.VANILLA,info.getFirst().player)).openMenu();
			} else {
				Utils.chatColorsPlugin(info.getFirst().player,"&cYou do not have access to this function!");
			}
		};
		return createButton(Material.COOKIE,"&fVanilla Food",lore,null,method);
	}
	
	public static Button custom() {
		List<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add("&aGet and edit");
		lore.add("&acustom food info.");
		Consumer<Pair<Menu,Object>> method = (info) -> {(new Menu(MenuType.CUSTOM,info.getFirst().player)).openMenu();};
		return createButton(head,"&dCustom Food",lore,heads.CUSTOM,method);
	}
	
	public static Button editFood(Consumer<Pair<Menu,Object>> method) {
		return createButton(Material.WRITABLE_BOOK,"&bEdit Food",null,null,method,1);
	}
	
	public static Button editEffects(Consumer<Pair<Menu,Object>> method) {
		ItemStack item = createButton(Material.POTION,"&bEffects",null,null,null).item();
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		meta.setBasePotionData(new PotionData(PotionType.REGEN,false,false));
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		item.setItemMeta(meta);
		return new Button(item,method,2);
	}
	
	public static Button info(List<String> lore) {
		return createButton(head,"&bInfo",lore,heads.INFO,null);
	}
	
	public static Button info(Material material, String name, List<String> lore) {
		return createButton(material,"&b" + name,lore,null,null);
	}
	
	public static Button copy() {
		List<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add("&aCopy custom food");
		lore.add("&ainfo to another.");
		Consumer<Pair<Menu,Object>> method = (info) -> {(new EditMenu(MenuType.COPY,info.getFirst().player)).openMenu();};
		return createButton(Material.COMPARATOR,"&bCopy",null,null,method);
	}
	
	public static Button defaultFood() {
		Consumer<Pair<Menu,Object>> method = (info) -> {
			EditMenu menu = (EditMenu) info.getFirst();
			MCFoodsDefault defaultFood = MCFoodsDefault.getFood(menu.currentFood.material());
			if (defaultFood == null) menu.currentFood = new CustomFood(new ItemStack(menu.currentFood.material()),null,null,null,null);
			else menu.currentFood = defaultFood.asFood();
			menu.setIncrementButtons(null,false,0,0,0,null);
		};
		List<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add("&aLoad default values.");
		return createButton(head,"&bDefault",lore,heads.DEFAULT,method);
	}
	
	public static Button unset(Consumer<Pair<Menu,Object>> method, List<String> lore, Object info) {
		return createButton(Material.BARRIER,"&cUnset",lore,null,method,info);
	}
	
	public static Button plus(int lvl, double num, Consumer<Pair<Menu,Object>> method) {
		heads plus;
		if (lvl == 1) {
			plus = heads.PLUS1;
		} else if (lvl == 2) {
			plus = heads.PLUS2;
		} else if (lvl == 3) {
			plus = heads.PLUS3;
		} else {
			plus = heads.PLUS4;
		}
		return createButton(head,"&b+" + Double.toString(Math.abs(num)),null,plus,method,Math.abs(num));
	}
	
	public static Button minus(int lvl, double num, Consumer<Pair<Menu,Object>> method) {
		heads minus;
		if (lvl == 1) {
			minus = heads.MINUS1;
		} else if (lvl == 2) {
			minus = heads.MINUS2;
		} else if (lvl == 3) {
			minus = heads.MINUS3;
		} else {
			minus = heads.MINUS4;
		}
		return createButton(head,"&b-" + Double.toString(Math.abs(num)),null,minus,method,-Math.abs(num));
	}
	
	public static Button hunger() {
		return createButton(Material.RABBIT_STEW,"&bHunger",null,null,Methods.hungerMethod);
	}
	
	public static Button saturation() {
		return createButton(Material.GOLDEN_CARROT,"&bSaturation",null,null,Methods.saturationMethod);
	}
	
	public static Button chance() {
		return createButton(head,"&bChance (%)",null,heads.CHANCE,Methods.chanceMethod);
	}
	
	public static Button ok(Consumer<Pair<Menu,Object>> method) {
		return createButton(head,"&a&lOK!",null,heads.OK,method);
	}
	
	private static ItemStack getHead(String skin) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		meta = setSkullSkin(meta,skin);
		head.setItemMeta(meta);
		return head;
	}
	
	private static ItemStack getHead(heads info) {
		return getHead(info.skin);
	}
	
	private static Button createButton(Material type, String name, List<String> lore, Object skin, Consumer<Pair<Menu,Object>> method, Object info) {
		ItemStack item = new ItemStack(type);
		if (type == Material.PLAYER_HEAD && skin != null) {
			if (skin instanceof heads) {
				item = getHead((heads) skin);
			} else {
				item = getHead((String) skin);
			}
		}
		ItemMeta meta = item.getItemMeta();
		if (name != null) {
			meta.setDisplayName(Utils.chatColors(name));
		}
		if (lore != null) {
			meta.setLore(Utils.chatColors(lore));
		}
		item.setItemMeta(meta);
		return new Button(item,method,info);
	}

	private static Button createButton(Material type, String name, List<String> lore, Object skin, Consumer<Pair<Menu,Object>> method) {
		return createButton(type,name,lore,skin,method,null);
	}
	
	private static SkullMeta setSkullSkin(SkullMeta skullMeta, String skin) {
		if (skin == null || skin == "" || skin.isEmpty()) return null;
		try {
			Method metaSetProfileMethod = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
			metaSetProfileMethod.setAccessible(true);
			UUID id = new UUID(skin.substring(skin.length() - 20).hashCode(),skin.substring(skin.length() - 10).hashCode());
			GameProfile profile = new GameProfile(id,"1");
			profile.getProperties().put("textures", new Property("textures", skin));
			metaSetProfileMethod.invoke(skullMeta, profile);
		} catch (Exception e) {
			return null;
		}
		return skullMeta;
	}
	
	private enum heads{
		NEXT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDllY2NjNWMxYzc5YWE3ODI2YTE1YTdmNWYxMmZiNDAzMjgxNTdjNTI0MjE2NGJhMmFlZjQ3ZTVkZTlhNWNmYyJ9fX0="),
		PREVIOUS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="),
		BACK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0="),
		CLOSE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZjNjBkYTQxNGJmMDM3MTU5YzhiZThkMDlhOGVjYjkxOWJmODlhMWEyMTUwMWI1YjJlYTc1OTYzOTE4YjdiIn19fQ=="),
		CUSTOM("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTIxZDhkOWFlNTI3OGUyNmJjNDM5OTkyM2QyNWNjYjkxNzNlODM3NDhlOWJhZDZkZjc2MzE0YmE5NDM2OWUifX19"),
		INFO("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzU3NDcwMTBkODRhYTU2NDgzYjc1ZjYyNDNkOTRmMzRjNTM0NjAzNTg0YjJjYzY4YTQ1YmYzNjU4NDAxMDVmZCJ9fX0="),
		PLUS1("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVlMGIyMzU5ZDEyYWYyMjUyNDhhZThjMTQ0MDQ3OTM2YjhiMjUzNDY2OGUzNGI3ODFjMTVmNTg5OSJ9fX0="),
		PLUS2("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFkMGY4MmEyYTRjZGQ4NWY3OWY0ZDlkOTc5OGY5YzNhNWJjY2JlOWM3ZjJlMjdjNWZjODM2NjUxYThmM2Y0NSJ9fX0="),
		PLUS3("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzdiNzMzZTBiYTk2NGU4MTU3NDc2ZjMzNTM0MjZhODdjZWFiM2RiYzNmYjRiZGRhYTJkNTc4ODZkZjM3YmE2In19fQ=="),
		PLUS4("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY2Yjg1ZjYyNjQ0NGRiZDViZGRmN2E1MjFmZTUyNzQ4ZmU0MzU2NGUwM2ZiZDM1YjZiNWU3OTdkZTk0MmQifX19"),
		MINUS1("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM0ZjliMWQ2ODI1YzhlY2Y4OTc5YzQ1MmRjNzYwNGNkYzMxY2JhZjk5NThiMGMzYjc0ZjFkMzI4YmUwMTUifX19"),
		MINUS2("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRmNWMyZjg5M2JkM2Y4OWNhNDA3MDNkZWQzZTQyZGQwZmJkYmE2ZjY3NjhjODc4OWFmZGZmMWZhNzhiZjYifX19"),
		MINUS3("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzQ5ZDI3MWM1ZGY4NGY4YTNjOGFhNWQxNTQyN2Y2MjgzOTM0MWRhYjUyYzYxOWE1OTg3ZDM4ZmJlMThlNDY0In19fQ=="),
		MINUS4("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTlkYmVkNTIyZThkZTFhNjgxZGRkZDM3ODU0ZWU0MjY3ZWZjNDhiNTk5MTdmOWE5YWNiNDIwZDZmZGI5In19fQ=="),
		CHANCE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTlmMjdkNTRlYzU1NTJjMmVkOGY4ZTE5MTdlOGEyMWNiOTg4MTRjYmI0YmMzNjQzYzJmNTYxZjllMWU2OWYifX19"),
		OK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2UyYTUzMGY0MjcyNmZhN2EzMWVmYWI4ZTQzZGFkZWUxODg5MzdjZjgyNGFmODhlYThlNGM5M2E0OWM1NzI5NCJ9fX0="),
		DEFAULT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTdjMjE0NGZkY2I1NWMzZmMxYmYxZGU1MWNhYmRmNTJjMzg4M2JjYjU3ODkyMzIyNmJlYjBkODVjYjJkOTgwIn19fQ==");
		
		private final String skin;
		
		private heads(String skin) {
			this.skin = skin;
		}
	}
}