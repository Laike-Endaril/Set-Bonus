package com.fantasticsource.setbonus.client.gui.equip;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBlacklist;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.mctools.items.RegistryRegexItemFilter;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.tools.datastructures.Color;

import java.util.ArrayList;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class EquipGUI extends GUIScreen
{
    public SetBonusData data;
    public GUIEquip clickedElement;
    public Equip equip;
    public GUILabeledTextInput id, domain, item, meta;
    public GUITextLabel requiredNBTLabel, disallowedNBTLabel;
    public GUIView requiredNBTView, disallowedNBTView;
    public GUIScrollView requiredNBT, disallowedNBT;
    public GUIVerticalScrollbar requiredNBTScrollbar, disallowedNBTScrollbar;


    public EquipGUI(SetBonusData data, GUIEquip clickedElement)
    {
        this.data = data;
        this.clickedElement = clickedElement;
        equip = clickedElement.equip;


        show();


        //Root
        root.add(new GUIDarkenedBackground(this));
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //ID
        ArrayList<String> idBlacklist = new ArrayList<>();
        idBlacklist.add("");
        for (GUIElement element : clickedElement.parent.children)
        {
            if (element == clickedElement) continue;
            if (element instanceof GUIEquip) idBlacklist.add(((GUIEquip) element).equip.id);
        }
        id = new GUILabeledTextInput(this, reformat(MODID + ".config.id") + ": ", equip.id, new FilterBlacklist(idBlacklist.toArray(new String[0])));
        root.addAll(id, new GUIElement(this, 1, 0));


        //Domain
        domain = new GUILabeledTextInput(this, reformat(MODID + ".config.domain") + ": ", equip.filter.domainRegex, FilterNone.INSTANCE);
        root.addAll(domain, new GUIElement(this, 1, 0));

        //Item
        item = new GUILabeledTextInput(this, reformat(MODID + ".config.item") + ": ", equip.filter.itemRegex, FilterNotEmpty.INSTANCE);
        root.addAll(item, new GUIElement(this, 1, 0));

        //Meta
        meta = new GUILabeledTextInput(this, reformat(MODID + ".config.meta") + ": ", equip.filter.metaRegex, FilterNone.INSTANCE);
        root.addAll(meta, new GUIElement(this, 1, 0));


        root.add(new GUITextButton(this, reformat(MODID + ".config.save"), Color.GREEN).addClickActions(() ->
        {
            if (!id.valid())
            {
                id.setText(equip.id);
                id.label.click();
            }
            else if (!item.valid())
            {
                item.setText(equip.filter.itemRegex);
                item.label.click();
            }
            else
            {
                //ID
                String oldID = equip.id;
                equip.id = id.getText();

                //Domain, item, meta
                equip.filter.domainRegex = domain.getText().trim();
                if (equip.filter.domainRegex.isEmpty()) equip.filter.domainRegex = ".*";
                equip.filter.itemRegex = item.getText().trim();
                equip.filter.metaRegex = meta.getText().trim();
                if (equip.filter.metaRegex.isEmpty()) equip.filter.metaRegex = ".*";

                //NBT
                //TODO
//            equip.filter.tagsRequired.clear();
//            equip.filter.tagsDisallowed.clear();


                data.equipment.put(equip.id, data.equipment.remove(oldID));

                for (Set set : data.sets.values())
                {
                    RegistryRegexItemFilter filter = set.involvedEquips.remove(oldID);
                    if (filter != null) set.involvedEquips.put(equip.id, filter);
                }


                clickedElement.set(equip);
                close();
            }
        }));

        root.add(new GUITextButton(this, reformat(MODID + ".config.cancel"), Color.RED).addClickActions(this::close));


        //NBT
        requiredNBTLabel = new GUITextLabel(this, 1, Color.GREEN);
        requiredNBTLabel.setText(reformat(MODID + ".config.requiredNBT"));
        root.add(requiredNBTLabel);

        requiredNBTView = new GUIView(this, 1, 1 - requiredNBTLabel.y - requiredNBTLabel.height * 2);
        requiredNBTLabel.addRecalcActions(() -> requiredNBTView.height = (1 - requiredNBTLabel.y - requiredNBTLabel.height * 2) * 0.5);
        root.add(requiredNBTView);

        requiredNBT = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1);
        requiredNBTScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, Color.AQUA, Color.BLANK, Color.AQUA, Color.BLANK, requiredNBT);
        requiredNBTView.addAll(requiredNBT, requiredNBTScrollbar);


        disallowedNBTLabel = new GUITextLabel(this, 1, Color.RED);
        disallowedNBTLabel.setText(reformat(MODID + ".config.disallowedNBT"));
        root.add(disallowedNBTLabel);

        disallowedNBTView = new GUIView(this, 1, 1 - disallowedNBTLabel.y - disallowedNBTLabel.height);
        disallowedNBTView.addRecalcActions(() -> disallowedNBTView.height = 1 - disallowedNBTLabel.y - disallowedNBTLabel.height);
        root.add(disallowedNBTView);

        disallowedNBT = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1);
        disallowedNBTScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, Color.AQUA, Color.BLANK, Color.AQUA, Color.BLANK, disallowedNBT);
        disallowedNBTView.addAll(disallowedNBT, disallowedNBTScrollbar);
    }

    @Override
    public String title()
    {
        return reformat(equip.id);
    }
}
