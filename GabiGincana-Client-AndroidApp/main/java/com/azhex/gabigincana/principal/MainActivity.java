package com.azhex.gabigincana.principal;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.azhex.gabigincana.R;
import com.azhex.gabigincana.cliente.Cliente;
import com.azhex.gabigincana.cliente.ClienteService;
import com.azhex.gabigincana.herramientas.DialogoOk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private Spinner spHost;
    private Button btnLogin;
    private EditText inUser, inClave;
    private String separador1 = "-1-2-3-4-";
    private ServiceConnection scCliente;
    private ClienteService mClienteService;
    private boolean mClienteServiceStat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spHost = (Spinner) findViewById(R.id.spHost);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        inClave = (EditText) findViewById(R.id.inClave);
        inUser = (EditText) findViewById(R.id.inUser);

        updateHosts();

        scCliente = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ClienteService.LocalBinder binder = (ClienteService.LocalBinder) service;
                mClienteService = binder.getService();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnLogin.setEnabled(true);
                    }
                });
                mClienteServiceStat = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mClienteServiceStat = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnLogin.setEnabled(false);
                    }
                });
            }
        };

        btnLogin.setEnabled(false);

        if(!mClienteServiceStat) {
            Intent cliInt = new Intent(getApplicationContext(), ClienteService.class);
            bindService(cliInt, scCliente, Context.BIND_AUTO_CREATE);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(mClienteServiceStat){
                            mClienteService.cliente = null;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnLogin.setText("Entrando...");
                                    btnLogin.setEnabled(false);
                                }
                            });
                            String[] host = spHost.getSelectedItem().toString().split(":");
                            try{
                                mClienteService.conectar(host[0], Integer.parseInt(host[1]));
                            }catch(Exception e){
                                dlgOk("Servidor malformed", "Servidor no apto para la conexión");
                            }

                            if(!mClienteService.conectado){
                                dlgOk("Login error", "Error al conectar con el servidor, puede que el servidor no se encuentre online");
                                try {
                                    mClienteService.desconectar();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnLogin.setText("Entrar");
                                        btnLogin.setEnabled(true);
                                    }
                                });

                                return;
                            }

                            String username = inUser.getText().toString();
                            String clave = inClave.getText().toString();

                            String loginFormateado = "login" + separador1 + username + separador1 + clave;

                            if(!mClienteService.sendMsg(loginFormateado)){
                                dlgOk("Login error", "Error al enviar login al servidor, puede que el servidor no se encuentre online");
                                try {
                                    mClienteService.desconectar();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnLogin.setText("Entrar");
                                        btnLogin.setEnabled(true);
                                    }
                                });

                                return;
                            }

                            String resp = "";

                            resp = mClienteService.recvMsg();

                            try {
                                Thread.sleep(2000); // Espera de 2 seg para retrasar el inicio de la otra actividad
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnLogin.setText("Entrar");
                                    btnLogin.setEnabled(true);
                                }
                            });

                            if(!(resp == null) && !resp.equals("ERROR, EXCEPTION") && !resp.equals("ERROR, NO CONECTADO")){
                                if(resp.equals("CLAVE_INCORRECTA")){
                                    dlgOk("Login error", "La clave no es correcta");
                                    try {
                                        mClienteService.desconectar();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }else if(resp.equals("USER_EXISTENTE")){
                                    dlgOk("Login error", "El usuario seleccionado ya existe, selecciona otro y vuelve a intentarlo");
                                    try {
                                        mClienteService.desconectar();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }else if(resp.split(separador1).length > 1){
                                    String respAux[] = resp.split(separador1);

                                    if(respAux[0].equals("login")){
                                        if(respAux[1].equals("OK")){
                                            Intent gincana = new Intent(getApplicationContext(), GincanaActivity.class);
                                            gincana.putExtra("username", username);
                                            startActivity(gincana);
                                        }
                                    }else{
                                        try {
                                            mClienteService.desconectar();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }else{
                                    dlgOk("Login error", "Error de formato del servidor, puede que el servidor tenga algún fallo");
                                    try {
                                        mClienteService.desconectar();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else{
                                dlgOk("Login error", "Error al conectar con el servidor, puede que el servidor no se encuentre online");
                                try {
                                    mClienteService.desconectar();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateHosts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            mClienteService.desconectar();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unbindService(scCliente);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuCreditos:
                Intent creditosIntent = new Intent(this, CreditosActivity.class);
                startActivity(creditosIntent);

                return true;
            case R.id.menuAjustes:
                Intent ajustesIntent = new Intent(this, AjustesActivity.class);
                startActivity(ajustesIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dlgOk(String title, String msg){
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogoOk dlg = new DialogoOk();
        dlg.title = title;
        dlg.msg = msg;
        dlg.show(fragmentManager, "dlgOk");
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
        spHost.setAdapter(adptHostAdapter);
    }
}
