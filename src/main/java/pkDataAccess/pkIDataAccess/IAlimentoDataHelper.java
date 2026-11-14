package pkDataAccess.pkIDataAccess;

import java.util.List;

import pkDataAccess.pkDTO.Alimento;

public interface IAlimentoDataHelper extends IDataHelperSQL<Alimento, Integer> {
    List<Alimento> buscarPorTipo(String categoria);
}
