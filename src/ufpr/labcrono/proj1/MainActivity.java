package ufpr.labcrono.proj1;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	LinearLayout body;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        body = (LinearLayout) findViewById(R.id.body);
        
        
        String aux = 
        		  "{box:["
        		+ "  {\"class\":\"Text\",    \"title\":\"Qual seu nome?\"},"
        		+ "  {\"class\":\"Date\",    \"title\":\"Qual sua data de nascimento?\"},"
        		+ "  {\"class\":\"Text\",    \"title\":\"Qual seu curso?\"},"
  				+ "  {\"class\":\"Enum\", \"title\":\"Qual é seu sexo?\","
  				+ "   \"box\":[{title:\"Feminino\"},{title:\"Masculino\"}]"
  				+ "  },"
        		+ "  {\"class\":\"Text\",    \"title\":\"Qual seu email?\"},"
        		+ "  {\"class\":\"Text\",    \"title\":\"Qual seu periodo?\"},"
        		+ "  {\"class\":\"Boolean\", \"title\":\"Voce trabalha?\"},"
        		+ "  {\"class\":\"Boolean\", \"title\":\"Atualmente você faz uso contínuo de medicamento?\"},"
  				+ "  {\"class\":\"Enum\", \"title\":\"Qual é sua mão dominante?\","
  				+ "   \"box\":[{title:\"Esquerda\"},{title:\"Direita\"}]"
  				+ "  },"
 				+ "  {\"class\":\"Boolean\", \"title\":\"Você possui diagnóstico de daltonismo?\"},"
 				+ "  {\"class\":\"Boolean\", \"title\":\"Em períodos de avaliações na Universidade, você altera seus hábitos de sono?\"},"
 				+ "  {\"class\":\"Boolean\", \"title\":\"Você possui diagnóstico de daltonismo?\"}"
        		+ "]}";
        
        this.buildForm(aux);
        

        
        /*TextView tv2 = new TextView(this);        
        tv2.setText("1. Qual a sua idade?");        
        EditText edittext2 = new EditText(this);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    //getApplicationContext());
    
    private void buildForm(String form_str){
    	try {
			//JSONObject reader = new JSONObject("teste.json");
        	JSONObject form = new JSONObject( form_str );
        	JSONArray box = form.getJSONArray("box");

        	for (int i=0; i<box.length(); i++){
        		JSONObject issue = box.getJSONObject(i);
        		
        		String classe = issue.getString("class");
        		String title  = issue.getString("title");
                TextView tv = new TextView(this);
                tv.setText(Integer.toString(i+1)+". "+title);
                body.addView(tv);
                
                if ( classe.equals("Int") ){
                	this.buildIntInput(issue);
                } else if ( classe.equals("Text") ){
                	this.buildTextInput(issue);
                } else if ( classe.equals("Date") ){
                	this.buildDateInput(issue);
                } else if ( classe.equals("Boolean") ){
                	this.buildBooleanInput(issue);
                } else if ( classe.equals("Enum") ){
                	this.buildEnumInput(issue);
                } else {
                	TextView tv1 = new TextView(this);
                	tv1.setText("Error: "+classe);
                    body.addView(tv1);
                }
        	}
            
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    }
    
    
    private void buildTextInput(JSONObject issue) throws JSONException {   	
        EditText edittext = new EditText(this); 
        this.body.addView(edittext);
    }
    
    private void buildIntInput(JSONObject issue) throws JSONException {   	
        EditText edittext = new EditText(this);  
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        this.body.addView(edittext);
    }
    
    
    private void buildDateInput(JSONObject issue) throws JSONException {   	
        EditText edittext = new EditText(this);  
        edittext.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        this.body.addView(edittext);
    }
    
    private void buildBooleanInput(JSONObject issue) throws JSONException {   	
    	Spinner spinner = new Spinner(this);
    	List<String> list = new ArrayList<String>();
    	list.add("Não");
    	list.add("Sim");
    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
    	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner.setAdapter(dataAdapter);   	
        this.body.addView(spinner);
    }
    
    private void buildEnumInput(JSONObject issue) throws JSONException {   	
    	Spinner spinner = new Spinner(this);
    	List<String> list = new ArrayList<String>();

    	JSONArray box = issue.getJSONArray("box");
    	for (int i=0; i<box.length(); i++){
    		JSONObject option = box.getJSONObject(i);
    		String     title  = option.getString("title");
    		list.add( title );
    	}

    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
    	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner.setAdapter(dataAdapter);   	
        this.body.addView(spinner);
    }
    
    
    
    
    
    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
        intent.setType("*/*"); 
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }
    
}
