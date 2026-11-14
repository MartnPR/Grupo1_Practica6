package pkDataAccess.pkIDataAccess;

import java.util.Date;
import java.util.List;

import pkDataAccess.pkDTO.Reporte;

public interface IReporteDataHelper extends IDataHelperSQL<Reporte, String> {
    List<Reporte> buscarPorFecha(Date fechaGeneracion);
    List<Reporte> buscarPorFranjaHoraria(String franjaHoraria);
}
