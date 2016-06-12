package org.undergroundbunker.harshworld.library.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;import org.undergroundbunker.harshworld.library.Util;
import org.undergroundbunker.harshworld.library.client.RenderUtil;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public abstract class AbstractColouredTexture extends TextureAtlasSprite {

    private Logger log = Util.getLogger("AbstractColouredTexture");

    protected static Map<String, TextureAtlasSprite> cache = Maps.newHashMap();

    protected TextureAtlasSprite baseTexture;
    private String backupTextureLocation;
    private String extra;

    protected AbstractColouredTexture(TextureAtlasSprite baseTexture, String spriteName) {
        super(spriteName);

        this.baseTexture = baseTexture;
        this.backupTextureLocation = baseTexture.getIconName();
    }

    protected AbstractColouredTexture(String baseTextureLocation, String spriteName) {
        super(spriteName);

        this.baseTexture = null;
        this.backupTextureLocation = baseTextureLocation;
    }

    public TextureAtlasSprite setSuffix(String suffix) {
        this.extra = suffix;
        this.baseTexture = null;

        return this;
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    @Override
    public boolean load(IResourceManager manager, ResourceLocation location) {

        this.framesTextureData = Lists.newArrayList();
        this.frameCounter = 0;
        this.tickCounter = 0;

        if (baseTexture == null || baseTexture.getFrameCount() <= 0) {
            baseTexture = null;
            if (extra != null && !extra.isEmpty()) {
                baseTexture = backupLoadTexture(new ResourceLocation(backupTextureLocation + "_" + extra), manager);
            }
            if (baseTexture == null) {
                baseTexture = backupLoadTexture(new ResourceLocation(backupTextureLocation), manager);
            }
        }

        int[][] data;

        if (baseTexture != null && baseTexture.getFrameCount() > 0) {
            this.copyFrom(baseTexture);

            int[][] original = baseTexture.getFrameTextureData(0);
            data = new int[original.length][];

            for (int i = 0; i < original.length; i++) {
                if (original[i] != null) {
                    data[i] = Arrays.copyOf(original[i], original[i].length);
                }
            }
        } else {
            this.width = 1; // needed so we don't crash
            this.height = 1;
            // failure
            return false;
        }
        processData(data);

        if (this.framesTextureData.isEmpty()) {
            this.framesTextureData.add(data);
        }

        return false;
    }

    protected void processData(int[][] data) {

        for (int mipmap = 0; mipmap < data.length; mipmap++) {
            if (data[mipmap] == null) {
                continue;
            }
            for (int pxCoord = 0; pxCoord < data[mipmap].length; pxCoord++) {
                data[mipmap][pxCoord] = colourPixel(data[mipmap][pxCoord], mipmap, pxCoord);
            }
        }
    }

    protected abstract int colourPixel(int pixel, int mipmap, int pxCoord);


    protected TextureAtlasSprite backupLoadTexture(ResourceLocation resourceLocation, IResourceManager resourceManager) {

        if(resourceLocation.equals(TextureMap.LOCATION_MISSING_TEXTURE)) {
            return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }

        String id = resourceLocation.toString();
        TextureAtlasSprite sprite = cache.get(id);

        if(sprite != null) {
            // got it cached
            return sprite;
        }

        sprite = TextureAtlasSprite.makeAtlasSprite(resourceLocation);
        IResource iresource = null;
        resourceLocation = this.getResourceLocation(resourceLocation);

        try {
            // load the general info
            PngSizeInfo pngsizeinfo = PngSizeInfo.makeFromResource(resourceManager.getResource(resourceLocation));
            iresource = resourceManager.getResource(resourceLocation);
            boolean flag = iresource.getMetadata("animation") != null;
            sprite.loadSprite(pngsizeinfo, flag);

            // load the actual texture data
            sprite.loadSpriteFrames(iresource, Minecraft.getMinecraft().gameSettings.mipmapLevels + 1);

            cache.put(id, sprite);
        } catch(IOException e) {
            log.error("Unable to generate " + this.getIconName() + ": unable to load " + resourceLocation + "!\nBase texture: " + baseTexture.getIconName(), e);
            net.minecraftforge.fml.client.FMLClientHandler.instance().trackMissingTexture(resourceLocation);
            sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        } finally
        {
            IOUtils.closeQuietly(iresource);
        }

        return sprite;
    }

    protected TextureAtlasSprite backupLoadtextureAtlasSprite(ResourceLocation resourceLocation, IResourceManager resourceManager) {

        ResourceLocation resourceLocation1 = null;//this.completeResourceLocation(resourceLocation, 0);
        TextureAtlasSprite textureAtlasSprite = TextureAtlasSprite.makeAtlasSprite(resourceLocation);

        try {
            IResource iResource = resourceManager.getResource(resourceLocation1);
            BufferedImage[] aBufferedImage = new BufferedImage[1 + 4]; // iirc TextureMap.mipmapLevels is always 4? :I
            aBufferedImage[0] = TextureUtil.readBufferedImage(iResource.getInputStream());
            TextureMetadataSection textureMetaDataSection = iResource.getMetadata("texture");

            PngSizeInfo pngSizeInfo = PngSizeInfo.makeFromResource(iResource);
            AnimationMetadataSection animationMetaDataSection = iResource.getMetadata("animation");
            textureAtlasSprite.loadSprite(pngSizeInfo, animationMetaDataSection != null);

            return textureAtlasSprite;
        }
        catch(RuntimeException runtimeException) {
            log.error("Unable to parse metadata from " + resourceLocation1, runtimeException);
        }
        catch(IOException ioException1) {
            log.error("Unable to load " + resourceLocation1, ioException1);
        }

        return null;
    }

    protected ResourceLocation getResourceLocation(ResourceLocation resourceLocation) {
        return new ResourceLocation(resourceLocation.getResourceDomain(), String.format("%s/%s%s", "textures", resourceLocation.getResourcePath(), ".png"));
    }

    public static int getPerceptualBrightness(int col) {

        double r = RenderUtil.red(col) / 255.0;
        double g = RenderUtil.green(col) / 255.0;
        double b = RenderUtil.blue(col) / 255.0;

        return getPerceptualBrightness(r, g, b);
    }

    public static int getPerceptualBrightness(double r, double g, double b) {

        double brightness = Math.sqrt(0.241 * r * r + 0.691 * g * g + 0.068 * b * b);

        return (int) (brightness * 255);
    }

    protected static int mult(int c1, int c2) {

        return (int) ((float) c1 * (c2 / 255f));
    }

    protected int getX(int pxCoord) {
        return pxCoord % width;
    }

    protected int getY(int pxCoord) {
        return pxCoord / width;
    }

    protected int coord(int x, int y) {
        return y * width + x;
    }


    public static class CacheClearer implements IResourceManagerReloadListener {
        public static CacheClearer INSTANCE = new CacheClearer();

        private CacheClearer() {}

        @Override
        public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
            AbstractColouredTexture.cache.clear();
        }
    }

}