package com.mitdroid.kenshi.GUI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

import com.mitdroid.kenshi.mitdroid.R;

import java.util.ArrayList;

public class ChooseFlagActivity extends AppCompatActivity {

    private ArrayList<String> flags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_flag);
    }

    public void chosenFlag(View view) {
        boolean isChecked = ((CheckBox)view).isChecked();
        switch (view.getId()) {
            case R.id.checkBoxAckScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanAck));
                else
                    //remove flag
                break;

            case R.id.checkBoxConnectScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanConnect));
                else
                    //remove the flag
                break;

            case R.id.checkBoxFinScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanFin));
                else
                    //remove the flag
                break;

            case R.id.checkBoxIdleScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setIdleScan));
                else
                    //remove the flag
                break;

            case R.id.checkBoxMaimonScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanMaimon));
                else
                    //remove the flag
                break;

            case R.id.checkBoxSctpInitScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanSCTPInit));
                else
                    //remove the flag
                break;

            case R.id.checkBoxProtocolScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanProtocol));
                else
                    //remove the flag
                break;

            case R.id.checkBoxUDPScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanUdp));
                else
                    //remove the flag
                break;

            case R.id.checkBoxWindowScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanWindow));
                else
                    //remove the flag
                break;

            case R.id.checkBoxXmasScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanXmas));
                else
                    //remove the flag
                break;

            case R.id.checkBoxCookieEchoScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanCookieEcho));
                else
                    //remove the flag
                break;

            case R.id.checkBoxMaxParallelScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setMaxParallelScanGroupSize));
                else
                    //remove the flag
                    break;

            case R.id.checkBoxMinParallelScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setMinParallelScanGroupSize));
                else
                    //remove the flag
                break;

            case R.id.checkBoxTCPNullScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanTcpNull));
                else
                    //remove the flag
                    break;

            case R.id.checkBoxTcpSynScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanTcpSyn));
                else
                    //remove the flag
                break;
        }
    }

    public void sendData() {
        Intent intent = new Intent();
        intent.putExtra("flags", flags);
        setResult(RESULT_OK, intent);
    }

}
