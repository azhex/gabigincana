package com.azhex.gabigincana.cliente;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Cliente {
    public Socket sock;
    public BufferedReader in;
    public PrintWriter out;
    public boolean conectado = false;

    public Cliente(final String h, final int p){
        SocketAddress addr = new InetSocketAddress(h, p);

        try{
            sock = new Socket();
            sock.connect(addr, 7000);
            sock.setSoTimeout(10000);

            if(sock.isConnected()){
                conectado = true;
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                out = new PrintWriter(sock.getOutputStream(), true);
            }else{
                Log.e("SocketConnect", "No conectado");
            }
        }catch(IOException e){
            Log.e("SocketConnect", "ERROR IOExcept");
            e.printStackTrace();
            conectado = false;
        }
    }

    public boolean sendMsg(String msg){
        try{
            if(conectado){
                out.println(msg);

                return true;
            }else{
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
            if(sock.isConnected() && !sock.isInputShutdown()){
                msg = in.readLine();
            }else{
                return "ERROR, NO CONECTADO";
            }
        }catch(Exception e){
            return "ERROR, EXCEPTION";
        }

        return msg;
    }
}
