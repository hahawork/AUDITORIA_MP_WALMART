package gv.haha.auditoria_mp_walmart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import gv.haha.auditoria_mp_walmart.clases.BaseDatos;

public class VerDatosExistentes extends AppCompatActivity {

    BaseDatos baseDatos;
    LinearLayout llDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_datos_existentes);


        iniciarInstancias();

    }

    private void iniciarInstancias() {
        baseDatos = new BaseDatos(this);
        llDatos = (LinearLayout) findViewById(R.id.llDatos_VDE);
    }


}
