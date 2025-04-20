package com.fantasticsource.setbonus;

import com.fantasticsource.tools.ReflectionTool;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import mezz.jei.Internal;
import mezz.jei.ingredients.IngredientFilter;
import mezz.jei.ingredients.TooltipSearchTree;
import mezz.jei.suffixtree.CombinedSearchTrees;
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
        MinecraftForge.EVENT_BUS.register(ReflectionTool.get(IngredientFilter.class, "backgroundBuilder", ingredientFilter));
    }
}
