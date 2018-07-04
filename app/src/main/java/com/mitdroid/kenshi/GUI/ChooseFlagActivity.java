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
/*
            case R.id.checkBoxIdleScan:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setIdleScan));
                else
                    flags.remove(getResources().getString(R.string.setIdleScan));
                break;
*/
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
/*
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
*/
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
/*
            case R.id.checkBoxBinaryPayloadToSentPackage:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setBinaryPayloadToSentPackage));
                else
                    flags.remove(getResources().getString(R.string.setBinaryPayloadToSentPackage));
                break;

            case R.id.checkBoxCustomizeTCP:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setCustomizedTCPFlags));
                else
                    flags.remove(getResources().getString(R.string.setCustomizedTCPFlags));
                break;

            case R.id.checkBoxDecoy:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setDecoy));
                else
                    flags.remove(getResources().getString(R.string.setDecoy));
                break;

            case R.id.checkBoxFragmentPacket:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setFragmentPacket));
                else
                    flags.remove(getResources().getString(R.string.setFragmentPacket));
                break;

            case R.id.checkBoxFTPRelayHost:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setFTPRelay));
                else
                    flags.remove(getResources().getString(R.string.setFTPRelay));
                break;

            case R.id.checkBoxInitProbeTimeout:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setInitProbeTimeout));
                else
                    flags.remove(getResources().getString(R.string.setInitProbeTimeout));
                break;

            case R.id.checkBoxInterface:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setInterface));
                else
                    flags.remove(getResources().getString(R.string.setInterface));
                break;

            case R.id.checkBoxMaxParallelProbe:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setMaxParallelProbe));
                else
                    flags.remove(getResources().getString(R.string.setMaxParallelProbe));
                break;

            case R.id.checkBoxMaxProbeTimeout:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setMaxProbeTimeout));
                else
                    flags.remove(getResources().getString(R.string.setMaxProbeTimeout));
                break;

            case R.id.checkBoxMaxRetries:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setMaxRetries));
                else
                    flags.remove(getResources().getString(R.string.setMaxRetries));
                break;

            case R.id.checkBoxMinParallelProbe:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setMinParallelProbe));
                else
                    flags.remove(getResources().getString(R.string.setMinParallelProbe));
                break;

            case R.id.checkBoxMinProbeTimeout:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setMinProbeTimeout));
                else
                    flags.remove(getResources().getString(R.string.setMinProbeTimeout));
                break;

            case R.id.checkBoxMTU:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setMTU));
                else
                    flags.remove(getResources().getString(R.string.setMTU));
                break;
*/
            case R.id.checkBoxOSEnable:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setOSEnable));
                else
                    flags.remove(getResources().getString(R.string.setOSEnable));
                break;

            case R.id.checkBoxOSGuess:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setOSGuess));
                else
                    flags.remove(getResources().getString(R.string.setOSGuess));
                break;

            case R.id.checkBoxOSLimit:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setOSLimit));
                else
                    flags.remove(getResources().getString(R.string.setOSLimit));
                break;

            case R.id.checkBoxPortConsecutive:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setPortConsecutiveScan));
                else
                    flags.remove(getResources().getString(R.string.setPortConsecutiveScan));
                break;

            case R.id.checkBoxPortFastmode:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setPortFastMode));
                else
                    flags.remove(getResources().getString(R.string.setPortFastMode));
                break;
/*
            case R.id.checkBoxPortRatio:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setPortRatio));
                else
                    flags.remove(getResources().getString(R.string.setPortRatio));
                break;

            case R.id.checkBoxPortsExclude:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setPortsExclusion));
                else
                    flags.remove(getResources().getString(R.string.setPortsExclusion));
                break;

            case R.id.checkBoxPortSpecification:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setPortSpecification));
                else
                    flags.remove(getResources().getString(R.string.setPortSpecification));
                break;

            case R.id.checkBoxProxies:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setProxies));
                else
                    flags.remove(getResources().getString(R.string.setProxies));
                break;

            case R.id.checkBoxRandomDataToSentPackage:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setRandomDataToSentPackage));
                else
                    flags.remove(getResources().getString(R.string.setRandomDataToSentPackage));
                break;
*/
            case R.id.checkBoxSendPackageBogus:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setSendPackageBogus));
                else
                    flags.remove(getResources().getString(R.string.setSendPackageBogus));
                break;
/*
            case R.id.checkBoxSendPackageIPOptions:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setSendPackageIPOptions));
                else
                    flags.remove(getResources().getString(R.string.setSendPackageIPOptions));
                break;
*/
            case R.id.checkBoxVersionDetection:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setVersionDetection));
                else
                    flags.remove(getResources().getString(R.string.setVersionDetection));
                break;
/*
            case R.id.checkBoxSourcePort:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setSourcePort));
                else
                    flags.remove(getResources().getString(R.string.setSourcePort));
                break;

            case R.id.checkBoxSpoofMACAddress:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setSpoofMACAddress));
                else
                    flags.remove(getResources().getString(R.string.setSpoofMACAddress));
                break;

            case R.id.checkBoxStringToSentPackage:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setStringToSentPackage));
                else
                    flags.remove(getResources().getString(R.string.setStringToSentPackage));
                break;

            case R.id.checkBoxTimeOption:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setTime));
                else
                    flags.remove(getResources().getString(R.string.setTime));
                break;

            case R.id.checkBoxTimeToLive:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setTimeToLive));
                else
                    flags.remove(getResources().getString(R.string.setTimeToLive));
                break;

            case R.id.checkBoxTopPorts:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setTopPorts));
                else
                    flags.remove(getResources().getString(R.string.setTopPorts));
                break;
*/
            case R.id.checkBoxVersionAll:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setVersionAll));
                else
                    flags.remove(getResources().getString(R.string.setVersionAll));
                break;
/*
            case R.id.checkBoxVersionIntensity:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setVersionIntesity));
                else
                    flags.remove(getResources().getString(R.string.setVersionIntesity));
                break;
*/
            case R.id.checkBoxVersionLight:
                if(isChecked)
                    flags.add(getResources().getString(R.string.setVersionLight));
                else
                    flags.remove(getResources().getString(R.string.setVersionLight));
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
