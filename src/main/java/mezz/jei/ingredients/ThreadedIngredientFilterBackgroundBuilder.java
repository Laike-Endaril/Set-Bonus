package mezz.jei.ingredients;

import com.fantasticsource.setbonus.Compat;
import com.fantasticsource.tools.ReflectionTool;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import mezz.jei.Internal;
import mezz.jei.config.Config;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.suffixtree.CombinedSearchTrees;
import mezz.jei.suffixtree.GeneralizedSuffixTree;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ThreadedIngredientFilterBackgroundBuilder extends IngredientFilterBackgroundBuilder
{
    protected static volatile ThreadedData QUEUED_DATA = null, PROCESSED_DATA = null;
    protected static Thread thread = null;
    protected static final Runnable runnable = () ->
    {
        ThreadedData workingData;

        while (QUEUED_DATA != null)
        {
            workingData = QUEUED_DATA;
            QUEUED_DATA = null;


            Collection<String> strings;
            for (Map.Entry<PrefixedSearchTree.IStringsGetter, GeneralizedSuffixTree> entry : workingData.dataMap.entrySet())
            {
                GeneralizedSuffixTree tree = entry.getValue();
                for (int i = 0; i < workingData.elements.length; i++)
                {
                    strings = entry.getKey().getStrings(workingData.elements[i]);

                    if (strings.isEmpty()) tree.put("", i);
                    else for (String string : strings) tree.put(string, i);


                    if (QUEUED_DATA != null) break;
                }
                if (QUEUED_DATA != null) break;
            }


            if (QUEUED_DATA == null) PROCESSED_DATA = workingData;
        }
    };

    static
    {
        MinecraftForge.EVENT_BUS.register(ThreadedIngredientFilterBackgroundBuilder.class);
    }


    protected final Collection<PrefixedSearchTree> trees;
    protected final int elementCount;

    public ThreadedIngredientFilterBackgroundBuilder(Collection prefixedSearchTrees, NonNullList<IIngredientListElement> elementList)
    {
        super(null, null);
        trees = prefixedSearchTrees;
        elementCount = elementList.size();

        MinecraftForge.EVENT_BUS.register(this);

        QUEUED_DATA = new ThreadedData(prefixedSearchTrees, elementList);
        if (thread != null && thread.isAlive()) thread.stop();
        thread = new Thread(runnable);
        thread.setName("Set Bonus JEI Tooltip Reload");
        thread.start();
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && PROCESSED_DATA != null)
        {
            ThreadedData data = PROCESSED_DATA;
            PROCESSED_DATA = null;

            if (data.elements.length == elementCount)
            {
                for (PrefixedSearchTree tree : trees)
                {
                    ReflectionTool.set(PrefixedSearchTree.class, "tree", tree, data.dataMap.get(tree.getStringsGetter()));
                }

                IngredientFilter ingredientFilter = Internal.getIngredientFilter();
                Object searchTree = ReflectionTool.get(IngredientFilter.class, "searchTree", ingredientFilter);
                CombinedSearchTrees combinedSearchTrees = (CombinedSearchTrees) ReflectionTool.invoke(IngredientFilter.class, "buildCombinedSearchTrees", ingredientFilter, searchTree, trees);
                ReflectionTool.set(IngredientFilter.class, "combinedSearchTrees", ingredientFilter, combinedSearchTrees);
                ReflectionTool.set(IngredientFilter.class, "filterCached", ingredientFilter, null);
                Internal.getRuntime().getIngredientListOverlay().updateLayout(true);
            }
        }
    }

    @Override
    public void start()
    {
        Compat.refreshJEITooltips();
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
    }


    public static boolean needsReload(Char2ObjectMap prefixedSearchTrees)
    {
        for (Map.Entry<Character, PrefixedSearchTree> entry : ((Char2ObjectMap<PrefixedSearchTree>) prefixedSearchTrees).entrySet())
        {
            if (entry.getValue().getMode() != Config.SearchMode.DISABLED && entry.getKey() == '#') return true;
        }
        return false;
    }


    public static class ThreadedData
    {
        LinkedHashMap<PrefixedSearchTree.IStringsGetter, GeneralizedSuffixTree> dataMap = new LinkedHashMap<>();
        IIngredientListElement[] elements;

        ThreadedData(Collection<PrefixedSearchTree> prefixedSearchTrees, NonNullList<IIngredientListElement> elementList)
        {
            for (PrefixedSearchTree tree : prefixedSearchTrees)
            {
                if (tree.getMode() != Config.SearchMode.DISABLED) dataMap.put(tree.getStringsGetter(), new GeneralizedSuffixTree());
            }
            this.elements = elementList.toArray(new IIngredientListElement[0]);
        }
    }
}
