package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.primer.bryanalvarez.captura_info_gt.Models.HttpFileUploader;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class ActualizarActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_FILE_REQUEST =1 ;
    private Spinner spinnerTablas;
    private Button btn_descargar_archivo;
    private Button btn_selecconar_archivo;
    private Button btn_procesar_actualizacion;
    private AlertDialog alertDialog_cargando;

    private final int PERMISSION_READ_EXTERNAL_MEMORY = 1;
    private String path;

    RequestQueue request;
    StringRequest stringRequest;
    String tabla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);

        request = Volley.newRequestQueue(this);

        alertDialog_cargando = new AlertDialog.Builder(ActualizarActivity.this).create();
        alertDialog_cargando.setMessage("Cargando datos...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        bindUI();
        traer_funciones_WebService();

        btn_descargar_archivo.setOnClickListener(this);
        btn_selecconar_archivo.setOnClickListener(this);
        btn_procesar_actualizacion.setOnClickListener(this);

        checkForPermission();

        spinnerTablas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tabla = spinnerTablas.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindUI() {
        spinnerTablas = (Spinner) findViewById(R.id.spinnerTablas);
        btn_descargar_archivo = (Button) findViewById(R.id.btn_descargar_archivo);
        btn_selecconar_archivo = (Button) findViewById(R.id.btn_selecconar_archivo);
        btn_procesar_actualizacion = (Button) findViewById(R.id.btn_procesar_actualizacion);
    }

    private void traer_funciones_WebService(){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_tablas_actualizacion.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                ArrayList<String> tablas = new ArrayList<>();
                String tabla;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){
                        tabla = jsonArray.getJSONObject(i).getString("Tables_in_u683416683_gyt");
                        tablas.add(tabla);
                    }
                    spinnerTablas.setAdapter(new ArrayAdapter<String>(ActualizarActivity.this, android.R.layout.simple_list_item_1, tablas));
                    alertDialog_cargando.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        request.add(stringRequest);
    }

    private void checkForPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_MEMORY);
        }
    }

    private boolean hasPermission(String permissionToCheck) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, permissionToCheck);
        return (permissionCheck == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_descargar_archivo:
                descargarArchivo(tabla);
                break;
            case R.id.btn_selecconar_archivo:
                if(hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    showFileChooser();
                }
                break;
            case R.id.btn_procesar_actualizacion:
                break;
        }
    }

    private void descargarArchivo(String tabla) {
        Uri uri = Uri.parse("https://golfyturf.com/feria_automovil/AppWebServices/descargar_archivo_actualizacion.php?tabla="+tabla+"&usuario="+Util.getId_usuario());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void showFileChooser() {

        //Intent intent = new Intent();
        //intent.setType("*/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(intent,"Selecione un archivo para subir.."),PICK_FILE_REQUEST);

        /*Intent i = new Intent(this, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, PICK_FILE_REQUEST);*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK) {

            /*File file = new File(data.getData().getPath());
            String path = "/storage/Download/"+data.getData().getLastPathSegment();
            uploadFile(path);*/




            /*List<Uri> files = Utils.getSelectedFilesFromResult(data);
            for (Uri uri: files) {
                File file = Utils.getFileForUri(uri);
                Toast.makeText(this, ""+file.getPath(), Toast.LENGTH_SHORT).show();
            }*/
        }
    }

    private void subirArchivo(String path) {
        String uploadId = UUID.randomUUID().toString();
        final String URL = "https://golfyturf.com/feria_automovil/AppWebServices/procesar_archivo_actualizacion.php";

        try {
            new MultipartUploadRequest(this, uploadId, URL)
                    .addFileToUpload(path, "picture")
                    .addParameter("usuario", Util.getId_usuario())
                    .addParameter("tabla", tabla)
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {

                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    })
                    .setNotificationConfig(new UploadNotificationConfig())
                    .startUpload();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ActualizarActivity.this,"Exception "+e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void uploadFile(String filename){
        try {

            FileInputStream fis = new FileInputStream(filename);
            HttpFileUploader htfu = new HttpFileUploader("https://golfyturf.com/feria_automovil/AppWebServices/procesar_archivo_actualizacion.php","noparamshere", filename);
            htfu.doStart(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(ActualizarActivity.this,"Exception "+e.toString(),Toast.LENGTH_LONG).show();
        }
    }

}
