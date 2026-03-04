package gui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {
    private static MediaPlayer bgPlayer;
    private static MediaPlayer typePlayer;
    private static MediaPlayer openingPlayer;
    private static MediaPlayer endingPlayer;

    private static Media loadMedia(String path) {
        try {
            URL res = SoundManager.class.getResource(path);
            if (res == null) {
                System.err.println("Media resource not found: " + path);
                return null;
            }
            return new Media(res.toExternalForm());
        } catch (Exception e) {
            System.err.println("Failed to load media: " + path + " -> " + e);
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

    public static void playOpening(int page) {
        if (page <= 0) return;
        try {
            // stop previous opening sound if any
            if (openingPlayer != null) {
                openingPlayer.stop();
                openingPlayer.dispose();
                openingPlayer = null;
            }
            String path = "/sound/Openning_" + page + ".mp3";
            Media m = loadMedia(path);
            if (m == null) return;
            openingPlayer = new MediaPlayer(m);
            openingPlayer.setVolume(1.0);
            openingPlayer.setOnEndOfMedia(() -> {
                try {
                    if (openingPlayer != null) {
                        openingPlayer.stop();
                        openingPlayer.dispose();
                        openingPlayer = null;
                    }
                } catch (Exception ignored) {}
            });
            openingPlayer.play();
        } catch (Exception ignored) {}
    }

    public static void stopOpening() {
        try {
            if (openingPlayer != null) {
                openingPlayer.stop();
                openingPlayer.dispose();
                openingPlayer = null;
            }
        } catch (Exception ignored) {}
    }

    public static void playEnding(int page) {
        if (page <= 0) return;
        try {
            if (endingPlayer != null) {
                endingPlayer.stop();
                endingPlayer.dispose();
                endingPlayer = null;
            }
            String path = "/sound/Ending_" + page + ".mp3";
            Media m = loadMedia(path);
            if (m == null) return;
            endingPlayer = new MediaPlayer(m);
            endingPlayer.setVolume(1.0);
            endingPlayer.setOnEndOfMedia(() -> {
                try {
                    if (endingPlayer != null) {
                        endingPlayer.stop();
                        endingPlayer.dispose();
                        endingPlayer = null;
                    }
                } catch (Exception ignored) {}
            });
            endingPlayer.play();
        } catch (Exception ignored) {}
    }

    public static void stopEnding() {
        try {
            if (endingPlayer != null) {
                endingPlayer.stop();
                endingPlayer.dispose();
                endingPlayer = null;
            }
        } catch (Exception ignored) {}
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
