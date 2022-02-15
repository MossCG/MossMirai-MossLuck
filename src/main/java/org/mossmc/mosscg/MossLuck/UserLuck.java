package org.mossmc.mosscg.MossLuck;

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static org.mossmc.mosscg.MossLuck.MossLuck.*;
import static org.mossmc.mosscg.MossMirai.Plugin.API.PluginConfig.getPluginConfigDir;
import static org.mossmc.mosscg.MossMirai.Plugin.API.PluginLogger.*;

public class UserLuck {
    public static Map<Long,Integer> cacheLuckMap = new HashMap<>();

    public static String luckMessage;
    public static String fileDate;

    public static Integer luckMax;
    public static Integer luckMin;

    public static Random random = new Random();

    public static FileWriter getWriter;

    public static Integer getRandomInt(int max,int min) {
        return random.nextInt(max-min+1)+min;
    }

    public static void getLuck(MessageChainBuilder chain, long number, MessageChain message) {
        if (!getNowDate().equals(fileDate)) {
            updateLuckDate();
        }
        if (cacheLuckMap.containsKey(number)) {
            chain.append(luckMessage.replace("{rp}",String.valueOf(cacheLuckMap.get(number))));
        } else {
            int luck = getRandomInt(luckMax,luckMin);
            chain.append(luckMessage.replace("{rp}",String.valueOf(luck)));
            writerInput(number,luck);
        }
    }

    public static void updateLuckDate() {
        sendInfo("日期已更新！今日人品缓存刷新中！");
        cacheLuckMap.clear();
        sendInfo("已更新今日人品缓存");
        writerUpdate();
        sendInfo("已更新今日人品存储");
        sendInfo("今日人品模块日期更新完毕！");
    }

    public static void loadLuck() throws Exception{
        File cacheDir = new File(getPluginConfigDir(pluginName)+"/luckSave");
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdir()) {
                sendError("无法创建人品存储文件夹！");
            }
        }
        String date = getNowDate();
        File cacheFile = new File(getPluginConfigDir(pluginName)+"/luckSave/"+date+".yml");
        getWriter = new FileWriter(cacheFile,true);
        if (cacheFile.exists()) {
            Yaml yaml = new Yaml();
            FileInputStream input;
            input = new FileInputStream(cacheFile);
            Map<?,?> luckCache = yaml.loadAs(input, Map.class);
            if (luckCache != null) {
                Iterator<?> iterator = luckCache.keySet().iterator();
                sendInfo("正在读取人品保存文件");
                Object user;
                long userNumber;
                int luckNumber;
                while (iterator.hasNext()) {
                    user = iterator.next();
                    userNumber = Long.parseLong(user.toString());
                    luckNumber = Integer.parseInt(luckCache.get(user).toString());
                    cacheLuckMap.put(userNumber, luckNumber);
                }
                luckCache.clear();
            }
            sendInfo("用户今日人品缓存读取完成");
        }
        fileDate = getNowDate();
    }

    public static void writerUpdate() {
        String date = null;
        try {
            date = getNowDate();
            if (getWriter != null) {
                getWriter.close();
            }
            String targetFilePath = getPluginConfigDir(pluginName)+"/luckSave/"+date+".yml";
            getWriter = new FileWriter(targetFilePath,true);
        } catch (IOException e) {
            sendException(e);
            sendWarn("今日人品模块写入功能初始化失败！");
        } finally {
            fileDate = date;
        }
    }

    public static void writerInput(Long number,int userLuck) {
        try {
            cacheLuckMap.put(number,userLuck);
            getWriter.write("\r\n"+number+": "+userLuck);
            getWriter.flush();
        } catch (IOException e) {
            sendException(e);
            sendWarn("用户人品缓存写入失败！");
        }
    }
}
