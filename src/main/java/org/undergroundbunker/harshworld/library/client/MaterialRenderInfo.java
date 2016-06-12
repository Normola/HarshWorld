package org.undergroundbunker.harshworld.library.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.undergroundbunker.harshworld.library.client.texture.*;

@SideOnly(Side.CLIENT)
public interface MaterialRenderInfo {

    TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location);

    boolean isStitched();

    boolean useVertexColouring();

    int getVertexColour();

    String getTextureSuffix();

    MaterialRenderInfo setTextureSuffix(String suffix);

    abstract class AbstractMaterialRenderInfo implements MaterialRenderInfo {

        private String suffix;

        @Override
        public boolean isStitched() {
            return true;
        }

        @Override
        public boolean useVertexColouring() {
            return false;
        }

        @Override
        public int getVertexColour() {
            return 0xffffffff;
        }

        @Override
        public String getTextureSuffix() {
            return suffix;
        }

        @Override
        public MaterialRenderInfo setTextureSuffix(String suffix) {
            this.suffix = suffix;

            return this;
        }
    }

    class Default extends AbstractMaterialRenderInfo {

        public final int colour;

        public Default(int colour) {
            this.colour = colour;
        }

        @Override
        public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
            return baseTexture;
        }

        @Override
        public boolean isStitched() {
            return false;
        }

        @Override
        public boolean useVertexColouring() {
            return true;
        }

        @Override
        public int getVertexColour() {
            return colour;
        }
    }

    class MultiColour extends AbstractMaterialRenderInfo {

        protected final int low, mid, high;

        public MultiColour(int low, int mid, int high) {
            this.low = low;
            this.mid = mid;
            this.high = high;
        }

        @Override
        public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
            return new SimpleColouredTexture(low, mid, high, baseTexture, location);
        }
    }

    class InverseMultiColour extends MultiColour {

        public InverseMultiColour(int low, int mid, int high) {
            super(low, mid, high);
        }

        @Override
        public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
            return new InverseColouredTexture(low, mid, high, baseTexture, location);
        }
    }

    class Metal extends AbstractMaterialRenderInfo {
        protected float shinyness;
        protected float brightness;
        protected float hueshift;
        public int colour;

        public Metal(int colour, float shinyness, float brightness, float hueshift) {
            this.colour = colour;
            this.shinyness = shinyness;
            this.brightness = brightness;
            this.hueshift = hueshift;
        }

        public Metal(int colour) {
            this(colour, 0.4f, 0.4f, 0.1f);
        }


        @Override
        public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
            return new MetalColouredTexture(baseTexture, location, colour, shinyness, brightness, hueshift);
        }
    }

    class BlockTexture extends AbstractMaterialRenderInfo {

        protected String texturePath;
        protected Block block;

        public BlockTexture(String texturePath) {
            this.texturePath = texturePath;
        }

        @Override
        public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
            TextureAtlasSprite blockTexture = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(texturePath);

            if(blockTexture == null) {
                blockTexture = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
            }

            TextureColouredTexture sprite = new TextureColouredTexture(blockTexture, baseTexture, location);
            sprite.stencil = false;

            return sprite;
        }
    }

    class AnimatedTexture extends AbstractMaterialRenderInfo {

        protected String texturePath;

        public AnimatedTexture(String texturePath) {
            this.texturePath = texturePath;
        }

        @Override
        public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
            TextureAtlasSprite blockTexture = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(texturePath);

            if(blockTexture == null) {
                blockTexture = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
            }

            TextureColouredTexture sprite = new AnimatedColouredTexture(blockTexture, baseTexture, location);

            return sprite;
        }
    }

}