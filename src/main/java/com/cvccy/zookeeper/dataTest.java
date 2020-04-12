package com.cvccy.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class dataTest {

    ZooKeeper zooKeeper;


    @Before
    public void init() throws IOException {
        String conn = "cvccy.com:2181";
        zooKeeper = new ZooKeeper(conn, 20000, new Watcher() {
            @Override
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
    public void getData2() throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData("/hanfeng", true, null);
        System.out.println(new String(data));
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void getData21() throws KeeperException,InterruptedException {
        byte[] data = zooKeeper.getData("/fuyang",true,null);//设置监听
        System.out.println(new String(data));
        Thread.sleep(Long.MAX_VALUE);
        //byte[] data = zooKeeper.getData("/fuyang2",true,null);//设置监听
        /**
         * 同时存在两个获取数据，就不能知道是那一条出发的监听，
         * 可以使用自定义监听getData3()
         */
    }

    @Test
    public void getData3() throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        zooKeeper.getData("/hanfeng", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    zooKeeper.getData(event.getPath(), this, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(event.getPath());
            }
        }, stat);
        System.out.println(stat);
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void getData4() throws KeeperException, InterruptedException {
        zooKeeper.getData("/tuling", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println(stat);
            }
        }, "");
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void getChild() throws KeeperException,InterruptedException{
        List<String> children = zooKeeper.getChildren("/fuyang",event -> {
            System.out.println(event.getPath());
            try {
                zooKeeper.getChildren(event.getPath(),false);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        children.stream().forEach(System.out::println);
        Thread.sleep(Long.MAX_VALUE);

    }


    @Test
    public void createData() throws KeeperException, InterruptedException {
        List<ACL> list = new ArrayList<>();
        int perm = ZooDefs.Perms.ADMIN | ZooDefs.Perms.READ;//cdwra

        /**
         * 数字位移
         * int c = 1<<0
         * int d = 1<<1
         * int w = 1<<2
         * int r = 1<<3
         * int a = 1<<4
         *
         *
         * R 00000001 << 0=1
         * W 00000010 << 1=2
         * C 00000100 << 2=4
         * D 00001000 << 3=8
         * A 00010000 << 4=16
         *
         *
         * R|W=00000011 == 3
         *
         * rwcda = 00011111=31
         *
         */
        ACL acl = new ACL(perm, new Id("world", "anyone"));
        ACL acl2 = new ACL(perm, new Id("ip", "192.168.0.149"));
        ACL acl3 = new ACL(perm, new Id("ip", "127.0.0.1"));
        list.add(acl);//可添加多个，满足任意一个即可，序列化
        list.add(acl2);
        list.add(acl3);
        zooKeeper.create("/fuyang/lu", "hello".getBytes(), list, CreateMode.PERSISTENT);
    }

    /**
     * [zk: localhost:2181(CONNECTED) 9] ls /fuyang
     * [lu, taihe]
     * [zk: localhost:2181(CONNECTED) 10] getAcl /fuyang/lu
     * 'world,'anyone
     * : ra
     * 'ip,'192.168.0.149
     * : ra
     * 'ip,'127.0.0.1
     * : ra
     * [zk: localhost:2181(CONNECTED) 11] set /fuyang/lu "hello"
     * Authentication is not valid : /fuyang/lu
     */


}
