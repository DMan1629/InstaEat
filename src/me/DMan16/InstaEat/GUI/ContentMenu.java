package me.DMan16.InstaEat.GUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.datafixers.util.Pair;

import me.DMan16.InstaEat.Config.MCFoodsDefault;
import me.DMan16.InstaEat.InstaEat.InstaEat;
import me.DMan16.InstaEat.Utils.Utils;

public class ContentMenu extends Menu {
	public HashMap<Integer,List<Button>> pages;
	public int page;
	public int edgeTop;
	public int edgeBot;
	public PairInt nextLoc = pairInt(-1,-1);
	public PairInt prevLoc = pairInt(-1,1);
	
	public ContentMenu(MenuType type, Player player, int lines, int edgeTop, int edgeBot) {
		super(type,player,lines);
		this.pages = null;
		this.page = 1;
		this.edgeTop = edgeTop;
		this.edgeBot = edgeBot;
		if (type == MenuType.VANILLA) {
			vanilla();
		}
	}
	
	public ContentMenu(MenuType type, Player player) {
		this(type,player,defaultLines,1,1);
	}

	private void vanilla() {
		List<Button> content = new ArrayList<Button>();
		boolean firstPlaceable = true;
		for (Material food : MCFoodsDefault.getAllFoods()) {
			if (MCFoodsDefault.isPlaceableFood(food) && firstPlaceable) {
				firstPlaceable = false;
				for (int i = (content.size() % 9); i < 9; i++) {
					content.add(null);
				}
			}
			ItemStack item = new ItemStack(food);
			ItemMeta meta = item.getItemMeta();
			List<String> lore = new ArrayList<String>();
			lore.add("");
			lore.addAll(InstaEat.FoodsManager.get(food).info());
			meta.setLore(Utils.chatColors(lore));
			item.setItemMeta(meta);
			Consumer<Pair<Menu,Object>> method = (info) -> {(new EditMenu(MenuType.EDITVANILLA,info.getFirst().player,item)).openMenu();};
			content.add(new Button(item,method));
		}
		addPages(content);
		setPage(1);
	}
	
	private int nextAddPage() {
		return (pages == null || pages.isEmpty()) ? 0 : Collections.max(pages.keySet()) + 1;
	}
	
	public void addPages(List<Button> buttons) {
		if (pages == null) {
			pages = new HashMap<Integer,List<Button>>();
			addPages(null);
		}
		List<Button> content = new ArrayList<Button>();
		if (buttons == null || buttons.isEmpty()) {
			for (int i = 0; i < 9 * (lines - edgeTop - edgeBot); i ++) {
				content.add(null);
			}
			pages.put(nextAddPage(),content);
		} else {
			for (Button button : buttons) {
				if (content.size() == 9 * (lines - edgeTop - edgeBot)) {
					pages.put(nextAddPage(),content);
					content = new ArrayList<Button>();
				}
				content.add(button);
			}
			if (!content.isEmpty()) {
				for (int i = content.size(); i < inv.getSize() - (9 * (edgeTop + edgeBot)); i++) {
					content.add(null);
				}
				pages.put(nextAddPage(),content);
			}
		}
	}
	
	public void setPage(int page) {
		if (page >= 0 && page < pages.size()) {
			this.page = page;
			updateContent();
		}
	}
	
	public boolean isPageEmpty(int page) {
		List<Button> current = pages.get(page);
		for (Button button : current) {
			if (button != null) return false;
		}
		return true;
	}
	
	public boolean isNext() {
		return (page < pages.size() - 1 && !isPageEmpty(page) && !isPageEmpty(page + 1));
	}
	
	public boolean isPrev() {
		return (page > 0 && !isPageEmpty(page) && !isPageEmpty(page - 1));
	}
	
	public void next() {
		if (isNext()) setPage(page + 1);
	}
	
	public void previous() {
		if (isPrev()) setPage(page - 1);
	}
	
	public void updateContent() {
		if (pages != null) {
			for (int i = 9 * edgeTop; i < inv.getSize() - (9 * edgeBot); i++) {
				button(i,null);
			}
			List<Button> current = pages.get(page) == null ? new ArrayList<Button>() : pages.get(page);
			for (int i = 9 * edgeTop; i < inv.getSize() - (9 * edgeBot); i++) {
				button(i,current.get(i - (9 * edgeTop)));
			}
			button(nextLoc,isNext() ? Buttons.next() : (nextLoc.equals(pairInt(-1,nextLoc.second)) ? Buttons.edge() : null));
			button(prevLoc,isPrev() ? Buttons.previous() : (prevLoc.equals(pairInt(-1,prevLoc.second)) ? Buttons.edge() : null));
		}
	}
}