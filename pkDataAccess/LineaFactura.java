public class LineaFactura {
    private Integer numLineaCompra;
    private int cantidad;
    private double precioUnitario;
    private double subtotalAlimento;
    private List<Alimento> alimentoDetalle;

    public LineaFactura() {
    }

    public LineaFactura(int cantidad) {
        this.cantidad = cantidad;
        this.alimentoDetalle = new ArrayList<>();
    }

    public Integer getNumLineaCompra() {
        return numLineaCompra;
    }

    public void setNumLineaCompra(Integer numLineaCompra) {
        this.numLineaCompra = numLineaCompra;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotalAlimento() {
        return subtotalAlimento;
    }

    public void setSubtotalAlimento(double subtotalAlimento) {
        this.subtotalAlimento = subtotalAlimento;
    }

    @Override
    public String toString() {
        return "LineaFactura: Detalla la cantidad, precio unitario y subtotal de un alimento dentro de una factura.";
    }
}
