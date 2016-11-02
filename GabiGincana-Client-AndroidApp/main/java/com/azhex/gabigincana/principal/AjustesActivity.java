package com.azhex.gabigincana.principal;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.azhex.gabigincana.R;
import com.azhex.gabigincana.herramientas.DialogoOk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class AjustesActivity extends AppCompatActivity {
    private Button btnAddServer;
    private Button btnRmServer;
    private Button btnBack;
    private EditText inIp;
    private EditText inPort;
    private Spinner spServers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        btnAddServer = (Button) findViewById(R.id.btnAddServer);
        btnRmServer = (Button) findViewById(R.id.btnRmServer);
        btnBack = (Button) findViewById(R.id.btnBack);
        inIp = (EditText) findViewById(R.id.inIp);
        inPort = (EditText) findViewById(R.id.inPort);
        spServers = (Spinner) findViewById(R.id.spServers);

        updateHosts();

        btnAddServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PrintWriter outs;
                    outs = new PrintWriter(new OutputStreamWriter(openFileOutput("hosts.txt", Context.MODE_APPEND)));
                    outs.append(inIp.getText().toString() + ":" + inPort.getText().toString() + "\n");
                    outs.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                updateHosts();
                dlgOk("Ajustes info", "Servidor " + inIp.getText() + ":" + inPort.getText() + " añadido");
            }
        });

        btnRmServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String servRemoved = "";
                try {
                    BufferedReader ins = new BufferedReader(new InputStreamReader(openFileInput("hosts.txt")));
                    String hosts = "";
                    ArrayList<String> hostsAux = new ArrayList<String>();
                    while((hosts = ins.readLine()) != null){
                        hostsAux.add(hosts);
                    }
                    String[] hostsAuxPro = hostsAux.toArray(new String[0]);
                    hosts = "";
                    for(int i=0;i<hostsAuxPro.length;i++){
                        if(i == 0){
                            hosts += hostsAuxPro[i];
                        }else{
                            if (!(i == spServers.getSelectedItemPosition())) {
                                hosts += "\n" + hostsAuxPro[i];
                            }else{
                                servRemoved = hostsAuxPro[i];
                            }
                        }
                    }
                    PrintWriter outs;
                    outs = new PrintWriter(new OutputStreamWriter(openFileOutput("hosts.txt", Context.MODE_PRIVATE)));
                    outs.println(hosts);
                    outs.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                updateHosts();
                if(servRemoved != "") {
                    dlgOk("Ajustes info", "Servidor " + servRemoved + " eliminado");
                }else{
                    dlgOk("Ajustes info", "El servidor a eliminar no existe");
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateHosts(){
        final ArrayList<String> spHostsArray = new ArrayList<String>();
        try {
            BufferedReader ins = new BufferedReader(new InputStreamReader(openFileInput("hosts.txt")));
            String linea;
            while((linea=ins.readLine()) != null) {
                spHostsArray.add(linea);
            }
            ins.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            try {
                PrintWriter outs = new PrintWriter(new OutputStreamWriter(openFileOutput("hosts.txt", Context.MODE_PRIVATE)));
                outs.println("localhost:7979");
                outs.close();
                String linea;
                BufferedReader inso = new BufferedReader(new InputStreamReader(openFileInput("hosts.txt")));
                while((linea=inso.readLine()) != null) {
                    spHostsArray.add(linea);
                }
                inso.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        final ArrayAdapter<String> adptHostAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spHostsArray);
        adptHostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spServers.setAdapter(adptHostAdapter);
    }

    private void dlgOk(String title, String msg){
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogoOk dlg = new DialogoOk();
        dlg.title = title;
        dlg.msg = msg;
        dlg.show(fragmentManager, "dlgOk");
    }
}
