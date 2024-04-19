package com.example.setcardgame.config;

import android.util.Log;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class WebSocketClient {
    public static StompClient mStompClient;
    public static CompositeDisposable compositeDisposable;
    private static final String TAG = "WebSocketClient";

    private WebSocketClient() {
    }

    public static void createWebSocket(String websocketConnectUrl) {
        compositeDisposable = new CompositeDisposable();
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, websocketConnectUrl);
        Disposable lifecycle = mStompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d(TAG, "Stomp connection opened");
                    break;
                case ERROR:
                    Log.e(TAG, "Error: ", lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.d(TAG, "Stomp connection closed");
                    break;
                case FAILED_SERVER_HEARTBEAT:
                    Log.d(TAG, "Failed server heartbeat");
                    break;
            }
        });
        if (!mStompClient.isConnected()) {
            mStompClient.connect();
        }
        compositeDisposable.add(lifecycle);
        mStompClient.withClientHeartbeat(15000);
    }

    public static void disconnectWebSocket() {
        if (mStompClient != null && (mStompClient.isConnected())) {
            mStompClient.disconnect();
            compositeDisposable.dispose();
            Log.d(TAG, "Disconnected");
        }
    }
}