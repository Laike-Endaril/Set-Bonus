package mezz.jei.ingredients;

import com.fantasticsource.setbonus.Compat;
import mezz.jei.config.Config;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.suffixtree.GeneralizedSuffixTree;
import net.minecraft.client.Minecraft;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collection;

public class AdaptiveIngredientFilterBackgroundBuilder extends IngredientFilterBackgroundBuilder
{
    //This class only extends IngredientFilterBackgroundBuilder for compat with original JEI when reflecting into it
    //If this was ever added directly to JEI code, it could simply be the new version of the existing IngredientFilterBackgroundBuilder instead, and the start() method override could be removed
    //It could also use Collection<PrefixedSearchTree> instead of a generic Collection as an argument, and no cast in the constructor (I only did this because PrefixedSearchTree is non-public)
    protected final Collection<PrefixedSearchTree> prefixedSearchTrees;
    protected final NonNullList<IIngredientListElement> elementList;
    protected long lastStepTime = 0;

    public AdaptiveIngredientFilterBackgroundBuilder(Collection prefixedSearchTrees, NonNullList<IIngredientListElement> elementList)
    {
        super(null, null);
        this.prefixedSearchTrees = (Collection<PrefixedSearchTree>) prefixedSearchTrees;
        this.elementList = elementList;
        buildOrRebuild();
    }

    @Override
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (Minecraft.getMinecraft().player != null && event.phase == TickEvent.Phase.END) buildOrRebuild();
    }

    @Override
    public void start()
    {
        buildOrRebuild();
    }

    public void buildOrRebuild()
    {
        if (lastStepTime == 0)
        {
            lastStepTime = System.currentTimeMillis() - Compat.msPerStep;
            MinecraftForge.EVENT_BUS.register(this);
        }


        final long stopTime = lastStepTime + Compat.msPerStep;
        for (PrefixedSearchTree prefixedTree : this.prefixedSearchTrees)
        {
            Config.SearchMode mode = prefixedTree.getMode();
            if (mode != Config.SearchMode.DISABLED)
            {
                PrefixedSearchTree.IStringsGetter stringsGetter = prefixedTree.getStringsGetter();
                GeneralizedSuffixTree tree = prefixedTree.getTree();
                for (int i = tree.getHighestIndex() + 1; i < this.elementList.size(); i++)
                {
                    if (System.currentTimeMillis() >= stopTime)
                    {
                        lastStepTime = stopTime;
                        return;
                    }

                    IIngredientListElement element = elementList.get(i);
                    Collection<String> strings = stringsGetter.getStrings(element);

                    if (strings.isEmpty()) tree.put("", i);
                    else for (String string : strings) tree.put(string, i);
                }
            }
        }

        lastStepTime = 0;
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
