package test;

public class Personas {
    private static String nombre;
    private static Personas personas;

    private Personas(String nombre) {
        this.nombre = nombre;
    }
    private Personas(){

    }
    public static  Personas getInstance(){
        if(personas == null){
            personas = new Personas();
            return personas;
        }
        return personas;
    }

    public static String getNombre() {
        return nombre;
    }
}
