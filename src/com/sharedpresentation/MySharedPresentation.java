package com.sharedpresentation;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**suspect
 * Created by Admin on 08.06.14.
 */
@SuppressWarnings("unused")
@ServerEndpoint(value = "/sharedpresentation")
public class MySharedPresentation {
    private volatile static int classCounter = 1;
    private volatile static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    private volatile static Map<Session, MySharedPresentation> clientsMap = new ConcurrentHashMap<Session, MySharedPresentation>();
    private volatile static Queue<String> textMessagesQueue = new LinkedList<String>();

    private volatile static TimerGraphicMsgSender timerGraphicMsgSender = new TimerGraphicMsgSender();
    private volatile static TimerTextMsgSender timerTextMsgSender = new TimerTextMsgSender();
    private volatile static Thread timerThread = new Thread(timerGraphicMsgSender);

    private volatile boolean allBinaryDataSend = false;
    private volatile boolean allTextDataSend = false;

    //TODO:Need to create static volatile Queue or thread save list (order is important!) for String messages
    //and for ByteBuffer messages. Each class will add his received data to this collections
    //and thread timer will send data to all other clients(peers) and delete each record from collection
    //separately after sending!

    public MySharedPresentation() {
        System.out.println("Create new server endpoint class-" + classCounter++);
        if (!timerThread.isAlive()){
            timerThread.setDaemon(true);
            timerThread.start();
        }
        else
            System.out.println("Timer has already been started");
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
//        textMessagesQueue.offer(message);
        sendToAll(message);
    }

    @OnMessage
    public void onMessage(ByteBuffer message) {
        System.out.println("binary, " + message.array().length + " type=" + message);
    }

    public void sendToAll(String message) {
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

    public static Queue<String> getTextMessagesQueue() {
        return textMessagesQueue;
    }
}