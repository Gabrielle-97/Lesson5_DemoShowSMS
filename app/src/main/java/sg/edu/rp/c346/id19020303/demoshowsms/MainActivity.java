package sg.edu.rp.c346.id19020303.demoshowsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActivityChooserView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tvSms;
    Button btnRetrieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSms = findViewById(R.id.tv);
        btnRetrieve = findViewById(R.id.btnRetrieve);

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //----------Start of runtime ----------
                int permissionCheck = PermissionChecker.checkSelfPermission(
                        MainActivity.this, Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    //Stops the action from proceeding further as permission not granted yet
                    return;
                }

                //Create all messages URI
                Uri uri = Uri.parse("content://sms");

                //The columns we want
                //dat is when the message took place
                //address is the number of the other party
                //body is the message content
                //type 1 is received, type 2 send
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                //Get Content Resolver object from which to
                //query the content provider
                ContentResolver cr = getContentResolver();

                //The filter String
                String filter = "body LIKE ? AND body LIKE ?";

                //The matches for ?
                String[] filterArgs = {"%late%", "%min%"};
                //Fetch SMS messages from Built-in Content Provider

                //Fetch SMS message from built-in Content Provider
                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                String smsBody = "";
                if(cursor.moveToFirst()){
                    do{
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);

                        if (type.equalsIgnoreCase("1")){
                            type = "Inbox: ";
                        }

                        else{
                            type = "Sent: ";
                        }

                        smsBody += type + " " + address + "\n at " + date +
                                "\n\"" + body + "\"\n\n";
                    }
                    while (cursor.moveToNext());
                }

                tvSms.setText(smsBody);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 0: {
                //If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    //Permission was granted, weeeeeeeeeeeeeeeeeeeeeeeeh!! Do the read SMS
                    //as if the btnRetrieve is clicked
                    btnRetrieve.performClick();
                }

                else {
                    //permission denied...notify user
                    Toast.makeText(MainActivity.this, "Permission was not granted", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}