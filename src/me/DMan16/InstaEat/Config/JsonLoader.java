package me.DMan16.InstaEat.Config;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import me.DMan16.InstaEat.InstaEat.InstaEat;

public class JsonLoader {
	private final String pluginDir = "plugins/" + InstaEat.getPluginName();
	private Path dir;
	private Path path;

	public JsonLoader() throws IOException {
		dir = Paths.get(pluginDir);
	}
	
	public void write(String fileName, JSONObject defaultFormat) {
		path = dir.resolve(fileName);
		try {
			if (!Files.exists(path, new LinkOption[0])) {
				Files.createDirectories(path.getParent(), new FileAttribute[0]);
				Files.createFile(path, new FileAttribute[0]);
			}
			Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(defaultFormat.toJSONString());
			String prettyJsonString = gson.toJson(je);
			PrintWriter pw = new PrintWriter(pluginDir + "/" + fileName);
			pw.write(prettyJsonString);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public <V> JSONObject read(String fileName, JSONObject defaultFormat) {
		path = dir.resolve(fileName);
		if (!Files.exists(path, new LinkOption[0])) {
			write(fileName,defaultFormat);
		}
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader(pluginDir + "/" + fileName)) {
			Object obj = null;
			try {
				obj = jsonParser.parse(reader);
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
			}
			JSONObject arr = (JSONObject) obj;
            return arr;
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return null;
	}
}