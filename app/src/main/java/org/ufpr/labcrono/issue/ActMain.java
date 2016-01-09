package org.ufpr.labcrono.issue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

public class ActMain extends AppCompatActivity {
	FormPkg formpkg;
	LinearLayout body;
	Form form;
	TextView error_msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		body = (LinearLayout) findViewById(R.id.body);

		SharedPreferences sharedPref = this.getSharedPreferences("preferences",Context.MODE_PRIVATE);
		String form_name = sharedPref.getString("form_used", "");
		String textformatting = sharedPref.getString( "textformatting", "UTF-8" );

		this.form = null;
		this.formpkg = new FormPkg();
		try {
			this.form = formpkg.load( form_name );
			this.form.load(textformatting);
			this.form.render( this );
		} catch (IOException e) {
			this.showError(e.getMessage());
		} catch (JSONException e) {
			this.showError(e.getMessage());
		} catch (Exception e) {
			this.showError(e.getMessage());
		}


		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String num = form.hasIssueEmpty();
				if ( !num.isEmpty() ){
					Snackbar.make(view, "Questão " + num + " nao foi respondida", Snackbar.LENGTH_LONG).setAction("Action", null).show();
				} else {
					Snackbar.make(view, "", Snackbar.LENGTH_LONG).setAction(
						"Submeter", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
							Toast.makeText(ActMain.this, "Obrigado pela sua Contribuição", Toast.LENGTH_SHORT).show();
							ActMain.this.form.saveForm();
							finish();
							}
						}
					).show();
				}
			}
		});

	}



	@Override
	public void onBackPressed() {
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_act_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_exit) {
			this.finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	public void showError(String msg){
		this.body.removeAllViews();

		this.error_msg = new TextView(this);
		this.error_msg.setText(msg);

		//this.error_msg.setTextColor(0xFFFF0000);
		this.body.addView(error_msg);
		//Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();*/

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setVisibility(View.GONE);
	}

}
