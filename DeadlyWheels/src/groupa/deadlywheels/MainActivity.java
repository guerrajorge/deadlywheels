package groupa.deadlywheels;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends Activity {

    /** Called when the user clicks on the image (for now)
     * later it will be called after a certain time interval */
    public void gotologin(View view){
        // Do something in response to the touched image
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //getting rib off the status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
        setContentView(R.layout.activity_main);
    }
}
