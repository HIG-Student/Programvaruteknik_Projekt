package se.hig.programvaruteknik.model;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.owlike.genson.Genson;

@SuppressWarnings("javadoc")
public class TestDataCollection
{
    private DataSource createSource(String token)
    {
	return new DataSourceBuilder()
	{
	    {
		setName("[" + token + ":name]");
		setUnit("[" + token + ":unit]");

		setSourceName("[" + token + ":source_name]");
		setSourceLink("[" + token + ":source_link]");
	    }

	    @Override
	    protected Map<LocalDate, List<Double>> generateData()
	    {
		return new TreeMap<>();
	    }
	}.build();
    }

    @Test
    public void test()
    {
	@SuppressWarnings("serial")
	DataCollection collection = new DataCollection(
		"[title]",
		createSource("A"),
		createSource("B"),
		new TreeMap<String, MatchedDataPair>()
		{
		    {
			put("date_1", new MatchedDataPair(1d, 1d));
			put("date_2", new MatchedDataPair(2d, 2d));
		    }
		});

	validateData(collection.asJSON());
	validateData(collection.asJSON(null));
    }

    private void validateData(String JSON)
    {
	@SuppressWarnings("unchecked")
	Map<String, Object> data = (Map<String, Object>) new Genson().deserialize(JSON, Map.class).get("data");

	assertEquals("[title]", data.get("name"));

	assertEquals("[A:name]", data.get("a_name"));
	assertEquals("[A:unit]", data.get("a_unit"));
	assertEquals("[A:source_name]", data.get("a_source_name"));
	assertEquals("[A:source_link]", data.get("a_source_link"));

	assertEquals("[B:name]", data.get("b_name"));
	assertEquals("[B:unit]", data.get("b_unit"));
	assertEquals("[B:source_name]", data.get("b_source_name"));
	assertEquals("[B:source_link]", data.get("b_source_link"));

	@SuppressWarnings("unchecked")
	Map<String, Map<String, Double>> pairs = (Map<String, Map<String, Double>>) data.get("data");
	assertEquals(2, pairs.size());

	assertEquals(2, pairs.get("date_1").size());
	assertEquals(new Double(1), pairs.get("date_1").get("a"));
	assertEquals(new Double(1), pairs.get("date_1").get("b"));

	assertEquals(2, pairs.get("date_2").size());
	assertEquals(new Double(2), pairs.get("date_2").get("a"));
	assertEquals(new Double(2), pairs.get("date_2").get("b"));
    }
}
