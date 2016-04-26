package se.hig.programvaruteknik;

/**
 * Formatter that prettifies JSON<br>
 * <br>
 * Observe that the validity of the JSON string is not checked!
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class JSONFormatter
{
    private String tab;
    private String newLine;

    /**
     * Creates a formatter with default settings<br>
     * <br>
     * <ul>
     * <li>Tab: \t</li>
     * <li>New line: \n</li>
     * </ul>
     */
    public JSONFormatter()
    {
	this("\t", "\n");
    }

    /**
     * Creates a formatter with specific settings
     * 
     * @param tab
     *            The token to symbolize a tab (usually \t or spaces)
     * @param newLine
     *            The token to symbolize a new line (usually \n or \r\n)
     */
    public JSONFormatter(String tab, String newLine)
    {
	setTab(tab);
	setNewLine(newLine);
    }

    /**
     * Set the tab token to use
     * 
     * @param tab
     *            The token to symbolize a tab (usually \t or spaces)
     * @return This formatter
     */
    public JSONFormatter setTab(String tab)
    {
	this.tab = tab;
	return this;
    }

    /**
     * Set the new-line token to use
     * 
     * @param newLine
     *            The token to symbolize a new line (usually \n or \r\n)
     * @return This formatter
     */
    public JSONFormatter setNewLine(String newLine)
    {
	this.newLine = newLine;
	return this;
    }

    /**
     * Formats a JSON string
     * 
     * @param JSON
     *            The JSON string to format
     * @return The formatted JSON string
     */
    public String format(String JSON)
    {
	StringBuilder builder = new StringBuilder();
	boolean inString = false;
	boolean lastWasBreak = true;
	int level = 0;

	for (char c : JSON.toCharArray())
	{
	    if (c == '"') inString = !inString;
	    if (inString)
	    {
		builder.append(c);
		continue;
	    }
	    else
		if (c == ' ' || c == '\t' || c == '\n' || c == '\r') continue;

	    if (c == '{' || c == '[')
	    {
		if (!lastWasBreak)
		{
		    builder.append(newLine);
		    doTabs(builder, tab, level);
		}
		builder.append(c);
		level++;
		builder.append(newLine);
		doTabs(builder, tab, level);

		lastWasBreak = true;
		continue;
	    }
	    else
		lastWasBreak = false;

	    if (c == '}' || c == ']')
	    {
		level--;
		builder.append(newLine);
		doTabs(builder, tab, level);
		builder.append(c);
		continue;
	    }

	    if (c == ':')
	    {
		builder.append(": ");
		continue;
	    }

	    if (c == ',')
	    {
		builder.append(",");
		builder.append(newLine);
		doTabs(builder, tab, level);
		lastWasBreak = true;
		continue;
	    }
	    else
		lastWasBreak = false;

	    builder.append(c);
	}

	return builder.toString();
    }

    private void doTabs(StringBuilder builder, String tab, int amount)
    {
	for (int i = 0; i < amount; i++)
	    builder.append(tab);
    }

    /**
     * No formatting
     */
    public static JSONFormatter NONE = new JSONFormatter()
    {
	@Override
	public String format(String JSON)
	{
	    return JSON;
	}
    };
}
