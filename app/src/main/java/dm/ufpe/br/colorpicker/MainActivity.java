package dm.ufpe.br.colorpicker;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;

public class MainActivity extends Activity implements View.OnTouchListener, View.OnClickListener {

    private static final int RANGE = 255;
    private int red;
    private int green;
    private int blue;

    private TextView txt_cyan;
    private TextView txt_magenta;
    private TextView txt_yellow;
    private TextView txt_key;

    private ImageView image;
    private FrameLayout frame_color;
    private ImageButton btn_camera;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {

        this.red = 0;
        this.green = 0;
        this.blue = 0;

        this.txt_cyan = (TextView) findViewById(R.id.txt_cyan);
        this.txt_magenta = (TextView) findViewById(R.id.txt_magenta);
        this.txt_yellow = (TextView) findViewById(R.id.txt_yellow);
        this.txt_key = (TextView) findViewById(R.id.txt_key);

        this.image = (ImageView) findViewById(R.id.img_color);
        this.frame_color = (FrameLayout) findViewById(R.id.frame_color);
        this.btn_camera = (ImageButton) findViewById(R.id.btn_camera);

        this.image.setImageResource(R.drawable.flor);

        this.image.setOnTouchListener(this);
        this.btn_camera.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        int x = (int)motionEvent.getX();
        int y = (int)motionEvent.getY();

        this.bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(this.bitmap);
        image.draw(canvas);

       if((x >= 0 && x < this.bitmap.getWidth() && y >= 0 && y < this.bitmap.getHeight())) {

           int pixel = this.bitmap.getPixel(x, y);

           this.red = Color.red(pixel);
           this.green = Color.green(pixel);
           this.blue = Color.blue(pixel);

           this.txt_cyan.setText("Cyan: " + this.getCyan()+"");
           this.txt_magenta.setText("Magenta: " + this.getMagenta()+"");
           this.txt_yellow.setText("Yellow: " + this.getYellow()+"");
           this.txt_key.setText("Key: " + this.getKey()+"");

           Log.i("COLOR", "Pixel: " + pixel);
           Log.i("COLOR", "R: " + this.red + " G: " + this.green + " B: " + this.blue);

           this.frame_color.setBackgroundColor(Color.rgb(this.red, this.green, this.blue));

       }
        bitmap.recycle();
        return false;
    }

    /**
     *
     * Convert RGB to CMYK
     *
     */

    private double get_red() {
        return (double) this.red/RANGE;
    }

    private double get_green() {
        return (double) this.green/RANGE;
    }

    private double get_blue() {
        return (double) this.blue/RANGE;
    }

    private double calcKey() {
        return 1 - maxRGB();
    }

    private String getKey() {
        double k = calcKey() * 100;
        return new DecimalFormat("#.##").format(k);
    }

    private String getCyan() {
        double c = (1 - get_red() - calcKey()) / (1 - calcKey()) * 100;
        return new DecimalFormat("#.##").format(c);
    }

    private String getMagenta() {
        double m = (1 - get_green() - calcKey()) / (1 - calcKey()) * 100;
        return new DecimalFormat("#.##").format(m);
    }

    private String getYellow() {
        double y = (1 - get_blue() - calcKey()) / (1 - calcKey()) * 100;
        return new DecimalFormat("#.##").format(y);
    }

    private double maxRGB(){
        return Math.max(Math.max(get_blue(), get_green()), get_red());
    }

    /**
     *
     * Camera
     *
     */

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_camera:
                camera();
                break;
            default:
                break;
        }

    }

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
            }
        }
    }

}
