package pkDataAccess.pkDTO;

import java.util.Date;

public class Reserva {
    private Integer idReserva;
    private Date fechaCreacion;
    private String franjaHoraria;
    private boolean cancelada;
    private Mesa mesa;
    private UsuarioPoliComedor usuario;

    
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


    @Override
    public String toString() {
        return "Reserva: Representa la reservación de una o más mesas en una franja horaria específica para un estudiante o profesor.";
    }
}
