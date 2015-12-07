package com.sg.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.telnet.TelnetClient;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemCached {
	/*是否启用MemCached内存数据库*/
    protected static boolean enUsed = true;
    
    /*创建全局唯一的可实例化对象*/
    protected static MemCached memCached = new MemCached();
    
    /*初始化MemCached客户端对象*/
    protected static MemCachedClient memClient = new MemCachedClient();
    
    /*定义MemCached服务器运行环境配置文件名称*/
    @SuppressWarnings("unused")
	private static final String MemCachedConfigFile_NAME = "MemCachedConfig.xml";
    
    /*定义可用的MemCached服务器列表，用于分布式存储*/
    private static String[] serverListArr = new String[1];
    
    /*定义各MemCached服务器的负载权重列表，与服务器列表按先后顺序对应*/
    private static Integer[] weightListArr = new Integer[1];;
    
    /*定义MemCached服务器运行环境表，配置文件中关于参数相关数据将保存到该表*/
    private static Map<String, String> serverConfig;
    
    /*定义MemCached服务器运行状态表，用于保存各状态的中文解释*/
    protected static HashMap<String, String> statsItems;

    /*设置全局静态参数，以下代码在整个服务器运行周期内仅运行一次*/
    static {
    	/*初始化MemCached运行环境配置*/
    	/*首先初始化各参数默认值，然后加载配置文件，遍历其中的参数值并进行覆盖*/
        initConfig();

        if(enUsed){ 
        	/*如果已启用memcached缓存服务*/
        	/*获取socke连接池的实例对象*/
            SockIOPool pool = SockIOPool.getInstance();
    
            /*设置可用的MemCached服务器信息，实现分布式存储*/
            pool.setServers(serverListArr);
            
            /*设置各MemCached服务器的负载权重，根据可支配内存实现负载均衡*/
            pool.setWeights(weightListArr);
    
            /*设置初始连接数*/
            pool.setInitConn(Integer.parseInt(serverConfig.get("initConn").toString()));
            
            /*设置最小连接数*/
            pool.setMinConn(Integer.parseInt(serverConfig.get("minConn").toString()));
            
            /*设置最大连接数*/
            pool.setMaxConn(Integer.parseInt(serverConfig.get("maxConn").toString()));
            
            /*设置连接最大空闲时间*/
            pool.setMaxIdle(Long.parseLong(serverConfig.get("maxIdle").toString()));
            
            /*设置主线程的睡眠时间，每隔该时间维护一次各连接线程状态*/
            pool.setMaintSleep(Long.parseLong(serverConfig.get("maintSleep").toString()));
    
            /*关闭nagle算法*/
            pool.setNagle(false);
            
            /*读取操作的超时限制*/
            pool.setSocketTO(Integer.parseInt(serverConfig.get("socketTO").toString()));
            
            /*连接操作的超时限制，0为不限制*/
            pool.setSocketConnectTO(Integer.parseInt(serverConfig.get("socketConnTO").toString()));
    
            /*初始化连接池*/
            pool.initialize();
            
            /*压缩设置，超过指定大小的数据都会被压缩*/
            /*从java_memcached-release_2.6.1开始已经不再支持内置的数据压缩功能*/
            
            /*memClient.setCompressEnable(Boolean.parseBoolean(serverConfig.get("compressEnable").toString()));
            memClient.setCompressThreshold(Long.parseLong(serverConfig.get("compressThreshold").toString()));*/
        }
    }
    
    /**
     * @category 初始化MemCached运行环境配置
     * @category 注：该方法在整个服务器周期内仅运行一次
     */
    @SuppressWarnings({ "unchecked", "serial" })
	protected static void initConfig(){
        
    	/*初始化可用的MemCached服务器列表默认值（本机）*/
        serverListArr[0] = "127.0.0.1:11211";
        weightListArr[0] = 1;
        
        /*初始化MemCached服务器运行环境表（默认值），当某参数未在配置文件中进行定义时，将使用该默认值*/
        serverConfig = new HashMap<String, String>(){
            private static final long serialVersionUID = 1L;
            {
            	/*设置初始连接数*/
                put("initConn", "5");
                /*设置最小连接数*/
                put("minConn", "5");
                /*设置最大连接数*/
                put("maxConn", "250");
                /*设置连接最大空闲时间（6小时）*/
                put("maxIdle", "21600000");
                /*设置主线程的睡眠时间（30秒）*/
                put("maintSleep", "30");
                /*读取操作的超时限制（10秒）*/
                put("socketTO", "10000");
                /*连接操作的超时限制（不限制）*/
                put("socketConnTO", "0");
                /*是否启用自动压缩（启用）*/
                put("compressEnable", "true");
                /*超过指定大小的数据都会被压缩（64K）*/
                put("compressThreshold", "65536");
            }
        };

        /*开始读取配置文件，并将其中的参数值向默认环境表中进行覆盖*/
        String filePath = Thread.currentThread().getContextClassLoader().getResource("MemCachedConfig.xml").getPath().substring(1);
        File file = new File(filePath.replaceAll("%20"," "));
        try{
        	/*如果可以成功加载配置文件*/
            if(file.exists()){
                SAXReader sr = new SAXReader();
                Document doc = sr.read(file);
                /*获得根节点*/
                Element Root = doc.getRootElement();
                /*获得是否启用memcached节点*/
                Element Enabled = (Element)Root.selectSingleNode("Enabled");
                /*获得可用的服务器列表父节点*/
                Element Servers = (Element)Root.selectSingleNode("Servers");
                /*获得运行环境参数列表父节点*/
                Element Config = (Element)Root.selectSingleNode("Config");
                /*是否启用memcached缓存服务*/
                enUsed = Boolean.parseBoolean(Enabled.getText());
                /*备用的服务器列表*/
                List<Element> serverDoms = Servers.elements();
                /*经检测，实际可用的服务器列表*/
                List<Element> serverUsed = new ArrayList<Element>();
                /*初始化Telnet对象，用来检测服务器是否可以成功连接*/
                TelnetClient telnet = new TelnetClient();
                /*连接超时：5秒*/
                telnet.setConnectTimeout(5000);
                for(Element serverTmp : serverDoms){
                    try{
                    	/*连接到服务器*/
                        telnet.connect(serverTmp.attributeValue("host"), Integer.parseInt(serverTmp.attributeValue("post")));
                        /*断开连接*/
                        telnet.disconnect();
                        /*连接成功，将服务器添加到实际可用列表*/
                        serverUsed.add(serverTmp);
                    }catch(Exception e){}
                }
                /*经检测，实际可用的服务器个数*/
                int serverCount = serverUsed.size();
                /*没有发现实际可用的服务器，返回*/
                if(serverCount == 0){
                    enUsed = false;
                    return;
                }
                /*初始化服务器地址及端口号数组*/
                serverListArr = new String[serverCount];
                /*初始化服务器负载权重数组*/
                weightListArr = new Integer[serverCount];
                /*向服务器数组进行赋值*/
                for(int ind=0; ind < serverCount; ind++){
                    serverListArr[ind] = serverUsed.get(ind).attributeValue("host") + ":" + serverUsed.get(ind).attributeValue("post");
                    weightListArr[ind] = Integer.parseInt(serverUsed.get(ind).attributeValue("weight").toString());
                }
                /*返回服务器运行环境参数列表，用于遍历配置文件*/
                Object[] serverConfigArr = serverConfig.keySet().toArray();
                for(Object cfgItem : serverConfigArr){
                	/*查找指定的参数节点*/
                    Node node = Config.selectSingleNode("//property[@name='" + cfgItem + "']");
                    /*如果该参数节点不存在，则继续查找下一个参数，该参数将采用默认值*/
                    if(node == null) continue;
                    Element configNode = (Element)node;
                    /*添加配置文件中定义的参数值*/
                    serverConfig.put(cfgItem.toString(), configNode.getTextTrim());
                }
            }
        }catch(Exception e){
            System.out.println(e.toString());
        }

        /*初始化MemCached服务器运行状态表，对各状态进行中文解释*/
        statsItems = new HashMap<String, String>(){
            {
            	put("pid", "MemCached服务进程ID");
                put("version", "MemCached服务版本");
                put("pointer_size", "MemCached服务器架构");
                put("time", "服务器当前时间");
                put("uptime", "服务器本次启动以来，总共运行时间");
                put("connection_structures", "服务器分配的连接结构数");
                put("total_connections", "服务器本次启动以来，累计响应连接总次数");
                put("curr_connections", "当前打开的连接数");
                put("limit_maxbytes", "允许服务支配的最大内存容量");
                put("bytes", "当前已使用的内存容量");
                put("bytes_written", "服务器本次启动以来，写入的数据量");
                put("bytes_read", "服务器本次启动以来，读取的数据量");
                put("total_items", "服务器本次启动以来，曾存储的Item总个数");
                put("curr_items", "当前存储的Item个数");
                put("cmd_get", "服务器本次启动以来，执行Get命令总次数");
                put("get_hits", "服务器本次启动以来，Get操作的命中次数");
                put("get_misses", "服务器本次启动以来，Get操作的未命中次数");
                put("cmd_set", "服务器本次启动以来，执行Set命令总次数");
            }
        };
    }

    /**
     * @category 保护型构造方法，不允许实例化！
     */
    protected MemCached() {

    }

    /**
     * @category 操作类入口：获取唯一实例.
     * 
     * @return MemCached对象
     */
    public static MemCached getInstance() {
        return memCached;
    }
    
    /**
     * @category 返回是否已经启用memcached内存服务器
     * 
     * @return boolean
     */
    public static boolean used(){
        return enUsed;
    }

    /**
     * @category 插入新记录.
     * @category 前提：记录的Key在缓存中不存在
     * @param key 记录的主键
     * @param value 记录的内容
     * @return boolean 操作结果
     */
    public boolean add(String key, Object value) {
        if(!enUsed){
            return false;
        }else{
            return memClient.add(key, value);
        }
    }

    /**
     * @category 插入新记录并设置超时日期
     * @category 前提：记录的Key在缓存中不存在
     * @param key 记录的主键
     * @param value 记录的内容
     * @param expiryDate 超时日期
     * @return boolean 操作结果
     */
    public boolean add(String key, Object value, Date expiryDate) {
        if(!enUsed){
            return false;
        }else{
            return memClient.add(key, value, expiryDate);
        }
    }

    /**
     * @category 插入新记录并设置超时天数
     * @category 前提：记录的Key在缓存中不存在
     * @param key 记录的主键
     * @param value 记录的内容
     * @param expiryDays 超时天数
     * @return boolean 操作结果
     */
    public boolean add(String key, Object value, int expiryDays){
        if(!enUsed){
            return false;
        }else{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE,expiryDays); //��������
            return memClient.add(key, value, calendar.getTime());
        }
    }
    
    /**
     * @category 插入新记录或更新已有记录
     * @category 解释：记录的Key在缓存中不存在则插入；否则更新
     * @param key 记录的主键
     * @param value 记录的内容
     * @return boolean 操作结果
     */
    public boolean set(String key, Object value){
        if(!enUsed){
            return false;
        }else{
            return memClient.set(key, value);
        }
    }

    /**
     * @category 插入新记录或更新已有记录，并设置超时日期
     * @category 解释：记录的Key在缓存中不存在则插入；否则更新
     * @param key 记录的主键
     * @param value 记录的内容
     * @param expiryDate 超时日期
     * @return boolean 操作结果
     */
    public boolean set(String key, Object value, Date expiryDate){
        if(!enUsed){
            return false;
        }else{
            return memClient.set(key, value, expiryDate);
        }
    }

    /**
     * @category 插入新记录或更新已有记录，并设置超时天数
     * @category 解释：记录的Key在缓存中不存在则插入；否则更新
     * @param key 记录的主键
     * @param value 记录的内容
     * @param expiryDate 超时天数
     * @return boolean 操作结果
     */
    public boolean set(String key, Object value, int expiryDays){
        if(!enUsed){
            return false;
        }else{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE,expiryDays); //��������
            return memClient.set(key, value, calendar.getTime());
        }
    }

    /**
     * @category 更新已有记录
     * @category 前提：记录的Key在缓存中已经存在
     * @param key 记录的主键
     * @param value 记录的内容
     * @return boolean 操作结果
     */
    public boolean replace(String key, Object value) {
        if(!enUsed){
            return false;
        }else{
            return memClient.replace(key, value);
        }
    }

    /**
     * @category 更新已有记录，并设置超时日期
     * @category 前提：该值在缓存中已经存在
     * @param key 记录的主键
     * @param value 记录的内容
     * @param expiryDate 超时日期
     * @return boolean 操作结果
     */
    public boolean replace(String key, Object value, Date expiryDate) {
        if(!enUsed){
            return false;
        }else{
            return memClient.replace(key, value, expiryDate);
        }
    }

    /**
     * @category 更新已有记录，并设置超时天数
     * @category 前提：该值在缓存中已经存在
     * @param key 记录的主键
     * @param value 记录的内容
     * @param expiryDays 超时天数
     * @return boolean 操作结果
     */
    public boolean replace(String key, Object value, int expiryDays) {
        if(!enUsed){
            return false;
        }else{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE,expiryDays); //��������
            return memClient.replace(key, value, calendar.getTime());
        }
    }

    /**
     * @category 返回单条记录
     * 
     * @param key 记录的主键
     * @return 记录的内容
     */
    public Object get(String key) {
        if(!enUsed){
            return null;
        }else{
            return memClient.get(key);
        }
    }

    /**
     * @category 返回多条记录
     * 
     * @param keys 记录的主键数组
     * @return Map<String, Object> 多条记录的内容
     */
    public Map<String, Object> get(String[] keys) {
        if(!enUsed){
            return null;
        }else{
            return memClient.getMulti(keys);
        }
    }
    
    /**
     * @category 删除记录
     * @category 执行该方法之后，使用stats的统计结果会同步更新
     * @param key 记录的主键
     * @return 操作结果
     */
    public boolean delete(String key){
        if(!enUsed){
            return false;
        }else{
            return memClient.delete(key);
        }
    }
}
