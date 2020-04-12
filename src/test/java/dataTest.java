
import org.apache.zookeeper.*;
import org.junit.*;

import java.io.IOException;

public class dataTest {

    ZooKeeper zooKeeper;


    @Before
    public void init() throws IOException {
        String conn = "cvccy.com:2181";
        zooKeeper = new ZooKeeper(conn, 4000, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent.getPath());
            }
        });
    }

    @Test
    public void getData() throws KeeperException,InterruptedException {
        byte[] data = zooKeeper.getData("/hanfeng",false,null);//无监听
        System.out.println(new String(data));
    }

    @Test
    public void getData2() throws KeeperException,InterruptedException,IOException {


        byte[] data = zooKeeper.getData("/hanfeng",true,null);//设置监听

        System.out.println(new String(data));

        Thread.sleep(Long.MAX_VALUE);

    }


}
