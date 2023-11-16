package test;

public class ProgramacionFluida {

    public static void main(String[] args) {
        Persona jean = new Persona();
        jean.setAltura(1.75).setEdad(18).setNombre("Jean Franco").setPeso(75);
        jean.mostrarInformacion();

    }

}
class Persona {
    private String nombre, apellido;
    private int edad;
    private double peso, altura;
    public void mostrarInformacion(){
        System.out.println("El nombre es " + nombre + " la edad es " + edad + " el peso es " + peso + " la altura es " + altura);
    }
    public Persona setNombre(String nombre){
        this.nombre = nombre;
        return this;
    }
    public Persona setEdad(int edad){
        this.edad = edad;
        return this;
    }
    public Persona setPeso(double peso){
        this.peso = peso;
        return this;
    }
    public Persona setAltura(double altura){
        this.altura = altura;
        return this;
    }
    public void setSegundoNombre(String segundoNombre){

    }
}
