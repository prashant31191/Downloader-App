package us.shandian.giga.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;
import com.googlecode.mp4parser.authoring.tracks.H264TrackImpl;
import com.googlecode.mp4parser.authoring.tracks.MP3TrackImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import us.shandian.giga.R;

/**
 * Created by Admin on 7/11/2016.
 */

public class AudioChangeActivity extends Activity
{


    TextView tvDetail;
    Button btnSelectAudio,btnSelectVideo,btnDone;

    String videoFile = "";
    String audioFile = "";
    String outputFile = "";
    int REQUEST_TAKE_GALLERY_VIDEO = 101;
    int REQUEST_TAKE_GALLERY_AUDIO = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.audio_change_activity);

        try
        {
            tvDetail = (TextView)findViewById(R.id.tvDetail);
            btnSelectAudio = (Button)findViewById(R.id.btnSelectAudio);
            btnSelectVideo = (Button)findViewById(R.id.btnSelectVideo);
            btnDone = (Button)findViewById(R.id.btnDone);



btnSelectVideo.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setType("video/mp4");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);

    }
});


            btnSelectAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("audio/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Audio"),REQUEST_TAKE_GALLERY_AUDIO);

                }
            });




            btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDone();
                }
            });







        }
        catch (Exception e)
        {
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

private void setDone()
{
   /* try {

        // "/storage/emulated/0/Download/Speedo Swim Technique - Breaststroke - Created by Speedo, Presented by ProSwimwear.mp4";
        // "/storage/sdcard1/MyGeet/Judas..mp3";
        H264TrackImpl h264Track = new H264TrackImpl(new FileDataSourceImpl(videoFile));
        MP3TrackImpl mp3Track = new MP3TrackImpl(new FileDataSourceImpl(audioFile));
        Movie movie = new Movie();
        movie.addTrack(h264Track);
        movie.addTrack(mp3Track);
        Container mp4file = new DefaultMp4Builder().build(movie);
        outputFile = videoFile;
        FileChannel fileChannel = new FileOutputStream(new File(outputFile)).getChannel();
        mp4file.writeContainer(fileChannel);
        fileChannel.close();
    }catch (Exception e)
    {
        e.printStackTrace();
        Toast.makeText(this, "An error occurred222: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }*/



    try {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
       /* H264TrackImpl h264Track = new H264TrackImpl(new FileDataSourceImpl(baseDir + "/video.h264"));
        AACTrackImpl aacTrack = new AACTrackImpl(new FileDataSourceImpl( baseDir + "/aac_sample.aac" ));
*/
        H264TrackImpl h264Track = new H264TrackImpl(new FileDataSourceImpl(videoFile));
        AACTrackImpl aacTrack = new AACTrackImpl(new FileDataSourceImpl(audioFile));

        Movie movie = new Movie();
        movie.addTrack(h264Track);
        movie.addTrack(aacTrack);
        Container mp4file = new DefaultMp4Builder().build(movie);

        FileChannel fc = new FileOutputStream(new File(baseDir +"/output.mp4")).getChannel();
        mp4file.writeContainer(fc);
        fc.close();

    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
}



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                Uri selectedImageUri = data.getData();

                // OI FILE Manager
             String   filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
               String selectedImagePath = getPath(selectedImageUri);
                if (selectedImagePath != null) {

                    Log.i("=--Video path-=","--path---"+selectedImagePath);
                    videoFile = selectedImagePath;

                    /*Intent intent = new Intent(HomeActivity.this,VideoplayAvtivity.class);
                    intent.putExtra("path", selectedImagePath);
                    startActivity(intent);*/
                }
            }
            if (requestCode == REQUEST_TAKE_GALLERY_AUDIO) {
                Uri selectedImageUri = data.getData();

                // OI FILE Manager
                String   filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);
                if (selectedImagePath != null) {

                    Log.i("=--audio path-=","--path---"+selectedImagePath);
                    audioFile = selectedImagePath;

                    /*Intent intent = new Intent(HomeActivity.this,VideoplayAvtivity.class);
                    intent.putExtra("path", selectedImagePath);
                    startActivity(intent);*/
                }
            }
        }
    }

    // UPDATED!
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}
