import java.util.Date;

public class Factura {
    private String idFactura;
    private Date fecha;
    private List<LineaFactura> lineaFactura;
    private Integer numeroPedido;
    private String metodoPago;
    private double total;
    private double descuento;
    private Ticket ticket;

    public Factura() {
    }

    public Factura(Date fecha, Integer numeroPedido, String metodoPago) {
        this.fecha = fecha;
        this.numeroPedido = numeroPedido;
        this.metodoPago = metodoPago;
        this.lineaFactura = new Arraylist<>();
    }

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
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
}
