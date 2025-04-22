package mezz.jei.search;

import com.fantasticsource.setbonus.Compat;
import mezz.jei.config.Config;
import mezz.jei.gui.ingredients.IIngredientListElement;
import net.minecraft.client.Minecraft;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AdaptiveSearchable extends PrefixedSearchable
{
    protected IIngredientListElement[] ingredientsInProgress = new IIngredientListElement[0];
    protected int lastFinishedIndex = 0;
    protected long lastStepTime = 0;

    public AdaptiveSearchable(PrefixInfo prefixInfo)
    {
        super(prefixInfo.createStorage(), prefixInfo);
        buildOrRebuild();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (Minecraft.getMinecraft().player != null && event.phase == TickEvent.Phase.END) buildOrRebuild();
    }

    @Override
    public void submitAll(NonNullList<IIngredientListElement> ingredients)
    {
        if (prefixInfo.getMode() != Config.SearchMode.DISABLED)
        {
            ingredientsInProgress = ingredients.toArray(new IIngredientListElement[0]);
            lastFinishedIndex = 0;
            buildOrRebuild();
        }
    }

    @Override
    public void start()
    {
    }

    @Override
    public void stop()
    {
    }


    public void buildOrRebuild()
    {
        if (prefixInfo.getMode() == Config.SearchMode.DISABLED) return;


        if (lastStepTime == 0)
        {
            lastStepTime = System.currentTimeMillis() - Compat.msPerStep;
            MinecraftForge.EVENT_BUS.register(this);
        }

        final long stopTime = lastStepTime + Compat.msPerStep;
        while (lastFinishedIndex < ingredientsInProgress.length)
        {
            if (System.currentTimeMillis() >= stopTime)
            {
                lastStepTime = stopTime;
                return;
            }

            submit(ingredientsInProgress[lastFinishedIndex++]);
        }

        lastStepTime = 0;
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
