package br.ufpe.dm.colorpicker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class MainActivity extends ActionBarActivity implements View.OnTouchListener, View.OnClickListener {

    int red = 0;
    int green = 0;
    int blue = 0;

    private ConvertToCMYK convertToCMYK;

    private EditText edt_cyan;
    private EditText edt_magenta;
    private EditText edt_yellow;
    private EditText edt_key;

    //Camera

    private ImageView image;
    private FrameLayout frame_color;
    private ImageButton btn_camera;

    private FrameLayout frame_color1;
    private LinearLayout color_icon;
    private RelativeLayout relative;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;

    private Bitmap bitmap;

    //Bluetooth

    private static final String TAG = "bluetooth2";

    private Button btn_enviar;

    final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb;

    private ConnectedThread mConnectedThread;
    private Handler h;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "00:14:03:07:13:28";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ActionBar ac = getSupportActionBar();
        //ac.setBackgroundDrawable(new ColorDrawable(R.color.red));

        this.initialize();

        this.initHandler();

        this.btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        this.checkBTState();
    }

    private void initialize() {

        this.convertToCMYK = new ConvertToCMYK();
        this.sb = new StringBuilder();

        this.edt_cyan = (EditText) findViewById(R.id.txt_cyan);
        this.edt_magenta = (EditText) findViewById(R.id.txt_magenta);
        this.edt_yellow = (EditText) findViewById(R.id.txt_yellow);
        this.edt_key = (EditText) findViewById(R.id.txt_key);

        this.image = (ImageView) findViewById(R.id.img_color);
        this.frame_color = (FrameLayout) findViewById(R.id.frame_color);
        this.btn_camera = (ImageButton) findViewById(R.id.btn_camera);

        this.frame_color1 = (FrameLayout) findViewById(R.id.frame_color1);
        this.color_icon = (LinearLayout) findViewById(R.id.color_icon);
        this.relative = (RelativeLayout) findViewById(R.id.relative);

        this.btn_enviar = (Button) findViewById(R.id.btn_enviar);

        this.image.setImageResource(R.drawable.flor);

        this.image.setOnTouchListener(this);
        this.btn_camera.setOnClickListener(this);
        this.btn_enviar.setOnClickListener(this);
    }

    private void editTextChanged() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, Settings.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setFinishOnTouchOutside(boolean finish) {
        super.setFinishOnTouchOutside(finish);
        this.frame_color1.setVisibility(View.INVISIBLE);
    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        int x = (int)motionEvent.getX();
        int y = (int)motionEvent.getY();


        int pixel = 0;

        this.bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(this.bitmap);
        image.draw(canvas);

       if((x >= 0 && x < this.bitmap.getWidth() && y >= 0 && y < this.bitmap.getHeight())) {

           pixel = this.bitmap.getPixel(x, y);

           red = Color.red(pixel);
           green = Color.green(pixel);
           blue = Color.blue(pixel);

           this.convertToCMYK.set_red(red);
           this.convertToCMYK.set_green(green);
           this.convertToCMYK.set_blue(blue);

           this.fillEditViews();

          // Log.i("COLOR", "Pixel: " + pixel);
           //Log.i("COLOR", "R: " + this.red + " G: " + this.green + " B: " + this.blue);
           Log.i("POSITION", "x: " + x + " y: " + y);

           float framePositionY = (int)y-this.image.getY() - 100;
           float framePositionX = (int)x + this.image.getX() - 75;

           if(framePositionY <= 10) {
               frame_color1.setRotation(180.0f);
               this.frame_color1.setX(framePositionX);
               this.frame_color1.setY(framePositionY + this.frame_color1.getHeight());

           } else {
               frame_color1.setRotation(0.0f);
               this.frame_color1.setX(framePositionX);
               this.frame_color1.setY(framePositionY);
           }

           this.frame_color.setBackgroundColor(Color.rgb(red, green, blue));
           this.color_icon.setBackgroundColor(Color.rgb(red, green, blue));

           Log.i("ALTURA", "x: " + framePositionX);

           this.frame_color1.setVisibility(View.VISIBLE);
       }

        bitmap.recycle();
        return false;
    }

    private void fillEditViews() {
        this.edt_cyan.setText(this.convertToCMYK.getCyan()+"");
        this.edt_magenta.setText(this.convertToCMYK.getMagenta()+"");
        this.edt_yellow.setText(this.convertToCMYK.getYellow()+"");
        this.edt_key.setText(this.convertToCMYK.getKey()+"");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_camera:
                camera();
                break;
            case R.id.btn_enviar:
                writeBluetooth();
                Toast.makeText(this, "Send to Bluetooth", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }

    /**
     *
     * Camera
     *
     */

    private void camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    private Uri getOutputMediaFileUri() {
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
        imagesFolder.mkdirs();
        File image = new File(imagesFolder, "image.jpg");
        return Uri.fromFile(image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                this.image.setImageDrawable(Drawable.createFromPath(data.getData().getPath()));
            } else if (resultCode == RESULT_CANCELED) {
            } else {
                //Toast.makeText(this, "Captura falhou", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     *
     * Bluetooth
     *
     */

    private void writeBluetooth() {

        String cyanStr = edt_cyan.getText().toString();
        String magentaStr = edt_magenta.getText().toString();
        String yellowStr = edt_yellow.getText().toString();
        String keyStr = edt_key.getText().toString();

        Log.d("Debug", "Magenta " + edt_magenta.getText().toString());
        Log.d("Debug", "Cyan " + edt_cyan.getText().toString());

        int cyan = Integer.parseInt(edt_cyan.getText().toString());
        int magenta = Integer.parseInt(edt_magenta.getText().toString());
        int yellow = Integer.parseInt(edt_yellow.getText().toString());
        int key = Integer.parseInt(edt_key.getText().toString());

        Log.d("Debug", "Magenta " + magenta);
        Log.d("Debug", "Cyan " + cyan);
        Log.d("Debug", "Yellow " + yellow);
        Log.d("Debug", "key " + key);

        int check = 0;
        mConnectedThread.write("105");
        //mConnectedThread.write("3");    // Send "0" via Bluetooth
        //mConnectedThread.write("3");

        mConnectedThread.write(cyanStr); //cyan
        mConnectedThread.write(magentaStr); //magenta
        mConnectedThread.write(yellowStr); //yellow
        mConnectedThread.write(keyStr); //Key

        mConnectedThread.write("0"); //Water
        mConnectedThread.write(""+red); //Red
        mConnectedThread.write(""+green); //Green
        mConnectedThread.write(""+blue); //Blue
        check = ((int) (cyan+magenta+yellow+key+0/*water*/+red+green+blue) / 8);
        Log.d("Debug", "Check " + check);
        mConnectedThread.write("" + check);
    }




    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private void checkBTState() {
        if(btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    public void initHandler() {
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("\r\n");
                        if (endOfLineIndex > 0) {
                            String sbprint = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());

                        }

                        break;
                }
            }

        };
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        btAdapter.cancelDiscovery();

        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        mConnectedThread = new ConnectedThread(btSocket, h);
        mConnectedThread.start();

    }


    /**
     *
     *
     * Convert to RGB
     *
     */

    public void convertToRGB() {

        if (edt_cyan != null && edt_magenta != null && edt_yellow != null && edt_key != null) {
            int cyan = Integer.parseInt(edt_cyan.getText().toString());
            int magenta = Integer.parseInt(edt_magenta.getText().toString());
            int yellow = Integer.parseInt(edt_yellow.getText().toString());
            int key = Integer.parseInt(edt_key.getText().toString());

            int red = 255 * (1 - cyan) * (1 - key);
            int green = 255 * (1 - magenta) * (1 - key);
            int blue = 255 * (1 - yellow) * (1 - key);

            //this.frame_color.setBackgroundColor(Color.rgb(red, green, blue));

        }
    }
}
