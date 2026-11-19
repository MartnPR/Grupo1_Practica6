package pkDataAccess.pkDTO;

import java.util.Date;
import com.google.zxing.common.BitMatrix;

public class Ticket {
    private Integer idTicket;
    private Date fechaGeneracion;
    private boolean valido;
    private BitMatrix codigoQR;

    /**
     * 1. Nombre: idFactura.
     * 2. Cinco razones para incluirlo: representa la FK idfactura del DDL;
     * permite insertar la relación ticket-factura; permite actualizarla;
     * permite recuperarla en consultas; mantiene completo el mapeo de TICKET.
     * 3. Cinco razones por las que no puede omitirse: la columna es parte de
     * TICKET; omitirla rompe la correspondencia DTO-DDL; INSERT perdería la FK;
     * UPDATE no podría conservar la relación; SELECT devolvería datos incompletos.
     */
    private String idFactura;

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

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    
    @Override
    public String toString() {
        return "Ticket: Representa el comprobante de acceso o consumo con código QR y estado de validez.";
    }
}
