package com.freejob.freejob.Activities;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Items.Request;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import br.com.uol.pslibs.checkout_in_app.PSCheckout;
import br.com.uol.pslibs.checkout_in_app.transparent.listener.PSBilletListener;
import br.com.uol.pslibs.checkout_in_app.transparent.vo.PSBilletRequest;
import br.com.uol.pslibs.checkout_in_app.transparent.vo.PaymentResponseVO;
import br.com.uol.pslibs.checkout_in_app.wallet.util.PSCheckoutConfig;

public class PaymentActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference reference;
    Request request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        InitPagSeguro();
        InitVariables();
    }

    private void InitVariables() {
        auth = new CommonMethods().getAuth(PaymentActivity.this);
        reference = new CommonMethods().getRef(PaymentActivity.this);

        Gson gson = new Gson();
        String json = getIntent().getStringExtra("REQUEST");
        request = gson.fromJson(json, Request.class);

        Button generate = findViewById(R.id.AP_generate);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerateBooklet();
            }
        });
    }

    private void InitPagSeguro() {
        //Inicialização a lib com parametros necessarios
        PSCheckoutConfig psCheckoutConfig = new PSCheckoutConfig();
        psCheckoutConfig.setSellerEmail("desenvolvimento.freejob@gmail.com");
        psCheckoutConfig.setSellerToken("1F64CCE3A6AA400794765D62BEAA0C17");

        //psCheckoutConfig.setContainer(R.id.fragment_container);

        PSCheckout.initTransparent(PaymentActivity.this, psCheckoutConfig);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//Android 6+ fornece controle para LIB para request de permissões
        PSCheckout.onRequestPermissionsResult(this, requestCode, permissions, grantResults);//Controle Lib Activity Life Cycle
    }

    private void GenerateBooklet(){
        final User[] client = new User[1];
        reference.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                client[0] = dataSnapshot.getValue(User.class);
                PSBilletRequest psBilletRequest = new PSBilletRequest();

                psBilletRequest
                        .setDocumentNumber(client[0].getCPF())
                        .setName(client[0].getName())
                        .setEmail(client[0].getEmail())
                        .setAreaCode(client[0].getPhone().substring(0, 1))
                        .setPhoneNumber(client[0].getPhone().substring(2))
                        .setStreet(client[0].getMainAddress().getLogradouro())
                        .setAddressComplement(client[0].getMainAddress().getComp())
                        .setAddressNumber(String.valueOf(client[0].getMainAddress().getNumero()))
                        .setDistrict(client[0].getMainAddress().getBairro())
                        .setCity(client[0].getMainAddress().getCidade())
                        .setState(client[0].getMainAddress().getEstado())
                        .setCountry("BRA")
                        .setPostalCode(String.valueOf(client[0].getMainAddress().getCEP()))
                        .setTotalValue(request.getValue())
                        .setAmount(0.0)
                        .setDescriptionPayment("Pagamento pelo serviço de "+ request.getType() + " feito do cliente:  "+client[0].getName()+" para o trabalhador: ")
                        .setQuantity(1);

                final ProgressDialog progDialog = new ProgressDialog(PaymentActivity.this);
                PSBilletListener psBilletListener = new PSBilletListener() {
                    @Override
                    public void onSuccess(PaymentResponseVO responseVO) {
                        // responseVO.getBookletNumber() - número do código de barras do boleto

                        // responseVO.getPaymentLink() - link para download do boleto
                    }

                    @Override
                    public void onFailure(Exception e) {
                        progDialog.dismiss();

                        Log.e("ERROR", e.getCause().toString());
                        Snackbar.make(findViewById(android.R.id.content), "Não foi possível gerar o boleto porque "+
                                e.getCause(),
                                Snackbar.LENGTH_INDEFINITE)
                                .show();
                    }

                    @Override
                    public void onProcessing() {
                    }
                };
                PSCheckout.generateBooklet(psBilletRequest, psBilletListener, PaymentActivity.this);

                progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDialog.setTitle("Gerando boleto...");
                progDialog.setMessage("Aguarde um momento enquanto geramos seu boleto...");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
