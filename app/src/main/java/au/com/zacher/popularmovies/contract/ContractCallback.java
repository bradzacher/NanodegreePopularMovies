package au.com.zacher.popularmovies.contract;

/**
 * Created by Brad on 12/07/2015.
 */
public interface ContractCallback<T> {
    public void success(T result);

    public void failure(Exception e);
}
