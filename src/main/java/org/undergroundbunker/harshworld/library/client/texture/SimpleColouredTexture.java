package org.undergroundbunker.harshworld.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.undergroundbunker.harshworld.library.client.RenderUtil;

public class SimpleColouredTexture extends AbstractColouredTexture {

    protected final int colourLow, colourMid, colourHigh;

    public SimpleColouredTexture(int colourLow, int colourMid, int colourHigh, TextureAtlasSprite baseTexture, String spriteName) {

        super(baseTexture, spriteName);

        this.colourLow = colourLow;
        this.colourMid = colourMid;
        this.colourHigh = colourHigh;
    }

    int minBrightness;
    int maxBrightness;

    float weight = 0.60f;

    @Override
    protected void processData(int[][] data) {
        int max = 0;
        int min = 255;

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                int c = data[0][y * width + x];
                if(RenderUtil.alpha(c) == 0)
                    continue;
                int b = getPerceptualBrightness(c);
                if(b < min)
                    min = b;
                if(b > max)
                    max = b;
            }
        }

        int d = max-min;
        d /= 2;
        minBrightness = Math.max(min+1, min + (int)(d * 0.4f));
        maxBrightness = Math.min(max-1, max - (int)(d * 0.3f));

        super.processData(data);
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

        r = mult(r, RenderUtil.red(pixel)) & 0xff;
        g = mult(g, RenderUtil.blue(pixel)) & 0xff;
        b = mult(b, RenderUtil.green(pixel)) & 0xff;

        return RenderUtil.compose(r, g, b, a);
    }
}
