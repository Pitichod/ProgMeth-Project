package gui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {
    private static MediaPlayer bgPlayer;
    private static MediaPlayer typePlayer;

    private static Media loadMedia(String path) {
        try {
            URL res = SoundManager.class.getResource(path);
            if (res == null) return null;
            return new Media(res.toExternalForm());
        } catch (Exception e) {
            return null;
        }
    }

    public static void playBackgroundLoop() {
        try {
            if (bgPlayer != null) {
                bgPlayer.stop();
                bgPlayer.dispose();
                bgPlayer = null;
            }
            Media m = loadMedia("/sound/bg.mp3");
            if (m == null) return;
            bgPlayer = new MediaPlayer(m);
            bgPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgPlayer.setVolume(0.6);
            bgPlayer.play();
        } catch (Exception ignored) {
        }
    }

    public static void stopBackground() {
        try {
            if (bgPlayer != null) {
                bgPlayer.stop();
                bgPlayer.dispose();
                bgPlayer = null;
            }
        } catch (Exception ignored) {
        }
    }

    private static void playOneShot(String path, double volume) {
        try {
            Media m = loadMedia(path);
            if (m == null) return;
            MediaPlayer p = new MediaPlayer(m);
            p.setVolume(volume);
            p.setOnEndOfMedia(() -> {
                p.stop();
                p.dispose();
            });
            p.play();
        } catch (Exception ignored) {
        }
    }

    public static void playClick() {
        playOneShot("/sound/click.mp3", 1.0);
    }

    public static void playWin() {
        playOneShot("/sound/win.mp3", 1.0);
    }

    public static void playLose() {
        playOneShot("/sound/lose.mp3", 1.0);
    }

    public static void playWalk() {
        playOneShot("/sound/walk.mp3", 0.9);
    }

    public static void playPickUp() {
        playOneShot("/sound/pick_up_item.mp3", 1.0);
    }

    public static void playHurt() {
        playOneShot("/sound/hurt.mp3", 1.0);
    }

    public static void playType() {
        try {
            // stop previous if any
            if (typePlayer != null) {
                typePlayer.stop();
                typePlayer.dispose();
                typePlayer = null;
            }
            Media m = loadMedia("/sound/type.mp3");
            if (m == null) return;
            typePlayer = new MediaPlayer(m);
            typePlayer.setVolume(0.9);
            typePlayer.setOnEndOfMedia(() -> {
                if (typePlayer != null) {
                    typePlayer.stop();
                    typePlayer.dispose();
                    typePlayer = null;
                }
            });
            typePlayer.play();
        } catch (Exception ignored) {
        }
    }

    public static void stopType() {
        try {
            if (typePlayer != null) {
                typePlayer.stop();
                typePlayer.dispose();
                typePlayer = null;
            }
        } catch (Exception ignored) {
        }
    }
}
