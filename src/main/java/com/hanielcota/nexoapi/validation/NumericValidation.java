package com.hanielcota.nexoapi.validation;

/**
 * Utilitários para validação de valores numéricos.
 * Fornece validações comuns com mensagens de erro consistentes.
 * <p>
 * Este utilitário centraliza a lógica de validação numérica que era
 * duplicada em múltiplas classes de propriedades (MenuSize, MenuSlot,
 * ItemAmount, DetectionRadius, etc.).
 * </p>
 * <p>
 * Tipos de validação suportados:
 * <ul>
 *   <li>Range validation (min/max inclusive)</li>
 *   <li>Positive validation (> 0)</li>
 *   <li>Non-negative validation (>= 0)</li>
 *   <li>Minimum validation (>= min)</li>
 *   <li>Maximum validation (<= max)</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
public final class NumericValidation {
    
    private NumericValidation() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Valida que um valor int está dentro de um range (inclusivo).
     * <p>
     * Exemplo: validateRange(5, 1, 10, "Amount") valida que 5 está entre 1 e 10.
     * </p>
     *
     * @param value     o valor a validar
     * @param min       valor mínimo permitido (inclusivo)
     * @param max       valor máximo permitido (inclusivo)
     * @param fieldName nome do campo para mensagens de erro
     * @throws IllegalArgumentException se value < min ou value > max
     */
    public static void validateRange(int value, int min, int max, String fieldName) {
        if (value < min) {
            throw new IllegalArgumentException(
                fieldName + " must be at least " + min + ", but was: " + value
            );
        }
        if (value > max) {
            throw new IllegalArgumentException(
                fieldName + " must not exceed " + max + ", but was: " + value
            );
        }
    }
    
    /**
     * Valida que um valor double está dentro de um range (inclusivo).
     */
    public static void validateRange(double value, double min, double max, String fieldName) {
        if (value < min) {
            throw new IllegalArgumentException(
                fieldName + " must be at least " + min + ", but was: " + value
            );
        }
        if (value > max) {
            throw new IllegalArgumentException(
                fieldName + " must not exceed " + max + ", but was: " + value
            );
        }
    }
    
    /**
     * Valida que um valor int é positivo (> 0).
     * <p>
     * Útil para valores que não podem ser zero ou negativos.
     * </p>
     *
     * @param value     o valor a validar
     * @param fieldName nome do campo para mensagens de erro
     * @throws IllegalArgumentException se value <= 0
     */
    public static void validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(
                fieldName + " must be positive, but was: " + value
            );
        }
    }
    
    /**
     * Valida que um valor double é positivo (> 0.0).
     */
    public static void validatePositive(double value, String fieldName) {
        if (value <= 0.0) {
            throw new IllegalArgumentException(
                fieldName + " must be positive, but was: " + value
            );
        }
    }
    
    /**
     * Valida que um valor int não é negativo (>= 0).
     * <p>
     * Útil para índices, contadores, etc. onde zero é válido.
     * </p>
     *
     * @param value     o valor a validar
     * @param fieldName nome do campo para mensagens de erro
     * @throws IllegalArgumentException se value < 0
     */
    public static void validateNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(
                fieldName + " cannot be negative, but was: " + value
            );
        }
    }
    
    /**
     * Valida que um valor double não é negativo (>= 0.0).
     */
    public static void validateNonNegative(double value, String fieldName) {
        if (value < 0.0) {
            throw new IllegalArgumentException(
                fieldName + " cannot be negative, but was: " + value
            );
        }
    }
    
    /**
     * Valida que um valor int é pelo menos o mínimo especificado.
     *
     * @param value     o valor a validar
     * @param min       valor mínimo permitido (inclusivo)
     * @param fieldName nome do campo para mensagens de erro
     * @throws IllegalArgumentException se value < min
     */
    public static void validateMinimum(int value, int min, String fieldName) {
        if (value < min) {
            throw new IllegalArgumentException(
                fieldName + " must be at least " + min + ", but was: " + value
            );
        }
    }
    
    /**
     * Valida que um valor int não excede o máximo especificado.
     *
     * @param value     o valor a validar
     * @param max       valor máximo permitido (inclusivo)
     * @param fieldName nome do campo para mensagens de erro
     * @throws IllegalArgumentException se value > max
     */
    public static void validateMaximum(int value, int max, String fieldName) {
        if (value > max) {
            throw new IllegalArgumentException(
                fieldName + " must not exceed " + max + ", but was: " + value
            );
        }
    }
}

