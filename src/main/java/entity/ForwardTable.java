package entity;

import utils.MyHashMap;

import java.io.Serializable;

public class ForwardTable implements Serializable {
    private char hostName;
    private String portNum;
    public MyHashMap<Character, String> hostMap = new MyHashMap<>();

    public void updateHostMap(char hostName, String portNum) {
        this.hostMap.put(hostName, portNum);
    }

    public ForwardTable(char hostName, String portNum) {
        this.hostName = hostName;
        this.portNum = portNum;
    }
    public ForwardTable() {
    }

    public char getHostName() {
        return hostName;
    }
    public void setHostName(char hostName) {
        this.hostName = hostName;
    }
    public String getPortNum() {
        return portNum;
    }
    public void setPortNum(String portNum) {
        this.portNum = portNum;
    }

    @Override
    public String toString() {
        return "转发表: \n站\t网段\n" +  hostMap;
    }
}
