package com.sharedpresentation;

import com.sharedpresentation.commons.ExceptionUtils;
import org.apache.log4j.Logger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * suspect
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
    private volatile static Thread graphicMsgTimerThread = new Thread(timerGraphicMsgSender);
    private volatile static Thread textMsgTimerThread = new Thread(timerTextMsgSender);
    private static final Logger logger = Logger.getLogger(MySharedPresentation.class);

    private volatile boolean allBinaryDataSend = false;
    private volatile boolean allTextDataSend = false;

    private GraphicFileUtils graphicFileUtils = new GraphicFileUtils();

    //TODO:add flag, that we will set to "true" when we want to stop all threads!

    static {
        if (!graphicMsgTimerThread.isAlive()) {
            graphicMsgTimerThread.start();
        } else
            logger.info("Timer (graphic msg) has already been started");

        if (!textMsgTimerThread.isAlive()) {
            textMsgTimerThread.start();
        } else
            logger.info("Timer (text msg) has already been started");
    }

    public MySharedPresentation() {
        logger.info("Create new server endpoint class-" + classCounter++);
    }

    @OnOpen
    public void onOpen(Session peer) {
        //May be Integer.MAX_VALUE
        peer.setMaxBinaryMessageBufferSize(1000000);
        logger.info("New peer added");
        peers.add(peer);
        clientsMap.put(peer, this);
        logger.info(peers.size() + " active peers");

        sendCurrentPicture(peer);
    }

    @OnMessage
    public void onMessage(String message) {
        logger.info("string, " + message.length());
        textMessagesQueue.offer(message);
    }

    @OnMessage
    public void onMessage(ByteBuffer message) {
        logger.info("binary, " + message.array().length + " type=" + message);
    }

    @OnClose
    public void onClose(Session peer) {
        synchronized (this) {
            try {
                if (peers.contains(peer))
                    peers.remove(peer);
                if (clientsMap.containsKey(peer))
                    clientsMap.remove(peer);
                peer.close();
            } catch (Exception e) {
                logger.error(ExceptionUtils.createExceptionMessage(e));
            }
            logger.info("Peer closed. Only " + peers.size() + " connections still exist.");
        }
    }

    private void sendCurrentPicture(Session peer) {
        try {
            boolean sendResult = WSUtils.sendStringMessage(peer, graphicFileUtils.getGraphicFileBase64Representation());
            while (!sendResult && peer != null && peer.isOpen()) {
                sendResult = WSUtils.sendStringMessage(peer, graphicFileUtils.getGraphicFileBase64Representation());
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.createExceptionMessage(e));
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