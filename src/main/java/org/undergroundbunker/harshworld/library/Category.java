package org.undergroundbunker.harshworld.library;

import com.google.common.collect.Maps;

import java.util.Locale;
import java.util.Map;

public class Category {

    public static Map<String, Category> categories = Maps.newHashMap();

    public static final Category TOOL = new Category("tool");
    public static final Category WEAPON = new Category("weapon");
    public static final Category HARVEST = new Category("harvest");
    public static final Category PROJECTILE = new Category("projectile");
    public static final Category NO_MELEE = new Category("no_melee");

    public final String name;

    public Category(String name) {
        this.name = name.toLowerCase(Locale.US);
        categories.put(name, this);
    }
}
