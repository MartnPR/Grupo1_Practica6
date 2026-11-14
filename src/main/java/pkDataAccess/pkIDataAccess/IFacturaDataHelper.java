package pkDataAccess.pkIDataAccess;

import pkDataAccess.Factura;
import java.util.Date;
import java.util.List;

public interface IFacturaDataHelper extends IDataHelperSQL<Factura, String> {
    List<Factura> buscarPorMetodoPago(String metodo);
    List<Factura> buscarPorFecha(Date fecha);
}
