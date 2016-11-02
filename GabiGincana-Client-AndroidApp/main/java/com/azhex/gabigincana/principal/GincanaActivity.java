package com.azhex.gabigincana.principal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.azhex.gabigincana.R;
import com.azhex.gabigincana.cliente.ClienteService;
import com.azhex.gabigincana.herramientas.QREscaner;
import com.google.android.gms.vision.text.Text;

import java.io.IOException;

public class GincanaActivity extends AppCompatActivity {
    private String separador1 = "-1-2-3-4-";
    private ServiceConnection scCliente;
    private MediaPlayer[] mediaPlayer;
    private ClienteService mClienteService;
    private boolean mClienteServiceStat = false;
    private TextView tvUsername, tvScore, tvTop, tvChat;
    private ScrollView scllChatBox;
    private Button btnSendChatMsg, btnScan;
    private EditText inChatMsg;
    private String chatLog = "";
    private String username = "";
    private int score = 0;
    private int top = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gincana);

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvScore = (TextView) findViewById(R.id.tvScore);
        tvTop = (TextView) findViewById(R.id.tvTop);
        tvChat = (TextView) findViewById(R.id.tvChat);
        btnSendChatMsg = (Button) findViewById(R.id.btnSendChatMsg);
        inChatMsg = (EditText) findViewById(R.id.inChatMsg);
        scllChatBox = (ScrollView) findViewById(R.id.scllChatBox);
        btnScan = (Button) findViewById(R.id.btnScan);

        username = getIntent().getExtras().getString("username");
        tvUsername.setText(getIntent().getExtras().getString("username"));
        tvScore.setText(String.valueOf(score));
        tvTop.setText(String.valueOf(top));

        mediaPlayer = new MediaPlayer[4];

        for(int i=0;i<mediaPlayer.length;i++){
            prepararSonidos(i);
        }

        if(!mediaPlayer[0].isPlaying()) mediaPlayer[0].start();

        scCliente = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ClienteService.LocalBinder binder = (ClienteService.LocalBinder) service;
                mClienteService = binder.getService();
                mClienteServiceStat = true;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mClienteService.sendMsg("playing" + separador1 + "OK");

                        while(mClienteServiceStat && mClienteService.conectado && mClienteService.cliente != null){
                            processMsg();
                        }
                    }
                }).start();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mClienteServiceStat = false;
            }
        };

        if(!mClienteServiceStat) {
            Intent cliInt = new Intent(getApplicationContext(), ClienteService.class);
            bindService(cliInt, scCliente, Context.BIND_AUTO_CREATE);
        }

        btnSendChatMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClienteServiceStat){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mClienteService.sendMsg("chatMsgSend" + separador1 + username + separador1 + inChatMsg.getText().toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    inChatMsg.setText("");
                                }
                            });
                        }
                    }).start();
                }
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mediaPlayer[2].isPlaying()) mediaPlayer[2].start();
                Intent qrScan = new Intent(getApplicationContext(), QREscaner.class);
                startActivity(qrScan);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(!mediaPlayer[1].isPlaying()) mediaPlayer[1].start();
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
        inflater.inflate(R.menu.menu_gincana, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDesconectar:
                try {
                    if(!mediaPlayer[1].isPlaying()) mediaPlayer[1].start();
                    mClienteService.desconectar();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();

                return true;
            case R.id.menuCreditos:
                Intent creditosIntent = new Intent(this, CreditosActivity.class);
                startActivity(creditosIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void processMsg(){
        String msg = "";

        try {
            msg = mClienteService.recvMsg();

            if(msg != null && !msg.equals("")) {
                final String msgAux[] = msg.split(separador1);

                switch (msgAux[0]) {
                    case "userInfo":
                        if (msgAux.length == 3) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvScore.setText(msgAux[1]);
                                    tvTop.setText(msgAux[2]);
                                }
                            });
                        }

                        break;
                    case "chatMsgRecv":
                        if (msgAux.length == 3) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    printChatBox(msgAux[1] + ": " + msgAux[2]);
                                    scllChatBox.fullScroll(ScrollView.FOCUS_DOWN);
                                    if(!msgAux[1].toUpperCase().matches(".*SERVIDOR.*")){
                                        if(!mediaPlayer[3].isPlaying()) mediaPlayer[3].start();
                                    }
                                }
                            });
                        }

                        break;
                }
            }
        }catch(Exception e){
            try {
                mClienteService.desconectar();
                finish();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }

    private void printChatBox(String txt){
        chatLog += txt + "<br/>\n";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvChat.setText(Html.fromHtml(chatLog, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvChat.setText(Html.fromHtml(chatLog));
        }
    }

    private void prepararSonidos(final int id){
        switch(id){
            case 0:
                mediaPlayer[0] = MediaPlayer.create(GincanaActivity.this, R.raw.iniciar_sesion);
                mediaPlayer[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        prepararSonidos(id);
                    }
                });

                break;
            case 1:
                mediaPlayer[1] = MediaPlayer.create(GincanaActivity.this, R.raw.cerrar_sesion);
                mediaPlayer[1].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        prepararSonidos(id);
                    }
                });

                break;
            case 2:
                mediaPlayer[2] = MediaPlayer.create(GincanaActivity.this, R.raw.qr);
                mediaPlayer[2].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        prepararSonidos(id);
                    }
                });

                break;
            case 3:
                mediaPlayer[3] = MediaPlayer.create(GincanaActivity.this, R.raw.mens_entrada);
                mediaPlayer[3].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        prepararSonidos(id);
                    }
                });

                break;
        }
    }
}
