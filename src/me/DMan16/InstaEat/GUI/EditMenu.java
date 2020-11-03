package me.DMan16.InstaEat.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.mojang.datafixers.util.Pair;

import me.DMan16.InstaEat.Config.MCFoodsDefault;
import me.DMan16.InstaEat.InstaEat.CustomFood;
import me.DMan16.InstaEat.InstaEat.InstaEat;
import me.DMan16.InstaEat.Utils.Utils;

public class EditMenu extends ContentMenu {
	public final PairInt input = pairInt(1,5);
	public final PairInt input2 = pairInt(4,2);
	public final PairInt output = pairInt(4,-2);
	public final PairInt incrementCenter = pairInt(5,5);
	
	private ItemStack item;
	public CustomFood currentFood;
	private ItemStack item2;
	private ItemStack itemOutput;
	public final boolean editInput;
	private int mode;
	
	public EditMenu(MenuType type, Player player, ItemStack item, int mode) {
		super(type,player,6,2,3);
		this.item = item;
		this.currentFood = null;
		this.item2 = null;
		this.itemOutput = null;
		this.mode = mode;
		this.editInput = type != MenuType.EDITVANILLA;
		this.nextLoc = pairInt(4,-1);
		this.prevLoc = pairInt(4,1);
		button(input, new Button(item,Methods.updateEditMethod));
		bigBox(input,Buttons.empty());
		if (type == MenuType.EDITVANILLA) {
			button(input.add(1,1),Buttons.editFood(Methods.setModeMethod));
			button(input.add(1,-1),Buttons.editEffects(Methods.setModeMethod));
			button(loc(-1,1),Buttons.defaultFood());
		}
		buildPages();
	}
	
	public EditMenu(MenuType type, Player player, ItemStack item) {
		this(type,player,item,initialMode(type));
	}
	
	public EditMenu(MenuType type, Player player) {
		this(type,player,null);
	}
	
	private void buildPages() {
		List<Button> content = new ArrayList<Button>();
		content.add(null);
		content.add(Buttons.unset(Methods.unsetFoodMethod,null,null));
		content.add(null);
		content.add(Buttons.chance());
		content.add(null);
		content.add(Buttons.hunger());
		content.add(null);
		content.add(Buttons.saturation());
		addPages(content);
		addPages(null);
		content = new ArrayList<Button>();
		List<PotionEffectType> potionEffects = new ArrayList<PotionEffectType>(Arrays.asList(PotionEffectType.values()));
		Collections.sort(potionEffects, new Comparator<PotionEffectType>(){
		    public int compare(PotionEffectType type1, PotionEffectType type2) {
		        return type1.getName().compareToIgnoreCase(type2.getName());
		    }
		});
		for (PotionEffectType type : potionEffects) {
			ItemStack item = new ItemStack(Material.POTION);
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			meta.setBasePotionData(new PotionData(PotionType.REGEN));
			meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			meta.setDisplayName(Utils.chatColors("&f" + Utils.splitCapitalize(type.getName(),"_")));
			item.setItemMeta(meta);
			content.add(new Button(item,Methods.effectMethod,type));
		}
		addPages(content);
	}
	
	public void setIncrementButtons(Consumer<Pair<Menu,Object>> method, boolean small, Integer current, int max, int min, Button info) {
		setIncrementButtons(method,small,current == null ? 0D : (double) current,max,min,info);
	}
	
	public void setIncrementButtons(Consumer<Pair<Menu,Object>> method, boolean small, Float current, int max, int min, Button info) {
		setIncrementButtons(method,small,current == null ? 0D : (double) current,max,min,info);
	}
	
	public void setIncrementButtons(Consumer<Pair<Menu,Object>> method, boolean small, Double current, int max, int min, Button info) {
		double val = current == null ? 0 : (double) current;
		double val1 = small ? 0.1 : 1;
		int val2 = small ? 1 : 10;
		int val3 = small ? 10 : 100;
		int val4 = small ? 100 : 1000;
		button(incrementCenter,info);
		button(incrementCenter.add(0,1),val + val1 <= max ? Buttons.plus(1,val1,method) : null);
		button(incrementCenter.add(0,-1),val - val1 >= min ? Buttons.minus(1,val1,method) : null);
		button(incrementCenter.add(0,2),val + val2 <= max ? Buttons.plus(2,val2,method) : null);
		button(incrementCenter.add(0,-2),val - val2 >= min ? Buttons.minus(2,val2,method) : null);
		button(incrementCenter.add(0,3),val + val3 <= max ? Buttons.plus(3,val3,method) : null);
		button(incrementCenter.add(0,-3),val - val3 >= min ? Buttons.minus(3,val3,method) : null);
		button(incrementCenter.add(0,4),val + val4 <= max ? Buttons.plus(4,val4,method) : null);
		button(incrementCenter.add(0,-4),val - val4 >= min ? Buttons.minus(4,val4,method) : null);
		update();
	}
	
	private static int initialMode(MenuType type) {
		if (type == MenuType.EDITFOOD) {
			return 1;
		}
		if (type == MenuType.EDITEFFECTS) {
			return 2;
		}
		return 0;
	}
	
	public void setMode(int mode) {
		if (this.mode == mode) return;
		setIncrementButtons(null,false,0,0,0,null);
		button(incrementCenter.add(-1,-1),null);
		button(incrementCenter.add(-1,0),null);
		button(incrementCenter.add(-1,1),null);
		this.mode = mode;
		update();
	}
	
	private void clearIncrements() {
		setIncrementButtons(null,false,0,0,0,null);
		for (int i = 0; i <= 4 ; i++) {
			button(incrementCenter.add(-1,i),null);
			button(incrementCenter.add(-1,-i),null);
		}
	}
	
	public void setLegalButtons(boolean flag) {
		if (type == MenuType.COPY) {
			if (item2 == null) {
				bigBox(input2,Buttons.empty());
				setOK(false);
			} else if (MCFoodsDefault.isFood(item2.getType()) && itemOutput == null) {
				bigBox(input2,Buttons.V());
				setOK(currentFood.fullySet());
			} else {
				bigBox(input2,Buttons.X());
				setOK(false);
			}
			if (itemOutput == null) {
				bigBox(output,null);
			} else {
				bigBox(output,Buttons.output());
			}
		} else {
			if (flag) {
				if (mode == 1) {
					setPage(1);
				} else if (mode == 2) {
					setPage(3);
				} else {
					setPage(0);
				}
			}
			if (currentFood != null) setOK(currentFood.fullySet() || (currentFood.notSet() && type == MenuType.EDITVANILLA));
			else setOK(false);
		}
	}
	
	void setOK(boolean ok) {
		Button button = Buttons.edge();
		if (ok) {
			Consumer<Pair<Menu,Object>> method;
			if (type == MenuType.EDITVANILLA) {
				method = (info) -> {
					InstaEat.FoodsManager.set(currentFood);
					ItemMeta meta = item.getItemMeta();
					List<String> lore = new ArrayList<String>();
					lore.add("");
					lore.addAll(InstaEat.FoodsManager.get(item.getType()).info());
					meta.setLore(Utils.chatColors(lore));
					item.setItemMeta(meta);
					inv.setItem(loc(input),item);
				};
			} else if (type == MenuType.COPY) {
				method = (info) -> {
					EditMenu menu = (EditMenu) info.getFirst();
					menu.setOutput();
				};
			} else {
				method = (info) -> {
					EditMenu menu = (EditMenu) info.getFirst();
					menu.setItem();
				};
			}
			button = Buttons.ok(method);
		}
		button(loc(-1,-1),button);
	}
	
	public void update() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(InstaEat.getMain(), new Runnable() {
			@Override
			public void run() {
				boolean update = true;
				if (type == MenuType.EDITVANILLA) {
					if (currentFood == null) currentFood = InstaEat.FoodsManager.get(item.getType());
				} else {
					ItemStack item1 = inv.getItem(loc(input));
					button(input, new Button(item1,Methods.updateEditMethod));
					List<String> infoLore = new ArrayList<String>();
					if (item1 == null) {
						item = null;
						currentFood = null;
						bigBox(input,Buttons.empty());
						update = false;
						infoLore = null;
						setPage(0);
						clearIncrements();
					} else if (!sameItem(item1,item)) {
						item = item1.clone();
						currentFood = null;
						if (MCFoodsDefault.isFood(item.getType())) {
							currentFood = new CustomFood(item);
							if (type == MenuType.COPY) {
								update = currentFood.fullySet();
							} else if (type == MenuType.EDITEFFECTS) {
								update = currentFood.fullySet() && currentFood.chance() != 0;
							}
							bigBox(input,update ? Buttons.V() : Buttons.X());
						} else {
							bigBox(input,Buttons.X());
							update = false;
							infoLore = null;
						}
						if (!update) setPage(0);
						clearIncrements();
					} else {
						setItemAmount(item1.getAmount());
						update = false;
					}
					if (type == MenuType.COPY) {
						item2 = inv.getItem(loc(input2));
						button(input2, new Button(item2,Methods.updateEditMethod));
						itemOutput = inv.getItem(loc(output));
						button(output, new Button(itemOutput,Methods.updateEditMethod));
					}
					if (infoLore != null) {
						if (currentFood != null && currentFood.fullySet()) {
							infoLore.addAll(currentFood.info());
						} else {
							infoLore.add("&cNot a Custom Food");
						}
					}
					button(1,9,Buttons.info(infoLore));
				}
				setLegalButtons(update);
				player.updateInventory();
			}
		},2);
	}
	
	public boolean sameItem(ItemStack item1, ItemStack item2) {
		if (item1 == null || item2 == null) {
			return item1 == item2;
		} else {
			ItemStack cmp1 = item1.clone();
			ItemStack cmp2 = item2.clone();
			cmp1.setAmount(1);
			cmp2.setAmount(1);
			return cmp1.equals(cmp2);
		}
	}
	
	public ItemStack getItem() {
		return item == null ? null : item.clone();
	}
	
	public void setItemAmount(int amount) {
		item.setAmount(amount);
		inv.setItem(loc(input),item);
		if (currentFood != null) currentFood.setItemAmount(amount);
	}
	
	public void setItem() {
		if (currentFood != null) inv.setItem(loc(input),currentFood.item());
		update();
	}
	
	public ItemStack getItem2() {
		return item2 == null ? null : item2.clone();
	}
	
	public void setOutput() {
		CustomFood outputFood = new CustomFood(item2);
		outputFood.hunger(currentFood.hunger());
		outputFood.saturation(currentFood.saturation());
		outputFood.chance(currentFood.chance());
		outputFood.effects(currentFood.effects());
		inv.setItem(loc(output),outputFood.item());
		inv.setItem(loc(input2),null);
		update();
	}
	
	public ItemStack getOutput() {
		return itemOutput == null ? null : itemOutput.clone();
	}
	
	public void setItem2(ItemStack item) {
		inv.setItem(loc(input2),item);
		update();
	}
	
	public class PairItem {
		public PairInt pair;
		public ItemStack item;
		
		public PairItem(PairInt pair, ItemStack item) {
			this.pair = pair;
			this.item = item;
		}
	}
}