package pkBusinessLogic;

import java.util.List;

import pkDataAccess.pkDAO.FacturaDAO;
import pkDataAccess.pkDAO.ReporteDAO;
import pkDataAccess.pkDAO.ReservaDAO;
import pkDataAccess.pkDTO.Factura;
import pkDataAccess.pkDTO.Reporte;

public class CalcularEstadistica {

    private final FacturaDAO facturaDAO;
    private final ReservaDAO reservaDAO;
    private final ReporteDAO reporteDAO;

    public CalcularEstadistica() {

        this.facturaDAO =
                new FacturaDAO();

        this.reservaDAO =
                new ReservaDAO();

        this.reporteDAO =
                new ReporteDAO();
    }

    public double calcularTotalVentas() {

        List<Factura> facturas =
                facturaDAO.leerTodos();

        double totalVentas = 0;

        if (facturas == null) {
            return totalVentas;
        }

        for (Factura factura : facturas) {

            totalVentas +=
                    factura.getTotal()
                    - factura.getDescuento();
        }

        return totalVentas;
    }

    public int calcularTotalComidasVendidas() {

        /*
         * Temporalmente se toma la cantidad
         * de facturas como cantidad de pedidos.
         *
         * Si se requiere la cantidad exacta
         * de alimentos, debe calcularse desde
         * las líneas de factura.
         */

        List<Factura> facturas =
                facturaDAO.leerTodos();

        return facturas == null
                ? 0
                : facturas.size();
    }

    public int calcularTotalReservasHechas() {

        return reservaDAO.getCount();
    }

    public Reporte generarReporte() {

        Reporte reporte =
                new Reporte();

        reporte.setTotalVentas(
                calcularTotalVentas()
        );

        reporte.setTotalPedidos(
                calcularTotalComidasVendidas()
        );

        reporte.setTotalReservas(
                calcularTotalReservasHechas()
        );

        reporteDAO.agregar(reporte);

        return reporte;
    }
}