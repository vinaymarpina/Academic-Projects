/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author hkanna
 */
public interface HashFunction<T> {

    public int hashCode(T key, int n);

    public int number();

    public void generateNew();
}
