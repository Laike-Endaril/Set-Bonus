package com.fantasticsource.setbonus.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BonusScreen extends GuiScreen
{
    @Override
    public boolean doesGuiPauseGame()
    {
        return true;
    }
}
