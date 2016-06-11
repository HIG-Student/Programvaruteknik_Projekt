/**
 * 
 */
package se.hig.programvaruteknik.servlet;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import se.hig.programvaruteknik.data.StockSourceBuilder;
import se.hig.programvaruteknik.model.DataSourceBuilder;
import se.hig.programvaruteknik.model.Param;
import se.hig.programvaruteknik.model.SourceGenerator;

/**
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
@SuppressWarnings("javadoc")
@RunWith(PowerMockRunner.class)
@PrepareForTest(
{
	StockSourceBuilder.class,
	StockSourceBuilder.StockInfo.class,
	StockSourceBuilder.StockName.class
})
public class TestDataSourceGenerator
{
    private DataSourceGenerator generator;

    @Before
    public void setUp()
    {
	generator = new DataSourceGenerator();
    }

    @Test
    public void testGetNoSource()
    {
	assertEquals(new TreeMap<String, Object>(), generator.getSources());
    }

    @Test
    public void testSetupNull()
    {
	new DataSourceGenerator(null);
    }

    @Test
    public void testRegisterNull()
    {
	generator.registerBuilders(null);
    }

    @Test
    public void testGetEmptySource()
    {
	generator.registerBuilders(ExampleEmptyBuilder.class);

	assertEquals(new TreeMap<String, Object>()
	{
	    {
		put("Example - Empty", new TreeMap<String, Object>()
		{
		    {
			put("inputs", new ArrayList<Object>());
		    }
		});
	    }
	}, generator.getSources());
    }

    @Test
    public void testGetExampleSource()
    {
	generator.registerBuilders(ExampleBuilder.class);

	assertEquals(new TreeMap<String, Object>()
	{
	    {
		put("Example", new TreeMap<String, Object>()
		{
		    {
			put("inputs", new ArrayList<Object>()
			{
			    {
				add(new TreeMap<String, Object>()
				{
				    {
					put("name", "Value a");
					put("type", "enum");
					put("value", "value-a");
					put("values", new ArrayList<Object>()
					{
					    {
						add(new TreeMap<String, Object>()
						{
						    {
							put("name", "ValA-A");
							put("value", "A");
						    }
						});
						add(new TreeMap<String, Object>()
						{
						    {
							put("name", "ValA-B");
							put("value", "B");
						    }
						});
					    }
					});
				    }
				});

				add(new TreeMap<String, Object>()
				{
				    {
					put("name", "Value b");
					put("type", "string");
					put("value", "value-b");
					put("values", new ArrayList<Object>()
					{
					    {
						add(new TreeMap<String, Object>()
						{
						    {
							put("name", "ValB-A");
							put("value", "A");
						    }
						});
						add(new TreeMap<String, Object>()
						{
						    {
							put("name", "ValB-B");
							put("value", "B");
						    }
						});
					    }
					});
				    }
				});
			    }
			});
		    }
		});
	    }
	}, generator.getSources());
    }

    @Test
    public void testCreateEmptySource() throws Exception
    {
	generator.registerBuilders(ExampleEmptyBuilder.class);

	assertNotNull(generator.getBuilder(new TreeMap<String, Object>()
	{
	    {
		put("source-type", "Example - Empty");
	    }
	}));
    }

    @Test
    public void testCreateExampleSource() throws Exception
    {
	generator.registerBuilders(ExampleBuilder.class);

	assertNotNull(generator.getBuilder(new TreeMap<String, Object>()
	{
	    {
		put("source-type", "Example");

		put("value-a", "A");
		put("value-b", "B");
	    }
	}));
    }

    @Test(expected = RuntimeException.class)
    public void testCreateNoParamEmptySource() throws Exception
    {
	generator.registerBuilders(NoParamClassBuilder.class);

	assertNotNull(generator.getBuilder(new TreeMap<String, Object>()
	{
	    {
		put("source-type", "Example - Empty");
	    }
	}));
    }

    @Test(expected = RuntimeException.class)
    public void testGetNoParamEmptySource()
    {
	generator.registerBuilders(NoParamClassBuilder.class);

	assertEquals(new TreeMap<String, Object>()
	{
	    {
		put("Example - Empty", new TreeMap<String, Object>()
		{
		    {
			put("inputs", new ArrayList<Object>());
		    }
		});
	    }
	}, generator.getSources());
    }

    static class NoParamClassBuilder extends DataSourceBuilder
    {
	@Override
	protected Map<LocalDate, List<Double>> generateData()
	{
	    return null;
	}

	@SourceGenerator("Example - Empty")
	private static DataSourceBuilder generate(String noParamString)
	{
	    return new ExampleBuilder();
	}
    }

    static class ExampleEmptyBuilder extends DataSourceBuilder
    {
	@Override
	protected Map<LocalDate, List<Double>> generateData()
	{
	    return null;
	}

	@SourceGenerator("Example - Empty")
	private static DataSourceBuilder generate()
	{
	    return new ExampleBuilder();
	}
    }

    static class ExampleBuilder extends DataSourceBuilder
    {
	static enum ExampleEnumA
	{
	    A("ValA-A"), B("ValA-B");

	    private String name;

	    public String getName()
	    {
		return name;
	    }

	    private ExampleEnumA(String name)
	    {
		this.name = name;
	    }
	}

	static enum ExampleEnumB
	{
	    A("ValB-A"), B("ValB-B");

	    private String name;

	    public String getName()
	    {
		return name;
	    }

	    private ExampleEnumB(String name)
	    {
		this.name = name;
	    }
	}

	@Override
	protected Map<LocalDate, List<Double>> generateData()
	{
	    return null;
	}

	@SourceGenerator("Example")
	private static DataSourceBuilder generate(@Param(value = "value-a", name = "Value a", suggestEnum = ExampleEnumA.class, onlyEnum = true) ExampleEnumA info, @Param(value = "value-b", name = "Value b", suggestEnum = ExampleEnumB.class) String name)
	{
	    return new ExampleBuilder();
	}
    }
}
