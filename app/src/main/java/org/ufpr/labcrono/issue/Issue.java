package org.ufpr.labcrono.issue;



import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Context;

/*** Brief class Issue
 */
public class Issue {
	JSONObject form;
	TextView title;
	View input;
	ArrayList<Issue> box;

	// Variaveis Globais usada na rotina Build
	ActMain actmain;
	LinearLayout body;

	/*! Construtor padrão para Pergunta
	 * @param main  - Objeto ActivityMain usado para possibilitar, que esta classe crie Views na Activity
	 * @param issue - Objeto Json da Pergunta no modelo definido
	 */
	public Issue(JSONObject issue){

		this.form = issue;
		this.box  = new ArrayList<Issue>();
		try {
			if ( !this.form.has("ishidden") ){
				this.form.put("ishidden", false);
			}
			if ( !form.has("class") ){
				form.put("class", "Text");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/*! Construtor padrão para SubPergunta
	 * @param issue_father Objeto da Issue pai
	 * @param issue Objeto Json da Pergunta no modelo definido
	 */
	/*public Issue(Issue issue_father, JSONObject issue){
		this.form = issue;
		this.box  = new ArrayList<Issue>();
		try {
			if ( !this.form.has("ishidden") ){
				this.form.put("ishidden", false);
			}
			if ( !form.has("class") ){
				form.put("class", "Text");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}*/



	/*! Seta a visibilidade da Pergunta
	 * param flag - [View.VISIBLE|View.GONE]
	 */
	public void setVisibity(int flag) { // flag = [View.VISIBLE|View.GONE|...]
		if ( this.title != null )
			this.title.setVisibility(flag);
		if ( this.input != null )
			this.input.setVisibility(flag);
		try {
			if ( flag == View.VISIBLE )
				this.form.put("ishidden", false);
			else
				this.form.put("ishidden", true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		for (int i=0; i<this.box.size(); i++){
			this.box.get(i).setVisibity(flag);
		}
	}

	/*! Atualiza os valores do Objeto Json, o qual será usado para salvar os dados no arquivo */
	public void update() throws JSONException{
		String classe = this.form.getString("class");
		if ( classe.equals("Text") || classe.equals("Int") || classe.equals("Date") ){
			EditText edit  = (EditText) this.input;
			Editable value = edit.getText();
			this.form.put("value", value);
		} else if ( classe.equals("Enum") || classe.equals("Boolean") ){
			Spinner spinner = (Spinner) this.input;
			//String value = spinner.getSelectedItem().toString();
			int value = spinner.getSelectedItemPosition();
			this.form.put("value", value);
		} else if ( classe.equals("Checkbox") ){
			LinearLayout  group = (LinearLayout) this.input;
			JSONArray value = new JSONArray();
			for (int i=0; i<group.getChildCount(); i++){
				CheckBox check = (CheckBox) group.getChildAt(i);
				value.put( check.isChecked() );
			}
			this.form.put("value", value);
		}
		for (int i=0; i<this.box.size(); i++){
			this.box.get(i).update();
		}
	}







	/*! Constroi o visual da Pergunta */
	public String hasIssueEmpty(){
		try {
			if ( form.getBoolean("ishidden") == false ){
				String classe = form.getString("class");

				if ( classe.equals("Int") || classe.equals("Text") || classe.equals("Date") ){
					EditText edit  = (EditText) this.input;
					Editable value = edit.getText();
					if ( value.length() == 0 ){
						return this.form.getString("num");
					}
				} else if ( classe.equals("Boolean") ){
					Spinner spinner = (Spinner) this.input;
					String value = spinner.getSelectedItem().toString();
					if ( value.equals("") ){
						return this.form.getString("num");
					}
					if ( value.equals("Sim") ){
						for ( int i=0; i<this.box.size(); i++ ){
							String num = this.box.get(i).hasIssueEmpty();
							if ( !num.isEmpty() )
								return num;
						}
					}
				} else if ( classe.equals("Enum") ){
					Spinner spinner = (Spinner) this.input;
					if ( spinner.getSelectedItem().toString().equals("") ){
						return this.form.getString("num");
					}
				}
			}
			return "";
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return "";
	}


	/*public void setGenre(String genre){
		if ( genre.equalsIgnoreCase("female") ){
			if ( this.form.has("only_male") ){
				this.setVisibity(View.GONE);
			} else if ( this.form.has("only_female") ){
				this.setVisibity(View.VISIBLE);
			}
		} else if ( genre.equalsIgnoreCase("male") ){
			if ( this.form.has("only_female") ){
				this.setVisibity(View.GONE);
			} else if ( this.form.has("only_male") ){
				this.setVisibity(View.VISIBLE);
			}
		}
	}*/







	/*! Constroi o visual da Pergunta */
	public void build(ActMain actmain) throws JSONException{
		this.actmain = actmain;
		this.body = actmain.body;


		String classe = form.getString("class");
		this.buildTitle(this.form);
		if ( classe.equals("Int") ){
			this.buildIntInput(this.form);
		} else if ( classe.equals("Text") ){
			this.buildTextInput(this.form);
		} else if ( classe.equals("Date") ){
			this.buildDateInput(this.form);
		} else if ( classe.equals("Boolean") ){
			this.buildBooleanInput(this.form);
		} else if ( classe.equals("Enum") ){
			this.buildEnumInput(this.form);
		} else if ( classe.equals("Checkbox") ){
			this.buildCheckboxInput(this.form);
		} else {
			throw new JSONException("Classe da Pergunta não definida");
		}

		if ( this.form.has("ishidden") && this.form.getBoolean("ishidden") ){
			this.setVisibity(View.GONE);
		}
	}



	/* ======================================================================================*/

	/*! Constroi o titulo da Pergunta
	 * @param issue Objeto Issue no modelo definido
	 * */
	protected void buildTitle(JSONObject issue) throws JSONException{
		this.title = new TextView(this.actmain);
		this.title.setText(issue.getString("num") + ". " + issue.getString("title"));
		this.title.setTextSize(24);
		this.body.addView(this.title);
	}


	/*! Quando a Pergunta é do tipo Texto, então constroi o visual da Pergunta:Text
	 * @param issue Objeto Issue no modelo definido
	 */
	protected void buildTextInput(JSONObject issue) throws JSONException {
		EditText edittext = new EditText(this.actmain);
		edittext.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
		if ( issue.has("value") ){
			edittext.setText( issue.getString("value") );
		}
		this.input = edittext;
		this.body.addView( edittext );//addIssueInBody(issue, edittext);
	}


	/*! Quando a Pergunta é do tipo Inteiro, então constroi o visual da Pergunta:Int
	 *  @param issue Objeto Issue no modelo definido
	 */
	protected void buildIntInput(JSONObject issue) throws JSONException {
		EditText edittext = new EditText(this.actmain);
		edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
		if ( issue.has("value") ){
			edittext.setText( issue.getString("value") );
		}
		this.input = edittext;
		this.body.addView( edittext );
	}


	/*! Quando a Pergunta é do tipo Data, então constroi o visual da Pergunta:Date
	 *  @param issue Objeto Issue no modelo definido
	 */
	protected void buildDateInput(JSONObject issue) throws JSONException {
		EditText edittext = new EditText(this.actmain);
		edittext.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
		if ( issue.has("value") ){
			edittext.setText( issue.getString("value") );
		}
		this.input = edittext;
		this.body.addView( edittext );
	}


	/*! Quando a Pergunta é do tipo Boolena, então constroi o visual da Pergunta:Boolean
	 *  @param issue Objeto Issue no modelo definido
	 */
	protected void buildBooleanInput(JSONObject issue) throws JSONException {
		final Spinner spinner = new Spinner(this.actmain);
		List<String> list = new ArrayList<String>();
		list.add("");
		list.add("Não");
		list.add("Sim");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.actmain,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);

		if ( issue.has("value") ){
			spinner.setSelection(issue.getInt("value"));
		}

		this.input = spinner;
		this.body.addView(spinner);

		if ( issue.has("box") ){
			String num_base = issue.getString("num");
			JSONArray box = issue.getJSONArray("box");
			for (int i=0; i<box.length(); i++){
				JSONObject subissue_form = box.getJSONObject(i);

				// Add the variables num and ishidden in the JSON
				subissue_form.put("ishidden", true);
				subissue_form.put( "num", num_base+"."+Integer.toString(i+1) );

				// Create the Sub-Issue and add in the parent box
				Issue subissue = new Issue(subissue_form);
				subissue.build(this.actmain);
				this.box.add(subissue);
			}
		}

		// Force the focus to Spinner (2016-03-04 - Bug fix)
		//spinner.setFocusable(true);
		spinner.setFocusableInTouchMode(true);
		spinner.setOnFocusChangeListener(new View.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if ( hasFocus ){
					if ( spinner.getWindowToken() != null )
						spinner.performClick();
				}
			}
		});


		/* Executa um evento quando um novo item eh selecionado */
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
				// Set visible or invisible
				if (arg2 == 2) {  // simd
					Issue.this.setSubVisibity(View.VISIBLE);
					Issue.this.body.invalidate();
				} else { // nao
					Issue.this.setSubVisibity(View.GONE);
					Issue.this.body.invalidate();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});


	}


	/*! @brief Quando a Pergunta é do tipo Enumeração, então constroi o visual da Pergunta:Enum
	 *  @param issue Objeto Json
	 */
	protected void buildEnumInput(JSONObject issue) throws JSONException {
		final Spinner spinner = new Spinner(this.actmain);
		List<String> list = new ArrayList<String>();
		list.add( "" );

		JSONArray box = issue.getJSONArray("box");
		for (int i=0; i<box.length(); i++){
			JSONObject option = box.getJSONObject(i);
			String     title  = option.getString("title");
			list.add( title );
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.actmain,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);


		if ( issue.has("value") ){
			spinner.setSelection(issue.getInt("value"));
		}

		// Force the focus to Spinner (2016-03-04 - Bug fix)
		//spinner.setFocusable(true);
		spinner.setFocusableInTouchMode(true);
		spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (spinner.getWindowToken() != null)
						spinner.performClick();
				}
			}
		});

		/* Quando a pergunta é sobre Genero, cria um evento para MainActivity */
		/*if ( issue.has("is_genre") ){
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long id) {
					String res = parent.getItemAtPosition(pos).toString();
					MainActivity main = (MainActivity) Issue.this.context;
					if ( res.equalsIgnoreCase("feminino") ){
						main.setGenre("female");
						Issue.this.body.invalidate();
					} else if (res.equalsIgnoreCase("masculino")){
						main.setGenre("male");
						Issue.this.body.invalidate();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
		}*/



		this.input = spinner;
		this.body.addView( spinner );
	}


	/*! Quando a Pergunta é do tipo Checkbox, então constroi o visual da Pergunta:Checkbox
	 *  @param issue Objeto Issue no modelo definido
	 */
	protected void buildCheckboxInput(JSONObject issue) throws JSONException {
		LinearLayout group = new LinearLayout(this.actmain);
		this.input = group;

		if ( issue.has("align") ){
			if ( issue.getString("align").equals("vertical")  )
				group.setOrientation(LinearLayout.VERTICAL);
			else
				group.setOrientation(LinearLayout.HORIZONTAL);
		}

		JSONArray jbox = issue.getJSONArray("box");

		if ( issue.has("value") ){
			JSONArray valor = issue.getJSONArray("value");
			for (int i=0; i<jbox.length(); i++){
				CheckBox check = new CheckBox(this.actmain);
				check.setText( jbox.getJSONObject(i).getString("title") );
				check.setChecked( valor.getBoolean(i) );
				group.addView(check);
			}
		} else {
			for (int i=0; i<jbox.length(); i++){
				CheckBox check = new CheckBox(this.actmain);
				check.setText( jbox.getJSONObject(i).getString("title") );
				check.setChecked(false);
				group.addView(check);
			}
		}




		this.body.addView( group );
	}


	/*! Seta a visibilidade de todas as SubPerguntas da Pergunta
	 *  @param flag O valor da visibilidade,o qual pode ser [View.VISIBLE,View.GONE]
	 */
	protected void setSubVisibity(int flag){
		for (int i=0; i<this.box.size(); i++){
			this.box.get(i).setVisibity(flag);
		}
	}

	/*----------------------------------------------------------------------------------------*/

}