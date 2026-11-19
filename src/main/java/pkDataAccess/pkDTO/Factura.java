package pkDataAccess.pkDTO;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Factura {
    private String idFactura;

    /**
     * 1. Nombre: idUsuario.
     * 2. Cinco razones para incluirlo: representa la columna idUsuario del DDL;
     * permite insertar la FK; permite actualizar la FK; permite leer la FK;
     * mantiene la correspondencia completa del DTO con FACTURA.
     * 3. Cinco razones por las que no puede omitirse: el DDL lo define;
     * omitirlo pierde la relación con USUARIO; INSERT quedaría incompleto;
     * UPDATE no podría modificar la relación; SELECT no podría reconstruirla.
     */
    private String idUsuario;
    private Date fecha;
    private List<LineaFactura> lineaFactura;
    

    private Integer numeroPedido;
    private String metodoPago;
    private double total;
    private double descuento;
    private Ticket ticket;

    public Factura() {
        this.lineaFactura = new ArrayList<>();
    }

    public Factura(Date fecha, Integer numeroPedido, String metodoPago) {
        this.fecha = fecha;
        this.numeroPedido = numeroPedido;
        this.metodoPago = metodoPago;
        this.lineaFactura = new ArrayList<>();
    }

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(Integer numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "Factura: Registra los detalles financieros, método de pago y montos totales de un pedido.";
    }

    public List<LineaFactura> getLineaFactura() {
        return lineaFactura;
    }

    public void setLineaFactura(List<LineaFactura> lineaFactura) {
        this.lineaFactura = lineaFactura;
    }
}
