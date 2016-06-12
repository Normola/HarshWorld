package org.undergroundbunker.harshworld.library.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class RenderUtil {

    private RenderUtil() {
    }

    public static float FLUID_OFFSET = 0.005f;

    public static void renderTiledTextureAtlas(int x, int y, int width, int height, float depth, TextureAtlasSprite sprite) {

        Minecraft mc = Minecraft.getMinecraft();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldRenderer = tessellator.getBuffer();

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        putTiledTextureQuads(worldRenderer, x, y, width, height, depth, sprite);

        tessellator.draw();
    }

    public static void renderTiledFluid(int x, int y, int width, int height, float depth, FluidStack fluidStack) {

        Minecraft mc = Minecraft.getMinecraft();
        TextureAtlasSprite fluidSprite = mc.getTextureMapBlocks().getAtlasSprite(fluidStack.getFluid().getStill(fluidStack).toString());

        renderTiledTextureAtlas(x, y, width, height, depth, fluidSprite);
    }

    public static void putTiledTextureQuads(VertexBuffer renderer, int x, int y, int width, int height, float depth, TextureAtlasSprite sprite) {

        float u1 = sprite.getMinU();
        float v1 = sprite.getMinV();

        do {
            int renderHeight = Math.min(sprite.getIconHeight(), height);
            height -= renderHeight;

            float v2 = sprite.getInterpolatedV((16f * renderHeight)/(float)sprite.getIconHeight());

            int x2 = x;
            int width2 = width;

            do {
                int renderWidth = Math.min(sprite.getIconWidth(), width2);
                width2 -= renderWidth;

                float u2 = sprite.getInterpolatedU((16f * renderWidth)/(float)sprite.getIconWidth());

                renderer.pos(x2,               y,                depth).tex(u1, v1).endVertex();
                renderer.pos(x2,               y + renderHeight, depth).tex(u1, v2).endVertex();
                renderer.pos(x2 + renderWidth, y + renderHeight, depth).tex(u2, v2).endVertex();
                renderer.pos(x2 + renderWidth, y,                depth).tex(u2, v1).endVertex();

                x2 += renderWidth;
            } while(width2 > 0);

            y += renderHeight;
        } while(height > 0);
    }

    public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double w, double h, double d) {

        double wd = (1d-w) / 2d;
        double hd = (1d-h) / 2d;
        double dd = (1d-d) / 2d;

        renderFluidCuboid(fluid, pos, x, y, z, wd, hd, dd, 1d-wd, 1d-hd, 1d-dd);
    }

    public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2) {

        int color = fluid.getFluid().getColor(fluid);
        renderFluidCuboid(fluid, pos, x, y, z, x1, y1, z1, x2, y2, z2, color);
    }

    public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, int color) {

        Minecraft mc = Minecraft.getMinecraft();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer renderer = tessellator.getBuffer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        int brightness = mc.theWorld.getCombinedLight(pos, fluid.getFluid().getLuminosity());

        pre(x, y, z);

        TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
        TextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

        putTexturedQuad(renderer, still,   x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.DOWN,  color, brightness, false);
        putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.NORTH, color, brightness, true);
        putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.EAST,  color, brightness, true);
        putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.SOUTH, color, brightness, true);
        putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.WEST,  color, brightness, true);
        putTexturedQuad(renderer, still  , x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.UP,    color, brightness, false);

        tessellator.draw();

        post();
    }

    public static void renderStackedFluidCuboid(FluidStack fluid, double px, double py, double pz, BlockPos pos, BlockPos from, BlockPos to, double yMin, double yMax) {

        if (yMin >= yMax) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer renderer = tessellator.getBuffer();

        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        int color = fluid.getFluid().getColor(fluid);
        int brightness = mc.theWorld.getCombinedLight(pos, fluid.getFluid().getLuminosity());

        pre(px, py, pz);
        GlStateManager.translate(from.getX(), from.getY(), from.getZ());

        TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
        TextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

        if (still == null) {
            still = mc.getTextureMapBlocks().getMissingSprite();
        }
        if (flowing == null) {
            flowing = mc.getTextureMapBlocks().getMissingSprite();
        }

        int xd = to.getX() - from.getX();
        int yd = (int) (yMax - yMin);
        int zd = to.getZ() - from.getZ();

        double xMin = FLUID_OFFSET;
        double xMax = xd + 1d - FLUID_OFFSET;

        double zMin = FLUID_OFFSET;
        double zMax = zd + 1d - FLUID_OFFSET;

        double[] xs = new double[2 + xd];
        double[] ys = new double[2 + yd];
        double[] zs = new double[2 + zd];

        xs[0] = xMin;

        for (int i = 1; i <= xd; i++) {
            xs[i] = i;
        }

        xs[xd + 1] = xMax;
        ys[0] = yMin;

        for (int i = 1; i <= yd; i++) {
            ys[i] = i;
        }

        ys[yd + 1] = yMax;
        zs[0] = zMin;

        for (int i = 1; i <= zd; i++) {
            zs[i] = i;
        }

        zs[zd + 1] = zMax;

        for (int y = 0; y <= yd; y++) {
            for (int z = 0; z <= zd; z++) {
                for (int x = 0; x <= xd; x++) {

                    double x1 = xs[x];
                    double x2 = xs[x + 1] - x1;
                    double y1 = ys[y];
                    double y2 = ys[y + 1] - y1;
                    double z1 = zs[z];
                    double z2 = zs[z + 1] - z1;

                    if (x == 0)
                        putTexturedQuad(renderer, flowing, x1, y1, z1, x2, y2, z2, EnumFacing.WEST, color, brightness, true);
                    if (x == xd)
                        putTexturedQuad(renderer, flowing, x1, y1, z1, x2, y2, z2, EnumFacing.EAST, color, brightness, true);
                    if (y == 0)
                        putTexturedQuad(renderer, still, x1, y1, z1, x2, y2, z2, EnumFacing.DOWN, color, brightness, false);
                    if (y == yd)
                        putTexturedQuad(renderer, still, x1, y1, z1, x2, y2, z2, EnumFacing.UP, color, brightness, false);
                    if (z == 0)
                        putTexturedQuad(renderer, flowing, x1, y1, z1, x2, y2, z2, EnumFacing.NORTH, color, brightness, true);
                    if (z == zd)
                        putTexturedQuad(renderer, flowing, x1, y1, z1, x2, y2, z2, EnumFacing.SOUTH, color, brightness, true);
                }
            }
        }
        tessellator.draw();

        post();
    }

    public static void putTexturedCuboid(VertexBuffer renderer, ResourceLocation location, double x1, double y1, double z1, double x2, double y2, double z2, int color, int brightness) {

        Minecraft mc = Minecraft.getMinecraft();

        boolean flowing = false;
        TextureAtlasSprite sprite = mc.getTextureMapBlocks().getTextureExtry(location.toString());
        putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.DOWN, color, brightness, flowing);
        putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.NORTH, color, brightness, flowing);
        putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.EAST, color, brightness, flowing);
        putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.SOUTH, color, brightness, flowing);
        putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.WEST, color, brightness, flowing);
        putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.UP, color, brightness, flowing);
    }

    public static void putTexturedQuad(VertexBuffer renderer, TextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, EnumFacing face, int color, int brightness, boolean flowing) {

        int l1 = brightness >> 0x10 & 0xFFFF;
        int l2 = brightness & 0xFFFF;

        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;

        putTexturedQuad(renderer, sprite, x,y,z, w,h, d, face, r,g,b,a, l1, l2, flowing);
    }

    public static void putTexturedQuad(VertexBuffer renderer, TextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, EnumFacing face, int r, int g, int b, int a, int light1, int light2, boolean flowing) {

        if(sprite == null) {
            return;
        }

        double minU;
        double maxU;
        double minV;
        double maxV;

        double size = 16f;
        if(flowing) size = 8f;

        double x1 = x;
        double x2 = x + w;
        double y1 = y;
        double y2 = y + h;
        double z1 = z;
        double z2 = z + d;

        double xt1 = x1%1d;
        double xt2 = xt1 + w;
        while(xt2 > 1f) xt2 -= 1f;
        double yt1 = y1%1d;
        double yt2 = yt1 + h;
        while(yt2 > 1f) yt2 -= 1f;
        double zt1 = z1%1d;
        double zt2 = zt1 + d;
        while(zt2 > 1f) zt2 -= 1f;

        if(flowing) {
            double tmp = 1d - yt1;
            yt1 = 1d - yt2;
            yt2 = tmp;
        }

        switch(face) {
            case DOWN:
            case UP:
                minU = sprite.getInterpolatedU(xt1 * size);
                maxU = sprite.getInterpolatedU(xt2 * size);
                minV = sprite.getInterpolatedV(zt1 * size);
                maxV = sprite.getInterpolatedV(zt2 * size);
                break;
            case NORTH:
            case SOUTH:
                minU = sprite.getInterpolatedU(xt2 * size);
                maxU = sprite.getInterpolatedU(xt1 * size);
                minV = sprite.getInterpolatedV(yt1 * size);
                maxV = sprite.getInterpolatedV(yt2 * size);
                break;
            case WEST:
            case EAST:
                minU = sprite.getInterpolatedU(zt2 * size);
                maxU = sprite.getInterpolatedU(zt1 * size);
                minV = sprite.getInterpolatedV(yt1 * size);
                maxV = sprite.getInterpolatedV(yt2 * size);
                break;
            default:
                minU = sprite.getMinU();
                maxU = sprite.getMaxU();
                minV = sprite.getMinV();
                maxV = sprite.getMaxV();
        }

        switch(face) {
            case DOWN:
                renderer.pos(x1, y1, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y1, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y1, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y1, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                break;
            case UP:
                renderer.pos(x1, y2, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y2, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                break;
            case NORTH:
                renderer.pos(x1, y1, z1).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y2, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y1, z1).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                break;
            case SOUTH:
                renderer.pos(x1, y1, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y1, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z2).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y2, z2).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                break;
            case WEST:
                renderer.pos(x1, y1, z1).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y1, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y2, z2).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y2, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                break;
            case EAST:
                renderer.pos(x2, y1, z1).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y1, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                break;
        }
    }

    public static void pre(double x, double y, double z) {

        GlStateManager.pushMatrix();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (Minecraft.isAmbientOcclusionEnabled())
        {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        }
        else
        {
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        GlStateManager.translate(x, y, z);
    }

    public static void post() {
        GlStateManager.disableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    public static void setColorRGB(int color) {
        setColorRGBA(color | 0xff000000);
    }

    public static void setColorRGBA(int color) {
        float a = (float)alpha(color) / 255.0F;
        float r = (float)red(color) / 255.0F;
        float g = (float)green(color) / 255.0F;
        float b = (float)blue(color) / 255.0F;

        GlStateManager.color(r, g, b, a);
    }


    public static void setBrightness(VertexBuffer renderer, int brightness) {

        renderer.putBrightness4(brightness, brightness, brightness, brightness);
    }

    public static int compose(int r, int g, int b, int a) {

        int rgb = a;
        rgb = (rgb << 8) + r;
        rgb = (rgb << 8) + g;
        rgb = (rgb << 8) + b;
        return rgb;
    }

    public static int alpha(int c) {
        return (c >> 24) & 0xFF;
    }

    public static int red(int c) {
        return (c >> 16) & 0xFF;
    }

    public static int green(int c) {
        return (c >> 8) & 0xFF;
    }

    public static int blue(int c) {
        return (c) & 0xFF;
    }

}
