package be.retailsonar.retailsonar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class keuzeMapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_keuze_maps);



        Button GotomapsAfstand = (Button) findViewById(R.id.buttonAfstand);
        GotomapsAfstand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(keuzeMapsActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        Button GotomapsOpp = (Button) findViewById(R.id.buttonOpp);
        GotomapsOpp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(keuzeMapsActivity.this, AreaMapsActivity.class);
                startActivity(i);
            }
        });
    }
}
