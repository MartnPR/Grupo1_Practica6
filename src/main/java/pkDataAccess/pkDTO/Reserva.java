package pkDataAccess.pkDTO;

import java.util.Date;
import java.util.List;

public class Reserva {
    private Integer idReserva;
    private Date fechaCreacion;
    private String franjaHoraria;
    private boolean cancelada;
    private Mesa mesa;
    private UsuarioPoliComedor usuario;
    private String codigoEstudiante;
    private String cedulaProfesor;
    private List<Mesa> listMesa;

    
    public Reserva() {
    }

    public Reserva(UsuarioPoliComedor usuario, Mesa mesa, String franjaHoraria) {
        this.usuario = usuario;
        this.franjaHoraria = franjaHoraria;
        this.fechaCreacion = new Date();
        this.mesa = mesa;
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

   
    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public UsuarioPoliComedor getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioPoliComedor usuario) {
        this.usuario = usuario;
    }

    public boolean isCancelada() {
        return cancelada;
    }

    public void setCancelada(boolean cancelada) {
        this.cancelada = cancelada;
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
