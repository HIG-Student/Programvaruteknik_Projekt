import {Component} from "angular2/core";
import {GraphView} from "app/graphs/graph_view/graph_view";
import {DataLoader} from "./data_loader";
import {HTTP_PROVIDERS} from 'angular2/http';

@Component(
    {
        selector: "hello-world",
        templateUrl: "hello_world.html",
        styleUrls: ["hello_world.css"],
        directives: [GraphView],
        providers: [HTTP_PROVIDERS, DataLoader]
    })
export class HelloWorld 
{
    yourName: string = '';
}