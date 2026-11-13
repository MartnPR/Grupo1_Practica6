public class Alimento {
    private Integer idAlimento;
    private String nombre;
    private String tipo;
    private String descripcion;
    private int cantidadEnStock;
    private double precioUnitario;

    public Alimento() {
    }

    public Alimento(String nombre, int tipo, String descripcion, int cantidadEnStock) {
        this.nombre = nombre;
        this.tipo = String.valueOf(tipo);
        this.descripcion = descripcion;
        this.cantidadEnStock = cantidadEnStock;
    }

    public Alimento(String nombre, String tipo, String descripcion, int cantidadEnStock) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.cantidadEnStock = cantidadEnStock;
    }

    public Integer getIdAlimento() {
        return idAlimento;
    }

    public void setIdAlimento(Integer idAlimento) {
        this.idAlimento = idAlimento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidadEnStock() {
        return cantidadEnStock;
    }

    public void setCantidadEnStock(int cantidadEnStock) {
        this.cantidadEnStock = cantidadEnStock;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    @Override
    public String toString() {
        return "Alimento: Representa un producto alimenticio del menú con su stock disponible y precio unitario.";
    }
}