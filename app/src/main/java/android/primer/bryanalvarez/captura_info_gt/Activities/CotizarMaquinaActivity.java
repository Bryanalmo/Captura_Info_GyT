package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Clientes_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Componente_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Contactos_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Cliente;
import android.primer.bryanalvarez.captura_info_gt.Models.Componente;
import android.primer.bryanalvarez.captura_info_gt.Models.Contacto;
import android.primer.bryanalvarez.captura_info_gt.Models.SubCotizacion;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CotizarMaquinaActivity extends AppCompatActivity {

    private AlertDialog alertDialog_cargando;
    private LinearLayout linear_layout_cliente_contacto;
    //private Spinner spinner_clientes;
    private Spinner spinner_contactos;
    private AutoCompleteTextView spinner_clientes;
    private RecyclerView listViewComponentes;
    private RecyclerView recycler_view_contactos;
    //private Maquina maquina;
    private TextView tv_total_cotizar_sin_iva_maquina;
    private TextView tv_total_cotizar_iva_maquina;
    private TextView tv_total_cotizar_maquina;
    private Button bt_cotizar_maquina;
    private ImageButton ib_crear_cliente;
    private ImageButton ib_crear_contacto;
    private Button ib_copiar_a_contactos;
    private Componente_Adapter componente_adapter;
    private Contactos_Adapter contactos_adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DividerItemDecoration itemDecoration;
    private ArrayList<Componente> componentes = new ArrayList<>();
    private ArrayList<Componente> componentes_select = new ArrayList<>();
    private ArrayList<Cliente> clientes;
    private ArrayList<Contacto> contactos;
    private ArrayList<Contacto> contactos_agregados = new ArrayList<>();
    private SubCotizacion subCotizacion;
    RequestQueue request;
    StringRequest stringRequest;
    String idCliente;
    String idContacto;
    String id_maquina;
    String editar_crear;
    long valor_final;
    long valor;
    long aumento_IVA;
    long precio_descuento=0;
    boolean clientesCargados = false;
    boolean contactosCargados = false;
    boolean componentesCargados = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotizar_maquina);

        id_maquina = getIntent().getExtras().getString("Id_maquina");
        editar_crear = getIntent().getExtras().getString("Crear_Editar");

        request = Volley.newRequestQueue(this);
        alertDialog_cargando = new AlertDialog.Builder(CotizarMaquinaActivity.this).create();
        alertDialog_cargando.setMessage("Cargando...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        bindUI();

        if (editar_crear.equals("Crear")){
            linear_layout_cliente_contacto.setVisibility(View.VISIBLE);
            bt_cotizar_maquina.setText("ENVIAR");
        }else if (editar_crear.equals("Editar")){
            linear_layout_cliente_contacto.setVisibility(View.GONE);
            bt_cotizar_maquina.setText("ACTUALIZAR");
            subCotizacion = Util.getSubCotizacion();
        }

        traer_componentes_WebService();

        bt_cotizar_maquina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editar_crear.equals("Crear")){
                    alertDialog_cargando = new AlertDialog.Builder(CotizarMaquinaActivity.this).create();
                    alertDialog_cargando.setMessage("¿Desea agregar más maquinas a la cotización?");
                    alertDialog_cargando.setCancelable(false);
                    alertDialog_cargando.setCanceledOnTouchOutside(false);
                    alertDialog_cargando.setButton(DialogInterface.BUTTON_POSITIVE, "Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(CotizarMaquinaActivity.this,MaquinasActivity.class);
                            startActivity(intent);
                        }
                    });
                    alertDialog_cargando.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showAlertDetallesCorreo();
                        }
                    });
                    alertDialog_cargando.show();
                    agregarSubCotizacion();
                    //Util.contactos_agregados = contactos_agregados;
                }else if (editar_crear.equals("Editar")){
                    alertDialog_cargando = new AlertDialog.Builder(CotizarMaquinaActivity.this).create();
                    alertDialog_cargando.setMessage("¿Desea actualizar la cotización?");
                    alertDialog_cargando.setCancelable(false);
                    alertDialog_cargando.setCanceledOnTouchOutside(false);
                    alertDialog_cargando.setButton(DialogInterface.BUTTON_POSITIVE, "Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            agregarSubCotizacion();
                            Intent intent = new Intent(CotizarMaquinaActivity.this,VerCotizacionActivity.class);
                            startActivity(intent);
                        }
                    });
                    alertDialog_cargando.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alertDialog_cargando.show();
                }

            }
        });

        ib_crear_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertCrearCliente("Crear cliente","Ingrese los datos del cliente");
            }
        });

        ib_crear_contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertCrearContacto("Crear contácto","Ingrese los datos del contácto");
            }
        });

        spinner_clientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < clientes.size(); i++) {
                    if (clientes.get(i).getNombre().equals(spinner_clientes.getText().toString())){
                        Util.setCliente(clientes.get(i));
                        idCliente = clientes.get(i).getId();
                        traer_contactos_WebService(spinner_contactos);
                    }
                }
            }
        });

        spinner_contactos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Contacto contactoSelected = (Contacto) spinner_contactos.getSelectedItem();
                //contactos.get(position).setAgregado(true);
                //contactos_agregados.add(contactoSelected);
                Util.setContacto(contactoSelected);
                idContacto = contactoSelected.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ib_copiar_a_contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactos == null){
                    Toast.makeText(CotizarMaquinaActivity.this, "Se debe seleccionar un cliente primero", Toast.LENGTH_SHORT).show();
                }else{
                    showAlertCopiarAContactos();
                }

            }
        });

        traer_clientes_WebService(spinner_clientes);

    }

    private void cargarCliente() {
        if (Util.getCotizaciones_maquinas().size()>0){
            for (int i = 0; i < clientes.size(); i++) {
                if (clientes.get(i).getNombre().equals(Util.getCliente().getNombre())){
                    spinner_clientes.setText(clientes.get(i).getNombre());
                    spinner_clientes.showDropDown();
                    //spinner_clientes.setSelection(i);
                    idCliente = clientes.get(i).getId();
                    traer_contactos_WebService(spinner_contactos);
                }
            }
        }

    }

    private void cargarContacto() {
        if (Util.getCotizaciones_maquinas().size()>0){
            for (int i = 0; i < contactos.size(); i++) {
                if (contactos.get(i).getId().equals(Util.getContacto().getId())){
                    spinner_contactos.setSelection(i);
                }
            }
            contactos_agregados = Util.contactos_agregados;
        }

    }

    private void bindUI() {
        linear_layout_cliente_contacto = (LinearLayout) findViewById(R.id.linear_layout_cliente_contacto);
        ib_crear_cliente = (ImageButton) findViewById(R.id.ib_crear_cliente);
        ib_crear_contacto = (ImageButton) findViewById(R.id.ib_crear_contacto);
        ib_copiar_a_contactos = (Button) findViewById(R.id.ib_copiar_a_contactos);
        bt_cotizar_maquina = (Button) findViewById(R.id.bt_cotizar_maquina);
        //spinner_clientes = (Spinner) findViewById(R.id.spinner_clientes);
        spinner_clientes = (AutoCompleteTextView) findViewById(R.id.spinner_clientes);
        spinner_contactos = (Spinner) findViewById(R.id.spinner_contactos);
        tv_total_cotizar_sin_iva_maquina = (TextView) findViewById(R.id.tv_total_cotizar_sin_iva_maquina);
        tv_total_cotizar_iva_maquina = (TextView) findViewById(R.id.tv_total_cotizar_iva_maquina);
        tv_total_cotizar_maquina = (TextView) findViewById(R.id.tv_total_cotizar_maquina);
        listViewComponentes = (RecyclerView) findViewById(R.id.listViewComponentes);
        listViewComponentes.setHasFixedSize(true);
        listViewComponentes.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(this);
        itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        listViewComponentes.setLayoutManager(mLayoutManager);
        listViewComponentes.addItemDecoration(itemDecoration);
    }

    private void comprobarAgregados(){
        componentes_select = new ArrayList<>();
        for (int i = 0; i < componentes.size() ; i++) {
            if(componentes.get(i).isAgregado()){
                componentes_select.add(componentes.get(i));
            }
        }
       // Toast.makeText(CotizarMaquinaActivity.this,componentes_select.size()+" ",Toast.LENGTH_SHORT).show();
        sumarPrecios();
    }

    private void agregarSubCotizacion() {
        String id_modelo_maquina = id_maquina;
        SubCotizacion subCotizacionAgregada;
        if (editar_crear.equals("Crear")){
            subCotizacionAgregada = new SubCotizacion(id_modelo_maquina,valor_final,componentes_select);
            Util.cotizaciones_maquinas.add(subCotizacionAgregada);
        }else if (editar_crear.equals("Editar")){
            subCotizacionAgregada = new SubCotizacion(subCotizacion.getId(),id_modelo_maquina,subCotizacion.getModelo_maquina(),
                    valor, aumento_IVA,valor_final,componentes_select,subCotizacion.getImagen());
            Util.getCotizacion_maquina().getSubCotizaciones().remove(subCotizacion);
            Util.getCotizacion_maquina().getSubCotizaciones().add(subCotizacionAgregada);
        }
    }

    private void sumarPrecios() {
        valor = 0;
        aumento_IVA = 0;
        valor_final = 0;
        for (int i=0; i<componentes_select.size(); i++){
            int cantidad = componentes_select.get(i).getCantidad();
            for (int j = 0; j < cantidad; j++) {
                if (componentes_select.get(i).getDescuento() > 0){
                    valor += componentes_select.get(i).getPrecio_descuento();
                    aumento_IVA += componentes_select.get(i).getValor_IVA_descuento();
                    valor_final +=  componentes_select.get(i).getPrecio_IVA_descuento();
                }else{
                    valor += componentes_select.get(i).getPrecio();
                    aumento_IVA += componentes_select.get(i).getValor_IVA();
                    valor_final +=  componentes_select.get(i).getPrecio_IVA();
                }
            }
        }
        tv_total_cotizar_sin_iva_maquina.setText("$ "+ NumberFormat.getInstance().format(valor));
        tv_total_cotizar_iva_maquina.setText("$ "+NumberFormat.getInstance().format(aumento_IVA));
        tv_total_cotizar_maquina.setText("$ "+NumberFormat.getInstance().format(valor_final));

    }

    private void showAlertDescuento(final Componente componente) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detalles del componente:");


        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_descuento, null);
        builder.setView(viewInflated);

        final TextView tv_total_cotizar_sin_iva_descuento = (TextView) viewInflated.findViewById(R.id.tv_total_cotizar_sin_iva_descuento);
        final TextView tv_total_cotizar_iva_descuento = (TextView) viewInflated.findViewById(R.id.tv_total_cotizar_iva_descuento);
        final TextView tv_total_cotizar_descuento = (TextView) viewInflated.findViewById(R.id.tv_total_cotizar_descuento);
        final EditText et_dialog_descuento = (EditText) viewInflated.findViewById(R.id.et_dialog_descuento);
        final EditText et_dialog_cantidad = (EditText) viewInflated.findViewById(R.id.et_dialog_cantidad);

        et_dialog_descuento.setText(""+componente.getDescuento());
        et_dialog_cantidad.setText(""+componente.getCantidad());
        if (componente.getDescuento() > 0){
            tv_total_cotizar_sin_iva_descuento.setText(""+NumberFormat.getInstance().format(componente.getPrecio_descuento()*componente.getCantidad()));
            tv_total_cotizar_iva_descuento.setText(""+NumberFormat.getInstance().format(componente.getValor_IVA_descuento()*componente.getCantidad()));
            tv_total_cotizar_descuento.setText(""+NumberFormat.getInstance().format(componente.getPrecio_IVA_descuento()*componente.getCantidad()));
        }else{
            tv_total_cotizar_sin_iva_descuento.setText(""+NumberFormat.getInstance().format(componente.getPrecio()*componente.getCantidad()));
            tv_total_cotizar_iva_descuento.setText(""+NumberFormat.getInstance().format(componente.getValor_IVA()*componente.getCantidad()));
            tv_total_cotizar_descuento.setText(""+NumberFormat.getInstance().format(componente.getPrecio_IVA()*componente.getCantidad()));
        }

        et_dialog_cantidad.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
              if (s.length() != 0 && Integer.parseInt(s.toString()) != 0) {
                  componente.setCantidad(Integer.parseInt(s.toString()));
                  precio_descuento = (componente.getPrecio_descuento())*componente.getCantidad();
                  tv_total_cotizar_sin_iva_descuento.setText("" + NumberFormat.getInstance().format(precio_descuento));
                  long iva_descuento = (long) (precio_descuento * componente.getIVA());
                  tv_total_cotizar_iva_descuento.setText("" + NumberFormat.getInstance().format(iva_descuento));
                  long total_descuento = precio_descuento + iva_descuento;
                  tv_total_cotizar_descuento.setText("" + NumberFormat.getInstance().format(total_descuento));
                  componente.setDescuento(componente.getDescuento());
                  componente.setPrecio_descuento(precio_descuento/componente.getCantidad());
                  componente.setValor_IVA_descuento(iva_descuento/componente.getCantidad());
                  componente.setPrecio_IVA_descuento(total_descuento/componente.getCantidad());
              } else {
                  componente.setCantidad(1);
                  tv_total_cotizar_sin_iva_descuento.setText("" + NumberFormat.getInstance().format(componente.getPrecio()*componente.getCantidad()));
                  tv_total_cotizar_iva_descuento.setText("" + NumberFormat.getInstance().format(componente.getValor_IVA()*componente.getCantidad()));
                  tv_total_cotizar_descuento.setText("" + NumberFormat.getInstance().format(componente.getPrecio_IVA()*componente.getCantidad()));
              }
          }

          @Override
          public void afterTextChanged(Editable s) {

                                                      }
        });


        et_dialog_descuento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && Integer.parseInt(s.toString()) != 0) {
                    precio_descuento = (componente.getPrecio() - Integer.parseInt(s.toString()))*componente.getCantidad();
                    tv_total_cotizar_sin_iva_descuento.setText("" + NumberFormat.getInstance().format(precio_descuento));
                    long iva_descuento = (long) (precio_descuento * componente.getIVA());
                    tv_total_cotizar_iva_descuento.setText("" + NumberFormat.getInstance().format(iva_descuento));
                    long total_descuento = precio_descuento + iva_descuento;
                    tv_total_cotizar_descuento.setText("" + NumberFormat.getInstance().format(total_descuento));
                    componente.setDescuento(Integer.parseInt(s.toString()));
                    componente.setPrecio_descuento(precio_descuento/componente.getCantidad());
                    componente.setValor_IVA_descuento(iva_descuento/componente.getCantidad());
                    componente.setPrecio_IVA_descuento(total_descuento/componente.getCantidad());

                } else {
                    tv_total_cotizar_sin_iva_descuento.setText("" + NumberFormat.getInstance().format(componente.getPrecio()*componente.getCantidad()));
                    tv_total_cotizar_iva_descuento.setText("" + NumberFormat.getInstance().format(componente.getValor_IVA()*componente.getCantidad()));
                    tv_total_cotizar_descuento.setText("" + NumberFormat.getInstance().format(componente.getPrecio_IVA()*componente.getCantidad()));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                comprobarAgregados();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void traer_clientes_WebService(final AutoCompleteTextView spinner_clientes) {


        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_clientes_SILO.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Cliente cliente = null;
                clientes = new ArrayList<>();
                String id;
                String nombre;
                try {

                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        id = jsonArray.getJSONObject(i).getString("Id");
                        nombre = jsonArray.getJSONObject(i).getString("Nombre_completo");

                        cliente = new Cliente(id,nombre);
                        clientes.add(cliente);

                    }
                    //alertDialog_cargando.dismiss();
                    Clientes_Adapter adapter_clientes = new Clientes_Adapter(CotizarMaquinaActivity.this,R.layout.autocomplete_item_cliente,R.id.lbl_name,clientes);
                    spinner_clientes.setAdapter(adapter_clientes);
                    //spinner_clientes.setAdapter(new ArrayAdapter<Cliente>(CotizarMaquinaActivity.this, android.R.layout.simple_list_item_1, clientes));
                    cargarCliente();
                    clientesCargados = true;
                    hideAlertDialog();
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

    private void traer_contactos_WebService(final Spinner spinner_contactos) {


        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_contactos.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Contacto contacto = null;
                contactos = new ArrayList<>();
                String id;
                String nombre;
                String apellido;
                String correo;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        id = jsonArray.getJSONObject(i).getString("Id");
                        nombre = jsonArray.getJSONObject(i).getString("Nombres");
                        apellido = jsonArray.getJSONObject(i).getString("Apellidos");
                        correo = jsonArray.getJSONObject(i).getString("Email");

                        contacto = new Contacto(id,nombre+" "+apellido,correo);
                        contactos.add(contacto);

                    }
                    //alertDialog_cargando.dismiss();
                    spinner_contactos.setAdapter(new ArrayAdapter<Contacto>(CotizarMaquinaActivity.this, android.R.layout.simple_list_item_1, contactos));
                    cargarContacto();
                    contactosCargados = true;
                    hideAlertDialog();
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
                parametros.put("Id_cliente", Util.getCliente().getId());
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
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");

                        Precio = (int) (Precio * Util.monedaActual);
                        valor_IVA = (long) (Precio*IVA);
                        precio_IVA = Precio + valor_IVA;
                        componente = new Componente(Id,Nombre,Precio,IVA,valor_IVA,precio_IVA);
                        componente.setAgregado(false);
                        componentes.add(componente);

                    }

                    componente_adapter = new Componente_Adapter(CotizarMaquinaActivity.this, componentes, R.layout.list_view_componente, new Componente_Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Componente componente, int position) {
                            for (int i = 0; i < componentes_select.size() ; i++) {
                                if (componente.getId().equals(componentes_select.get(i).getId())){
                                    showAlertDescuento(componentes_select.get(i));
                                }else{
                                    Toast.makeText(CotizarMaquinaActivity.this,"Debe agregar el componente primero",Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    }, new Componente_Adapter.OnButtonBorrarClickListener() {
                        @Override
                        public void onButtonBorrarClick(Componente componente, int position,boolean isChecked) {
                            if(isChecked){
                                componente.setAgregado(true);
                                //Toast.makeText(CotizarMaquinaActivity.this,isChecked+" ",Toast.LENGTH_SHORT).show();
                            }else{
                                componente.setAgregado(false);
                                //Toast.makeText(CotizarMaquinaActivity.this,isChecked+" ",Toast.LENGTH_SHORT).show();
                                componentes_select.remove(componentes.get(position));
                            }
                            comprobarAgregados();
                            componente_adapter.notifyDataSetChanged();
                        }
                    });
                    listViewComponentes.setAdapter(componente_adapter);
                    //alertDialog_cargando.dismiss();
                    componentes = comprobarComponentesCotizados(componentes);
                    componente_adapter.notifyDataSetChanged();
                    comprobarAgregados();
                    componentesCargados = true;
                    hideAlertDialog();
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
                parametros.put("Id_maquina", id_maquina);
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private void hideAlertDialog() {
        if(editar_crear.equals("Editar")){
            if (componentesCargados){
                alertDialog_cargando.dismiss();
            }
        }else{
            if (clientesCargados && componentesCargados){
                alertDialog_cargando.dismiss();
            }
        }

    }

    private ArrayList<Componente> comprobarComponentesCotizados(ArrayList<Componente> componentes) {
        if (editar_crear.equals("Editar")){
            for (int j = 0; j < componentes.size(); j++) {
                Componente compo = componentes.get(j);
                ArrayList<Componente> componentes_cotizados = Util.getSubCotizacion().getComponentes();
                for (int k = 0; k < componentes_cotizados.size() ; k++) {
                    Componente compo_cotizado = componentes_cotizados.get(k);
                    if (compo.getId().equals(compo_cotizado.getId())){
                        compo_cotizado.setAgregado(true);
                        componentes.set(j,compo_cotizado);
                    }
                }

            }
        }
        return componentes;
    }

    private void showAlertCrearCliente(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_crear_cliente, null);
        builder.setView(viewInflated);

        final EditText et_dialog_nombre_cliente = (EditText) viewInflated.findViewById(R.id.et_dialog_nombre_cliente);
        final EditText et_dialog_nit_cliente = (EditText) viewInflated.findViewById(R.id.et_dialog_nit_cliente);
        final EditText et_dialog_celular_cliente = (EditText) viewInflated.findViewById(R.id.et_dialog_celular_cliente);
        final EditText et_dialog_ciudad_cliente = (EditText) viewInflated.findViewById(R.id.et_dialog_ciudad_cliente);
        final Button bt_dialog_crear_cliente = (Button) viewInflated.findViewById(R.id.bt_dialog_crear_cliente);
        final CheckBox cb_tratamiento_datos= (CheckBox) viewInflated.findViewById(R.id.cb_tratamiento_datos);

        final AlertDialog dialog = builder.create();
        dialog.show();

        bt_dialog_crear_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = et_dialog_nombre_cliente.getText().toString();
                String nit = et_dialog_nit_cliente.getText().toString();
                String celular = et_dialog_celular_cliente.getText().toString();
                String ciudad = et_dialog_ciudad_cliente.getText().toString();
                if(cb_tratamiento_datos.isChecked()){
                            dialog.dismiss();
                            alertDialog_cargando.show();
                            crear_cliente_WebService(nombre,nit,celular,ciudad);
                }else{
                    Toast.makeText(CotizarMaquinaActivity.this,"Por favor apruebe el tratamiento de datos",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void crear_cliente_WebService(final String nombre, final String nit, final String celular, final String ciudad) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_cliente.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                Toast.makeText(CotizarMaquinaActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    jsonObject = new JSONObject(response);
                    if(jsonObject.getString("Success").equals("true")){
                        String id_cliente = jsonObject.getString("Id_cliente");
                        Toast.makeText(CotizarMaquinaActivity.this, "Cliente creado", Toast.LENGTH_SHORT).show();
                        clientes.add(new Cliente(id_cliente,nombre));
                    }else{
                        Toast.makeText(CotizarMaquinaActivity.this, "Error al crear el cliente", Toast.LENGTH_SHORT).show();
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
                parametros.put("Nombre_cliente", nombre);
                parametros.put("Nit_cliente", nit);
                parametros.put("Celular_cliente", celular);
                parametros.put("Ciudad_cliente", ciudad);
                return  parametros;
            }
        };
        request.add(stringRequest);

    }

    private void showAlertCrearContacto(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_crear_contacto, null);
        builder.setView(viewInflated);

        final EditText et_dialog_nombre_contacto = (EditText) viewInflated.findViewById(R.id.et_dialog_nombre_contacto);
        final EditText et_dialog_apellido_contacto = (EditText) viewInflated.findViewById(R.id.et_dialog_apellido_contacto);
        final EditText et_dialog_celular_contacto = (EditText) viewInflated.findViewById(R.id.et_dialog_celular_contacto);
        final EditText et_dialog_correo_contacto = (EditText) viewInflated.findViewById(R.id.et_dialog_correo_contacto);
        final EditText et_dialog_cargo_contacto = (EditText) viewInflated.findViewById(R.id.et_dialog_cargo_contacto);
        final Button bt_dialog_crear_contacto = (Button) viewInflated.findViewById(R.id.bt_dialog_crear_contacto);
        final CheckBox cb_tratamiento_datos= (CheckBox) viewInflated.findViewById(R.id.cb_tratamiento_datos);

        final AlertDialog dialog = builder.create();
        dialog.show();

        bt_dialog_crear_contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = et_dialog_nombre_contacto.getText().toString();
                String apellido = et_dialog_apellido_contacto.getText().toString();
                String celular = et_dialog_celular_contacto.getText().toString();
                String correo = et_dialog_correo_contacto.getText().toString();
                String cargo = et_dialog_cargo_contacto.getText().toString();
                if(cb_tratamiento_datos.isChecked()){
                    dialog.dismiss();
                    alertDialog_cargando.show();
                    crear_contacto_WebService(nombre,apellido,celular,correo,cargo);
                }else{
                    Toast.makeText(CotizarMaquinaActivity.this,"Por favor apruebe el tratamiento de datos",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void crear_contacto_WebService(final String nombre, final String apellido, final String celular, final String correo, final String cargo) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_contacto.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                try {
                    jsonObject = new JSONObject(response);
                    if(jsonObject.getString("Success").equals("true")){
                        String id_contacto = jsonObject.getString("Id_contacto");
                        Toast.makeText(CotizarMaquinaActivity.this, "Contacto creado", Toast.LENGTH_SHORT).show();
                        //contactos.add(new Contacto(id_contacto,nombre,correo));
                        traer_contactos_WebService(spinner_contactos);
                    }else{
                        Toast.makeText(CotizarMaquinaActivity.this, "Error al crear el contacto", Toast.LENGTH_SHORT).show();
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
                parametros.put("Id_cliente", idCliente);
                parametros.put("Nombre_contacto", nombre);
                parametros.put("Apellido_contacto", apellido);
                parametros.put("Celular_contacto", celular);
                parametros.put("Correo_contacto", correo);
                parametros.put("Cargo_contacto", cargo);
                return  parametros;
            }
        };
        request.add(stringRequest);

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
                alertDialog_cargando = new AlertDialog.Builder(CotizarMaquinaActivity.this).create();
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

    private void showAlertCopiarAContactos() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Copiar a contáctos");
        for (int j = 0; j <contactos.size() ; j++) {
            for (int k = 0; k <Util.contactos_agregados.size() ; k++) {
                if (contactos.get(j).getId().equals(Util.contactos_agregados.get(k).getId())){
                    contactos.get(j).setAgregado(true);
                    break;
                }else{
                    contactos.get(j).setAgregado(false);
                }
            }
        }

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_copiar_a_contactos, null);
        builder.setView(viewInflated);

        recycler_view_contactos = (RecyclerView) viewInflated.findViewById(R.id.recycler_view_contactos);
        recycler_view_contactos.setHasFixedSize(true);
        recycler_view_contactos.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(this);
        itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recycler_view_contactos.setLayoutManager(mLayoutManager);
        recycler_view_contactos.addItemDecoration(itemDecoration);

        contactos_adapter = new Contactos_Adapter(getApplicationContext(), contactos, R.layout.list_view_item_contacto, new Contactos_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contacto contacto, int position) {

            }
        }, new Contactos_Adapter.OnButtonBorrarClickListener() {
            @Override
            public void onButtonBorrarClick(Contacto contacto, int position, boolean isChecked) {
                if(isChecked){
                    contacto.setAgregado(true);
                    Util.contactos_agregados.add(contacto);
                }else{
                    contacto.setAgregado(false);
                    Util.contactos_agregados.remove(contactos.get(position));
                }
                //Toast.makeText(CotizarRepuestosActivity.this,isChecked+" " + contactos_agregados.size()+ " " + contacto.getCorreo() + " " + contacto.getId(),Toast.LENGTH_SHORT).show();
            }
        });

        recycler_view_contactos.setAdapter(contactos_adapter);
        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void crear_cotizacion_WebService(final String asunto, final String cuerpo){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_cotizacion_maquina.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                Toast.makeText(CotizarMaquinaActivity.this, response, Toast.LENGTH_LONG).show();
                try {
                    jsonObject = new JSONObject(response);
                    Util.cotizaciones_maquinas.clear();
                    Util.contactos_agregados.clear();
                    if(jsonObject.getString("Success").equals("true")){
                        Toast.makeText(CotizarMaquinaActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CotizarMaquinaActivity.this,CotizacionesMaquinariaActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(CotizarMaquinaActivity.this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Util.cotizaciones_maquinas.clear();
                    Util.contactos_agregados.clear();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Util.cotizaciones_maquinas.clear();
                    Util.contactos_agregados.clear();
                    Toast.makeText(CotizarMaquinaActivity.this, "Creación exitosa, un email será enviado en breve.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CotizarMaquinaActivity.this,CotizacionesMaquinariaActivity.class);
                    startActivity(intent);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                long valor_total = 0;
                parametros.put("Id_cliente", idCliente );
                parametros.put("Id_contacto", idContacto);
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        request.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        if (editar_crear.equals("Crear")){
            Intent intent = new Intent(CotizarMaquinaActivity.this,MaquinasActivity.class);
            startActivity(intent);
        }else if (editar_crear.equals("Editar")){
            Intent intent = new Intent(CotizarMaquinaActivity.this,VerCotizacionActivity.class);
            startActivity(intent);
        }

    }
}
