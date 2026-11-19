package pkDataAccess.pkDTO;

public class UsuarioTipoDTO {
    private String idUsuarioTipo;
    private String descripcion;

    public UsuarioTipoDTO() {
    }

    public UsuarioTipoDTO(String idUsuarioTipo, String descripcion) {
        this.idUsuarioTipo = idUsuarioTipo;
        this.descripcion = descripcion;
    }

    public String getIdUsuarioTipo() {
        return idUsuarioTipo;
    }

    public void setIdUsuarioTipo(String idUsuarioTipo) {
        this.idUsuarioTipo = idUsuarioTipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "UsuarioTipoDTO [idUsuarioTipo=" + idUsuarioTipo
                + ", descripcion=" + descripcion + "]";
    }
}
