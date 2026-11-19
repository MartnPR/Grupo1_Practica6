package pkDataAccess.pkDTO;


public class LineaFactura {
    private Integer numLineaFactura;
    private int cantidad;
    private double precioUnitario;
    private double subtotalAlimento;
    private String idFactura;
    private Alimento alimento;

    
    
    public LineaFactura() {
    }

    public LineaFactura(int cantidad, Alimento alimento) {
        this.cantidad = cantidad;
        this.alimento = alimento;
    }

    public Integer getNumLineaFactura() {
        return numLineaFactura;
    }

    public void setNumLineaFactura(Integer numLineaFactura) {
        this.numLineaFactura = numLineaFactura;
    }

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
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

    public Alimento getAlimento() {
        return alimento;
    }

    public void setAlimento(Alimento alimento) {
        this.alimento = alimento;
    }


    @Override
    public String toString() {
        return "LineaFactura: Detalla la cantidad, precio unitario y subtotal de un alimento dentro de una factura.";
    }
}
