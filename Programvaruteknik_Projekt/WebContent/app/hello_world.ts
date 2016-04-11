import {Component} from "angular2/core";
import {GraphView} from "app/graphs/graph_view/graph_view";
import {DataLoader} from "app/data_loader";
import {HTTP_PROVIDERS} from 'angular2/http';

@Component(
{
  selector: "hello-world",
  templateUrl: "app/hello_world.html",
  styleUrls: ["app/hello_world.css"],
  directives: [GraphView],
  providers: [HTTP_PROVIDERS, DataLoader]
})
export class HelloWorld 
{
	yourName: string = '';
}