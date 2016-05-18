import {Component} from "angular2/core";
import {GraphView} from "app/graphs/graph_view/graph_view";
import {DataLoader} from "app/data_loader";
import {ProgressService} from "app/progress_service";
import {HTTP_PROVIDERS} from 'angular2/http';

@Component(
{
  selector: "statistics",
  templateUrl: "app/statistics.html",
  styleUrls: ["app/statistics.css"],
  directives: [GraphView],
  providers: [HTTP_PROVIDERS, DataLoader, ProgressService]
})
export class Statistics 
{
	constructor(private progressor: ProgressService) 
	{
	
	}
}