package entity;

public class Frame {
    private char resHost;
    private char desHost;

    public Frame(char resHost, char desHost) {
        this.resHost = resHost;
        this.desHost = desHost;
    }

    public Frame() {
    }
    public char getDesHost() {
        return desHost;
    }
    public void setDesHost(char desHost) {
        this.desHost = desHost;
    }
    public char getResHost() {
        return resHost;
    }
    public void setResHost(char resHost) {
        this.resHost = resHost;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "resHost='" + resHost + '\'' +
                ", desHost='" + desHost + '\'' +
                '}';
    }
}
