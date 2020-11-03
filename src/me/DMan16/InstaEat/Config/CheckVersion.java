package me.DMan16.InstaEat.Config;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

public class CheckVersion {
	private String current;

	public CheckVersion() {
		boolean noVersion = true;
		for (Version version : Version.values()) {
			if (Bukkit.getVersion().contains(version.getId())) {
				current = version.getId();
				noVersion = false;
			}
		}
		if (noVersion) {
			current = "0";
		}
	}

	public Boolean isDead() {
		return (current.equalsIgnoreCase("0"));
	}
	
	public String supportedVersions() {
		return StringUtils.join(Version.values(),", ");
	}

	String getLatest() {
		return Version.values()[-1].getId();
	}

	private enum Version {
		v1_16("1.16");
		private final String id;
		
		private Version(String id) {
			this.id = id;
		}
		
		String getId() {
			return id;
		}
	}
}