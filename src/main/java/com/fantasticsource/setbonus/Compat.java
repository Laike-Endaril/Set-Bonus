package com.fantasticsource.setbonus;

import com.fantasticsource.tools.ReflectionTool;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import mezz.jei.Internal;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.ingredients.AdaptiveIngredientFilterBackgroundBuilder;
import mezz.jei.ingredients.IngredientFilter;
import mezz.jei.ingredients.IngredientFilterBackgroundBuilder;
import mezz.jei.ingredients.TooltipSearchTree;
import mezz.jei.suffixtree.CombinedSearchTrees;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

public class Compat
{
    public static boolean jei = false;

    public static void init()
    {
        if (Loader.isModLoaded("jei")) jei = true;
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
}
