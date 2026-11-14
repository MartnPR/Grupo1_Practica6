package pkDataAccess.pkIDataAccess;

import pkDataAccess.pkDTO.UsuarioPoliComedor;

public interface IUsuarioDataHelper extends IDataHelperSQL<UsuarioPoliComedor, String> {
    UsuarioPoliComedor buscarPorTipo(String tipo);
    UsuarioPoliComedor buscarPorCodigoUnico(String codigoUnico);
    UsuarioPoliComedor buscarPorCedula(String cedula);
}
