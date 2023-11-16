package test;

public class Perro {
    private String raza, nombre;

    public Perro(String raza, String nombre) {
        this.raza = raza;
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public Perro setRaza(String raza) {
        this.raza = raza;
        return this;
    }

    public String getNombre() {
        return nombre;
    }

    public Perro setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }
}
class Main9 {
    public static void main(String[] args) {
        Perro perro = new Perro("No se", "Si");
        perro.setRaza("kfrfrf").setNombre("pene");
    }
}