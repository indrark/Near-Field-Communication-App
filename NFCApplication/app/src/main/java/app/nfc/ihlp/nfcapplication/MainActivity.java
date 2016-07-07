package app.nfc.ihlp.nfcapplication;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import IHLP.NFC.App.R;

public class MainActivity extends Activity {

    // Define NfcAdapter for controlling file transfer.
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define Package Manager to get access to File System in user's phone.
        PackageManager pm = this.getPackageManager();

        // Validate if NFC hardware is available on device.
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            Toast.makeText(this, "This phone does not have NFC hardware.",
                    Toast.LENGTH_SHORT).show();

        }
        // Validate if OS version is Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Toast.makeText(this, "Android Beam is not supported on this phone.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            // Confirmation of NFC and Android Beam support on phone.
            Toast.makeText(this, "Android Beam is supported on this phone.",
                    Toast.LENGTH_SHORT).show();
        }


        // Provide definition for Button to initiate Image, Video, and Contacts transfer.
        Button btnImage = (Button) findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFile("temp.jpg");
            }
        });

        Button btnVideo = (Button) findViewById(R.id.btnVideo);
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFile("temp.mp4");
            }
        });

        Button btnContact = (Button) findViewById(R.id.btnContact);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFile("Contacts.VCF");
            }
        });

    }

    // send an Image, Video or Contacts file from Downloads directory.
    public void sendFile(String fileName)
    {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Verify if NFC is enabled on the phone.

        if(!nfcAdapter.isEnabled()){

            // If NFC is disabled, prompt the user to Settings to enable NFC.
            Toast.makeText(this, "Please enable NFC.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));

        }

        // Verify if Android Beam is enabled on the phone.

        else if(!nfcAdapter.isNdefPushEnabled()) {

            // If Android Beam is disabled, prompt the user to Settings to enable Android Beam.
            Toast.makeText(this, "Please enable Android Beam.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));

        }
        else {

            // Get the path to user's Downloads directory where the image, video, and contacts file will be stored.

            File fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            // Create a new file by using the provided directory and file name.

            File fileToTransfer = new File(fileDirectory, fileName);
            fileToTransfer.setReadable(true, false);

            nfcAdapter.setBeamPushUris(new Uri[]{Uri.fromFile(fileToTransfer)}, this);

        }
    }
}
