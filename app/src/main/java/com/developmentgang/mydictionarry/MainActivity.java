package com.developmentgang.mydictionarry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class MainActivity extends AppCompatActivity {
   TextView sourceLanguage,detectedLanguage;
   Button trans;
   Spinner languageSpinner;
   String [] selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         sourceLanguage = findViewById(R.id.sourceLanguge);
         detectedLanguage = findViewById(R.id.detectedLanguge);
         trans = findViewById(R.id.translate);
        languageSpinner = findViewById(R.id.languageSpinner);
        selectedLanguage = getResources().getStringArray(R.array.selectTargetLanguage);

        // setting adapter in dropdown
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,selectedLanguage);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

         trans.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
               if(sourceLanguage.getText().length() > 0){
                   String spinerLanguge = languageSpinner.getSelectedItem().toString();
                   languageIdentity(sourceLanguage.getText().toString(),spinerLanguge);
                   Toast.makeText(getApplicationContext(),spinerLanguge,Toast.LENGTH_SHORT).show();
               }
               else {
                   Toast.makeText(getApplicationContext(),"text length should be minimum 10",Toast.LENGTH_SHORT).show();
               }
             }
         });
    }
    //end of on create
    private void languageIdentity(String text,  String target){
        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode.equals("und")) {
                                    Log.i("TAG", "Can't identify language.");
                                } else {
                                    Toast.makeText(getApplicationContext(),languageCode,Toast.LENGTH_SHORT).show();
                                    translation(text,languageCode,target);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be loaded or other internal error.
                                // ...
                            }
                        });
    }


    private void translation(String text, String source, String targeted ){
        // create a translator;
        String target = targeted;
        if (target == null){
            target = TranslateLanguage.GERMAN;
        }
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(source)
                        .setTargetLanguage(target)
                        .build();
        Translator translatorModel =
                Translation.getClient(options);
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        // checking either its download or not
        translatorModel.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("downloadStatus", "onFailure: "+aVoid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("downloadStatus", "onFailure: "+e.getMessage());
                    }
                });



        // translate the language
        translatorModel.translate(text)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.d("translated", "onSuccess: "+s);
                        detectedLanguage.setText(s);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("translated", "onFailure: "+e.getMessage());
                    }
                });
    }

}