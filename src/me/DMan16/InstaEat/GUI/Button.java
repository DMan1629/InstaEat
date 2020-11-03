package me.DMan16.InstaEat.GUI;

import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.mojang.datafixers.util.Pair;

public class Button {
	private ItemStack item;
	private Consumer<Pair<Menu,Object>> method;
	public final Object info;
	
	public Button(ItemStack item, Consumer<Pair<Menu,Object>> method, Object info) {
		this.item = item == null ? null : item.clone();
		this.method = method;
		this.info = info;
	}
	
	public Button(ItemStack item, Consumer<Pair<Menu,Object>> method) {
		this(item,method,null);
	}
	
	public void run(Menu menu) {
		if (method == null) return;
		method.accept(new Pair<Menu,Object>(menu,info));
	}
	
	public ItemStack item() {
		return (item == null ? null : item.clone());
	}
}