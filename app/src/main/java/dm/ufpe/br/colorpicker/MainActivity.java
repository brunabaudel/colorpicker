package dm.ufpe.br.colorpicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends Activity implements View.OnTouchListener, View.OnClickListener {

    private ConvertToCMYK convertToCMYK;

    private TextView txt_cyan;
    private TextView txt_magenta;
    private TextView txt_yellow;
    private TextView txt_key;

    private ImageView image;
    private FrameLayout frame_color;
    private ImageButton btn_camera;

    private FrameLayout frame_color1;
    private LinearLayout color_icon;
    private RelativeLayout relative;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initialize();
    }

    private void initialize() {

        this.convertToCMYK = new ConvertToCMYK();

        this.txt_cyan = (TextView) findViewById(R.id.txt_cyan);
        this.txt_magenta = (TextView) findViewById(R.id.txt_magenta);
        this.txt_yellow = (TextView) findViewById(R.id.txt_yellow);
        this.txt_key = (TextView) findViewById(R.id.txt_key);

        this.image = (ImageView) findViewById(R.id.img_color);
        this.frame_color = (FrameLayout) findViewById(R.id.frame_color);
        this.btn_camera = (ImageButton) findViewById(R.id.btn_camera);

        this.frame_color1 = (FrameLayout) findViewById(R.id.frame_color1);
        this.color_icon = (LinearLayout) findViewById(R.id.color_icon);
        this.relative = (RelativeLayout) findViewById(R.id.relative);

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
    public void setFinishOnTouchOutside(boolean finish) {
        super.setFinishOnTouchOutside(finish);
        this.frame_color1.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        int x = (int)motionEvent.getX();
        int y = (int)motionEvent.getY();

        int red = 0;
        int green = 0;
        int blue = 0;
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

           this.fillTextViews();

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

    private void fillTextViews() {
        this.txt_cyan.setText("Cyan: " + this.convertToCMYK.getCyan()+"");
        this.txt_magenta.setText("Magenta: " + this.convertToCMYK.getMagenta()+"");
        this.txt_yellow.setText("Yellow: " + this.convertToCMYK.getYellow()+"");
        this.txt_key.setText("Key: " + this.convertToCMYK.getKey()+"");
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
