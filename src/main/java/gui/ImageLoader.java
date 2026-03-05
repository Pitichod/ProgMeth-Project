package gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;

/**
 * Utility class for loading and caching images from classpath resources.
 * Images are cached by path (and optionally by requested dimensions) to avoid
 * redundant I/O on repeated loads.
 */
public class ImageLoader {
    /** Cache of previously loaded images keyed by path (and dimensions). */
    private static final Map<String, Image> imageCache = new HashMap<>();

    /**
     * Loads an image from the classpath at the given path, scaled to the
     * requested width and height. Returns a cached copy if available.\n     * If the resource is not found, a 1x1 transparent placeholder is returned.
     *
     * @param path   the classpath resource path (e.g. "/images/Player.png")
     * @param width  the desired width in pixels
     * @param height the desired height in pixels
     * @return the loaded {@link Image}, or a placeholder if not found
     */
    public static Image loadImage(String path, int width, int height) {
        String cacheKey = path + "_" + width + "x" + height;
        
        if (imageCache.containsKey(cacheKey)) {
            return imageCache.get(cacheKey);
        }

        try (InputStream input = ImageLoader.class.getResourceAsStream(path)) {
            if (input == null) {
                return createPlaceholder(width, height);
            }
            Image image = new Image(input, width, height, true, true);
            imageCache.put(cacheKey, image);
            return image;
        } catch (IOException e) {
            System.err.println("Failed to load image: " + path);
            return createPlaceholder(width, height);
        }
    }

    /**
     * Loads an image at its original size from the classpath.
     * Returns a cached copy if available, or {@code null} if the resource is not found.
     *
     * @param path the classpath resource path
     * @return the loaded {@link Image}, or {@code null} if not found
     */
    public static Image loadImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        try (InputStream input = ImageLoader.class.getResourceAsStream(path)) {
            if (input == null) {
                return null;
            }
            Image image = new Image(input);
            imageCache.put(path, image);
            return image;
        } catch (IOException e) {
            System.err.println("Failed to load image: " + path);
            return null;
        }
    }

    /**
     * Creates a 1x1 transparent PNG placeholder image scaled to the given dimensions.
     *
     * @param width  the desired width
     * @param height the desired height
     * @return a placeholder {@link Image}
     */
    private static Image createPlaceholder(int width, int height) {
        return new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==", 
                         width, height, true, true);
    }

    /**
     * Clears the entire image cache, freeing memory.
     */
    public static void clearCache() {
        imageCache.clear();
    }
}
