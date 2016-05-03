import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import 'rxjs/add/operator/map';

@Injectable()
export class DataLoader
{
    constructor(private http: Http) { }

    getCorrData(data, sourceA, sourceB)
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
                        "sourceA": sourceA,
                        "sourceB": sourceB
                    }
                }))
            .map(res => res.json().data);
    }

    getSourceData(data, source)
    {
        console.log("get", data, source);
        data["type"] = "data-source";

        if (!data["data"])
            data["data"] = {};

        //data["resolution"] = "YEAR";
        //data["merge-type-x"] = "AVERAGE";
        //data["merge-type-] = "AVERAGE";
		
        if (source)
            data.data.source = source;

        return this.http
            .post("SampleServlet", JSON.stringify(data))
            .map(res => res.json().data);
    }

    getSources()
    {
        return this.http
            .post("SampleServlet", JSON.stringify(
                {
                    "type": "sources"
                }))
            .map(res => res.json().data);
    }
}