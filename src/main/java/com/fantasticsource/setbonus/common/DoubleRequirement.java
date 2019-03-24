package com.fantasticsource.setbonus.common;

import com.fantasticsource.tools.datastructures.Pair;

public class DoubleRequirement
{
    public static final int
            MODE_GREATER_OR_EQUAL = 0,
            MODE_LESS_OR_EQUAL = 1,
            MODE_EQUAL = 2,
            MODE_GREATER = 3,
            MODE_LESS = 4,
            MODE_NOT_EQUAL = 5;


    int mode;
    double amount;


    public DoubleRequirement(double amount)
    {
        this(0, amount);
    }

    public DoubleRequirement(int mode, double amount)
    {
        this.mode = mode;
        if (mode < 0 || mode > 5) throw new IllegalArgumentException("Unknown mode: " + mode);
        this.amount = amount;
    }

    public static Pair<String, DoubleRequirement> parse(String string)
    {
        Pair<String, DoubleRequirement> result = null;

        try
        {
            String[] tokens;
            if (string.contains(">="))
            {
                tokens = string.split(">=");
                result = new Pair<>(tokens[0].trim(), new DoubleRequirement(0, Double.parseDouble(tokens[1].trim())));
            }
            else if (string.contains("<="))
            {
                tokens = string.split("<=");
                result = new Pair<>(tokens[0].trim(), new DoubleRequirement(1, Double.parseDouble(tokens[1].trim())));
            }
            else if (string.contains("!="))
            {
                tokens = string.split("!=");
                result = new Pair<>(tokens[0].trim(), new DoubleRequirement(5, Double.parseDouble(tokens[1].trim())));
            }
            else if (string.contains("="))
            {
                tokens = string.split("=");
                result = new Pair<>(tokens[0].trim(), new DoubleRequirement(2, Double.parseDouble(tokens[1].trim())));
            }
            else if (string.contains(">"))
            {
                tokens = string.split(">");
                result = new Pair<>(tokens[0].trim(), new DoubleRequirement(3, Double.parseDouble(tokens[1].trim())));
            }
            else if (string.contains("<"))
            {
                tokens = string.split("<");
                result = new Pair<>(tokens[0].trim(), new DoubleRequirement(4, Double.parseDouble(tokens[1].trim())));
            }
        }
        catch (NumberFormatException e)
        {
            return null;
        }

        return result;
    }


    public boolean check(double amount)
    {
        switch (mode)
        {
            case MODE_GREATER_OR_EQUAL:
                return amount >= this.amount;
            case MODE_LESS_OR_EQUAL:
                return amount <= this.amount;
            case MODE_GREATER:
                return amount > this.amount;
            case MODE_LESS:
                return amount < this.amount;
            case MODE_EQUAL:
                return amount == this.amount;
            case MODE_NOT_EQUAL:
                return amount != this.amount;
        }
        return false;
    }
}
