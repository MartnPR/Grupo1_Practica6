package pkDataAccess.pkDTO;

import java.util.Date;
import com.google.zxing.common.BitMatrix;

public class Ticket {
    private Integer idTicket;
    private Date fechaGeneracion;
    private boolean valido;
    private BitMatrix codigoQR;

    public Ticket() {
    }

    public Ticket(Integer idTicket, boolean valido, BitMatrix codigoQR) {
        this.idTicket = idTicket;
        this.valido = valido;
        this.codigoQR = codigoQR;
        this.fechaGeneracion = new Date();
    }

    public Integer getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(Integer idTicket) {
        this.idTicket = idTicket;
    }

    public Date getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(Date fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public BitMatrix getCodigoQR() {
        return codigoQR;
    }

    public void setCodigoQR(BitMatrix codigoQR) {
        this.codigoQR = codigoQR;
    }

    
    @Override
    public String toString() {
        return "Ticket: Representa el comprobante de acceso o consumo con código QR y estado de validez.";
    }
}
