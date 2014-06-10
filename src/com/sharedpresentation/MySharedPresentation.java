package com.sharedpresentation;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Admin on 08.06.14.
 */
@SuppressWarnings("unused")
@ServerEndpoint(value = "/sharedpresentation")
public class MySharedPresentation {
    private volatile static int classCounter = 1;
    private volatile static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    private volatile static Map<Session, MySharedPresentation> clientsMap = new ConcurrentHashMap<Session, MySharedPresentation>();

//    private volatile static TimerDate timer = new TimerDate();
//    private volatile static Thread timerThread = new Thread(timer);

    private volatile boolean allBinaryDataSend = false;
    private volatile boolean allTextDataSend = false;

    //TODO:Need to create static volatile Queu or thread save list (order is important!) for String messages
    //and for ByteBuffer messages. Each class will add his received data to this collections
    //and thread timer will send data to all other clients(peers) and delete each record from collection
    //separately after sending!

    public MySharedPresentation() {
        System.out.println("Create new server endpoint class-" + classCounter++);
//        timerThread.setDaemon(true);
//        if (!timerThread.isAlive())
//            timerThread.start();
    }

    @OnOpen
    public void onOpen(Session peer) {
        //May be Integer.MAX_VALUE
        peer.setMaxBinaryMessageBufferSize(1000000);
        System.out.println("New peer added");
        peers.add(peer);
        clientsMap.put(peer, this);
        System.out.println(peers.size() + " active peers");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("string, " + message.length());
        sendToAll(message);
    }

    @OnMessage
    public void onMessage(ByteBuffer message) {
        System.out.println("binary, " + message.array().length + " type=" + message);
        sendToAll(message);
    }

    private void sendToAll(String message) {
        allTextDataSend = false;
        try {
            Iterator<Session> sIterator = peers.iterator();
            while (sIterator.hasNext()) {
                synchronized (this) {
                    Session s = sIterator.next();
                    if (s != null && s.isOpen())
                        s.getBasicRemote().sendText(message);
                }
            }
            allTextDataSend = true;
        } catch (Exception e) {
            allTextDataSend = true;
            e.printStackTrace();
        }
    }

    public void sendToAll(ByteBuffer b) {
//        if (b != null) {
//            double[] bArr = b.asDoubleBuffer().array();
//            for (int i = 0; i < bArr.length; i++) {
//                System.out.print(bArr[i]+" ");
//            }
//            System.out.println();
//        }
        allBinaryDataSend = false;
        try {
            Iterator<Session> sIterator = peers.iterator();
            while (sIterator.hasNext()) {
                synchronized (this) {
                    Session s = sIterator.next();
                    if (s != null && s.isOpen())
                        s.getBasicRemote().sendBinary(b);
                }
            }
            allBinaryDataSend = true;
        } catch (Exception e) {
            allBinaryDataSend = true;
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session peer) {
        synchronized (this) {
            if (peers.contains(peer))
                peers.remove(peer);
            if (clientsMap.containsKey(peer))
                clientsMap.remove(peer);
            System.out.println("Peer closed. Only " + peers.size() + " connections still exist.");
        }
    }

    public boolean isAllBinaryDataSend() {
        return allBinaryDataSend;
    }

    public boolean isAllTextDataSend() {
        return allTextDataSend;
    }

    public static Map<Session, MySharedPresentation> getClientsMap() {
        return clientsMap;
    }
}