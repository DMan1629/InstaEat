package me.DMan16.InstaEat.GUI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.mojang.datafixers.util.Pair;

import me.DMan16.InstaEat.InstaEat.InstaEat;

public class Menu {
	private final List<PairInt> smallBox = Arrays.asList(pairInt(-1,0),pairInt(0,-1),pairInt(0,1),pairInt(1,0));
	private final List<PairInt> bigBox = Arrays.asList(pairInt(-1,-1),pairInt(-1,0),pairInt(-1,1),pairInt(0,-1),pairInt(0,1),
			pairInt(1,-1),pairInt(1,0),pairInt(1,1));
	public final static int defaultLines = 5;
	
	public Inventory inv;
	public HashMap<Integer,Button> invMap;
	public final MenuType type;
	public final Player player;
	public final MenuType back;
	public int lines;
	
	public Menu(MenuType type, Player player, int lines) {
		this.player = player;
		this.type = type;
		this.inv = null;
		this.invMap = new HashMap<Integer,Button>();
		this.lines = lines;
		MenuType back = null;
		if (type == MenuType.CUSTOM || type == MenuType.VANILLA) {
			back = MenuType.MAIN;
		} else if (type == MenuType.EDITVANILLA) {
			back = MenuType.VANILLA;
		} else if (type == MenuType.COPY || type == MenuType.EDITFOOD || type == MenuType.EDITEFFECTS) {
			back = MenuType.CUSTOM;
		} 
		this.back = back;
		newInventory(lines);
		if (type == MenuType.MAIN) {
			main();
		} else if (type == MenuType.CUSTOM) {
			custom();
		}
	}
	
	public PairInt pairInt(int a1, int a2) {
		return new PairInt(a1,a2);
	}

	public Menu(MenuType type, Player player) {
		this(type,player,defaultLines);
	}
	
	public void openMenu() {
		if (inv != null) {
			player.closeInventory();
			if (InstaEat.MenuManager.containsKey(player)) {
				InstaEat.MenuManager.replace(player,this);
			} else {
				InstaEat.MenuManager.put(player,this);
			}
			player.openInventory(inv);
		}
	}

	public void main() {
		PairInt vanilla = pairInt(3,3);
		PairInt custom = pairInt(3,-3);
		button(vanilla,Buttons.vanilla());
		button(custom,Buttons.custom());
		bigBox(vanilla,Buttons.empty());
		bigBox(custom,Buttons.empty());
	}

	public void custom() {
		PairInt copy = pairInt(3,3);
		PairInt editFood = pairInt(3,5);
		PairInt editEffects = pairInt(3,-3);
		button(copy,Buttons.copy());
		Consumer<Pair<Menu,Object>> method1 = (info) -> {(new EditMenu(MenuType.EDITFOOD,info.getFirst().player)).openMenu();};
		Consumer<Pair<Menu,Object>> method2 = (info) -> {(new EditMenu(MenuType.EDITEFFECTS,info.getFirst().player)).openMenu();};
		button(editFood,Buttons.editFood(method1));
		button(editEffects,Buttons.editEffects(method2));
		smallBox(copy,Buttons.empty());
		smallBox(editFood,Buttons.empty());
		smallBox(editEffects,Buttons.empty());
	}
	
	public void button(int loc, Button button) {
		if (loc < 0 || loc >= inv.getSize()) return;
		inv.setItem(loc,null);
		invMap.remove(loc);
		inv.setItem(loc,button == null ? null : button.item());
		invMap.put(loc,button == null ? null : (button.item() == null ? null : button));
	}
	
	public void button(int line, int loc, Button button) {
		loc = (loc - (loc > 1 ? 1 : -1)) % 9 + (loc > 1 ? 1 : -1);
		line = (line - (line > 1 ? 1 : -1)) % lines + (line > 1 ? 1 : -1);
		if (line == 0 || loc == 0) return;
		if (line < 0) {
			line += lines + 1;
		}
		if (loc < 0) {
			loc += 9 + 1;
		}
		button(loc(line,loc),button);
	}
	
	public void button(PairInt loc, Button button) {
		button(loc.first,loc.second,button);
	}
	
	public int loc(int line, int loc) {
		if (line == 0 || loc == 0) return -1;
		if (line < 0) {
			line += lines + 1;
		}
		if (loc < 0) {
			loc += 9 + 1;
		}
		return (line - 1) * 9 + loc - 1;
	}
	
	public int loc(PairInt loc) {
		int a = loc.first;
		int b = loc.second;
		return loc(a,b);
	}
	
	public PairInt loc(int loc) {
		return pairInt((loc / 9) + 1,(loc % 9) + 1);
	}
	
	public void newInventory(int lines) {
		inv = Bukkit.createInventory(player,lines * 9,type.getName());
		for (int i = 1; i <= 9; i++) {
			button(1,i,Buttons.edge());
			button(-1,i,Buttons.edge());
		}
		button(-1,5,Buttons.close());
		if (back != null) {
			button(1,1,Buttons.back());
		}
	}
	
	public void back() {
		if (back == null) return;
		if (back == MenuType.VANILLA) {
			(new ContentMenu(back,player)).openMenu();
		} else {
			(new Menu(back,player)).openMenu();
		}
	}
	
	public void smallBox(PairInt loc, Button item) {
		box(loc,smallBox,item);
	}
	
	public void bigBox(PairInt loc, Button item) {
		box(loc,bigBox,item);
	}
	
	public void box(PairInt loc, List<PairInt> box, Button item) {
		for (PairInt add : box) {
			button(loc.add(add),item);
		}
	}
	
	public class PairInt {
		public final int first;
		public final int second;
		
		public PairInt(int first, int second) {
			this.first = first;
			this.second = second;
		}
		
		public boolean equals(PairInt pair) {
			return (first == pair.first) && (second == pair.second);
		}
		
		public PairInt add(PairInt add) {
			return new PairInt(this.first + add.first,this.second + add.second);
		}
		
		public PairInt add(int first, int second) {
			return add(new PairInt(first,second));
		}
	}
}