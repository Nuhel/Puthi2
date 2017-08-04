package com.example.nuhel.puthi2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.nuhel.puthi2.R.id.image;

public class MainActivity extends AppCompatActivity {


    private Button button;
    private ImageView imageView;
    private Uri uri;
    private Response response = null;

    private String imagesrc;
    private Uri imgsrc2;

    private String result="";

    private String results[];

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.upload);
        imageView = (ImageView) findViewById(R.id.imageview);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == MainActivity.this.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }


            imgsrc2 = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(imgsrc2,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagesrc = cursor.getString(columnIndex);
            cursor.close();

            uri = data.getData();
            makeT(imagesrc);
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Featching Token");
            progressDialog.setMessage("Loading");
            progressDialog.show();
            up();

        }
    }

    private void makeT(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }

    private  void up(){


        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {

                OkHttpClient client = new OkHttpClient();
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(80, TimeUnit.SECONDS);
                builder.readTimeout(80, TimeUnit.SECONDS);
                builder.writeTimeout(80, TimeUnit.SECONDS);
                client = builder.build();


                MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
                RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"sampleFile\"; filename=\""+imagesrc.toString()+"\"\r\nContent-Type: image/jpeg\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
                Request request = new Request.Builder()

                        .url("http://113.11.120.208/upload")
                        .post(body)
                        .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                        .addHeader("cache-control", "no-cache")
                        //.addHeader("postman-token", "180e357b-c12f-22eb-af32-2af0f9d6dbd5")
                        .build();

                try {
                    response = client.newCall(request).execute();
                    if(response.isSuccessful()){

                        result = response.body().string();

                    }else
                        result = request.body().toString()+"Nuhel2";
                } catch (IOException e) {
                    result = e.toString()+"Nuhel3";
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                progressDialog.dismiss();
                makeT(s);
            }
        }.execute();
    }

}
