package org.undergroundbunker.harshworld.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.MathHelper;
import org.undergroundbunker.harshworld.library.client.RenderUtil;

import java.awt.*;

public class MetalColouredTexture extends AbstractColouredTexture {

    protected int baseColour;
    protected float shinyness;
    protected float brightness;
    protected float hueShift;

    public MetalColouredTexture(TextureAtlasSprite baseTexture, String spriteName, int baseColour, float shinyness, float brightness, float hueShift) {

        super(baseTexture, spriteName);
        this.baseColour = baseColour;
        this.shinyness = shinyness;
        this.brightness = brightness;
        this.hueShift = hueShift;
    }

    public MetalColouredTexture(String baseTextureLocation, String spriteName, int baseColour, float shinyness, float brightness, float hueShift) {

        super(baseTextureLocation, spriteName);
        this.baseColour = baseColour;
        this.shinyness = shinyness;
        this.brightness = brightness;
        this.hueShift = hueShift;
    }

    @Override
    protected int colourPixel(int pixel, int mipmap, int pxCoord) {
        int a = RenderUtil.alpha(pixel);
        if(a == 0) {
            return pixel;
        }

        float l = getPerceptualBrightness(pixel)/255f;

        int c = baseColour;

        int r = RenderUtil.red(c);
        int b = RenderUtil.blue(c);
        int g = RenderUtil.green(c);

        r = mult(r, RenderUtil.red(pixel)) & 0xff;
        g = mult(g, RenderUtil.blue(pixel)) & 0xff;
        b = mult(b, RenderUtil.green(pixel)) & 0xff;

        float[] hsl = Color.RGBtoHSB(r,g,b, null);
        hsl[0] -= (0.5f-l*l) * hueShift;

        if(l > 0.9f) {
            hsl[1] = MathHelper.clamp_float(hsl[1] - (l * l * shinyness), 0, 1);
        }

        if(l > 0.8f)
            hsl[2] = MathHelper.clamp_float(hsl[2] + l*l*brightness, 0, 1);

        c = Color.HSBtoRGB(hsl[0], hsl[1], hsl[2]);
        r = RenderUtil.red(c);
        b = RenderUtil.blue(c);
        g = RenderUtil.green(c);

        return RenderUtil.compose(r, g, b, a);
    }

}
