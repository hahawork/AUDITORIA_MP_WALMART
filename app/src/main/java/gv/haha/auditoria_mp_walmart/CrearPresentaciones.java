package gv.haha.auditoria_mp_walmart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CrearPresentaciones extends AppCompatActivity {

    LinearLayout llContenedor;
    CardView cvSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_presentaciones);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        llContenedor = findViewById(R.id.llcontentSlide_CP);

        AgregarSeccion();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AgregarSeccion();

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void AgregarSeccion() {

        cvSlide = new CardView(this);

        LinearLayout.LayoutParams layoutParamsCV = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsCV.setMargins(5, 10, 5, 5);

        cvSlide.setLayoutParams(layoutParamsCV);
        cvSlide.setRadius(5);
        cvSlide.setPadding(5, 5, 5, 5);
        cvSlide.setMaxCardElevation(30);
        cvSlide.setMaxCardElevation(8);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout llhorizontal1 = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams llpImageView = new LinearLayout.LayoutParams(0, 100);
        llpImageView.weight = 1;
        llpImageView.setMargins(5,5,5,5);

        ImageView iv1 = new ImageView(this);
        iv1.setImageResource(R.drawable.bg_logo);
        iv1.setLayoutParams(llpImageView);

        ImageView iv2 = new ImageView(this);
        iv2.setImageResource(R.drawable.ic_download);
        iv2.setLayoutParams(llpImageView);

        llhorizontal1.addView(iv1);
        llhorizontal1.addView(iv2);

        LinearLayout llhorizontal2 = new LinearLayout(this);
        llhorizontal2.setOrientation(LinearLayout.HORIZONTAL);
        llhorizontal2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        ImageView iv3 = new ImageView(this);
        iv3.setImageResource(R.drawable.bg_logo);
        iv3.setLayoutParams(llpImageView);

        ImageView iv4 = new ImageView(this);
        iv4.setImageResource(R.drawable.ic_download);
        iv4.setLayoutParams(llpImageView);

        llhorizontal2.addView(iv3);
        llhorizontal2.addView(iv4);


        ll.addView(llhorizontal1);
        ll.addView(llhorizontal2);

        cvSlide.addView(ll);
        llContenedor.addView(cvSlide);

    }
}
