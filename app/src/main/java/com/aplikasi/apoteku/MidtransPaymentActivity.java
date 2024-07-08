package com.aplikasi.apoteku;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.aplikasi.apoteku.model.Obat;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.util.ArrayList;
import java.util.List;

public class MidtransPaymentActivity extends AppCompatActivity implements TransactionFinishedCallback {

    private TextView textViewObatNama, textViewObatHarga;
    private Button buttonBayar;
    private Obat obat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_midtrans_payment);

        textViewObatNama = findViewById(R.id.textViewObatNama);
        textViewObatHarga = findViewById(R.id.textViewObatHarga);
        buttonBayar = findViewById(R.id.buttonBayar);

        obat = getIntent().getParcelableExtra("obat");

        if (obat != null) {
            textViewObatNama.setText(obat.getNama());
            textViewObatHarga.setText("Rp. " + obat.getHarga());
        }

        buttonBayar.setOnClickListener(v -> {
            proceedToPayment();
        });
    }

    private void proceedToPayment() {
        initializeMidtransSDK();

        // Start payment process
        MidtransSDK.getInstance().startPaymentUiFlow(this);
    }

    private void initializeMidtransSDK() {
        // Initialize Midtrans SDK
        SdkUIFlowBuilder.init()
                .setClientKey("SB-Mid-client-_KJOruedBfyTVnjZ") // Replace with your Midtrans client key
                .setContext(this)
                .setTransactionFinishedCallback(this)
                .enableLog(true) // Enable logging for debug purposes
                .buildSDK();

        // Set transaction details
        MidtransSDK.getInstance().setTransactionRequest(getTransactionRequest());
    }

    private com.midtrans.sdk.corekit.core.TransactionRequest getTransactionRequest() {
        com.midtrans.sdk.corekit.core.TransactionRequest transactionRequest = new com.midtrans.sdk.corekit.core.TransactionRequest(System.currentTimeMillis() + "", obat.getHarga());

        List<ItemDetails> itemDetailsList = new ArrayList<>();
        ItemDetails itemDetails = new ItemDetails(obat.getNama(), (double) obat.getHarga(), 1, "Obat");
        itemDetailsList.add(itemDetails);
        transactionRequest.setItemDetails((ArrayList<ItemDetails>) itemDetailsList);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setEmail("orderapotek@gmail.com");
        customerDetails.setPhone("081234567890");
        customerDetails.setFirstName("Akbar");
        customerDetails.setLastName("Devs");
        transactionRequest.setCustomerDetails(customerDetails);

        return transactionRequest;
    }

    @Override
    public void onTransactionFinished(TransactionResult transactionResult) {
        if (transactionResult.getResponse() != null) {
            switch (transactionResult.getStatus()) {
                case TransactionResult.STATUS_SUCCESS:
                    Toast.makeText(this, "Transaction success.", Toast.LENGTH_SHORT).show();
                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this, "Transaction pending.", Toast.LENGTH_SHORT).show();
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(this, "Transaction failed.", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Transaction finished with status: " + transactionResult.getStatus(), Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Toast.makeText(this, "Transaction canceled.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
