package pkDataAccess.pkDTO;

public class UsuarioPoliComedor {
    private String idUsuario;
    private String nombre;
    private String apellido;
    private String contrasenia;
    private String tipo;
    public String getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getContrasenia() {
        return contrasenia;
    }
    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String toString() {
        return "UsuarioPoliComedor [idUsuario=" + idUsuario + ", nombre=" + nombre + ", apellido=" + apellido
                + ", contrasenia=" + contrasenia + ", tipo=" + tipo + "]";
    }

}
