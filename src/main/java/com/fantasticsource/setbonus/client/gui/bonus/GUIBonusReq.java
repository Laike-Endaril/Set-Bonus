package com.fantasticsource.setbonus.client.gui.bonus;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.tools.datastructures.Color;

public class GUIBonusReq extends GUITextLabel
{
    public ABonusRequirement requirement;

    public GUIBonusReq(GUIScreen screen, SetBonusData data, ABonusRequirement requirement, double width)
    {
        this(screen, data, requirement, width, 1);
    }

    public GUIBonusReq(GUIScreen screen, SetBonusData data, ABonusRequirement requirement, double width, double scale)
    {
        super(screen, width, Color.AQUA, scale);
        set(requirement);

        addClickActions(() ->
        {
            if (internalText.activeColor == Color.PURPLE) new BonusReqGUI(data, this);
            else if (screen instanceof ServerConfigGUI) ((ServerConfigGUI) screen).select(this);
        });
    }


    public void set(ABonusRequirement requirement)
    {
        this.requirement = requirement;
        internalText.setText(requirement == null ? "" : requirement.toString());
        runEditActions();
    }
}
