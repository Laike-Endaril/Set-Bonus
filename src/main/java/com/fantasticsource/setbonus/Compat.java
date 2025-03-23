package com.fantasticsource.setbonus;

import com.fantasticsource.tools.ReflectionTool;
import mezz.jei.JustEnoughItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.gui.textures.Textures;
import mezz.jei.startup.JeiStarter;
import mezz.jei.startup.ProxyCommonClient;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Method;
import java.util.List;

public class Compat
{
    public static Method jeiProxyCommonClientReloadItemList = null;

    public static boolean jei = false;

    public static void init()
    {
        if (Loader.isModLoaded("jei"))
        {
            jei = true;
            jeiProxyCommonClientReloadItemList = ReflectionTool.getMethod(ReflectionTool.getClassByName("mezz.jei.startup.ProxyCommonClient"), "reloadItemList");
        }
    }

    public static void refreshJEITooltips()
    {
        if (!jei) return;


        ProxyCommonClient proxy = (ProxyCommonClient) JustEnoughItems.getProxy();
        JeiStarter starter = (JeiStarter) ReflectionTool.get(ProxyCommonClient.class, "starter", proxy);
        starter.start((List<IModPlugin>) ReflectionTool.get(ProxyCommonClient.class, "plugins", proxy), (Textures) ReflectionTool.get(ProxyCommonClient.class, "textures", proxy));
    }
}
