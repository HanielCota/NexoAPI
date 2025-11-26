package com.hanielcota.nexoapi.validation;

import org.jetbrains.annotations.NotNull;

/**
 * Utilitário para trabalhar com strings opcionais.
 * Fornece normalização consistente de valores que podem ser null ou vazios.
 * <p>
 * Este utilitário é usado por value objects que representam campos opcionais
 * como descrições, permissões, mensagens, etc.
 * </p>
 * <p>
 * Operações realizadas:
 * <ul>
 *   <li>Conversão de null para string vazia (ou valor padrão)</li>
 *   <li>Remoção de espaços em branco</li>
 *   <li>Preservação de strings vazias (válidas para campos opcionais)</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
public final class OptionalString {
    
    private OptionalString() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Normaliza uma string opcional usando string vazia como padrão.
     * <p>
     * Comportamento:
     * <ul>
     *   <li>null → ""</li>
     *   <li>"  hello  " → "hello"</li>
     *   <li>"" → ""</li>
     *   <li>"  " → ""</li>
     * </ul>
     * </p>
     *
     * @param value a string a normalizar (pode ser null)
     * @return string normalizada (nunca null)
     */
    @NotNull
    public static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
    
    /**
     * Normaliza uma string opcional com valor padrão customizado.
     * <p>
     * Útil quando você quer um padrão diferente de string vazia.
     * </p>
     *
     * @param value        a string a normalizar (pode ser null)
     * @param defaultValue valor padrão se value é null
     * @return string normalizada ou defaultValue
     */
    @NotNull
    public static String normalizeOrDefault(String value, @NotNull String defaultValue) {
        return value == null ? defaultValue : value.trim();
    }
    
    /**
     * Verifica se uma string opcional está vazia após normalização.
     * <p>
     * Retorna true se value é null, vazia, ou contém apenas espaços.
     * </p>
     *
     * @param value a string a verificar (pode ser null)
     * @return true se vazia, false caso contrário
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    /**
     * Verifica se uma string opcional está presente (não vazia).
     * <p>
     * Retorna true se value não é null e contém algum caractere não-espaço.
     * </p>
     *
     * @param value a string a verificar (pode ser null)
     * @return true se presente, false se vazia
     */
    public static boolean isPresent(String value) {
        return !isEmpty(value);
    }
}

