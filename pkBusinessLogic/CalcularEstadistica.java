package pkBusinessLogic;

import d*tos.FacturaDAO;
import datos.Repor*eDAO;
import datos.ReservaDAO;
imp*rt entidades.Factura;
import entid*des.Reporte;

import java.util.Lis*;

public class CalcularEstadistic* {

    private final FacturaDAO f*cturaDAO;
    private final Reserv*DAO reservaDAO;
    private final *eporteDAO reporteDAO;

    public *alcularEstadistica(
            Fa*turaDAO facturaDAO,
            Re*ervaDAO reservaDAO,
            Re*orteDAO reporteDAO) {

        if *facturaDAO == null) {
            *hrow new IllegalArgumentException(*                    "FacturaDAO no*puede ser nulo."
            );
  *     }

        if (reservaDAO == *ull) {
            throw new Illeg*lArgumentException(
              *     "ReservaDAO no puede ser nulo*"
            );
        }

      * if (reporteDAO == null) {
       *    throw new IllegalArgumentExcep*ion(
                    "ReporteD*O no puede ser nulo."
            *;
        }

        this.facturaD*O = facturaDAO;
        this.reser*aDAO = reservaDAO;
        this.reporteDAO = reporteDAO;
    }

    public double calcularTotalVentas() {

        List<Factura> facturas = facturaDAO.obtenerTodas();
        double totalVentas = 0;

        if (facturas == null) {
            return totalVentas;
        }

        for (Factura factura : facturas) {
            totalVentas += factura.getTotal()
                    - factura.getDescuento();
        }

        return totalVentas;
    }

    public int calcularTotalComidasVendidas() {

        /*
         * Si cada factura equivale a un pedido, puede
         * utilizarse temporalmente la cantidad de facturas.
         *
         * Si una factura contiene varias comidas,
         * debe sumarse la cantidad de sus detalles.
         */

        List<Factura> facturas*= facturaDAO.obtenerTodas();

    *   return facturas == null ? 0 : f*cturas.size();
    }

    public i*t calcularTotalReservasHechas() {
*       return reservaDAO.contarRes*rvas();
    }

    public Reporte *enerarReporte() {

        Reporte*reporte = new Reporte();

        *eporte.setTotalVentas(calcularTota*Ventas());
        reporte.setCant*dadComidas(
                calcul*rTotalComidasVendidas()
        );*        reporte.setCantidadReserva*(
                calcularTotalRes*rvasHechas()
        );

        b*olean guardado = reporteDAO.guarda*(reporte);

        if (!guardado)*{
            throw new IllegalSta*eException(
                    "N* se pudo guardar el reporte."
    *       );
        }

        retur* reporte;
    }
}