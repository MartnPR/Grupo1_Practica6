package pkDataAccess.pkIDataAccess;

import java.util.Date;
import java.util.List;

import pkDataAccess.pkDTO.Factura;

public interface IFacturaDataHelper extends IDataHelperSQL<Factura, String> {
    List<Factura> buscarPorMetodoPago(String metodo);
    List<Factura> buscarPorFecha(Date fecha);
}
