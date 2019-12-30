package o.horbenko.nnettysample;

public class Main {

    public static void main(String[] args) {
        NettyServerBootstrap bootstrap = new NettyServerBootstrap(8081);
        bootstrap.runServer();
    }
}
