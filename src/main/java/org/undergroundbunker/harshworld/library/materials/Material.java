package org.undergroundbunker.harshworld.library.materials;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.undergroundbunker.harshworld.library.Util;
import org.undergroundbunker.harshworld.library.client.MaterialRenderInfo;
import org.undergroundbunker.harshworld.library.utils.RecipeMatchRegistry;

public class Material extends RecipeMatchRegistry {

    public static final Material UNKNOWN = new Material("unknown", TextFormatting.WHITE);

    public final String identifier;
    protected Fluid fluid;

    @SideOnly(Side.CLIENT)
    public MaterialRenderInfo renderInfo;
    public int materialTextColour = 0xFFFFFF;

    //private ItemStack representetiveItem;
    //private ItemStack sharrdItem;

    public Material(String identifier, TextFormatting textColour) {
        this(identifier, Util.enumChatFormattingToColour(textColour));
    }

    public Material(String identifier, int colour) {
        this.identifier = Util.sanitiseLocalisationString(identifier);

        if (((colour >> 24) & 0xFF) == 0) {
            colour |= 0xFF << 24;
        }

        this.materialTextColour = colour;
        if (FMLCommonHandler.instance().getSide().isClient()) {
            setRenderInfo(colour);
        }
    }

    public String getIdentifier() {
        return identifier;
    }


    @SideOnly(Side.CLIENT)
    public void setRenderInfo(MaterialRenderInfo renderInfo) {
        this.renderInfo = renderInfo;
    }

    @SideOnly(Side.CLIENT)
    public MaterialRenderInfo setRenderInfo(int colour) {
        setRenderInfo(new MaterialRenderInfo.Default(colour));
        return renderInfo;
    }
}
