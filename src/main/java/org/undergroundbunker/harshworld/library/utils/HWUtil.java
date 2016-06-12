package org.undergroundbunker.harshworld.library.utils;

import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagList;
import org.undergroundbunker.harshworld.library.HWRegistry;
import org.undergroundbunker.harshworld.library.materials.Material;

import java.util.List;

public class HWUtil {

    public static List<Material> getMaterialsFromTagList(NBTTagList tagList) {

        List<Material> materials = Lists.newLinkedList();

        if(tagList.getTagType() != TagUtil.TAG_TYPE_STRING) {
            return materials;
        }

        for (int i = 0; i < tagList.tagCount(); i++) {
            String identifier = tagList.getStringTagAt(i);
            Material material = HWRegistry.getMaterial(identifier);
            materials.add(material);
        }

        return materials;
    }
}
