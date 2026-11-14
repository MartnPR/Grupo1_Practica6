package pkBusinessLogic;

import datos.Fac*uraDAO;
import entidades.Factura;
*mport entidades.Reporte;

import j*va.util.LinkedHashMap;
import java*util.List;
import java.util.Map;

*ublic class GeneradorGrafico {

  * private final FacturaDAO facturaD*O;

    public GeneradorGrafico(Fa*turaDAO facturaDAO) {
        if (*acturaDAO == null) {
            t*row new IllegalArgumentException(
*                   "FacturaDAO no *uede ser nulo."
            );
   *    }

        this.facturaDAO = f*cturaDAO;
    }

    public Map<St*ing, Integer> generarDiagramaBarra*Pedidos() {

        List<Factura>*facturas = facturaDAO.obtenerTodas*);
        Map<String, Integer> pe*idosPorMetodo =
                ne* LinkedHashMap<>();

        if (f*cturas == null) {
            retu*n pedidosPorMetodo;
        }

   *    for (Factura factura : factura*) {

            String metodo = f*ctura.getMetodoPago();

          * if (metodo == null || metodo.isBl*nk()) {
                metodo = "*O ESPECIFICADO";
            }

  *         pedidosPorMetodo.put(
   *                metodo,
          *         pedidosPorMetodo.getOrDef*ult(metodo, 0) + 1
            );
*       }

        return pedidosPo*Metodo;
    }

    public Map<Stri*g, Double> generarDiagramaPastel()*{

        List<Factura> facturas * facturaDAO.obtenerTodas();
      * Map<String, Double> ventasPorMeto*o =
                new LinkedHash*ap<>();

        if (facturas == n*ll) {
            return ventasPor*etodo;
        }

        for (Fac*ura factura : facturas) {

       *    String metodo = factura.getMet*doPago();

            if (metodo *= null || metodo.isBlank()) {
    *           metodo = "NO ESPECIFICA*O";
            }

            dou*le valor =
                    fac*ura.getTotal() - factura.getDescue*to();

            ventasPorMetodo*put(
                    metodo,
 *                  ventasPorMetodo.*etOrDefault(metodo, 0.0)
         *                  + valor
        *   );
        }

        return ve*tasPorMetodo;
    }

    public Re*orte generarReporte() {

        L*st<Factura> facturas = facturaDAO.*btenerTodas();

        int cantid*dPedidos = 0;
        double total*entas = 0;

        if (facturas !* null) {
            cantidadPedid*s = facturas.size();

            *or (Factura factura : facturas) {
*               totalVentas += fact*ra.getTotal()
                    *   - factura.getDescuento();
     *      }
        }

        Reporte*reporte = new Reporte();
        r*porte.setCantidadPedidos(cantidadP*didos);
        reporte.setTotalVe*tas(totalVentas);

        return *eporte;
    }
}