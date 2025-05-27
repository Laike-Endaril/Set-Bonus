package mezz.jei.search;

import com.fantasticsource.tools.ReflectionTool;
import mezz.jei.Internal;
import mezz.jei.config.Config;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.ingredients.IngredientFilter;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Map;

public class ThreadedHEITooltipReloader
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
            IIngredientListElement element = workingData.elements[i];
            strings = workingData.stringsGetter.getStrings(element);

            for (String string : strings)
            {
                workingData.tree.put(string, element);
                if (STOP) break;
            }


            if (STOP) break;
        }


        if (!STOP) PROCESSED_DATA = workingData;
        else STOP = false;
    };


    protected final PrefixedSearchable tooltipSearchable;
    protected final int elementCount;

    public ThreadedHEITooltipReloader(Map<PrefixInfo, PrefixedSearchable> prefixedSearchables, NonNullList<IIngredientListElement> elements)
    {
        tooltipSearchable = getTooltipSearchable(prefixedSearchables);
        elementCount = elements.size();

        MinecraftForge.EVENT_BUS.register(this);

        QUEUED_DATA = new ThreadedData((PrefixInfo.IStringsGetter) ReflectionTool.get(PrefixInfo.class, "stringsGetter", tooltipSearchable.prefixInfo), elements);
        if (thread != null && thread.isAlive())
        {
            STOP = true;
            while (thread.isAlive()) ;
            System.out.println("Cancelled reloading HEI tooltips on background thread");
        }
        thread = new Thread(runnable);
        thread.setName("Set Bonus HEI Tooltip Reload");
        thread.start();
        System.out.println("Started reloading HEI tooltips on background thread");
    }

    protected static PrefixedSearchable getTooltipSearchable(Map<PrefixInfo, PrefixedSearchable> prefixedSearchables)
    {
        for (Map.Entry<PrefixInfo, PrefixedSearchable> entry : prefixedSearchables.entrySet())
        {
            if (entry.getKey().getPrefix() == '#')
            {
                return entry.getValue();
            }
        }
        return null;
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && PROCESSED_DATA != null)
        {
            ThreadedData data = PROCESSED_DATA;
            PROCESSED_DATA = null;

            ReflectionTool.set(PrefixedSearchable.class, "searchStorage", tooltipSearchable, data.tree);

            IngredientFilter ingredientFilter = Internal.getIngredientFilter();
            ingredientFilter.invalidateCache();
            ReflectionTool.set(IngredientFilter.class, "filterCached", ingredientFilter, null);
            Internal.getRuntime().getIngredientListOverlay().updateLayout(true);

            System.out.println("Finished reloading HEI tooltips on background thread");
        }
    }


    public static boolean needsReload(Map<PrefixInfo, PrefixedSearchable> prefixedSearchables)
    {
        for (Map.Entry<PrefixInfo, PrefixedSearchable> entry : prefixedSearchables.entrySet())
        {
            if (entry.getValue().getMode() != Config.SearchMode.DISABLED && entry.getKey().getPrefix() == '#') return true;
        }
        return false;
    }


    public static class ThreadedData
    {
        PrefixInfo.IStringsGetter stringsGetter;
        GeneralizedSuffixTree tree;
        IIngredientListElement[] elements;

        ThreadedData(PrefixInfo.IStringsGetter stringsGetter, NonNullList<IIngredientListElement> elements)
        {
            this.stringsGetter = stringsGetter;
            tree = new GeneralizedSuffixTree();
            this.elements = elements.toArray(new IIngredientListElement[0]);
        }
    }
}
