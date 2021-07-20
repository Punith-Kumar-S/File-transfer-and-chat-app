package com.example.blabme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.InetAddresses;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class mainclass extends AppCompatActivity {
    Button btnonoff,btndiscover,btnsend;
    ListView listview;
    TextView readmsgbox,connectionstatus;
    EditText writemsg;

    WifiManager wifiManager;
    WifiP2pManager mmanager;
    WifiP2pManager.Channel mchannel;
    BroadcastReceiver mreceiver;
    IntentFilter mintentfilter;
    List<WifiP2pDevice> peers=new ArrayList<WifiP2pDevice>();
    String[] devicenamearray;
    WifiP2pDevice[] devicearray;
    static final int messageread=1;
    serverclass serverclass;
    clientClass clientClass;
    sendrecive sendrecive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialWork();
        exqlistener();
    }
    Handler handler= new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch(msg.what){
                case messageread:
                  byte[] readbuff= (byte[]) msg.obj;
                  String tempmsg=new String(readbuff,0,msg.arg1);
                  readmsgbox.setText(tempmsg);
                  break;
            }
            return true;
        }
    });


    private void exqlistener() {
        btnonoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    btnonoff.setText("Wifi ON");
                } else {
                    wifiManager.setWifiEnabled(true);
                    btnonoff.setText("Wifi OFF");
                }
            }
        });

        btndiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mmanager.discoverPeers(mchannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionstatus.setText("Discover Started");
                    }

                    @Override
                    public void onFailure(int reason) {
                        connectionstatus.setText("Discover Starting Failed");
                    }
                });
            }
        });


       listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                final WifiP2pDevice device=devicearray[position];
                WifiP2pConfig config=new WifiP2pConfig();
                config.deviceAddress=device.deviceAddress;

                mmanager.connect(mchannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),"Connected to"+device.deviceName,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(),"Not Connected",Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });



        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             String msg=writemsg.getText().toString();
             sendrecive.write(msg.getBytes());
            }
        });
    }

    private void initialWork() {
        btnonoff=(Button) findViewById(R.id.onOff);
        btndiscover=(Button) findViewById(R.id.discover);
        btnsend=(Button) findViewById(R.id.sendButton);
        listview=(ListView) findViewById(R.id.peerListView);
        readmsgbox=(TextView)findViewById(R.id.readMsg);
        connectionstatus=(TextView)findViewById(R.id.connectionStatus);
        writemsg=(EditText)findViewById(R.id.writeMsg);


        wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mmanager= (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mchannel=mmanager.initialize(this,getMainLooper(),null);

        mreceiver=new WiFiDirectBroadcastREceiver(mmanager,mchannel,this);
        mintentfilter=new IntentFilter();
        mintentfilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mintentfilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mintentfilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mintentfilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }

    WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerlist) {
            if (!peerlist.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerlist.getDeviceList());

                devicenamearray=new String[peerlist.getDeviceList().size()];
                devicearray=new WifiP2pDevice[peerlist.getDeviceList().size()];
                int index=0;
                for(WifiP2pDevice device:peerlist.getDeviceList()){
                    devicenamearray[index]=device.deviceName;
                    devicearray[index]=device;
                    index++;
                }
                ArrayAdapter<String> adapter =new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,devicenamearray);
                listview.setAdapter(adapter);

            }
            if(peers.size()==0){
                Toast.makeText(getApplicationContext(),"NO Device Found",Toast.LENGTH_SHORT).show();
            }
        }

    };
    WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupowneraddress=wifiP2pInfo.groupOwnerAddress;
            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
                connectionstatus.setText("Host");
                serverclass= new serverclass();
                serverclass.start();

            }else if(wifiP2pInfo.groupFormed){
                connectionstatus.setText("Client");
                clientClass= new clientClass(groupowneraddress);
                clientClass.start();
            }

        }
    };

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceiver(mreceiver,mintentfilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mreceiver);
    }

    public class serverclass extends Thread{
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try{

           serverSocket =new ServerSocket(8888);
           socket=serverSocket.accept();
           sendrecive=new sendrecive(socket);
           sendrecive.start();
        }
            catch (IOException e){
                e.printStackTrace();
            }
    }
    }

    private class sendrecive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public sendrecive(Socket skt){
            socket=skt;
            try {
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer=new byte[1024];
            int bytes;
            while (socket!=null){
                    try {
                        bytes = inputStream.read(buffer);
                        if(bytes>0){
                            handler.obtainMessage(messageread,bytes,-1,buffer).sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


            }
        }
        public void write (byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public class clientClass extends Thread{
        Socket socket;
        String getHostAddress;


        public clientClass(InetAddress hostAddress){
            getHostAddress=hostAddress.getHostAddress();
            socket =new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(getHostAddress, 8888), 500);
                sendrecive=new sendrecive(socket);
                sendrecive.start();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}

