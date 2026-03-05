package application;

import gui.GameApp;
import javafx.application.Application;

/**
 * Application entry point that delegates to the JavaFX {@link gui.GameApp} launcher.
 */
public class Main {
	/**
	 * Launches the JavaFX application.
	 *
	 * @param args command-line arguments forwarded to JavaFX
	 */
	public static void main(String[] args) {
		Application.launch(GameApp.class, args);
	}
}
