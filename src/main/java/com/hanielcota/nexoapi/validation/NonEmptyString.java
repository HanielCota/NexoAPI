package com.hanielcota.nexoapi.validation;

import java.util.Objects;

/**
 * Utilitário para validação de strings não vazias com normalização.
 * Usado por value objects que representam nomes, labels, identificadores, etc.
 * <p>
 * Este utilitário centraliza a lógica de validação e normalização que era
 * duplicada em múltiplas classes do sistema de comandos.
 * </p>
 * <p>
 * Operações realizadas:
 * <ul>
 *   <li>Validação de null</li>
 *   <li>Remoção de espaços em branco (trim)</li>
 *   <li>Validação de string vazia</li>
 *   <li>Conversão para lowercase</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
public final class NonEmptyString {
    
    private NonEmptyString() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Valida e normaliza uma string não vazia.
     * <p>
     * Aplica as seguintes transformações:
     * <ol>
     *   <li>Valida que value não é null</li>
     *   <li>Remove espaços em branco do início e fim</li>
     *   <li>Valida que não é vazia após trim</li>
     *   <li>Converte para lowercase</li>
     * </ol>
     * </p>
     *
     * @param value     a string a validar e normalizar
     * @param fieldName nome do campo para mensagens de erro contextualizadas
     * @return a string normalizada (trimmed e lowercase)
     * @throws NullPointerException     se value é null
     * @throws IllegalArgumentException se value é vazia após trim
     */
    public static String validateAndNormalize(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null.");
        
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank.");
        }
        
        return trimmed.toLowerCase();
    }
    
    /**
     * Valida e normaliza sem conversão para lowercase.
     * Útil para valores que devem preservar case (como senhas).
     *
     * @param value     a string a validar e normalizar
     * @param fieldName nome do campo para mensagens de erro
     * @return a string normalizada (apenas trimmed)
     * @throws NullPointerException     se value é null
     * @throws IllegalArgumentException se value é vazia após trim
     */
    public static String validateAndTrim(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null.");
        
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank.");
        }
        
        return trimmed;
    }
    
    /**
     * Apenas valida que a string não é null ou vazia (sem normalização).
     * Útil para pré-validações ou casos especiais.
     *
     * @param value     a string a validar
     * @param fieldName nome do campo para mensagens de erro
     * @throws NullPointerException     se value é null
     * @throws IllegalArgumentException se value é vazia após trim
     */
    public static void validate(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null.");
        
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank.");
        }
    }
}