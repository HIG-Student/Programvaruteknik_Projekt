import {Component} from "angular2/core";
import {GraphView} from "app/graphs/graph_view/graph_view";

@Component(
{
  selector: "hello-world",
  templateUrl: "app/hello_world.html",
  styleUrls: ["app/hello_world.css"],
  directives: [GraphView]
})
export class HelloWorld 
{
  yourName: string = '';
}