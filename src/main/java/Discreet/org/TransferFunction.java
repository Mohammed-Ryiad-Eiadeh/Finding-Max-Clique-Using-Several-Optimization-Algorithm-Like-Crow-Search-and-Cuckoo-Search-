package Discreet.org;

import org.ojalgo.function.special.ErrorFunction;

import java.util.function.DoubleUnaryOperator;

/**<li>transfer functions selector</li>*/

public enum TransferFunction implements DoubleUnaryOperator {
    V1, V2, V3, V4;

    /**
     * Applies this operator to the given operand.
     *
     * @param operand The continuous value to be converted to binary value
     * @return The binary value of the continuous input
     */
    @Override
    public double applyAsDouble(double operand) {
        // here we use V2-Transfer Function to convert continuous value to binary one
        return switch (this) {
            case V1 -> Math.abs(ErrorFunction.erf(Math.sqrt(Math.PI) / 2 * operand)) >= 0.5 ? 1 : 0;
            case V2 -> Math.abs(Math.tan(operand)) >= 0.5 ? 1.0 : 0.0;
            case V3 -> Math.abs(operand / Math.abs(1 + Math.pow(operand, 2))) >= 0.5 ? 1 : 0;
            case V4 -> Math.abs(2 / Math.PI * Math.atan(Math.PI / 2 * operand)) >= 0.5 ? 1 : 0;
        };
    }
}
