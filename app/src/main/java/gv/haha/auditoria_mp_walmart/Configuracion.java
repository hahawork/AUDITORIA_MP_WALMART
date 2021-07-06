package gv.haha.auditoria_mp_walmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import gv.haha.auditoria_mp_walmart.clases.Globales;
import gv.haha.auditoria_mp_walmart.clases.Variables;
import gv.haha.auditoria_mp_walmart.clases.classCustomToast;

public class Configuracion extends AppCompatActivity implements View.OnClickListener {

    Button btnDescPDV, btnDescDisplay, btnAplicacion;
    Globales G;
    SharedPreferences setting;

    SeekBar sbTamanFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iniciarControles();

    }

    private void iniciarControles() {

        setting = PreferenceManager.getDefaultSharedPreferences(this);
        G = new Globales(this);
        btnDescDisplay = findViewById(R.id.btnDescargDisp_Cnf);
        btnDescPDV = findViewById(R.id.btnDescargPDV_Cnf);
        btnAplicacion = findViewById(R.id.btnDescargAPP_Cnf);
        sbTamanFoto = findViewById(R.id.sbTamanFotos_Cnf);
        sbTamanFoto.setProgress(setting.getInt(Variables.SETT_FOTO_SIZE_KEY,3));


        btnDescPDV.setOnClickListener(this);
        btnDescDisplay.setOnClickListener(this);
        btnAplicacion.setOnClickListener(this);

        sbTamanFoto.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView) findViewById(R.id.tvTamanFoto_Cnf)).setText("Tamaño maximo de las fotos " + progress+"/"+seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == btnDescPDV) {
            verificaPuntosDeVentasEnDB();
        }

        if (v == btnDescDisplay) {
            obtenerListaDisplayOnline();
        }

        if (v == btnAplicacion) {
            startActivity(new Intent(this, DescargarActualizacionWebView.class));
        }
    }

    private void verificaPuntosDeVentasEnDB() {
        try {

            G.VerificarNuevosPuntosguardados(true, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void obtenerListaDisplayOnline() {
        try {
            if (G.TieneConexion()) {
                G.wsObtnerNombresDisplayOnline(0);
            } else {
                new classCustomToast(this).Show_ToastError("No tienes conexión a internet para actualizar la lista.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
