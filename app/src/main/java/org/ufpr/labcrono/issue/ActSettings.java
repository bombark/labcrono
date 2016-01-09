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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ActSettings extends AppCompatActivity {
	LinearLayout body;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_settings);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);


		this.body = (LinearLayout) findViewById(R.id.body);




		// Text Formatting
		RadioGroup rg_textformatting = (RadioGroup) findViewById(R.id.textformatting);
		rg_textformatting.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()            {
			@Override
			public void onCheckedChanged(RadioGroup rg, int selectedId) {
				selectedId = rg.getCheckedRadioButtonId();
				RadioButton formchoosed = (RadioButton) findViewById(selectedId);
				String textformatting = formchoosed.getText().toString();
				SharedPreferences sharedPref = ActSettings.this.getSharedPreferences("preferences",Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("textformatting", textformatting);
				editor.commit();
			}
		});







		// Pegar o formulario selecionado
		final RadioButton[] rb = new RadioButton[50];

		FormPkg formpkg = new FormPkg();
		ArrayList<Form> list = null;
		try {
			list = formpkg.list();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}


		SharedPreferences sharedPref = this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
		String form_choosed = sharedPref.getString("form_used", "");

		int i = 0;
		RadioGroup rg = new RadioGroup(this);
		rg.setOrientation(RadioGroup.VERTICAL);
		for (Form form : list) {
			rb[i]  = new RadioButton(this);
			rg.addView(rb[i]);
			rb[i].setText(form.name);
			if ( form.name.equals(form_choosed) )
				rb[i].setChecked(true);
			else
				rb[i].setChecked(false);
			i += 1;
		}
		this.body.addView(rg);

		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()            {
			@Override
			public void onCheckedChanged(RadioGroup rg, int selectedId) {
				selectedId = rg.getCheckedRadioButtonId();
				RadioButton formchoosed = (RadioButton) findViewById(selectedId);
				String form_name = formchoosed.getText().toString();
				SharedPreferences sharedPref = ActSettings.this.getSharedPreferences("preferences",Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("form_used", form_name);
				editor.commit();
			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

}
