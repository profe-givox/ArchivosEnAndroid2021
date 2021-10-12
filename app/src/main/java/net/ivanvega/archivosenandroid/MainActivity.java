package net.ivanvega.archivosenandroid;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    EditText cajaTexto;
    RadioGroup optGroupAlmacenamiento;

    Button btnAbrir, btnGuardar;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher, as an instance variable.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cajaTexto = findViewById(R.id.txtTexto);
        optGroupAlmacenamiento = findViewById(R.id.optGroupTipoAlama);
        btnAbrir = findViewById(R.id.btnAbrir);
        btnGuardar = findViewById(R.id.btnGuardar);


        btnGuardar
                .setOnClickListener(view -> guardarArchivo());
        btnAbrir.setOnClickListener(view -> abrirArchivo());

       requestPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                // Permission is granted. Continue the action or workflow in your
                                // app.
                                guadarArchivoMemoriaExterna();
                            } else {
                                // Explain to the user that the feature is unavailable because the
                                // features requires a permission that the user has denied. At the
                                // same time, respect the user's decision. Don't link to system
                                // settings in an effort to convince the user to change their
                                // decision.
                                Toast.makeText(this, "Lo sentimos Es necesario el permiso" +
                                        " de lo contrario queda inhabilitado el almacenamiento externo",Toast.LENGTH_LONG).show();
                                //btnGuardar.setEnabled(false);
                            }
                        });
    }

    private void abrirArchivo() {
        if(optGroupAlmacenamiento.getCheckedRadioButtonId()==R.id.optExterna){
            abrirArchivoExterno();

        }

    }


    private void guardarArchivo() {
        if( optGroupAlmacenamiento.getCheckedRadioButtonId()
                == R.id.optExterna  ){
            validarPermiso();

        }
    }

    private void abrirArchivoExterno() {
        File pathExternal =  getExternalFilesDir(null);
        File file = new File(pathExternal, "MiArchivo.txt");

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            String texto= "";
            int car=-1;
            do {
                car =  fileInputStream.read();

                if(car != -1) {
                    texto += (char)car;
                }

            }while(car!=-1);

            cajaTexto.setText(texto);
            fileInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void validarPermiso(){
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            //performAction(...);
            guadarArchivoMemoriaExterna();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected. In this UI,
                    // include a "cancel" or "no thanks" button that allows the user to
                    // continue using your app without granting the permission.
                    //showInContextUI(...);
                  Toast.makeText(this, "Es necesario para escribir" +
                         " el archivo en la memoria exerna",Toast.LENGTH_LONG).show();

                }else{
                    requestPermissionLauncher.launch(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
        }else{
            requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


    }

    private void guadarArchivoMemoriaExterna() {

        getExternalFilesDir(null);

        File pathExternal =  getExternalFilesDir(null);
        File file = new File(pathExternal, "MiArchivo.txt");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true );

            fileOutputStream.write(cajaTexto.getText().toString().getBytes());

            fileOutputStream.close();

            cajaTexto.setText("");

            for ( String nombre : getExternalFilesDir(null).list() ){
                Log.d("ARCHIV", nombre);
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}