package gv.haha.auditoria_mp_walmart;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import gv.haha.auditoria_mp_walmart.clases.AsyncTaskComplete;
import gv.haha.auditoria_mp_walmart.clases.Globales;
import gv.haha.auditoria_mp_walmart.clases.classCustomToast;
import gv.haha.auditoria_mp_walmart.clases.classWebService;

import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static gv.haha.auditoria_mp_walmart.clases.Variables.SETT_CLIENTE_ASIGNADO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.SETT_COD_USUARIO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.SETT_NOMBRE_USUARIO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_REGISTRO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.option_registro;

public class Registro extends AppCompatActivity implements View.OnClickListener, AsyncTaskComplete {

    private static EditText emailid, telefono;
    private static Button loginButton;
    private static LinearLayout loginLayout;
    private static Animation shakeAnimation;
    Globales MG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        iniciarToolBar();

        MG = new Globales(this);
        emailid = (EditText) findViewById(R.id.login_emailid);
        telefono = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.loginBtn);
        loginLayout = (LinearLayout) findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(this,
                R.anim.shake);

        EnableRuntimePermission();
        emailid.setText(getUserEmail());

        setListeners();

    }

    private void iniciarToolBar() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                checkValidation();
                break;
        }

    }

    // Check Validation before login
    private void checkValidation() {

        boolean isOK = true;
        String mensaje = "";

        // Get email id and password
        String getEmailId = emailid.getText().toString();
        String getTelefono = telefono.getText().toString();

        //si el campo de correo o el campo del telefono esrtan vacios
        if (TextUtils.isEmpty(emailid.getText()) && TextUtils.isEmpty(telefono.getText())) {

            isOK = false;
            mensaje = "Favor ingrese sus datos.";
        }

        // si el campo de correo no esta vacio y
        // verifica si el correo no es  valido
        boolean formato_correo = android.util.Patterns.EMAIL_ADDRESS.matcher(getEmailId).matches();
        if (!TextUtils.isEmpty(emailid.getText()) && !formato_correo) {
            isOK = false;
            mensaje = "El formato de correo no es aceptado.";
        }

        if (isOK) {

            //parametros para enviar al web service
            ArrayList<classWebService> parametros = new ArrayList<>();
            parametros.add(new classWebService("EmailUsuario", getEmailId));
            parametros.add(new classWebService("TelefUsuario", getTelefono));

            //se manda a ejecutar el web service
            // el resultado esta en el metodo onTaskRegistroComplete
            MG.webServicePost(URL_WS_REGISTRO, parametros,option_registro);


        } else {

            loginLayout.startAnimation(shakeAnimation);
            new classCustomToast(this).Show_ToastError(mensaje);
        }

    }

    @Override
    public void onAsyncTaskComplete(JSONObject result, int option) {

        SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = setting.edit();

        // si el resultado del web service trae algo, si no no se realizo
        //con exito la solicitud del ws
        if (result == null) {

            new AlertDialog.Builder(this).setTitle("Error al Obtener.").setMessage("Sin repuesta del servidor, o revisa la conexión de datos.").setIcon(R.drawable.error).show();

        } else {
            int success = 0;
            try {
                success = result.getInt("success");

                if (success == 1) {

                    editor.putInt(SETT_COD_USUARIO, result.getInt("MaxIdUsuario"));
                    editor.putString(SETT_CLIENTE_ASIGNADO, result.getString("IdCliente"));
                    editor.putString(SETT_NOMBRE_USUARIO, result.getString("NombreUsuario"));
                    editor.commit();


                    new classCustomToast(this).Toast(result.getString("message"), R.drawable.ic_success);

                    startActivity(new Intent(this, MainActivity.class));
                    this.finish();

                } else {

                    new AlertDialog.Builder(this).setTitle("Error al Recuperar.").setMessage(result.getString("message")).setIcon(R.drawable.error).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


    public void EnableRuntimePermission() {

        ActivityCompat.requestPermissions(this, new String[]
                {
                        GET_ACCOUNTS,
                        READ_CONTACTS,
                        READ_PHONE_STATE,
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE
                }, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    boolean GetAccountPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (GetAccountPermission) {
                        emailid.setText(getUserEmail());
                    }
                }
                break;
        }
    }

    public String getUserEmail() {

        if (ActivityCompat.checkSelfPermission(this, GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, GET_ACCOUNTS)) {

            } else {

                ActivityCompat.requestPermissions(this, new String[]{GET_ACCOUNTS}, 1);

            }

        } else {
            AccountManager manager = AccountManager.get(this);
            Account[] accounts = manager.getAccountsByType("com.google");
            List<String> possibleEmails = new LinkedList<String>();

            for (Account account : accounts) {
                // TODO: Check possibleEmail against an email regex or treat
                // account.name as an email address only for certain account.type values.
                possibleEmails.add(account.name);
            }

            if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
                String email = possibleEmails.get(0);
                String[] parts = email.split("@");

                if (parts.length > 1)
                    return email;
                else
                    return "";
            }
        }
        return "";

    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }
}
