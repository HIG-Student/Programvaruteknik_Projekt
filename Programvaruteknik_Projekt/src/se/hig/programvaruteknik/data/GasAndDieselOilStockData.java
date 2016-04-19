package se.hig.programvaruteknik.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import se.hig.programvaruteknik.model.DataSource;

public class GasAndDieselOilStockData extends JSONDataSourceBuilder
{
	@SuppressWarnings("unchecked")
	public GasAndDieselOilStockData()
	{
		setSourceSupplier(
				DataSupplierFactory.createURLFetcher("https://www.quandl.com/api/v3/datasets/UN/GASDIESELOILSCHANGESINSTOCKS_SWE.json"));

			setListExtractor(
				(source) -> listToMap(
					(((List<List<Object>>) ((Map<String, Object>) source.get("dataset")).get("data"))))
			);

			setDataExtractor(
				(entry, adder) -> adder.accept(LocalDate.parse((String) entry.get("0")), (Double) entry.get("1")));

			setNameExtractor(
				(source) -> ((Map<String, Object>) source.get("dataset")).get("name").toString().split(",")[0]);

			setUnit("Prisändring på aktien");

			setSourceName("Quandl");
			setSourceLink("www.quandl.com");
		    }

		    public List<Map<String, Object>> listToMap(List<List<Object>> list)
		    {
			return list.stream().map((entry) -> new TreeMap<String, Object>()
			{
			    {
				put("0", entry.get(0));
				put("1", entry.get(1));
			    }
			}).collect(Collectors.toList());
		    }

		    public static void main(String[] args)
		    {
			DataSource source = new GasAndDieselOilStockData().build();
			for (Entry<LocalDate, Double> entry : source.getData().entrySet())
			{
			    System.out.println(entry.getKey() + ": " + entry.getValue());
			}
		    }
}