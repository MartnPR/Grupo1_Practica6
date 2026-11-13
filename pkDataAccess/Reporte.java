import java.util.Date;

public class Reporte {
    private String idReporte;
    private Date fechaGeneracion;
    private String franjaHoraria;
    private boolean graficos;
    private String descripcion;

    public Reporte() {
    }

    public Reporte(String franjaHoraria, boolean graficos) {
        this.franjaHoraria = franjaHoraria;
        this.graficos = graficos;
        this.fechaGeneracion = new Date();
    }

    public String getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(String idReporte) {
        this.idReporte = idReporte;
    }

    public Date getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(Date fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getFranjaHoraria() {
        return franjaHoraria;
    }

    public void setFranjaHoraria(String franjaHoraria) {
        this.franjaHoraria = franjaHoraria;
    }

    public boolean isGraficos() {
        return graficos;
    }

    public void setGraficos(boolean graficos) {
        this.graficos = graficos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Reporte: Contiene la información estadística generada por franja horaria con opción de incluir gráficos.";
    }
}