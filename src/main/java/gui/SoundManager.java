package gui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {
    private static MediaPlayer bgPlayer;
    private static MediaPlayer typePlayer;
    private static MediaPlayer openingPlayer;
    private static MediaPlayer endingPlayer;
    
    // Master volume control (0.0 to 1.0)
    private static double masterVolume = 0.6;
    private static double volumeBeforeMute = 0.6; // Store volume before mute
    private static boolean isMuted = false;

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
            bgPlayer.setVolume(masterVolume);
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
            openingPlayer.setVolume(masterVolume);
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
            endingPlayer.setVolume(masterVolume);
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
            typePlayer.setVolume(masterVolume);
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

    // Volume Control Methods
    
    /**
     * Increase volume by 0.1 (10%)
     */
    public static void increaseVolume() {
        setVolume(masterVolume + 0.1);
    }

    /**
     * Decrease volume by 0.1 (10%)
     */
    public static void decreaseVolume() {
        setVolume(masterVolume - 0.1);
    }

    /**
     * Set master volume (0.0 to 1.0)
     * @param volume Target volume level
     */
    public static void setVolume(double volume) {
        try {
            // Clamp volume between 0.0 and 1.0
            masterVolume = Math.max(0.0, Math.min(1.0, volume));
            volumeBeforeMute = masterVolume; // Update reference volume
            isMuted = false; // Volume change unmutes

            // Apply volume to all active players
            if (bgPlayer != null) {
                bgPlayer.setVolume(masterVolume);
            }
            if (typePlayer != null) {
                typePlayer.setVolume(masterVolume);
            }
            if (openingPlayer != null) {
                openingPlayer.setVolume(masterVolume);
            }
            if (endingPlayer != null) {
                endingPlayer.setVolume(masterVolume);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Get current master volume level
     * @return Volume between 0.0 and 1.0
     */
    public static double getVolume() {
        return masterVolume;
    }

    /**
     * Toggle mute/unmute
     */
    public static void toggleMute() {
        if (isMuted) {
            unmute();
        } else {
            mute();
        }
    }

    /**
     * Mute all sounds (volume to 0.0) and remember previous volume
     */
    public static void mute() {
        try {
            if (!isMuted) {
                volumeBeforeMute = masterVolume;
                isMuted = true;
                masterVolume = 0.0;

                // Apply mute to all active players
                if (bgPlayer != null) {
                    bgPlayer.setVolume(0.0);
                }
                if (typePlayer != null) {
                    typePlayer.setVolume(0.0);
                }
                if (openingPlayer != null) {
                    openingPlayer.setVolume(0.0);
                }
                if (endingPlayer != null) {
                    endingPlayer.setVolume(0.0);
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Unmute to previous volume level
     */
    public static void unmute() {
        try {
            if (isMuted) {
                isMuted = false;
                masterVolume = volumeBeforeMute;

                // Apply volume to all active players
                if (bgPlayer != null) {
                    bgPlayer.setVolume(masterVolume);
                }
                if (typePlayer != null) {
                    typePlayer.setVolume(masterVolume);
                }
                if (openingPlayer != null) {
                    openingPlayer.setVolume(masterVolume);
                }
                if (endingPlayer != null) {
                    endingPlayer.setVolume(masterVolume);
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Check if sound is muted
     * @return True if muted, false otherwise
     */
    public static boolean isMutedStatus() {
        return isMuted;
    }
}
