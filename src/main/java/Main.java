import entity.ForwardTable;
import entity.Frame;
import utils.CloneUtils;
import utils.MyHashMap;

import java.util.*;

/**
 * @author chchuECNU
 */
public class Main {
    /**
     * 用于分配站：'@' + 1 => 站：'A'
     */
    public static char hostName = '@';
    /**
     * 站-网段映射
     */
    public static Map<Character, String> hostSegmentMap = new HashMap<>();
    /**
     * 各网段-多个网桥映射
     */
    public static Map<String, List<String>> segmentBridgesMap = new HashMap<>();
    /**
     * 各网桥-多个网段映射
     */
    public static Map<String, List<String>> bridgeSegmentsMap = new HashMap<>();
    /**
     * 网桥列表
     */
    public static List<String> bridge = new ArrayList<>();
    /**
     * 各网桥-转发表映射
     */
    public static MyHashMap<String, ForwardTable> forwardTables = new MyHashMap<>();
    /**
     * 转发前的各网桥-转发表映射
     */
    public static MyHashMap<String, ForwardTable> oldForwardTables = new MyHashMap<>();
    /**
     * 帧
     */
    public static Frame frame;
    /**
     * 供后退用的栈
     */
    public static Stack<MyHashMap<String, ForwardTable>> forwardTableStack = new Stack<>();

    /**
     * 开始程序
     * @param args
     */
    public static void main(String[] args) {
        initNetTopo();
        initForwardTables(bridge);
        simuProcess();
    }

    /**
     * 网络拓扑初始化
     */
    private static void initNetTopo() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>网络拓扑初始化>>>>>>>>>>>>>>>>>>>>>>>");
        Scanner s = new Scanner(System.in);
        System.out.println("【请输入网桥数量】：");
        int bridgeNum = s.nextInt();
        System.out.println();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>需要分配各网桥所管辖的网段>>>>>>>>>>>>>>>>>>>>>>>");
        String[][] segments = new String[bridgeNum][];
        for (int i = 0; i < bridgeNum; i++) {
            int bridgeIndex = i + 1;
            // 初始化网桥
            initBridges(bridgeIndex);
            initSegments(bridgeIndex, segments);
        }
    }
        /**
         * 初始化网桥
         */
        private static void initBridges(int bridgeIndex) {
            bridge.add("b" + bridgeIndex);
        }
        /**
         * 初始化网段
         *
         * @param bridgeIndex
         * @param segments
         */
        private static void initSegments(int bridgeIndex, String[][] segments) {
            int index = bridgeIndex - 1;
            System.out.println("【请输入网桥b" + bridgeIndex + "管辖网段】（多个数字请用<英文逗号>隔开）：");
            Scanner s = new Scanner(System.in);
            String segmentLine = s.next();
            // 从下标0开始存储字符串
            segments[index] = segmentLine.split(",");

            int segmentNum = segments[index].length;
            List<String> segList = new ArrayList<>();
            for (int j = 0; j < segmentNum; j++) {
                String segment = segments[index][j];
                segList.add(segment);
                List<String> bridgeList = null;
                if (!segmentBridgesMap.containsKey(segment)) {
                    // 若之前没有网段记录，就创建
                    bridgeList = new ArrayList<>();
                    bridgeList.add("b" + bridgeIndex);
                    segmentBridgesMap.put(segment, bridgeList);
                    // 并且需要初始化站点
                    initHosts(segment);
                } else {
                    // 否则，更新网段-网桥映射
                    bridgeList = segmentBridgesMap.get(segment);
                    bridgeList.add("b" + bridgeIndex);
                    segmentBridgesMap.put(segment, bridgeList);
                    System.out.println("网段" + segment + "已分配站点，不需要重新分配");
                }
                System.out.println();
            }
            bridgeSegmentsMap.put("b" + bridgeIndex, segList);
        }
        /**
         * 初始化站点
         * @param segment
         */
        private static void initHosts(String segment) {
            System.out.println("网段" + segment + "需要设置站点！");
            System.out.println("【请输入网段" + segment + "的站点数量】：");
            Scanner s = new Scanner(System.in);
            int hostNum = s.nextInt();
            for (int z = 0; z < hostNum; z++) {
                System.out.println(">>>分配站点中。。。");
                hostName = (char) (hostName + 1);
                hostSegmentMap.put(hostName, segment);
                System.out.println(">>>网段" +
                        segment + "有站点" + hostName);
            }
        }

    /**
     * 初始化转发表：建立网桥和转发表的映射
     *
     * @param bridge
     */
    private static void initForwardTables(List<String> bridge) {
        for (int i = 0; i < bridge.size(); i++) {
            ForwardTable forwardTable = new ForwardTable();
            String bridgeI = bridge.get(i);
            forwardTables.put(bridgeI, forwardTable);
        }
    }

    /**
     * 网桥模拟
     */
    private static void simuProcess() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>帧初始化>>>>>>>>>>>>>>>>>>>>>>>");
        while (true) {
            // 初始化帧
            initFrame();
            char resHost = frame.getResHost();
            char desHost = frame.getDesHost();

            // 备份（深拷贝）转发前各网桥的转发表
            oldForwardTables = CloneUtils.clone(forwardTables);
            forwardTableStack.add(oldForwardTables);

            // 网桥模拟
            String forwardSegment = hostSegmentMap.get(resHost);
            List<String> forwardBridges = segmentBridgesMap.get(forwardSegment);
            System.out.println();
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>网桥模拟开始>>>>>>>>>>>>>>>>>>>>>>>");
            // 对同一网段的所有网桥转发该帧
            for (int i = 0; i < forwardBridges.size(); i++) {
                String forwardBridge = forwardBridges.get(i);
                forward(frame, forwardBridge, forwardSegment);
            }
            System.out.println("一轮模拟结束后的转发表：" + "\n" + forwardTables);

            // 转发结束后决定后续功能
            Boolean nextStep = nextFunction();
            // 转发或者结束
            if(nextStep) {
                continue;
            } else {
                return;
            }
        }
    }
        /**
         * 设置帧的源站和目的站
         */
        private static void initFrame() {
            Scanner s = new Scanner(System.in);
            System.out.println("【请输入源站】（大写英文字母）：");
            String res = s.next();
            char resHost = res.charAt(0);
            System.out.println("【请输入目的站】（大写英文字母）：");
            String des = s.next();
            char desHost = des.charAt(0);
            frame = new Frame(resHost, desHost);
            System.out.println("该帧的源站为" + resHost + "，目的站为" + desHost);
        }
        /**
         * 转发、丢弃、洪泛
         *
         * @param frame
         */
        private static void forward(Frame frame, String bridge, String arriSegment) {
            System.out.println(">>>帧进入网桥" + bridge + "，进入网段为" + arriSegment);
            // 进入网桥，获取转发表
            ForwardTable forwardTable = forwardTables.get(bridge);
            // 获取转发表的记录
            Map<Character, String> hostMap = forwardTable.hostMap;
            char desHost = frame.getDesHost();
            if (hostMap.containsKey(desHost)) {
                String recordSegment = hostMap.get(desHost);
                if (recordSegment.equals(arriSegment)) {
                    System.out.println("网桥" + bridge + "的网段" + arriSegment + "：因为转发表记录网段与进入网段一致，丢弃此帧。");
                } else {
                    System.out.println("转发表有" + desHost + "站的记录网段"+"，网桥" + bridge + "向网段" + recordSegment + "转发。");
                    List<String> bridges = segmentBridgesMap.get(recordSegment);
                    for (int i = 0; i < bridges.size(); i++) {
                        String changedBridge = bridges.get(i);
                        if (changedBridge.equals(bridge)) {
                            continue;
                        } else {
                            // 网段为记录网段，帧不变
                            forward(frame, changedBridge, recordSegment);
                        }
                    }
                }
            } else {
                System.out.println("转发表没有" + desHost + "站的记录网段，网桥" + bridge + "需要使用泛洪算法广播该帧");
                floodAlgo(frame, bridge, arriSegment);
            }

            char resHost = frame.getResHost();
            if (!hostMap.containsKey(resHost)) {
                System.out.println("源站" + resHost + "不在" + bridge + "的转发表中，需要在转发表添加记录：站" + resHost + "，网段" + arriSegment);
                System.out.println(">>>添加中");
                forwardTable.updateHostMap(resHost, arriSegment);
                forwardTables.replace(bridge, forwardTable);
            }
        }
            /**
             * 泛洪算法：向该网桥（除进入网段外）所有网段广播该帧
             *
             * @param frame
             * @param bridge
             * @param arrSegment
             */
            private static void floodAlgo(Frame frame, String bridge, String arrSegment) {
                System.out.println(">>>广播该帧");
                List<String> segments = bridgeSegmentsMap.get(bridge);
                for (int i = 0; i < segments.size(); i++) {
                    String changedSegment = segments.get(i);
                    if (changedSegment.equals(arrSegment)) {
                        continue;
                    } else {
                        List<String> bridges = segmentBridgesMap.get(changedSegment);
                        for (int j = 0; j < bridges.size(); j++) {
                            String changedBridge = bridges.get(j);
                            if (changedBridge.equals(bridge)) {
                                continue;
                            } else {
                                forward(frame, changedBridge, changedSegment);
                            }
                        }
                    }
                }
            }
        /**
         * 转发结束后的功能：结束、继续转发、后退
         * @return
         */
        private static boolean nextFunction() {
            while (true) {
                System.out.println();
                System.out.println("【请决定下一步操作】（1. 继续转发[C]；2. 后退[B]；3. 结束[E]）：");
                Scanner s = new Scanner(System.in);
                String isContinue = s.next();
                if ("E".equals(isContinue)) {
                    System.out.println("网桥模拟结束");
                    return false;
                } else if ("B".equals(isContinue)) {
                    if (forwardTableStack.empty()) {
                        System.out.println("转发表已无记录，无法后退！");
                    } else {
                        forwardTables = CloneUtils.clone(forwardTableStack.pop());
                        System.out.println("后退成功！转发表为：" + forwardTables);
                    }
                } else if ("C".equals(isContinue)) {
                    System.out.println("继续转发！");
                    return true;
                }
            }
        }

}
