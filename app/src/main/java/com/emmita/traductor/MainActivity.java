package com.emmita.traductor;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.emmita.traductor.Models.LanguageResponse;
import com.emmita.traductor.Models.TranslateResponse;
import com.emmita.traductor.Retrofit.Api;
import com.emmita.traductor.Retrofit.ApiService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/";

    ImageView imageView;
    TextView original_txt;
    TextView translated_txt;
    private Button btn_camera;
    Bitmap bitmap;
    String txtFromImg;
    String lang;
    String first_lang;
    String second_lang;

    ApiService service;
    Call<LanguageResponse> languageResponseCall;
    Call<TranslateResponse> translateResponseCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        service = Api.getApi(BASE_URL).create(ApiService.class);

        imageView = (ImageView) findViewById(R.id.image_view);
        original_txt = (TextView) findViewById(R.id.o_txt);
        translated_txt = (TextView) findViewById(R.id.t_txt);
        btn_camera = (Button) findViewById(R.id.btn);

        original_txt.setText("Hola");

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                getLanguage(Api.KEY, original_txt.toString());
//                translateText(Api.KEY, original_txt.toString(), "es-en");


                original_txt.setVisibility(View.INVISIBLE);
                translated_txt.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK){

            bitmap = (Bitmap) data.getExtras().get("data");

            imageView.setImageBitmap(bitmap);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.detect:
                detextText(bitmap);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void detextText(Bitmap bitmap){

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        final FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {

                for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()){

                    String blockText = block.getText();

                    original_txt.setText(blockText);
                    txtFromImg = blockText;
                    dialogTextdetected();


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Failure", e.toString());
            }
        });

    }

    private void dialogTextdetected(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Texto detectado");
        builder.setMessage(txtFromImg + "\n \n¿Está escrito correctamente el texto detectado en la imagen?");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialogTransalte();
                dialog.dismiss();

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Por favor vuelve a tomar la foto", Toast.LENGTH_SHORT).show();

            }
        });

        builder.create().show();

    }

    private void dialogTransalte(){

        final String[] langs = {"Español", "Inglés"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, langs);
        final Spinner spinner = new Spinner(MainActivity.this);
     //   spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        spinner.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Traducir texto a español");
        builder.setMessage("Por favor selecciona el botón siguiente para traducir el texto");
//        builder.setView(spinner);

        builder.setPositiveButton("Siguiente", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                lang = "en-es";


                translateText(Api.KEY, txtFromImg, lang);
                original_txt.setVisibility(View.VISIBLE);
                translated_txt.setVisibility(View.VISIBLE);
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();


    }



    private void translateText(String key, String text, String lang){

        translateResponseCall = service.getText(key, text, lang);
        translateResponseCall.enqueue(new Callback<TranslateResponse>() {
            @Override
            public void onResponse(Call<TranslateResponse> call, Response<TranslateResponse> response) {

                if (response.isSuccessful()){

                    TranslateResponse translateResponse = response.body();
                    String[] text = translateResponse.getText();
                    translated_txt.setText(text[0]);

                }

            }

            @Override
            public void onFailure(Call<TranslateResponse> call, Throwable t) {

                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });

    }


}
