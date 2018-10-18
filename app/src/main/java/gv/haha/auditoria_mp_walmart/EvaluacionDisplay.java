package gv.haha.auditoria_mp_walmart;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class EvaluacionDisplay extends AppCompatActivity {

    BottomNavigationView navigationView;
    Fragment fNuevoReporte, fUltimReporte, fTodoslosReportes;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            try {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()) {
                    case R.id.navigation_nuevo:
                        transaction.replace(R.id.fragment_container, fNuevoReporte);
                        transaction.addToBackStack(null);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        transaction.commit();

                        return true;
                    case R.id.navigation_ultimo:
                        transaction.replace(R.id.fragment_container, fUltimReporte);
                        transaction.addToBackStack(null);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        transaction.commit();

                        return true;

                    case R.id.navigation_todos:
                        transaction.replace(R.id.fragment_container, fTodoslosReportes);
                        //transaction.addToBackStack(null);
                        transaction.commit();

                        return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluacion_display);

        iniciarComponentes();
    }

    private void iniciarComponentes() {

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fNuevoReporte = new FragmentNuevoReporte();
        fUltimReporte = new FragmentUltimoReporte();
        fTodoslosReportes=new FragmentTodosReportes();

        FragmentManager FM = getSupportFragmentManager();
        FragmentTransaction FT = FM.beginTransaction();
        FT.replace(R.id.fragment_container, fNuevoReporte);
        FT.commit();
    }
}
