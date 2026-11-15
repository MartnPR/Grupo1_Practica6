package pkBusinessLogic;

public class AutenticarUsuario {

    public AutenticarUsuario() {
    }

    public boolean autenticar(
            String metodo,
            String usuario,
            String pasw) {

        if (metodo == null) {
            return false;
        }

        switch (metodo.toLowerCase()) {

            case "estudiante":
                try {
                    return autenticarEstudiante(
                            Integer.parseInt(usuario));
                } catch (NumberFormatException e) {
                    return false;
                }

            case "personal":
                return autenticarPersonal(usuario, pasw);

            case "profesor":
                try {
                    return autenticarProfesor(
                            Integer.parseInt(usuario), pasw);
                } catch (NumberFormatException e) {
                    return false;
                }

            default:
                return false;
        }
    }

    private boolean autenticarEstudiante(int codigoUnico) {
        // Validar código único
        return codigoUnico > 0;
    }

    private boolean autenticarPersonal(
            String usuario,
            String pasw) {

        return usuario != null
                && !usuario.isBlank()
                && pasw != null
                && !pasw.isBlank();
    }

    private boolean autenticarProfesor(
            int cedula,
            String pasw) {

        return cedula > 0
                && pasw != null
                && !pasw.isBlank();
    }
}
