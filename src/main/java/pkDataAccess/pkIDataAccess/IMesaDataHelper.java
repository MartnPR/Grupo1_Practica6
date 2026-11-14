package pkDataAccess.pkIDataAccess;

import java.util.List;

import pkDataAccess.pkDTO.Mesa;

public interface IMesaDataHelper extends IDataHelperSQL<Mesa, Integer> {
    List<Mesa> buscarPorEstado(boolean reservado);
}
