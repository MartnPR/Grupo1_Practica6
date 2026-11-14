package pkDataAccess;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Reserva {
    private Integer idReserva;
    private Date fechaCreacion;
    private String franjaHoraria;
    private String codigoEstudiante;
    private String cedulaProfesor;
    private List<Mesa> listMesa;

    
    public Reserva() {
    }

    public Reserva(String codigoEstudiante, String franjaHoraria) {
        this.codigoEstudiante = codigoEstudiante;
        this.franjaHoraria = franjaHoraria;
        this.fechaCreacion = new Date();
        this.listMesa = new ArrayList<>();
    }

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFranjaHoraria() {
        return franjaHoraria;
    }

    public void setFranjaHoraria(String franjaHoraria) {
        this.franjaHoraria = franjaHoraria;
    }

    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }

    public void setCodigoEstudiante(String codigoEstudiante) {
        this.codigoEstudiante = codigoEstudiante;
    }

    public String getCedulaProfesor() {
        return cedulaProfesor;
    }

    public void setCedulaProfesor(String cedulaProfesor) {
        this.cedulaProfesor = cedulaProfesor;
    }

    public List<Mesa> getListMesa() {
        return listMesa;
    }

    public void setListMesa(List<Mesa> listMesa) {
        this.listMesa = listMesa;
    }


    @Override
    public String toString() {
        return "Reserva: Representa la reservación de una o más mesas en una franja horaria específica para un estudiante o profesor.";
    }
}
