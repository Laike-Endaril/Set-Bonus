package mezz.jei.search;

import java.io.PrintWriter;
import java.util.Set;

public class GeneralizedSuffixTree<T> implements ISearchStorage<T>
{
    @Override
    public void getSearchResults(String token, Set<T> results)
    {
    }

    @Override
    public void getAllElements(Set<T> results)
    {
    }

    @Override
    public void put(String key, T value)
    {
    }

    @Override
    public String statistics()
    {
        return null;
    }

    @Override
    public void printTree(PrintWriter out, boolean includeSuffixLinks)
    {
    }
}
