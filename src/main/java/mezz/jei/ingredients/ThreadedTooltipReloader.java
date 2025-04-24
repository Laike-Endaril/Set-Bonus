package mezz.jei.ingredients;

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
import java.util.Map;

public class ThreadedTooltipReloader
{
    protected static volatile ThreadedData QUEUED_DATA = null, PROCESSED_DATA = null;
    protected static volatile boolean STOP = false;
    protected static Thread thread = null;
    protected static final Runnable runnable = () ->
    {
        ThreadedData workingData;

        workingData = QUEUED_DATA;
        QUEUED_DATA = null;


        Collection<String> strings;
        for (int i = 0; i < workingData.elements.length; i++)
        {
            strings = workingData.stringsGetter.getStrings(workingData.elements[i]);

            if (strings.isEmpty()) workingData.tree.put("", i);
            else
            {
                for (String string : strings)
                {
                    workingData.tree.put(string, i);
                    if (STOP) break;
                }
            }


            if (STOP) break;
        }


        if (!STOP) PROCESSED_DATA = workingData;
        else STOP = false;
    };

    static
    {
        MinecraftForge.EVENT_BUS.register(ThreadedTooltipReloader.class);
    }


    protected final PrefixedSearchTree tooltipTree;
    protected final int elementCount;

    public ThreadedTooltipReloader(Char2ObjectMap prefixedSearchTrees, NonNullList<IIngredientListElement> elementList)
    {
        tooltipTree = (PrefixedSearchTree) prefixedSearchTrees.get('#');
        elementCount = elementList.size();

        MinecraftForge.EVENT_BUS.register(this);

        QUEUED_DATA = new ThreadedData(tooltipTree.getStringsGetter(), elementList);
        if (thread != null && thread.isAlive())
        {
            STOP = true;
            while (thread.isAlive()) ;
        }
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
                ReflectionTool.set(PrefixedSearchTree.class, "tree", tooltipTree, data.tree);

                IngredientFilter ingredientFilter = Internal.getIngredientFilter();
                Object searchTree = ReflectionTool.get(IngredientFilter.class, "searchTree", ingredientFilter);
                Char2ObjectMap<PrefixedSearchTree> trees = (Char2ObjectMap<PrefixedSearchTree>) ReflectionTool.get(IngredientFilter.class, "prefixedSearchTrees", ingredientFilter);
                CombinedSearchTrees combinedSearchTrees = (CombinedSearchTrees) ReflectionTool.invoke(IngredientFilter.class, "buildCombinedSearchTrees", ingredientFilter, searchTree, trees.values());
                ReflectionTool.set(IngredientFilter.class, "combinedSearchTrees", ingredientFilter, combinedSearchTrees);
                ReflectionTool.set(IngredientFilter.class, "filterCached", ingredientFilter, null);
                Internal.getRuntime().getIngredientListOverlay().updateLayout(true);
            }
        }
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
        PrefixedSearchTree.IStringsGetter stringsGetter;
        GeneralizedSuffixTree tree;
        IIngredientListElement[] elements;

        ThreadedData(PrefixedSearchTree.IStringsGetter stringsGetter, NonNullList<IIngredientListElement> elementList)
        {
            this.stringsGetter = stringsGetter;
            tree = new GeneralizedSuffixTree();
            this.elements = elementList.toArray(new IIngredientListElement[0]);
        }
    }
}
