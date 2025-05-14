package com.fantasticsource.setbonus.client.gui.set;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.tools.datastructures.Color;

public class GUISet extends GUITextLabel
{
    public Set set;

    public GUISet(GUIScreen screen, Set set, double width)
    {
        this(screen, set, width, 1);
    }

    public GUISet(GUIScreen screen, Set set, double width, double scale)
    {
        super(screen, width, Color.AQUA, Color.BLANK, scale);
        set(set);
        setColor(Color.AQUA);

        addClickActions(() ->
        {
            if (internalText.activeColor == Color.PURPLE) new SetGUI(this);
            else
            {
                setColor(Color.PURPLE);
                if (parent != null)
                {
                    for (GUIElement element : parent.children)
                    {
                        if (element instanceof GUISet && element != this) ((GUISet) element).setColor(Color.AQUA);
                    }
                }
            }
        });
    }


    public void set(Set set)
    {
        this.set = set;
        internalText.setText(reformat(set.name));
    }
}
