/**
 * 
 */
package i5.las2peer.services.servicePackage.metrics;

/**
 * @author sathvik
 *
 */
public interface IEvaluator<E> {
    public void compute();
    public E getValue();
}

