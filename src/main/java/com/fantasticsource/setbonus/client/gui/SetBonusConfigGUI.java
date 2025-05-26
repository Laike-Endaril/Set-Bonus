package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiConfig;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class SetBonusConfigGUI extends GUIScreen
{
    public static boolean dontReplace = false;

    public SetBonusConfigGUI(GuiConfig oldGUI)
    {
        show();


        //Root
        root.add(new GUIDarkenedBackground(this));
        GUITextButton element = new GUINavbar(this);
        element.autoplace = false;
        ((GUINavbar) element).maxParentsDisplayed = 0;
        root.add(element);


        root.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);


        //Client settings
        element = new GUITextButton(this, reformat(MODID + ".config.clientSettings"));
        element.addClickActions(ClientConfigGUI::new);
        element.setColor(Color.AQUA);
        root.add(element);
        root.add(new GUITextSpacer(this));


        //Server settings
        element = new GUITextButton(this, reformat(MODID + ".config.serverSettings"));
        element.addClickActions(() ->
        {
            if (!SetBonusData.setServerFromConfigCalled) SetBonusData.setServerFromConfig();
            new ServerConfigGUI(SetBonusData.SERVER_DATA.clone());
        });
        element.setColor(Color.AQUA);
        root.add(element);
        root.add(new GUITextSpacer(this));


        //Forge config GUI
        element = new GUITextButton(this, reformat(MODID + ".config.oldGUI"));
        element.addClickActions(() ->
        {
            addPostClosedActions(() ->
            {
                dontReplace = true;
                Minecraft.getMinecraft().displayGuiScreen(oldGUI);
            });
            close();
        });
        element.setColor(Color.AQUA);
        root.add(element);
    }


    @Override
    public void show()
    {
        showStacked();
    }

    @Override
    public String title()
    {
        return "Set Bonus";
    }
}
