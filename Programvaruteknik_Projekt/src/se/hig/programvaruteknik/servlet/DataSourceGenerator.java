/**
 * 
 */
package se.hig.programvaruteknik.servlet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import se.hig.programvaruteknik.model.DataSourceBuilder;
import se.hig.programvaruteknik.model.Param;
import se.hig.programvaruteknik.model.SourceGenerator;

/**
 * A generator that generates a JSON stucture that can be used by clients to
 * list sources from data sources on the server side
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class DataSourceGenerator
{
    private List<Class<? extends DataSourceBuilder>> builders = new ArrayList<>();

    /**
     * Makes a datasource generator
     */
    public DataSourceGenerator()
    {

    }

    /**
     * Makes a datasource generator and registers builder
     * 
     * @param builders
     *            The builder to register
     */
    public DataSourceGenerator(Class<? extends DataSourceBuilder>... builders)
    {
	this();
	registerBuilders(builders);
    }

    /**
     * Register a new builder to the generator
     * 
     * @param builders
     *            The builders to add
     */
    public void registerBuilders(Class<? extends DataSourceBuilder>... builders)
    {
	if (builders == null) return;

	for (Class<? extends DataSourceBuilder> builder : builders)
	{
	    this.builders.add(builder);
	}
    }

    /**
     * Gets a list of the inforamtion about the supplied builders
     * 
     * @param builders
     *            The builders to get information about
     * @return Outputter thta outputs the JSON
     */
    public Map<String, Object> getSources()
    {
	TreeMap<String, Object> data = new TreeMap<String, Object>();
	for (Class<? extends DataSourceBuilder> builder : builders)
	{
	    data.putAll(getGenerators(builder));
	}

	return data;
    }

    /**
     * Get a builder form a JSON request
     * 
     * @param JSONRequest
     *            The request that requests a builder
     * @return The builder that the request requests
     * @throws Exception
     *             If errors
     */
    public DataSourceBuilder getBuilder(Map<String, Object> JSONRequest) throws Exception
    {
	Map<String, Method> generators = getSourceGenerators();
	Method generator = generators.get(JSONRequest.get("source-type"));
	generator.setAccessible(true);

	List<Object> arguments = new ArrayList<>();

	for (Parameter param : generator.getParameters())
	{
	    if (param.isAnnotationPresent(Param.class))
	    {
		Param annotation = param.getDeclaredAnnotation(Param.class);

		if (annotation.onlyEnum())
		{
		    arguments.add(
			    annotation
				    .suggestEnum()
				    .getDeclaredField(JSONRequest.get(annotation.value()).toString())
				    .get(null));
		}
		else
		{
		    arguments.add(JSONRequest.get(annotation.value()).toString());
		}
	    }
	    else
		throw new RuntimeException("Missing Param annotation in generator function");
	}

	return (DataSourceBuilder) generator.invoke(null, arguments.toArray(new Object[arguments.size()]));
    }

    private Map<String, Method> getSourceGenerators()
    {
	Map<String, Method> sourceGenerators = new TreeMap<String, Method>();
	for (Class<? extends DataSourceBuilder> builder : builders)
	{
	    for (Method method : getSourceGenerators(builder))
	    {
		sourceGenerators.put(method.getDeclaredAnnotation(SourceGenerator.class).value(), method);
	    }
	}
	return sourceGenerators;
    }

    private List<Method> getSourceGenerators(Class<?> target)
    {
	List<Method> sourceGenerators = new ArrayList<Method>();
	for (Method method : target.getDeclaredMethods())
	{
	    if (method.isAnnotationPresent(SourceGenerator.class))
	    {
		sourceGenerators.add(method);
	    }
	}
	return sourceGenerators;
    }

    private Map<String, Object> getGenerators(Class<? extends DataSourceBuilder> classToGetMethodsFrom)
    {
	Map<String, Object> information = new TreeMap<>();

	for (Method method : getSourceGenerators(classToGetMethodsFrom))
	{
	    TreeMap<String, Object> classInfo = new TreeMap<String, Object>();
	    classInfo.put("inputs", extractParamInfo(method));
	    information.put(method.getAnnotation(SourceGenerator.class).value(), classInfo);
	}

	return information;
    }

    private List<Map<String, Object>> extractParamInfo(Method method)
    {
	List<Map<String, Object>> information = new LinkedList<>();

	for (Parameter param : method.getParameters())
	{
	    if (param.isAnnotationPresent(Param.class))
	    {
		Param annotation = param.getDeclaredAnnotation(Param.class);
		TreeMap<String, Object> paramInfo = new TreeMap<String, Object>();
		paramInfo.put("name", annotation.name());
		paramInfo.put("value", annotation.value());
		paramInfo.put("type", annotation.onlyEnum() ? "enum" : "string");
		paramInfo.put("values", getValues(annotation.suggestEnum()));
		information.add(paramInfo);
	    }
	    else
		throw new RuntimeException("Missing Param annotation in generator function");
	}

	return information;
    }

    private <T> List<Map<String, Object>> getValues(Class<T> enumish)
    {
	List<Map<String, Object>> list = new LinkedList<>();

	if (enumish != null)
	{
	    for (Field f : enumish.getDeclaredFields())
	    {
		if (f.isEnumConstant())
		{
		    Map<String, Object> map = new TreeMap<String, Object>();

		    map.put("value", f.getName());

		    try
		    {
			Method m = enumish.getDeclaredMethod("getName");
			map.put("name", m.invoke(f.get(null)));
		    }
		    catch (Exception e)
		    {
			map.put("name", f.getName());
		    }

		    list.add(map);
		}
	    }
	}

	return list;
    }
}
