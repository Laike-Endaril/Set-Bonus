package com.fantasticsource.setbonus;

import com.fantasticsource.tools.ReflectionTool;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import mezz.jei.Internal;
import mezz.jei.config.Config;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.ingredients.AdaptiveIngredientFilterBackgroundBuilder;
import mezz.jei.ingredients.IngredientFilter;
import mezz.jei.ingredients.IngredientFilterBackgroundBuilder;
import mezz.jei.ingredients.TooltipSearchTree;
import mezz.jei.search.AdaptiveSearchable;
import mezz.jei.search.CombinedSearchables;
import mezz.jei.search.PrefixInfo;
import mezz.jei.search.PrefixedSearchable;
import mezz.jei.suffixtree.CombinedSearchTrees;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

import java.util.Map;
import java.util.Set;

public class Compat
{
    public static boolean jei = false, hei = false;

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
                    break;
            }
        }
    }

    public static void refreshJEITooltips()
    {
        IngredientFilter ingredientFilter = Internal.getIngredientFilter();
        Char2ObjectMap prefixedSearchTrees = (Char2ObjectMap) ReflectionTool.get(IngredientFilter.class, "prefixedSearchTrees", ingredientFilter);
        TooltipSearchTree tooltipSearchTree = new TooltipSearchTree();
        prefixedSearchTrees.put('#', tooltipSearchTree);

        //Based on IngredientFilter.modesChanged()
        Object searchTree = ReflectionTool.get(IngredientFilter.class, "searchTree", ingredientFilter);
        CombinedSearchTrees combinedSearchTrees = (CombinedSearchTrees) ReflectionTool.invoke(IngredientFilter.class, "buildCombinedSearchTrees", ingredientFilter, searchTree, prefixedSearchTrees.values());
        ReflectionTool.set(IngredientFilter.class, "combinedSearchTrees", ingredientFilter, combinedSearchTrees);
        ReflectionTool.set(IngredientFilter.class, "filterCached", ingredientFilter, null);

        //The part with lots of computation (per item count in modpack)
        //JEI already does this in a deferred, limited CPU-time-per-tick way, but I've improved upon their method...a LOT
        //Theirs simply used 40ms per client tick (20ms, 2 times per tick, once at START phase and once at END phase)
        //Mine limits CPU usage to END phase only, based on how much CPU time is still available to work within that tick without going below 20tps
        Object backgroundBuilder = ReflectionTool.get(IngredientFilter.class, "backgroundBuilder", ingredientFilter);
        if (backgroundBuilder instanceof AdaptiveIngredientFilterBackgroundBuilder) ((AdaptiveIngredientFilterBackgroundBuilder) backgroundBuilder).buildOrRebuild();
        else
        {
            MinecraftForge.EVENT_BUS.unregister(backgroundBuilder);
            NonNullList<IIngredientListElement> elementList = (NonNullList<IIngredientListElement>) ReflectionTool.get(IngredientFilterBackgroundBuilder.class, "elementList", backgroundBuilder);
            backgroundBuilder = new AdaptiveIngredientFilterBackgroundBuilder(prefixedSearchTrees.values(), elementList);
            ReflectionTool.set(IngredientFilter.class, "backgroundBuilder", ingredientFilter, backgroundBuilder);
        }
    }

    public static void refreshHEITooltips()
    {
        if ((boolean) ReflectionTool.invoke(Config.class, "isUltraLowMemoryMode", null))
        {
            System.out.println("NOTE: ULTRA LOW MEMORY MODE IS NOT COMPATIBLE WITH TOOLTIP SEARCHING (UNLESS CHANGED SINCE HEI 1.12.2-4.27.3)");
            return;
        }


        Class elementSearchClass = ReflectionTool.getClassByName("mezz.jei.search.ElementSearch");

        IngredientFilter ingredientFilter = Internal.getIngredientFilter();
        Object elementSearch = ReflectionTool.get(IngredientFilter.class, "elementSearch", ingredientFilter);
        Map<PrefixInfo, PrefixedSearchable> prefixedSearchables = (Map<PrefixInfo, PrefixedSearchable>) ReflectionTool.get(ReflectionTool.getClassByName("mezz.jei.search.ElementSearch"), "prefixedSearchables", elementSearch);
        CombinedSearchables combinedSearchables = new CombinedSearchables();
        for (PrefixInfo prefixInfo : prefixedSearchables.keySet())
        {
            if (prefixInfo.getPrefix() == '#')
            {
                PrefixedSearchable prefixedSearchable = prefixedSearchables.get(prefixInfo);
                if (prefixedSearchable instanceof AdaptiveSearchable) MinecraftForge.EVENT_BUS.unregister(prefixedSearchable);

                prefixedSearchable = new AdaptiveSearchable(prefixInfo);
                prefixedSearchables.put(prefixInfo, prefixedSearchable);
                combinedSearchables.addSearchable(prefixedSearchable);
                prefixedSearchable.submitAll(NonNullList.from(null, ((Set<IIngredientListElement>) ReflectionTool.invoke(elementSearchClass, "getAllIngredients", elementSearch)).toArray(new IIngredientListElement[0])));
            }
            else
            {
                combinedSearchables.addSearchable(prefixedSearchables.get(prefixInfo));
            }
        }
        ReflectionTool.set(elementSearchClass, "combinedSearchables", elementSearch, combinedSearchables);

        ingredientFilter.invalidateCache();
    }
}
