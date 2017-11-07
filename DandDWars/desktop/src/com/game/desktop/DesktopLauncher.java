package com.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.game.DandDWars;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "D&D Wars";
		config.width = 640;
		config.height = 640;
		config.resizable = false;
		
		new LwjglApplication(new DandDWars(), config);
		
	}
}
