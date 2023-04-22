package Discreet.org;

import static org.apache.commons.math3.special.Erf.erf;

/**
 * This interface contains a method that converts continuous values to binary ones
 */
public interface discretion {

    /**
     * This method used to convert continuous values to binary ones
     * @param TF is the type (id) of the transfer function
     * @param value is the continuous value to be converted to binary one
     * @return return the converted value based on the selected function
     */
    static double DiscreteViaTransfer(TransferFunction TF, double value) {
        // here we use V2-Transfer Function to convert continuous value to binary one
        return switch (TF) {
            case TFV1 -> Math.abs(erf(Math.sqrt(Math.PI) / 2 * value)) >= 0.5 ? 1 : 0;
            case TFV2 -> Math.abs(Math.tan(value)) >= 0.5 ? 1.0 : 0.0;
            case TFV3 -> Math.abs(value / Math.abs(1 + Math.pow(value, 2))) >= 0.5 ? 1 : 0;
            case TFV4 -> Math.abs(2 / Math.PI * Math.atan(Math.PI / 2 * value)) >= 0.5 ? 1 : 0;
        };
    }
}
