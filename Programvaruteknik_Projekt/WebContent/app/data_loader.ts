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
		.post("SampleServlet?sourceA=Weather,GÄVLE_A&sourceB=Stock,MSFT,Value", JSON.stringify(null))
		.map(res => res.json().data);
	}
}