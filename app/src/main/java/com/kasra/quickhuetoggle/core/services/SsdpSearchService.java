package com.kasra.quickhuetoggle.core.services;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.design.widget.Snackbar;
import android.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class SsdpSearchService {
    private OkHttpClient httpClient;

    public SsdpSearchService(OkHttpClient client) {
        this.httpClient = client;
    }

    public void start(Context ctx, PublishSubject<String> result) {
        PublishSubject<Pair<String, String>> ssdpSearchSubject = PublishSubject.create();
        ssdpSearchSubject.asObservable()
                .filter(res -> res.second.contains("IpBridge"))
                .subscribe(res -> {
                        verify(res.first).subscribe(s -> {
                            result.onNext(s);
                            result.onCompleted();
                        });
                    });

        Observable.fromCallable(() -> { findDevices(ctx, ssdpSearchSubject); return Observable.empty(); })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private Observable<String> verify(String host) {
        Request request = new Request.Builder()
                .url("http://" + host + "/description.xml")
                .build();

        return Observable.defer(() -> {
            try {
                Response response = httpClient.newCall(request).execute();
                String body = response.body().string();
                if (body.contains("Philips hue bridge")) {
                    return Observable.just(host);
                }
            } catch (IOException e) {
                return Observable.error(e);
            }
            return Observable.empty();
        });
    }

    private void findDevices(Context ctx, PublishSubject<Pair<String, String>> subject) {
        WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);

        WifiManager.MulticastLock lock = wifi.createMulticastLock("HueDiscoveryLock");
        lock.acquire();

        DatagramSocket socket;
        String inetAddress = "239.255.255.250";
        InetAddress group;
        try {
            group = InetAddress.getByName(inetAddress);
        } catch (UnknownHostException e) {
            return;
        }
        int multicastPort = 1900;

        String query =
                "M-SEARCH * HTTP/1.1\r\n" +
                        "HOST: " + multicastPort + ":" + multicastPort +"\r\n" +
                        "MAN: \"ssdp:discover\"\r\n" +
                        "MX: 8\r\n" +
                        "ST: ssdp:all\r\n" +
                        "\r\n";

        try {
            socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.bind(null);
        } catch (SocketException e) {
            return;
        }

        DatagramPacket dgram = new DatagramPacket(query.getBytes(), query.length(), group, multicastPort);
        try {
            socket.send(dgram);
        } catch (IOException e) {
            return;
        }

        long time = System.currentTimeMillis();
        long curTime = System.currentTimeMillis();

        try {
            while (curTime - time < 5000) {
                DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
                socket.receive(p);

                String s = new String(p.getData(), 0, p.getLength());
                subject.onNext(new Pair<>(p.getAddress().getHostAddress(), s));

                curTime = System.currentTimeMillis();
            }
        } catch (IOException e) {
            return;
        }

        socket.close();
        lock.release();
    }

}
