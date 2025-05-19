package com.fantasticsource.setbonus.client.gui.equip;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBlacklist;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.items.RegistryRegexItemFilter;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;

import java.util.ArrayList;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class EquipGUI extends GUIScreen
{
    public SetBonusData data;
    public GUIEquip clickedElement;
    public Equip equip;
    public GUILabeledTextInput id, domain, item, meta;


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


        //NBT
        //TODO
    }

    @Override
    public void onClosed()
    {
        super.onClosed();
        if (id.valid() && item.valid())
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
        }

        clickedElement.set(equip);
    }

    @Override
    public String title()
    {
        return reformat(equip.id);
    }
}
