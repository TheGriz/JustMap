package ru.bulldog.justmap.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import ru.bulldog.justmap.JustMap;

import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class ImageUtil {
	private ImageUtil() {}	

	private static ResourceManager resourceManager;
	
	private static void checkResourceManager() {
		if (resourceManager == null) resourceManager = MinecraftClient.getInstance().getResourceManager();
	}
	
	public static boolean imageExists(Identifier image) {
		if (image == null) return false;
		
		try {
			return resourceManager.containsResource(image);
		} catch(Exception ex) {
			JustMap.LOGGER.catching(ex);
			return false;
		}
	}
	
	public static NativeImage loadImage(Identifier image, int w, int h) {
		checkResourceManager();
		
		if (imageExists(image)) {
			try (Resource resource = resourceManager.getResource(image)) {
				return NativeImage.read(resource.getInputStream());			
			} catch (IOException e) {
				JustMap.LOGGER.logWarning(String.format("Can't load texture image: %s. Will be created empty image.", image));
				JustMap.LOGGER.logWarning(String.format("Cause: %s.", e.getMessage()));
			}
		}
		
		return new NativeImage(w, h, false);
	}
	
	public static NativeImage applyColor(NativeImage image, int color) {
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				if (image.getPixelOpacity(i, j) == -1) {
					int newColor = ColorHelper.multiplyColor(image.getPixelColor(i, j), color);
					image.setPixelColor(i, j, ColorUtil.toABGR(newColor));
				}
			}
		}
		
		return image;
	}
	
	public static void fillImage(NativeImage image, int color) {
		image.fillRect(0, 0, image.getWidth(), image.getHeight(), color);
	}
	
	public static NativeImage readTile(NativeImage source, int x, int y, int w, int h, boolean closeSource) {
		NativeImage tile = new NativeImage(w, h, false);
		
		for(int i = 0; i < w; i++) {
			for(int j = 0; j < h; j++) {
				tile.setPixelColor(i, j, source.getPixelColor(x + i, y + j));
			}
		}
		
		if (closeSource) source.close();
		
		return tile;
	}
	
	public static NativeImage writeTile(NativeImage image, NativeImage tile, int x, int y) {
		int tileWidth = tile.getWidth();
		int tileHeight = tile.getHeight();
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		if (tileWidth + x <= 0 || tileHeight + y <= 0) return image;
		
		if (x + tileWidth > imageWidth) {
			tileWidth = imageWidth - x;
		}		
		if (y + tileHeight > imageHeight) {
			tileHeight = imageHeight - y;
		}
	
		for (int i = 0; i < tileWidth; i++) {
			int xp = x + i;
			if (xp < 0) continue;
	
			for (int j = 0; j < tileHeight; j++) {
				int yp = y + j;
				if (yp < 0) continue;
				
				try {
					image.setPixelColor(xp, yp, tile.getPixelColor(i, j));
				} catch(Exception ex) {
					return null;
				}
			}
		}
		
		return image;
	}
	
	public static NativeImage fromBufferedImage(BufferedImage image) {
		return null;
	}
}