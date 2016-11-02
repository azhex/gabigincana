package com.azhex.gabigincana.cliente;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class ClienteService extends Service {
    private final IBinder mbinder = new LocalBinder();
    public String host = "";
    public int port;
    public Cliente cliente;
    public String user = "";
    public boolean conectado = false;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mbinder;
    }

    public class LocalBinder extends Binder{
        public ClienteService getService(){
            return ClienteService.this;
        }
    }

    public boolean conectar(String h, int p){
        host = h;
        port = p;

        try{
            cliente = new Cliente(host, port);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        if(cliente.conectado){
            conectado = true;

            return true;
        }else{
            return false;
        }
    }

    public boolean sendMsg(String msg){
        try {
            if (cliente.conectado) {
                if (cliente.sendMsg(msg)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public String recvMsg(){
        String msg = "";

        try{
            msg = cliente.recvMsg();
        }catch(Exception e){
            e.printStackTrace();
            conectado = false;
        }

        return msg;
    }

    public boolean desconectar() throws IOException {
        if(cliente != null){
            cliente.sock.close();
            conectado = false;

            return true;
        }else{
            return false;
        }
    }
}
