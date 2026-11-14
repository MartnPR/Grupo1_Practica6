package pkDataAccess.pkIDataAccess;

import pkDataAccess.Reporte;
import java.util.Date;
import java.util.List;

public interface IReporteDataHelper extends IDataHelperSQL<Reporte, String> {
    List<Reporte> buscarPorFecha(Date fechaGeneracion);
    List<Reporte> buscarPorFranjaHoraria(String franjaHoraria);
}
