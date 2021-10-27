package server;

public class Server {

    // todo rehash计算负载因子时可能会触发并发问题
    public static boolean isInBgSaveOrBgRewriteAOFCmd() {
        return false;
    }

}
