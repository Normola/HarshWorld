package org.undergroundbunker.harshworld.library.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import org.undergroundbunker.harshworld.library.client.RenderUtil;

public class TextureColouredTexture extends AbstractColouredTexture {

    protected TextureAtlasSprite addTexture;
    protected final String addTextureLocation;
    protected int[][] textureData;
    protected int textureW;
    protected int textureH;
    protected float scale;
    protected int offsetX = 0;
    protected int offsetY = 0;

    public boolean stencil = false;

    public TextureColouredTexture(String addTextureLocation, TextureAtlasSprite baseTexture, String spriteName) {

        super(baseTexture, spriteName);
        this.addTextureLocation = addTextureLocation;
        this.addTexture = null;
    }

    public TextureColouredTexture(TextureAtlasSprite addTexture, TextureAtlasSprite baseTexture, String spriteName) {

        super(baseTexture, spriteName);
        this.addTextureLocation = addTexture.getIconName();
        this.addTexture = addTexture;
    }

    @Override
    protected int colourPixel(int pixel, int mipmap, int pxCoord) {
        int a = RenderUtil.alpha(pixel);
        if(a == 0) {
            return pixel;
        }

        if(textureData == null) {
            loadData();
        }

        int texCoord = pxCoord;

        if(width > textureW) {
            int texX = (pxCoord % width) % textureW;
            int texY = (pxCoord / height) % textureH;
            texCoord = texY * textureW + texX;
        }

        int c = textureData[mipmap][texCoord];

        int r = RenderUtil.red(c);
        int b = RenderUtil.blue(c);
        int g = RenderUtil.green(c);

        if(!stencil) {
            r = mult(mult(r, RenderUtil.red(pixel)), RenderUtil.red(pixel));
            g = mult(mult(g, RenderUtil.green(pixel)), RenderUtil.green(pixel));
            b = mult(mult(b, RenderUtil.blue(pixel)), RenderUtil.blue(pixel));
        }
        return RenderUtil.compose(r, g, b, a);
    }

    protected void loadData() {
        if(addTexture == null || addTexture.getFrameCount() <= 0) {
            addTexture = backupLoadTexture(new ResourceLocation(addTextureLocation), Minecraft.getMinecraft().getResourceManager());
        }

        textureData = addTexture.getFrameTextureData(0);

        textureW = addTexture.getIconWidth();
        textureH = addTexture.getIconHeight();
        scale = (float)textureH/(float)width;
    }

    public void setOffset(int x, int y) {
        offsetX = x;
        offsetY = y;
    }

    protected int coord2(int x, int y) {
        return y * textureW + x;
    }

}
