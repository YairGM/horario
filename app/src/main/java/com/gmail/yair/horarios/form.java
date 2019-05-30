package com.gmail.yair.horarios;

import android.app.TimePickerDialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;



public class form extends AppCompatActivity {

    private List<Personas> listPerson = new ArrayList<Personas>();
    ArrayAdapter<Personas> arrayAdapterPersona;

    EditText nomM, horaentrada,horasalida,salon;
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
        setContentView(R.layout.activity_form);

        nomM=findViewById(R.id.materia);
        horaentrada=findViewById(R.id.horarioentrada);
        horasalida=findViewById(R.id.horariosalida);
        dia=findViewById(R.id.dia);
        edificios=findViewById(R.id.edificio);
        salon=findViewById(R.id.salon);
        ctrldia=findViewById(R.id.txtdia);
        ctrledi=findViewById(R.id.txtedificio);

        listV_personas = findViewById(R.id.lv_datosPersonas);


        horaentrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendario = Calendar.getInstance();
                int currentHour = calendario.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendario.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog=new TimePickerDialog(form.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay>=12){
                            ampm = "PM";
                        }
                        else{
                            ampm = "AM";
                        }
                        horaentrada.setText(String.format("%02d:%02d", hourOfDay, minute)+ampm);
                        horaentrada.setText(hourOfDay + ":" + minute);
                    }
                }, 0, 0, false);
                timePickerDialog.show();

            }
        });
        horasalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendario = Calendar.getInstance();
                int currentHour = calendario.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendario.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog=new TimePickerDialog(form.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay>=12){
                            ampm = "PM";
                        }
                        else{
                            ampm = "AM";
                        }
                        horasalida.setText(String.format("%02d:%02d", hourOfDay, minute)+ampm);
                        horasalida.setText(hourOfDay + ":" + minute);
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
                horaentrada.setText(personaSelected.getHoraentrada());
                horasalida.setText(personaSelected.getHorasalida());
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

                    arrayAdapterPersona = new ArrayAdapter<Personas>(form.this, android.R.layout.simple_list_item_1, listPerson);
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
        String horarios = horaentrada.getText().toString();
        String horariosalida = horasalida.getText().toString();
        String dias = dia.getSelectedItem().toString();
        String edificio = edificios.getSelectedItem().toString();
        String salones = salon.getText().toString();

        switch (item.getItemId()){
            case R.id.action_guarda:{
                if (nombremateria.equals("")||horaentrada.equals("")||horariosalida.equals("")||edificio.equals("")||dias.equals("")||salones.equals("")){
                    validacion();
                }
                else {
                    Personas p = new Personas();
                    p.setUid(UUID.randomUUID().toString());
                    p.setMateria(nombremateria);
                    p.setHoraentrada(horarios);
                    p.setHorasalida(horariosalida);
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
                p.setHoraentrada(horaentrada.getText().toString().trim());
                p.setHorasalida(horasalida.getText().toString().trim());
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
            case R.id.action_cerrar:{
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(form.this, login.class));
                Toast.makeText(this,"Sesion cerrada", Toast.LENGTH_LONG).show();
                break;
            }
            default:break;
        }
        return true;
    }

    private void limpiarCajas() {
        nomM.setText("");
        horaentrada.setText("");
        horasalida.setText("");
        salon.setText("");
        dia.setSelection(0);
        edificios.setSelection(0);
        ctrledi.setText("");
        ctrldia.setText("");
    }
    private void validacion() {
        String nombremateria = nomM.getText().toString();
        String horarios = horaentrada.getText().toString();
        String horariosalida = horasalida.getText().toString();
        String dias = dia.getSelectedItem().toString();
        String edificio = edificios.getSelectedItem().toString();
        String salones = salon.getText().toString();
        if (nombremateria.equals("")){
            nomM.setError("Ingresar materia");
        }
        else if (horarios.equals("")){
            horaentrada.setError("Ingresar hora de entrada");
        }
        else if (horariosalida.equals("")){
            horasalida.setError("Ingresar hora de salida");
        }
        else if (salones.equals("")){
            salon.setError("Ingresar salon");
        }
    }

}
