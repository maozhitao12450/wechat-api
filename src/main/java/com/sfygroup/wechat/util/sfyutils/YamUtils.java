package com.sfygroup.wechat.util.sfyutils;

import com.alibaba.fastjson.JSON;
import com.sfygroup.wechat.util.file.FileHandler;
import com.sfygroup.wechat.util.file.FileUtils;
import com.sfygroup.wechat.util.sfyutils.listener.ReloadListenr;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * @author 你好
 * TODO 待完善，通过配置类间接生成的bean无法被重加载，待完善
 */
public class YamUtils {

    /**
     * 当前运行环境 dev prod test
     */
    private static String env = "";

    /**
     * 配置文件最后会实体化到该map中
     */
    private static Map<String, Object> properties = new HashMap<>();

    /**
     * 保存所有配置文件路径
     */
    private static Set<String> files = new HashSet<>();

    private static String scanFilePath;

    private static List<ReloadListenr> listeners = new ArrayList<>();

    public YamUtils() {
        scanFilePath = this.getClass().getClassLoader().getResource("").getPath();
        init();
        System.out.println("==================>>YamUtils inited ");
    }

    public YamUtils(String path) {
        scanFilePath = path;
        init();
        System.out.println("==================>>YamUtils inited ");
    }

    /**
     * 获取配置文件
     *
     * @param name
     * @return
     */
    public static Object getProperty(String name) {
        return getProperty(properties, name);
    }

    /**
     * 获取配置文件
     *
     * @param name
     * @return
     */
    public static Object getProperty(Map properties, String name) {
        String[] split = name.split("\\.");
        return getProperty(properties, split, 0);
    }


    /**
     * 获取配置String文件
     *
     * @param name
     * @return
     */
    public static String getStringProperty(String name) {
        Object property = getProperty(name);
        return property == null ? null : property.toString();
    }

    /**
     * 获取配置文件递归
     *
     * @param properties
     * @param split
     * @param i
     * @return
     */
    private static Object getProperty(Object properties, String[] split, int i) {
        if (properties instanceof Map) {
            Map properties1 = (Map) properties;
            if (i == split.length - 1) {
                return properties1.get(split[i]);
            } else {
                return getProperty(properties1.get(split[i]), split, i + 1);
            }
        } else {
            return null;
        }
    }

    /**
     * 初始化扫描配置文件以及导入配置文件
     */
    public static void init() {
        setEnv();
        scanFileAndLoadPropertyFiles();
    }

    private static void setEnv() {
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String inputArgument : inputArguments) {
            if (inputArgument.contains("sfy.profiles")) {
                //获取环境参数
                env = inputArgument.split("=")[1];
                break;
            }
        }
    }

    /**
     * 扫描配置文件并加载
     */
    public static void scanFileAndLoadPropertyFiles() {
        FileUtils fileUtils = new FileUtils();
        try {
            fileUtils.traverseFolder2(scanFilePath, new FileHandler() {
                @Override
                public void handlerFile(File file2) throws FileNotFoundException {
                    String name = file2.getName();
                    if (isEnvFile(name)) {
                        //说明是配置文件
                        loadYamlFile(file2);
                    }
                }
            });

            //配置文件导入完成
            System.out.println("配置文件加载完成" + JSON.toJSONString(getProperties()));

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isEnvFile(String name) {
        // 当env 不存在时，只认不含 - 的配置文件，
        // 当env 存在时，识别所有满足 条件的文件
        return (name.endsWith(env + ".yml") || name.endsWith(env + ".yaml")) && (env.length() != 0 || !name.contains("-"));
    }

    /**
     * 加载配置文件
     *
     * @param file
     * @throws FileNotFoundException
     */
    public static void loadYamlFile(File file) throws FileNotFoundException {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            Map map = new Yaml().loadAs(reader, Map.class);
            if (map == null){
                System.out.println("==================>> load yaml failure , empty yaml");
                return;
            }
            Set set = map.keySet();
            for (Object o : set) {
                String[] split = o.toString().split("\\.");
                if (split.length > 1) {
                    //说明有 xxx.xxx.xx的格式，拆成xxx: xxx: xx: 格式
                    setMap(split, 0, map, map.get(o));
                }
            }
            files.add(file.getAbsolutePath());
            System.out.println("==================>> load yaml success :" + file.getAbsolutePath());
            getProperties().putAll(map);

            for (ReloadListenr listenr : listeners) {
                listenr.afterPropertyLoaded(file, map, getProperties());
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("close reader fail : " + file.getAbsolutePath());
                }
            }
        }
    }


    /**
     * 批量加载配置文件
     *
     * @throws FileNotFoundException
     */
    public static void loadAllFiles(Set<String> files) throws FileNotFoundException {
        for (String file : files) {
            loadYamlFile(new File(file));
        }
    }

    /**
     * 重加载所有配置文件
     *
     * @throws FileNotFoundException
     */
    public static void reloadFiles() throws FileNotFoundException {
        loadAllFiles(files);
        //重加载配置完成
        System.out.println("配置文件重加载完成" + JSON.toJSONString(getProperties()));
    }

    public static Map<String, Object> getProperties() {
        return properties;
    }

    private static Object setMap(String[] split, int i, Map map, Object o) {
        String name = split[i];
        if (split.length == i + 1) {
            return map.put(name, o);
        } else {
            HashMap<String, Object> hashMap = new HashMap<>();
            map.put(name, hashMap);
            setMap(split, i + 1, hashMap, o);
            return hashMap;
        }
    }

    public static void addListenr(ReloadListenr listenr) {
        listeners.add(listenr);
    }

    public static String getEnv() {
        return env;
    }
}














