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
                    flags.remove(getResources().getString(R.string.setScanAck));
                break;

            case R.id.checkBoxConnectScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanConnect));
                else
                    flags.remove(getResources().getString(R.string.setScanConnect));
                break;

            case R.id.checkBoxFinScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanFin));
                else
                    flags.remove(getResources().getString(R.string.setScanFin));
                break;

            case R.id.checkBoxIdleScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setIdleScan));
                else
                    flags.remove(getResources().getString(R.string.setIdleScan));
                break;

            case R.id.checkBoxMaimonScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanMaimon));
                else
                    flags.remove(getResources().getString(R.string.setScanMaimon));
                break;

            case R.id.checkBoxSctpInitScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanSCTPInit));
                else
                    flags.remove(getResources().getString(R.string.setScanSCTPInit));
                break;

            case R.id.checkBoxProtocolScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanProtocol));
                else
                    flags.remove(getResources().getString(R.string.setScanProtocol));
                break;

            case R.id.checkBoxUDPScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanUdp));
                else
                    flags.remove(getResources().getString(R.string.setScanUdp));
                break;

            case R.id.checkBoxWindowScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanWindow));
                else
                    flags.remove(getResources().getString(R.string.setScanWindow));
                break;

            case R.id.checkBoxXmasScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanXmas));
                else
                    flags.remove(getResources().getString(R.string.setScanXmas));
                break;

            case R.id.checkBoxCookieEchoScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanCookieEcho));
                else
                    flags.remove(getResources().getString(R.string.setScanCookieEcho));
                break;

            case R.id.checkBoxMaxParallelScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setMaxParallelScanGroupSize));
                else
                    flags.remove(getResources().getString(R.string.setMaxParallelScanGroupSize));
                    break;

            case R.id.checkBoxMinParallelScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setMinParallelScanGroupSize));
                else
                    flags.remove(getResources().getString(R.string.setMinParallelScanGroupSize));
                break;

            case R.id.checkBoxTCPNullScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanTcpNull));
                else
                    flags.remove(getResources().getString(R.string.setScanTcpNull));
                    break;

            case R.id.checkBoxTcpSynScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setScanTcpSyn));
                else
                    flags.remove(getResources().getString(R.string.setScanTcpSyn));
                break;
        }
    }

    public void sendData(View view) {
        Intent intent = new Intent();
        intent.putExtra("custom flags", flags);
        setResult(RESULT_OK, intent);
        finish();
    }

}
