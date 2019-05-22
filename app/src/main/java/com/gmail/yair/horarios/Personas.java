package com.gmail.yair.horarios;


public class Personas {
    private String uid;
    private String Materia;
    private String Horario;
    private String Dia;
    private String Edificio;
    private String Salon;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMateria(){
        return Materia;
    }

    public void setMateria (String materia){
        Materia=materia;
    }

    public String getHorario(){
        return Horario;
    }

    public void setHorario (String horario){
        Horario=horario;
    }

    public String getDia(){
        return Dia;
    }

    public void setDia (String dia){
        Dia=dia;
    }

    public String getEdificio(){
        return Edificio;
    }
    public void setEdificio(String edificio){
        Edificio=edificio;
    }

    public String getSalon(){
        return Salon;
    }

    public void setSalon (String salon){
        Salon =salon;
    }
    @Override
    public String toString() {
        return Materia;
    }
}
