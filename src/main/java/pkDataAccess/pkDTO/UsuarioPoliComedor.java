package pkDataAccess.pkDTO;

public class UsuarioPoliComedor {
    private String idUsuario;
    private String nombre;
    private String apellido;
    private String contrasenia;
    private String idUsuarioTipo;
    private UsuarioTipo usuarioTipo;

    public UsuarioPoliComedor() {
    }

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
    public String getIdUsuarioTipo() {
        return idUsuarioTipo;
    }

    public void setIdUsuarioTipo(String idUsuarioTipo) {
        this.idUsuarioTipo = idUsuarioTipo;
    }

    public UsuarioTipo getUsuarioTipo() {
        return usuarioTipo;
    }

    public void setUsuarioTipo(UsuarioTipo usuarioTipo) {
        this.usuarioTipo = usuarioTipo;
    }

    public String getTipo() {
        return usuarioTipo == null ? idUsuarioTipo : usuarioTipo.getDescripcion();
    }

    public void setTipo(String tipo) {
        if (usuarioTipo == null) {
            usuarioTipo = new UsuarioTipo();
        }
        usuarioTipo.setDescripcion(tipo);
    }

    @Override
    public String toString() {
        return "UsuarioPoliComedor [idUsuario=" + idUsuario + ", nombre=" + nombre + ", apellido=" + apellido
                + ", contrasenia=" + contrasenia + ", idUsuarioTipo=" + idUsuarioTipo
                + ", usuarioTipo=" + usuarioTipo + "]";
    }

}
