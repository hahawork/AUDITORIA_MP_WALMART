package gv.haha.auditoria_mp_walmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gv.haha.auditoria_mp_walmart.clases.BaseDatos;
import gv.haha.auditoria_mp_walmart.clases.Globales;
import gv.haha.auditoria_mp_walmart.clases.SQLHelper;
import gv.haha.auditoria_mp_walmart.clases.classCustomToast;
import gv.haha.auditoria_mp_walmart.clases.classWebService;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static gv.haha.auditoria_mp_walmart.clases.Variables.SETT_COD_USUARIO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.SETT_FECHA_ULTIM_BK_DB_CSV;
import static gv.haha.auditoria_mp_walmart.clases.Variables.SETT_NOMBRE_USUARIO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_EVAL_DISPLAY_ENC;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_NOMBRE_DISPLAY;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_PUNTOSDEVENTA;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_ENCABEZADO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.option_verificaactualizacion;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    Button btnRevisionpdv, btnEvaluardisplay;

    Globales G;
    BaseDatos baseDatos;

    SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        iniciarToolBar();

        verificarRegistro();

        ModificarBaseDatos();

        if (G.TieneConexion()) {

            verificaPuntosDeVentasEnDB();

            G.enviarRevisionPdvEncabezado();

            G.enviarEvaluacionDisplayEncabezado();

            obtenerListaDisplayOnline();
        }


        try {

            baseDatos.exportDB2CSV();
            G.BackupDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void iniciarToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setting = PreferenceManager.getDefaultSharedPreferences(this);
        baseDatos = new BaseDatos(this);
        G = new Globales(this);
    }

    private void iniciarComponente() {

        btnRevisionpdv = (Button) findViewById(R.id.btnRevisionpdv_MA);
        btnRevisionpdv.setOnClickListener(this);
        btnEvaluardisplay = (Button) findViewById(R.id.btnEvaluardisplay_MA);
        btnEvaluardisplay.setOnClickListener(this);

        /**
         * para verificar una nueva version de la aplicacion en el servidor
         */
        //si tiene conexion a internet
        if (G.TieneConexion()) {

            List<classWebService> arrParams = new ArrayList<>();
            arrParams.clear();
            arrParams.add(new classWebService("version", String.valueOf(G.getVersionApp())));
            arrParams.add(new classWebService("idapp", "8"));

            G.webServiceVerificaNuevaVersion(arrParams);

        }
    }

    /**
     * verifica si existe un usuario habilitado
     */
    private void verificarRegistro() {

        EnableRuntimePermission();

        try {

            if (setting.getString(SETT_NOMBRE_USUARIO, "").equals("") || setting.getInt(SETT_COD_USUARIO, 0) == 0) {
                startActivity(new Intent(this, Registro.class));
            } else {

                new classCustomToast(this).Toast("Hola " + setting.getString(SETT_NOMBRE_USUARIO, ""), R.drawable.ic_success);
                iniciarComponente();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void verificaPuntosDeVentasEnDB() {
        try {

            Cursor cursor = baseDatos.obtenerMaxRegistro(TBL_PUNTOSDEVENTA, "IdPdV");
            if (cursor.moveToFirst()) {
                G.VerificarNuevosPuntosguardados(false, cursor.getInt(cursor.getColumnIndex("IdPdV")));
            } else {
                G.VerificarNuevosPuntosguardados(true, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void obtenerListaDisplayOnline(){
        try {

            Cursor cursor = baseDatos.obtenerMaxRegistro(TBL_NOMBRE_DISPLAY, "idEnviado");
            if (cursor.moveToFirst()) {
                G.wsObtnerNombresDisplayOnline( cursor.getInt(cursor.getColumnIndex("idEnviado")));
            } else {
                G.wsObtnerNombresDisplayOnline(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void EnableRuntimePermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION,
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE,
                        GET_ACCOUNTS
                }, 110);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ajuste) {


        }
        if (id==R.id.nav_descargarDisplay){
            if(G.TieneConexion()) {
                obtenerListaDisplayOnline();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {

        if (view == btnRevisionpdv) {

            startActivity(new Intent(this, RevisionPdv.class));

        } else if (view == btnEvaluardisplay) {

            startActivity(new Intent(this, EvaluacionDisplay.class));
        }
    }

    private void ModificarBaseDatos() {
        //executa el oncreate de la clase de la base de datos
        new SQLHelper(this).onCreate(new SQLHelper(this).getWritableDatabase());

        //si no existe el campo
        if (!baseDatos.ExisteColumna(TBL_EVAL_DISPLAY_ENC, "FechReg")) {
            //agregar el nuevo campo
            baseDatos.AgregarColumna(TBL_EVAL_DISPLAY_ENC, "FechReg", "TIMESTAMP");
        }
        //si no existe el campo
        if (!baseDatos.ExisteColumna(TBL_EVAL_DISPLAY_ENC, "IdDislay")) {
            //agregar el nuevo campo
            baseDatos.AgregarColumna(TBL_EVAL_DISPLAY_ENC, "IdDislay", "integer");
        }
        //si no existe el campo
        if (!baseDatos.ExisteColumna(TBL_EVAL_DISPLAY_ENC, "EstadoEnviado")) {
            //agregar el nuevo campo
            baseDatos.AgregarColumna(TBL_EVAL_DISPLAY_ENC, "EstadoEnviado", "integer");
        }

        //si no existe el campo
        if (!baseDatos.ExisteColumna(TBL_EVAL_DISPLAY_ENC, "idEnviado")) {
            //agregar el nuevo campo
            baseDatos.AgregarColumna(TBL_EVAL_DISPLAY_ENC, "idEnviado", "integer");
        }

        //si no existe el campo
        if (!baseDatos.ExisteColumna(TBL_REPORTE_ENCABEZADO, "idEnviado")) {
            //agregar el nuevo campo
            baseDatos.AgregarColumna(TBL_REPORTE_ENCABEZADO, "idEnviado", "integer");
        }
    }

}
