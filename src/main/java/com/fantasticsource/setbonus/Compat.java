package com.fantasticsource.setbonus;

import com.fantasticsource.setbonus.config.SetBonusConfig;
import com.fantasticsource.tools.ReflectionTool;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import mezz.jei.Internal;
import mezz.jei.config.Config;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.ingredients.IngredientFilter;
import mezz.jei.ingredients.ThreadedJEITooltipReloader;
import mezz.jei.search.PrefixInfo;
import mezz.jei.search.PrefixedSearchable;
import mezz.jei.search.ThreadedHEITooltipReloader;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

import java.util.Map;
import java.util.Set;

public class Compat
{
    public static boolean jei = false, hei = false;
    public static Class elementSearchClass;
    public static int minMSPerStep = 10, maxMSPerStep = 30;
    public static Object threadedTooltipReloader = null;

    public static void init()
    {
        if (Loader.isModLoaded("jei"))
        {
            switch (Loader.instance().getIndexedModList().get("jei").getName())
            {
                case "Just Enough Items":
                    jei = true;
                    break;

                case "Had Enough Items":
                    hei = true;
                    elementSearchClass = ReflectionTool.getClassByName("mezz.jei.search.ElementSearch");
                    break;
            }
        }
    }


    public static void refreshTooltips()
    {
        //No point in refreshing tooltips if we don't have any
        if (!SetBonusConfig.clientSettings.enableTooltips) return;


        if (jei) refreshJEITooltips();
        else if (hei) refreshHEITooltips();
    }

    public static void refreshJEITooltips()
    {
        IngredientFilter ingredientFilter = Internal.getIngredientFilter();
        Char2ObjectMap prefixedSearchTrees = (Char2ObjectMap) ReflectionTool.get(IngredientFilter.class, "prefixedSearchTrees", ingredientFilter);
        if (!ThreadedJEITooltipReloader.needsReload(prefixedSearchTrees)) return;


        if (threadedTooltipReloader != null) MinecraftForge.EVENT_BUS.unregister(threadedTooltipReloader);
        NonNullList<IIngredientListElement> elementList = (NonNullList<IIngredientListElement>) ReflectionTool.get(IngredientFilter.class, "elementList", ingredientFilter);
        threadedTooltipReloader = new ThreadedJEITooltipReloader(prefixedSearchTrees, elementList);
    }

    public static void refreshHEITooltips()
    {
        if ((boolean) ReflectionTool.invoke(Config.class, "isUltraLowMemoryMode", null))
        {
            System.out.println("NOTE: HEI ULTRA LOW MEMORY MODE IS NOT COMPATIBLE WITH TOOLTIP SEARCHING (UNLESS CHANGED SINCE HEI 1.12.2-4.27.3)");
            return;
        }


        IngredientFilter ingredientFilter = Internal.getIngredientFilter();
        Object elementSearch = ReflectionTool.get(IngredientFilter.class, "elementSearch", ingredientFilter);
        Map<PrefixInfo, PrefixedSearchable> prefixedSearchables = (Map<PrefixInfo, PrefixedSearchable>) ReflectionTool.get(ReflectionTool.getClassByName("mezz.jei.search.ElementSearch"), "prefixedSearchables", elementSearch);
        if (!ThreadedHEITooltipReloader.needsReload(prefixedSearchables)) return;


        if (threadedTooltipReloader != null) MinecraftForge.EVENT_BUS.unregister(threadedTooltipReloader);
        Set<IIngredientListElement<?>> allIngredients = new ReferenceOpenHashSet<>();
        prefixedSearchables.get(PrefixInfo.NO_PREFIX).getAllElements(allIngredients);
        NonNullList<IIngredientListElement> elements = NonNullList.from(null, allIngredients.toArray(new IIngredientListElement[0]));
        threadedTooltipReloader = new ThreadedHEITooltipReloader(prefixedSearchables, elements);
    }
}
