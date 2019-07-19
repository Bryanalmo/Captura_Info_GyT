package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Accesorio_AutoComplete_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Componente_AutoComplete_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Maquinas_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio_Maquina;
import android.primer.bryanalvarez.captura_info_gt.Models.Componente;
import android.primer.bryanalvarez.captura_info_gt.Models.Funcion;
import android.primer.bryanalvarez.captura_info_gt.Models.Maquina;
import android.primer.bryanalvarez.captura_info_gt.Models.SubCotizacion;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MaquinasActivity extends AppCompatActivity {

    private RecyclerView gridViewMaquinas;
    private FrameLayout frame_layout_num_maquinas;
    private FloatingActionButton floatingActionButtonNumMaquinas;
    private FloatingActionButton fab_ver_cotizaciones;
    private FloatingActionButton fab_cotizar_best_product;
    private TextView tv_num_maquinas;
    private Maquinas_Adapter adapter_maquinas;
    private ArrayList<Maquina> maquinas = new ArrayList<>();
    private AlertDialog alertDialog_cargando;
    private Spinner spinner_funciones;
    private int myLastVisiblePos;
    private RecyclerView.LayoutManager mLayoutManager;
    private DividerItemDecoration itemDecoration;

    private ArrayList<Componente> componentes = new ArrayList<>();
    private ArrayList<Accesorio_Maquina> accesorios = new ArrayList<>();

    private String idComponente_Accesorio="";
    String enviar_guardar = "";

    RequestQueue request;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maquinas);

        request = Volley.newRequestQueue(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        frame_layout_num_maquinas = (FrameLayout) findViewById(R.id.frame_layout_num_maquinas);
        floatingActionButtonNumMaquinas = (FloatingActionButton) findViewById(R.id.floatingActionButtonNumMaquinas);
        fab_ver_cotizaciones = (FloatingActionButton) findViewById(R.id.fab_ver_cotizaciones);
        fab_cotizar_best_product = (FloatingActionButton) findViewById(R.id.fab_cotizar_best_product);
        tv_num_maquinas = (TextView) findViewById(R.id.tv_num_maquinas);
        spinner_funciones = (Spinner) findViewById(R.id.spinner_funciones);
        gridViewMaquinas = (RecyclerView) findViewById(R.id.gridViewMaquinas);

        gridViewMaquinas.setHasFixedSize(true);
        gridViewMaquinas.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new GridLayoutManager(this,2);
        gridViewMaquinas.setLayoutManager(mLayoutManager);

        floatingActionButtonNumMaquinas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog_cargando = new AlertDialog.Builder(MaquinasActivity.this).create();
                alertDialog_cargando.setMessage("¿Desea enviar ahora la cotización?");
                alertDialog_cargando.setButton(DialogInterface.BUTTON_POSITIVE, "Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmarEnvio();
                    }
                });
                alertDialog_cargando.setButton(DialogInterface.BUTTON_NEGATIVE, "Volver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog_cargando.show();
            }
        });


        fab_cotizar_best_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findBestProduct();
            }
        });

        fab_ver_cotizaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaquinasActivity.this,CotizacionesMaquinariaActivity.class);
                startActivity(intent);
            }
        });
        traer_funciones_WebService(spinner_funciones);
        spinner_funciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                alertDialog_cargando = new AlertDialog.Builder(MaquinasActivity.this).create();
                alertDialog_cargando.setMessage("Cargando...");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();
                Funcion funcion = (Funcion) spinner_funciones.getSelectedItem();
                traer_maquinas_WebService(funcion.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        registerForContextMenu(gridViewMaquinas);
        hideShowNumMaquinas();
        setHideShowFABs();
    }

    private void findBestProduct() {
        for (int i = 0; i < maquinas.size(); i++) {
            if(maquinas.get(i).isBest_product()){
                Intent intent = new Intent(MaquinasActivity.this, CotizarMaquinaActivity.class);
                Util.setMaquina(maquinas.get(i));
                intent.putExtra("Id_maquina", maquinas.get(i).getId());
                intent.putExtra("Crear_Editar", "Crear_Best_product");
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opciones_maquinas, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_maquinas_crear:
                Intent intent = new Intent(MaquinasActivity.this,CrearActualizarMaquinaActivity.class);
                intent.putExtra("editar_crear","crear");
                startActivity(intent);
                return true;
            case R.id.menu_maquinas_filtro:
                Intent intent2 = new Intent(MaquinasActivity.this,ModificarFiltroActivity.class);
                startActivity(intent2);
                return true;
            case R.id.menu_maquinas_crear_componente:
                showDialogCrearComponente();
                return true;
            case R.id.menu_maquinas_crear_accesorio:
                showDialogCrearAccesorio();
                return true;
            case R.id.menu_maquinas_modificar_precios:
                showDialogModificarPrecios();
                return true;
            default:return super.onOptionsItemSelected(item);
        }

    }

    private void showDialogModificarPrecios() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Modificar precios");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_modificar_precios, null);
        builder.setView(viewInflated);

        final EditText et_modificar_precio = (EditText) viewInflated.findViewById(R.id.et_modificar_precio);
        final AutoCompleteTextView actv_nombre = (AutoCompleteTextView) viewInflated.findViewById(R.id.actv_nombre);
        final RadioButton radioButton_componentes = (RadioButton) viewInflated.findViewById(R.id.radioButton_componentes);
        final RadioButton radioButton_accesorios = (RadioButton) viewInflated.findViewById(R.id.radioButton_accesorios);

        componentes.clear();
        accesorios.clear();

        traer_componentes_WebService();
        traer_accesorios_WebService();

        radioButton_componentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Componente_AutoComplete_Adapter adapter_clientes = new Componente_AutoComplete_Adapter(MaquinasActivity.this,R.layout.autocomplete_item_cliente,R.id.lbl_name,componentes);
                actv_nombre.setAdapter(adapter_clientes);
            }
        });

        radioButton_accesorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Accesorio_AutoComplete_Adapter adapter_clientes = new Accesorio_AutoComplete_Adapter(MaquinasActivity.this,R.layout.autocomplete_item_cliente,R.id.lbl_name,accesorios);
                actv_nombre.setAdapter(adapter_clientes);
            }
        });

        actv_nombre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (radioButton_componentes.isChecked()){
                    for (int i = 0; i < componentes.size(); i++) {
                        if (componentes.get(i).getNombre().equals(actv_nombre.getText().toString())){
                            et_modificar_precio.setText(componentes.get(i).getPrecio()+"");
                            idComponente_Accesorio = componentes.get(i).getId();
                        }
                    }
                }else if (radioButton_accesorios.isChecked()){
                    for (int i = 0; i < accesorios.size(); i++) {
                        if (accesorios.get(i).getNombre().equals(actv_nombre.getText().toString())){
                            et_modificar_precio.setText(accesorios.get(i).getPrecio()+"");
                            idComponente_Accesorio = componentes.get(i).getId();
                        }
                    }
                }
            }
        });

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String precio = et_modificar_precio.getText().toString();
                String componente_accesorio = "";
                if (radioButton_componentes.isChecked()){
                    componente_accesorio = "componente";
                }else if (radioButton_accesorios.isChecked()){
                    componente_accesorio = "accesorio";
                }

                alertDialog_cargando = new AlertDialog.Builder(MaquinasActivity.this).create();
                alertDialog_cargando.setMessage("Enviando datos...");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();

                modificar_precios_WebService(precio,componente_accesorio);
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogCrearAccesorio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Crear accesorio");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_crear_accesorio, null);
        builder.setView(viewInflated);

        final EditText et_dialog_referencia_accesorio = (EditText) viewInflated.findViewById(R.id.et_dialog_referencia_accesorio);
        final EditText et_dialog_descripcion_accesorio = (EditText) viewInflated.findViewById(R.id.et_dialog_descripcion_accesorio);
        final EditText et_dialog_precio_accesorio = (EditText) viewInflated.findViewById(R.id.et_dialog_precio_accesorio);
        final EditText et_dialog_IVA_accesorio = (EditText) viewInflated.findViewById(R.id.et_dialog_IVA_accesorio);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String referencia = et_dialog_referencia_accesorio.getText().toString();
                String descripcion = et_dialog_descripcion_accesorio.getText().toString();
                String precio = et_dialog_precio_accesorio.getText().toString();
                String IVA = et_dialog_IVA_accesorio.getText().toString();

                alertDialog_cargando = new AlertDialog.Builder(MaquinasActivity.this).create();
                alertDialog_cargando.setMessage("Enviando datos...");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();

                crear_accesorio_WebService(referencia,descripcion,precio,IVA);
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogCrearComponente() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Crear componente");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_crear_componente, null);
        builder.setView(viewInflated);

        final EditText et_dialog_referencia_componente = (EditText) viewInflated.findViewById(R.id.et_dialog_referencia_componente);
        final EditText et_dialog_nombre_componente = (EditText) viewInflated.findViewById(R.id.et_dialog_nombre_componente);
        final EditText et_dialog_descripcion_componente = (EditText) viewInflated.findViewById(R.id.et_dialog_descripcion_componente);
        final EditText et_dialog_precio_componente = (EditText) viewInflated.findViewById(R.id.et_dialog_precio_componente);
        final EditText et_dialog_IVA_componente = (EditText) viewInflated.findViewById(R.id.et_dialog_IVA_componente);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String referencia = et_dialog_referencia_componente.getText().toString();
                String nombre = et_dialog_nombre_componente.getText().toString();
                String descripcion = et_dialog_descripcion_componente.getText().toString();
                String precio = et_dialog_precio_componente.getText().toString();
                String IVA = et_dialog_IVA_componente.getText().toString();

                alertDialog_cargando = new AlertDialog.Builder(MaquinasActivity.this).create();
                alertDialog_cargando.setMessage("Enviando datos...");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();

                crear_componente_WebService(referencia,nombre,descripcion,precio,IVA);
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void modificar_precios_WebService(final String precio, final String componente_accesorio) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/actualizar_precio_componente_accesorio.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                try {
                    jsonObject = new JSONObject(response);
                    Util.cotizaciones_maquinas.clear();
                    componentes.clear();
                    accesorios.clear();
                    if(jsonObject.getString("Success").equals("true")){
                        Toast.makeText(MaquinasActivity.this, "Actualización exitosa", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MaquinasActivity.this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Toast.makeText(MaquinasActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("Id", idComponente_Accesorio);
                parametros.put("Precio", precio);
                parametros.put("Componente_Accesorio", componente_accesorio);

                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.add(stringRequest);
    }

    private void crear_componente_WebService(final String referencia, final String nombre, final String descripcion, final String precio, final String iva) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_componente.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                try {
                    jsonObject = new JSONObject(response);
                    Util.cotizaciones_maquinas.clear();
                    if(jsonObject.getString("Success").equals("true")){
                        Toast.makeText(MaquinasActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MaquinasActivity.this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Toast.makeText(MaquinasActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("Referencia", referencia);
                parametros.put("Nombre", nombre);
                parametros.put("Descripcion", descripcion);
                parametros.put("Precio", precio);
                parametros.put("IVA", iva);

                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.add(stringRequest);
    }

    private void crear_accesorio_WebService(final String referencia,  final String descripcion, final String precio, final String iva) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_accesorio_maquina.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                try {
                    jsonObject = new JSONObject(response);
                    Util.cotizaciones_maquinas.clear();
                    if(jsonObject.getString("Success").equals("true")){
                        Toast.makeText(MaquinasActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MaquinasActivity.this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Toast.makeText(MaquinasActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("Referencia", referencia);
                parametros.put("Descripcion", descripcion);
                parametros.put("Precio", precio);
                parametros.put("IVA", iva);

                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.add(stringRequest);
    }

    private void setHideShowFABs() {
        gridViewMaquinas.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    floatingActionButtonNumMaquinas.hide();
                    fab_ver_cotizaciones.hide();
                }else if (dy < 0){
                    floatingActionButtonNumMaquinas.show();
                    fab_ver_cotizaciones.show();
                }
            }
        });
    }

    private void hideShowNumMaquinas() {
        if (Util.cotizaciones_maquinas.size() == 0){
            frame_layout_num_maquinas.setVisibility(View.GONE);
        }else{
            frame_layout_num_maquinas.setVisibility(View.VISIBLE);
            tv_num_maquinas.setText(Util.cotizaciones_maquinas.size()+"");
        }
    }

    private void traer_maquinas_WebService(final String funcion) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_maquinas.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Maquina maquina= null;
                ArrayList<Maquina> maquinas_WS = new ArrayList<>();
                //Toast.makeText(MaquinasActivity.this, response, Toast.LENGTH_SHORT).show();
                String Id;
                String Modelo_maquina;
                String Referencia;
                String Descripcion = "";
                int Precio;
                int Precio_dolares;
                double IVA;
                String Link;
                String Informacion_tecnica;
                String Funcion;
                String Tipo_motor;
                String Marca_motor;
                String Marca;
                String Imagen_equipo;
                long Precio_IVA;
                long Aumento_IVA;
                boolean Best_product;
                double Descuento;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id = jsonArray.getJSONObject(i).getString("Id");
                        Referencia = jsonArray.getJSONObject(i).getString("Referencia");
                        Descripcion = jsonArray.getJSONObject(i).getString("Descripcion");
                        Modelo_maquina = jsonArray.getJSONObject(i).getString("Modelo_maquina");
                        Precio = jsonArray.getJSONObject(i).getInt("Precio");
                        Precio_dolares = jsonArray.getJSONObject(i).getInt("Precio");
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");
                        Link = jsonArray.getJSONObject(i).getString("Link");
                        Informacion_tecnica = jsonArray.getJSONObject(i).getString("Informacion_tecnica");
                        Funcion = jsonArray.getJSONObject(i).getString("Funcion");
                        Tipo_motor = jsonArray.getJSONObject(i).getString("Tipo_motor");
                        Marca_motor = jsonArray.getJSONObject(i).getString("Marca_motor");
                        Marca = jsonArray.getJSONObject(i).getString("Marca");
                        Imagen_equipo = jsonArray.getJSONObject(i).getString("Imagen_equipo");
                        Precio = (int) (Precio * Util.monedaActual);
                        Aumento_IVA = (long) (Precio*IVA);
                        Precio_IVA = (long) (Precio + (Aumento_IVA));
                        Best_product = jsonArray.getJSONObject(i).getBoolean("Best_product");
                        Descuento = jsonArray.getJSONObject(i).getDouble("Descuento");

                        maquina = new Maquina();

                        maquina.setId(Id);
                        maquina.setReferencia(Referencia);
                        maquina.setDescripcion(Descripcion);
                        maquina.setModelo_maquina(Modelo_maquina);
                        maquina.setPrecio(Precio);
                        maquina.setIVA(IVA);
                        maquina.setPrecio_IVA(Precio_IVA);
                        maquina.setAumento_IVA(Aumento_IVA);
                        maquina.setLink(Link);
                        maquina.setInformacion_tecnica(Informacion_tecnica);
                        maquina.setFuncion(Funcion);
                        maquina.setTipo_motor(Tipo_motor);
                        maquina.setMarca_motor(Marca_motor);
                        maquina.setMarca(Marca);
                        maquina.setImagen_equipo(Imagen_equipo);
                        maquina.setBest_product(Best_product);
                        maquina.setDescuento(Descuento);
                        maquina.setPrecio_dolares(Precio_dolares);

                        maquinas_WS.add(maquina);
                    }
                    maquinas_WS = bindMaquinasAgregadas(maquinas_WS);
                    adapter_maquinas = new Maquinas_Adapter(MaquinasActivity.this, maquinas_WS, R.layout.list_view_item_maquinas, new Maquinas_Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Maquina maquina, int position) {
                            Intent intent = new Intent(MaquinasActivity.this, CotizarMaquinaActivity.class);
                            Util.setMaquina(maquinas.get(position));
                            intent.putExtra("Id_maquina", maquinas.get(position).getId());
                            intent.putExtra("Crear_Editar", "Crear");
                            startActivity(intent);
                        }
                    }, new Maquinas_Adapter.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(Maquina maquina, final int position, PopupMenu popupMenu) {
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.editar_maquina:
                                            Intent intent = new Intent(MaquinasActivity.this,CrearActualizarMaquinaActivity.class);
                                            intent.putExtra("editar_crear","editar");
                                            Util.setMaquina(maquinas.get(position));
                                            startActivity(intent);
                                            return true;
                                        default:
                                            return false;
                                    }
                                }
                            });
                            popupMenu.show();
                        }
                    });
                    gridViewMaquinas.setAdapter(adapter_maquinas);
                    adapter_maquinas.notifyDataSetChanged();
                    maquinas=maquinas_WS;
                    alertDialog_cargando.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MaquinasActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("Funcion", funcion);
                return  parametros;
            }
        };
        request.add(stringRequest);
    }


    private void traer_componentes_WebService() {


        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_componentes.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Componente componente= null;
                String Id;
                String Nombre;
                String Modelo_maquina;
                long Precio;
                double IVA;
                long valor_IVA;
                long precio_IVA;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id = jsonArray.getJSONObject(i).getString("Id");
                        Nombre = jsonArray.getJSONObject(i).getString("Nombre");
                        Precio = jsonArray.getJSONObject(i).getLong("Precio");
                        Precio = (int) (Precio);
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");
                        valor_IVA = (long) (Precio*IVA);
                        precio_IVA = Precio + valor_IVA;
                        componente = new Componente(Id,Nombre,Precio,IVA,valor_IVA,precio_IVA);
                        componente.setAgregado(false);
                        componentes.add(componente);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("Id_maquina", "0");
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private void traer_accesorios_WebService() {


        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_accesorios_maquinaria.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Accesorio_Maquina accesorio_maquina= null;
                String Id;
                String Nombre;
                long Precio;
                double IVA;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id = jsonArray.getJSONObject(i).getString("Id");
                        Nombre = jsonArray.getJSONObject(i).getString("Descripcion");
                        Precio = jsonArray.getJSONObject(i).getLong("Precio");
                        Precio = (int) (Precio);
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");

                        accesorio_maquina= new Accesorio_Maquina(Id,Nombre,Precio,IVA);
                        accesorio_maquina.setAgregado(false);
                        accesorios.add(accesorio_maquina);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("Id_maquina", "0");
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private ArrayList<Maquina> bindMaquinasAgregadas(ArrayList<Maquina> maquinas_WS) {
        int count = 0;
        String maquinas = "";
        if (Util.cotizaciones_maquinas.size()>0){
            for (int i = 0; i <maquinas_WS.size() ; i++) {
                Maquina maquina = maquinas_WS.get(i);
                for (int j = 0; j < Util.cotizaciones_maquinas.size(); j++) {
                    SubCotizacion subCotizacion = Util.getCotizaciones_maquinas().get(j);
                    if(maquinas_WS.get(i).getId().equals(subCotizacion.getId_modelo_maquina())){
                        maquinas_WS.get(i).setAgregado(true);
                        break;
                    }else{
                        maquinas_WS.get(i).setAgregado(false);
                    }
                }
            }
        }
        return maquinas_WS;
    }

    private void traer_funciones_WebService(final Spinner spinner){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_funciones.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Funcion funcion = null;
                ArrayList<Funcion> funciones = new ArrayList<>();
                funciones.add(new Funcion("0","Todas"));
                String id;
                String desc_funcion;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        id = jsonArray.getJSONObject(i).getString("Id");
                        desc_funcion = jsonArray.getJSONObject(i).getString("Funcion");

                        funcion = new Funcion(id,desc_funcion);
                        funciones.add(funcion);

                    }
                    spinner.setAdapter(new ArrayAdapter<Funcion>(MaquinasActivity.this, android.R.layout.simple_list_item_1, funciones));

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

    private void confirmarEnvio(){
        AlertDialog alertDialog = new AlertDialog.Builder(MaquinasActivity.this).create();
        alertDialog.setMessage("¿Desea solo guardar la cotización o enviarla ahora?");
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enviar_guardar = "2";
                showAlertDetallesCorreo();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enviar_guardar = "1";
                crear_cotizacion_WebService("","");
                alertDialog_cargando = new AlertDialog.Builder(MaquinasActivity.this).create();
                alertDialog_cargando.setMessage("Guardando datos");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();
            }
        });
        alertDialog.show();

    }

    private void showAlertDetallesCorreo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Detalles de correo electronico");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_detalles_correo, null);
        builder.setView(viewInflated);

        final RadioGroup radioGroup  = (RadioGroup) viewInflated.findViewById(R.id.radioGroup);
        final TextView textView  = (TextView) viewInflated.findViewById(R.id.textView59);
        final EditText et_asunto_correo = (EditText) viewInflated.findViewById(R.id.et_asunto_correo);
        final EditText et_cuerpo_correo = (EditText) viewInflated.findViewById(R.id.et_cuerpo_correo);

        radioGroup.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        cargarAsuntoyCuerpoCorreo(et_asunto_correo,et_cuerpo_correo);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String asunto = et_asunto_correo.getText().toString();
                String cuerpo = et_cuerpo_correo.getText().toString();
                //Toast.makeText(CotizarRepuestosActivity.this, formaPago, Toast.LENGTH_SHORT).show();
                crear_cotizacion_WebService(asunto,cuerpo);
                alertDialog_cargando = new AlertDialog.Builder(MaquinasActivity.this).create();
                alertDialog_cargando.setMessage("Enviando datos");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cargarAsuntoyCuerpoCorreo(EditText et_asunto_correo, EditText et_cuerpo_correo){

        String cuerpo = "En Golf & Turf SAS, distribuidores autorizados para Colombia de Jacobsen, E-Z-GO, Ventrac, entre otras reconocidas marcas, agradecemos la oportunidad de presentarle esta oferta, esperando que se atractiva para usted y que provea una solución integral a sus necesidades. Estaremos atentos a resolver cualquier inquietud que pueda surgir de la misma. A continuación presentaremos los medios a través de los cuales puede comunicarse con nosotros: ";
        et_cuerpo_correo.setText(cuerpo);
        String asunto = "Propuesta Maquinaria G&T";
        et_asunto_correo.setText(asunto);
    }

    private void crear_cotizacion_WebService(final String asunto, final String cuerpo){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_cotizacion_maquina.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                try {
                    jsonObject = new JSONObject(response);
                    Util.cotizaciones_maquinas.clear();
                    if(jsonObject.getString("Success").equals("true")){
                        Toast.makeText(MaquinasActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MaquinasActivity.this,CotizacionesMaquinariaActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MaquinasActivity.this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Util.cotizaciones_maquinas.clear();
                    Toast.makeText(MaquinasActivity.this, "Creación exitosa, un email será enviado en breve.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MaquinasActivity.this,CotizacionesMaquinariaActivity.class);
                    startActivity(intent);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                long valor_total = 0;
                parametros.put("Id_cliente", Util.getCliente().getId());
                parametros.put("Id_contacto", Util.getContacto().getId());
                parametros.put("Enviar_guardar", enviar_guardar);
                parametros.put("Numero_cot", " ");
                parametros.put("Id_comercial", Util.getId_usuario() );
                parametros.put("Asunto", asunto );
                parametros.put("Cuerpo", cuerpo );
                ArrayList<SubCotizacion> subCotizaciones = Util.cotizaciones_maquinas;
                parametros.put("Num_sub_cotizaciones", subCotizaciones.size()+ "" );
                parametros.put("Moneda", Util.monedaActual+"");
                for (int i=0; i<subCotizaciones.size(); i++){
                    SubCotizacion subCotizacion = subCotizaciones.get(i);
                    parametros.put("Id_modelo_maquina"+i, subCotizacion.getId_modelo_maquina() );
                    parametros.put("Valor"+i, subCotizacion.getValor()+"" );
                    valor_total += subCotizacion.getValor();
                    ArrayList<Componente> componentes = subCotizacion.getComponentes();
                    parametros.put("Num_componentes"+i, componentes.size()+ "" );
                    for (int j = 0; j <componentes.size() ; j++) {
                        Componente componente = componentes.get(j);
                        parametros.put("Id_componente"+i+j,componente.getId());
                        parametros.put("Nombre"+i+j,componente.getNombre());
                        parametros.put("Precio"+i+j,componente.getPrecio()+"");
                        parametros.put("IVA"+i+j,componente.getIVA()+"");
                        parametros.put("Valor_IVA"+i+j,componente.getValor_IVA() + "");
                        parametros.put("Precio_IVA"+i+j,componente.getPrecio_IVA() + "");
                        parametros.put("Descuento"+i+j,componente.getDescuento() + "");
                        parametros.put("Precio_descuento"+i+j,componente.getPrecio_descuento() + "");
                        parametros.put("Valor_IVA_descuento"+i+j,componente.getValor_IVA_descuento() + "");
                        parametros.put("Precio_IVA_descuento"+i+j,componente.getPrecio_IVA_descuento()+ "");
                        parametros.put("Cantidad"+i+j,componente.getCantidad()+ "");
                    }
                }
                parametros.put("Cantidad_contactos", Util.contactos_agregados.size()+ "" );
                for (int j = 0; j <Util.contactos_agregados.size() ; j++) {
                    parametros.put("Id_contacto"+j, Util.contactos_agregados.get(j).getId());
                    parametros.put("Correo_contacto"+j, Util.contactos_agregados.get(j).getCorreo());
                    parametros.put("Nombre_contacto"+j, Util.contactos_agregados.get(j).getNombre());
                    //s +=  contactos_agregados.get(j).getId() + " "+contactos_agregados.get(j).getCorreo() + "\n";
                }
                parametros.put("Valor_total",valor_total+"");

                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.add(stringRequest);
    }


    @Override
    public void onBackPressed() {
        alertDialog_cargando = new AlertDialog.Builder(MaquinasActivity.this).create();
        alertDialog_cargando.setTitle("¿Está seguro que quiere salir?");
        alertDialog_cargando.setMessage("Si presiona continuar se perderan los datos de las cotizaciones que aún no ha enviado ");
        alertDialog_cargando.setButton(DialogInterface.BUTTON_POSITIVE, "Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MaquinasActivity.this,MenuActivity.class);
                startActivity(intent);
            }
        });
        alertDialog_cargando.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog_cargando.show();
    }
}
