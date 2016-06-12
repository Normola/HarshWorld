package org.undergroundbunker.harshworld.library.client.texture;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;
import org.undergroundbunker.harshworld.library.HWAPIException;
import org.undergroundbunker.harshworld.library.Util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class AnimatedColouredTexture extends TextureColouredTexture {

    private static final Logger log = Util.getLogger("AnimatedColouredTexture");

    private TextureAtlasSprite actualTexture;

    public AnimatedColouredTexture(TextureAtlasSprite addTexture, TextureAtlasSprite baseTexture, String spriteName) {
        super(addTexture, baseTexture, spriteName);
    }

    @Override
    public boolean load(IResourceManager manager, ResourceLocation location) {

        if(addTexture.getFrameCount() > 0) {
            actualTexture = addTexture;
        }
        else {
            actualTexture = backupLoadTexture(new ResourceLocation(addTextureLocation), manager);
        }

        return super.load(manager, location);
    }

    @Override
    protected void processData(int[][] data) {

        ResourceLocation resourceLocation = this.getResourceLocation(new ResourceLocation(addTextureLocation));
        IResource iResource = null;

        try {
            iResource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);
        }
        catch(IOException e) {
            log.error("Unable to load " + resourceLocation, e);
            return;
        }
        // todo: clean all this up to use the metadata of the actualTexture and therefore only run once without separation
        AnimationMetadataSection meta = iResource.getMetadata("animation");

        if(meta == null) {
            throw new HWAPIException(String.format(
                    "Trying to create animated texture from %s but no animation data is present", addTextureLocation));
        }

        if(meta.getFrameCount() > 0) {
            for(Integer i1 : meta.getFrameIndexSet()) {
                if(this.framesTextureData.size() <= i1) {
                    for(int j = this.framesTextureData.size(); j <= i1; ++j) {
                        this.framesTextureData.add(null);
                    }
                }

                int[][] data2 = new int[data.length][];
                for(int j = 0; j < data.length; j++) {
                    if(data[j] != null) {
                        data2[j] = data[j].clone();
                    }
                }

                textureData = actualTexture.getFrameTextureData(i1);

                super.processData(data2);
                this.framesTextureData.set(i1, data2);
            }
        }
        else {
            List<AnimationFrame> frameList = Lists.newArrayList();

            int count = actualTexture.getFrameCount();

            for(int i = 0; i < count; i++) {
                int[][] data2 = new int[data.length][];
                for(int j = 0; j < data.length; j++) {
                    if(data[j] != null) {
                        data2[j] = data[j].clone();
                    }
                }

                textureData = actualTexture.getFrameTextureData(i);
                super.processData(data2);

                this.framesTextureData.add(i, data2);
                frameList.add(new AnimationFrame(i, -1));
            }

            meta = new AnimationMetadataSection(frameList, this.width, this.height, meta.getFrameTime(), meta.isInterpolate());
        }

        // todo: access transform this
        try {
            Field f = TextureAtlasSprite.class.getDeclaredField("animationMetadata");
            f.setAccessible(true);
            f.set(this, meta);
        }
        catch(ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

}


