package pkDataAccess.pkIDataAccess;

import pkDataAccess.Mesa;
import java.util.List;

public interface IMesaDataHelper extends IDataHelperSQL<Mesa, Integer> {
    List<Mesa> buscarPorEstado(boolean reservado);
}
