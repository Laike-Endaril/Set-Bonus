package com.fantasticsource.setbonus.client.gui.bonus.element;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class PotionEffectGUI extends GUIScreen
{
    public GUIPotionEffect clickedElement;
    public FantasticPotionEffect potionEffect;

    public PotionEffectGUI(GUIPotionEffect clickedElement)
    {
        this.clickedElement = clickedElement;
        potionEffect = clickedElement.potionEffect;
        if (potionEffect == null) potionEffect = new FantasticPotionEffect(MobEffects.INVISIBILITY, FantasticPotionEffect.MAX_DURATION_THRESHOLD);


        show();


        //Root
        root.add(new GUIDarkenedBackground(this));
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //Save, load, etc
        GUITextButton save = new GUITextButton(this, reformat(MODID + ".config.save"), Color.GREEN);
        root.add(save);
        root.add(new GUITextButton(this, reformat(MODID + ".config.cancel"), Color.ORANGE).addClickActions(this::close));
        root.add(new GUITextButton(this, reformat(MODID + ".config.delete"), Color.RED).addClickActions(() ->
        {
            clickedElement.set(null);
            close();
        }));
        root.add(new GUITextSpacer(this));


        //Potion Name
        GUILabeledTextInput name = new GUILabeledTextInput(this, reformat(MODID + ".config.potionName") + ": ", potionEffect.getPotion().getRegistryName().toString(), FilterNotEmpty.INSTANCE);
        root.add(name);
        root.add(new GUIElement(this, 1, 0));

        //Level
        GUILabeledTextInput level = new GUILabeledTextInput(this, reformat(MODID + ".config.level") + ": ", "" + (potionEffect.getAmplifier() >= 0 ? potionEffect.getAmplifier() + 1 : potionEffect.getAmplifier()), FilterInt.INSTANCE);
        root.add(level);
        root.add(new GUIElement(this, 1, 0));

        //Duration
        GUILabeledTextInput duration = new GUILabeledTextInput(this, reformat(MODID + ".config.duration") + ": ", "" + potionEffect.getDuration(), FilterRangedInt.get(0, Integer.MAX_VALUE));
        root.add(duration);
        root.add(new GUIElement(this, 1, 0));

        //Interval
        GUILabeledTextInput interval = new GUILabeledTextInput(this, reformat(MODID + ".config.interval") + ": ", "" + potionEffect.interval, FilterRangedInt.get(0, Integer.MAX_VALUE));
        root.add(interval);
        root.add(new GUIElement(this, 1, 0));


        //Save actions
        save.addClickActions(() ->
        {
            if (!name.valid())
            {
                name.setText(potionEffect.getPotion().getRegistryName().toString());
                name.label.click();
            }
            else if (!level.valid())
            {
                level.setText("" + (potionEffect.getAmplifier() >= 0 ? potionEffect.getAmplifier() + 1 : potionEffect.getAmplifier()));
                level.label.click();
            }
            else if (!duration.valid())
            {
                duration.setText("" + potionEffect.getDuration());
                duration.label.click();
            }
            else if (!interval.valid())
            {
                interval.setText("" + potionEffect.interval);
                interval.label.click();
            }
            else
            {
                //Update GUI
                int lvl = FilterInt.INSTANCE.parse(level.getText());
                FantasticPotionEffect potionEffect = new FantasticPotionEffect(ForgeRegistries.POTIONS.getValue(new ResourceLocation(name.getText())), FilterInt.INSTANCE.parse(duration.getText()), lvl <= 0 ? lvl : lvl - 1);
                potionEffect.interval = FilterInt.INSTANCE.parse(interval.getText());
                clickedElement.set(potionEffect);
                close();
            }
        });
    }


    @Override
    public String title()
    {
        return reformat(MODID + ".config.attributeModifier");
    }
}
