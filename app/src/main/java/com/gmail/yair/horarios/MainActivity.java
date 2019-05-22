package com.gmail.yair.horarios;

import android.app.TimePickerDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Spinner;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;



public class MainActivity extends AppCompatActivity {

    private List<Personas> listPerson = new ArrayList<Personas>();
    ArrayAdapter<Personas> arrayAdapterPersona;

    EditText nomM, horario,salon;
    Spinner dia, edificios;
    TextView ctrldia, ctrledi;
    ListView listV_personas;
    Calendar calendario;
    int currentHour;
    int currentMinute;
    String ampm;
    TimePickerDialog timePickerDialog;


    Calendar C = Calendar.getInstance();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Personas personaSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nomM=findViewById(R.id.materia);
        horario=findViewById(R.id.horario);
        dia=findViewById(R.id.dia);
        edificios=findViewById(R.id.edificio);
        salon=findViewById(R.id.salon);
        ctrldia=findViewById(R.id.txtdia);
        ctrledi=findViewById(R.id.txtedificio);

        listV_personas = findViewById(R.id.lv_datosPersonas);


        horario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendario = Calendar.getInstance();
                int currentHour = calendario.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendario.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog=new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay>=12){
                            ampm = "PM";
                        }
                        else{
                            ampm = "AM";
                        }
                        horario.setText(String.format("%02d:%02d", hourOfDay, minute)+ampm);
                        horario.setText(hourOfDay + ":" + minute);
                    }
                }, 0, 0, false);
                timePickerDialog.show();

            }
        });

        inicializarFirebase();
        listarDatos();

        listV_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelected = (Personas) parent.getItemAtPosition(position);
                nomM.setText(personaSelected.getMateria());
                horario.setText(personaSelected.getHorario());
                salon.setText(personaSelected.getSalon());
                ctrldia.setText(personaSelected.getDia());
                ctrledi.setText(personaSelected.getEdificio());
            }
        });
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPerson.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Personas p = objSnaptshot.getValue(Personas.class);
                    listPerson.add(p);

                    arrayAdapterPersona = new ArrayAdapter<Personas>(MainActivity.this, android.R.layout.simple_list_item_1, listPerson);
                    listV_personas.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String nombremateria = nomM.getText().toString();
        String horarios = horario.getText().toString();
        String dias = dia.getSelectedItem().toString();
        String edificio = edificios.getSelectedItem().toString();
        String salones = salon.getText().toString();

        switch (item.getItemId()){
            case R.id.action_guarda:{
                if (nombremateria.equals("")||horarios.equals("")||edificio.equals("")||dias.equals("")||salones.equals("")){
                    validacion();
                }
                else {
                    Personas p = new Personas();
                    p.setUid(UUID.randomUUID().toString());
                    p.setMateria(nombremateria);
                    p.setHorario(horarios);
                    p.setDia(dias);
                    p.setEdificio(edificio);
                    p.setSalon(salones);
                    databaseReference.child("Persona").child(p.getUid()).setValue(p);
                    Toast.makeText(this, "Agregado", Toast.LENGTH_LONG).show();
                    limpiarCajas();
                }
                break;
            }
            case R.id.action_modifica:{
                Personas p = new Personas();
                p.setUid(personaSelected.getUid());
                p.setMateria(nomM.getText().toString().trim());
                p.setHorario(horario.getText().toString().trim());
                p.setDia(dia.getSelectedItem().toString().trim());
                p.setEdificio(edificios.getSelectedItem().toString().trim());
                p.setSalon(salon.getText().toString().trim());
                databaseReference.child("Persona").child(p.getUid()).setValue(p);
                Toast.makeText(this,"Actualizado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }
            case R.id.action_limpia:{
                limpiarCajas();
                break;
            }
            case R.id.action_elimina:{
                Personas p = new Personas();
                p.setUid(personaSelected.getUid());
                databaseReference.child("Persona").child(p.getUid()).removeValue();
                Toast.makeText(this,"Eliminado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }
            default:break;
        }
        return true;
    }

    private void limpiarCajas() {
        nomM.setText("");
        horario.setText("");
        salon.setText("");
        dia.setSelection(0);
        edificios.setSelection(0);
        ctrledi.setText("");
        ctrldia.setText("");
    }
    private void validacion() {
        String nombremateria = nomM.getText().toString();
        String horarios = horario.getText().toString();
        String dias = dia.getSelectedItem().toString();
        String edificio = edificios.getSelectedItem().toString();
        String salones = salon.getText().toString();
        if (nombremateria.equals("")){
            nomM.setError("Required");
        }
        else if (horarios.equals("")){
            horario.setError("Required");
        }
        else if (salones.equals("")){
            salon.setError("Required");
        }
    }

}
