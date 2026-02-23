package gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;

public class ImageLoader {
    private static final Map<String, Image> imageCache = new HashMap<>();

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

    private static Image createPlaceholder(int width, int height) {
        return new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==", 
                         width, height, true, true);
    }

    public static void clearCache() {
        imageCache.clear();
    }
}
