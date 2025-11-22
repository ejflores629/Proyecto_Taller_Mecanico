package hn.uth.proyecto_tallermecanico.util;

public class CifradoUtil {

    public static String cifrar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        int desplazamiento = texto.length(); // La llave es el largo
        StringBuilder resultado = new StringBuilder();

        for (char caracter : texto.toCharArray()) {
            char cifrado = (char) (caracter + desplazamiento);
            resultado.append(cifrado);
        }

        return resultado.toString();
    }

    public static String descifrar(String textoCifrado) {
        if (textoCifrado == null || textoCifrado.isEmpty()) {
            return textoCifrado;
        }

        int desplazamiento = textoCifrado.length(); // La llave sigue siendo el largo
        StringBuilder resultado = new StringBuilder();

        for (char caracter : textoCifrado.toCharArray()) {
            // Restamos el desplazamiento para volver al original
            char descifrado = (char) (caracter - desplazamiento);
            resultado.append(descifrado);
        }

        return resultado.toString();
    }
}