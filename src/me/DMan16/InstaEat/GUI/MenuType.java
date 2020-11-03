package me.DMan16.InstaEat.GUI;

import me.DMan16.InstaEat.InstaEat.InstaEat;
import me.DMan16.InstaEat.Utils.Utils;

public enum MenuType {
	MAIN(null),
	VANILLA("Vanilla Food"),
	EDITVANILLA(VANILLA.name),
	CUSTOM("Custom Food"),
	EDITFOOD(CUSTOM.name),
	EDITEFFECTS(CUSTOM.name),
	COPY(CUSTOM.name);
	
	private final String name;
	
	private MenuType(String name) {
		this.name = name;
	}
	
	public String getName() {
		String newName = InstaEat.getPluginNameColors();
		if (name != null) {
			newName += "&f - " + name;
		}
		return Utils.chatColors(newName);
	}
}