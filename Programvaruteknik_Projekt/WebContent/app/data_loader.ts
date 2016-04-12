import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import 'rxjs/add/operator/map';

@Injectable()
export class DataLoader
{
	constructor(private http: Http) { }
	
	getDataSource(params)
	{
		return this.http
		.post("SampleServlet", JSON.stringify(
		{
			"type": "data-correlation",
			"resolution": "YEAR",
			"merge-type-x": "AVERAGE",
			"merge-type-y": "AVERAGE",
			"data":
			{
				"sourceA":
				{
					"source-type": "weather",
					"weather-location": "GÄVLE_A"
				},
				"sourceB":
				{
					"source-type": "stock",
					"stock-info": "PRICE",
					"stock-name": "MSFT",
				}
			}
		}))
		.map(res => res.json().data);
	}
}