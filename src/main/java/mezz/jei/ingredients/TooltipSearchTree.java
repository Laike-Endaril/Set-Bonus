package mezz.jei.ingredients;

import mezz.jei.config.Config;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.suffixtree.GeneralizedSuffixTree;

public class TooltipSearchTree extends PrefixedSearchTree
{
    public TooltipSearchTree()
    {
        super(new GeneralizedSuffixTree(), null, null);
    }

    @Override
    public PrefixedSearchTree.IStringsGetter getStringsGetter()
    {
        return IIngredientListElement::getTooltipStrings;
    }

    @Override
    public Config.SearchMode getMode()
    {
        return Config.getTooltipSearchMode();
    }
}
