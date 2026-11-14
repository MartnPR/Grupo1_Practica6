package pkDataAccess.pkDTO;

public class Mesa {
    private Integer idMesa;
    private boolean reservada;

    public Mesa() {
    }

    public Mesa(Integer idMesa, boolean reservada) {
        this.idMesa = idMesa;
        this.reservada = reservada;
    }

    public Integer getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(Integer idMesa) {
        this.idMesa = idMesa;
    }

    public boolean isReservada() {
        return reservada;
    }

    public void setReservada(boolean reservada) {
        this.reservada = reservada;
    }

    @Override
    public String toString() {
        return "Mesa: Representa una mesa física del comedor y su estado actual de disponibilidad.";
    }
}
