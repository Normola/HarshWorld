package org.undergroundbunker.harshworld.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.undergroundbunker.harshworld.library.client.RenderUtil;

public class InverseColouredTexture extends SimpleColouredTexture {

    public InverseColouredTexture(int colourLow, int colourMid, int colourHigh, TextureAtlasSprite baseTexture, String spriteName) {
        super(colourLow, colourMid, colourHigh, baseTexture, spriteName);
    }

    @Override
    protected int colourPixel(int pixel, int mipmap, int pxCoord) {
        int a = RenderUtil.alpha(pixel);
        if(a == 0) {
            return pixel;
        }

        int brightness = getPerceptualBrightness(pixel);
        int c = colourMid;
        if(brightness < minBrightness) {
            c = colourLow;
        }
        else if(brightness > maxBrightness) {
            c = colourHigh;
        }

        int r = RenderUtil.red(c);
        int b = RenderUtil.blue(c);
        int g = RenderUtil.green(c);

        r = ~mult(r, brightness) & 0xff;
        g = ~mult(g, brightness) & 0xff;
        b = ~mult(b, brightness) & 0xff;

        return RenderUtil.compose(r, g, b, a);
    }
}
