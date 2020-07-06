package emotionDetect.emotion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.emot.emotionPlayer.MusicPlayer;
import com.emot.emotionPlayer.R;
import com.emot.emotionPlayer.activities.MainActivity;
import com.emot.emotionPlayer.dataloaders.PlaylistLoader;
import com.emot.emotionPlayer.dataloaders.PlaylistSongLoader;
import com.emot.emotionPlayer.fragments.Emotion;
import com.emot.emotionPlayer.models.Playlist;
import com.emot.emotionPlayer.models.Song;
import com.emot.emotionPlayer.utils.TimberUtils;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import emotionDetect.predictivemodels.Classification;
import emotionDetect.predictivemodels.TensorFlowClassifier;

public class emotion_Main extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private int playlistcount;
    private long happy_id, sad_id, neutral_id;
    private boolean showAuto;
    private SquareImageView faceImageView;
    private TextView emotionShowView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int PIXEL_WIDTH = 48;
    TensorFlowClassifier classifier;
    Button detect;
    private List<Playlist> playlists = new ArrayList<>();
    private List<Playlist> emotion_playlists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion__main);


        this.faceImageView = (SquareImageView) this.findViewById(R.id.facialImageView);
        Button photoButton = (Button) this.findViewById(R.id.phototaker);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        detect = (Button) findViewById(R.id.detect);
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectEmotion();
            }
        });
        Button reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearStatus();
            }
        });
        detect.setEnabled(false);
        this.emotionShowView = (TextView) findViewById(R.id.emotionTxtView);
        loadModel();


        Toolbar toolbar = findViewById(R.id.toolbar);




    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void loadModel() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier=TensorFlowClassifier.create(getAssets(), "CNN",
                            "opt_em_convnet_5000.pb", "labels.txt", PIXEL_WIDTH,
                            "input", "output_50", true, 7);

                } catch (final Exception e) {
                    //if they aren't found, throw an error!
                    throw new RuntimeException("Error initializing classifiers!", e);
                }
            }
        }).start();
    }

    /**
     * The main function for emotion detection
     */
    private void detectEmotion() {
        String text = null;

        Bitmap image = ((BitmapDrawable) faceImageView.getDrawable()).getBitmap();
        Bitmap grayImage = toGrayscale(image);
        Bitmap resizedImage = getResizedBitmap(grayImage, 48, 48);
        int pixelarray[];

        //Initialize the intArray with the same size as the number of pixels on the image
        pixelarray = new int[resizedImage.getWidth() * resizedImage.getHeight()];

        //copy pixel data from the Bitmap into the 'intArray' array
        resizedImage.getPixels(pixelarray, 0, resizedImage.getWidth(), 0, 0, resizedImage.getWidth(), resizedImage.getHeight());


        float normalized_pixels[] = new float[pixelarray.length];
        for (int i = 0; i < pixelarray.length; i++) {
            // 0 for white and 255 for black
            int pix = pixelarray[i];
            int b = pix & 0xff;
            //  normalized_pixels[i] = (float)((0xff - b)/255.0);
            // normalized_pixels[i] = (float)(b/255.0);
            normalized_pixels[i] = (float) (b);

        }
        System.out.println(normalized_pixels);
        Log.d("pixel_values", String.valueOf(normalized_pixels));


        try {
            final Classification res = classifier.recognize(normalized_pixels);
            //if it can't classify, output a question mark
            if (res.getLabel() == null) {
                text = "Status: " + ": ?\n";
            } else {
                //else output its name
                text = String.format("%s: %s, %f", "Status: ", res.getLabel(),
                        res.getConf());
            }
        } catch (Exception e) {
            System.out.print("Exception:" + e.toString());

        }

        if (text.contains("Sad"))
           this.emotionShowView.setText("Sad");
        else if (text.contains("Happy"))
           this.emotionShowView.setText("Happy");
          else this.emotionShowView.setText("Neutral");
        //this.emotionShowView.setText(text);


       // this.faceImageView.setImageBitmap(grayImage);

        if (text.contains("Sad") || text.contains("sad")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("msg", "Sad");
            startActivity(intent);
        }
        if (text.contains("Happy")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("msg", "Happy");
            startActivity(intent);
        }
        if(text.contains("Neutral") || text.contains("Angry") || text.contains("Disgust") || text.contains("Fear") || text.contains("Surprise")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("msg", "Neutral");
            startActivity(intent);
        }
    }

    /**
     *
     */
    private void clearStatus(){
        detect.setEnabled(false);
        this.faceImageView.setImageResource(R.drawable.ic_launcher_background);
        this.emotionShowView.setText("Status: ?");

    }

    /**
     *
     * @param bmpOriginal
     * @return
     */
    // https://stackoverflow.com/questions/3373860/convert-a-bitmap-to-grayscale-in-android?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    //https://stackoverflow.com/questions/15759195/reduce-size-of-bitmap-to-some-specified-pixel-in-android?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            detect.setEnabled(true);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            faceImageView.setImageBitmap(imageBitmap);
        }
    }
}
