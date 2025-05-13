package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class SetBonusConfigGUI extends GUIScreen
{
    public SetBonusConfigGUI()
    {
        show();


        //Root
        root.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);

        root.add(new GUIDarkenedBackground(this));
        GUIElement element = new GUINavbar(this);
        ((GUINavbar) element).maxParentsDisplayed = 0;
        root.add(element);

        root.add(new GUITextSpacer(this));

        element = new GUITextButton(this, reformat(MODID + ".config.clientSettings"));
        element.onClickActions.add(ClientConfigGUI::new);
        root.add(element);

//        //Scrollview
//        double x = 0, y = element.height, w = 0.98, h = 1 - element.height;
//        GUIScrollView view = new GUIScrollView(this, x, y, w, h);
//        root.add(view);
//        root.add(new GUIVerticalScrollbar(this, view.x + view.width, view.y, 0.02, view.height, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, view));


        //TODO Client
        //TODO "Enable tooltips"

        //TODO Server
        //TODO Equipment
        //TODO Sets
        //TODO Set Bonuses
        //TODO Set Bonus Elements
        //TODO Remember to do lang support in the GUI
        //TODO Remember to do explanations, also with lang support
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
