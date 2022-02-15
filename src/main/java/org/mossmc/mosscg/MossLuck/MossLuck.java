package org.mossmc.mosscg.MossLuck;

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

import static org.mossmc.mosscg.MossMirai.Plugin.API.PluginLogger.*;
import static org.mossmc.mosscg.MossMirai.Plugin.API.PluginCommand.*;
import static org.mossmc.mosscg.MossMirai.Plugin.API.PluginConfig.*;

import static org.mossmc.mosscg.MossLuck.UserLuck.*;

public class MossLuck {

    public static void onEnable() throws Exception{
        sendInfo("欢迎使用MossLuck插件 By 墨守MossCG");
        sendInfo("正在加载配置文件");
        InputStream propertiesStream = MossLuck.class.getClassLoader().getResourceAsStream("plugin.properties");
        properties = new Properties();
        properties.load(propertiesStream);
        pluginName = properties.getProperty("pluginName");
        saveDefaultConfig(pluginName);
        loadDefaultConfig(pluginName);
        getConfig = getDefaultConfig(pluginName);
        luckMessage = getConfig.get("luckMessage");
        luckMax = Integer.valueOf(getConfig.get("luckMax"));
        luckMin = Integer.valueOf(getConfig.get("luckMin"));
        sendInfo("配置文件加载完成");
        sendInfo("正在加载人品记录");
        loadLuck();
        sendInfo("记录人品加载完成");
        sendInfo("正在注册指令");
        loadCommand();
        sendInfo("指令注册完成");
    }

    public static String pluginName;
    public static Properties properties;
    public static Map<String,String> getConfig;

    public static void loadCommand() throws Exception{
        Class<?> methodClass = Class.forName("org.mossmc.mosscg.MossLuck.UserLuck");
        String[] commands = getConfig.get("luckCommand").split("\\|");
        int i = 0;
        while (i<commands.length) {
            registerCommand(commands[i], methodClass.getDeclaredMethod("getLuck", MessageChainBuilder.class, long.class, MessageChain.class));
            i++;
        }
    }

    public static String getNowDate() {
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        int month = rightNow.get(Calendar.MONTH)+1;
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        return year + "-" + month + "-" + day;
    }
}