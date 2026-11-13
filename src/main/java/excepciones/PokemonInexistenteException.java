package excepciones;

public class PokemonInexistenteException extends Exception{


    public PokemonInexistenteException(String mensaje) {
        super(mensaje);
    }
}
